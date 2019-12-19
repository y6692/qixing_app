package cn.qimate.bike.core.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.nostra13.universalimageloader.core.DisplayImageOptions;
import cn.nostra13.universalimageloader.core.ImageLoader;
import cn.qimate.bike.R;
import cn.qimate.bike.core.common.Urls;

/**
 * 
 * Create custom Dialog windows for your application Custom dialogs rely on
 * custom layouts wich allow you to create and use your own look & feel.
 * 
 * Under GPL v3 : http://www.gnu.org/licenses/gpl-3.0.html
 * 
 * <a href="http://my.oschina.net/arthor" target="_blank" rel="nofollow">@author
 * </a> antoine vianey
 *
 */
public class CustomDialog extends Dialog {

	public CustomDialog(Context context, int theme) {
		super(context, theme);
	}

	public CustomDialog(Context context) {
		super(context);
	}

	/**
	 * Helper class for creating a custom dialog
	 */
	public static class Builder {

		private Context context;
		private String title;
		private String message;
		private String positiveButtonText;
		private String negativeButtonText;
		private boolean isHint = true;
		private View contentView;
		private int type;
		private String electricity;
		private String mileage;
		private String fee;
		private String img_url;

		public String getImg_url() {
			return img_url;
		}

		public Builder setImg_url(String img_url) {
			this.img_url = img_url;
			return this;
		}

		public String getElectricity() {
			return electricity;
		}

		public Builder setElectricity(String electricity) {
			this.electricity = electricity;
			return this;
		}

		public String getMileage() {
			return mileage;
		}

		public Builder setMileage(String mileage) {
			this.mileage = mileage;
			return this;
		}

		public String getFee() {
			return fee;
		}

		public Builder setFee(String fee) {
			this.fee = fee;
			return this;
		}

		private OnClickListener positiveButtonClickListener, negativeButtonClickListener;

		public Builder(Context context) {
			this.context = context;
		}

		public int getType() {
			return type;
		}

		public Builder setType(int type) {
			this.type = type;
			return this;
		}

		/**
		 * Set the Dialog message from String
		 * 
		 * @param message
		 * @return
		 */
		public Builder setMessage(String message) {
			this.message = message;
			return this;
		}

		/**
		 * Set the Dialog message from resource
		 * 
		 * @param message
		 * @return
		 */
		public Builder setMessage(int message) {
			this.message = (String) context.getText(message);
			return this;
		}

		/**
		 * Set the Dialog title from resource
		 * 
		 * @param title
		 * @return
		 */
		public Builder setTitle(int title) {
			this.title = (String) context.getText(title);
			return this;
		}

		/**
		 * Set the Dialog title from String
		 * 
		 * @param title
		 * @return
		 */
		public Builder setTitle(String title) {
			this.title = title;
			return this;
		}
		public Builder setHint(boolean isHint) {
			this.isHint = isHint;
			return this;
		}
		/**
		 * Set a custom content view for the Dialog. If a message is set, the
		 * contentView is not added to the Dialog...
		 * 
		 * @param v
		 * @return
		 */
		public Builder setContentView(View v) {
			this.contentView = v;
			return this;
		}

		/**
		 * Set the positive button resource and it's listener
		 * 
		 * @param positiveButtonText
		 * @param listener
		 * @return
		 */
		public Builder setPositiveButton(int positiveButtonText, OnClickListener listener) {
			this.positiveButtonText = (String) context.getText(positiveButtonText);
			this.positiveButtonClickListener = listener;
			return this;
		}

		/**
		 * Set the positive button text and it's listener
		 * 
		 * @param positiveButtonText
		 * @param listener
		 * @return
		 */
		public Builder setPositiveButton(String positiveButtonText, OnClickListener listener) {
			this.positiveButtonText = positiveButtonText;
			this.positiveButtonClickListener = listener;
			return this;
		}

		/**
		 * Set the negative button resource and it's listener
		 * 
		 * @param negativeButtonText
		 * @param listener
		 * @return
		 */
		public Builder setNegativeButton(int negativeButtonText, OnClickListener listener) {
			this.negativeButtonText = (String) context.getText(negativeButtonText);
			this.negativeButtonClickListener = listener;
			return this;
		}

		/**
		 * Set the negative button text and it's listener
		 * 
		 * @param negativeButtonText
		 * @param listener
		 * @return
		 */
		public Builder setNegativeButton(String negativeButtonText, OnClickListener listener) {
			this.negativeButtonText = negativeButtonText;
			this.negativeButtonClickListener = listener;
			return this;
		}

		/**
		 * Create the custom dialog
		 */
		public CustomDialog create() {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// instantiate the dialog with the custom Theme
			final CustomDialog dialog = new CustomDialog(context, R.style.Dialog);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);

			View layout;

			Log.e("CustomDialog===", type+"===");

			if(type==1){
				layout = inflater.inflate(R.layout.alertdialog, null);
			}else{
				if(type==2){
					layout = inflater.inflate(R.layout.alertdialog2, null);
				}else if(type==3){
					layout = inflater.inflate(R.layout.alertdialog3, null);
				}else if(type==4){
					layout = inflater.inflate(R.layout.alertdialog4, null);
				}else if(type==5){
					layout = inflater.inflate(R.layout.alertdialog5, null);
				}else if(type==6){
					layout = inflater.inflate(R.layout.alertdialog6, null);
				}else if(type==7){	//客服电话
					layout = inflater.inflate(R.layout.alertdialog7, null);
				}else if(type==8){	//客服电话
					layout = inflater.inflate(R.layout.alertdialog8, null);
				}else if(type==9){	//维护
					layout = inflater.inflate(R.layout.alertdialog9, null);
				}else{
					layout = inflater.inflate(R.layout.alertdialog, null);
				}


                // set the confirm button
				if(type!=0){
					if (positiveButtonText != null) {
						((Button) layout.findViewById(R.id.positiveButton)).setText(positiveButtonText);
						if (positiveButtonClickListener != null) {
							((Button) layout.findViewById(R.id.positiveButton)).setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									positiveButtonClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
								}
							});
						}
					} else {
						// if no confirm button just set the visibility to GONE
						layout.findViewById(R.id.positiveButton).setVisibility(View.GONE);
					}
					// set the cancel button
					if (negativeButtonText != null) {
						((Button) layout.findViewById(R.id.negativeButton)).setText(negativeButtonText);
						if (negativeButtonClickListener != null) {
							((Button) layout.findViewById(R.id.negativeButton)).setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									negativeButtonClickListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
								}
							});
						}
					} else {
						// if no confirm button just set the visibility to GONE
						layout.findViewById(R.id.negativeButton).setVisibility(View.GONE);
					}
				}

			}

//			dialog.addContentView(layout, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			dialog.addContentView(layout, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			// set the dialog title
			if(type!=0 && type!=4){
				((TextView) layout.findViewById(R.id.title)).setText(title);
			}


			if(type==4){
//				TextView tv_electricity = ((TextView) layout.findViewById(R.id.electricity));
//				TextView tv_mileage = ((TextView) layout.findViewById(R.id.mileage));
//				TextView tv_fee = ((TextView) layout.findViewById(R.id.fee));
//
//				tv_electricity.setText(electricity);
//				tv_mileage.setText(mileage);
//				tv_fee.setText(fee);

				ImageView iv_cancel = (ImageView) layout.findViewById(R.id.iv_cancel);

				iv_cancel.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});

			}else if(type==5 || type==9){
//				ImageView iv_img_url = ((ImageView) layout.findViewById(R.id.img_url));
//
//				ImageLoader.getInstance().displayImage(img_url, iv_img_url, options);

			}else if(type==7){
				TextView tel1 = ((TextView) layout.findViewById(R.id.tel1));
				TextView tel2 = ((TextView) layout.findViewById(R.id.tel2));

				tel1.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent=new Intent();
						intent.setAction(Intent.ACTION_CALL);
						intent.setData(Uri.parse("tel:" + "0519-86999222"));
						context.startActivity(intent);
					}
				});

				tel2.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent=new Intent();
						intent.setAction(Intent.ACTION_CALL);
						intent.setData(Uri.parse("tel:" + "13275248446"));
						context.startActivity(intent);
					}
				});

			}else if(type==0){
//				TextView hintText = ((TextView) layout.findViewById(R.id.hintText));
//				if (hintText != null) {
//					if (isHint){
//						hintText.setVisibility(View.GONE);
//					}else {
//						hintText.setVisibility(View.VISIBLE);
//					}
//				}

                ImageView alert_cancelBtn = (ImageView) layout.findViewById(R.id.alert_cancelBtn);

                alert_cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
			}


			// set the content message
			if (message != null) {
				((TextView) layout.findViewById(R.id.message)).setText(message);
			} else if (contentView != null) {
				// if no message set
				// add the contentView to the dialog body
				((LinearLayout) layout.findViewById(R.id.content)).removeAllViews();
				((LinearLayout) layout.findViewById(R.id.content)).addView(contentView, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			}
			dialog.setContentView(layout);

			dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);

			return dialog;
		}

	}


	private static DisplayImageOptions options = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.empty_photo) // 加载图片时的图片
			.showImageForEmptyUri(R.drawable.empty_photo) // 没有图片资源时的默认图片
			.showImageOnFail(R.drawable.big_loadpic_fail_listpage) // 加载失败时的图片
			.cacheInMemory(false) // 启用内存缓存
			.cacheOnDisk(false) // 启用外存缓存
			.considerExifParams(true) // 启用EXIF和JPEG图像格式
			// .displayer(new RoundedBitmapDisplayer(20)) //设置显示风格这里是圆角矩形
			.build();

}
