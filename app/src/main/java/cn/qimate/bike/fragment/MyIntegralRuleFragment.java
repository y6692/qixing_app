package cn.qimate.bike.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import cn.qimate.bike.R;
import cn.qimate.bike.base.BaseFragment;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.core.widget.WebViewWithProgress;

/**
 * Created by Administrator1 on 2017/2/14.
 */

public class MyIntegralRuleFragment extends BaseFragment{

    private Context context;
    private View mRootView;

    private WebViewWithProgress mWebViewWithProgress;
    private WebView webview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.frag_my_integral_record, container, false);
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        initView();
    }

    private void initView(){

        mWebViewWithProgress = mRootView.findViewById(R.id.frag_myIntegral_record_webView);
        webview = mWebViewWithProgress.getWebView();

        WebSettings webSettings = webview.getSettings();
        // 设置WebView属性，能够执行Javascript脚本
        webSettings.setJavaScriptEnabled(true);

        // 设置WebView属性，取消密码保存提示
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);

        // 设置Web视图
        webview.loadUrl(Urls.pointRule);
    }
}
