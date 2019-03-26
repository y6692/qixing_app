package cn.qimate.bike.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.qimate.bike.R;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.widget.WebViewWithProgress;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;

/**
 * Created by Administrator1 on 2017/2/13.
 */

public class MonthlyCardActivity extends SwipeBackActivity implements View.OnClickListener{

    private Context context;
    private LinearLayout ll_back;
    private TextView title;

    private WebViewWithProgress mWebViewWithProgress;
    private WebView webview;
    private Button goPay;

    private String link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_monthlycard);
        context = this;
        initView();
    }

    private void initView(){
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        title = (TextView) findViewById(R.id.mainUI_title_titleText);
        goPay = (Button) findViewById(R.id.ui_payMonth_cart_submitBtn);

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
        ll_back.setOnClickListener(this);
        goPay.setOnClickListener(this);

        webview.setClickable(true);
        webview.setWebViewClient(new WebViewClient());

        webview.setWebViewClient(new WebViewClient() {
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

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_back:
                scrollToFinishActivity();
                break;
            case R.id.ui_payMonth_cart_submitBtn:
                UIHelper.goToAct(context, PayMontCartActivity.class);
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
