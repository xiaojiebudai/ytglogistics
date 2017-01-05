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
import com.ytglogistics.www.ytglogistics.been.PrintInfo;
import com.ytglogistics.www.ytglogistics.karics.library.zxing.android.CaptureActivity;
import com.ytglogistics.www.ytglogistics.utils.Consts;
import com.ytglogistics.www.ytglogistics.utils.ParamsUtils;
import com.ytglogistics.www.ytglogistics.utils.WWToast;
import com.ytglogistics.www.ytglogistics.utils.ZLog;
import com.ytglogistics.www.ytglogistics.xutils.WWXCallBack;

import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/12/26.
 */

public class FuncOutNumActivity extends FatherActivity {
    @BindView(R.id.ed_so)
    TextView edSo;
    @BindView(R.id.ed_cangwei)
    TextView edCangwei;
    @BindView(R.id.ed_chucangnum)
    TextView edChucangnum;
    @BindView(R.id.ed_shijinum)
    TextView edShijinum;
    @BindView(R.id.lv_data)
    RecyclerView lvData;
    @BindView(R.id.ed_CtnNO)
    EditText edCtnNO;
    @BindView(R.id.tv_add)
    TextView tvAdd;
    @BindView(R.id.tv_del)
    TextView tvDel;
    @BindView(R.id.tv_ok)
    TextView tvOk;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.tv_saomiao)
    TextView tvSaomiao;
    @BindView(R.id.ll_operate)
    LinearLayout llOperate;
//    private AppInMax inMax;
private ArrayList<AppInMax> appInMaxes;
    private ArrayList<DataCbm> lsit = new ArrayList<DataCbm>();
    private BaseRecyclerAdapter mAdapter;
    private int selectPosition = -1;
    private String Serial;
    @Override
    protected int getLayoutId() {
        return R.layout.act_funoutnum;
    }

    @Override
    protected void initValues() {
        initDefautHead("出仓修改", true);
//        inMax = JS.parseObject(getIntent().getStringExtra(Consts.KEY_DATA), AppInMax.class);
        appInMaxes = (ArrayList<AppInMax>) JSON.parseArray(
                getIntent().getStringExtra(Consts.KEY_DATA), AppInMax.class);
        Serial = getIntent().getStringExtra("Serial");
        ZLog.showPost(Serial+"");
//        edSo.setText(inMax.So);
//        edCangwei.setText(inMax.Loca);
//        edChucangnum.setText(inMax.OutCtn+"");
        getListData();
    }
    private void getListData() {
        showWaitDialog();
        RequestParams params = ParamsUtils.getSessionParams(Api.GetAppMxNumsById());
        params.addBodyParameter("inId", Serial);
        x.http().get(params, new WWXCallBack("GetAppMxNumsById") {
            @Override
            public void onAfterSuccessOk(JSONObject data) {
                JSONArray jsonArray = data.getJSONArray("Data");
                lsit = (ArrayList<DataCbm>) JSON.parseArray(
                        jsonArray.toJSONString(), DataCbm.class);
//                setShijiNum();
                mAdapter.addAll(lsit);
            }

            @Override
            public void onAfterFinished() {
                dismissWaitDialog();
            }
        });
    }
//实际数量
    private void setShijiNum() {
        int num=0;
        for (int i = 0; i <lsit.size() ; i++) {
            num+=lsit.get(i).Soquan;
        }
        edShijinum.setText(num+"");
    }

    @Override
    protected void initView() {
        mAdapter = new BaseRecyclerAdapter<DataCbm>(this, lsit, R.layout.fun_in_list) {
            @Override
            protected void convert(BaseViewHolder helper, DataCbm item) {
                helper.setText(R.id.tv_num, item.Palletid);
                helper.setText(R.id.tv_name, item.Po);
                helper.setText(R.id.tv_bowei, item.Skn);
                helper.setText(R.id.tv_state, item.Soquan+"");
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
                edCtnNO.setText(item.Soquan + "");
                for (int i = 0; i <mAdapter.getData().size() ; i++) {
                    ((DataCbm) mAdapter.getItem(i)).isSelect=false;
                }
                ((DataCbm) mAdapter.getItem(position)).isSelect=true;
                mAdapter.notifyDataSetChanged();
            }
        });
        mAdapter.setSelectedColor(R.color.text_selected_white_gray);
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
                        ((DataCbm) mAdapter.getItem(selectPosition)).Soquan = Integer.valueOf(s + "");
                        mAdapter.notifyDataSetChanged();
                        setShijiNum();
                    }

                }

            }
        });
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

    @OnClick({R.id.tv_add, R.id.tv_del, R.id.tv_ok, R.id.tv_cancel, R.id.tv_saomiao})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_add:
                DataCbm item = new DataCbm();
//                item.So = inMax.So;
//                item.Po = inMax.Po;
//                item.Skn = inMax.Skn;
//                item.InMxId = inMax.RowId;
                mAdapter.add(item);
                selectPosition = mAdapter.getData().size() - 1;
                edCtnNO.setText("");
                break;
            case R.id.tv_del:
                if (selectPosition != -1) {
                    mAdapter.remove(selectPosition);
                    selectPosition = -1;
                    edCtnNO.setText("");
                } else {
                    WWToast.showShort("请先选择一列数据");
                }
                break;
            case R.id.tv_ok:
                if (mAdapter.getData().size() == 0) {
                    WWToast.showShort("沒有任何数据");
                } else {
                    boolean isOver = true;
                    for (int i = 0; i < mAdapter.getData().size(); i++) {
                        DataCbm item1 = (DataCbm)mAdapter.getData().get(i);
                        if (item1.Soquan <=  0) {
                            WWToast.showShort("列表中存在空数据，请填写");
                            isOver = false;
                            break;
                        }
                    }
                    if (isOver) {
                        Intent intent = new Intent();
                        intent.putExtra(Consts.KEY_DATA, JSONArray.toJSONString(mAdapter.getData()));
                        setResult(RESULT_OK, intent);
                        finish();
                    }

                }
                break;
            case R.id.tv_cancel:
                finish();
                break;
            case R.id.tv_saomiao:
                Intent intent = new Intent(this, CaptureActivity.class);
                startActivityForResult(intent, 999);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null)
            return;
        if (requestCode == 999) {
            String info = data.getExtras().getString("codedContent");
            if(!TextUtils.isEmpty(info)){
                RequestParams sessionParams = ParamsUtils.getSessionParams(Api.PalletGet());
                sessionParams.addBodyParameter("palletId",info);
                x.http().get(sessionParams, new WWXCallBack("PalletGet") {
                    @Override
                    public void onAfterSuccessOk(JSONObject data) {
                        PrintInfo printInfo = (PrintInfo) JSON.parseObject(data.getString("Data"), PrintInfo.class);
                        boolean isOk=false;
                        for (int i = 0; i <appInMaxes.size() ; i++) {
                            if(printInfo.Sono.equals(appInMaxes.get(i).So)&&printInfo.Po.equals(appInMaxes.get(i).Po)&&printInfo.Skn.equals(appInMaxes.get(i).Skn)){
                                isOk=true;
                               if((appInMaxes.get(i).Soquan+printInfo.Pkgs)<=appInMaxes.get(i).OutCtn){
                                   //总数据添加
                                   appInMaxes.get(i).Soquan+=printInfo.Pkgs;
                                   DataCbm item = new DataCbm();
                                   item.Palletid = printInfo.Palletid;
                                   item.So = printInfo.Sono;
                                   item.Po = printInfo.Po;
                                   item.Skn = printInfo.Skn;
                                   item.InMxId = appInMaxes.get(i).RowId;
                                   item.Soquan=printInfo.Pkgs;
                                   mAdapter.add(item);
                                   selectPosition = mAdapter.getData().size() - 1;

                                   for (int j = 0; j <mAdapter.getData().size() ; j++) {
                                       ((DataCbm) mAdapter.getItem(j)).isSelect=false;
                                   }
                                   ((DataCbm) mAdapter.getItem(selectPosition)).isSelect=true;
                                   mAdapter.notifyDataSetChanged();

                                   edCtnNO.setText(item.Soquan+"");
                                   break;
                               }else{
                                   WWToast.showShort("该板单重复或者板单信息有误");
                               }
                            }
                        }
                        if (!isOk){
                            WWToast.showShort("板单信息有误！！！");
                        }
                    }

                    @Override
                    public void onAfterFinished() {

                    }
                });
            }else{
                WWToast.showShort("未扫描到信息，请重新扫描");
            }

        }
    }
}
