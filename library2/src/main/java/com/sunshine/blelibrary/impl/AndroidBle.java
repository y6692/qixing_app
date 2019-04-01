package com.sunshine.blelibrary.impl;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.sunshine.blelibrary.config.Config;
import com.sunshine.blelibrary.dispose.impl.Battery;
import com.sunshine.blelibrary.dispose.impl.CloseLock;
import com.sunshine.blelibrary.dispose.impl.LockResult;
import com.sunshine.blelibrary.dispose.impl.LockStatus;
import com.sunshine.blelibrary.dispose.impl.OpenLock;
import com.sunshine.blelibrary.dispose.impl.TY;
import com.sunshine.blelibrary.dispose.impl.Token;
import com.sunshine.blelibrary.inter.IBLE;
import com.sunshine.blelibrary.inter.OnConnectionListener;
import com.sunshine.blelibrary.inter.OnDeviceSearchListener;
import com.sunshine.blelibrary.mode.BatteryTxOrder;
import com.sunshine.blelibrary.mode.GetLockStatusTxOrder;
import com.sunshine.blelibrary.mode.GetTokenTxOrder;
import com.sunshine.blelibrary.mode.OpenLockTxOrder;
import com.sunshine.blelibrary.mode.TxOrder;
import com.sunshine.blelibrary.mode.resetLockTxOrder;
import com.sunshine.blelibrary.utils.ConvertUtils;
import com.sunshine.blelibrary.utils.EncryptUtils;
import com.sunshine.blelibrary.utils.GlobalParameterUtils;
import com.sunshine.blelibrary.utils.Logger;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.sunshine.blelibrary.utils.EncryptUtils.Encrypt;

/**
 * 作者：LiZhao
 * 时间：2017.2.8 11:48
 * 邮箱：44493547@qq.com
 * 备注：
 */
public class AndroidBle implements IBLE {

    private static final String TAG = AndroidBle.class.getSimpleName();
    private Context context;
    private BluetoothAdapter mBluetoothAdapter;
    private OnDeviceSearchListener mOnDeviceSearchListener;
    private BluetoothGatt mBluetoothGatt;
    private boolean CONNECT_STATUS = false;
    private Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    mHandler.removeMessages(1);
                    mHandler.sendEmptyMessageDelayed(1,5000);
                    break;
                case 1:
                    if (null!=mOnConnectionListener){
                        mOnConnectionListener.onTimeOut();
                    }
                    break;
            }
        }
    };
    private OnConnectionListener mOnConnectionListener;
    private BluetoothGattCharacteristic read_characteristic;
    private BluetoothGattCharacteristic write_characteristic;
    private List<BluetoothDevice> bluetoothDeviceList = new ArrayList<>();
    private Token mToken;
    private BluetoothManager bluetoothManager;
    private TxOrder mSendTxOrder;

    public AndroidBle(Context context) {
        this.context = context;
        bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            return;
        }
        GlobalParameterUtils.getInstance().setContext(context.getApplicationContext());
        mToken = new Token();
        Battery battery = new Battery();
        OpenLock openLock = new OpenLock();
        TY ty = new TY();
        CloseLock closeLock = new CloseLock();
        LockStatus lockStatus = new LockStatus();
        LockResult lockResult = new LockResult();

        mToken.nextHandler = battery;
        battery.nextHandler = openLock;
        openLock.nextHandler = ty;
        ty.nextHandler = closeLock;
        closeLock.nextHandler = lockStatus;
        lockStatus.nextHandler = lockResult;
    }

    @Override
    public boolean enableBluetooth() {
        return mBluetoothAdapter.enable();
    }

    @Override
    public boolean disableBluetooth() {
        return mBluetoothAdapter.disable();
    }

    @Override
    public boolean isEnable() {
        return mBluetoothAdapter.isEnabled();
    }

    @Override
    public void startScan(OnDeviceSearchListener onDeviceSearchListener) {
        bluetoothDeviceList.clear();
        if (mBluetoothAdapter == null) return;
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
        this.mOnDeviceSearchListener = onDeviceSearchListener;
        mBluetoothAdapter.startLeScan(mLeScanCallback);
    }

    @Override
    public void stopScan() {
        if (mBluetoothAdapter == null) return;
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
    }

    @Override
    public boolean connect(String address, OnConnectionListener onConnectionListener) {
        if (TextUtils.isEmpty(address) || mBluetoothAdapter == null || null == onConnectionListener)
            return false;
        this.mOnConnectionListener = onConnectionListener;
        BluetoothDevice bluetoothDevice = mBluetoothAdapter.getRemoteDevice(address);
        if (null == bluetoothDevice) {
            return false;
        }
        if (mBluetoothGatt != null) {
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
        mBluetoothGatt = bluetoothDevice.connectGatt(context, false, mBluetoothGattCallback);
        return mBluetoothGatt.connect();
    }

    @Override
    public boolean connectDevice(BluetoothDevice device, OnConnectionListener onConnectionListener) {
        if (device == null || onConnectionListener == null) return false;
        this.mOnConnectionListener = onConnectionListener;
        if (mBluetoothGatt != null) {
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
        mBluetoothGatt = device.connectGatt(context, false, mBluetoothGattCallback);
        return mBluetoothGatt.connect();
    }



    @Override
    public boolean getToken() {
        return writeObject(new GetTokenTxOrder());
    }

    @Override
    public boolean getBattery() {
        return writeObject(new BatteryTxOrder());
    }

    @Override
    public boolean openLock() {
        return writeObject(new OpenLockTxOrder());
    }

    @Override
    public boolean resetLock() {
        return writeObject(new resetLockTxOrder());
    }

    @Override
    public boolean getLockStatus() {
        return writeObject(new GetLockStatusTxOrder());
    }

    @Override
    public void disconnect() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.disconnect();

    }

    @Override
    public BluetoothAdapter getBluetoothAdapter() {
        if (mBluetoothAdapter==null){
            bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bluetoothManager.getAdapter();
        }
        return mBluetoothAdapter;
    }

    @Override
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;

    }

    @Override
    public boolean getConnectStatus() {
        return CONNECT_STATUS;
    }

    @Override
    public boolean refreshCache() {
        if (mBluetoothGatt != null) {
            try {
                BluetoothGatt localBluetoothGatt = mBluetoothGatt;
                Method localMethod = localBluetoothGatt.getClass().getMethod("refresh", new Class[0]);
                if (localMethod != null) {
                    boolean bool = ((Boolean) localMethod.invoke(localBluetoothGatt, new Object[0])).booleanValue();
                    return bool;
                }
            } catch (Exception localException) {
                localException.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public void resetBluetoothAdapter() {
        mBluetoothAdapter.disable();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBluetoothAdapter.enable();
            }
        }, 2000);
    }


    /**
     * 写入指令
     *
     * @param txOrder 发送指令对象
     * @return 是否成功
     */
    private boolean writeObject(TxOrder txOrder) {
        if (mBluetoothGatt == null || write_characteristic == null) {
            return false;
        }
        byte[] miwen = Encrypt(ConvertUtils.hexString2Bytes(txOrder.generateString()), Config.key);
        if (miwen != null) {
            write_characteristic.setValue(miwen);
            mHandler.removeMessages(0);
            mHandler.sendEmptyMessage(0);
            mSendTxOrder = txOrder;
            Logger.e("发送：", txOrder.generateString());
            return mBluetoothGatt.writeCharacteristic(write_characteristic);
        }
        return false;
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
            if (null != mOnDeviceSearchListener) {
                mOnDeviceSearchListener.onScanDevice(device, rssi, scanRecord);
            }
        }
    };
    private BluetoothGattCallback mBluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (status == 133) {
                CONNECT_STATUS = false;
                gatt.disconnect();
                connectDevice(gatt.getDevice(),mOnConnectionListener);
            }

            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    gatt.discoverServices();
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    CONNECT_STATUS = false;
                    if (null != mOnConnectionListener) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mOnConnectionListener.onDisconnect(Config.DISCONNECT);
                            }
                        });
                    }
                    gatt.close();
                    break;
            }
        }

        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                BluetoothGattService service = gatt.getService(Config.bltServerUUID);
                if (null != service) {
                    read_characteristic = service.getCharacteristic(Config.readDataUUID);
                    write_characteristic = service.getCharacteristic(Config.writeDataUUID);
                    int properties = read_characteristic.getProperties();
                    if ((properties | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                        gatt.setCharacteristicNotification(read_characteristic, true);
                        BluetoothGattDescriptor descriptor = read_characteristic.getDescriptor(Config.CLIENT_CHARACTERISTIC_CONFIG);
                        if (null != descriptor) {
                            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            gatt.writeDescriptor(descriptor);
                        }
                    }
                }
                if (null != mOnConnectionListener) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            CONNECT_STATUS = true;
                            mOnConnectionListener.onServicesDiscovered(TextUtils.isEmpty(gatt.getDevice().getName()) ? "null" : gatt.getDevice().getName(), gatt.getDevice().getAddress());
                        }
                    });
                }
            }
            super.onServicesDiscovered(gatt, status);
        }


        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            mHandler.removeMessages(1);
            byte mingwen[] = null;
            try {
                byte[] values = characteristic.getValue();
                byte[] x = new byte[16];
                System.arraycopy(values, 0, x, 0, 16);
                mingwen = EncryptUtils.Decrypt(x, Config.key);
                mToken.handlerRequest(ConvertUtils.bytes2HexString(mingwen));
                Logger.e(TAG, "返回：" + ConvertUtils.bytes2HexString(mingwen));
            } catch (Exception e) {
                Logger.e(TAG, "没有该指令：" + ConvertUtils.bytes2HexString(mingwen));
            }
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            GlobalParameterUtils.getInstance().setBusy(false);
        }
    };

}
