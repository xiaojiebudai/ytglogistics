package com.ytglogistics.www.ytglogistics.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ytglogistics.www.ytglogistics.R;
import com.ytglogistics.www.ytglogistics.been.ZxDzl;

/**
 * Created by zxj on 2017/4/12.
 *
 * @description
 */



    public class ZxDzlListSelectAdapter extends SuperBaseAdapter<ZxDzl> {
        private int selectPostion = 0;
        public ZxDzlListSelectAdapter(Context ctx) {
            super(ctx);
        }
        public void setSelectPostion(int position) {
            this.selectPostion = position;
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
           ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.list_text_item, null);
                viewHolder = new ViewHolder(convertView);
            } else
                viewHolder = (ViewHolder) convertView.getTag();
            if (position == selectPostion) {
                convertView.setBackgroundColor(mContext.getResources()
                        .getColor(R.color.top_title_bg));
            } else {
                convertView.setBackgroundColor(mContext.getResources()
                        .getColor(R.color.white));
            }
            viewHolder.setData(position);
            return convertView;
        }

        public class ViewHolder {
            public TextView tv_info;


            public ViewHolder(View convertView) {
                tv_info = (TextView) convertView.findViewById(R.id.tv_info);
                convertView.setTag(this);
            }

            public void setData(int Position) {
                ZxDzl item = getItem(Position);
                tv_info.setText(item.Bh);
            }

        }

    }
