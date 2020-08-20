package cn.qimate.bike.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadOptions;
//import com.bumptech.glide.Glide;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.jock.pickerview.view.view.OptionsPickerView;
import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
//import cn.qimate.bike.BuildConfig;
import cn.qimate.bike.R;
import cn.qimate.bike.core.common.GetImagePath;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.core.widget.CustomDialog;
import cn.qimate.bike.core.widget.KeyboardLayout;
import cn.qimate.bike.core.widget.ListenerInputView;
import cn.qimate.bike.core.widget.LoadingDialog;
import cn.qimate.bike.core.widget.MLImageView;
import cn.qimate.bike.model.AdmissionTimeBean;
import cn.qimate.bike.model.CollegeListBean;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.model.UpTokenBean;
import cn.qimate.bike.model.UserBean;
import cn.qimate.bike.model.UserIndexBean;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;
import cn.qimate.bike.util.GlideRoundTransform;
import cn.qimate.bike.util.LogUtil;
import cn.qimate.bike.util.QiNiuInitialize;
import cn.qimate.bike.util.ToastUtil;
import cn.qimate.bike.util.UtilAnim;
import cn.qimate.bike.util.UtilBitmap;
import cn.qimate.bike.util.UtilScreenCapture;

/**
 * Created by Administrator on 2017/2/12 0012.
 */

public class PersonInfoActivity extends SwipeBackActivity implements View.OnClickListener {

    private Context context;

    private String imgUrl = Urls.uploadsheadImg;
    private String imageurl = "";
    private Uri imageUri;
    private final String IMAGE_FILE_NAME = "picture.jpg";// 照片文件名称
    private String urlpath; // 图片本地路径
    private String resultStr = ""; // 服务端返回结果集
    private final int REQUESTCODE_PICK = 0; // 相册选图标记
    private final int REQUESTCODE_TAKE = 1; // 相机拍照标记
    private final int REQUESTCODE_CUTTING = 2; // 图片裁切标记

    private ImageView iv_popup_window_back;
    private RelativeLayout rl_popup_window;

    private Button takePhotoBtn, pickPhotoBtn, cancelBtn;
    private Bitmap upBitmap;


    private LoadingDialog loadingDialog;
    private LinearLayout ll_back;
    private TextView title;
//    private TextView rightBtn;

    private ListenerInputView nameEdit;
    private TextView phoneNum;
    private ImageView headerImageView;
    private ImageView iv_camera;
    private LinearLayout ll_tip, ll_info;
    private KeyboardLayout personUI_mainLayout;
    private RelativeLayout rl_header;
    private RelativeLayout rl_name;
    private RelativeLayout rl_phoneNum;
    private RelativeLayout rl_realNameAuth;
    private RelativeLayout rl_studentAuth;
//    private EditText nickNameEdit;
    private RelativeLayout sexLayout, collegeLayout, admissionTimeLayout;
    private TextView sexText, schoolText, collegeText, admissionTimeText;


//    private RelativeLayout schoolLayout;
//    private TextView schoolText;
//    private RelativeLayout classLayout;
//    private TextView classEdit;
//    private EditText stuNumEdit;
//    private Button submitBtn;

    private OptionsPickerView pvOptions;
    private OptionsPickerView pvOptions1;
    private OptionsPickerView pvOptions2;

    private String upToken = "";
    private boolean isAuth;
    private String avatar = "";
    private String nickname = "";
    private String nickname0 = "";
    private String phone = "";
    private int sex;
    private String school_name = "";
    private String school_area = "";
    private int college_id;
    private String college_name = "";
    private String admission_time = "";
    private int is_full;

    private boolean isFirst = true;



    // 输入法
    private List<CollegeListBean> collegeList = new ArrayList<>();
    static ArrayList<String> item1 = new ArrayList<>();
    static ArrayList<String> item2 = new ArrayList<>();
    static ArrayList<String> item3 = new ArrayList<>();
    static ArrayList<String> item4 = new ArrayList<>();
    static ArrayList<ArrayList<String>> item5 = new ArrayList<>();

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            // 三级联动效果
            pvOptions.setPicker(item1);
            pvOptions.setCyclic(false, false, false);
            pvOptions.setSelectOptions(0, 0, 0);
//            sexLayout.setClickable(true);
        };
    };

    private Handler handler1 = new Handler() {
        public void handleMessage(Message msg) {
            // 三级联动效果
            pvOptions1.setPicker(item2);
            pvOptions1.setCyclic(false, false, false);
            pvOptions1.setSelectOptions(0, 0, 0);
//            schoolLayout.setClickable(true);
        };
    };
    private Handler handler2 = new Handler() {
        public void handleMessage(Message msg) {
            // 三级联动效果
            pvOptions2.setPicker(item3, item5, true);
            pvOptions2.setCyclic(false, false, false);
            pvOptions2.setSelectOptions(0, 0, 0);
//            classLayout.setClickable(true);
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_info);
        context = this;

//        isAuth = getIntent().getBooleanExtra("isAuth", false);
//        avatar = getIntent().getStringExtra("avatar");
//        nickname = getIntent().getStringExtra("nickname");
//        phone = getIntent().getStringExtra("phone");
//        sex = getIntent().getIntExtra("sex", 0);
//        school_name = getIntent().getStringExtra("school_name");
//        school_area = getIntent().getStringExtra("school_area");
//        college_id = getIntent().getIntExtra("college_id", 0);
//        college_name = getIntent().getStringExtra("college_name");
//        admission_time = getIntent().getStringExtra("admission_time");
//        is_full = getIntent().getIntExtra("is_full", 0);

        LogUtil.e("pia===onCreate", isAuth+"==="+avatar+"==="+nickname+"==="+phone+"==="+sex+"==="+school_name+"==="+school_area+"==="+college_id+"==="+college_name+"==="+admission_time);

//        schoolList = new ArrayList<>();
        initView();
    }

    private void initView(){

        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

        imageUri = Uri.parse("file:///sdcard/temp.jpg");
        iv_popup_window_back = (ImageView) findViewById(R.id.popupWindow_back);
        rl_popup_window = (RelativeLayout) findViewById(R.id.popupWindow);

        takePhotoBtn = (Button) findViewById(R.id.takePhotoBtn);
        pickPhotoBtn = (Button)findViewById(R.id.pickPhotoBtn);
        cancelBtn = (Button)findViewById(R.id.cancelBtn);

        takePhotoBtn.setOnClickListener(itemsOnClick);
        pickPhotoBtn.setOnClickListener(itemsOnClick);
        cancelBtn.setOnClickListener(itemsOnClick);

        // 选项选择器
        pvOptions = new OptionsPickerView(context,false);
        pvOptions1 = new OptionsPickerView(context,false);
        pvOptions2 = new OptionsPickerView(context,false);

        pvOptions.setTitle("选择学院");
        pvOptions1.setTitle("选择性别");
        pvOptions2.setTitle("选择入学时间");

        ll_back = (LinearLayout) findViewById(R.id.ll_backBtn);
//        title = (TextView) findViewById(R.id.mainUI_title_titleText);
//        title.setText("个人信息");
//        rightBtn = (TextView) findViewById(R.id.mainUI_title_rightBtn);
//        rightBtn.setText("变更手机");

        personUI_mainLayout = (KeyboardLayout)findViewById(R.id.personUI_mainLayout);
        ll_tip = (LinearLayout)findViewById(R.id.ll_tip);
        rl_header = (RelativeLayout)findViewById(R.id.rl_header);
        ll_info = (LinearLayout)findViewById(R.id.ll_info);
        headerImageView = (ImageView)findViewById(R.id.iv_header);
//        iv_camera = (ImageView)findViewById(R.id.iv_camera);
        nameEdit = (ListenerInputView)findViewById(R.id.settingUI_name);
        phoneNum = (TextView)findViewById(R.id.settingUI_phoneNum);
        rl_name = (RelativeLayout)findViewById(R.id.rl_name);
        rl_phoneNum = (RelativeLayout)findViewById(R.id.rl_phoneNum);
        sexLayout = (RelativeLayout)findViewById(R.id.rl_sex);
        collegeLayout = (RelativeLayout)findViewById(R.id.rl_college);
        admissionTimeLayout = (RelativeLayout)findViewById(R.id.rl_admission_time);
        rl_realNameAuth = (RelativeLayout)findViewById(R.id.rl_realNameAuth);
        rl_studentAuth = (RelativeLayout)findViewById(R.id.rl_studentAuth);

        sexText = (TextView)findViewById(R.id.tv_sex);
        schoolText = (TextView)findViewById(R.id.tv_school);
        collegeText = (TextView)findViewById(R.id.tv_college);
        admissionTimeText = (TextView)findViewById(R.id.tv_admission_time);



//        Glide.with(this).load(R.drawable.head_icon).transform(new GlideRoundTransform(this,50)).into(headerImageView);

//        if (schoolList.isEmpty() || item1.isEmpty()){
//            getSchoolList();
//        }

        ll_back.setOnClickListener(this);
//        headerImageView.setOnClickListener(this);
//        iv_camera.setOnClickListener(this);
        rl_header.setOnClickListener(this);
        rl_name.setOnClickListener(this);
//        rl_phoneNum.setOnClickListener(this);
        sexLayout.setOnClickListener(this);
        collegeLayout.setOnClickListener(this);
        admissionTimeLayout.setOnClickListener(this);
//        rl_realNameAuth.setOnClickListener(this);
//        rl_studentAuth.setOnClickListener(this);

//        initHttp();

        new Thread(new Runnable() {

            @Override
            public void run() {
                if (item2!= null && !item2.isEmpty() && 0 != item2.size()) {
                    handler1.sendEmptyMessage(0x123);
                    return;
                }
                if (!item2.isEmpty() || 0 != item2.size()) {
                    item2.clear();
                }
                item2.add("男");
                item2.add("女");
                handler1.sendEmptyMessage(0x123);
            }
        }).start();

        personUI_mainLayout.setOnSizeChangedListener(new KeyboardLayout.SizeChangedListener() {
            @Override
            public void onChanged(boolean showKeyboard) {
//                super.onSizeChanged(w, h, oldw, oldh);
                Log.e("onChanged===", "---" + showKeyboard);

                if(!showKeyboard){
                    isFirst = true;
                    nameEdit.setFocusable(false);
                    submit_nickname();
                }

//                if (null != mChangedListener && 0 != oldw && 0 != oldh) {
//                    if (h < oldh) {
//                        mShowKeyboard = true;
//                    } else {
//                        mShowKeyboard = false;
//                    }
//                    mChangedListener.onChanged(mShowKeyboard);
//                    Log.e(TAG, "mShowKeyboard-----      " + mShowKeyboard);
//                }
            }
        });


        // 设置默认选中的三级项目
        // 监听确定选择按钮
        pvOptions.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                college_id = collegeList.get(options1).getId();
                college_name = item1.get(options1);
                collegeText.setText(college_name);

                submit_college_id();
            }
        });

        pvOptions1.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {

            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                sex = options1 + 1;
                sexText.setText(item2.get(options1));

                submit_sex();
            }
        });

        pvOptions2.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {

            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
//                admission_time = item3.get(options1);
                admission_time = item3.get(options1) + item5.get(options1).get(option2);
                admissionTimeText.setText(admission_time);

                submit_admission_time();
            }
        });




//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                //        String string = new SimpleDateFormat("yyyy-MM").format(new Date()).toString();
//                Calendar calendar = Calendar.getInstance();
//                int year = calendar.get(Calendar.YEAR);
//                int month = calendar.get(Calendar.MONTH)+1;
//                LogUtil.e("admission_time===2", "==="+year+"==="+month);
//
//                if(item3.size()>0){
//                    item3.clear();
//                }
//
//                if(item4.size()>0){
//                    item4.clear();
//                }
//
//                for (int i=0; i<100*12; i++){
//                    if(month!=0){
//                    }else{
//                        item3.add(year + "年");
//
//                        item5.add(item4);
//
//                        item4 = new ArrayList<>();
//
//                        month = 12;
//                        year--;
//                    }
//
//                    item4.add(month + "月");
//
//                    month--;
//                }
//                handler2.sendEmptyMessage(0x123);
//            }
//        }).start();

        nameEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
//                nickname = nameEdit.getText().toString();
                LogUtil.e("onFocusChange===", nameEdit.getText().toString()+"==="+hasFocus);

                if(!hasFocus){
                    submit_nickname();
                }
            }
        });

        nameEdit.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                LogUtil.e("onTouch===", nameEdit.getText().toString()+"==="+isFirst);

                if(isFirst){
                    isFirst = false;
//                    nickname0 = nameEdit.getContext().toString();
                    nickname0 = nameEdit.getText().toString();
                }


                nameEdit.setFocusable(true);
                nameEdit.setFocusableInTouchMode(true);
                nameEdit.requestFocus();
                return false;
            }
        });

//        nameEdit.setShowSoftInputOnFocus(true);

        nameEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                LogUtil.e("onFocusChange===", "==="+hasFocus);
            }
        });
//        nameEdit.setOnKeyBoardStateChangeListener(new ListenerInputView.OnKeyBoardStateChangeListener() {
//
//            @Override
//            public void OnKeyBoardState(int state) {
//                LogUtil.e("OnKeyBoardState===", "==="+state);
//                switch (state) {
//                    //开启
//                    case 1:
//                        Toast.makeText(getApplicationContext(), "输入法显示了.", Toast.LENGTH_SHORT).show();
//                        break;
//                    //关闭
//                    case 0:
//                        Toast.makeText(getApplicationContext(), "变化为正常状态(输入法关闭).", Toast.LENGTH_SHORT).show();
//                        break;
//
//                    default:
//                        break;
//                }
//
//            }
//        });

//
//        // 切换后将EditText光标置于末尾
//        CharSequence charSequence = stuNumEdit.getText();
//        if (charSequence instanceof Spannable) {
//            Spannable spanText = (Spannable) charSequence;
//            Selection.setSelection(spanText, charSequence.length());
//        }
//
//        if ("2".equals(SharedPreferencesUrls.getInstance().getString("iscert",""))){
//            nameEdit.setEnabled(false);
//            sexLayout.setEnabled(false);
//            schoolLayout.setEnabled(false);
//            classLayout.setEnabled(false);
//            stuNumEdit.setEnabled(false);
//
//        }else {
//            nameEdit.setEnabled(true);
//            sexLayout.setEnabled(true);
//            schoolLayout.setEnabled(true);
//            classLayout.setEnabled(true);
//            stuNumEdit.setEnabled(true);
//        }

        initHttp();
        admission_time();
        getUpToken();
        colleges();
    }


    public void initHttp() {
        Log.e("pia===initHttp", "===");


        String access_token = SharedPreferencesUrls.getInstance().getString("access_token", "");
        if (access_token != null && !"".equals(access_token)) {
            HttpHelper.get(context, Urls.user, new TextHttpResponseHandler() {
                @Override
                public void onStart() {
                    onStartCommon("正在加载");
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    onFailureCommon(throwable.toString());
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, final String responseString) {
                    m_myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Log.e("pia===initHttp1", "==="+responseString);

                                ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                                UserBean bean = JSON.parseObject(result.getData(), UserBean.class);
//                              myPurse.setText(bean.getMoney());
//                              myIntegral.setText(bean.getPoints());


                                int cert1_status = bean.getCert1_status();
                                isAuth = cert1_status==3?true:false;

//                                points = bean.getPoints();
                                avatar = bean.getAvatar();
                                nickname = bean.getNickname();
                                phone = bean.getPhone();
                                sex = bean.getSex();
                                school_name = bean.getSchool_name();
                                school_area = bean.getSchool_area();
                                college_id = bean.getCollege_id();
                                college_name = bean.getCollege_name();
                                admission_time = bean.getAdmission_time();
                                is_full = bean.getIs_full();


                                if(is_full==0){
                                    ll_tip.setVisibility(View.VISIBLE);
                                }else{
                                    ll_tip.setVisibility(View.GONE);
                                }

                                if(isAuth){
                                    ll_info.setVisibility(View.VISIBLE);
                                }else{
                                    ll_info.setVisibility(View.GONE);
                                }

                                if(avatar==null || "".equals(avatar)){
                                    headerImageView.setImageResource(R.drawable.head_icon);
                                }else{
//                                  Glide.with(context).load(avatar).crossFade().into(headerImageView);
                                    Glide.with(context).load(avatar).into(headerImageView);
                                }

                                nameEdit.setText(nickname);
                                phoneNum.setText(phone);
                                if(sex==1){
                                    sexText.setText("男");
                                }else if(sex==2){
                                    sexText.setText("女");
                                }
                                schoolText.setText(school_name+((school_area==null||"".equals(school_area))?"":("（"+school_area+"）")));
                                collegeText.setText(college_name);
                                admissionTimeText.setText(admission_time);


                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (loadingDialog != null && loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
                            }
                        }
                    });

                }
            });
        } else {
            Toast.makeText(context, "请先登录账号", Toast.LENGTH_SHORT).show();
            UIHelper.goToAct(context, LoginActivity.class);
        }
    }

//    @Override
//    public void onResize(int w, int h, int oldw, int oldh) {
//        //如果第一次初始化
//        if (oldh == 0) {
//            return;
//        }
//        //如果用户横竖屏转换
//        if (w != oldw) {
//            return;
//        }
//        if (h < oldh) {
//            //输入法弹出
//        } else if (h > oldh) {
//            //输入法关闭
////            setCommentViewEnabled(false, false);
//        }
////        int distance = h - old;
////        EventBus.getDefault().post(new InputMethodChangeEvent(distance,mCurrentImageId));
//    }

    public void getUpToken() {
        RequestParams params = new RequestParams();
        HttpHelper.get(context, Urls.uploadtoken, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                onStartCommon("正在加载");
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                onFailureCommon(throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final String responseString) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            LogUtil.e("uploadtoken===", "==="+responseString);

                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

//                            LogUtil.e("uploadtoken===1", result.getData()+"==="+result.getStatus_code());

                            UpTokenBean bean = JSON.parseObject(result.getData(), UpTokenBean.class);

                            LogUtil.e("uploadtoken===2", bean+"==="+bean.getToken());

                            if (null != bean.getToken()) {

                                upToken = bean.getToken();

//                                SharedPreferencesUrls.getInstance().putString("access_token", "Bearer "+bean.getToken());
//                                Toast.makeText(context,"恭喜您,获取成功",Toast.LENGTH_SHORT).show();
//                                scrollToFinishActivity();

//                                uploadImage();
                            }else{
                                Toast.makeText(context, result.getMessage(), Toast.LENGTH_SHORT).show();
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (loadingDialog != null && loadingDialog.isShowing()){
                            loadingDialog.dismiss();
                        }
                    }
                });

            }
        });

    }

    public void uploadImage() {
        //定义数据上传结束后的处理动作
        final UpCompletionHandler upCompletionHandler = new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, final JSONObject response) {

//                JSONObject jsonObject = new JSONObject(info.timeStamp);

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        LogUtil.e("uploadImage===0", "==="+response);

                        try {
                            JSONObject jsonObject = new JSONObject(response.getString("image"));

                            avatar = jsonObject.getString("key");
//                    imageList.add("\""+imageurl+"\"");

                            LogUtil.e("UpCompletion===", avatar+"===");

//                    if(imageList.size()==imageUrlList.size()){
//
//                    }

//                    iv_add_photo.setVisibility(View.GONE);

//                    if (loadingDialog != null && loadingDialog.isShowing()){
//                        loadingDialog.dismiss();
//                    }

                            submit_avatar();

                        } catch (JSONException e) {

                            if (loadingDialog != null && loadingDialog.isShowing()){
                                loadingDialog.dismiss();
                            }
                            e.printStackTrace();
                        }
                    }
                });





            }
        };
        final UploadOptions uploadOptions = new UploadOptions(null, null, false, new UpProgressHandler() {
            @Override
            public void progress(String key, final double percent) {
                //百分数格式化
                NumberFormat fmt = NumberFormat.getPercentInstance();
                fmt.setMaximumFractionDigits(2);//最多两位百分小数，如25.23%

                LogUtil.e("progress===", "==="+fmt.format(percent));

//                tv.setText("图片已经上传:" + fmt.format(percent));
            }
        }, new UpCancellationSignal() {
            @Override
            public boolean isCancelled() {
                return false;
            }
        });
        try {
            //上传图片jjj
            LogUtil.e("uploadImage===", "==="+upToken);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        m_myHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (loadingDialog != null && !loadingDialog.isShowing()) {
                                    loadingDialog.setTitle("正在提交");
                                    loadingDialog.show();
                                }
                            }
                        });


                        QiNiuInitialize.getSingleton().put(getByte(), null, upToken, upCompletionHandler, uploadOptions);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void admission_time() {

        Log.e("admission_time===0", "===");

        try{
//          协议名 register注册协议 recharge充值协议 cycling_card骑行卡协议 insurance保险协议
            HttpHelper.get(context, Urls.admission_time, new TextHttpResponseHandler() {
                @Override
                public void onStart() {
                    if (loadingDialog != null && !loadingDialog.isShowing()) {
                        loadingDialog.setTitle("请稍等");
                        loadingDialog.show();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Toast.makeText(context, "fail=="+responseString, Toast.LENGTH_LONG).show();

                    Log.e("admission_time===fail", throwable.toString()+"==="+responseString);

                    if (loadingDialog != null && loadingDialog.isShowing()){
                        loadingDialog.dismiss();
                    }
                    UIHelper.ToastError(context, throwable.toString());
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    try {

//                        Toast.makeText(context, "=="+responseString, Toast.LENGTH_LONG).show();

                        Log.e("admission_time===1", "==="+responseString);

                        ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                        AdmissionTimeBean bean = JSON.parseObject(result.getData(), AdmissionTimeBean.class);

                        long time = (long)bean.getUnix()*1000l;
//                        long time = bean.getUnix();
                        String s = new SimpleDateFormat("yyyy-MM").format(time);
                        int year = Integer.parseInt(s.split("-")[0]);
                        int month = Integer.parseInt(s.split("-")[1]);
                        Log.e("admission_time===2", time+"==="+year+"==="+month);

//                        if(item.size()>0){
//                            item.clear();
//                        }
//
//                        if(item1.size()>0){
//                            item1.clear();
//                        }
//
//                        for (int i=0; i<bean.getCount()*12; i++){
//
////                            Log.e("admission_time===2", year+"==="+month+"==="+item+"==="+item1+"==="+item2);
//
//                            if(month!=0){
//                            }else{
//                                item.add(year + "年");
//
//                                item2.add(item1);
//
//                                item1 = new ArrayList<>();
//
//                                month = 12;
//                                year--;
//                            }
//
//                            item1.add(month + "月");
//
//                            month--;
//                        }

                        if(item3.size()>0){
                            item3.clear();
                        }

                        if(item4.size()>0){
                            item4.clear();
                        }

                        for (int i=0; i<bean.getCount()*12; i++){
                            if(month!=0){
                            }else{
                                item3.add(year + "年");

                                item5.add(item4);

                                item4 = new ArrayList<>();

                                month = 12;
                                year--;
                            }

                            item4.add(month + "月");

                            month--;
                        }
                        handler2.sendEmptyMessage(0x123);

//                        pvOptions.setPicker(item, item2, true);
//                        pvOptions.setCyclic(false, false, false);
//                        pvOptions.setSelectOptions(0, 0, 0);
//                        pvOptions.setLabels("省", "市");

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (loadingDialog != null && loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                    }
                }


            });
        }catch (Exception e){
            Toast.makeText(context, "==="+e, Toast.LENGTH_SHORT).show();
        }

    }

    private void colleges(){

        HttpHelper.get(context, Urls.colleges, null, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                if (loadingDialog != null && !loadingDialog.isShowing()) {
                    loadingDialog.setTitle("正在加载");
                    loadingDialog.show();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (loadingDialog != null && loadingDialog.isShowing()){
                    loadingDialog.dismiss();
                }
                UIHelper.ToastError(context, throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final String responseString) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.e("ria===colleges1", "==="+responseString);

                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                            JSONArray JSONArray = new JSONArray(result.getData());
//                            if (schoolList.size() != 0 || !schoolList.isEmpty()){
//                                schoolList.clear();
//                            }
                            if (item1.size() != 0 || !item1.isEmpty()){
                                item1.clear();
                            }
                            for (int i = 0; i < JSONArray.length();i++){
                                CollegeListBean bean = JSON.parseObject(JSONArray.getJSONObject(i).toString(), CollegeListBean.class);
                                collegeList.add(bean);
                                item1.add(bean.getName());
                            }
                            handler.sendEmptyMessage(0x123);


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (loadingDialog != null && loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onClick(View v) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

        LogUtil.e("onClick===", v+"===");

//        nameEdit.clearFocus();
        isFirst = true;
        nameEdit.setFocusable(false);

        switch (v.getId()){
            case R.id.ll_backBtn:
//                nameEdit.setFocusable(false);

                scrollToFinishActivity();
                break;

            case R.id.rl_header:
                clickPopupWindow();
                break;

//            case R.id.rl_name:
//                nameEdit.setFocusable(true);
//                nameEdit.setFocusableInTouchMode(true);
//                nameEdit.requestFocus();
////                UIHelper.goToAct(context, ChangePhoneNumActivity.class);
//                break;

            case R.id.rl_phoneNum:
//                UIHelper.goToAct(context, ChangePhoneNumActivity.class);
                break;

            case R.id.rl_sex:
                pvOptions1.show();
                break;

            case R.id.rl_college:
                if(collegeList.size()==0){
                    ToastUtil.showMessageApp(context, "暂无学院");
                }else{
                    pvOptions.show();
                }

                break;

            case R.id.rl_admission_time:
                pvOptions2.show();
                break;

            case R.id.rl_realNameAuth:
                UIHelper.goToAct(context, RealNameAuthActivity.class);
                break;

            case R.id.rl_studentAuth:
                UIHelper.goToAct(context, ChangePhoneNumActivity.class);
                break;

//            case R.id.settingUI_sexLayout:
//                pvOptions1.show();
//                break;
//            case R.id.settingUI_schoolLayout:
//                pvOptions.show();
//                break;
//            case R.id.settingUI_classLayout:
//                pvOptions2.show();
//                break;
//            case R.id.settingUI_submitBtn:
//                String uid = SharedPreferencesUrls.getInstance().getString("uid","");
//                String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
//                if (uid == null || "".equals(uid) || access_token == null || "".equals(access_token)){
//                    Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                String name = nameEdit.getText().toString().trim();
//                String nickName = nickNameEdit.getText().toString().trim();
//                String grade = classEdit.getText().toString().trim();
//                String stuNum = stuNumEdit.getText().toString().trim();
//                if(name == null || "".equals(name)){
//                    Toast.makeText(context,"请输入您的姓名",Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if(nickName == null || "".equals(nickName)){
//                    Toast.makeText(context,"请输入您的昵称",Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if(sex == null || "".equals(sex)){
//                    Toast.makeText(context,"请选择您的性别",Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if(school == null || "".equals(school)){
//                    Toast.makeText(context,"请选择您的学校",Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if(grade == null || "".equals(grade)){
//                    Toast.makeText(context,"请输入您的年级",Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if(stuNum == null || "".equals(stuNum)){
//                    Toast.makeText(context,"请输入您的学号",Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                submit(uid,access_token,name,nickName,sex,school,grade,stuNum);
//                break;
        }
    }

    private void clickPopupWindow() {
        // 获取截图的Bitmap
        Bitmap bitmap = UtilScreenCapture.getDrawing(this);

        Log.e("pia===clickPopupWindow", "==="+bitmap);

        if (bitmap != null) {
            // 将截屏Bitma放入ImageView
            iv_popup_window_back.setImageBitmap(bitmap);
            // 将ImageView进行高斯模糊【25是最高模糊等级】【0x77000000是蒙上一层颜色，此参数可不填】
//            UtilBitmap.blurImageView(context, iv_popup_window_back, 25, 0x77000000);
            UtilBitmap.blurImageView(context, iv_popup_window_back, 5, 0xAA000000);
        } else {
            // 获取的Bitmap为null时，用半透明代替
            iv_popup_window_back.setBackgroundColor(0x77000000);
        }

//        Glide.with(this)
//                .load(R.drawable.app_icon)
////                .apply(bitmapTransform(new BlurTransformation(50)))
//                .bitmapTransform(new BlurTransformation(this, 14, 3))
//                .into(iv_popup_window_back);
//
//        iv_popup_window_back.setVisibility(View.VISIBLE);

        // 打开弹窗
        UtilAnim.showToUp(rl_popup_window, iv_popup_window_back);

    }

    private void clickClosePopupWindow() {
        UtilAnim.hideToDown(rl_popup_window, iv_popup_window_back);
    }

    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        @SuppressLint("NewApi")
        @Override
        public void onClick(View v) {
            clickClosePopupWindow();
            switch (v.getId()) {
                // 拍照
                case R.id.takePhotoBtn:
                    if (Build.VERSION.SDK_INT >= 23) {
                        int checkPermission = context.checkSelfPermission(Manifest.permission.CAMERA);
                        if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                                requestPermissions(new String[]{Manifest.permission.CAMERA}, 101);
                            } else {
                                CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
                                customBuilder.setType(3).setTitle("温馨提示").setMessage("您需要在设置里打开相机权限！")
                                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        requestPermissions(new String[]{Manifest.permission.CAMERA},
                                                101);

                                    }
                                });
                                customBuilder.create().show();
                            }
                            return;
                        }
                    }

                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                        Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

//                        File file = new File(Environment.getExternalStorageDirectory()+"/images/", IMAGE_FILE_NAME);
                        File file = new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME);
                        if(!file.getParentFile().exists()){
                            file.getParentFile().mkdirs();
                        }

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(context, getPackageName() + ".fileprovider", file));

                        }else {
                            takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                        }


                        startActivityForResult(takeIntent, REQUESTCODE_TAKE);
                    }else {
                        Toast.makeText(context,"未找到存储卡，无法存储照片！",Toast.LENGTH_SHORT).show();
                    }

                    break;

                // 相册选择图片
                case R.id.pickPhotoBtn:
//                    Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

                        Intent intent;
                        if (Build.VERSION.SDK_INT < 19) {
                            intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                        } else {
                            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        }
                        startActivityForResult(intent, REQUESTCODE_PICK);
                    } else {
                        Toast.makeText(context, "未找到存储卡，无法存储照片！", Toast.LENGTH_SHORT).show();
                    }

                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.e("pia=onActivityResult", requestCode+"==="+resultCode);

        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK) {
//                    codenum = data.getStringExtra("codenum");
//                    m_nowMac = data.getStringExtra("m_nowMac");


                } else {
//                    Toast.makeText(context, "扫描取消啦!", Toast.LENGTH_SHORT).show();
                }
                break;

            case REQUESTCODE_PICK:// 直接从相册获取

                if (resultCode == RESULT_OK) {

                    if (data != null) {
                        try {
                            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

                                Log.e("minef=REQUESTCODE_PICK", Build.VERSION.SDK_INT+"==="+data.getData());

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    File imgUri = new File(GetImagePath.getPath(context, data.getData()));

                                    Log.e("minef=REQUESTCODE_PICK2", imgUri+"==="+ getPackageName());

                                    Uri dataUri = FileProvider.getUriForFile(context, getPackageName() + ".fileprovider", imgUri);

                                    Log.e("minef=REQUESTCODE_PICK3", imgUri+"==="+dataUri);

                                    startPhotoZoom(dataUri);
                                } else {
                                    startPhotoZoom(data.getData());
                                }
                            } else {
                                Toast.makeText(context, "未找到存储卡，无法存储照片！", Toast.LENGTH_SHORT).show();
                            }
                        } catch (NullPointerException e) {
                            e.printStackTrace();// 用户点击取消操作
                        }
                    }

//                    if (data != null){
//                        try {
//                            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
//                                if (imageUri != null) {
//                                urlpath = getRealFilePath(data.getData());
////                                urlpath  = FileUtil.getFilePathByUri(context, data.getData());
////                                urlpath = getRealFilePath(context, imageUri);
//
////                                urlpath  = "/zhuanke/firstBottomMenu/4321681icon_w_1_ORIGIN_0kjM.png";
//
////                                    Cursor cursor = null;
////
//////                                String[] proj = { MediaStore.Images.Media.DATA};
//////                                cursor = getActivity().getContentResolver().query(data.getData(), proj, null, null, null);
////                                    cursor = getActivity().getContentResolver().query(data.getData(), null, null, null, null);
////
////                                    if (cursor.moveToFirst()) {
//////                                      urlpath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
////                                        urlpath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
////
////                                        Log.e("minef=REQUESTCODE_PICK0", cursor+"==="+urlpath);
////
////                                        //api>=19时，photo_path的值为null，此时再做处理
////                                        if(urlpath == null) {
////                                            String wholeID = getDocumentId(data.getData());
////                                            String id = wholeID.split(":")[1];
////                                            String[] column = { MediaStore.Images.Media.DATA };
////                                            String sel = MediaStore.Images.Media._ID +"=?";
////                                            cursor = getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel, new String[] { id }, null);
////                                            int columnIndex = cursor.getColumnIndex(column[0]);
////                                            if (cursor.moveToFirst()){
////                                                urlpath = cursor.getString(columnIndex);//此时的路径为照片路径
////                                            }
////                                        }
////                                    }
////                                    cursor.close();
//
////                                    mHandler.sendEmptyMessage(1);
//
//                                    compress(); //压缩图片
//
//                                    //                                Bitmap bitmap = BitmapFactory.decodeFile(filepath.getPath());
//                                    //                                upBitmap = BitmapFactory.decodeFile(urlpath);
//
//                                    Log.e("minef=REQUESTCODE_PICK", urlpath+"==="+imageUri+"==="+upBitmap);
//
//                                    //                                upBitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(uriImageview));
//
//                                    headerImageView.setImageBitmap(upBitmap);
//
//                                    Log.e("minef=REQUESTCODE_PICK3", "===");
//
//
////                                uploadImage();
//                                }
//                            }else {
//                                Toast.makeText(context,"未找到存储卡，无法存储照片！",Toast.LENGTH_SHORT).show();
//                            }
//                        } catch (NullPointerException e) {
//                            e.printStackTrace();// 用户点击取消操作
//                        }
//                    }

                } else {
//                    Toast.makeText(context, "扫描取消啦!", Toast.LENGTH_SHORT).show();
                }



                break;
            case REQUESTCODE_TAKE:// 调用相机拍照
//                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                        //通过FileProvider创建一个content类型的Uri
//                        Uri inputUri = FileProvider.getUriForFile(context,
//                                context.getPackageName() + ".provider",
//                                new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME));
//                        startPhotoZoom(inputUri);//设置输入类型
//                    } else {
//                        File temp = new File(Environment.getExternalStorageDirectory() + "/" + IMAGE_FILE_NAME);
//                        startPhotoZoom(Uri.fromFile(temp));
//                    }
//                } else {
//                    Toast.makeText(context, "未找到存储卡，无法存储照片！", Toast.LENGTH_SHORT).show();
//                }

                if (resultCode == RESULT_OK) {
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){


//                        Log.e("REQUESTCODE_TAKE===0", "==="+data.getData());

                        File temp = new File(Environment.getExternalStorageDirectory() + "/" + IMAGE_FILE_NAME);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                        File temp = new File(Environment.getExternalStorageDirectory() + "/images/" + IMAGE_FILE_NAME);

                            if (Uri.fromFile(temp) != null) {
                                urlpath = getRealFilePath(Uri.fromFile(temp));
                                Log.e("REQUESTCODE_TAKE===", temp+"==="+Uri.fromFile(temp)+"==="+urlpath);


                                File imgUri = new File(GetImagePath.getPath(context, Uri.fromFile(temp)));
                                Uri dataUri = FileProvider.getUriForFile(context, getPackageName() + ".fileprovider", imgUri);

                                Log.e("REQUESTCODE_TAKE===1", imgUri+"==="+dataUri);

//                            Uri filepath = Uri.fromFile(temp);
                                startPhotoZoom(dataUri);

//                            compress(); //压缩图片
//
//                            headerImageView.setImageBitmap(upBitmap);
//
//                            Log.e("REQUESTCODE_TAKE===3", upBitmap+"===");
//
//                            if (loadingDialog != null && !loadingDialog.isShowing()) {
//                                loadingDialog.setTitle("正在提交");
//                                loadingDialog.show();
//                            }
//
//                            uploadImage();


                            }
                        } else {
                            urlpath = getRealFilePath(Uri.fromFile(temp));
                            startPhotoZoom(Uri.fromFile(temp));
                        }

//                        File imgUri = new File(GetImagePath.getPath(context, data.getData()));

                    }else {
                        Toast.makeText(context,"未找到存储卡，无法存储照片！",Toast.LENGTH_SHORT).show();
                    }
                }



                break;
            case REQUESTCODE_CUTTING:// 取得裁剪后的图片
                if (data != null) {
                    setPicToView(data);
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    void compress(){
        // 设置参数
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 只获取图片的大小信息，而不是将整张图片载入在内存中，避免内存溢出
        BitmapFactory.decodeFile(urlpath, options);
        int height = options.outHeight;
        int width= options.outWidth;
        int inSampleSize = 3; // 默认像素压缩比例，压缩为原图的1/2
//        int minLen = Math.min(height, width); // 原图的最小边长
//        if(minLen > 100) { // 如果原始图像的最小边长大于100dp（此处单位我认为是dp，而非px）
//            float ratio = (float)minLen / 100.0f; // 计算像素压缩比例
//            inSampleSize = (int)ratio;
//        }
        options.inJustDecodeBounds = false; // 计算好压缩比例后，这次可以去加载原图了
        options.inSampleSize = inSampleSize; // 设置为刚才计算的压缩比例
        upBitmap = BitmapFactory.decodeFile(urlpath, options); // 解码文件

//        ByteArrayOutputStream bos=new ByteArrayOutputStream();
//        upBitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos);//参数100表示不压缩
    }

    //获取资源文件中的图片
    public byte[] getByte() {
//        Resources res = getResources();
//        Bitmap bm = BitmapFactory.decodeResource(res, R.drawable.bike3);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bm.compress(Bitmap.CompressFormat.PNG, 80, baos);
        LogUtil.e("getByte===1", upBitmap+"===");
        upBitmap.compress(Bitmap.CompressFormat.PNG, 50, baos);
//        upBitmap.compress(Bitmap.CompressFormat.PNG, 10, baos);
        LogUtil.e("getByte===2", upBitmap+"==="+baos.toByteArray().length);

//        QiNiuInitialize.getSingleton().put(getByte(), null, upToken, upCompletionHandler, uploadOptions);

        return baos.toByteArray();
    }

    private void setPicToView(Intent data) {
        m_myHandler.post(new Runnable() {
            @Override
            public void run() {
//                Bundle extras = data.getExtras();
                if (imageUri != null) {
                    urlpath = getRealFilePath(imageUri);    ///sdcard/temp.jpg
//            if (loadingDialog != null && !loadingDialog.isShowing()) {
//                loadingDialog.setTitle("请稍等");
//                loadingDialog.show();
//            }
//            new Thread(uploadImageRunnable).start();

//            upBitmap = BitmapFactory.decodeFile(urlpath);

                    compress();

                    headerImageView.setImageBitmap(upBitmap);

                    Log.e("setPicToView===", imageUri+"==="+urlpath+"==="+upBitmap);

                    uploadImage();

                    Log.e("pia===setPicToView", "==="+urlpath);

//            Glide.with(this).load(bitmap).transform(new GlideRoundTransform(context,50)).into(headerImageView);

//            Glide.with(this).load(R.drawable.app_icon).transform(new GlideRoundTransform(this,50)).into(headerImageView);

//            urlpath  = FileUtil.getFilePathByUri(context, data.getData());
//            urlpath  = FileUtil.getFilePathByUri(context, extras);



//            compress(); //压缩图片
//            headerImageView.setImageBitmap(upBitmap);

                }
            }
        });


    }

//    File temp = new File(Environment.getExternalStorageDirectory() + "/images/" + IMAGE_FILE_NAME);

    public void startPhotoZoom2(Uri uri) {
        Log.e("pia=startPhotoZoom", "==="+uri);

        if (uri == null) {
            return;
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/images/" + IMAGE_FILE_NAME));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }

        Log.e("pia=startPhotoZoom", imageUri + "===" + uri);

        intent.setDataAndType(uri, "image/*");
        // crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, REQUESTCODE_CUTTING);
    }

    public void startPhotoZoom(Uri uri) {
        Log.e("pia=startPhotoZoom", Build.VERSION.SDK_INT+"==="+uri);

        if (uri == null) {
            return;
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Log.e("pia=startPhotoZoom1", Build.VERSION.SDK_INT+"==="+uri);

            imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }

        Log.e("minef=startPhotoZoom", imageUri+"==="+uri);

        intent.setDataAndType(uri, "image/*");
        // crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
//        intent.putExtra("outputX", 600);
//        intent.putExtra("outputY", 600);
        intent.putExtra("scale", true);
        intent.putExtra("scaleUpIfNeeded", true);// 去黑边
        intent.putExtra("circleCrop", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, REQUESTCODE_CUTTING);
    }

    public String getRealFilePath(final Uri uri) {
        if (null == uri)
            return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
//            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);

            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);

                        Log.e("getRealFilePath===", cursor+"==="+data);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

//    private void submit(String avatar, String nickname, int sex, int college_id, String admission_time){
    private void submit_avatar(){
        LogUtil.e("pia===submit", avatar+"==="+nickname+"==="+sex+"==="+college_id+"==="+admission_time);



        RequestParams params = new RequestParams();
        params.put("avatar", avatar);   //TODO  没有头像链接获取
//        params.put("nickname", nickname);
//        params.put("sex", sex);
//        params.put("college_id", college_id);   //TODO  没同时更新college_name
//        params.put("admission_time", admission_time);

        submit(params);
    }

    private void submit_nickname(){
        LogUtil.e("pia===nickname", avatar+"==="+nickname+"==="+sex+"==="+college_id+"==="+admission_time);

        RequestParams params = new RequestParams();
//        params.put("avatar", avatar);   //TODO  没有头像链接获取
        params.put("nickname", nameEdit.getText().toString());
//        params.put("sex", sex);
//        params.put("college_id", college_id);   //TODO  没同时更新college_name
//        params.put("admission_time", admission_time);
        submit(params);
    }

    private void submit_sex(){
        LogUtil.e("pia===submit", avatar+"==="+nickname+"==="+sex+"==="+college_id+"==="+admission_time);

        RequestParams params = new RequestParams();
//        params.put("avatar", avatar);   //TODO  没有头像链接获取
//        params.put("nickname", nickname);
        params.put("sex", sex);
//        params.put("college_id", college_id);   //TODO  没同时更新college_name
//        params.put("admission_time", admission_time);
        submit(params);
    }

    private void submit_college_id(){
        LogUtil.e("pia===submit", avatar+"==="+nickname+"==="+sex+"==="+college_id+"==="+admission_time);

        RequestParams params = new RequestParams();
//        params.put("avatar", avatar);   //TODO  没有头像链接获取
//        params.put("nickname", nickname);
//        params.put("sex", sex);
        params.put("college_id", college_id);   //TODO  没同时更新college_name
//        params.put("admission_time", admission_time);
        submit(params);
    }

    private void submit_admission_time(){
        LogUtil.e("pia===submit", avatar+"==="+nickname+"==="+sex+"==="+college_id+"==="+admission_time);

        RequestParams params = new RequestParams();
//        params.put("avatar", avatar);   //TODO  没有头像链接获取
//        params.put("nickname", nickname);
//        params.put("sex", sex);
//        params.put("college_id", college_id);   //TODO  没同时更新college_name
        params.put("admission_time", admission_time);

        submit(params);
    }


    private void submit(RequestParams params){
        HttpHelper.put(context, Urls.user, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (loadingDialog != null && !loadingDialog.isShowing()) {
                            loadingDialog.setTitle("正在提交");
                            loadingDialog.show();
                        }
                    }
                });

            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, final Throwable throwable) {

                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (loadingDialog != null && loadingDialog.isShowing()){
                            loadingDialog.dismiss();
                        }
                        UIHelper.ToastError(context, throwable.toString());
                    }
                });
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final String responseString) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                            Log.e("pia===submit1", "==="+responseString);

//                            Toast.makeText(context, result.getMessage(),Toast.LENGTH_SHORT).show();
                            ToastUtil.showMessageApp(context, result.getMessage());

                            if(result.getStatus_code()!=200){
                                nameEdit.setText(nickname0);
                            }

                            user();
                        } catch (Exception e) {
                            if (loadingDialog != null && loadingDialog.isShowing()){
                                loadingDialog.dismiss();
                            }

                            e.printStackTrace();
                        }

                    }
                });

            }
        });
    }

    public void user() {
        Log.e("pia===user", "===");

        HttpHelper.get(context, Urls.user, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                onStartCommon("正在加载");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                onFailureCommon(throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, final String responseString) {
                m_myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.e("minef===initHttp1", "==="+responseString);

                            ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);

                            UserBean bean = JSON.parseObject(result.getData(), UserBean.class);
                            is_full = bean.getIs_full();

                            if(is_full==0){
                                ll_tip.setVisibility(View.VISIBLE);
                            }else{
                                ll_tip.setVisibility(View.GONE);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (loadingDialog != null && loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                    }
                });

            }
        });
    }



    /**
     *
     * 获取学校
     * */
//    private void getSchoolList(){
//
//        HttpHelper.get(context, Urls.schoolList, new TextHttpResponseHandler() {
//
//            @Override
//            public void onStart() {
//                if (loadingDialog != null && !loadingDialog.isShowing()) {
//                    loadingDialog.setTitle("正在加载");
//                    loadingDialog.show();
//                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                if (loadingDialog != null && loadingDialog.isShowing()){
//                    loadingDialog.dismiss();
//                }
//                UIHelper.ToastError(context, throwable.toString());
//            }
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, String responseString) {
//                try {
//                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
//                    if (result.getFlag().equals("Success")) {
//                        JSONArray JSONArray = new JSONArray(result.getData());
//                        if (schoolList.size() != 0 || !schoolList.isEmpty()){
//                            schoolList.clear();
//                        }
//                        if (item1.size() != 0 || !item1.isEmpty()){
//                            item1.clear();
//                        }
//                        for (int i = 0; i < JSONArray.length();i++){
//                            SchoolListBean bean = JSON.parseObject(JSONArray.getJSONObject(i).toString(),SchoolListBean.class);
//                            schoolList.add(bean);
//                            item1.add(bean.getSchool());
//                        }
//                        handler.sendEmptyMessage(0x123);
//                    }else {
//                        Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
//                    }
//                }catch (Exception e) {
//                    e.printStackTrace();
//                }
//                if (loadingDialog != null && loadingDialog.isShowing()){
//                    loadingDialog.dismiss();
//                }
//            }
//        });
//    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            submit();
            nameEdit.setFocusable(false);

            scrollToFinishActivity();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onDestroy() {
        if (loadingDialog != null && loadingDialog.isShowing()){
            loadingDialog.dismiss();
        }

        super.onDestroy();
    }
}
