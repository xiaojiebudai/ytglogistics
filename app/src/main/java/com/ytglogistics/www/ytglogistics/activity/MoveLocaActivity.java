package com.ytglogistics.www.ytglogistics.activity;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.library.BaseRecyclerAdapter;
import com.github.library.BaseViewHolder;
import com.github.library.listener.OnRecyclerItemClickListener;
import com.ytglogistics.www.ytglogistics.R;
import com.ytglogistics.www.ytglogistics.api.Api;
import com.ytglogistics.www.ytglogistics.been.DataImg;
import com.ytglogistics.www.ytglogistics.been.DataSolt;
import com.ytglogistics.www.ytglogistics.utils.ParamsUtils;
import com.ytglogistics.www.ytglogistics.utils.WWToast;
import com.ytglogistics.www.ytglogistics.xutils.WWXCallBack;

import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/12/22.
 */

public class MoveLocaActivity extends FatherActivity {
    @BindView(R.id.et_no)
    EditText etNo;
    @BindView(R.id.tv_search)
    TextView tvSearch;
    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.textView2)
    TextView textView2;
    @BindView(R.id.lv_data)
    RecyclerView lvData;
    @BindView(R.id.tv_po)
    TextView tvPo;
    @BindView(R.id.tv_skn)
    TextView tvSkn;
    @BindView(R.id.tv_oldloca)
    TextView tvOldloca;
    @BindView(R.id.tv_newplaceId)
    EditText tvNewplaceId;
    @BindView(R.id.tv_change)
    TextView tvChange;
    private ArrayList<DataSolt> dataSolts;
    private BaseRecyclerAdapter mAdapter;
    private DataSolt dataSolt;

    @Override
    protected int getLayoutId() {
        return R.layout.act_movepolist;
    }

    @Override
    protected void initValues() {
        initDefautHead("移库", true);
    }

    @Override
    protected void initView() {
        lvData.setBackgroundResource(R.drawable.bg_yellow_white_shape);
        mAdapter = new BaseRecyclerAdapter<DataSolt>(this, dataSolts, R.layout.list_funinmax_item) {
            @Override
            protected void convert(BaseViewHolder helper, DataSolt item) {
                helper.setText(R.id.tv_so, item.Po);
                helper.setText(R.id.tv_po, item.Skn);
                helper.setText(R.id.tv_skn, item.Loca);
                if(item.isSelect){
                    helper.getView(R.id.ll_container).setBackgroundResource(R.color.top_title_bg);
                }else{
                    helper.getView(R.id.ll_container).setBackgroundResource(R.color.white);
                }
            }
        };
        mAdapter.setOnRecyclerItemClickListener(new OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                dataSolt = (DataSolt) mAdapter.getData().get(position);
                tvPo.setText(dataSolt.Po);
                tvSkn.setText(dataSolt.Skn);
                tvOldloca.setText(dataSolt.Loca);
                for (int i = 0; i <mAdapter.getData().size() ; i++) {
                    ((DataSolt) mAdapter.getItem(i)).isSelect=false;
                }
                ((DataSolt) mAdapter.getItem(position)).isSelect=true;
                mAdapter.notifyDataSetChanged();
            }
        });
        lvData.setHasFixedSize(true);
        lvData.setLayoutManager(new LinearLayoutManager(this));
        lvData.setItemAnimator(new DefaultItemAnimator());
        lvData.setAdapter(mAdapter);
    }

    @Override
    protected void doOperate() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.tv_search, R.id.tv_change})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_search:
                hideSoftKeyboard();
                String carno = etNo.getText().toString().trim();
                if (TextUtils.isEmpty(carno)) {
                    WWToast.showShort("请输入SO Num查询");
                    return;
                }
                showWaitDialog();
                RequestParams params = ParamsUtils.getSessionParams(Api.GetRecSoIts());
                params.addBodyParameter("sono", carno);
                x.http().get(params, new WWXCallBack("GetRecSoIts") {
                    @Override
                    public void onAfterSuccessOk(JSONObject data) {
                        JSONArray jsonArray = data.getJSONArray("Data");
                        dataSolts = (ArrayList<DataSolt>) JSONObject.parseArray(
                                jsonArray.toJSONString(), DataSolt.class);
                        mAdapter.setData(dataSolts);
                    }

                    @Override
                    public void onAfterFinished() {
                        dismissWaitDialog();
                    }
                });
                break;
            case R.id.tv_change:
                hideSoftKeyboard();
                String newplaceId = tvNewplaceId.getText().toString().trim();
                if (dataSolt == null) {
                    WWToast.showShort("请选择要修改的入仓记录");
                    return;
                }
                if (TextUtils.isEmpty(newplaceId)) {
                    WWToast.showShort("请输入新的仓位");
                    return;
                }
                showWaitDialog();
                RequestParams params1 = ParamsUtils.getSessionParams(Api.ChangeSoLoca());
                params1.addBodyParameter("newloca", dataSolt.Keyid);
                params1.addBodyParameter("keyId", newplaceId);
                x.http().get(params1, new WWXCallBack("ChangeSoLoca") {
                    @Override
                    public void onAfterSuccessOk(JSONObject data) {
                        WWToast.showShort("修改成功");
                        dataSolt = null;
                        tvPo.setText("");
                        tvSkn.setText("");
                        tvOldloca.setText("");
                        tvNewplaceId.setText("");
                    }

                    @Override
                    public void onAfterFinished() {
                        dismissWaitDialog();
                    }
                });
                break;
        }
    }
}
