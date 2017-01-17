package com.ytglogistics.www.ytglogistics.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.library.BaseRecyclerAdapter;
import com.github.library.BaseViewHolder;
import com.github.library.listener.OnRecyclerItemClickListener;
import com.ytglogistics.www.ytglogistics.R;
import com.ytglogistics.www.ytglogistics.api.Api;
import com.ytglogistics.www.ytglogistics.been.DataCbm;
import com.ytglogistics.www.ytglogistics.been.DataImg;
import com.ytglogistics.www.ytglogistics.been.Place;
import com.ytglogistics.www.ytglogistics.been.PoSearch;
import com.ytglogistics.www.ytglogistics.dialog.FuncPlaceStatusDialog;
import com.ytglogistics.www.ytglogistics.utils.ParamsUtils;
import com.ytglogistics.www.ytglogistics.xutils.WWXCallBack;

import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/12/22.
 */

public class FuncPlaceStatusActivity extends FatherActivity {
    @BindView(R.id.lv_data)
    RecyclerView lvData;
    private ArrayList<Place> lsit = new ArrayList<Place>();
    private BaseRecyclerAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.act_funcplacestatus;
    }

    @Override
    protected void initValues() {
        initDefautHead("泊位图", true);
    }

    @Override
    protected void initView() {
        mAdapter = new BaseRecyclerAdapter<Place>(this, lsit, R.layout.list_funinmax_item) {
            @Override
            protected void convert(BaseViewHolder helper, Place item) {
                helper.setText(R.id.tv_so, item.AreaId);
                helper.setText(R.id.tv_po, item.PlaceId);
                switch (item.Status) {
                    case 0:
                        item.StatusText = "空闲";
                        break;
                    case 1:
                        item.StatusText = "作业中";
                        break;
                    case 2:
                        item.StatusText = "停用";
                        break;
                    case 3:
                        item.StatusText = "通知离场";
                        break;
                    default:
                        item.StatusText = "未知";
                }
                helper.setText(R.id.tv_skn, item.StatusText);
                if (item.isSelect) {
                    helper.getView(R.id.ll_container).setBackgroundResource(R.color.top_title_bg);
                } else {
                    helper.getView(R.id.ll_container).setBackgroundResource(R.color.white);
                }
            }
        };
        mAdapter.setOnRecyclerItemClickListener(new OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Place item = (Place) mAdapter.getItem(position);
                for (int i = 0; i < mAdapter.getData().size(); i++) {
                    ((Place) mAdapter.getItem(i)).isSelect = false;
                }
                ((Place) mAdapter.getItem(position)).isSelect = true;
                mAdapter.notifyDataSetChanged();
                FuncPlaceStatusDialog dialog = new FuncPlaceStatusDialog(FuncPlaceStatusActivity.this, item);
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        getList();
                    }
                });
                dialog.show();
            }
        });
        mAdapter.setSelectedColor(R.color.text_selected_white_gray);
        lvData.setHasFixedSize(true);
        lvData.setLayoutManager(new LinearLayoutManager(this));
        lvData.setItemAnimator(new DefaultItemAnimator());
        mAdapter.openLoadAnimation(false);
        lvData.setAdapter(mAdapter);
    }

    @Override
    protected void doOperate() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        getList();
    }

    private void getList() {
        showWaitDialog();
        RequestParams params = ParamsUtils.getSessionParams(Api.PlaceStatus());
        x.http().get(params, new WWXCallBack("PlaceStatus") {
            @Override
            public void onAfterSuccessOk(JSONObject data) {
                JSONArray jsonArray = data.getJSONArray("Data");
                lsit = (ArrayList<Place>) JSONObject.parseArray(
                        jsonArray.toJSONString(), Place.class);
                mAdapter.setData(lsit);
            }

            @Override
            public void onAfterFinished() {
                dismissWaitDialog();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
