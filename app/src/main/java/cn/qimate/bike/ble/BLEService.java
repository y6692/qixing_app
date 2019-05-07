package cn.qimate.bike.ble;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


import java.nio.ByteOrder;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import cn.qimate.bike.base.BaseFragmentActivity;
import cn.qimate.bike.util.ByteUtil;
import cn.qimate.bike.util.IoBuffer;


/**
 * 中心设备扫描后 获取device直接连接
 */
@SuppressLint("NewApi")
public class BLEService {
	public Context view;
	public static BluetoothAdapter bluetoothAdapter;
	BluetoothGatt bluetoothGatt;
	BluetoothGattService gattService;
	BluetoothGattCharacteristic characteristic;
	AtomicInteger connectstate = new AtomicInteger();
	String address;
	Handler handler  = new Handler();
	public boolean showValue  = false;
	CountDownLatch cdl = null;
	private AtomicInteger cnt = new AtomicInteger();

	public static final UUID serviceuuid= UUID.fromString("0000ff00-0000-1000-8000-00805f9b34fb");
	public static final UUID chracteruuid= UUID.fromString("0000ff01-0000-1000-8000-00805f9b34fb");
	public static final UUID CLIENT_CHARACTERISTIC_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
	
	private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {

		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
			if (newState == BluetoothProfile.STATE_CONNECTED) { // 连接
				//sleep(300);
				connectstate.incrementAndGet();
				bluetoothGatt.discoverServices();
				handler.post(new Runnable() {
					@Override
					public void run() {
//						TextView textView = ((TextView)((BaseActivity)view).findViewById(R.id.readvalue));
//						textView.setText("connection");
					}
				});
			} else if (newState == BluetoothProfile.STATE_DISCONNECTED) { // 断开
				BLEService.this.disconnect();
				handler.post(new Runnable() {
					@Override
					public void run() {
//						TextView textView = ((TextView)((BaseActivity)view).findViewById(R.id.readvalue));
//						textView.setText("disconnect");
					}
				});
				ByteUtil.log("cnt.get()====="+cnt.get());
                System.out.println();
                if (cnt.get()<3){
					BLEService.this.connect(address);
					cnt.incrementAndGet();
				}

			}
		}


		
		@Override
		public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
			super.onMtuChanged(gatt, mtu, status);
			if (status == BluetoothGatt.GATT_SUCCESS) {   //修改发送 字节数
				System.out.println("MTUSize====" + mtu);
		    }  
		};

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				gattService = bluetoothGatt.getService(serviceuuid);
				if (gattService != null) {
					connectstate.incrementAndGet();
					ByteUtil.log("发现service服务======"+gattService);
					characteristic = gattService.getCharacteristic(chracteruuid);
					// 设置 不接收 返回需要 硬件端支持,默认不设置
					//characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
					setCharacteristicNotify(true);
					cnt.set(0);
					ByteUtil.log("发现characteristic服务====="+characteristic);

					if(showValue){
						handler.post(new Runnable() {

							@Override
							public void run() {
								//Toast.makeText(view, "发现characteristic服" , Toast.LENGTH_SHORT).show();
								if(view instanceof BaseFragmentActivity){
									BaseFragmentActivity view = (BaseFragmentActivity)BLEService.this.view;
//									view.device_text.setText(address);
//									view.connected= true;
//									view.invalidateOptionsMenu();
									view.oncall();
								}
							}
						});
					}
					
				}
			} else {

			}
		}

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
			System.out.println("onCharacteristicRead");
			if (status == BluetoothGatt.GATT_SUCCESS) {
				System.out.println("readVaule====" + IoBuffer.toHexArray(characteristic.getValue()));
			}
		}

		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
			super.onCharacteristicWrite(gatt, characteristic, status);
			cdl.countDown();
			/*if(Build.VERSION.SDK_INT>Build.VERSION_CODES.KITKAT_WATCH){
				// 5.0以上设置等待  15-30
				sleep(15);
			}else if(Build.VERSION.SDK_INT>Build.VERSION_CODES.M){
				// 6.0以上设置等待  100以上
				sleep(100);
			}*/

		};

		private IoBuffer ioBuffer = IoBuffer.allocate(258);

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
			if (characteristic.getValue() != null) {
				byte [] value = characteristic.getValue();
				ByteUtil.log("onCharacteristicChanged-->" + IoBuffer.toHexArray(value));
				if(ioBuffer.remaining() < value.length){
					ioBuffer.buf.clear();
				}
				ioBuffer.writeBytes(value);

				final byte[] data =  readBytebuf();
				if(data == null) return;
//				handler.post(new Runnable() {
//					@Override
//					public void run() {
//						TextView textView = ((TextView)((BaseActivity)view).findViewById(R.id.readvalue));
//						textView.setText(ioBuffer.toHexArray(data));
//					}
//				});
			}
			sleep(30);
		}

		@Override
		public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
			System.out.println("onDescriptorWriteonDescriptorWrite = " + status + ", descriptor =" + descriptor.getUuid().toString());
		}

		@Override
		public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
			System.out.println("rssi = " + rssi);
		}

		/* 按协议解析 */
		public byte[] readBytebuf(){
			ioBuffer.markWriteIndex();
			ioBuffer.flip();
			int bytesize = ioBuffer.readByte();
			if (ioBuffer.remaining() < bytesize) {
				ioBuffer.resetWriteIndex();
				return null;
			}

			final byte[] data = new byte[bytesize];
			ioBuffer.readBytes(data, 0, bytesize);
			ioBuffer.compact();
			parseB3(data);
			//postBleData(bytesize, data);
			return data;
		}
	};

	void parseB3(byte[] data){
		ByteUtil.log("B3=="+ByteUtil.toByteArray(data));
		if(data[0] == (byte) 0xB3){
			IoBuffer io = IoBuffer.wrap(data);
			io.buf.order(ByteOrder.LITTLE_ENDIAN);
			byte[] b =  new byte[15];
			io.readBytes(b);
			final int i = io.readShort();
			handler.post(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(view, "当前检测电压值: "+ i, Toast.LENGTH_SHORT).show();
				}
			});
		}
	}

	void postBleData(int size, byte[] bytes){
		IoBuffer postdate = IoBuffer.allocate(size+1);
		postdate.writeByte(size);
		postdate.writeBytes(bytes);
		postdate.flip();
		byte[] bb = postdate.array();
		ByteUtil.log("postbledata==>"+ByteUtil.toByteArray(bb));
//		ApiService.postbledata(bb, new HttpCallback() {
//			@Override
//			public void onSuccess(String t) {
//				ByteUtil.log(t);
//			}
//		});
	}


	public void sleep(long million){
		try {
			Thread.sleep(million);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void write(byte[] b){
		String s = ByteUtil.toByteArray(b);
		System.out.println(s);
		final long a = System.currentTimeMillis();
		if(characteristic!=null){
			characteristic.setValue(b);
			bluetoothGatt.writeCharacteristic(characteristic);
			ByteUtil.log("writebuf-->"+IoBuffer.toHexArray(b));
			try {
				cdl = new CountDownLatch(1);
				boolean  flag = cdl.await(200, TimeUnit.MILLISECONDS);
				if(!flag){
					ByteUtil.log("cdl.await=超时=");
				}
			}catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}
	
	public void write(IoBuffer buffer){
		final long a = System.currentTimeMillis();
		System.out.println(ByteUtil.toByteArray(buffer.array()));
		if(buffer.remaining()<20){
			write(buffer.readableBytes());
			return;
		}
		int length = (int)Math.ceil((double)buffer.remaining()/20) ;
		byte [] bb = new byte[20];
		for(int i=1; i<=length; i++){
			if(length == i){
				bb = new byte[buffer.remaining()];
				buffer.readBytes(bb);
				write(bb);
				//Toast.makeText(view, "all"+(System.currentTimeMillis()-a) , Toast.LENGTH_SHORT).show();
				return;
			}
			buffer.readBytes(bb);
			write(bb);
		}
	}
	
	 /**
     * Enables or disables notification on a give characteristic.
     * 设置通知后 才能在  onCharacteristicChanged 收到设备返回的消息
     * @param enabled If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotify(boolean enabled) {
		if (bluetoothAdapter == null || bluetoothGatt == null) {
            Log.w("BLEService", "BluetoothAdapter not initialized");
            return;
        }
        bluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        // This is specific to Heart Rate Measurement.
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG);
        if(descriptor != null ){
        	 descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
             bluetoothGatt.writeDescriptor(descriptor);
        }
       
    }
	
	
	@Deprecated
	public class LocalBinder extends Binder {
		BLEService getService() {
			return BLEService.this;
		}
	}
	
	public boolean connect(String address){
		if(connectstate.get() > 0) return false;
		this.address = address;
		if(bluetoothAdapter == null){
			Toast.makeText(view, "bluetoothAdapter is null" , Toast.LENGTH_SHORT).show();
			return false;
		}
		final BluetoothDevice device = bluetoothAdapter	.getRemoteDevice(address);
		bluetoothGatt = device.connectGatt(view, false, gattCallback);
		if(bluetoothGatt!=null){
			//5.0 可以使用
			String str ="版本: " + Build.MODEL + ","
					+ Build.VERSION.SDK + ","
					+ Build.VERSION.RELEASE+","+Build.VERSION.SDK_INT;
			System.out.println(str);
			if (Build.VERSION.SDK_INT>Build.VERSION_CODES.KITKAT_WATCH){
				bluetoothGatt.requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_HIGH);
				//bluetoothGatt.requestMtu(200);
			}
		}

		return true;
	}
	

	public void disconnect() {
		if (bluetoothGatt != null) {
			bluetoothGatt.disconnect();
			bluetoothGatt.close();
			connectstate.set(0);
		}
	}

	/**
	 * 人为关闭, 不重连
	 */
	public  void  artifClose(){
		cnt.set(10);
		disconnect();
	}

}
