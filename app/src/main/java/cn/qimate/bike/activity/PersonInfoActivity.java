package cn.qimate.bike.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Selection;
import android.text.Spannable;
import android.view.KeyEvent;
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

import org.apache.http.Header;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import cn.jock.pickerview.view.view.OptionsPickerView;
import cn.loopj.android.http.RequestParams;
import cn.loopj.android.http.TextHttpResponseHandler;
import cn.qimate.bike.R;
import cn.qimate.bike.core.common.HttpHelper;
import cn.qimate.bike.core.common.SharedPreferencesUrls;
import cn.qimate.bike.core.common.UIHelper;
import cn.qimate.bike.core.common.Urls;
import cn.qimate.bike.core.widget.LoadingDialog;
import cn.qimate.bike.model.ResultConsel;
import cn.qimate.bike.model.SchoolListBean;
import cn.qimate.bike.model.UserIndexBean;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;

/**
 * Created by Administrator on 2017/2/12 0012.
 */

public class PersonInfoActivity extends SwipeBackActivity implements View.OnClickListener {

    private Context context;
    private LoadingDialog loadingDialog;
    private LinearLayout ll_back;
    private TextView title;
//    private TextView rightBtn;

    private TextView nameEdit;
    private TextView phoneNum;
    private RelativeLayout rl_name;
    private RelativeLayout rl_phoneNum;
    private RelativeLayout rl_realNameAuth;
    private RelativeLayout rl_studentAuth;
//    private EditText nickNameEdit;
//    private RelativeLayout sexLayout;
//    private TextView sexText;
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
//    static ArrayList<String> item1 = new ArrayList<>();
//    static ArrayList<String> item2 = new ArrayList<>();
//    static ArrayList<String> item3 = new ArrayList<>();

//    private Handler handler = new Handler() {
//        public void handleMessage(Message msg) {
//            // 三级联动效果
//            pvOptions.setPicker(item1);
//            pvOptions.setCyclic(false, false, false);
//            pvOptions.setSelectOptions(0, 0, 0);
//            sexLayout.setClickable(true);
//        };
//    };
//
//    private Handler handler1 = new Handler() {
//        public void handleMessage(Message msg) {
//            // 三级联动效果
//            pvOptions1.setPicker(item2);
//            pvOptions1.setCyclic(false, false, false);
//            pvOptions1.setSelectOptions(0, 0, 0);
//            schoolLayout.setClickable(true);
//        };
//    };
//    private Handler handler2 = new Handler() {
//        public void handleMessage(Message msg) {
//            // 三级联动效果
//            pvOptions2.setPicker(item3);
//            pvOptions2.setCyclic(false, false, false);
//            pvOptions2.setSelectOptions(0, 0, 0);
//            classLayout.setClickable(true);
//        };
//    };

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

        // 选项选择器
//        pvOptions = new OptionsPickerView(context,false);
//        pvOptions1 = new OptionsPickerView(context,false);
//        pvOptions2 = new OptionsPickerView(context,false);
//
//        pvOptions.setTitle("选择学校");
//        pvOptions1.setTitle("选择性别");
//        pvOptions2.setTitle("选择年级");

        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        title = (TextView) findViewById(R.id.mainUI_title_titleText);
        title.setText("个人信息");
//        rightBtn = (TextView) findViewById(R.id.mainUI_title_rightBtn);
//        rightBtn.setText("变更手机");

        nameEdit = (TextView)findViewById(R.id.settingUI_name);
        phoneNum = (TextView)findViewById(R.id.settingUI_phoneNum);
        rl_name = (RelativeLayout)findViewById(R.id.rl_name);
        rl_phoneNum = (RelativeLayout)findViewById(R.id.rl_phoneNum);
        rl_realNameAuth = (RelativeLayout)findViewById(R.id.rl_realNameAuth);
        rl_studentAuth = (RelativeLayout)findViewById(R.id.rl_studentAuth);


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
        rl_name.setOnClickListener(this);
        rl_phoneNum.setOnClickListener(this);
        rl_realNameAuth.setOnClickListener(this);
        rl_studentAuth.setOnClickListener(this);
//        rightBtn.setOnClickListener(this);
//        sexLayout.setOnClickListener(this);
//        schoolLayout.setOnClickListener(this);
//        classLayout.setOnClickListener(this);
//        submitBtn.setOnClickListener(this);
        initHttp();

//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                if (item2!= null && !item2.isEmpty() && 0 != item2.size()) {
//                    handler1.sendEmptyMessage(0x123);
//                    return;
//                }
//                if (!item2.isEmpty() || 0 != item2.size()) {
//                    item2.clear();
//                }
//                item2.add("男");
//                item2.add("女");
//                handler1.sendEmptyMessage(0x123);
//            }
//        }).start();
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
//        pvOptions1.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
//
//            @Override
//            public void onOptionsSelect(int options1, int option2, int options3) {
//                sex = item2.get(options1);
//                sexText.setText(item2.get(options1));
//            }
//        });
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
            case R.id.ll_back:
                scrollToFinishActivity();
                break;

            case R.id.rl_name:
                UIHelper.goToAct(context, ChangePhoneNumActivity.class);
                break;

            case R.id.rl_phoneNum:
                UIHelper.goToAct(context, ChangePhoneNumActivity.class);
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
