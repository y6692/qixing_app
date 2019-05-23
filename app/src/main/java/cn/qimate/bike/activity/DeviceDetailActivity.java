package cn.qimate.bike.activity;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sofi.blelocker.library.Code;
import com.sofi.blelocker.library.connect.listener.BleConnectStatusListener;
import com.sofi.blelocker.library.connect.options.BleConnectOptions;
import com.sofi.blelocker.library.model.BleGattProfile;
import com.sofi.blelocker.library.protocol.ICloseListener;
import com.sofi.blelocker.library.protocol.IConnectResponse;
import com.sofi.blelocker.library.protocol.IEmptyResponse;
import com.sofi.blelocker.library.protocol.IGetGprsConfigResponse;
import com.sofi.blelocker.library.protocol.IGetRecordResponse;
import com.sofi.blelocker.library.protocol.IGetStatusResponse;
import com.sofi.blelocker.library.protocol.IQueryOpenStateResponse;
import com.sofi.blelocker.library.protocol.IQueryRunStateResponse;
import com.sofi.blelocker.library.protocol.ITemporaryActionResponse;
import com.sofi.blelocker.library.search.SearchRequest;
import com.sofi.blelocker.library.search.SearchResult;
import com.sofi.blelocker.library.search.response.SearchResponse;
import com.sofi.blelocker.library.utils.BluetoothLog;
import com.sofi.blelocker.library.utils.StringUtils;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.http.OkHttpClientManager;
import cn.http.ResultCallback;
import cn.http.rdata.RGetUserTradeStatus;
import cn.http.rdata.RRent;
import cn.http.rdata.RetData;
import cn.qimate.bike.R;
import cn.qimate.bike.lock.utils.Utils;
import cn.qimate.bike.util.Globals;
import cn.qimate.bike.util.UIHelper;
import okhttp3.Request;

import static com.sofi.blelocker.library.Constants.STATUS_CONNECTED;

/**
 * Created by heyong on 2017/5/19.
 */

public class DeviceDetailActivity extends Activity implements View.OnClickListener{
    private static final String TAG = DeviceDetailActivity.class.getSimpleName();
//    @BindView(R.id.layLock)
    RelativeLayout layLock;
//    @BindView(R.id.tvName)
    TextView tvName;
//    @BindView(R.id.tvState)
    TextView tvState;
//    @BindView(R.id.tvOpen)
    TextView tvOpen;
//    @BindView(R.id.tvLog)
//    TextView tvLog;

    Button btnQueryState;
    Button temporaryAction;

    private String mac, name;
    private boolean mConnected = false;
    private String version = "";   //硬件版本号

//    @Override
//    protected int getLayoutRes() {
//        return R.layout.ac_ui_device_detail;
//    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_ui_device_detail);
        ButterKnife.bind(this);

        layLock = findViewById(R.id.layLock);
        tvName = findViewById(R.id.tvName);
        tvState = findViewById(R.id.tvState);
        tvOpen = findViewById(R.id.tvOpen);
        btnQueryState = findViewById(R.id.btnQueryState);
        temporaryAction = findViewById(R.id.temporaryAction);

        layLock.setOnClickListener(this);
        btnQueryState.setOnClickListener(this);
        temporaryAction.setOnClickListener(this);

        bindData();
//        bindView();

//        setDisplayNone();

//        if(getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayShowTitleEnabled(false);
//            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//        }
//        setTitle("大门锁");
    }

//    @Override
    protected void bindData() {
//        if (getIntent() != null) {
//            mac = getIntent().getStringExtra("mac");
//            name = StringUtils.getBikeName(getIntent().getStringExtra("name"));
//        }

//        mac = "A4:34:F1:7B:BF:A9";
        mac = "A4:34:F1:7B:BF:9A";
        name = "GpDTxe7<a";

        ClientManager.getClient().registerConnectStatusListener(mac, mConnectStatusListener);
        ClientManager.getClient().notifyClose(mac, mCloseListener); //监听锁关闭事件

        OkHttpClientManager.getInstance().GetUserTradeStatus(new ResultCallback<RGetUserTradeStatus>() {
            @Override
            public void onResponse(RGetUserTradeStatus rGetUserTradeStatus) {
                if (rGetUserTradeStatus.getResult() < 0) {
                    UIHelper.showToast(DeviceDetailActivity.this, ""+rGetUserTradeStatus.getResult());
                } else {
                    RGetUserTradeStatus.ResultBean resultBean = rGetUserTradeStatus.getInfo();

                    Globals.bType = resultBean.getUserTradeStatus();
                    Globals.bikeNo = resultBean.getBikeNo();
                    if (Globals.bType == 1) {
                        tvOpen.setText("已开锁");
                        returnCar();
                    } else {
                        tvOpen.setText("开锁");
                        rentCar();
                    }
                }
            }

            @Override
            public void onError(Request request, Exception e) {
                UIHelper.showToast(DeviceDetailActivity.this, e.getMessage());
            }
        });
    }

//    @Override
//    protected void bindView() {
//        super.bindView();
//        setDisplayNone();
//        setTitle("大门锁");
//    }

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.mainUI_title_backBtn:
////                closeBle();
//
//                scrollToFinishActivity();
//                break;
//            default:
//                break;
//        }
//    }



//    @OnClick({R.id.layLock})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layLock :
                UIHelper.showProgress(this, "获取锁状态中");
                ClientManager.getClient().getStatus(mac, new IGetStatusResponse() {
                    @Override
                    public void onResponseSuccess(String version, String keySerial, String macKey, String vol) {
                        UIHelper.dismiss();
                        queryStatusServer(version, keySerial, macKey, vol);
                    }

                    @Override
                    public void onResponseFail(int code) {
                        Log.e(TAG, Code.toString(code));
                        UIHelper.dismiss();
                        UIHelper.showToast(DeviceDetailActivity.this, Code.toString(code));
                    }

                });
                break;

            case R.id.btnQueryState:
                queryOpenState();
                break;
            case R.id.temporaryAction:
                temporaryAction();
                break;
//            case R.id.btnScan:
//                ClientManager.getClient().disconnect(mac);
//                break;
//            case R.id.btnBuzzer :
//                buzzer();
//                break;
//            case R.id.btnQueryOpenState :
//                queryOpenState();
//                break;
//            case R.id.btnQueryRunState :
//                queryRunState();
//                break;
//            case R.id.btnGetConfig :
//                getGprsConfig();
//                break;
//            case R.id.btnGetRecord :
//                getBleRecord();
//                break;
//            case R.id.btnLowPacketEnable :
//                enableLowPower();
//                break;
//            case R.id.btnLowPacketDisable :
//                disableLowPower();
//                break;
//            case R.id.btnUpdatekey :
//                requestKeyServer(name);
//                break;
//            case R.id.btnExitFactory :
//                exitFactory();
//                break;
        }
    }

    private void refreshData(boolean refresh) {
        if (refresh) {
            tvState.setText("");
            tvName.setText(Globals.BLE_NAME);
            layLock.setClickable(true);
        } else {
            tvState.setText("开始蓝牙扫描");
            tvName.setText("");
            layLock.setClickable(false);
        }
    }

    //连接设备
    private void connectDevice() {
        BleConnectOptions options = new BleConnectOptions.Builder()
                .setConnectRetry(3)
                .setConnectTimeout(20000)
                .setServiceDiscoverRetry(1)
                .setServiceDiscoverTimeout(10000)
                .setEnableNotifyRetry(1)
                .setEnableNotifyTimeout(10000)
                .build();

        ClientManager.getClient().connect(mac, options, new IConnectResponse() {
            @Override
            public void onResponseFail(int code) {
                Log.e(TAG, Code.toString(code));
                UIHelper.showToast(DeviceDetailActivity.this, Code.toString(code));
            }

            @Override
            public void onResponseSuccess(BleGattProfile profile) {
                BluetoothLog.v(String.format("profile:\n%s", profile));
                refreshData(true);

                if (Globals.bType == 1) {
                    UIHelper.showProgress(DeviceDetailActivity.this, "正在关锁中");
                    getBleRecord();
                }
            }
        });
    }

    private void connectDeviceIfNeeded() {
        if (!mConnected) {
            connectDevice();
        } else {
            ClientManager.getClient().stopSearch();
        }
    }

    //扫描结果
    private final SearchResponse mSearchResponse = new SearchResponse() {
        @Override
        public void onSearchStarted() {
            Log.e("===","DeviceDetailActivity.onSearchStarted");
        }

        @Override
        public void onDeviceFounded(SearchResult device) {
            Log.e("===","DeviceDetailActivity.onDeviceFounded " + device.device.getAddress());
            if (device.getAddress().contains(mac)) {
                ClientManager.getClient().stopSearch();
                connectDeviceIfNeeded();
            }
        }

        @Override
        public void onSearchStopped() {
            Log.e("===","DeviceDetailActivity.onSearchStopped");

        }

        @Override
        public void onSearchCanceled() {
            Log.e("===","DeviceDetailActivity.onSearchCanceled");

        }
    };

    //扫描，0是一直扫描
    private void searchDevice() {
        SearchRequest request = new SearchRequest.Builder()
                .searchBluetoothLeDevice(0).build();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        ClientManager.getClient().search(request, mSearchResponse);
    }

    //租车，开启扫描
    private void rentCar() {
        connectDeviceIfNeeded();
    }

    //还车，支持长连接不断开，不支持断开扫描
    private void returnCar() {
        if(Globals.isBleConnected && StringUtils.isNotEmpty(version) && StringUtils.supportLongConnected(version)) {

        }
        else {
            ClientManager.getClient().disconnect(mac);
            searchDevice();
        }
    }

    //获取服务器的加密信息
    private void queryStatusServer(String version, String keySerial, String macKey, String vol) {
        Log.e(TAG, "version:" + version + " keySerial:" + keySerial + " macKey:" + macKey + " vol:" + vol);
        this.version = version;
        int timestamp = (int) StringUtils.getCurrentTimestamp();

        UIHelper.showProgress(this, "get_bike_server");
        OkHttpClientManager.getInstance().Rent(macKey, keySerial, timestamp, new ResultCallback<RRent>() {

            @Override
            public void onResponse(RRent rRent) {
                UIHelper.dismiss();
                if (rRent.getResult() >= 0) {
                    RRent.ResultBean resultBean = rRent.getInfo();
                    openBleLock(resultBean);
                }
                else {
                    UIHelper.showToast(DeviceDetailActivity.this, ""+rRent.getResult());
                }
            }

            @Override
            public void onError(Request request, Exception e) {
                UIHelper.dismiss();
                UIHelper.showToast(DeviceDetailActivity.this, e.getMessage());
            }

        });
    }

    //与设备，开锁
    private void openBleLock(RRent.ResultBean resultBean) {
        UIHelper.showProgress(this, "open_bike_status");
//        ClientManager.getClient().openLock(mac, "18112348925", resultBean.getServerTime(),
        ClientManager.getClient().openLock(mac,"000000000000", resultBean.getServerTime(),
                resultBean.getKeys(), resultBean.getEncryptionKey(), new IEmptyResponse(){
                    @Override
                    public void onResponseFail(int code) {
                        Log.e(TAG, Code.toString(code));
                        UIHelper.dismiss();
                        UIHelper.showToast(DeviceDetailActivity.this, Code.toString(code));
                    }

                    @Override
                    public void onResponseSuccess() {
                        UIHelper.dismiss();
                        getBleRecord();
                    }
                });
    }

    //与设备，临时停车
    private void temporaryAction() {
        UIHelper.showProgress(this, "temporaryAction");
//        ClientManager.getClient().temporaryAction(mac, "18112348925", new ITemporaryActionResponse() {
        ClientManager.getClient().temporaryAction(mac, "000000000000", new ITemporaryActionResponse() {
            @Override
            public void onResponseSuccess() {
                UIHelper.dismiss();
                UIHelper.showToast(DeviceDetailActivity.this, "临时停车成功");
            }

            @Override
            public void onResponseFail(int code) {
                Log.e(TAG, Code.toString(code));
                UIHelper.dismiss();
                UIHelper.showToast(DeviceDetailActivity.this, Code.toString(code));
            }
        });
    }

    //与设备，获取记录
    private void getBleRecord() {
        UIHelper.showProgress(this, "get_bike_record");
        ClientManager.getClient().getRecord(mac, new IGetRecordResponse() {
            @Override
            public void onResponseSuccess(String phone, String bikeTradeNo, String timestamp,
                                          String transType, String mackey, String index, String cap, String vol) {
                UIHelper.dismiss();
                uploadRecordServer(phone, bikeTradeNo, timestamp, transType, mackey, index, cap, vol);
            }

            @Override
            public void onResponseSuccessEmpty() {
                UIHelper.dismiss();
                UIHelper.showToast(DeviceDetailActivity.this, "record empty");
            }

            @Override
            public void onResponseFail(int code) {
                Log.e(TAG, Code.toString(code));
                UIHelper.dismiss();
                UIHelper.showToast(DeviceDetailActivity.this, Code.toString(code));
            }
        });
    }

    //与服务器，上传记录
    private String tradeNo = "";
    private void uploadRecordServer(String phone, String bikeTradeNo, String timestamp, String transType,
                                    String mackey, String index, String cap, String vol) {
        UIHelper.showProgress(this, "upload_record_server");
        tradeNo = bikeTradeNo;
        OkHttpClientManager.getInstance().BikeTradeRecord(phone, StringUtils.decodeTradeNo(bikeTradeNo),
                timestamp, transType, mackey, index, cap, vol, "", "", new ResultCallback<RetData>() {
                    @Override
                    public void onResponse(RetData retData) {
                        UIHelper.dismiss();
                        if (retData.getResult() >= 0) {
                            deleteBleRecord();
                        }
                        else {
                            UIHelper.showToast(DeviceDetailActivity.this, ""+retData.getResult());
                        }
                    }

                    @Override
                    public void onError(Request request, Exception e) {
                        UIHelper.dismiss();
                        UIHelper.showToast(DeviceDetailActivity.this, e.getMessage());
                    }
                });
    }

    //与设备，删除记录
    private void deleteBleRecord() {
        UIHelper.showProgress(this, "delete_bike_record");
        ClientManager.getClient().deleteRecord(mac, tradeNo, new IGetRecordResponse() {
            @Override
            public void onResponseSuccess(String phone, String bikeTradeNo, String timestamp, String transType, String mackey, String index, String cap, String vol) {
                UIHelper.dismiss();
                uploadRecordServer(phone, bikeTradeNo, timestamp, transType, mackey, index, cap, vol);
            }

            @Override
            public void onResponseSuccessEmpty() {
                UIHelper.dismiss();

                if(Globals.bType == 1) {
                    Globals.bType = 0;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvOpen.setText("开锁");
                        }
                    });
                    rentCar();
                }
                else {
                    Globals.bType = 1;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvOpen.setText("已开锁");
                        }
                    });
                    returnCar();
                }
            }

            @Override
            public void onResponseFail(int code) {
                Log.e(TAG, Code.toString(code));
                UIHelper.dismiss();
                UIHelper.showToast(DeviceDetailActivity.this, Code.toString(code));
            }
        });
    }

//    //与设备，蜂鸣器响
//    private void buzzer() {
//        UIHelper.showProgress(this, buzzer);
//        ClientManager.getClient().buzzer(mac, new IEmptyResponse(){
//                    @Override
//                    public void onResponseFail(int code) {
//                        LOG.E(TAG, Code.toString(code));
//                        UIHelper.dismiss();
//                        UIHelper.showToast(DeviceDetailActivity.this, Code.toString(code));
//                    }
//
//                    @Override
//                    public void onResponseSuccess() {
//                        UIHelper.dismiss();
//                        appendLog(getString(R.string.buzzer) + getString(R.string.success));
//                    }
//                });
//    }
//
    //与设备，校准锁开关状态
    private void queryOpenState() {
        UIHelper.showProgress(this, "collectState");
        ClientManager.getClient().queryOpenState(mac, new IQueryOpenStateResponse() {
            @Override
            public void onResponseSuccess(boolean open) {
                UIHelper.dismiss();

                Log.e("queryOpenState===", "===="+open);

//                if(open) {
//                    Globals.bType = 1;
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            tvOpen.setText("已开锁");
//                        }
//                    });
//                }
//                else {
//                    Globals.bType = 0;
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            tvOpen.setText("开锁");
//                        }
//                    });
//                }
            }

            @Override
            public void onResponseFail(int code) {
                Log.e(TAG, Code.toString(code));
                UIHelper.dismiss();
                UIHelper.showToast(DeviceDetailActivity.this, Code.toString(code));
            }
        });
    }
//
//    //与设备，获取锁运行状态
//    private void queryRunState() {
//        UIHelper.showProgress(this, R.string.runState);
//        ClientManager.getClient().queryRunState(mac, new IQueryRunStateResponse() {
//            @Override
//            public void onResponseSuccess(String time, String vol, boolean elec, boolean buzzer, int recordNum) {
//                LOG.E(TAG, "time:" + time + " vol:" + vol + " elec:" + elec + " buzzer:" + buzzer +
//                        " recordNum:" + recordNum);
//                UIHelper.dismiss();
//                RunState runState = new RunState(time, vol, elec, buzzer, recordNum);
//                appendLog(runState.toString());
//            }
//
//            @Override
//            public void onResponseFail(int code) {
//                LOG.E(TAG, Code.toString(code));
//                UIHelper.dismiss();
//                UIHelper.showToast(DeviceDetailActivity.this, Code.toString(code));
//            }
//        });
//    }
//
//    //与设备，获取锁GPRS配置
//    private void getGprsConfig() {
//        UIHelper.showProgress(this, R.string.getGprsConfig);
//        ClientManager.getClient().getGprsConfig(mac, new IGetGprsConfigResponse() {
//            @Override
//            public void onResponseSuccess(String ip, String path, String port, String apn, String ping,
//                                          String gps) {
//                LOG.E(TAG, "ip:" + ip + " path:" + path + " port:" + port + " apn:" + apn +
//                        " ping:" + ping + " gps:" + gps);
//                UIHelper.dismiss();
//                GprsConfig gprsConfig = new GprsConfig(ip, path, port, apn, ping, gps);
//                appendLog(gprsConfig.toString());
//            }
//
//            @Override
//            public void onResponseFail(int code) {
//                LOG.E(TAG, Code.toString(code));
//                UIHelper.dismiss();
//                UIHelper.showToast(DeviceDetailActivity.this, Code.toString(code));
//            }
//        });
//    }
//
//    //与设备，启用低功耗模式
//    private void enableLowPower() {
//        UIHelper.showProgress(this, R.string.enableLowPower);
//        ClientManager.getClient().lowPower(mac, true, new IEmptyResponse() {
//            @Override
//            public void onResponseSuccess() {
//                UIHelper.dismiss();
//                appendLog(getString(R.string.enableLowPower) + getString(R.string.success));
//            }
//
//            @Override
//            public void onResponseFail(int code) {
//                LOG.E(TAG, Code.toString(code));
//                UIHelper.dismiss();
//                UIHelper.showToast(DeviceDetailActivity.this, Code.toString(code));
//            }
//        });
//    }
//
//    //与设备，禁用低功耗模式
//    private void disableLowPower() {
//        UIHelper.showProgress(this, R.string.disableLowPower);
//        ClientManager.getClient().lowPower(mac, false, new IEmptyResponse() {
//            @Override
//            public void onResponseSuccess() {
//                UIHelper.dismiss();
//                appendLog(getString(R.string.disableLowPower) + getString(R.string.success));
//            }
//
//            @Override
//            public void onResponseFail(int code) {
//                LOG.E(TAG, Code.toString(code));
//                UIHelper.dismiss();
//                UIHelper.showToast(DeviceDetailActivity.this, Code.toString(code));
//            }
//        });
//    }
//
//    //与服务器，请求更新秘钥
//    private String lockNo = "";
//    private void requestKeyServer(String lockno) {
//        UIHelper.showProgress(this, R.string.request_key_server);
//        lockNo = lockno;
//        OkHttpClientManager.getInstance().CourtRequestUpdate(lockno, new ResultCallback<RCourtCommitUpdate>() {
//                    @Override
//                    public void onResponse(RCourtCommitUpdate rCourtCommitUpdate) {
//                        UIHelper.dismiss();
//                        if (rCourtCommitUpdate.getResult() >= 0) {
//                            RCourtCommitUpdate.InfoBean infoBean = rCourtCommitUpdate.getInfo();
//                            updateKey(infoBean.getNewkey(), infoBean.getOldkey(), infoBean.getIndex());
//                        }
//                        else {
//                            UIHelper.showToast(DeviceDetailActivity.this, Utils.toString(rCourtCommitUpdate.getResult()));
//                        }
//                    }
//
//                    @Override
//                    public void onError(Request request, Exception e) {
//                        UIHelper.dismiss();
//                        UIHelper.showToast(DeviceDetailActivity.this, e.getMessage());
//                    }
//                });
//    }
//
//    //与服务器，确认更新秘钥
//    private void commitKeyServer(String lockno, boolean success) {
//        UIHelper.showProgress(this, R.string.commit_key_server);
//        OkHttpClientManager.getInstance().CourtCommitUpdate(lockno, success, new ResultCallback<RetData>() {
//            @Override
//            public void onResponse(RetData retData) {
//                UIHelper.dismiss();
//                if (retData.getResult() >= 0) {
//                    appendLog(getString(R.string.updateKey) + getString(R.string.success));
//                }
//                else {
//                    UIHelper.showToast(DeviceDetailActivity.this, Utils.toString(retData.getResult()));
//                }
//            }
//
//            @Override
//            public void onError(Request request, Exception e) {
//                UIHelper.dismiss();
//                UIHelper.showToast(DeviceDetailActivity.this, e.getMessage());
//            }
//        });
//    }
//
//    //与设备，更新秘钥
//    private void updateKey(String newKey, String oldKey, int indexKey) {
//        UIHelper.showProgress(this, R.string.updateKey);
//        ClientManager.getClient().updateKey(mac, newKey, oldKey, indexKey, new IEmptyResponse() {
//            @Override
//            public void onResponseSuccess() {
//                UIHelper.dismiss();
//                commitKeyServer(lockNo, true);
//            }
//
//            @Override
//            public void onResponseFail(int code) {
//                LOG.E(TAG, Code.toString(code));
//                UIHelper.dismiss();
//                UIHelper.showToast(DeviceDetailActivity.this, Code.toString(code));
//                commitKeyServer(lockNo, false);
//            }
//        });
//    }
//
//    //与设备，退出工厂模式
//    private void exitFactory() {
//        UIHelper.showProgress(this, R.string.exitFactory);
//        ClientManager.getClient().exitFactory(mac, new IEmptyResponse() {
//            @Override
//            public void onResponseSuccess() {
//                UIHelper.dismiss();
//                appendLog(getString(R.string.exitFactory) + getString(R.string.success));
//            }
//
//            @Override
//            public void onResponseFail(int code) {
//                LOG.E(TAG, Code.toString(code));
//                UIHelper.dismiss();
//                UIHelper.showToast(DeviceDetailActivity.this, Code.toString(code));
//            }
//        });
//    }
//
//    private void appendLog(final String str) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                String text = tvLog.getText().toString();
//                tvLog.setText(text +"\n"+str);
//
//                int offset = tvLog.getLineCount() * tvLog.getLineHeight();
//                if(offset >tvLog.getHeight()) {
//                    tvLog.scrollTo(0, offset - tvLog.getHeight());
//                }
//            }
//        });
//    }

//    R.id.btnScan, R.id.btnBuzzer, R.id.btnQueryOpenState, R.id.btnQueryRunState,
//    R.id.btnGetConfig, R.id.btnGetRecord, R.id.btnLowPacketEnable, R.id.btnLowPacketDisable,
//    R.id.btnUpdatekey, R.id.btnExitFactory





    //监听锁关闭事件
    private final ICloseListener mCloseListener = new ICloseListener() {
        @Override
        public void onNotifyClose() {
            Log.e("onNotifyClose===", "====");

            BluetoothLog.v(String.format(Locale.getDefault(), "DeviceDetailActivity onNotifyClose"));
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvOpen.setText("开锁");
                }
            });
            getBleRecord();
        }
    };

    //监听当前连接状态
    private final BleConnectStatusListener mConnectStatusListener = new BleConnectStatusListener() {
        @Override
        public void onConnectStatusChanged(String mac, int status) {
            BluetoothLog.v(String.format(Locale.getDefault(), "DeviceDetailActivity onConnectStatusChanged %d in %s",
                    status, Thread.currentThread().getName()));

            Log.e("ConnectStatus===", "===="+(status == STATUS_CONNECTED));

            Globals.isBleConnected = mConnected = (status == STATUS_CONNECTED);
            refreshData(mConnected);
            connectDeviceIfNeeded();
        }
    };

    @Override
    protected void onDestroy() {
        ClientManager.getClient().stopSearch();
        ClientManager.getClient().disconnect(mac);
        ClientManager.getClient().unnotifyClose(mac, mCloseListener);
        ClientManager.getClient().unregisterConnectStatusListener(mac, mConnectStatusListener);
        super.onDestroy();
    }

}
