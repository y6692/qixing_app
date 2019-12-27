package cn.qimate.bike.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.qimate.bike.R;
import cn.qimate.bike.base.BaseViewAdapter;
import cn.qimate.bike.base.BaseViewHolder;
import cn.qimate.bike.core.widget.MLImageView;
import cn.qimate.bike.model.HistoryRoadBean;

/**
 * Created by Administrator1 on 2017/2/13.
 */

public class HistoryRoadAdapter extends BaseViewAdapter<HistoryRoadBean> {

    private LayoutInflater inflater;

    public HistoryRoadAdapter(Context context) {
        super(context);
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.item_history_road, null);
        }
        View line = BaseViewHolder.get(convertView,R.id.item_historyRoad_line);
        MLImageView header = BaseViewHolder.get(convertView,R.id.item_historyRoad_header);
        TextView money = BaseViewHolder.get(convertView,R.id.item_historyRoad_money);
        TextView bikeCode = BaseViewHolder.get(convertView,R.id.item_historyRoad_bikeCode);
        TextView time = BaseViewHolder.get(convertView,R.id.item_historyRoad_time);
        HistoryRoadBean bean = getDatas().get(position);
        if (position == getDatas().size() -1){
            line.setVisibility(View.GONE);
        }else {
            line.setVisibility(View.VISIBLE);
        }
//        header.setImageResource(R.drawable.his_road_icon);
        money.setText("¥"+bean.getPrices());
        bikeCode.setText(bean.getCodenum());
        time.setText(bean.getSt_time());
        return convertView;
    }
}
