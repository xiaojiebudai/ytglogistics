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
import com.ytglogistics.www.ytglogistics.dialog.SelectBTDialog;
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

        View right = findViewById(R.id.rl_head_right);
        final TextView text = (TextView) findViewById(R.id.tv_head_right);
        text.setText(SharedPreferenceUtils.getInstance().getIsWifi() ? R.string.wifi : R.string.bluetooth);
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferenceUtils.getInstance().saveIsWifi(!SharedPreferenceUtils.getInstance().getIsWifi());
                if (context.getState() > 0) {
                    context.getObject().CON_CloseDevices(context.getState());
                }
                text.setText(SharedPreferenceUtils.getInstance().getIsWifi() ? R.string.wifi : R.string.bluetooth);
                context.setState(0);
            }
        });
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
                    if (context.getState() > 0) {
                        printLabel(item);
                    } else {
                        if (SharedPreferenceUtils.getInstance().getIsWifi()) {
                            //wifi链接
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
                        } else {
                            //蓝牙链接
                            SelectBTDialog selectBTDialog = new SelectBTDialog(BoardHeadPaperPrintActivity.this, context, new SelectBTDialog.OnSelectOk() {
                                @Override
                                public void seleckOk(String s) {
                                    connectBt(s);
                                }
                            });
                            selectBTDialog.show();
                        }
                    }

                } else {
                    WWToast.showShort("请先选择一条数据");
                }
            }
        });
        commonDialogTwiceConfirm.show();
    }

    private void connectBt(String s) {
        //链接之前需要先断开一下
        if (context.getState() > 0) {
            context.getObject().CON_CloseDevices(context.getState());
        }
        state = context.getObject().CON_ConnectDevices("RG-MLP80A", s, 200);
        if (state > 0) {
            WWToast.showShort("链接成功");
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

    public MyApplication context;
    public boolean mBconnect = false;
    public int state;

    public void connect(String IP) {
        //链接之前需要先断开一下
        if (context.getState() > 0) {
            context.getObject().CON_CloseDevices(context.getState());
        }

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

    //打印数据
    private void printLabel(PrintInfo info) {
        if (info == null) {
            WWToast.showShort("数据有误，请重新查询！");
            return;
        }
        //打印中文
        context.getObject().CON_PageStart(context.getState(), true,
                504, 800);
        context.getObject().DRAW_SetFillMode(false);
        context.getObject().CPCL_SetBold(context.getState(), true);
        context.getObject().CPCL_AlignType(context.getState(), preDefiniation.AlignType.AT_CENTER.getValue());
        context.getObject().DRAW_Print1D2DBarcode(context.getState(), preDefiniation.BarcodeType.BT_CODE128.getValue(), 0, 60, 4, 250, info.Palletid);
        context.getObject().DRAW_PrintText(context.getState(), 0, 320, info.Palletid, 24);
        context.getObject().DRAW_PrintText(context.getState(), 10, 400, "DATE: " + getPrintTime(info.CreateTime), 24);
        context.getObject().DRAW_PrintText(context.getState(), 10, 460, "SO NO: " + info.Sono, 24);
        context.getObject().DRAW_PrintText(context.getState(), 10, 520, "PO NO: " + ((info.Po == null) ? " " : info.Po), 24);
        context.getObject().DRAW_PrintText(context.getState(), 10, 580, "ITEM NO: " + ((info.Skn == null) ? " " : info.Skn), 24);
        /**
         * 理货员名字+“/”+叉车人编号
         * 如：张三/101
         */
        context.getObject().DRAW_PrintText(context.getState(), 10, 640, "QTY: " + info.Pkgs + "/" + info.PalletCtn, 24);
        context.getObject().DRAW_PrintText(context.getState(), 10,
                700, "操作员: " + ((TextUtils.isEmpty(info.UserName)) ? "" : info.UserName) + "/" + ((TextUtils.isEmpty(info.ZxdzlNo)) ? "" : info.ZxdzlNo), 24);

        context.getObject().DRAW_PrintLine(context.getState(), 0, 760,
                504, 3);


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
