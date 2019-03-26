package cn.qimate.bike.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.qimate.bike.R;
import cn.qimate.bike.core.widget.LoadingDialog;
import cn.qimate.bike.datepicker.MonthDateView;
import cn.qimate.bike.swipebacklayout.app.SwipeBackActivity;

/**
 * Created by Administrator1 on 2017/2/13.
 */

public class HistoryRoadFiltateActivity extends SwipeBackActivity implements View.OnClickListener{

    private Context context;
    private LoadingDialog loadingDialog;
    private LinearLayout ll_back;
    private TextView title;
    private TextView rightBtn;

    private TextView lastMonth;
    private TextView nextMonth;
    private TextView tv_date;
    private TextView tv_week;

    private MonthDateView monthDateView;
    private List<Integer> list;
    private int yearmonthday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_history_road_filtrate);
        context = this;
        list = new ArrayList<>();
        initView();
    }

    private void initView(){

        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);

        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        title = (TextView) findViewById(R.id.mainUI_title_titleText);
        title.setText("行程查询");
//        backImg.setImageResource(R.drawable.back_icon);
        rightBtn = (TextView)findViewById(R.id.mainUI_title_rightBtn);
        rightBtn.setText("确认");

        lastMonth = (TextView)findViewById(R.id.historyRoad_filtrate_lastMonth);
        nextMonth = (TextView)findViewById(R.id.historyRoad_filtrate_nextMonth);

        monthDateView = (MonthDateView) findViewById(R.id.historyRoad_filtrate_monthView);

        tv_date = (TextView) findViewById(R.id.date_text);
        tv_week = (TextView) findViewById(R.id.week_text);
        monthDateView.setTextView(tv_date, tv_week);

        monthDateView.setDateClick(new MonthDateView.DateClick() {
            @Override
            public void onClickOnDate() {
                String year_month = monthDateView.getmSelYear()
                        + String.format("%2d", (monthDateView.getmSelMonth() + 1)).replace(" ", "0");
                try {
                    yearmonthday = Integer
                            .valueOf(
                                    (year_month + String.format("%2d", (monthDateView.getmSelDay())).replace(" ", "0")))
                            .intValue();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                String yearmonthdates = monthDateView.getmSelYear() + "-"
                        + String.format("%2d", (monthDateView.getmSelMonth() + 1)).replace(" ", "0") + "-"
                        + String.format("%2d", (monthDateView.getmSelDay())).replace(" ", "0");
                Boolean ishas = false;
                for (int i = 0, len = list.size(); i < len; i++) {
                    if (list.get(i) == yearmonthday) {
                        list.remove(i);
                        --len;// 减少一个
                        --i;//
                        ishas = true;
                    }
                }
                //对list排序
                Collections.sort(list);
                if (!ishas) {
                     if (2 <= list.size()){
                         if (yearmonthday < list.get(0)){
                             list.remove(0);
                         }else{
                             list.remove(1);
                          }
                      }
                    list.add(yearmonthday);
                }
                Collections.sort(list);
                monthDateView.setDaysHasThingList(list);
                monthDateView.invalidate();
            }
        });

        ll_back.setOnClickListener(this);
        lastMonth.setOnClickListener(this);
        nextMonth.setOnClickListener(this);
        rightBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_back:
                scrollToFinishActivity();
                break;
            case R.id.mainUI_title_rightBtn:
                if (list.size() < 2){
                    Toast.makeText(context,"请选择开始于截止日期",Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent();
                String starttime = (""+list.get(0)).substring(0,4)+"-"+
                        (""+list.get(0)).substring(4,6)+"-"+(""+list.get(0)).substring(6,8);
                String endtime = (""+list.get(1)).substring(0,4)+"-"+
                        (""+list.get(1)).substring(4,6)+"-"+(""+list.get(1)).substring(6,8);
                intent.putExtra("starttime",starttime);
                intent.putExtra("endtime",endtime);
                setResult(0, intent);
                scrollToFinishActivity();
                break;
            case R.id.historyRoad_filtrate_lastMonth:
                monthDateView.onLeftClick();
                break;
            case R.id.historyRoad_filtrate_nextMonth:
                monthDateView.onRightClick();
                break;
        }
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
