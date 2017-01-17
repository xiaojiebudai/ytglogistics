package com.ytglogistics.www.ytglogistics.activity;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.Time;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.library.BaseRecyclerAdapter;
import com.github.library.BaseViewHolder;
import com.github.library.listener.OnRecyclerItemClickListener;
import com.ytglogistics.www.ytglogistics.R;
import com.ytglogistics.www.ytglogistics.api.Api;
import com.ytglogistics.www.ytglogistics.been.DataImg;
import com.ytglogistics.www.ytglogistics.been.DataInOut;
import com.ytglogistics.www.ytglogistics.dialog.DateChooseWheelViewDialog;
import com.ytglogistics.www.ytglogistics.utils.Consts;
import com.ytglogistics.www.ytglogistics.utils.ParamsUtils;
import com.ytglogistics.www.ytglogistics.utils.TimeUtil;
import com.ytglogistics.www.ytglogistics.utils.WWToast;
import com.ytglogistics.www.ytglogistics.view.SelectTimePop;
import com.ytglogistics.www.ytglogistics.xutils.WWXCallBack;

import org.xutils.http.RequestParams;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/12/23.
 */

public class FuncQueryActivity extends FatherActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_num)
    TextView tvNum;
    @BindView(R.id.lv_data)
    RecyclerView lvData;
    public static final int INYUYUE = 0;
    public static final int OUTYUYUE = 1;
    private int model = 0;
    @BindView(R.id.tv_0)
    TextView tv0;
    @BindView(R.id.tv_1)
    TextView tv1;
    @BindView(R.id.tv_2)
    TextView tv2;
    @BindView(R.id.tv_3)
    TextView tv3;
    private String url = "";
    private ArrayList<DataInOut> lsit = new ArrayList<DataInOut>();
    private BaseRecyclerAdapter mAdapter;
    private String date = "";

    @Override
    protected int getLayoutId() {
        return R.layout.act_funcquery;
    }

    @Override
    protected void initValues() {
        model = getIntent().getIntExtra(Consts.KEY_MODULE, INYUYUE);
        if (model == INYUYUE) {
            initDefautHead("入仓预约查询", true);
            tv1.setText("预约时间");
            tv2.setText("车牌号");
            tv3.setText("状态");
        } else {
            initDefautHead("出仓预约查询", true);
            tv1.setText("CLP");
            tv2.setText("柜号");
            tv3.setText("状态");
        }
    }

    @Override
    protected void initView() {
        mAdapter = new BaseRecyclerAdapter<DataInOut>(this, lsit, R.layout.fun_in_list) {
            @Override
            protected void convert(BaseViewHolder helper, DataInOut item) {

                helper.setText(R.id.tv_num, (helper.getAdapterPosition()+1) + "");
                if (model == INYUYUE) {
                    helper.setText(R.id.tv_name, item.PeriodName);
                    helper.setText(R.id.tv_bowei, item.CarNo);
                } else {
                    helper.setText(R.id.tv_name, item.So);
                    helper.setText(R.id.tv_bowei, item.CabinetNo);
                }
                if(item.isSelect){
                    helper.getView(R.id.ll_container).setBackgroundResource(R.color.top_title_bg);
                }else{
                    helper.getView(R.id.ll_container).setBackgroundResource(R.color.white);
                }
                switch (item.Status) {
                    case 0:
                        item.StatusName = "预约成功";
                        break;
                    case 1:
                        item.StatusName = "取号成功";
                        break;
                    case 2:
                        item.StatusName = "预约取消";
                        break;
                    case 3:
                        item.StatusName = "超时取消";
                        break;
                    case 4:
                        item.StatusName = "操作完成";
                        break;
                    default:
                        item.StatusName = "未知";
                }
                helper.setText(R.id.tv_state, item.StatusName);
            }

        };
        mAdapter.setOnRecyclerItemClickListener(new OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                for (int i = 0; i <mAdapter.getData().size() ; i++) {
                    ((DataInOut) mAdapter.getItem(i)).isSelect=false;
                }
                ((DataInOut) mAdapter.getItem(position)).isSelect=true;
                mAdapter.notifyDataSetChanged();
            }
        });
        lvData.setHasFixedSize(true);
        lvData.setLayoutManager(new LinearLayoutManager(this));
        lvData.setItemAnimator(new DefaultItemAnimator());
        mAdapter.openLoadAnimation(false);
        lvData.setAdapter(mAdapter);
    }

    @Override
    protected void doOperate() {
        Calendar calendar = Calendar.getInstance();
        date = calendar.get(Calendar.YEAR) + "-"
                + (calendar.get(Calendar.MONTH) > 8 ? calendar.get(Calendar.MONTH) + 1 : ("0" + (calendar.get(Calendar.MONTH) + 1))) + "-"
                + (calendar.get(Calendar.DAY_OF_MONTH) > 9 ? calendar.get(Calendar.DAY_OF_MONTH) : ("0" + calendar.get(Calendar.DAY_OF_MONTH)));
        tvTime.setText(date);
        getList();
    }

    private void getList() {
        if (TextUtils.isEmpty(date)) {
            WWToast.showShort("请选择查询日期");
            return;
        }
        showWaitDialog();
        RequestParams params = ParamsUtils.getSessionParams(Api.QueryOrder());
        params.addBodyParameter("direct", (model == INYUYUE) ? "0" : "1");
        params.addBodyParameter("date", date);
        x.http().get(params, new WWXCallBack("QueryOrder") {
            @Override
            public void onAfterSuccessOk(JSONObject data) {
                JSONArray jsonArray = data.getJSONArray("Data");
                lsit = (ArrayList<DataInOut>) JSONObject.parseArray(
                        jsonArray.toJSONString(), DataInOut.class);
                mAdapter.setData(lsit);
                tvNum.setText("已约数:" + data.getString("TotalCount"));
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

    private DateChooseWheelViewDialog startDateChooseDialog;

    @OnClick(R.id.tv_time)
    public void onClick() {
        if (startDateChooseDialog == null) {
            startDateChooseDialog = new DateChooseWheelViewDialog(FuncQueryActivity.this, new DateChooseWheelViewDialog.DateChooseInterface() {
                @Override
                public void getDateTime(long time, boolean longTimeChecked) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(time * 1000);
                    date = calendar.get(Calendar.YEAR) + "-"
                            + (calendar.get(Calendar.MONTH) > 8 ? calendar.get(Calendar.MONTH) + 1 : ("0" + (calendar.get(Calendar.MONTH) + 1))) + "-"
                            + (calendar.get(Calendar.DAY_OF_MONTH) > 9 ? calendar.get(Calendar.DAY_OF_MONTH) : ("0" + calendar.get(Calendar.DAY_OF_MONTH)));
                    tvTime.setText(date);
                    getList();
                }
            });
            startDateChooseDialog.setTimePickerGone(true);
            startDateChooseDialog.setDateDialogTitle("时间选择");

        }
        startDateChooseDialog.showDateChooseDialog();
    }
}
