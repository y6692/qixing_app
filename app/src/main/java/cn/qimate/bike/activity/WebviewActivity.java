package cn.qimate.bike.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sofi.blelocker.library.Code;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.channels.CompletionHandler;

import cn.qimate.bike.R;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.core.widget.CustomDialog;
import cn.qimate.bike.core.widget.WebViewWithProgress;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;
import cn.qimate.bike.util.UIHelper;
import cn.qimate.bike.util.UtilAnim;
import cn.qimate.bike.util.UtilBitmap;
import cn.qimate.bike.util.UtilScreenCapture;

/**
 * Created by Administrator1 on 2017/2/13.
 */

public class WebviewActivity extends SwipeBackActivity implements View.OnClickListener{

    private Context context;
    private Activity activity;
    private ImageView backImg;
    private TextView title;

    private WebViewWithProgress mWebViewWithProgress;
    private WebView webview;

    private String link;

    private String shareTitle;
    private String shareDesc;
    private String share_url;
    private UMImage image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_webview);
        context = this;
        activity = this;
        initView();
    }

    private void initView(){

        backImg = (ImageView) findViewById(R.id.mainUI_title_backBtn);
        title = (TextView) findViewById(R.id.mainUI_title_titleText);
        title.setText(getIntent().getExtras().getString("title"));

        link = getIntent().getExtras().getString("link");

        mWebViewWithProgress = (WebViewWithProgress) findViewById(R.id.webViewUI_webView);
        webview = mWebViewWithProgress.getWebView();

        WebSettings webSettings = webview.getSettings();
        // 设置WebView属性，能够执行Javascript脚本
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDomStorageEnabled(true);

        // 设置WebView属性，取消密码保存提示
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);

        // 设置Web视图
        webview.loadUrl(link);
        backImg.setOnClickListener(this);


//        webview.setJavascriptInterface(new JsApi());
//
//        webview.addJavascriptInterface();

//        webview.evaluateJavascript("javascript:window.android.share()", new ValueCallback<String>() {
//        webview.evaluateJavascript("share()", new ValueCallback<String>() {
//            @Override
//            public void onReceiveValue(String value) {
//                //此处为 js 返回的结果
//
//                Log.e("webview===2", value+"===");
//            }
//        });

        webview.setClickable(true);
        webview.setWebViewClient(new WebViewClient());
//        webview.setWebChromeClient(new WebChromeClient());

        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                Log.e("shouldOverride===", view+"==="+url);

                // 如下方案可在非微信内部WebView的H5页面中调出微信支付
                if (url.startsWith("weixin://wap/pay?")) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);

                    return true;
                }



                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {

                Log.e("onPageFinished===", view+"==="+url);

//                view.loadUrl("javascript:window.android.share();");

                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

        });


//        webview.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (event.getAction() == KeyEvent.ACTION_DOWN) {
//
//                    Log.e("web===WebView2", webview+"==="+webview.canGoBack());
//
//                    if (keyCode == KeyEvent.KEYCODE_BACK && webview.canGoBack()) {  //表示按返回键
//                        webview.goBack();   //后退
//                        return true;    //已处理
//                    }
//                }
//                finish();
//                return false;
//            }
//        });

        webview.addJavascriptInterface(new JsApi(), "android");
    }

    public class JsApi {

//        @SuppressLint("JavascriptInterface")
        @JavascriptInterface
//        title、subtitle、cover、path
        public void share(String title, String subtitle, String cover, String path) {

//            邀请好友送骑行卡===每月最多可获取20张免费骑行卡===https://img.7mate.cn/FhfM7G7BWR61yu7LochzoKmRZO9S?e=1586167451&token=FXDJS_lmH1Gfs-Ni9I9kpPf6MZFTGz5U5BP1CgNu:7epooGMDM1oxvdVWZq8d38m-Xh4=

            Log.e("JsApi===share", title+"==="+subtitle+"==="+cover+"==="+path);

//            CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
//            customBuilder.setType(3).setTitle("温馨提示").setMessage("如需临时上锁，请点击确定后关闭车锁")
//                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.cancel();
//                        }
//                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.cancel();
//                }
//            });
//            CustomDialog customDialog = customBuilder.create();
//            customDialog.show();

            shareTitle = title;
            shareDesc = subtitle;
            share_url = path;

            if (cover.indexOf(Urls.HTTP) == -1){
                image = new UMImage(context, Urls.host+cover);
            }else {
                image = new UMImage(context, cover);
            }

//            IWXAPI mWxApi = WXAPIFactory.createWXAPI(context, "wx86d98ec252f67d07", true);
//            mWxApi.registerApp("wx86d98ec252f67d07");

//            new ShareAction(activity).setDisplayList(SHARE_MEDIA.WEIXIN,
//                    SHARE_MEDIA.WEIXIN_CIRCLE,SHARE_MEDIA.QQ,SHARE_MEDIA.QZONE).setShareboardclickCallback(shareBoardlistener)
//                    .open();



            initmPopupWindowView();

        }
    }

    private ShareBoardlistener shareBoardlistener = new ShareBoardlistener() {

        @Override
        public void onclick(SnsPlatform snsPlatform, final SHARE_MEDIA share_media) {

            m_myHandler.post(new Runnable() {
                @Override
                public void run() {
                    new ShareAction(activity).setPlatform(share_media).setCallback(umShareListener)
                            .withTitle(shareTitle).withText(shareDesc).withTargetUrl(share_url).withMedia(image).share();
                }
            });

        }
    };

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

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /** attention to this below ,must add this **/

        Log.e("web===WebView", requestCode + "===" + resultCode + "===" + data);

        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);

//        m_myHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                UMShareAPI.get(context).onActivityResult(requestCode, resultCode, data);
//            }
//        });
    }

    public void initmPopupWindowView(){

        // 获取自定义布局文件的视图
        View customView = getLayoutInflater().inflate(R.layout.pop_share_menu, null, false);
        // 创建PopupWindow宽度和高度
        RelativeLayout pop_win_bg = (RelativeLayout) customView.findViewById(R.id.pop_win_bg);
        ImageView iv_popup_window_back = (ImageView) customView.findViewById(R.id.popupWindow_back);
        // 获取截图的Bitmap
        Bitmap bitmap = UtilScreenCapture.getDrawing(this);
        if (bitmap != null) {
            // 将截屏Bitma放入ImageView
            iv_popup_window_back.setImageBitmap(bitmap);
            // 将ImageView进行高斯模糊【25是最高模糊等级】【0x77000000是蒙上一层颜色，此参数可不填】
            UtilBitmap.blurImageView(context, iv_popup_window_back, 10,0xAA000000);
        } else {
            // 获取的Bitmap为null时，用半透明代替
            iv_popup_window_back.setBackgroundColor(0x77000000);
        }
        // 打开弹窗
        UtilAnim.showToUp(pop_win_bg, iv_popup_window_back);
        // 创建PopupWindow宽度和高度
        final PopupWindow popupwindow = new PopupWindow(customView, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, true);
        /**
         * 设置动画效果 ,从上到下加载方式等，不设置自动的下拉，最好 [动画效果不好，不加实现下拉效果，不错]
         */
        popupwindow.setAnimationStyle(R.style.PopupAnimation);
        popupwindow.setOutsideTouchable(false);

        LinearLayout wechatLayout = (LinearLayout)customView.findViewById(R.id.pop_menu_wechatLayout);
        LinearLayout wxcircleLayout = (LinearLayout)customView.findViewById(R.id.pop_menu_wxcircleLayout);
        LinearLayout qqLayout = (LinearLayout)customView.findViewById(R.id.pop_menu_qqLayout);
        LinearLayout qzoneBtn = (LinearLayout)customView.findViewById(R.id.pop_menu_qzoneLayout);
        TextView tv_share_cancel = (TextView)customView.findViewById(R.id.tv_share_cancel);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(final View v) {



                switch (v.getId()){
                    case R.id.pop_menu_wechatLayout:

                        m_myHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                new ShareAction(activity).setPlatform(SHARE_MEDIA.WEIXIN).setCallback(umShareListener).withTitle(shareTitle).withText(shareDesc).withTargetUrl(share_url).withMedia(image).share();
//                                new ShareAction(activity).setPlatform(SHARE_MEDIA.WEIXIN).setCallback(umShareListener).withTitle(shareTitle).withText(shareDesc).withTargetUrl(share_url).share();
                            }
                        });

                        break;

                    case R.id.pop_menu_wxcircleLayout:
                        m_myHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                new ShareAction(activity).setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE).setCallback(umShareListener).withTitle(shareTitle).withText(shareDesc).withTargetUrl(share_url).withMedia(image).share();

                            }
                        });
                        break;

                    case R.id.pop_menu_qqLayout:
                        m_myHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                new ShareAction(activity).setPlatform(SHARE_MEDIA.QQ).setCallback(umShareListener).withTitle(shareTitle).withText(shareDesc).withTargetUrl(share_url).withMedia(image).share();

                            }
                        });
                        break;

                    case R.id.pop_menu_qzoneLayout:
                        m_myHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                new ShareAction(activity).setPlatform(SHARE_MEDIA.QZONE).setCallback(umShareListener).withTitle(shareTitle).withText(shareDesc).withTargetUrl(share_url).withMedia(image).share();

                            }
                        });
                        break;

                    case R.id.tv_share_cancel:
                        break;
                }
                popupwindow.dismiss();


            }
        };

        wechatLayout.setOnClickListener(listener);
        wxcircleLayout.setOnClickListener(listener);
        qqLayout.setOnClickListener(listener);
        qzoneBtn.setOnClickListener(listener);
        tv_share_cancel.setOnClickListener(listener);

        popupwindow.showAtLocation(customView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }


////     js调用原生的方法
//    class JsApi {
//        Intent intent;
//        @JavascriptInterface
//        void goToProductDetail(JSONObject jsonObject, CompletionHandler handler) throws JSONException {
//            String url = jsonObject.getString("url");
//            Log.i("djy", "商品url-->" + url);
////            intent = new Intent(mContext, H5Activity.class);
////            intent.putExtra(H5Activity.INTENT_URL, AutoUrls.MallDomain + url);
////            intent.putExtra(H5Activity.INTENT_SHOW_HEAD, true);
////            startActivity(intent);
//        }
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mainUI_title_backBtn:
                scrollToFinishActivity();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            scrollToFinishActivity();
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);

        if (event.getAction() == KeyEvent.ACTION_DOWN) {

            Log.e("web===WebView", webview + "===" + webview.canGoBack());

            if (keyCode == KeyEvent.KEYCODE_BACK && webview.canGoBack()) {  //表示按返回键
                webview.goBack();   //后退
                return true;    //已处理
            }
        }
        finish();
        return false;

    }
}
