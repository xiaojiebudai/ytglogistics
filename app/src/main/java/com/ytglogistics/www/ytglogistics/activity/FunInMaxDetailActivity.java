package com.ytglogistics.www.ytglogistics.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ytglogistics.www.ytglogistics.MyApplication;
import com.ytglogistics.www.ytglogistics.R;
import com.ytglogistics.www.ytglogistics.api.Api;
import com.ytglogistics.www.ytglogistics.been.AppInMax;
import com.ytglogistics.www.ytglogistics.been.DataCbm;
import com.ytglogistics.www.ytglogistics.been.PrintInfo;
import com.ytglogistics.www.ytglogistics.dialog.InputDialog;
import com.ytglogistics.www.ytglogistics.utils.Consts;
import com.ytglogistics.www.ytglogistics.utils.ParamsUtils;
import com.ytglogistics.www.ytglogistics.utils.RegexUtil;
import com.ytglogistics.www.ytglogistics.utils.SharedPreferenceUtils;
import com.ytglogistics.www.ytglogistics.utils.TimeUtil;
import com.ytglogistics.www.ytglogistics.utils.WWToast;
import com.ytglogistics.www.ytglogistics.utils.preDefiniation;
import com.ytglogistics.www.ytglogistics.xutils.WWXCallBack;

import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/12/9.
 */

public class FunInMaxDetailActivity extends FatherActivity {
    @BindView(R.id.tv_lh_input)
    TextView tvLhInput;
    @BindView(R.id.tv_conmit)
    TextView tvConmit;
    @BindView(R.id.tv_print)
    TextView tvPrint;
    @BindView(R.id.ll_operate)
    LinearLayout llOperate;
    @BindView(R.id.tv_po)
    TextView tvPo;
    @BindView(R.id.tv_skn)
    TextView tvSkn;
    @BindView(R.id.tv_num)
    TextView tvNum;
    @BindView(R.id.tv_pcs)
    TextView tvPcs;
    @BindView(R.id.tv_geshu)
    TextView tvGeshu;
    @BindView(R.id.tv_cangwei)
    TextView tvCangwei;
    @BindView(R.id.tv_one_weight)
    TextView tvOneWeight;
    @BindView(R.id.tv_weight)
    TextView tvWeight;
    @BindView(R.id.tv_length)
    TextView tvLength;
    @BindView(R.id.tv_one_wigth)
    TextView tvOneWigth;
    @BindView(R.id.tv_one_height)
    TextView tvOneHeight;
    @BindView(R.id.tv_cbm)
    TextView tvCbm;
    @BindView(R.id.tv_bkcbm)
    TextView tvBkcbm;
    @BindView(R.id.tv_cbmrate)
    TextView tvCbmrate;
    @BindView(R.id.tv_xiangbang)
    TextView tvXiangbang;
    private InputDialog dialog;
    private AppInMax inMax;
    private String Serial;
    private ArrayList<DataCbm> lsit = new ArrayList<DataCbm>();
    @Override
    protected int getLayoutId() {
        return R.layout.act_funinmaxdetail;
    }

    @Override
    protected void initValues() {
        initDefautHead("入仓修改", true);

        context = (MyApplication) getApplicationContext();
        inMax = JSON.parseObject(getIntent().getStringExtra(Consts.KEY_DATA), AppInMax.class);
        Serial=getIntent().getStringExtra("Serial");
    }

    @Override
    protected void initView() {
       tvPo.setText(inMax.Po);
       tvSkn.setText(inMax.Skn);
       tvNum.setText((int) inMax.Soquan+"");
       tvPcs.setText(inMax.Format+"");
       tvGeshu.setText((int)inMax.Ttlpcs+"");
       tvCangwei.setText(inMax.Loca+"");
       tvOneWeight.setText(inMax.Unitwei+"");
       tvWeight.setText(inMax.Rweight+"");
        tvLength.setText(inMax.Leng+"");
        tvOneWigth.setText(inMax.Wide+"");
       tvOneHeight.setText(inMax.High+"");
       tvCbm.setText(inMax.Cbm+"");
       tvBkcbm.setText(inMax.BookingCbm+"");
       tvCbmrate.setText((inMax.CbmRate*100)+"%");
        tvXiangbang.setText(inMax.PaperCtn+"");
    }

    private void getPrintData() {
        RequestParams params = ParamsUtils.getSessionParams(Api.PdaPallet());
        params.addBodyParameter("rowId", inMax.RowId + "");
        x.http().get(params, new WWXCallBack("PdaPallet") {
            @Override
            public void onAfterSuccessOk(JSONObject data) {
                //获取打印数据打印
                JSONArray jsonArray = data.getJSONArray("Data");
                ArrayList<PrintInfo> list = (ArrayList<PrintInfo>) JSON.parseArray(
                        jsonArray.toJSONString(), PrintInfo.class);
                if (list != null && list.size() > 0) {
                    for (int i = 0; i < list.size(); i++) {
                        printLabel(list.get(i));
                    }
                }

            }

            @Override
            public void onAfterFinished() {

            }
        });
    }

    public MyApplication context;
    public boolean mBconnect = false;
    public int state;

    public void connect(String IP) {
        if (mBconnect) {
            //已经连接  直接打印
            getPrintData();
        } else {
            //去链接
            state = context.getObject().CON_ConnectDevices("RG-MLP80A", IP + ":9100", 200);
            if (state > 0) {
                WWToast.showShort("链接成功");
                SharedPreferenceUtils.getInstance().saveIP(IP);
                mBconnect = true;
                context.setState(state);
                context.setName("RG-MLP80A");
                context.setPrintway(0);
                getPrintData();
            } else {
                WWToast.showShort("链接失败");
                mBconnect = false;
            }
        }
    }

    private void QueryStatus() {
        //2:(0 状态正常，1 网络错误，2打印机缺纸，3脱机.4不存在打印对象未连接。5复位错误.6卡纸)
      int sta=  context.getObject().CON_QueryStatus2(context.getState(),2);
        switch(sta){
            case 0:
                WWToast.showShort("状态正常");
                break;
            case 1:
                WWToast.showShort("网络错误");
                break;
            case 2:
                WWToast.showShort("打印机缺纸");
                break;
            case 3:
                WWToast.showShort("脱机");
                break;
            case 4:
                WWToast.showShort("不存在打印对象未连接");
                break;
            case 5:
                WWToast.showShort("复位错误");
                break;
            case 6:
                WWToast.showShort("卡纸");
                break;
        }
    }

    //打印数据
    private void printLabel(PrintInfo info) {
        context.getObject().CPCL_PageStart(context.getState(),504, 680, 0, 1);
        context.getObject().CPCL_SetBold(context.getState(),true);
        context.getObject().CPCL_AlignType(context.getState(), preDefiniation.AlignType.AT_CENTER.getValue());
        context.getObject().CPCL_Print1DBarcode(context.getState(), preDefiniation.BarcodeType.BT_CODEBAR.getValue(), 0, 40, 4, 3, 250, info.Palletid, "gb2312");
        context.getObject().CPCL_PrintString(context.getState(), 0, 310, 1, 1, 0, 24, info.Palletid, "gb2312");
        context.getObject().CPCL_PrintString(context.getState(), 10, 380, 1, 1, 0, 24, "DATE: "+getPrintTime(info.CreateTime), "gb2312");
        context.getObject().CPCL_PrintString(context.getState(), 10, 440, 1, 1, 0, 24, "SO NO: "+info.Sono, "gb2312");
        context.getObject().CPCL_PrintString(context.getState(), 10, 500, 1, 1, 0, 24, "PO NO: "+((info.Po==null)?" ":info.Po), "gb2312");
        context.getObject().CPCL_PrintString(context.getState(), 10, 560, 1,1, 0, 24, "ITEM NO: "+((info.Skn==null)?" ":info.Skn), "gb2312");
        context.getObject().CPCL_PrintString(context.getState(), 10, 610, 1, 1, 0, 24, "QTY: "+ info.Pkgs+"/"+info.PaperCtn, "gb2312");
        context.getObject().CON_PageEnd(context.getState(),
                context.getPrintway());

    }
private String getPrintTime(String timeInfo){
String s=timeInfo.replace("/Date(","").replace(")/","");
    long s1=Long.parseLong(s.substring(0,s.indexOf("+")));
//    long s2=  Long.parseLong(s.substring(s.indexOf("+")+1));
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode==888){
                lsit = (ArrayList<DataCbm>) JSON.parseArray(
                        data.getStringExtra(Consts.KEY_DATA), DataCbm.class);
            }
        }
    }

    @OnClick({R.id.tv_lh_input, R.id.tv_conmit, R.id.tv_print})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_lh_input:
                Intent intent=new Intent(this,FuncInCbmActivity.class);
                intent.putExtra(Consts.KEY_DATA,JSON.toJSONString(inMax));
                intent.putExtra("Serial",Serial);
                startActivityForResult(intent,888);
                break;
            case R.id.tv_conmit:


                break;
            case R.id.tv_print:
                if (mBconnect) {
                    getPrintData();
                } else {
                    if (dialog == null) {
                        dialog = new InputDialog(FunInMaxDetailActivity.this, R.style.DialogStyle);
                        dialog.getTv_cancel().setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        String ip = SharedPreferenceUtils.getInstance().getIP();
                        if (!TextUtils.isEmpty(ip)) {
                            dialog.setInput(ip);
                        }

                        dialog.getTv_ok().setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                if (!TextUtils.isEmpty(dialog.getInput()) && RegexUtil.isIpaddress(dialog.getInput())) {
                                    connect(dialog.getInput());
                                    dialog.dismiss();
                                } else {
                                    WWToast.showShort("请输入正确IP地址");
                                }

                            }
                        });

                    }
                    dialog.show();
                }
                break;
        }
    }
}
