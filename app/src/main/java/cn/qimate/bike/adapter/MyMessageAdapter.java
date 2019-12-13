package cn.qimate.bike.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import cn.qimate.bike.R;
import cn.qimate.bike.base.BaseViewAdapter;
import cn.qimate.bike.base.BaseViewHolder;
import cn.qimate.bike.model.MyMessageBean;

/**
 * Created by Administrator1 on 2017/2/14.
 */

public class MyMessageAdapter extends BaseViewAdapter<MyMessageBean>{

    private LayoutInflater inflater;

    public MyMessageAdapter(Context context) {
        super(context);
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.item_my_message, null);
        }
        TextView title = BaseViewHolder.get(convertView,R.id.item_message_title);
        TextView content = BaseViewHolder.get(convertView,R.id.item_message_content);
        ImageView isRead = BaseViewHolder.get(convertView,R.id.item_message_isRead);
        MyMessageBean bean = getDatas().get(position);
        title.setText(bean.getTitle());
        content.setText(bean.getContent());

        if(bean.getIs_read()==0){
            isRead.setVisibility(View.VISIBLE);
        }else{
            isRead.setVisibility(View.GONE);
        }

        return convertView;
    }
}
