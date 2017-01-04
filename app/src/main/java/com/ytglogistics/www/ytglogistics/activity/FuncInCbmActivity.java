package com.ytglogistics.www.ytglogistics.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.library.BaseRecyclerAdapter;
import com.github.library.BaseViewHolder;
import com.github.library.listener.OnRecyclerItemClickListener;
import com.ytglogistics.www.ytglogistics.R;
import com.ytglogistics.www.ytglogistics.api.Api;
import com.ytglogistics.www.ytglogistics.been.AppInMax;
import com.ytglogistics.www.ytglogistics.been.DataCbm;
import com.ytglogistics.www.ytglogistics.utils.Consts;
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
 * Created by Administrator on 2016/12/21.
 */

public class FuncInCbmActivity extends FatherActivity {
    @BindView(R.id.lv_data)
    RecyclerView lvData;
    @BindView(R.id.ed_CtnNO)
    EditText edCtnNO;
    @BindView(R.id.ed_length)
    EditText edLength;
    @BindView(R.id.ed_width)
    EditText edWidth;
    @BindView(R.id.ed_height)
    EditText edHeight;
    @BindView(R.id.ed_weight)
    EditText edWeight;
    @BindView(R.id.tv_add)
    TextView tvAdd;
    @BindView(R.id.tv_del)
    TextView tvDel;
    @BindView(R.id.tv_ok)
    TextView tvOk;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.ll_operate)
    LinearLayout llOperate;
    private AppInMax inMax;
    private ArrayList<DataCbm> lsit = new ArrayList<DataCbm>();
    private BaseRecyclerAdapter mAdapter;
    private int selectPosition = -1;
    private String Serial;

    @Override
    protected int getLayoutId() {
        return R.layout.act_funcincbm;
    }

    @Override
    protected void initValues() {
        initDefautHead("长宽高修改", true);
        inMax = JSON.parseObject(getIntent().getStringExtra(Consts.KEY_DATA), AppInMax.class);
        Serial = getIntent().getStringExtra("Serial");
        getListData();
    }

    private void getListData() {
        showWaitDialog();
        RequestParams params = ParamsUtils.getSessionParams(Api.GetAppMxCbmById());
        params.addBodyParameter("serial", Serial);
        x.http().get(params, new WWXCallBack("GetAppMxCbmById") {
            @Override
            public void onAfterSuccessOk(JSONObject data) {
                //获取打印数据打印
                JSONArray jsonArray = data.getJSONArray("Data");
                ArrayList<DataCbm> listAll = (ArrayList<DataCbm>) JSON.parseArray(
                        jsonArray.toJSONString(), DataCbm.class);
                for (int i = 0; i < listAll.size(); i++) {
                    if (listAll.get(i).InMxId == inMax.RowId) {
                        lsit.add(listAll.get(i));
                    }
                }
                mAdapter.addAll(lsit);
            }

            @Override
            public void onAfterFinished() {
                dismissWaitDialog();
            }
        });
    }

    @Override
    protected void initView() {
        mAdapter = new BaseRecyclerAdapter<DataCbm>(this, lsit, R.layout.list_funinmax_item) {
            @Override
            protected void convert(BaseViewHolder helper, DataCbm item) {
                helper.setText(R.id.tv_so, item.So);
                helper.setText(R.id.tv_po, item.Po);
                helper.setText(R.id.tv_skn, item.Skn);
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
                DataCbm item = (DataCbm) mAdapter.getItem(position);
                selectPosition = position;
                edLength.setText(item.Leng + "");
                edCtnNO.setText(item.Ctnno + "");
                edWidth.setText(item.Wide + "");
                edHeight.setText(item.High + "");
                edWeight.setText(item.Unitwei + "");
                for (int i = 0; i <mAdapter.getData().size() ; i++) {
                    ((DataCbm) mAdapter.getItem(i)).isSelect=false;
                }
                ((DataCbm) mAdapter.getItem(position)).isSelect=true;
                mAdapter.notifyDataSetChanged();
            }
        });
        lvData.setHasFixedSize(true);
        lvData.setLayoutManager(new LinearLayoutManager(this));
        lvData.setItemAnimator(new DefaultItemAnimator());
        lvData.setAdapter(mAdapter);
        edCtnNO.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    if (selectPosition == -1) {
                    } else {
                        ((DataCbm) mAdapter.getItem(selectPosition)).Ctnno = Double.valueOf(s + "");
                    }

                }

            }
        });
        edLength.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    if (selectPosition == -1) {
                    } else {
                        ((DataCbm) mAdapter.getItem(selectPosition)).Leng = Double.valueOf(s + "");
                    }
                }
            }
        });
        ;
        edWidth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    if (selectPosition == -1) {
                    } else {
                        ((DataCbm) mAdapter.getItem(selectPosition)).Wide = Double.valueOf(s + "");
                    }
                }
            }
        });
        ;
        edHeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    if (selectPosition == -1) {
                    } else {
                        ((DataCbm) mAdapter.getItem(selectPosition)).High = Double.valueOf(s + "");
                    }
                }
            }
        });
        ;
        edWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    if (selectPosition == -1) {
                    } else {
                        ((DataCbm) mAdapter.getItem(selectPosition)).Unitwei = Double.valueOf(s + "");
                    }
                }
            }
        });
        ;


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

    @OnClick({R.id.tv_add, R.id.tv_del, R.id.tv_ok, R.id.tv_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_add:
                DataCbm item = new DataCbm();
                item.So = inMax.So;
                item.Po = inMax.Po;
                item.Skn = inMax.Skn;
                item.InMxId = inMax.RowId;
                mAdapter.add(item);
                selectPosition = mAdapter.getData().size() - 1;
                edLength.setText("");
                edCtnNO.setText("");
                edWidth.setText("");
                edHeight.setText("");
                edWeight.setText("");
                break;
            case R.id.tv_del:
                if (selectPosition != -1) {
                    mAdapter.remove(selectPosition);
                    selectPosition = -1;
                    edLength.setText("");
                    edCtnNO.setText("");
                    edWidth.setText("");
                    edHeight.setText("");
                    edWeight.setText("");
                } else {
                    WWToast.showShort("请先选择一列数据");
                }
                break;
            case R.id.tv_ok:
                if (lsit.size() == 0) {
                    WWToast.showShort("未增加数据");
                } else {
                    boolean isOver = true;
                    for (int i = 0; i < lsit.size(); i++) {
                        DataCbm item1 = lsit.get(i);
                        if (item1.Ctnno == 0 || item1.Leng <= 0 || item1.High <= 0 || item1.Unitwei <= 0 || item1.Wide <= 0) {
                            WWToast.showShort("列表中存在空数据，请填写");
                            isOver = false;
                            break;
                        }
                    }
                    if (isOver) {
                        Intent intent = new Intent();
                        intent.putExtra(Consts.KEY_DATA, JSONArray.toJSONString(lsit));
                        setResult(RESULT_OK, intent);
                        finish();
                    }

                }

                break;
            case R.id.tv_cancel:
                finish();
                break;
        }
    }
}
