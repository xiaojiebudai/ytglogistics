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
import com.ytglogistics.www.ytglogistics.been.DataSolt;
import com.ytglogistics.www.ytglogistics.been.PoSearch;
import com.ytglogistics.www.ytglogistics.utils.ParamsUtils;
import com.ytglogistics.www.ytglogistics.utils.TimeUtil;
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

public class PoSearchListActivity extends FatherActivity {
    @BindView(R.id.et_no)
    EditText etNo;
    @BindView(R.id.tv_search)
    TextView tvSearch;
    @BindView(R.id.lv_data)
    RecyclerView lvData;
    private ArrayList<PoSearch> poSearches;
    private BaseRecyclerAdapter mAdapter;
    @Override
    protected int getLayoutId() {
        return R.layout.act_posearch;
    }

    @Override
    protected void initValues() {
        initDefautHead("PO查询", true);
    }

    @Override
    protected void initView() {
        lvData.setBackgroundResource(R.drawable.bg_yellow_white_shape);
        mAdapter = new BaseRecyclerAdapter<PoSearch>(this, poSearches, R.layout.list_funoutmax_item) {
            @Override
            protected void convert(BaseViewHolder helper, PoSearch item) {
                helper.setText(R.id.tv_xuhao, getTime(item.Recevdate));
                helper.setText(R.id.tv_so, item.SoNo);
                helper.setText(R.id.tv_po, item.Skn);
                helper.setText(R.id.tv_skn, item.Loca);
                helper.setText(R.id.tv_state, item.Usablequan+"");
            }
        };
        mAdapter.setSelectedColor(R.color.text_selected_white_gray);
        lvData.setHasFixedSize(true);
        lvData.setLayoutManager(new LinearLayoutManager(this));
        lvData.setItemAnimator(new DefaultItemAnimator());
        lvData.setAdapter(mAdapter);
    }
    private String getTime(String timeInfo) {
        String s = timeInfo.replace("/Date(", "").replace(")/", "");
        long s1 = Long.parseLong(s.substring(0, s.indexOf("+")));
        return TimeUtil.getOnlyDateToS(s1);
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

    @OnClick(R.id.tv_search)
    public void onClick() {
        String carno = etNo.getText().toString().trim();
        if (TextUtils.isEmpty(carno)) {
            WWToast.showShort("请输入PO查询");
            return;
        }
        RequestParams params = ParamsUtils.getSessionParams(Api.PoQuery());
        params.addBodyParameter("po", carno);
        x.http().get(params, new WWXCallBack("PoQuery") {
            @Override
            public void onAfterSuccessOk(JSONObject data) {
                JSONArray jsonArray = data.getJSONArray("Data");
                poSearches = (ArrayList<PoSearch>) JSONObject.parseArray(
                        jsonArray.toJSONString(), PoSearch.class);
                mAdapter.setData(poSearches);
            }
            @Override
            public void onAfterFinished() {

            }
        });
    }
}
