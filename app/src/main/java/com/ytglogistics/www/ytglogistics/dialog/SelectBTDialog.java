package com.ytglogistics.www.ytglogistics.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;

import com.github.library.BaseRecyclerAdapter;
import com.github.library.BaseViewHolder;
import com.github.library.listener.OnRecyclerItemClickListener;
import com.ytglogistics.www.ytglogistics.MyApplication;
import com.ytglogistics.www.ytglogistics.R;

import java.util.ArrayList;

/**
 * Created by ZXJ on 2017/4/24.
 */

public class SelectBTDialog extends Dialog {
    private BaseRecyclerAdapter mAdapter;
    RecyclerView lvData;
    private Context context;
    private MyApplication application;
    ArrayList<String> getbtName = new ArrayList<String>();
    ArrayList<String> getbtNM = new ArrayList<String>();
    ArrayList<String> getbtMax = new ArrayList<String>();
    private OnSelectOk onSelectOk;
    public SelectBTDialog(Context context,MyApplication application,OnSelectOk onSelectOk) {
        super(context, R.style.DialogStyle);
        setContentView(R.layout.dialog_select_bt);
        this.context=context;
        this.application=application;
        this.onSelectOk=onSelectOk;
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.8);
        getWindow().setAttributes(attributes);
        initView();
    }
    private void initView() {
        getbtName.clear();

        getbtNM = (ArrayList<String>)application.getObject()
                .CON_GetWirelessDevices(0);
        for (int i = 0; i < getbtNM.size(); i++) {
            getbtName.add(getbtNM.get(i).split(",")[0]+"\n"+getbtNM.get(i).split(",")[1].substring(0,
                    17));
            getbtMax.add(getbtNM.get(i).split(",")[1].substring(0,
                    17));
        }
        lvData= (RecyclerView) findViewById(R.id.lv_data);
        mAdapter = new BaseRecyclerAdapter<String>(context, getbtName, R.layout.list_text_item) {
            @Override
            protected void convert(BaseViewHolder helper, String item) {
                helper.setText(R.id.tv_info,item);
            }
        };
        mAdapter.setOnRecyclerItemClickListener(new OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                onSelectOk.seleckOk(getbtMax.get(position));
                dismiss();
            }
        });
        lvData.setHasFixedSize(true);
        lvData.setLayoutManager(new LinearLayoutManager(context));
        lvData.setItemAnimator(new DefaultItemAnimator());
        mAdapter.openLoadAnimation(false);
        lvData.setAdapter(mAdapter);

    }
    public interface OnSelectOk{
        void seleckOk(String s);
    }

}
