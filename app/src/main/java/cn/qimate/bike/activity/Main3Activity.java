package cn.qimate.bike.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.TextView;

import cn.qimate.bike.R;
import cn.qimate.bike.base.BaseActivity;
import cn.qimate.bike.base.BaseFragmentActivity;

public class Main3Activity extends Activity {

    TextView title;
    private WebView myWebView;

    private String url ="https://item.taobao.com/item.htm?spm=a230r.1.14.16.1b075e4fooLHwL&id=581644369639&ns=1&abbucket=1#detail";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main3);

        title = findViewById(R.id.mainUI_title);
        title.setText("xxx8");

        myWebView = findViewById(R.id.myWebView);
        WebSettings mysettings = myWebView.getSettings();
        mysettings.setSupportZoom(true);
        mysettings.setBuiltInZoomControls(true);
        mysettings.setJavaScriptEnabled(true);
        mysettings.setDomStorageEnabled(true);

        myWebView.setClickable(true);
        myWebView.setWebViewClient(new WebViewClient());
        myWebView.loadUrl(url);

        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
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
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

        });


        myWebView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {

                    Log.e("main3===WebView", myWebView+"==="+myWebView.canGoBack());

                    if (keyCode == KeyEvent.KEYCODE_BACK && myWebView.canGoBack()) {  //表示按返回键
                        myWebView.goBack();   //后退
                        return true;    //已处理
                    }
                }
                finish();
                return false;
            }
        });


    }

//    @Override
//    protected void onDestroy() {
//
//
//        if (myWebView != null) {
//            myWebView.getSettings().setJavaScriptEnabled(false);
//            myWebView.clearFormData();
//            myWebView.clearHistory();
//
//
//            myWebView.stopLoading();
//
//            myWebView.removeAllViews();
//
//            myWebView.setWebViewClient(null);
//            myWebView.clearHistory();
//            myWebView.clearCache(true);
//            myWebView.loadUrl("about:blank");
//            myWebView.freeMemory();
//            myWebView.pauseTimers();
//
//            ((ViewGroup) myWebView.getParent()).removeView(myWebView);
//            myWebView.destroy();
//            myWebView = null;
//        }
//
//        super.onDestroy();
//
//        Log.e("main3===onDestroy", "==="+myWebView);
//
//
//    }

}
