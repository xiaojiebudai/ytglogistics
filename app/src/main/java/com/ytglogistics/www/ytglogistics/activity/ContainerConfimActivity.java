package com.ytglogistics.www.ytglogistics.activity;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.library.BaseRecyclerAdapter;
import com.github.library.BaseViewHolder;
import com.github.library.listener.OnRecyclerItemClickListener;
import com.ytglogistics.www.ytglogistics.MyApplication;
import com.ytglogistics.www.ytglogistics.R;
import com.ytglogistics.www.ytglogistics.api.Api;
import com.ytglogistics.www.ytglogistics.been.DataCont;
import com.ytglogistics.www.ytglogistics.been.DataSolt;
import com.ytglogistics.www.ytglogistics.dialog.DateChooseWheelViewDialog;
import com.ytglogistics.www.ytglogistics.utils.Consts;
import com.ytglogistics.www.ytglogistics.utils.ParamsUtils;
import com.ytglogistics.www.ytglogistics.utils.SharedPreferenceUtils;
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
 * Created by ZXJ on 2017/10/14.
 * 柜号确认
 */

public class ContainerConfimActivity extends FatherActivity {
    @BindView(R.id.et_no)
    EditText etNo;
    @BindView(R.id.tv_search)
    TextView tvSearch;
    @BindView(R.id.lv_data)
    RecyclerView lvData;
    @BindView(R.id.tv_operater)
    TextView tvOperater;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.ll_time)
    LinearLayout llTime;
    @BindView(R.id.rb_ok)
    RadioButton rbOk;
    @BindView(R.id.rb_waiting)
    RadioButton rbWaiting;
    @BindView(R.id.rb_error)
    RadioButton rb_error;
    @BindView(R.id.rg_result)
    RadioGroup rgResult;
    @BindView(R.id.et_remark)
    EditText etRemark;
    @BindView(R.id.tv_commit)
    TextView tvCommit;
    private ArrayList<DataCont> dataSolts;
    private BaseRecyclerAdapter mAdapter;
    private DataCont dataSolt;
    private int selectId = -1;
    private String containerNo = "";
    private long delTime = -1;
    private DateChooseWheelViewDialog dateChooseWheelViewDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.act_containerconfim;
    }

    @Override
    protected void initValues() {
        initDefautHead("柜号确认", true);
    }

    @Override
    protected void initView() {
        initTextHeadRigth(R.string.fresh, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getData();
            }
        });
        tvOperater.setText(SharedPreferenceUtils.getInstance().getUserName());
    }

    @Override
    protected void doOperate() {
        lvData.setBackgroundResource(R.drawable.bg_yellow_white_shape);
        mAdapter = new BaseRecyclerAdapter<DataCont>(this, dataSolts, R.layout.list_container_confirm_item) {
            @Override
            protected void convert(BaseViewHolder helper, DataCont item) {
                helper.setText(R.id.tv_num, item.ContNo);
                helper.setText(R.id.tv_type, item.ContSize);
                helper.setText(R.id.tv_work_num, item.TaskId);
                helper.setText(R.id.tv_bowei, item.PlaceId);

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
                selectId = position;
                dataSolt = (DataCont) mAdapter.getData().get(position);
                for (int i = 0; i < mAdapter.getData().size(); i++) {
                    ((DataCont) mAdapter.getItem(i)).isSelect = false;
                }
                ((DataCont) mAdapter.getItem(position)).isSelect = true;
                mAdapter.notifyDataSetChanged();
            }
        });
        lvData.setHasFixedSize(true);
        lvData.setLayoutManager(new LinearLayoutManager(this));
        lvData.setItemAnimator(new DefaultItemAnimator());
        mAdapter.openLoadAnimation(false);
        lvData.setAdapter(mAdapter);
        getData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.tv_search, R.id.ll_time, R.id.tv_commit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_search:
                hideSoftKeyboard();
                containerNo = etNo.getText().toString().trim();
                if (TextUtils.isEmpty(containerNo)) {
                WWToast.showShort("请输入柜号查询");
                return;
            }
                getData();

                break;
            case R.id.ll_time:
                if (dateChooseWheelViewDialog == null) {

                    dateChooseWheelViewDialog = new DateChooseWheelViewDialog(ContainerConfimActivity.this, new DateChooseWheelViewDialog.DateChooseInterface() {
                        @Override
                        public void getDateTime(long time, boolean longTimeChecked) {
                            tvTime.setText(TimeUtil.getTimeToS(time * 1000));
                            delTime = time;
                        }
                    });
                    dateChooseWheelViewDialog.setDateDialogTitle("确认时间");

                }
                dateChooseWheelViewDialog.showDateChooseDialog();
                break;
            case R.id.tv_commit:

                if (selectId == -1) {
                    WWToast.showShort("请先选择一条数据");
                    return;
                }
                if (delTime == -1) {
                    WWToast.showShort("请选择确认时间");
                    return;
                }

                String remark = etRemark.getText().toString().trim();
                JSONObject object = new JSONObject();
                switch (rgResult.getCheckedRadioButtonId()) {
                    case R.id.rb_ok:
                        object.put("gcResult", "已处理");
                        break;
                    case R.id.rb_waiting:
                        object.put("gcResult", "待处理");
                        break;
                    case R.id.rb_error:
                        if (TextUtils.isEmpty(remark)) {
                            WWToast.showShort("处理结果异常时必须填写备注信息");
                            return;
                        }
                        object.put("gcResult", "异常");
                        break;
                }
                showWaitDialog();
                object.put(Consts.KEY_SESSIONID, MyApplication
                        .getInstance().getSessionId());
                object.put("taskId", dataSolt.TaskId);
                object.put("gcTime", delTime);
                if (!TextUtils.isEmpty(remark)) {
                    object.put("gcExplain", remark);
                }
//                object.put("gcUser",taskId);


                x.http().post(ParamsUtils.getPostJsonParams(object, Api.TaskOutConfirm()), new WWXCallBack("TaskOutConfirm") {
                    @Override
                    public void onAfterSuccessOk(JSONObject data) {
                        mAdapter.getData().remove(selectId);
                        mAdapter.notifyDataSetChanged();
                        WWToast.showShort("柜号确认成功");
                        selectId = -1;
                        delTime = -1;
                        tvTime.setText("");
                        rgResult.check(R.id.rb_ok);
                        etRemark.setText("");
                    }

                    @Override
                    public void onAfterFinished() {
                        dismissWaitDialog();
                    }
                });
                break;
        }
    }


    private void getData() {
        showWaitDialog();
        RequestParams params = ParamsUtils.getSessionParams(Api.TaskOutList());
        params.addBodyParameter("contNo", containerNo);
        x.http().get(params, new WWXCallBack("TaskOutList") {
            @Override
            public void onAfterSuccessOk(JSONObject data) {
                JSONArray jsonArray = data.getJSONArray("Data");
                dataSolts = (ArrayList<DataCont>) JSONObject.parseArray(
                        jsonArray.toJSONString(), DataCont.class);
                mAdapter.setData(dataSolts);
            }

            @Override
            public void onAfterFinished() {
                dismissWaitDialog();
            }
        });
    }
}
