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
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
//import com.bumptech.glide.Glide;

import org.apache.http.Header;

import java.io.File;
import java.util.ArrayList;

import cn.jock.pickerview.view.view.OptionsPickerView;
import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
//import cn.qimate.bike.BuildConfig;
import cn.qimate.bike.BuildConfig;
import cn.qimate.bike.R;
import cn.qimate.bike.core.common.GetImagePath;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.core.widget.CustomDialog;
import cn.qimate.bike.core.widget.LoadingDialog;
import cn.qimate.bike.core.widget.MLImageView;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.model.UserIndexBean;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;
import cn.qimate.bike.util.GlideRoundTransform;
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

    private TextView nameEdit;
    private TextView phoneNum;
    private ImageView headerImageView;
    private ImageView iv_camera;
    private RelativeLayout rl_header;
    private RelativeLayout rl_name;
    private RelativeLayout rl_phoneNum;
    private RelativeLayout rl_realNameAuth;
    private RelativeLayout rl_studentAuth;
//    private EditText nickNameEdit;
    private RelativeLayout sexLayout;
    private TextView sexText;
//    private RelativeLayout schoolLayout;
//    private TextView schoolText;
//    private RelativeLayout classLayout;
//    private TextView classEdit;
//    private EditText stuNumEdit;
//    private Button submitBtn;

    private OptionsPickerView pvOptions;
    private OptionsPickerView pvOptions1;
    private OptionsPickerView pvOptions2;
    private String sex = "";
    private String school = "";

    // 输入法
//    private List<SchoolListBean> schoolList;
    static ArrayList<String> item1 = new ArrayList<>();
    static ArrayList<String> item2 = new ArrayList<>();
    static ArrayList<String> item3 = new ArrayList<>();

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
            pvOptions2.setPicker(item3);
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
//        pvOptions = new OptionsPickerView(context,false);
        pvOptions1 = new OptionsPickerView(context,false);
//        pvOptions2 = new OptionsPickerView(context,false);
//
//        pvOptions.setTitle("选择学校");
        pvOptions1.setTitle("选择性别");
//        pvOptions2.setTitle("选择年级");

        ll_back = (LinearLayout) findViewById(R.id.ll_backBtn);
//        title = (TextView) findViewById(R.id.mainUI_title_titleText);
//        title.setText("个人信息");
//        rightBtn = (TextView) findViewById(R.id.mainUI_title_rightBtn);
//        rightBtn.setText("变更手机");


        rl_header = (RelativeLayout)findViewById(R.id.rl_header);
        headerImageView = (ImageView)findViewById(R.id.iv_header);
//        iv_camera = (ImageView)findViewById(R.id.iv_camera);
        nameEdit = (TextView)findViewById(R.id.settingUI_name);
        phoneNum = (TextView)findViewById(R.id.settingUI_phoneNum);
        rl_name = (RelativeLayout)findViewById(R.id.rl_name);
        rl_phoneNum = (RelativeLayout)findViewById(R.id.rl_phoneNum);
        sexLayout = (RelativeLayout)findViewById(R.id.rl_sex);
        rl_realNameAuth = (RelativeLayout)findViewById(R.id.rl_realNameAuth);
        rl_studentAuth = (RelativeLayout)findViewById(R.id.rl_studentAuth);

        sexText = (TextView)findViewById(R.id.tv_sex);

//        Glide.with(this).load(R.drawable.head_icon).transform(new GlideRoundTransform(this,50)).into(headerImageView);


//        nickNameEdit = (EditText)findViewById(R.id.settingUI_nickName);
//        sexLayout = (RelativeLayout)findViewById(R.id.settingUI_sexLayout);
//        sexText = (TextView)findViewById(R.id.settingUI_sex);
//        schoolLayout = (RelativeLayout)findViewById(R.id.settingUI_schoolLayout);
//        schoolText = (TextView)findViewById(R.id.settingUI_school);
//        classLayout = (RelativeLayout)findViewById(R.id.settingUI_classLayout);
//        classEdit = (TextView)findViewById(R.id.settingUI_class);
//        stuNumEdit = (EditText)findViewById(R.id.settingUI_stuNum);
//
//        submitBtn = (Button)findViewById(R.id.settingUI_submitBtn);
//
//        if (schoolList.isEmpty() || item1.isEmpty()){
//            getSchoolList();
//        }

        ll_back.setOnClickListener(this);
        headerImageView.setOnClickListener(this);
//        iv_camera.setOnClickListener(this);
        rl_header.setOnClickListener(this);
        rl_name.setOnClickListener(this);
        rl_phoneNum.setOnClickListener(this);
        sexLayout.setOnClickListener(this);
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
//
//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                if (item3!= null && !item3.isEmpty() && 0 != item3.size()) {
//                    handler2.sendEmptyMessage(0x123);
//                    return;
//                }
//                if (!item3.isEmpty() || 0 != item3.size()) {
//                    item3.clear();
//                }
//                item3.add("大一");
//                item3.add("大二");
//                item3.add("大三");
//                item3.add("大四");
//                item3.add("硕士生");
//                item3.add("博士生");
//                item3.add("教职工");
//                item3.add("其他");
//                handler2.sendEmptyMessage(0x123);
//            }
//        }).start();
//
//        // 设置默认选中的三级项目
//        // 监听确定选择按钮
//        pvOptions.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
//
//            @Override
//            public void onOptionsSelect(int options1, int option2, int options3) {
//                school = item1.get(options1);
//                schoolText.setText(school);
//            }
//        });
//
        pvOptions1.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {

            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                sex = item2.get(options1);
                sexText.setText(item2.get(options1));
            }
        });
//
//        pvOptions2.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
//
//            @Override
//            public void onOptionsSelect(int options1, int option2, int options3) {
//                classEdit.setText(item3.get(options1));
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
    }

    @Override
    public void onClick(View v) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        switch (v.getId()){
            case R.id.ll_backBtn:
                scrollToFinishActivity();
                break;

            case R.id.iv_header:
                clickPopupWindow();
                break;

            case R.id.rl_name:
                UIHelper.goToAct(context, ChangePhoneNumActivity.class);
                break;

            case R.id.rl_phoneNum:
                UIHelper.goToAct(context, ChangePhoneNumActivity.class);
                break;

            case R.id.rl_sex:
                pvOptions1.show();
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

                        File file = new File(Environment.getExternalStorageDirectory()+"/images/", IMAGE_FILE_NAME);
                        if(!file.getParentFile().exists()){
                            file.getParentFile().mkdirs();
                        }

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", file));

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

        Log.e("minef=onActivityResult", requestCode+"==="+resultCode);

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

                                    Log.e("minef=REQUESTCODE_PICK2", imgUri+"==="+ BuildConfig.APPLICATION_ID);

                                    Uri dataUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", imgUri);

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
//                                BuildConfig.APPLICATION_ID + ".provider",
//                                new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME));
//                        startPhotoZoom(inputUri);//设置输入类型
//                    } else {
//                        File temp = new File(Environment.getExternalStorageDirectory() + "/" + IMAGE_FILE_NAME);
//                        startPhotoZoom(Uri.fromFile(temp));
//                    }
//                } else {
//                    Toast.makeText(context, "未找到存储卡，无法存储照片！", Toast.LENGTH_SHORT).show();
//                }

                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){

                    File temp = new File(Environment.getExternalStorageDirectory() + "/images/" + IMAGE_FILE_NAME);
                    if (Uri.fromFile(temp) != null) {
                        urlpath = getRealFilePath(Uri.fromFile(temp));
                        Log.e("REQUESTCODE_TAKE===", temp+"==="+urlpath);

//                        Uri filepath = Uri.fromFile(temp);

                        compress(); //压缩图片

                        headerImageView.setImageBitmap(upBitmap);

//                        Glide.with(this).load(upBitmap).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.head_icon).transform(new GlideRoundTransform(this,50)).into(headerImageView);

//                        Glide.with(context).load(urlpath).into(headerImageView);
//
//                        RequestOptions options = new RequestOptions()
//                                         .centerCrop()
//                                         .placeholder(R.mipmap.ic_launcher_round) //预加载图片
//                                         .error(R.drawable.ic_launcher_foreground) //加载失败图片
//                                         .priority(Priority.HIGH) //优先级
//                                         .diskCacheStrategy(DiskCacheStrategy.NONE) //缓存
//                                         .transform(new GlideRoundTransform(5)); //圆角
//                        Glide.with(context).load(list.get(position).getImage()).apply(options).into(holder.imageView);

//                        Glide.with(this).load(headPortrait)
//                                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
//                                .into(personalDetailImg);//圆角


                        Log.e("REQUESTCODE_TAKE===3", upBitmap+"===");

//                        uploadImage();
                    }

//                            File temp = new File(Environment.getExternalStorageDirectory() + "/images/" + IMAGE_FILE_NAME);
//                            if (Uri.fromFile(temp) != null) {
//                                urlpath = getRealFilePath(context, Uri.fromFile(temp));
//
//                                Log.e("REQUESTCODE_TAKE===", temp+"==="+urlpath);
//
//                                if (loadingDialog != null && !loadingDialog.isShowing()) {
//                                    loadingDialog.setTitle("请稍等");
//                                    loadingDialog.show();
//                                }
//
//                                new Thread(uploadImageRunnable).start();
//                            }
                }else {
                    Toast.makeText(context,"未找到存储卡，无法存储照片！",Toast.LENGTH_SHORT).show();
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
        int inSampleSize = 2; // 默认像素压缩比例，压缩为原图的1/2
//        int minLen = Math.min(height, width); // 原图的最小边长
//        if(minLen > 100) { // 如果原始图像的最小边长大于100dp（此处单位我认为是dp，而非px）
//            float ratio = (float)minLen / 100.0f; // 计算像素压缩比例
//            inSampleSize = (int)ratio;
//        }
        options.inJustDecodeBounds = false; // 计算好压缩比例后，这次可以去加载原图了
        options.inSampleSize = inSampleSize; // 设置为刚才计算的压缩比例
        upBitmap = BitmapFactory.decodeFile(urlpath, options); // 解码文件
    }

    private void setPicToView(Intent data) {
        Bundle extras = data.getExtras();
        if (imageUri != null) {
            urlpath = getRealFilePath(imageUri);
//            if (loadingDialog != null && !loadingDialog.isShowing()) {
//                loadingDialog.setTitle("请稍等");
//                loadingDialog.show();
//            }
//            new Thread(uploadImageRunnable).start();

            Bitmap bitmap = BitmapFactory.decodeFile(urlpath);
            headerImageView.setImageBitmap(bitmap);

//            Glide.with(this).load(bitmap).transform(new GlideRoundTransform(context,50)).into(headerImageView);

//            Glide.with(this).load(R.drawable.app_icon).transform(new GlideRoundTransform(this,50)).into(headerImageView);

//            urlpath  = FileUtil.getFilePathByUri(context, data.getData());
//            urlpath  = FileUtil.getFilePathByUri(context, extras);

            Log.e("minef===setPicToView", data.getData()+"==="+urlpath);

//            compress(); //压缩图片
//            headerImageView.setImageBitmap(upBitmap);

        }

    }

    public void startPhotoZoom(Uri uri) {
        if (uri == null) {
            return;
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
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
        intent.putExtra("outputX", 600);
        intent.putExtra("outputY", 600);
        intent.putExtra("scale", true);
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

    private void submit(String uid,String access_token,String realname,String nickname,
                        String sex,String school,String grade,String stunum){

        RequestParams params = new RequestParams();
        params.put("uid",uid);
        params.put("access_token",access_token);
        params.put("realname",realname);
        params.put("nickname",nickname);
        params.put("sex",sex);
        params.put("school",school);
        params.put("grade",grade);
        params.put("stunum",stunum);
        HttpHelper.post(context, Urls.editUserinfo, params, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                if (loadingDialog != null && !loadingDialog.isShowing()) {
                    loadingDialog.setTitle("正在提交");
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
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
                    if (result.getFlag().equals("Success")) {
                        Toast.makeText(context,"恭喜您,信息修改成功",Toast.LENGTH_SHORT).show();
                        scrollToFinishActivity();
                    } else {
                        Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
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


    private void initHttp(){

        String uid = SharedPreferencesUrls.getInstance().getString("uid","");
        String access_token = SharedPreferencesUrls.getInstance().getString("access_token","");
        if (uid != null && !"".equals(uid) && access_token != null && !"".equals(access_token)){
            RequestParams params = new RequestParams();
            params.put("uid",uid);
            params.put("access_token",access_token);
            HttpHelper.get(context, Urls.userIndex, params, new TextHttpResponseHandler() {
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
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    try {
                        ResultConsel result = JSON.parseObject(responseString, ResultConsel.class);
                        if (result.getFlag().equals("Success")) {
                            UserIndexBean bean = JSON.parseObject(result.getData(), UserIndexBean.class);
                            nameEdit.setText(bean.getRealname());
                            phoneNum.setText(bean.getTelphone());
//                            nickNameEdit.setText(bean.getNickname());
//                            sexText.setText(bean.getSex());
//                            schoolText.setText(bean.getSchool());
//                            classEdit.setText(bean.getGrade());
//                            stuNumEdit.setText(bean.getStunum());

                            sex = bean.getSex();
                            school = bean.getSchool();
                        } else {
                            Toast.makeText(context,result.getMsg(),Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (loadingDialog != null && loadingDialog.isShowing()){
                        loadingDialog.dismiss();
                    }
                }
            });
        }else {
            Toast.makeText(context,"请先登录账号",Toast.LENGTH_SHORT).show();
            UIHelper.goToAct(context,LoginActivity.class);
        }
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
            scrollToFinishActivity();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
