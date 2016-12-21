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
    public FunInAdapter(Context ctx) {
        super(ctx);
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

    public class ViewHolder {
        public TextView tv_name;
        public TextView tv_num;
        public TextView tv_bowei;
        public TextView tv_state;

        public ViewHolder(View convertView) {
            tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            tv_num = (TextView) convertView.findViewById(R.id.tv_num);
            tv_bowei = (TextView) convertView.findViewById(R.id.tv_bowei);
            tv_state = (TextView) convertView.findViewById(R.id.tv_state);
            convertView.setTag(this);
        }

        public void setData(int pos) {
            Car item = getItem(pos);
      tv_num.setText(pos+"");
            tv_name.setText(item.CarNo);
            tv_bowei.setText(item.PlaceId);
            tv_state.setText(item.CarNo);
            switch (item.Status) {
                case 0:
                    tv_state.setText( "已取号");
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
//            tv_num.setText("¥" + item.ChangeAmt);
//            tv_mark.setText(item.Explain);
            // 状态待处理
            // tv_state.setText();
            // tv_mark.setText(item.Explain);

        }

    }

}
