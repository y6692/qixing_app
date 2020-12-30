package cn.qimate.bike.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
        ImageView order_state = BaseViewHolder.get(convertView,R.id.item_order_state);
        BillBean bean = getDatas().get(position);

        Log.e("BillAdapter===", bean.getOrder_type()+"==="+bean.getCar_type()+"==="+bean.getOrder_amount());

//        订单类型 1骑行订单 2套餐卡订单 3充值订单 4认证充值订单 5调度费订单 6赔偿费订单
        int orderType = bean.getOrder_type();
//        1骑行订单 2套餐卡订单 3充值订单 4认证充值订单 5调度费订单 6赔偿费订单 7充值+认证充值订单 8调度费+赔偿费订单
        order_type.setText(orderType==1?"骑行订单":orderType==2?"套餐卡订单":orderType==3?"充值订单":orderType==4?"认证充值订单":orderType==5?"调度费订单":orderType==6?"赔偿费订单":orderType==7?"充值+认证充值订单":"调度费+赔偿费订单");
        order_sn.setText(bean.getOrder_sn());
        payment_time.setText(bean.getPayment_time());
        payment_name.setText(bean.getPayment_name());
        order_amount.setText(bean.getOrder_amount());

        if(bean.getOrder_state()==40 && bean.getIs_over_area()==1){
            order_state.setVisibility(View.VISIBLE);
        }else{
            order_state.setVisibility(View.GONE);
        }

        return convertView;
    }
}
