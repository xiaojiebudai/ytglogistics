package com.ytglogistics.www.ytglogistics.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.library.BaseRecyclerAdapter;
import com.github.library.BaseViewHolder;
import com.github.library.listener.OnRecyclerItemClickListener;
import com.ytglogistics.www.ytglogistics.MyApplication;
import com.ytglogistics.www.ytglogistics.R;
import com.ytglogistics.www.ytglogistics.api.Api;
import com.ytglogistics.www.ytglogistics.been.AppInMax;
import com.ytglogistics.www.ytglogistics.been.AppInResult;
import com.ytglogistics.www.ytglogistics.been.DataCbm;
import com.ytglogistics.www.ytglogistics.been.DataImg;
import com.ytglogistics.www.ytglogistics.dialog.CommonDialog;
import com.ytglogistics.www.ytglogistics.utils.Consts;
import com.ytglogistics.www.ytglogistics.utils.DialogUtils;
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
 * Created by Administrator on 2016/12/22.
 */

public class FunOutMxListActivity extends FatherActivity {
    @BindView(R.id.lv_data)
    RecyclerView lvData;
    @BindView(R.id.tv_commit)
    TextView tvCommit;
    @BindView(R.id.tv_shenhe)
    TextView tvShenhe;
    @BindView(R.id.tv_leave)
    TextView tvLeave;
    @BindView(R.id.tv_printe)
    TextView tv_printe;
    private AppInResult result;
    private ArrayList<AppInMax> appInMaxes;
    private BaseRecyclerAdapter mAdapter;
    private ArrayList<DataCbm> lsit = new ArrayList<DataCbm>();

    @Override
    protected int getLayoutId() {
        return R.layout.act_funoutmaxlist;
    }

    @Override
    protected void initValues() {
        initDefautHead("出仓修改", true);
        result = JSON.parseObject(getIntent().getStringExtra(Consts.KEY_DATA), AppInResult.class);
    }

    @Override
    protected void initView() {
        mAdapter = new BaseRecyclerAdapter<AppInMax>(this, appInMaxes, R.layout.list_funoutmax_item) {
            @Override
            protected void convert(BaseViewHolder helper, AppInMax item) {

                helper.setText(R.id.tv_xuhao, item.OutItem + "");
                helper.setText(R.id.tv_so, item.So);
                helper.setText(R.id.tv_po, item.Po);
                helper.setText(R.id.tv_skn, item.Skn);
                item.QtyStatus=item.Soquan+"/"+item.OutCtn;
                helper.setText(R.id.tv_state, item.QtyStatus);

                if(item.Soquan==item.OutCtn){
                    helper.setTextColor(R.id.tv_state,getResources().getColor(R.color.text_f7));
                }else{
                    helper.setTextColor(R.id.tv_state,getResources().getColor(R.color.red));
                }

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
                AppInMax item = (AppInMax) mAdapter.getItem(position);
                for (int i = 0; i <mAdapter.getData().size() ; i++) {
                    ((AppInMax) mAdapter.getItem(i)).isSelect=false;
                }
                ((AppInMax) mAdapter.getItem(position)).isSelect=true;
                mAdapter.notifyDataSetChanged();
            }
        });
        mAdapter.setSelectedColor(R.color.text_selected_white_gray);
        lvData.setHasFixedSize(true);
        lvData.setLayoutManager(new LinearLayoutManager(this));
        lvData.setItemAnimator(new DefaultItemAnimator());
        lvData.setAdapter(mAdapter);
    }

    @Override
    protected void doOperate() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        getOutMaxData();
    }

    private void getOutMaxData() {
        showWaitDialog();
        RequestParams params = ParamsUtils.getSessionParams(Api.GetAppOutMx());
        params.addBodyParameter("serial", result.Serial+"");
        params.addBodyParameter("clp", result.Clp);
        x.http().get(params, new WWXCallBack("GetAppOutMx") {
            @Override
            public void onAfterSuccessOk(JSONObject data) {
                JSONArray jsonArray = data.getJSONArray("Data");
                appInMaxes = (ArrayList<AppInMax>) JSON.parseArray(
                        jsonArray.toJSONString(), AppInMax.class);
                mAdapter.setData(appInMaxes);
            }

            @Override
            public void onAfterFinished() {
                dismissWaitDialog();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 888) {
                ZLog.showPost(  data.getStringExtra(Consts.KEY_DATA));
                ArrayList<DataCbm> lsit22 = (ArrayList<DataCbm>) JSON.parseArray(
                        data.getStringExtra(Consts.KEY_DATA), DataCbm.class);
                //把重复的去掉
                if (lsit22 != null && lsit22.size() > 0) {
                    for (int i = 0; i < lsit.size(); i++) {
                        if (lsit.get(i).InMxId == lsit22.get(0).InMxId) {
                            lsit.remove(i);
                        }
                    }
                }
                lsit.addAll(lsit22);
                //更数量
                for (int i = 0; i < lsit.size(); i++) {
                    for (int j = 0; j < mAdapter.getData().size(); j++) {
                        if (lsit.get(i).InMxId == ((AppInMax) mAdapter.getData().get(j)).RowId) {
                            ((AppInMax) mAdapter.getData().get(j)).Soquan = lsit.get(i).Soquan;
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.tv_commit, R.id.tv_shenhe, R.id.tv_leave,R.id.tv_printe})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_commit:
                allInfoCommit();
                break;
            case R.id.tv_printe:
                Intent intent = new Intent(FunOutMxListActivity.this, FuncOutNumActivity.class);
                intent.putExtra(Consts.KEY_DATA, JSONArray.toJSONString(appInMaxes));
                intent.putExtra("Serial", result.Serial+"");
                startActivityForResult(intent,888);
                break;
            case R.id.tv_shenhe:
                boolean isok = true;
                for (int i = 0; i < appInMaxes.size(); i++) {
                    if (appInMaxes.get(i).Soquan != appInMaxes.get(i).OutCtn) {
                        WWToast.showShort(appInMaxes.get(i).Skn + "的实际出仓数与出仓数不符.");
                        isok = false;
                        return;
                    }
                }
                if (isok) {
                    WWToast.showShort("装柜数量正常可以封柜门");
                }
                break;
            case R.id.tv_leave:
                //车辆离开
                boolean sureCommit = true;
                for (int i = 0; i < appInMaxes.size(); i++) {
                    if (appInMaxes.get(i).Soquan != appInMaxes.get(i).OutCtn) {
                        WWToast.showShort(appInMaxes.get(i).Skn + "的实际出仓数与出仓数不符.");
                        sureCommit = false;
                        return;
                    }
                }
                if (sureCommit) {
                    final CommonDialog twiceConfirm = DialogUtils.getCommonDialogTwiceConfirm(FunOutMxListActivity.this, "请确认录入的数据已提交，需要执行卸货完成并释放泊位？", true);
                    twiceConfirm.getButtonRight().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            RequestParams params = ParamsUtils.getSessionParams(Api.FinishOutQueue());
                            params.addBodyParameter("orderid", result.OrderId);
                            x.http().get(params, new WWXCallBack("FinishOutQueue") {
                                @Override
                                public void onAfterSuccessOk(JSONObject data) {
                                    WWToast.showShort("操作成功");
                                    finish();
                                }

                                @Override
                                public void onAfterFinished() {

                                }
                            });
                        }
                    });
                }

                break;
        }
    }

    private void allInfoCommit() {
        showWaitDialog();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Consts.KEY_SESSIONID, MyApplication
                .getInstance().getSessionId());
        JSONArray array = new JSONArray();
        for (int i = 0; i < mAdapter.getData().size(); i++) {
            array.add(((AppInMax) mAdapter.getData().get(i)).toJson());
        }
        jsonObject.put("objs", array);
        x.http().post(ParamsUtils.getPostJsonParams(jsonObject, Api.AppOutMxCommit()), new WWXCallBack("AppOutMxCommit") {
            @Override
            public void onAfterSuccessOk(JSONObject data) {
                JSONArray jsonArray = data.getJSONArray("Data");
                ArrayList<Long> rowIdList = (ArrayList<Long>) JSON.parseArray(
                        jsonArray.toJSONString(), Long.class);
                if (rowIdList.size() > 0) {
                    //更新DataCbm
                    for (int i = 0; i < lsit.size(); i++) {
                        for (int j = 0; j < mAdapter.getData().size(); j++) {
                            if (lsit.get(i).InMxId == ((AppInMax) mAdapter.getData().get(j)).RowId) {
                                lsit.get(i).InMxId = rowIdList.get(j);
                            }
                        }
                    }
                    //更新AppInMax
                    for (int j = 0; j < mAdapter.getData().size(); j++) {
                        ((AppInMax) mAdapter.getData().get(j)).RowId = rowIdList.get(j);
                    }
                }
                if (lsit != null && lsit.size() > 0) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(Consts.KEY_SESSIONID, MyApplication
                            .getInstance().getSessionId());
                    JSONArray array = new JSONArray();
                    for (int i = 0; i < lsit.size(); i++) {
                        array.add(lsit.get(i).toJson());
                    }
                    jsonObject.put("objs", array);
                    x.http().post(ParamsUtils.getPostJsonParams(jsonObject, Api.AppMxNumCommit()), new WWXCallBack("AppMxNumCommit") {
                        @Override
                        public void onAfterSuccessOk(JSONObject data) {
                            WWToast.showShort("提交成功");
                        }

                        @Override
                        public void onAfterFinished() {

                        }
                    });
                }

            }

            @Override
            public void onAfterFinished() {
                dismissWaitDialog();
            }
        });
    }
}
