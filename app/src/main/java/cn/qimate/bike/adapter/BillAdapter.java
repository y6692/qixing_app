package cn.qimate.bike.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.qimate.bike.R;
import cn.qimate.bike.base.BaseViewAdapter;
import cn.qimate.bike.base.BaseViewHolder;
import cn.qimate.bike.model.BillBean;
import cn.qimate.bike.model.MyMessageBean;

/**
 * Created by Administrator1 on 2017/2/14.
 */

public class BillAdapter extends BaseViewAdapter<BillBean>{

    private LayoutInflater inflater;

    public BillAdapter(Context context) {
        super(context);
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.item_bill, null);
        }
        TextView order_type = BaseViewHolder.get(convertView,R.id.item_order_type);
        TextView order_sn = BaseViewHolder.get(convertView,R.id.item_order_sn);
        TextView payment_time = BaseViewHolder.get(convertView,R.id.item_payment_time);
        TextView payment_name = BaseViewHolder.get(convertView,R.id.item_payment_name);
        TextView order_amount = BaseViewHolder.get(convertView,R.id.item_order_amount);
        BillBean bean = getDatas().get(position);

        Log.e("BillAdapter===", bean.getOrder_type()+"==="+bean.getCar_type()+"==="+bean.getOrder_amount());

//        1骑行订单 2购买骑行卡订单 3调度费订单 4赔偿费订单 5充值订单
        int orderType = bean.getOrder_type();
        order_type.setText(orderType==1?"骑行":orderType==2?"骑行卡":orderType==3?"调度费":orderType==4?"赔偿费":"充值");
        order_sn.setText(bean.getOrder_sn());
        payment_time.setText(bean.getPayment_time());
        payment_name.setText(bean.getPayment_name());
        order_amount.setText(bean.getOrder_amount());

        return convertView;
    }
}
