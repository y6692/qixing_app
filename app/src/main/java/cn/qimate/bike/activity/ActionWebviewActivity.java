package cn.qimate.bike.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;

import cn.qimate.bike.R;
import cn.qimate.bike.core.widget.WebViewWithProgress;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;

/**
 * Created by Administrator1 on 2017/2/13.
 */

public class ActionWebviewActivity extends SwipeBackActivity implements View.OnClickListener{

    private Context context;
    private LinearLayout ll_back;
    private TextView title;
    private ImageView rightBtn;

    private WebViewWithProgress mWebViewWithProgress;
    private WebView webview;
    private String link;
    private String imageUrl;
    private String shareTitle;
    private UMImage image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_webview_alter);
        context = this;
        initView();
    }

    private void initView(){

        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        title = (TextView) findViewById(R.id.mainUI_title_titleText);
        title.setText(getIntent().getExtras().getString("title"));
        rightBtn = (ImageView)findViewById(R.id.ui_webView_title_rightBtn);

        link = getIntent().getExtras().getString("link");
        imageUrl = getIntent().getExtras().getString("imageUrl");
        shareTitle = getIntent().getExtras().getString("shareTitle");
        image = new UMImage(ActionWebviewActivity.this, imageUrl);

        mWebViewWithProgress = (WebViewWithProgress) findViewById(R.id.webViewUI_webView);
        webview = mWebViewWithProgress.getWebView();

        WebSettings webSettings = webview.getSettings();
        // 设置WebView属性，能够执行Javascript脚本
        webSettings.setJavaScriptEnabled(true);

        // 设置WebView属性，取消密码保存提示
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);

        // 设置Web视图
        webview.loadUrl(link);

        ll_back.setOnClickListener(this);
        rightBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_back:
                scrollToFinishActivity();
                break;
            case R.id.ui_webView_title_rightBtn:
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
         new ShareAction(ActionWebviewActivity.this).setPlatform(share_media).setCallback(umShareListener)
                 .withText(shareTitle).withTargetUrl(link).withMedia(image).share();
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
            scrollToFinishActivity();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
