package com.ytglogistics.www.ytglogistics.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ytglogistics.www.ytglogistics.MyApplication;
import com.ytglogistics.www.ytglogistics.R;
import com.ytglogistics.www.ytglogistics.adapter.FunInAdapter;
import com.ytglogistics.www.ytglogistics.adapter.FunInMaxListAdapter;
import com.ytglogistics.www.ytglogistics.api.Api;
import com.ytglogistics.www.ytglogistics.been.AppInMax;
import com.ytglogistics.www.ytglogistics.been.AppInResult;
import com.ytglogistics.www.ytglogistics.been.DataCbm;
import com.ytglogistics.www.ytglogistics.been.PrintInfo;
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

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/12/9.
 */

public class FunInMaxListActivity extends FatherActivity {
    @BindView(R.id.lv_data)
    ListView lvData;
    private FunInMaxListAdapter adapter;
    private AppInResult result;
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
    EditText tvNum;
    @BindView(R.id.tv_pcs)
    EditText tvPcs;
    @BindView(R.id.tv_geshu)
    TextView tvGeshu;
    @BindView(R.id.tv_cangwei)
    EditText tvCangwei;
    @BindView(R.id.tv_one_weight)
    TextView tvOneWeight;
    @BindView(R.id.tv_weight)
    TextView tvWeight;
    @BindView(R.id.tv_length)
    EditText tvLength;
    @BindView(R.id.tv_one_wigth)
    EditText tvOneWigth;
    @BindView(R.id.tv_one_height)
    EditText tvOneHeight;
    @BindView(R.id.tv_cbm)
    TextView tvCbm;
    @BindView(R.id.tv_bkcbm)
    TextView tvBkcbm;
    @BindView(R.id.tv_cbmrate)
    TextView tvCbmrate;
    @BindView(R.id.tv_xiangbang)
    EditText tvXiangbang;
    private InputDialog dialog;
    private AppInMax inMax;
    private ArrayList<DataCbm> lsit = new ArrayList<DataCbm>();
    private int selectPosition = -1;
    private ArrayList<AppInMax> appInMaxes;
    private DecimalFormat df;

    @Override
    protected int getLayoutId() {
        return R.layout.act_funinmaxlist;
    }

    @Override
    protected void initValues() {
        context = (MyApplication) getApplicationContext();
        initDefautHead("入仓修改", true);
        result = JSON.parseObject(getIntent().getStringExtra(Consts.KEY_DATA), AppInResult.class);
    }


    @Override
    protected void initView() {
        adapter = new FunInMaxListAdapter(this);
        lvData.setAdapter(adapter);
        df = new DecimalFormat("######0.00");

        lvData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectPosition = position;
                inMax = adapter.getData().get(position);
                tvPo.setText(inMax.Po);
                tvSkn.setText(inMax.Skn);
                tvNum.setText((int) inMax.Soquan + "");
                tvPcs.setText(inMax.Format + "");
                tvGeshu.setText((int) inMax.Ttlpcs + "");
                tvCangwei.setText(inMax.Loca + "");
                tvOneWeight.setText(inMax.Unitwei + "");
                tvWeight.setText(df.format((inMax.Rweight)) + "");
                tvLength.setText(inMax.Leng + "");
                tvOneWigth.setText(inMax.Wide + "");
                tvOneHeight.setText(inMax.High + "");
                tvCbm.setText(df.format((inMax.Cbm)) + "");
                tvBkcbm.setText(inMax.BookingCbm + "");

                tvCbmrate.setText(df.format((inMax.CbmRate * 100)) + "%");
                tvXiangbang.setText(inMax.PaperCtn + "");
                adapter.setPos(position);
            }
        });
        tvCangwei.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    if (selectPosition == -1) {
                        WWToast.showShort("请先选择一条记录");
                    } else {
                        adapter.getData().get(selectPosition).Loca = s + "";
                    }

                }

            }
        });
        tvLength.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    if (selectPosition == -1) {
                        WWToast.showShort("请先选择一条记录");
                    } else {
                        adapter.getData().get(selectPosition).Leng = Double.valueOf(s + "");
                        setCbm(adapter.getData().get(selectPosition));
                    }

                }

            }
        });
        tvOneWigth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    if (selectPosition == -1) {
                        WWToast.showShort("请先选择一条记录");
                    } else {
                        adapter.getData().get(selectPosition).Wide = Double.valueOf(s + "");
                        setCbm(adapter.getData().get(selectPosition));
                    }

                }

            }
        });
        tvOneHeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    if (selectPosition == -1) {
                        WWToast.showShort("请先选择一条记录");
                    } else {
                        adapter.getData().get(selectPosition).High = Double.valueOf(s + "");
                        setCbm(adapter.getData().get(selectPosition));
                    }

                }

            }
        });

        tvNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    if (selectPosition == -1) {
                        WWToast.showShort("请先选择一条记录");
                    } else {
                        adapter.getData().get(selectPosition).Soquan = Integer.valueOf(s + "");
                        setAllWeight(adapter.getData().get(selectPosition).Soquan);
                        setCbm(adapter.getData().get(selectPosition));
                    }

                }

            }
        });
        tvPcs.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    if (selectPosition == -1) {
                        WWToast.showShort("请先选择一条记录");
                    } else {
                        adapter.getData().get(selectPosition).Format = Double.valueOf(s + "");
                    }

                }

            }
        });
        tvXiangbang.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    if (selectPosition == -1) {
                        WWToast.showShort("请先选择一条记录");
                    } else {
                        adapter.getData().get(selectPosition).PaperCtn = Integer.valueOf(s + "");
                    }

                }

            }
        });


    }

    private void setCbm(AppInMax appInMax) {

        appInMax.Cbm = appInMax.Leng * appInMax.Wide * appInMax.High * appInMax.Soquan * 0.001 * 0.001;
        BigDecimal b = new BigDecimal(appInMax.Cbm);
        appInMax.Cbm = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        inMax.Cbm = appInMax.Cbm;
        tvCbm.setText(df.format((inMax.Cbm)) + "");
        appInMax.CbmRate = (appInMax.Cbm - appInMax.BookingCbm) / appInMax.BookingCbm;
        BigDecimal c = new BigDecimal( appInMax.CbmRate);
        appInMax.CbmRate = c.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        inMax.CbmRate = appInMax.CbmRate;
        tvCbmrate.setText(df.format((inMax.CbmRate * 100)) + "%");
    }

    private void setAllWeight(int soquan) {
        adapter.getData().get(selectPosition).Rweight = adapter.getData().get(selectPosition).Unitwei * soquan;
        BigDecimal b = new BigDecimal(adapter.getData().get(selectPosition).Rweight);
        adapter.getData().get(selectPosition).Rweight = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

        inMax.Rweight = adapter.getData().get(selectPosition).Rweight;
        tvWeight.setText(df.format((inMax.Rweight)) + "");
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
        int sta = context.getObject().CON_QueryStatus2(context.getState(), 2);
        switch (sta) {
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
        context.getObject().CPCL_PageStart(context.getState(), 504, 680, 0, 1);
        context.getObject().CPCL_SetBold(context.getState(), true);
        context.getObject().CPCL_AlignType(context.getState(), preDefiniation.AlignType.AT_CENTER.getValue());
        context.getObject().CPCL_Print1DBarcode(context.getState(), preDefiniation.BarcodeType.BT_CODE128.getValue(), 0, 40, 4, 3, 250, info.Palletid, "gb2312");
        context.getObject().CPCL_PrintString(context.getState(), 0, 310, 1, 1, 0, 24, info.Palletid, "gb2312");
        context.getObject().CPCL_PrintString(context.getState(), 10, 380, 1, 1, 0, 24, "DATE: " + getPrintTime(info.CreateTime), "gb2312");
        context.getObject().CPCL_PrintString(context.getState(), 10, 440, 1, 1, 0, 24, "SO NO: " + info.Sono, "gb2312");
        context.getObject().CPCL_PrintString(context.getState(), 10, 500, 1, 1, 0, 24, "PO NO: " + ((info.Po == null) ? " " : info.Po), "gb2312");
        context.getObject().CPCL_PrintString(context.getState(), 10, 560, 1, 1, 0, 24, "ITEM NO: " + ((info.Skn == null) ? " " : info.Skn), "gb2312");
        context.getObject().CPCL_PrintString(context.getState(), 10, 610, 1, 1, 0, 24, "QTY: " + info.Pkgs + "/" + info.PaperCtn, "gb2312");
        context.getObject().CON_PageEnd(context.getState(),
                context.getPrintway());
    }

    private String getPrintTime(String timeInfo) {
        String s = timeInfo.replace("/Date(", "").replace(")/", "");
        long s1 = Long.parseLong(s.substring(0, s.indexOf("+")));
        return TimeUtil.getOnlyDateToS(s1);
    }

    @Override
    protected void doOperate() {
        getInMaxData();
    }

    private void getInMaxData() {
        showWaitDialog();
        RequestParams params = ParamsUtils.getSessionParams(Api.GetAppInMx());
        params.addBodyParameter("serial", result.Serial + "");
        params.addBodyParameter("queueNo", result.QueueNo);
        ZLog.showPost("queueNo" + result.QueueNo);
        x.http().get(params, new WWXCallBack("GetAppInMx") {
            @Override
            public void onAfterSuccessOk(JSONObject data) {
                JSONArray jsonArray = data.getJSONArray("Data");
                appInMaxes = (ArrayList<AppInMax>) JSON.parseArray(
                        jsonArray.toJSONString(), AppInMax.class);
                adapter.setData(appInMaxes);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 888) {
                ArrayList<DataCbm> lsit22 = (ArrayList<DataCbm>) JSON.parseArray(
                        data.getStringExtra(Consts.KEY_DATA), DataCbm.class);
                //把重复的去掉
                if (lsit22 != null && lsit22.size() > 0) {
                    for (int i = 0; i < lsit.size(); i++) {
                        if (lsit.get(i).InMxId == lsit22.get(0).InMxId) {
                            lsit.remove(i);
                        }
                    }
                }
                lsit.addAll(lsit22);
            }
        }
    }

    @OnClick({R.id.tv_lh_input, R.id.tv_conmit, R.id.tv_print})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_lh_input:
                if (inMax != null) {
                    Intent intent = new Intent(this, FuncInCbmActivity.class);
                    intent.putExtra(Consts.KEY_DATA, JSON.toJSONString(inMax));
                    intent.putExtra("Serial", result.Serial);
                    startActivityForResult(intent, 888);
                } else {
                    WWToast.showShort("请先选择一条数据");
                }
                break;
            case R.id.tv_conmit:
                allInfoCommit();
                break;
            case R.id.tv_print:
                if (inMax != null) {

                    if (mBconnect) {
                        getPrintData();
                    } else {
                        if (dialog == null) {
                            dialog = new InputDialog(FunInMaxListActivity.this, R.style.DialogStyle);
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

                } else {
                    WWToast.showShort("请先选择一条数据");
                }
                break;
        }
    }

    private void allInfoCommit() {
        showWaitDialog();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Consts.KEY_SESSIONID, MyApplication
                .getInstance().getSessionId());
        JSONArray array = new JSONArray();
        for (int i = 0; i < adapter.getData().size(); i++) {
            if ((adapter.getData().get(i).CbmRate > 0.05) || (adapter.getData().get(i).CbmRate < -0.05)) {
                WWToast.showShort(adapter.getData().get(i).Skn + " CbmRate大于5%，需要复尺.");
            }
            array.add(adapter.getData().get(i).toJson());
        }
        jsonObject.put("objs", array);
        x.http().post(ParamsUtils.getPostJsonParams(jsonObject, Api.AppInMxCommit()), new WWXCallBack("AppInMxCommit") {
            @Override
            public void onAfterSuccessOk(JSONObject data) {
                JSONArray jsonArray = data.getJSONArray("Data");
                ArrayList<Long> rowIdList = (ArrayList<Long>) JSON.parseArray(
                        jsonArray.toJSONString(), Long.class);
                if (rowIdList.size() > 0) {
                    //更新DataCbm
                    for (int i = 0; i < lsit.size(); i++) {
                        for (int j = 0; j < adapter.getData().size(); j++) {
                            if (lsit.get(i).InMxId == adapter.getData().get(j).RowId) {
                                lsit.get(i).InMxId = rowIdList.get(j);
                            }
                        }
                    }
                    //更新AppInMax
                    for (int j = 0; j < adapter.getData().size(); j++) {
                        adapter.getData().get(j).RowId = rowIdList.get(j);
                    }
                }
                if (lsit != null && lsit.size() > 0) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(Consts.KEY_SESSIONID, MyApplication
                            .getInstance().getSessionId());
                    JSONArray array = new JSONArray();
                    for (int i = 0; i < lsit.size(); i++) {
                        array.add(lsit.get(i).toJson());
                    }
                    jsonObject.put("objs", array);
                    x.http().post(ParamsUtils.getPostJsonParams(jsonObject, Api.AppMxCbmCommit()), new WWXCallBack("AppMxCbmCommit") {
                        @Override
                        public void onAfterSuccessOk(JSONObject data) {
                            WWToast.showShort("提交成功");
                        }

                        @Override
                        public void onAfterFinished() {

                        }
                    });
                }

            }

            @Override
            public void onAfterFinished() {
                dismissWaitDialog();
            }
        });
    }

    private void getPrintData() {
        showWaitDialog();
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
                dismissWaitDialog();
            }
        });
    }
}
