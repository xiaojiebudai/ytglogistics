package com.ytglogistics.www.ytglogistics.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.ytglogistics.www.ytglogistics.MyApplication;
import com.ytglogistics.www.ytglogistics.R;
import com.ytglogistics.www.ytglogistics.adapter.StevedListSelectAdapter;
import com.ytglogistics.www.ytglogistics.adapter.UserListSelectAdapter;
import com.ytglogistics.www.ytglogistics.api.Api;
import com.ytglogistics.www.ytglogistics.been.AppInResult;
import com.ytglogistics.www.ytglogistics.been.Car;
import com.ytglogistics.www.ytglogistics.been.Steved;
import com.ytglogistics.www.ytglogistics.been.User;
import com.ytglogistics.www.ytglogistics.dialog.CommonDialog;
import com.ytglogistics.www.ytglogistics.dialog.DateChooseWheelViewDialog;
import com.ytglogistics.www.ytglogistics.utils.Consts;
import com.ytglogistics.www.ytglogistics.utils.DialogUtils;
import com.ytglogistics.www.ytglogistics.utils.ParamsUtils;
import com.ytglogistics.www.ytglogistics.utils.SharedPreferenceUtils;
import com.ytglogistics.www.ytglogistics.utils.TimeUtil;
import com.ytglogistics.www.ytglogistics.utils.WWToast;
import com.ytglogistics.www.ytglogistics.utils.ZLog;
import com.ytglogistics.www.ytglogistics.xutils.WWXCallBack;

import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/12/8.
 */

public class FunDetailActivity extends FatherActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {
    @BindView(R.id.et_0)
    TextView et0;
    @BindView(R.id.ll_0)
    LinearLayout ll0;
    @BindView(R.id.et_1)
    TextView et1;
    @BindView(R.id.ll_1)
    LinearLayout ll1;
    @BindView(R.id.et_2)
    TextView et2;
    @BindView(R.id.ll_2)
    LinearLayout ll2;
    @BindView(R.id.et_3)
    TextView et3;
    @BindView(R.id.ll_3)
    LinearLayout ll3;
    @BindView(R.id.et_4)
    TextView et4;
    @BindView(R.id.ll_4)
    LinearLayout ll4;
    @BindView(R.id.et_5)
    TextView et5;
    @BindView(R.id.ll_5)
    LinearLayout ll5;
    @BindView(R.id.tv_save)
    TextView tvSave;
    @BindView(R.id.tv_get)
    TextView tvGet;
    @BindView(R.id.tv_commit)
    TextView tv_commit;

    private AppInResult result;
    private Car car;
    public static final int FUNIN = 0;
    public static final int FUNOUT = 1;
    private int model = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.act_fundetail;
    }

    @Override
    protected void initValues() {
        model = getIntent().getIntExtra(Consts.KEY_MODULE, FUNIN);
        initDefautHead("详情", true);
        car = JSONObject.parseObject(getIntent().getStringExtra(Consts.KEY_DATA), Car.class);
    }

    @Override
    protected void initView() {
        if (model == FUNIN) {
            tvSave.setText("保存");
            tvGet.setText("收货");
            tv_commit.setVisibility(View.VISIBLE);
        } else {
            tvSave.setText("提交资料");
            tvGet.setText("出货录入");
        }
    }

    @Override
    protected void doOperate() {
        showWaitDialog();
        RequestParams params = ParamsUtils.getSessionParams((model == FUNIN) ? Api.GetAppIn() : Api.GetAppOut());
        if (model == FUNIN) {
            params.addBodyParameter("queueNo", car.YyNo);
        } else {
            params.addBodyParameter("orderId", car.OrderId);
        }
        x.http().get(params, new WWXCallBack((model == FUNIN) ? "GetAppIn" : "GetAppOut") {
            @Override
            public void onAfterSuccessOk(JSONObject data) {
                result = JSON.parseObject(data.getString("Data"), AppInResult.class);
                if (result.Serial == 0) {
                    User user = (User) JSONObject.parseObject(SharedPreferenceUtils.getInstance().getUserInfo(), User.class);
                    result.UserId = user.Keyid;
                    result.QueueNo = car.YyNo;
                    result.CarNo = car.CarNo;
                    result.PlaceId = car.PlaceId;
                    result.OrderId = car.OrderId;
                    result.So = car.So;
                    result.OperType = 1;
                } else {
                    if (result.JdTime != null)
                        et2.setText(TimeUtil.getTimeToS(result.JdTime * 1000));
                    if (result.PdTime != null)
                        et3.setText(TimeUtil.getTimeToS(result.PdTime * 1000));
                    if (result.BeginTime != null)
                        et4.setText(TimeUtil.getTimeToS(result.BeginTime * 1000));
                    if (result.EndTime != null)
                        et5.setText(TimeUtil.getTimeToS(result.EndTime * 1000));
                }
                getStevedData();
                getUserData();
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

    private DateChooseWheelViewDialog startDateChooseDialog, startDateChooseDialog1, startDateChooseDialog2, startDateChooseDialog3;
    private DatePickerDialog dpd, dpd1, dpd2, dpd3;
    private TimePickerDialog tpd, tpd1, tpd2, tpd3;

    @OnClick({R.id.tv_commit, R.id.ll_0, R.id.ll_1, R.id.ll_2, R.id.ll_3, R.id.ll_4, R.id.ll_5, R.id.tv_save, R.id.tv_get})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_0:
                seleteSteved();
                break;
            case R.id.ll_1:
                seleteUser();
                break;
            case R.id.ll_2:
                if (dpd == null) {
                    Calendar now = Calendar.getInstance();
                     dpd = DatePickerDialog.newInstance(
                            FunDetailActivity.this,
                            now.get(Calendar.YEAR),
                            now.get(Calendar.MONTH),
                            now.get(Calendar.DAY_OF_MONTH)
                    );
                    dpd.setVersion(DatePickerDialog.Version.VERSION_2);
                }
                dpd.show(getFragmentManager(), "Datepickerdialog");

                break;
            case R.id.ll_3:
                if (dpd1 == null) {
                    Calendar now = Calendar.getInstance();
                     dpd1 = DatePickerDialog.newInstance(
                            FunDetailActivity.this,
                            now.get(Calendar.YEAR),
                            now.get(Calendar.MONTH),
                            now.get(Calendar.DAY_OF_MONTH)
                    );
                    dpd1.setVersion(DatePickerDialog.Version.VERSION_2);
                }
                dpd1.show(getFragmentManager(), "Datepickerdialog1");

                break;
            case R.id.ll_4:
                if (dpd2 == null) {
                    Calendar now = Calendar.getInstance();
                     dpd2 = DatePickerDialog.newInstance(
                            FunDetailActivity.this,
                            now.get(Calendar.YEAR),
                            now.get(Calendar.MONTH),
                            now.get(Calendar.DAY_OF_MONTH)
                    );
                    dpd2.setVersion(DatePickerDialog.Version.VERSION_2);
                }
                dpd2.show(getFragmentManager(), "Datepickerdialog2");

                break;
            case R.id.ll_5:
                if (dpd3 == null) {
                    Calendar now = Calendar.getInstance();
                     dpd3 = DatePickerDialog.newInstance(
                            FunDetailActivity.this,
                            now.get(Calendar.YEAR),
                            now.get(Calendar.MONTH),
                            now.get(Calendar.DAY_OF_MONTH)
                    );
                    dpd3.setVersion(DatePickerDialog.Version.VERSION_2);
                }
                dpd3.show(getFragmentManager(), "Datepickerdialog3");

                break;
            case R.id.tv_save:
                saveInfo();
                break;
            case R.id.tv_get:
                if (result.Serial > 0) {
                    Intent intent = new Intent(FunDetailActivity.this, (model == FUNIN) ? FunInMaxListActivity.class : FunOutMxListActivity.class);
                    intent.putExtra(Consts.KEY_DATA, JSON.toJSONString(result));
                    startActivity(intent);
                } else {
                    WWToast.showShort("请选择信息并保存后再操作");
                }

                break;
            case R.id.tv_commit:
//                if (result.JdTime == null || result.PdTime == null || result.BeginTime == null || result.EndTime == null) {
//                    WWToast.showShort("时间必须选择完全");
//                    return;
//                }
//                if (result.JdTime > result.PdTime || result.JdTime > result.BeginTime || result.JdTime > result.EndTime
//                        || result.PdTime > result.BeginTime || result.PdTime > result.EndTime
//                        || result.BeginTime > result.EndTime
//                        ) {
//                    WWToast.showShort("时间先后顺序有误");
//                    return;
//                }
                final CommonDialog commonDialogTwiceConfirm = DialogUtils.getCommonDialogTwiceConfirm(this, "请确认收货录入的数据已完成并提交？", true);
                commonDialogTwiceConfirm.setRightButtonCilck(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        commonDialogTwiceConfirm.dismiss();
                        showWaitDialog();

                        RequestParams sessionParams = ParamsUtils.getSessionParams(Api.FinishQueue());
                        sessionParams.addBodyParameter("operDate", car.OperDate);
                        sessionParams.addBodyParameter("queueNo", car.QueueNo);
                        ZLog.showPost(JSONObject.toJSONString(car));
                        x.http().get(sessionParams, new WWXCallBack("FinishQueue") {
                            @Override
                            public void onAfterSuccessOk(JSONObject data) {
                                WWToast.showShort("提交成功");
                                finish();
                            }

                            @Override
                            public void onAfterFinished() {
                                dismissWaitDialog();
                            }
                        });
                    }
                });
                commonDialogTwiceConfirm.show();
                break;

        }
    }

    private void saveInfo() {
        if (TextUtils.isEmpty(result.UserId) || TextUtils.isEmpty(result.StevedId)) {
            WWToast.showShort("请先完善信息再保存");
            return;
        }
        showWaitDialog();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Consts.KEY_SESSIONID, MyApplication
                .getInstance().getSessionId());
        jsonObject.put("obj", result.toJson());
        x.http().post(ParamsUtils.getPostJsonParams(jsonObject, Api.AppInCommit()), new WWXCallBack("AppInCommit") {
            @Override
            public void onAfterSuccessOk(JSONObject data) {
                result.Serial = data.getIntValue("Data");
                WWToast.showShort("提交成功");
                tvSave.setClickable(false);
            }

            @Override
            public void onAfterFinished() {
                dismissWaitDialog();
            }
        });
    }


    private void getStevedData() {
        showWaitDialog();
        RequestParams params = ParamsUtils.getSessionParams(Api.GetSteved());
        x.http().get(params, new WWXCallBack("GetSteved") {
            @Override
            public void onAfterSuccessOk(JSONObject data) {
                JSONArray jsonArray = data.getJSONArray("Data");
                stevedList = (ArrayList<Steved>) JSON.parseArray(
                        jsonArray.toJSONString(), Steved.class);
                if (stevedList != null && stevedList.size() > 0) {
                    for (int i = 0; i < stevedList.size(); i++) {
                        if (stevedList.get(i).Keyid.equals(result.StevedId)) {
                            et0.setText(stevedList.get(i).Stevedoringcompanyname);
                            break;
                        }
                    }

                }
            }

            @Override
            public void onAfterFinished() {
                dismissWaitDialog();
            }
        });
    }

    private void getUserData() {
        showWaitDialog();
        RequestParams params = ParamsUtils.getSessionParams(Api.GetUsers());
        x.http().get(params, new WWXCallBack("GetUsers") {
            @Override
            public void onAfterSuccessOk(JSONObject data) {
                JSONArray jsonArray = data.getJSONArray("Data");
                userList = (ArrayList<User>) JSON.parseArray(
                        jsonArray.toJSONString(), User.class);
                boolean isHave = false;
                if (userList != null && userList.size() > 0) {
                    for (int i = 0; i < userList.size(); i++) {
                        if (userList.get(i).Keyid.equals(result.UserId)) {
                            isHave = true;
                            et1.setText(userList.get(i).Usercode);
                            break;
                        }
                    }
                }
                if (!isHave) {
                    result.UserId = "";
                }
            }

            @Override
            public void onAfterFinished() {
                dismissWaitDialog();
            }
        });
    }

    /**
     * 选择公司
     */
    private ArrayList<Steved> stevedList;
    private PopupWindow popupWindow;

    private void seleteSteved() {
        if (popupWindow == null) {


            ListView listView = new ListView(this);
            listView.setBackgroundResource(R.drawable.bg_yellow_white_shape);
            final StevedListSelectAdapter adapter = new StevedListSelectAdapter(this);
            int width = 80;
            listView.setAdapter(adapter);
            adapter.setData(stevedList);
            popupWindow = new PopupWindow(listView,
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    adapter.setSelectPostion(position);
                    et0.setText(stevedList.get(position).Stevedoringcompanyname);
                    result.StevedId = stevedList.get(position).Keyid;
                    result.StevedName = stevedList.get(position).Stevedoringcompanyname;
                    popupWindow.dismiss();
                }
            });
        }
        popupWindow.showAsDropDown(ll0);
    }

    /**
     * 选择员工
     */
    private ArrayList<User> userList;
    private PopupWindow popupWindowUser;

    private void seleteUser() {
        if (popupWindowUser == null) {
            ListView listView = new ListView(this);
            listView.setBackgroundResource(R.drawable.bg_yellow_white_shape);
            final UserListSelectAdapter adapter = new UserListSelectAdapter(this);
            int width = 80;
            listView.setAdapter(adapter);
            adapter.setData(userList);
            popupWindowUser = new PopupWindow(listView,
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            popupWindowUser.setBackgroundDrawable(new ColorDrawable(Color.WHITE));

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    adapter.setSelectPostion(position);
                    et1.setText(userList.get(position).Usercode);
                    result.UserId = userList.get(position).Keyid;
                    result.UserCode = userList.get(position).Usercode;
                    popupWindowUser.dismiss();
                }
            });
        }
        popupWindowUser.showAsDropDown(ll1);
    }
private int year,month,day,hour,min,sec;
    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        this.year=year;
        this.month=monthOfYear+1;
        this.day=dayOfMonth;
        if (view == dpd) {
            if (tpd == null) {
                Calendar now = Calendar.getInstance();
                tpd = TimePickerDialog.newInstance(
                        FunDetailActivity.this,
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        true
                );
                tpd.enableSeconds(true);
                tpd.setVersion(TimePickerDialog.Version.VERSION_2);
            }
            tpd.show(getFragmentManager(), "Timepickerdialog");
        } else if (view == dpd1) {
            if (tpd1 == null) {
                Calendar now = Calendar.getInstance();
                tpd1 = TimePickerDialog.newInstance(
                        FunDetailActivity.this,
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        true
                );
                tpd1.enableSeconds(true);
                tpd1.setVersion(TimePickerDialog.Version.VERSION_2);
            }
            tpd1.show(getFragmentManager(), "Timepickerdialog1");
        } else if (view == dpd2) {
            if (tpd2 == null) {
                Calendar now = Calendar.getInstance();
                tpd2 = TimePickerDialog.newInstance(
                        FunDetailActivity.this,
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        true
                );
                tpd2.enableSeconds(true);
                tpd2.setVersion(TimePickerDialog.Version.VERSION_2);
            }
            tpd2.show(getFragmentManager(), "Timepickerdialog2");
        } else if (view == dpd3) {
            if (tpd3 == null) {
                Calendar now = Calendar.getInstance();
                tpd3 = TimePickerDialog.newInstance(
                        FunDetailActivity.this,
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        true
                );
                tpd3.enableSeconds(true);
                tpd3.setVersion(TimePickerDialog.Version.VERSION_2);
            }
            tpd3.show(getFragmentManager(), "Timepickerdialog3");
        }
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        this.hour=hourOfDay;
        this.min=minute;
        this.sec=second;
        long time =strTimeToDateFormat(year,month,day,hour,min,sec);
        if (view == tpd) {
            et2.setText(TimeUtil.getTimeToS(time * 1000));
            result.JdTime = time;
        } else if (view == tpd1) {
            et3.setText(TimeUtil.getTimeToS(time * 1000));
            result.PdTime = time;
        } else if (view == tpd2) {
            et4.setText(TimeUtil.getTimeToS(time * 1000));
            result.BeginTime = time;
        } else if (view == tpd3) {
            et5.setText(TimeUtil.getTimeToS(time * 1000));
            result.EndTime = time;
        }
    }

    /**
     * xx年xx月xx日xx时xx分转成时间戳
     *
     * @param yearStr
     * @param month
     * @param day
     * @param hourStr
     * @param minuteStr
     * @return
     */
    private long strTimeToDateFormat(int yearStr,int month, int day, int hourStr, int minuteStr,int sec) {
        String s = yearStr+ "-"+ month+ "-"+ day+"-"+ hourStr+"-"+
                + hourStr + "-" + minuteStr + "-"+sec;
        return Long.parseLong(TimeUtil.dataOne(s));
    }
}
