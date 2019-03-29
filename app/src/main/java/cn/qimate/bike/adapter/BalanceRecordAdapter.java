package cn.qimate.bike.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.qimate.bike.R;
import cn.qimate.bike.base.BaseViewAdapter;
import cn.qimate.bike.base.BaseViewHolder;
import cn.qimate.bike.model.HistoryRoadBean;
import cn.qimate.bike.model.MyIntegralRecordBean;

/**
 * Created by Administrator1 on 2017/2/14.
 */

public class BalanceRecordAdapter extends BaseViewAdapter<MyIntegralRecordBean> {

    private LayoutInflater inflater;

    public BalanceRecordAdapter(Context context) {
        super(context);
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.item_balance_record, null);
        }
        TextView time = BaseViewHolder.get(convertView,R.id.item_myIntegral_rule_time);
        TextView times = BaseViewHolder.get(convertView,R.id.item_myIntegral_rule_times);
        MyIntegralRecordBean bean = getDatas().get(position);
        time.setText(bean.getAddtime());
        times.setText(bean.getReason());

        View line = BaseViewHolder.get(convertView,R.id.item_balanceRecord_line);
        if (position == 0){
            line.setVisibility(View.GONE);
        }else {
            line.setVisibility(View.VISIBLE);
        }


        return convertView;
    }
}
