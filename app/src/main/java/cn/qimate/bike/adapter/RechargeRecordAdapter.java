package cn.qimate.bike.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alipay.sdk.app.PayTask;

import org.apache.http.Header;

import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
import cn.qimate.bike.R;
import cn.qimate.bike.alipay.PayResult;
import cn.qimate.bike.base.BaseViewAdapter;
import cn.qimate.bike.base.BaseViewHolder;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.model.RechargeRecordBean;
import cn.qimate.bike.model.ResultConsel;

/**
 * Created by Administrator1 on 2017/2/21.
 */

public class RechargeRecordAdapter  extends BaseViewAdapter<RechargeRecordBean>{

    private LayoutInflater inflater;
    private static final int SDK_PAY_FLAG = 1;
    private int position = -1;

    public RechargeRecordAdapter(Context context) {
        super(context);
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.item_recharge_record, null);
        }
        TextView dates = BaseViewHolder.get(convertView,R.id.item_rechargeRecord_dates);
        TextView info = BaseViewHolder.get(convertView,R.id.item_rechargeRecord_info);
        TextView payBtn = BaseViewHolder.get(convertView,R.id.item_rechargeRecord_payBtn);
        final RechargeRecordBean bean = getDatas().get(position);
        dates.setText(bean.getAddtime());
        info.setText("￥"+bean.getPayprice());
        if ("2".equals(bean.getStatus())){
            payBtn.setVisibility(View.GONE);
        }else {
            payBtn.setVisibility(View.VISIBLE);
        }
        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_alipay(bean.getOsn(),position);
            }
        });
        return convertView;
    }
    public void show_alipay(final String osn,int position) {
        this.position = position;
        Toast.makeText(getContext(), "正在调起支付宝支付...", Toast.LENGTH_LONG).show();
        RequestParams params = new RequestParams();
        params.put("uid", SharedPreferencesUrls.getInstance().getString("uid",""));
        params.put("access_token",SharedPreferencesUrls.getInstance().getString("access_token",""));
        params.put("osn", osn);
        HttpHelper.get(getContext(), Urls.alipayType, params, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
                    if (result.getFlag().equals("Success")) {
                        final String payInfo = result.getData();
                        Runnable payRunnable = new Runnable() {
                            @Override
                            public void run() {
                                // 构造PayTask 对象
                                PayTask alipay = new PayTask((Activity)getContext());
                                // 调用支付接口，获取支付结果
                                String result = alipay.pay(payInfo, true);
                                Message msg = new Message();
                                msg.what = SDK_PAY_FLAG;
                                msg.obj = result;
                                mHandler.sendMessage(msg);
                            }
                        };
                        // 必须异步调用
                        Thread payThread = new Thread(payRunnable);
                        payThread.start();
                    } else {
                        UIHelper.showToastMsg(getContext(), result.getMsg(), R.drawable.ic_error);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                UIHelper.ToastError(getContext(), throwable.toString());
            }
        });
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        if (position != -1){
                            getDatas().get(position).setStatus("2");
                            notifyDataSetChanged();
                        }
                        Toast.makeText(getContext(), "恭喜您,支付成功", Toast.LENGTH_SHORT).show();
                    } else {
                        position = -1;
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(getContext(), "支付结果确认中", Toast.LENGTH_SHORT).show();
                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(getContext(), "支付失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };
}
