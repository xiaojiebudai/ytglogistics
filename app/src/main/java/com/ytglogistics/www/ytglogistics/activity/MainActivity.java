package com.ytglogistics.www.ytglogistics.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ytglogistics.www.ytglogistics.MyApplication;
import com.ytglogistics.www.ytglogistics.R;
import com.ytglogistics.www.ytglogistics.api.Api;
import com.ytglogistics.www.ytglogistics.been.VersionInfo;
import com.ytglogistics.www.ytglogistics.dialog.CommonDialog;
import com.ytglogistics.www.ytglogistics.service.UpdateVersionService;
import com.ytglogistics.www.ytglogistics.utils.Consts;
import com.ytglogistics.www.ytglogistics.utils.DialogUtils;
import com.ytglogistics.www.ytglogistics.utils.ParamsUtils;
import com.ytglogistics.www.ytglogistics.utils.PublicWay;
import com.ytglogistics.www.ytglogistics.utils.SharedPreferenceUtils;
import com.ytglogistics.www.ytglogistics.utils.SystemUtil;
import com.ytglogistics.www.ytglogistics.utils.WWToast;
import com.ytglogistics.www.ytglogistics.utils.ZLog;
import com.ytglogistics.www.ytglogistics.xutils.WWXCallBack;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
    @BindView(R.id.cctv)
    ImageView cctv;

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
        appVerGet();
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

    @OnClick({R.id.cctv, R.id.start_app, R.id.receiving, R.id.shipping, R.id.warehousing, R.id.outofstorage, R.id.garage_m, R.id.move_operate, R.id.housing_reservation_query, R.id.outofstorage_query, R.id.locale_photos, R.id.housing_reservation_po_query, R.id.berth_map, R.id.loginout, R.id.exit})
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
                if (isAvilible(this, "com.embarcadero.PonderApp")) {
                    PublicWay.startApp(this, "com.embarcadero.PonderApp", "com.embarcadero.PonderApp.activity.WelcomeActivity");
                } else {
                    WWToast.showShort("还未安装衡云，请先去应用市场下载");
                }


                break;
            case R.id.cctv:
                if (isAvilible(this, "sinofloat.safe")) {
                    PublicWay.startApp(this, "sinofloat.safe", "sinofloat.main.ui.activities.LauncherActivity");
                } else {
                    WWToast.showShort("还未安装移动安防，请先去应用市场下载");
                }


                break;
        }
    }

    private boolean isAvilible(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();//获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);//获取所有已安装程序的包信息
        List<String> pName = new ArrayList<String>();//用于存储所有已安装程序的包名
        //从pinfo中将包名字逐一取出，压入pName list中
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
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

    private void appVerGet() {
        RequestParams params = new RequestParams("http://www.yplog.com.cn/x5/App/appinfo.json");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                final VersionInfo version = JSONObject.parseObject(result, VersionInfo.class);
                version.IsForce = true;
                version.DownloadUrl = "http://www.yplog.com.cn/x5/App/app-release.apk";
                try {
                    if (version.VerNo != SystemUtil.getVersionCode()) {
                        final CommonDialog commonDialog = DialogUtils
                                .getCommonDialog(MainActivity.this,
                                        version.VerInfo);
                        commonDialog.getButtonLeft().setText(R.string.cancel);
                        commonDialog.getButtonLeft().setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View arg0) {
                                        if (version.IsForce) {
                                            finish();
                                        }
                                        commonDialog.dismiss();
                                    }
                                });
                        commonDialog.getButtonRight().setText(R.string.download_right_now);
                        commonDialog.getButtonRight().setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View arg0) {

                                        if (version.IsForce) {
                                            apk_path = version.DownloadUrl;
                                            showDownloadDialog();
                                        } else {
                                            Intent updateIntent = new Intent(MainActivity.this,
                                                    UpdateVersionService.class);
                                            updateIntent
                                                    .putExtra("titleId", R.string.app_name);
                                            updateIntent.putExtra("downloadUrl", version.DownloadUrl);
                                            updateIntent.putExtra("app_desc", version.VerInfo);
                                            startService(updateIntent);

                                        }
                                        commonDialog.dismiss();
                                    }
                                });
                        if (version.IsForce) {
                            commonDialog.setCancelable(false);
                        } else {
                            commonDialog.setCancelable(true);
                        }
                        commonDialog.show();
                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });


    }

    ProgressDialog progressDialog;
    // 外存sdcard存放路径
    private static final String FILE_PATH = Environment.getExternalStorageDirectory() + "/" + "app/download" + "/";
    // 下载应用存放全路径
    private static final String FILE_NAME = FILE_PATH + "ytgapp.apk";
    // 准备安装新版本应用标记
    private static final int INSTALL_TOKEN = 1;
    private String apk_path = "";

    /**
     * 显示下载进度对话框
     */
    public void showDownloadDialog() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(R.string.downloading);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        new downloadAsyncTask().execute();
    }

    /**
     * 下载新版本应用
     */
    private class downloadAsyncTask extends AsyncTask<Void, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            progressDialog.show();
        }

        @Override
        protected Integer doInBackground(Void... params) {

            URL url;
            HttpURLConnection connection = null;
            InputStream in = null;
            FileOutputStream out = null;
            try {
                url = new URL(apk_path);
                connection = (HttpURLConnection) url.openConnection();

                in = connection.getInputStream();
                long fileLength = connection.getContentLength();
                File file_path = new File(FILE_PATH);
                if (!file_path.exists()) {
                    file_path.mkdir();
                }

                out = new FileOutputStream(new File(FILE_NAME));//为指定的文件路径创建文件输出流
                byte[] buffer = new byte[1024 * 1024];
                int len = 0;
                long readLength = 0;
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);//从buffer的第0位开始读取len长度的字节到输出流
                    readLength += len;
                    int curProgress = (int) (((float) readLength / fileLength) * 100);
                    publishProgress(curProgress);
                    if (readLength >= fileLength) {
                        break;
                    }
                }

                out.flush();
                return INSTALL_TOKEN;

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (connection != null) {
                    connection.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

            progressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Integer integer) {

            progressDialog.dismiss();//关闭进度条
            final CommonDialog commonDialog = DialogUtils
                    .getCommonDialog(MainActivity.this,
                            R.string.download_success_please_install);
            commonDialog.getButtonLeft().setVisibility(View.GONE);
            commonDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    finish();
                }
            });
            commonDialog.setCanceledOnTouchOutside(false);
            commonDialog.getButtonRight().setText(R.string.install_now);
            commonDialog.getButtonRight().setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            installApp();
                        }
                    });
            commonDialog.show();
            //安装应用
            installApp();
        }
    }

    /**
     * 安装新版本应用
     */
    private void installApp() {
        File appFile = new File(FILE_NAME);
        if (!appFile.exists()) {
            return;
        }
        // 跳转到新版本应用安装页面
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + appFile.toString()), "application/vnd.android.package-archive");
        startActivity(intent);
    }
}
