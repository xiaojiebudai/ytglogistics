package com.ytglogistics.www.ytglogistics.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ytglogistics.www.ytglogistics.R;
import com.ytglogistics.www.ytglogistics.been.Car;
import com.ytglogistics.www.ytglogistics.utils.DensityUtil;

/**
 * Created by Administrator on 2016/12/8.
 */

public class FunInAdapter extends SuperBaseAdapter<Car> {
    public static final int FUNIN = 0;
    public static final int FUNOUT = 1;
    private int model = 0;

    public FunInAdapter(Context ctx, int model) {
        super(ctx);
        this.model = model;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(mContext,
                    R.layout.fun_in_list, null);
            viewHolder = new ViewHolder(convertView);
        } else
            viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.setData(position);
        return convertView;
    }

    private int posSelect = -1;

    public void setPos(int pos) {
        this.posSelect = pos;
        notifyDataSetChanged();
    }

    public class ViewHolder {
        public TextView tv_name;
        public TextView tv_num;
        public TextView tv_bowei;
        public TextView tv_state;
        public LinearLayout ll_container;

        public ViewHolder(View convertView) {
            tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            tv_num = (TextView) convertView.findViewById(R.id.tv_num);
            tv_bowei = (TextView) convertView.findViewById(R.id.tv_bowei);
            tv_state = (TextView) convertView.findViewById(R.id.tv_state);
            ll_container = (LinearLayout) convertView.findViewById(R.id.ll_container);
            convertView.setTag(this);
        }

        public void setData(int pos) {
            Car item = getItem(pos);
            tv_num.setText(pos + 1 + "");
            if (posSelect == pos) {
                ll_container.setBackgroundResource(R.color.top_title_bg);
            } else {
                ll_container.setBackgroundResource(R.color.white);
            }
            if (model == FUNIN) {
                tv_name.setText(item.CarNo);
                tv_bowei.setText(item.PlaceId);
                switch (item.Status) {
                    case 0:
                        tv_state.setText("已取号");
                        break;
                    case 1:
                        tv_state.setText("已分配");
                        break;
                    case 2:
                        tv_state.setText("已完成");
                        break;
                    default:
                        tv_state.setText("未知");
                }
            } else {
                tv_name.setText(item.So);
                tv_bowei.setText(item.CabinetNo);
                tv_state.setText(item.PlaceId);
            }
        }

    }

}
