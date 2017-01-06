package com.ytglogistics.www.ytglogistics.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.fastjson.JSONObject;
import com.ytglogistics.www.ytglogistics.MyApplication;
import com.ytglogistics.www.ytglogistics.R;
import com.ytglogistics.www.ytglogistics.api.Api;
import com.ytglogistics.www.ytglogistics.utils.Consts;
import com.ytglogistics.www.ytglogistics.utils.ParamsUtils;
import com.ytglogistics.www.ytglogistics.utils.PublicWay;
import com.ytglogistics.www.ytglogistics.utils.SharedPreferenceUtils;
import com.ytglogistics.www.ytglogistics.utils.WWToast;
import com.ytglogistics.www.ytglogistics.xutils.WWXCallBack;

import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends FatherActivity {
    @BindView(R.id.receiving)
    ImageView receiving;
    @BindView(R.id.shipping)
    ImageView shipping;
    @BindView(R.id.warehousing)
    ImageView warehousing;
    @BindView(R.id.outofstorage)
    ImageView outofstorage;
    @BindView(R.id.garage_m)
    ImageView garageM;
    @BindView(R.id.move_operate)
    ImageView moveOperate;
    @BindView(R.id.housing_reservation_query)
    ImageView housingReservationQuery;
    @BindView(R.id.outofstorage_query)
    ImageView outofstorageQuery;
    @BindView(R.id.locale_photos)
    ImageView localePhotos;
    @BindView(R.id.housing_reservation_po_query)
    ImageView housingReservationPoQuery;
    @BindView(R.id.berth_map)
    ImageView berthMap;
    @BindView(R.id.loginout)
    ImageView loginout;
    @BindView(R.id.exit)
    ImageView exit;
    @BindView(R.id.start_app)
    ImageView start_app;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initValues() {
        initDefautHead("功能列表", false);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void doOperate() {

    }

    // 不去区分业务模式了，之后再改
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        boolean sessionError = intent.getBooleanExtra(Consts.KEY_SESSION_ERROR,
                false);
        if (sessionError) {// session相关错误
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.start_app,R.id.receiving, R.id.shipping, R.id.warehousing, R.id.outofstorage, R.id.garage_m, R.id.move_operate, R.id.housing_reservation_query, R.id.outofstorage_query, R.id.locale_photos, R.id.housing_reservation_po_query, R.id.berth_map, R.id.loginout, R.id.exit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.receiving:
                startActivity(new Intent(this, FunInActivity.class).putExtra(Consts.KEY_MODULE, FunInActivity.INOPERATE));
                break;
            case R.id.shipping:
                startActivity(new Intent(this, FunOutActivity.class).putExtra(Consts.KEY_MODULE, FunOutActivity.OUTOPERATE));
                break;
            case R.id.warehousing:
                startActivity(new Intent(this, FunInActivity.class).putExtra(Consts.KEY_MODULE, FunInActivity.INEDIT));
                break;
            case R.id.outofstorage:
                startActivity(new Intent(this, FunOutActivity.class).putExtra(Consts.KEY_MODULE, FunOutActivity.OUTEDIT));
                break;
            case R.id.garage_m:
                startActivity(new Intent(this, FuncPlaceActivity.class));
                break;
            case R.id.move_operate:
                startActivity(new Intent(this, MoveLocaActivity.class));
                break;
            case R.id.housing_reservation_query:
                startActivity(new Intent(this, FuncQueryActivity.class).putExtra(Consts.KEY_MODULE, FuncQueryActivity.INYUYUE));
                break;
            case R.id.outofstorage_query:
                startActivity(new Intent(this, FuncQueryActivity.class).putExtra(Consts.KEY_MODULE, FuncQueryActivity.OUTYUYUE));
                break;
            case R.id.locale_photos:
                startActivity(new Intent(this, FuncImageActivity.class));
                break;
            case R.id.housing_reservation_po_query:
                startActivity(new Intent(this, PoSearchListActivity.class));
                break;
            case R.id.berth_map:
                startActivity(new Intent(this, FuncPlaceStatusActivity.class));
                break;
            case R.id.loginout:
                MyApplication.getInstance().setSessionId("");
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.exit:
                finish();
                break;
            case R.id.start_app:
           if(isAvilible(this,"com.embarcadero.PonderApp")) {
               PublicWay.startApp(this,"com.embarcadero.PonderApp","com.embarcadero.PonderApp.activity.WelcomeActivity");
           }else{
               WWToast.showShort("还未安装衡云，请先去应用市场下载");
           }


                break;
        }
    }
    private boolean isAvilible(Context context, String packageName){
        final PackageManager packageManager = context.getPackageManager();//获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);//获取所有已安装程序的包信息
        List<String> pName = new ArrayList<String>();//用于存储所有已安装程序的包名
        //从pinfo中将包名字逐一取出，压入pName list中
        if(pinfo != null){
            for(int i = 0; i < pinfo.size(); i++){
                String pn = pinfo.get(i).packageName;
                pName.add(pn);
            }
        }
        return pName.contains(packageName);//判断pName中是否有目标程序的包名，有TRUE，没有FALSE
    }
    @Override
    protected void onResume() {
        super.onResume();
        getUserIngo();
    }

    private void getUserIngo() {
        showWaitDialog();
        x.http().get(ParamsUtils.getSessionParams(Api.GetMyInfo()), new WWXCallBack("GetMyInfo") {
            @Override
            public void onAfterSuccessOk(JSONObject data) {
                SharedPreferenceUtils.getInstance().saveUserInfo(data.getString("Data"));
            }

            @Override
            public void onAfterFinished() {
                dismissWaitDialog();
            }
        });
    }
}
