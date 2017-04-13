package com.ytglogistics.www.ytglogistics.activity;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.library.BaseRecyclerAdapter;
import com.github.library.BaseViewHolder;
import com.github.library.listener.OnRecyclerItemClickListener;
import com.ytglogistics.www.ytglogistics.MyApplication;
import com.ytglogistics.www.ytglogistics.R;
import com.ytglogistics.www.ytglogistics.api.Api;
import com.ytglogistics.www.ytglogistics.been.PrintInfo;
import com.ytglogistics.www.ytglogistics.dialog.CommonDialog;
import com.ytglogistics.www.ytglogistics.dialog.InputDialog;
import com.ytglogistics.www.ytglogistics.utils.DialogUtils;
import com.ytglogistics.www.ytglogistics.utils.ParamsUtils;
import com.ytglogistics.www.ytglogistics.utils.RegexUtil;
import com.ytglogistics.www.ytglogistics.utils.SharedPreferenceUtils;
import com.ytglogistics.www.ytglogistics.utils.TimeUtil;
import com.ytglogistics.www.ytglogistics.utils.WWToast;
import com.ytglogistics.www.ytglogistics.utils.preDefiniation;
import com.ytglogistics.www.ytglogistics.xutils.WWXCallBack;

import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zxj on 2017/4/11.
 *
 * @description 板头纸打印
 */

public class BoardHeadPaperPrintActivity extends FatherActivity {
    @BindView(R.id.et_no)
    EditText etNo;
    @BindView(R.id.tv_search)
    TextView tvSearch;
    @BindView(R.id.lv_data)
    RecyclerView lvData;
    private ArrayList<PrintInfo> poSearches;
    private BaseRecyclerAdapter mAdapter;
    private InputDialog dialog;

    @Override
    protected int getLayoutId() {
        return R.layout.act_boardheadpager_print;
    }

    @Override
    protected void initValues() {
        context = (MyApplication) getApplicationContext();
        initDefautHead("板头纸打印", true);
    }

    @Override
    protected void initView() {
        lvData.setBackgroundResource(R.drawable.bg_yellow_white_shape);
        mAdapter = new BaseRecyclerAdapter<PrintInfo>(this, poSearches, R.layout.list_boardheadpager) {
            @Override
            protected void convert(BaseViewHolder helper, PrintInfo item) {
                helper.setText(R.id.tv_so, item.Sono);

                helper.setText(R.id.tv_po, item.Po);
                helper.setText(R.id.tv_skn, item.Skn);
                helper.setText(R.id.tv_pid, item.Palletid);

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
                for (int i = 0; i < mAdapter.getData().size(); i++) {
                    ((PrintInfo) mAdapter.getItem(i)).isSelect = false;
                }
                ((PrintInfo) mAdapter.getItem(position)).isSelect = true;

                mAdapter.notifyDataSetChanged();
                //去打印
                showPrintTips((PrintInfo) mAdapter.getItem(position));
            }
        });
        lvData.setHasFixedSize(true);
        lvData.setLayoutManager(new LinearLayoutManager(this));
        lvData.setItemAnimator(new DefaultItemAnimator());
        mAdapter.openLoadAnimation(false);
        lvData.setAdapter(mAdapter);
    }

    private PrintInfo item;

    //打印相关代码
    private void showPrintTips(final PrintInfo item) {
        this.item = item;
        final CommonDialog commonDialogTwiceConfirm = DialogUtils.getCommonDialogTwiceConfirm(BoardHeadPaperPrintActivity.this, "确认打印" + item.Palletid + "！", true);
        commonDialogTwiceConfirm.setRightButtonCilck(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commonDialogTwiceConfirm.dismiss();
                if (item != null) {
                    QueryStatus();
                    if (mBconnect) {
                        printLabel(item);

                    } else {
                        if (dialog == null) {
                            dialog = new InputDialog(BoardHeadPaperPrintActivity.this, R.style.DialogStyle);
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
            }
        });
        commonDialogTwiceConfirm.show();
    }

    public MyApplication context;
    public boolean mBconnect = false;
    public int state;

    public void connect(String IP) {
        if (mBconnect) {
            //已经连接  直接打印
            printLabel(item);
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
                printLabel(item);
            } else {
                WWToast.showShort("链接失败");
                mBconnect = false;
            }
        }
    }

    private void QueryStatus() {
        //2:(0 状态正常，1 网络错误，2打印机缺纸，3脱机.4不存在打印对象未连接。5复位错误.6卡纸)
        int sta = context.getObject().CON_QueryStatus2(context.getState(), 2);
        mBconnect = false;
        switch (sta) {
            case 0:
//                WWToast.showShort("状态正常");
                mBconnect = true;
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
                WWToast.showShort("打印对象未连接");
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
        if (info == null) {
            WWToast.showShort("数据有误，请重新查询！");
            return;
        }

        //打印中文
        if (context.getName().equalsIgnoreCase("RG-MLP80A")) {
            context.getObject().CON_PageStart(context.getState(), false,
                    504,
                    720);
        }
        context.getObject().CON_PageStart(context.getState(), true,
                504, 720);
        context.getObject().DRAW_SetFillMode(false);
        context.getObject().CPCL_SetBold(context.getState(), true);
        context.getObject().CPCL_AlignType(context.getState(), preDefiniation.AlignType.AT_CENTER.getValue());
        context.getObject().DRAW_Print1D2DBarcode(context.getState(), preDefiniation.BarcodeType.BT_CODE128.getValue(), 0, 40, 4, 250, info.Palletid);
        context.getObject().DRAW_PrintText(context.getState(), 0, 310, info.Palletid, 24);
        context.getObject().DRAW_PrintText(context.getState(), 10, 380, "DATE: " + getPrintTime(info.CreateTime), 24);
        context.getObject().DRAW_PrintText(context.getState(), 10, 440, "SO NO: " + info.Sono, 24);
        context.getObject().DRAW_PrintText(context.getState(), 10, 500, "PO NO: " + ((info.Po == null) ? " " : info.Po), 24);
        context.getObject().DRAW_PrintText(context.getState(), 10, 560, "ITEM NO: " + ((info.Skn == null) ? " " : info.Skn), 24);
        /**
         * 理货员名字+“/”+叉车人编号
         * 如：张三/101
         */
        context.getObject().DRAW_PrintText(context.getState(), 10, 620, "QTY: " + info.Pkgs + "/" + info.PalletCtn, 24);
        context.getObject().DRAW_PrintText(context.getState(), 10,
                680, "操作员: " + ((TextUtils.isEmpty(info.UserName)) ? "" : info.UserName) + "/" + ((TextUtils.isEmpty(info.ZxdzlNo)) ? "" : info.ZxdzlNo), 24);
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

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }


    @OnClick(R.id.tv_search)
    public void onViewClicked() {
        String so = etNo.getText().toString().trim();
        if (TextUtils.isEmpty(so)) {
            WWToast.showShort("请输入SO查询");
            return;
        }
        showWaitDialog();
        RequestParams params = ParamsUtils.getSessionParams(Api.PdaPalletBySo());
        params.addBodyParameter("so", so);
        x.http().get(params, new WWXCallBack("PdaPalletBySO") {
            @Override
            public void onAfterSuccessOk(JSONObject data) {
                JSONArray jsonArray = data.getJSONArray("Data");
                poSearches = (ArrayList<PrintInfo>) JSONObject.parseArray(
                        jsonArray.toJSONString(), PrintInfo.class);
                mAdapter.setData(poSearches);
            }

            @Override
            public void onAfterFinished() {
                dismissWaitDialog();
            }
        });
    }
}
