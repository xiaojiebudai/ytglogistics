package com.ytglogistics.www.ytglogistics.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemProperties;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ToggleButton;

import com.ytglogistics.www.ytglogistics.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/2/9.
 */

public class BarCodeActivity extends FatherActivity implements View.OnClickListener {
    private EditText mReception;
    //接受广播
    private String RECE_DATA_ACTION = "com.se4500.onDecodeComplete";
    //调用扫描广播
    private String START_SCAN_ACTION = "com.geomobile.se4500barcode";

    private String STOP_SCAN="com.geomobile.se4500barcode.poweroff";

    private Button btnSingleScan, btnClear;
    private ToggleButton toggleButtonRepeat;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(android.content.Context context,
                              android.content.Intent intent) {
            String action = intent.getAction();
            if (action.equals(RECE_DATA_ACTION)) {
                String data = intent.getStringExtra("se4500");
                mReception.append(data+"\n");
                Intent intent2=new Intent();
                intent2.putExtra("codedContent", data);
                BarCodeActivity.this.setResult(RESULT_OK, intent2);
                finish();
                if (isRepeat) {
                    cancelRepeat();
                    repeatScan();
                }
            }
        }

    };

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.buttonclear:
                mReception.setText("");
                break;
            case R.id.buttonscan:
                startScan();
                break;
            default:
                break;
        }
    }

    public class MyTask extends TimerTask {
        @Override
        public void run() {
            startScan();
        }
    }
    private Timer timer = new Timer();
    @Override
    protected int getLayoutId() {
        return R.layout.act_barcode;
    }

    @Override
    protected void initValues() {
    initDefautHead("扫描条码",true);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void doOperate() {
        judgePropert();
        btnSingleScan = (Button) findViewById(R.id.buttonscan);
        btnClear = (Button) findViewById(R.id.buttonclear);
        toggleButtonRepeat = (ToggleButton) findViewById(R.id.button_repeat);
        btnSingleScan.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        // toggleButtonRepeat.setOnClickListener(this);
        toggleButtonRepeat
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        // TODO Auto-generated method stub
                        if (isChecked) {
                            isRepeat = true;
                            repeatScan();
                        } else {
                            isRepeat = false;
                            cancelRepeat();
                        }
                    }
                });

        mReception = (EditText) findViewById(R.id.EditTextReception);
        IntentFilter iFilter = new IntentFilter();
        //注册系统广播  接受扫描到的数据
        iFilter.addAction(RECE_DATA_ACTION);
        registerReceiver(receiver, iFilter);
    }
    /**
     * 判断快捷扫描是否勾选   不勾选跳转到系统设置中进行设置
     */
    private void judgePropert() {
        String result = SystemProperties.get("persist.sys.keyreport", "true");
        if (result.equals("false")) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.key_test_back_title)
                    .setMessage(R.string.action_dialog_setting_config)
                    .setPositiveButton(
                            R.string.action_dialog_setting_config_sure_go,
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // TODO Auto-generated method stub
                                    Intent intent = new Intent(
                                            Settings.ACTION_ACCESSIBILITY_SETTINGS);
                                    startActivityForResult(intent, 1);
                                }
                            })
                    .setNegativeButton(R.string.action_exit_cancel,
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // TODO Auto-generated method stub
                                    finish();
                                }

                            }

                    ).show();
        }
    }



    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        Intent intent =new Intent();
        intent.setAction("com.geomobile.se4500barcode.poweroff");
        sendBroadcast(intent);
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub

        super.onPause();
        if (isRepeat) {
            cancelRepeat();
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        if (isRepeat) {
            repeatScan();
        }
        super.onResume();
    }

    private boolean isRepeat = false;
    /**
     * 发送广播  调用系统扫描
     */
    private void startScan() {
        Intent intent = new Intent();
        intent.setAction(START_SCAN_ACTION);
        sendBroadcast(intent, null);
    }


    private void repeatScan() {
        if (timer == null) {
            timer = new Timer();
        }
        timer.scheduleAtFixedRate(new MyTask(), 100, 4 * 1000);
    }

    private void cancelRepeat() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    };
}
