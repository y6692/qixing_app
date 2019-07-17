package cn.qimate.bike.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;

import butterknife.ButterKnife;
import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
import cn.qimate.bike.R;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.core.widget.LoadingDialog;
import cn.qimate.bike.full.EnterActivity;
import cn.qimate.bike.model.InviteCodeBean;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;

/**
 * Created by 123 on 2018/3/14.
 */

public class InviteCodeActivity extends SwipeBackActivity implements View.OnClickListener {

    private ImageView backImage;
    private TextView codeText;
    private TextView redPacketText;
    private TextView countText;
    private LinearLayout countLayout;
    private LinearLayout redPacketLayout;
    private Button inviteBtn;

    private Context context;
    private LoadingDialog loadingDialog;
    private String shareTitle;
    private UMImage image;
    private String shareDesc;
    private String share_url;

    private boolean isBack = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_invate_code);
        ButterKnife.bind(this);
        context = this;
        isBack = getIntent().getExtras().getBoolean("isBack");
        init();
    }

    private void init(){

        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

        backImage = (ImageView)findViewById(R.id.ui_inviteCode_backImage);
        codeText = (TextView) findViewById(R.id.ui_inviteCode_codeText);
        redPacketText = (TextView) findViewById(R.id.ui_inviteCode_redPacketText);
        countText = (TextView) findViewById(R.id.ui_inviteCode_countText);
        countLayout = (LinearLayout) findViewById(R.id.ui_inviteCode_countLayout);
        redPacketLayout = (LinearLayout) findViewById(R.id.ui_inviteCode_redPacketLayout);
        inviteBtn = (Button) findViewById(R.id.ui_invateCode_inviteBtn);

        initListener();
    }


    private void initListener(){
        backImage.setOnClickListener(this);
        countLayout.setOnClickListener(this);
        redPacketLayout.setOnClickListener(this);
        inviteBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        final String uid = SharedPreferencesUrls.getInstance().getString("uid", "");
        final String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)) {
            Toast.makeText(context, "请先登录账号", Toast.LENGTH_SHORT).show();
            UIHelper.goToAct(context,LoginActivity.class);
            return;
        }
        switch (v.getId()){
            case R.id.ui_inviteCode_backImage:
                if (isBack){
                    scrollToFinishActivity();
                }else {
                    if ((!SharedPreferencesUrls.getInstance().getBoolean("isFirst", true)
                            && getVersion() == SharedPreferencesUrls.getInstance().getInt("version", 0))) {
                        UIHelper.goToAct(context, MainActivity.class);
                    } else {
                        SharedPreferencesUrls.getInstance().putBoolean("isFirst", false);
                        SharedPreferencesUrls.getInstance().putInt("version", getVersion());
                        UIHelper.goToAct(context, EnterActivity.class);
                    }
                }
                break;
            case R.id.ui_inviteCode_countLayout:
                UIHelper.goToAct(context,MyInviterListActivity.class);
                break;
            case R.id.ui_inviteCode_redPacketLayout:
//                UIHelper.goToAct(context,MyCommissionActivity.class);
                break;
            case R.id.ui_invateCode_inviteBtn:
                /**
                 * shareboard need the platform all you want and
                 * callbacklistener,then open it
                 **/
                new ShareAction(this).setDisplayList(SHARE_MEDIA.WEIXIN,
                        SHARE_MEDIA.WEIXIN_CIRCLE,SHARE_MEDIA.QQ,SHARE_MEDIA.QZONE).setShareboardclickCallback(shareBoardlistener)
                        .open();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initHttp();
    }

    private void initHttp(){

        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
            UIHelper.goToAct(context,LoginActivity.class);
            return;
        }
        RequestParams params = new RequestParams();
        params.put("uid", uid);
        params.put("access_token", access_token);
        HttpHelper.get(context, Urls.inviteCode, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                if (loadingDialog != null && !loadingDialog.isShowing()) {
                    loadingDialog.setTitle("正在加载");
                    loadingDialog.show();
                }
            }
            @Override
            public void onSuccess(int statusCode,  org.apache.http.Header[] headers, String responseString) {
                try {
                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
                    if (result.getFlag().equals("Success")) {
                        InviteCodeBean bean = JSON.parseObject(result.getData(),InviteCodeBean.class);
                        redPacketText.setText(bean.getSenddays());
                        countText.setText(bean.getInviter_num());
                        shareTitle = bean.getShare_title();
                        image = new UMImage(context, bean.getShare_thumb());
                        shareDesc = bean.getShare_desc();
                        share_url = bean.getShare_url();
                        codeText.setText(bean.getTelphone());
                    } else {
                        UIHelper.showToastMsg(context, result.getMsg(), R.drawable.ic_error);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (loadingDialog != null && loadingDialog.isShowing()){
                    loadingDialog.dismiss();
                }
            }

            @Override
            public void onFailure(int statusCode,  org.apache.http.Header[] headers, String responseString, Throwable throwable) {
                if (loadingDialog != null && loadingDialog.isShowing()){
                    loadingDialog.dismiss();
                }
                UIHelper.ToastError(context, throwable.toString());
            }

        });
    }
    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            Toast.makeText(context, "分享成功", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(context, " 分享失败啦", Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(context, "分享取消啦", Toast.LENGTH_SHORT).show();
        }
    };
    private ShareBoardlistener shareBoardlistener = new ShareBoardlistener() {

        @Override
        public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
            new ShareAction(InviteCodeActivity.this).setPlatform(share_media).setCallback(umShareListener)
                    .withTitle(shareTitle).withText(shareDesc).withTargetUrl(share_url).withMedia(image).share();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /** attention to this below ,must add this **/
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isBack){
                scrollToFinishActivity();
            }else {
                if ((!SharedPreferencesUrls.getInstance().getBoolean("isFirst", true)
                        && getVersion() == SharedPreferencesUrls.getInstance().getInt("version", 0))) {
                    UIHelper.goToAct(context, MainActivity.class);
                } else {
                    SharedPreferencesUrls.getInstance().putBoolean("isFirst", false);
                    SharedPreferencesUrls.getInstance().putInt("version", getVersion());
                    UIHelper.goToAct(context, EnterActivity.class);
                }
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public int getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            int version = info.versionCode;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
