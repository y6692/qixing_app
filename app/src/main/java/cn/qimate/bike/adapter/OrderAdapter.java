package cn.qimate.bike.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.qimate.bike.R;
import cn.qimate.bike.activity.SettlementPlatformActivity;
import cn.qimate.bike.base.BaseViewAdapter;
import cn.qimate.bike.base.BaseViewHolder;
import cn.qimate.bike.model.BillBean;

/**
 * Created by Administrator1 on 2017/2/14.
 */

public class OrderAdapter extends BaseViewAdapter<BillBean>{

    private LayoutInflater inflater;
    Context context;

    public OrderAdapter(Context context) {
        super(context);
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.item_order, null);
        }
        ImageView iv_bike = BaseViewHolder.get(convertView,R.id.item_iv_bike);
        TextView order_sn = BaseViewHolder.get(convertView,R.id.item_order_sn);
        TextView order_state = BaseViewHolder.get(convertView,R.id.item_order_state);
        TextView car_number = BaseViewHolder.get(convertView,R.id.item_car_number);
        TextView payment_time = BaseViewHolder.get(convertView,R.id.item_payment_time);
        TextView car_type = BaseViewHolder.get(convertView,R.id.item_car_type);
        TextView order_amount = BaseViewHolder.get(convertView,R.id.item_order_amount);
        RelativeLayout rl_pay = BaseViewHolder.get(convertView,R.id.item_rl_pay);
        TextView payBtn = BaseViewHolder.get(convertView,R.id.item_payBtn);
        BillBean bean = getDatas().get(position);

        Log.e("BillAdapter===", bean.getOrder_sn()+"==="+bean.getOrder_type()+"==="+bean.getCar_type()+"==="+bean.getOrder_amount());

//        1骑行订单 2购买骑行卡订单 3调度费订单 4赔偿费订单 5充值订单
//        订单状态 0已取消 10已下单 20进行中 30待支付 40已完成
        order_sn.setText(bean.getOrder_sn());
        order_state.setText(bean.getOrder_state()==0?"已取消":bean.getOrder_state()==10?"已下单":bean.getOrder_state()==20?"进行中":bean.getOrder_state()==30?"待支付":"已完成");
        car_number.setText(bean.getCar_number());
        payment_time.setText(bean.getCreated_at());
        car_type.setText(bean.getCar_type()==1?"单车":"助力车");
        order_amount.setText("¥"+bean.getOrder_amount());


        if(bean.getCar_type()==1){
            iv_bike.setImageResource(R.drawable.bike_icon2);
        }else{
            iv_bike.setImageResource(R.drawable.ebike_icon2);
        }

        if(bean.getOrder_state()==30){
            rl_pay.setVisibility(View.VISIBLE);

            payBtn.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, SettlementPlatformActivity.class);
                    intent.putExtra("pay_scene", 1);
                    context.startActivity(intent);
                }
            });
        }else{
            rl_pay.setVisibility(View.GONE);
        }

        return convertView;
    }
}
