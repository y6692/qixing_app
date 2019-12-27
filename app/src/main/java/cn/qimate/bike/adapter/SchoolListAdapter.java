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
import cn.qimate.bike.model.MyIntegralRecordBean;
import cn.qimate.bike.model.SchoolListBean;

/**
 * Created by Administrator1 on 2017/2/14.
 */

public class SchoolListAdapter extends BaseViewAdapter<SchoolListBean> {

    private LayoutInflater inflater;

    public SchoolListAdapter(Context context) {
        super(context);
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.item_school_list, null);
        }
        TextView name = BaseViewHolder.get(convertView, R.id.item_school_name);
        SchoolListBean bean = getDatas().get(position);

        Log.e("SchoolListAdapter===", "==="+bean.getName());

        name.setText(bean.getName());
        return convertView;
    }
}
