package com.ytglogistics.www.ytglogistics.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ytglogistics.www.ytglogistics.R;
import com.ytglogistics.www.ytglogistics.been.AppInMax;

/**
 * Created by Administrator on 2016/12/9.
 */

public class FunInMaxListAdapter extends SuperBaseAdapter<AppInMax> {
    private int  posSelect=-1;
    public FunInMaxListAdapter(Context ctx) {
        super(ctx);
    }
public void setPos(int pos){
    this.posSelect=pos;
    notifyDataSetChanged();
}

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(mContext,
                    R.layout.list_funinmax_item_new, null);
            viewHolder = new ViewHolder(convertView);
        } else
            viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.setData(position);
        return convertView;
    }

    public class ViewHolder {
        public TextView tv_so;
        public TextView tv_po;
        public TextView tv_skn;
        public TextView tv_dbk;
        public TextView tv_dc;
        public LinearLayout ll_container;

        public ViewHolder(View convertView) {
            tv_so = (TextView) convertView.findViewById(R.id.tv_so);
            tv_po = (TextView) convertView.findViewById(R.id.tv_po);
            tv_skn = (TextView) convertView.findViewById(R.id.tv_skn);
            tv_dbk = (TextView) convertView.findViewById(R.id.tv_dbk);
            tv_dc = (TextView) convertView.findViewById(R.id.tv_dc);
            ll_container = (LinearLayout) convertView.findViewById(R.id.ll_container);
            convertView.setTag(this);
        }

        public void setData(int pos) {
            AppInMax item = getItem(pos);
            tv_so.setText(item.So);
            tv_po.setText(item.Po);
            tv_skn.setText(item.Skn);
            tv_dc.setText(item.Dc);
            tv_dbk.setText(item.Dbk);
            if(posSelect==pos){
                ll_container.setBackgroundResource(R.color.top_title_bg);
            }else{
                ll_container.setBackgroundResource(R.color.white);
            }
        }

    }
}
