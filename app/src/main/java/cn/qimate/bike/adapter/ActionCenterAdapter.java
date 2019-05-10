package cn.qimate.bike.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.nostra13.universalimageloader.core.DisplayImageOptions;
import cn.nostra13.universalimageloader.core.ImageLoader;
import cn.qimate.bike.R;
import cn.qimate.bike.base.BaseViewAdapter;
import cn.qimate.bike.base.BaseViewHolder;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.model.ActionCenterBean;

/**
 * Created by LDY on 2017/2/10.
 */

public class ActionCenterAdapter extends BaseViewAdapter<ActionCenterBean>{

    private LayoutInflater inflater;

    private DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.empty_photo) // 加载图片时的图片
            .showImageForEmptyUri(R.drawable.empty_photo) // 没有图片资源时的默认图片
            .showImageOnFail(R.drawable.big_loadpic_fail_listpage) // 加载失败时的图片
            .cacheInMemory(false) // 启用内存缓存
            .cacheOnDisk(false) // 启用外存缓存
            .considerExifParams(true) // 启用EXIF和JPEG图像格式
            // .displayer(new RoundedBitmapDisplayer(20)) //设置显示风格这里是圆角矩形
            .build();

    public ActionCenterAdapter(Context context) {
        super(context);
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.item_action_center, null);
        }
        View line = BaseViewHolder.get(convertView,R.id.item_actionCenter_line);
        RelativeLayout layout = BaseViewHolder.get(convertView,R.id.item_actionCenter_Layout);
        ImageView imageview = BaseViewHolder.get(convertView,R.id.item_actionCenter_imageview);
        TextView title = BaseViewHolder.get(convertView,R.id.item_actionCenter_title);
        // 设置广告高度为屏幕高度0.6倍
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layout.getLayoutParams();
        params.height = (int) (((Activity)getContext()).getWindowManager().getDefaultDisplay().getWidth() * 0.4);
        layout.setLayoutParams(params);
        if (0 == position){
            line.setVisibility(View.GONE);
        }else {
            line.setVisibility(View.VISIBLE);
        }
        ActionCenterBean bean = getDatas().get(position);
        Log.e("Test",Urls.host+bean.getAc_thumb());
        if (bean.getAc_thumb() != null && !"".equals(bean.getAc_thumb())){
            // 加载图片
//            Glide.with(getContext()).load(Urls.host+bean.getAc_thumb()).placeholder(R.drawable.empty_photo).into(imageview);
            ImageLoader.getInstance().displayImage(Urls.host+bean.getAc_thumb(), imageview, options);
        }
        title.setText(bean.getTitle());
        return convertView;
    }
}
