package com.ytglogistics.www.ytglogistics.activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.library.BaseRecyclerAdapter;
import com.github.library.BaseViewHolder;
import com.github.library.listener.OnRecyclerItemClickListener;
import com.ytglogistics.www.ytglogistics.R;
import com.ytglogistics.www.ytglogistics.api.Api;
import com.ytglogistics.www.ytglogistics.been.AppInMax;
import com.ytglogistics.www.ytglogistics.been.DataImg;
import com.ytglogistics.www.ytglogistics.been.DataYy;
import com.ytglogistics.www.ytglogistics.been.Place;
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

public class FuncPlaceActivity extends FatherActivity {
    @BindView(R.id.et_no)
    EditText etNo;
    @BindView(R.id.tv_search)
    TextView tvSearch;
    @BindView(R.id._QueueNo)
    TextView QueueNo;
    @BindView(R.id.tv_oldplaceId)
    TextView tvOldplaceId;
    @BindView(R.id.tv_newplaceId)
    TextView tvNewplaceId;
    @BindView(R.id.tv_change)
    TextView tvChange;

    private ArrayList<DataYy> dataYys;
    private ArrayList<Place> places;
    private DataYy dataYySelect;
    private Place placeSelect;

    @Override
    protected int getLayoutId() {
        return R.layout.act_funcplace;
    }

    @Override
    protected void initValues() {
        initDefautHead("泊位管理", true);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void doOperate() {
        getAllPlace();
        getAllData();
    }

    private void getAllData() {
        showWaitDialog();
        RequestParams params = ParamsUtils.getSessionParams(Api.GetAllQueues());
        x.http().get(params, new WWXCallBack("GetAllQueues") {
            @Override
            public void onAfterSuccessOk(JSONObject data) {
                JSONArray jsonArray = data.getJSONArray("Data");
                dataYys = (ArrayList<DataYy>) JSONObject.parseArray(
                        jsonArray.toJSONString(), DataYy.class);
            }

            @Override
            public void onAfterFinished() {
                dismissWaitDialog();
            }
        });
    }

    private void getAllPlace() {
        showWaitDialog();
        RequestParams params = ParamsUtils.getSessionParams(Api.AllPlaces());
        params.addBodyParameter("bz", 0 + "");
        x.http().get(params, new WWXCallBack("AllPlaces") {
            @Override
            public void onAfterSuccessOk(JSONObject data) {
                JSONArray jsonArray = data.getJSONArray("Data");
                places = (ArrayList<Place>) JSONObject.parseArray(
                        jsonArray.toJSONString(), Place.class);
            }

            @Override
            public void onAfterFinished() {
                dismissWaitDialog();
            }
        });
    }

    /**
     * 选择位置
     */
    private PopupWindow popupWindow;

    private void seletePlace() {
        if (popupWindow == null) {
            RecyclerView listView = new RecyclerView(this);
            final BaseRecyclerAdapter mAdapter = new BaseRecyclerAdapter<Place>(this, places, R.layout.list_text_item) {
                @Override
                protected void convert(BaseViewHolder helper, Place item) {
                    helper.setText(R.id.tv_info, item.PlaceId);
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
                    placeSelect = (Place) mAdapter.getItem(position);
                    for (int i = 0; i <mAdapter.getData().size() ; i++) {
                        ((Place) mAdapter.getItem(i)).isSelect=false;
                    }
                    ((Place) mAdapter.getItem(position)).isSelect=true;
                    mAdapter.notifyDataSetChanged();
                    tvNewplaceId.setText(placeSelect.PlaceId);
                    popupWindow.dismiss();
                }
            });
            listView.setHasFixedSize(true);
            listView.setLayoutManager(new LinearLayoutManager(this));
            listView.setItemAnimator(new DefaultItemAnimator());
            mAdapter.openLoadAnimation(false);
            listView.setAdapter(mAdapter);
            popupWindow = new PopupWindow(listView,
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        }
        popupWindow.showAsDropDown(tvNewplaceId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.tv_search, R.id.tv_change, R.id.tv_newplaceId})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_search:
                hideSoftKeyboard();
                String carno = etNo.getText().toString().trim();
                if (TextUtils.isEmpty(carno)) {
                    WWToast.showShort("请输入所要查询的车牌或者CLP号");
                } else {
                    boolean have = false;
                    for (int i = 0; i < dataYys.size(); i++) {
                        if (dataYys.get(i).CarNo.contains(carno)) {
                            have = true;
                            dataYySelect = dataYys.get(i);
                            QueueNo.setText(dataYySelect.QueueNo);
                            tvOldplaceId.setText(dataYySelect.PlaceId);
                            break;
                        }
                    }
                    if (!have) {
                        WWToast.showShort("您所输入的车牌或者CLP记录不存在");
                    }
                }
                break;
            case R.id.tv_change:
                hideSoftKeyboard();
                if (dataYySelect == null) {
                    WWToast.showShort("请选择需要变更的车牌");
                    return;
                }
                if (placeSelect == null) {
                    WWToast.showShort("请选择需要变更到哪个泊位");
                    return;
                }
                if (dataYySelect.PlaceId.equals(placeSelect.PlaceId)) {
                    WWToast.showShort("新泊位与原泊位不能相同，请重新选择");
                    return;
                }
                showWaitDialog();
                RequestParams params = ParamsUtils.getSessionParams(Api.AllPlaces());
                params.addBodyParameter("queueNo", dataYySelect.QueueNo);
                params.addBodyParameter("oldPlace", dataYySelect.PlaceId);
                params.addBodyParameter("newPlace", placeSelect.PlaceId);
                x.http().get(params, new WWXCallBack("AllPlaces") {
                    @Override
                    public void onAfterSuccessOk(JSONObject data) {
                        WWToast.showShort("泊位变更成功");
                        finish();
                    }

                    @Override
                    public void onAfterFinished() {
                        dismissWaitDialog();
                    }
                });


                break;
            case R.id.tv_newplaceId:
                seletePlace();
                break;
        }
    }
}
