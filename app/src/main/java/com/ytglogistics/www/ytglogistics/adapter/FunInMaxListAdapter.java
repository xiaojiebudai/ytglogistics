package com.ytglogistics.www.ytglogistics.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ytglogistics.www.ytglogistics.R;
import com.ytglogistics.www.ytglogistics.been.AppInMax;

/**
 * Created by Administrator on 2016/12/9.
 */

public class FunInMaxListAdapter extends SuperBaseAdapter<AppInMax> {
    public FunInMaxListAdapter(Context ctx) {
        super(ctx);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(mContext,
                    R.layout.list_funinmax_item, null);
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

        public ViewHolder(View convertView) {
            tv_so = (TextView) convertView.findViewById(R.id.tv_so);
            tv_po = (TextView) convertView.findViewById(R.id.tv_po);
            tv_skn = (TextView) convertView.findViewById(R.id.tv_skn);
            convertView.setTag(this);
        }

        public void setData(int pos) {
            AppInMax item = getItem(pos);
            tv_so.setText(item.So);
            tv_po.setText(item.Po);
            tv_skn.setText(item.Skn);
        }

    }
}
