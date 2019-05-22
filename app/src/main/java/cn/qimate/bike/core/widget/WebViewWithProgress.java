package cn.qimate.bike.core.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import cn.qimate.bike.R;

/**
 * 
 * 备注:
 * 此WebViewWithProgress继承自Relativielayout,
 * 如果要设置webview的属性，要先调用getWebView()来取得
 * webview的实例
 * 
 * @author Administrator
 *
 */
public class WebViewWithProgress extends RelativeLayout{
	

	private Context context;
	private WebView mWebView = null;  
	//水平进度条
	private ProgressBar progressBar = null;
	//包含圆形进度条的布局
	private RelativeLayout progressBar_circle = null;  
	//进度条样式,Circle表示为圆形，Horizontal表示为水平
	private int mProgressStyle = ProgressStyle.Horizontal.ordinal();  
	//默认水平进度条高度
	public static int DEFAULT_BAR_HEIGHT = 10;
	//水平进度条的高
	private int mBarHeight = DEFAULT_BAR_HEIGHT; 
	
	public enum ProgressStyle{
		Horizontal,
		Circle
    }

    public WebViewWithProgress(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
		init();
	}

	public WebViewWithProgress(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.context = context;
        TypedArray attributes = context.obtainStyledAttributes(attrs,R.styleable.WebViewWithProgress);
        mProgressStyle = attributes.getInt(R.styleable.WebViewWithProgress_progressStyle,ProgressStyle.Horizontal.ordinal());
        mBarHeight = attributes.getDimensionPixelSize(R.styleable.WebViewWithProgress_barHeight, DEFAULT_BAR_HEIGHT);
        attributes.recycle();
		init();
	}

	private void init(){
		mWebView = new WebView(context);
		this.addView(mWebView, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		if(mProgressStyle == ProgressStyle.Horizontal.ordinal()){
			progressBar = (ProgressBar) LayoutInflater.from(context).inflate(R.layout.progress_horizontal, null);
			progressBar.setMax(100);
			progressBar.setProgress(0);
			WebViewWithProgress.this.addView(progressBar, LayoutParams.FILL_PARENT, mBarHeight);
		}else{
			progressBar_circle = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.progress_circle, null);
			WebViewWithProgress.this.addView(progressBar_circle, LayoutParams.FILL_PARENT,  LayoutParams.FILL_PARENT);
		}
		mWebView.setWebViewClient(new WebViewClient(){

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				view.loadUrl(url);
				return true;
			}
		});
		mWebView.setWebChromeClient(new WebChromeClient(){

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				// TODO Auto-generated method stub
				super.onProgressChanged(view, newProgress);
				if(newProgress == 100){
					if(progressBar!=null){
						progressBar.setVisibility(View.GONE);
					}
					if(progressBar_circle!=null){
						progressBar_circle.setVisibility(View.GONE);
					}
				}else{
					if(mProgressStyle == ProgressStyle.Horizontal.ordinal()){
						progressBar.setVisibility(View.VISIBLE);
						progressBar.setProgress(newProgress);
					}else{
						progressBar_circle.setVisibility(View.VISIBLE);
					}
				}
			}
			
			
		});
	}
	
	
	public WebView getWebView()
	{
		return mWebView;
	}
}
