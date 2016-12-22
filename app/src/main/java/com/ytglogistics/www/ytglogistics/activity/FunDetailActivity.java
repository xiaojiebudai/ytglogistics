package com.ytglogistics.www.ytglogistics.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TimePicker;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ytglogistics.www.ytglogistics.MyApplication;
import com.ytglogistics.www.ytglogistics.R;
import com.ytglogistics.www.ytglogistics.adapter.StevedListSelectAdapter;
import com.ytglogistics.www.ytglogistics.adapter.UserListSelectAdapter;
import com.ytglogistics.www.ytglogistics.api.Api;
import com.ytglogistics.www.ytglogistics.been.AppInMax;
import com.ytglogistics.www.ytglogistics.been.AppInResult;
import com.ytglogistics.www.ytglogistics.been.Car;
import com.ytglogistics.www.ytglogistics.been.Steved;
import com.ytglogistics.www.ytglogistics.been.User;
import com.ytglogistics.www.ytglogistics.dialog.DateChooseWheelViewDialog;
import com.ytglogistics.www.ytglogistics.dialog.InputDialog;
import com.ytglogistics.www.ytglogistics.utils.Consts;
import com.ytglogistics.www.ytglogistics.utils.ParamsUtils;
import com.ytglogistics.www.ytglogistics.utils.RegexUtil;
import com.ytglogistics.www.ytglogistics.utils.SharedPreferenceUtils;
import com.ytglogistics.www.ytglogistics.utils.TimeUtil;
import com.ytglogistics.www.ytglogistics.utils.WWToast;
import com.ytglogistics.www.ytglogistics.utils.ZLog;
import com.ytglogistics.www.ytglogistics.utils.preDefiniation;
import com.ytglogistics.www.ytglogistics.xutils.WWXCallBack;

import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/12/8.
 */

public class FunDetailActivity extends FatherActivity {
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

    private AppInResult result;
    private Car car;
    public static final int FUNIN=0;
    public static final int FUNOUT=1;
    private int model=0;
    @Override
    protected int getLayoutId() {
        return R.layout.act_fundetail;
    }

    @Override
    protected void initValues() {
        model=getIntent().getIntExtra(Consts.KEY_MODULE,FUNIN);
        initDefautHead("详情", true);
    }

    @Override
    protected void initView() {
if(model==FUNIN){
    tvSave.setText("保存");
    tvGet.setText("收货");
}else{
    tvSave.setText("提交资料");
    tvGet.setText("出货录入");
}
    }

    @Override
    protected void doOperate() {
        RequestParams params = ParamsUtils.getSessionParams((model==FUNIN)?Api.GetAppIn():Api.GetAppOut());
        if(model==FUNIN){
            params.addBodyParameter("queueNo", car.YyNo);
        }else{
            params.addBodyParameter("orderId", car.OrderId);
        }

        x.http().get(params, new WWXCallBack((model==FUNIN)?"GetAppIn":"GetAppOut") {
            @Override
            public void onAfterSuccessOk(JSONObject data) {
                result = JSON.parseObject(data.getString("Data"), AppInResult.class);
                et2.setText(TimeUtil.getTimeToS(result.JdTime * 1000));
                et3.setText(TimeUtil.getTimeToS(result.PdTime * 1000));
                et4.setText(TimeUtil.getTimeToS(result.BeginTime * 1000));
                et5.setText(TimeUtil.getTimeToS(result.EndTime * 1000));
                getStevedData();
                getUserData();
            }

            @Override
            public void onAfterFinished() {

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

    @OnClick({R.id.ll_0, R.id.ll_1, R.id.ll_2, R.id.ll_3, R.id.ll_4, R.id.ll_5, R.id.tv_save, R.id.tv_get})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_0:
                seleteSteved();
                break;
            case R.id.ll_1:
                seleteUser();
                break;
            case R.id.ll_2:
                if (startDateChooseDialog == null) {

                    startDateChooseDialog = new DateChooseWheelViewDialog(FunDetailActivity.this, new DateChooseWheelViewDialog.DateChooseInterface() {
                        @Override
                        public void getDateTime(long time, boolean longTimeChecked) {
                            et2.setText(TimeUtil.getTimeToS(time*1000));
                            result.JdTime=time;
                        }
                    });
                    startDateChooseDialog.setDateDialogTitle("接单时间");

                }
                startDateChooseDialog.showDateChooseDialog();
                break;
            case R.id.ll_3:
                if (startDateChooseDialog1 == null) {
                    startDateChooseDialog1 = new DateChooseWheelViewDialog(FunDetailActivity.this, new DateChooseWheelViewDialog.DateChooseInterface() {
                        @Override
                        public void getDateTime(long time, boolean longTimeChecked) {
                            et3.setText(TimeUtil.getTimeToS(time*1000));
                            result.PdTime=time;
                        }
                    });
                    startDateChooseDialog1.setDateDialogTitle("派单时间");
                }
                startDateChooseDialog1.showDateChooseDialog();
                break;
            case R.id.ll_4:
                if (startDateChooseDialog2 == null) {
                    startDateChooseDialog2 = new DateChooseWheelViewDialog(FunDetailActivity.this, new DateChooseWheelViewDialog.DateChooseInterface() {
                        @Override
                        public void getDateTime(long time, boolean longTimeChecked) {
                            et4.setText(TimeUtil.getTimeToS(time*1000));
                            result.BeginTime=time;
                        }
                    });
                    startDateChooseDialog2.setDateDialogTitle("开始时间");
                }
                startDateChooseDialog2.showDateChooseDialog();
                break;
            case R.id.ll_5:
                if (startDateChooseDialog3 == null) {
                    startDateChooseDialog3 = new DateChooseWheelViewDialog(FunDetailActivity.this, new DateChooseWheelViewDialog.DateChooseInterface() {
                        @Override
                        public void getDateTime(long time, boolean longTimeChecked) {
                            et5.setText(TimeUtil.getTimeToS(time*1000));
                            result.EndTime=time;
                        }
                    });
                    startDateChooseDialog3.setDateDialogTitle("结束时间");
                }
                startDateChooseDialog3.showDateChooseDialog();
                break;
            case R.id.tv_save:
                saveInfo();
                break;
            case R.id.tv_get:
                Intent intent = new Intent(FunDetailActivity.this,(model==FUNIN)? FunInMaxListActivity.class:FunOutMxListActivity.class);
                intent.putExtra(Consts.KEY_DATA, JSON.toJSONString(result));
                startActivity(intent);
                break;
        }
    }

    private void saveInfo() {

        JSONObject jsonObject=new JSONObject();
        jsonObject.put(Consts.KEY_SESSIONID, MyApplication
                .getInstance().getSessionId());
        jsonObject.put("obj",result.toJson());
        x.http().post(ParamsUtils.getPostJsonParams(jsonObject,Api.AppInCommit()), new WWXCallBack("AppInCommit") {
            @Override
            public void onAfterSuccessOk(JSONObject data) {
                    result.Serial=data.getString("Data");

               WWToast.showShort("提交成功");
                tvSave.setClickable(false);
            }

            @Override
            public void onAfterFinished() {

            }
        });
    }


    private void getStevedData() {
        RequestParams params = ParamsUtils.getSessionParams(Api.GetSteved());
        x.http().get(params, new WWXCallBack("GetSteved") {
            @Override
            public void onAfterSuccessOk(JSONObject data) {
                JSONArray jsonArray = data.getJSONArray("Data");
                stevedList = (ArrayList<Steved>) JSON.parseArray(
                        jsonArray.toJSONString(), Steved.class);
                if (stevedList != null && stevedList.size() > 0) {
                    for (int i = 0; i < stevedList.size(); i++) {
                        if(stevedList.get(i).Keyid.equals(result.StevedId)){
                            et0.setText(stevedList.get(i).Stevedoringcompanyname);
                            break;
                        }
                    }

                }
            }

            @Override
            public void onAfterFinished() {

            }
        });
    }

    private void getUserData() {
        RequestParams params = ParamsUtils.getSessionParams(Api.GetUsers());
        x.http().get(params, new WWXCallBack("GetUsers") {
            @Override
            public void onAfterSuccessOk(JSONObject data) {
                JSONArray jsonArray = data.getJSONArray("Data");
                userList = (ArrayList<User>) JSON.parseArray(
                        jsonArray.toJSONString(), User.class);
                if (userList != null && userList.size() > 0) {
                    for (int i = 0; i < userList.size(); i++) {
                        if(userList.get(i).Keyid.equals(result.UserId)){
                            et1.setText(userList.get(i).Usercode);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onAfterFinished() {

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
                    result.StevedId=stevedList.get(position).Keyid;
                    result.StevedName=stevedList.get(position).Stevedoringcompanyname;
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
                    result.UserId=userList.get(position).Keyid;
                    result.UserCode=userList.get(position).Usercode;
                    popupWindowUser.dismiss();
                }
            });
        }
        popupWindowUser.showAsDropDown(ll1);
    }


}
