package com.ytglogistics.www.ytglogistics.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.ytglogistics.www.ytglogistics.MyApplication;
import com.ytglogistics.www.ytglogistics.R;
import com.ytglogistics.www.ytglogistics.api.Api;
import com.ytglogistics.www.ytglogistics.utils.ParamsUtils;
import com.ytglogistics.www.ytglogistics.utils.SharedPreferenceUtils;
import com.ytglogistics.www.ytglogistics.utils.WWToast;
import com.ytglogistics.www.ytglogistics.xutils.WWXCallBack;

import org.xutils.http.RequestParams;
import org.xutils.x;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/12/8.
 */

public class LoginActivity extends FatherActivity {
    @BindView(R.id.ed_username)
    EditText edUsername;
    @BindView(R.id.ed_password)
    EditText edPassword;
    @BindView(R.id.tv_login)
    TextView tvLogin;

    @Override
    protected int getLayoutId() {
        return R.layout.act_login;
    }

    @Override
    protected void initValues() {
        initDefautHead("系统登陆", false);
    }

    @Override
    protected void initView() {
        if(!TextUtils.isEmpty(SharedPreferenceUtils.getInstance().getUserName())){
            edUsername.setText(SharedPreferenceUtils.getInstance().getUserName());
        }

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

    @OnClick(R.id.tv_login)
    public void onClick() {
        login();
    }

    /**
     * 登录操作
     */
    private void login() {

        final String username = edUsername.getText().toString();
        String psw = edPassword.getText().toString();
        if (TextUtils.isEmpty(username)) {
            WWToast.showShort("请输入用户名");
            return;
        }
        if (TextUtils.isEmpty(psw)) {
            WWToast.showShort("密码不能为空");
            return;
        }

        RequestParams params = new RequestParams(Api.UserLogin());
        params.addBodyParameter("userName", username);
        params.addBodyParameter("userPsd", psw);
        showWaitDialog();
        x.http().get(params,
                new WWXCallBack("UserLogin") {

                    @Override
                    public void onAfterSuccessOk(JSONObject data) {
                        MyApplication.getInstance().setSessionPast();
                        MyApplication.getInstance().setSessionId(
                                data.getString("Data"));
                        SharedPreferenceUtils.getInstance().saveUserName(username);
                        startActivity(new Intent(LoginActivity.this,
                                MainActivity.class));
                        finish();
                    }

                    @Override
                    public void onAfterSuccessError(JSONObject data) {
                        super.onAfterSuccessError(data);
                    }

                    @Override
                    public void onAfterFinished() {
                        dismissWaitDialog();
                    }
                });

    }
}
