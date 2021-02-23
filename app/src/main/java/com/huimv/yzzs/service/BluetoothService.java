/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huimv.yzzs.service;

import android.app.Service;
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
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.List;
import java.util.UUID;

/**
 * Service for managing connection and data communication with a GATT server
 * hosted on a given Bluetooth LE device.
 */
public class BluetoothService extends Service {
	private final static String TAG = BluetoothService.class.getSimpleName();

	private BluetoothManager mBluetoothManager;
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothGatt mBluetoothGatt;

	public final static String ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
	public final static String ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
	public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
	public final static String ACTION_DATA_AVAILABLE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
	public final static String EXTRA_DATA = "com.example.bluetooth.le.EXTRA_DATA";

	public final static UUID UUID_NOTIFY = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb");
	public final static UUID UUID_SERVICE = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb");

	// 华茂蓝牙 服务相关的UUID
	public final static UUID UUID_NOTIFY_HM = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb");
	public final static UUID UUID_SERVICE_HM = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb");

	// 利尔达蓝牙 服务相关的UUID
	public final static UUID UUID_SERVICE_LED = UUID.fromString("0000ff12-0000-1000-8000-00805f9b34fb");
	public final static UUID UUID_NOTIFY_LED_SEND = UUID.fromString("0000ff01-0000-1000-8000-00805f9b34fb");
	public final static UUID UUID_NOTIFY_LED_RECEIVE = UUID.fromString("0000ff02-0000-1000-8000-00805f9b34fb");
	public final static UUID UUID_NOTIFY_LED_CLIENT_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

	public BluetoothGattCharacteristic mNotifyCharacteristic;

	public boolean WriteValue(byte[] strValue) {
		if (mBluetoothGatt == null) {
			return false;
		}
		mNotifyCharacteristic.setValue(strValue);
		mBluetoothGatt.writeCharacteristic(mNotifyCharacteristic);
		return true;
	}

	public void findService(List<BluetoothGattService> gattServices) {
		 /*Log.i(TAG, "Count is:" + gattServices.size());
		 for (BluetoothGattService gattService : gattServices) {
		 Log.i(TAG, gattService.getUuid().toString());
		 Log.i(TAG, UUID_SERVICE.toString());
		 if
		 (gattService.getUuid().toString().equalsIgnoreCase(UUID_SERVICE.toString()))
		 {
		 List<BluetoothGattCharacteristic> gattCharacteristics =
		 gattService.getCharacteristics();
		 Log.i(TAG, "Count is:" + gattCharacteristics.size());
		 for (BluetoothGattCharacteristic gattCharacteristic :
		 gattCharacteristics) {
		 if
		 (gattCharacteristic.getUuid().toString().equalsIgnoreCase(UUID_NOTIFY_HM.toString()))
		 {
		 Log.i(TAG, gattCharacteristic.getUuid().toString());
		 Log.i(TAG, UUID_NOTIFY_HM.toString());
		 mNotifyCharacteristic = gattCharacteristic;
		 setCharacteristicNotification(gattCharacteristic, true);
		 broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
		 return;
		 }
		 }
		 }
		 }*/

		Log.i(TAG, "Count is:" + gattServices.size());
		for (BluetoothGattService gattService : gattServices) {
			Log.i(TAG, gattService.getUuid().toString());
			Log.i(TAG, UUID_SERVICE_HM.toString());
			Log.i(TAG, UUID_SERVICE_LED.toString());
			// 两个蓝牙模块的串口通信服务都允许被连接
			if (gattService.getUuid().toString().equalsIgnoreCase(UUID_SERVICE_HM.toString())
					|| gattService.getUuid().toString().equalsIgnoreCase(UUID_SERVICE_LED.toString())) {
				List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
				Log.i(TAG, "该蓝牙模块提供服务的个数 = Count is:" + gattCharacteristics.size());
				for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
					// 获取服务的 UUID
					String uuid = gattCharacteristic.getUuid().toString();
					if (uuid.equalsIgnoreCase(UUID_NOTIFY_HM.toString())
							|| uuid.equalsIgnoreCase(UUID_NOTIFY_LED_SEND.toString())) {
						Log.i(TAG, "现在连接的服务UUID = " + gattCharacteristic.getUuid().toString());
						mNotifyCharacteristic = gattCharacteristic;
						setCharacteristicNotification(gattCharacteristic, true);
						broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
					}
					if (uuid.equalsIgnoreCase(UUID_NOTIFY_LED_RECEIVE.toString())) {
						Log.i(TAG, "现在连接的服务UUID = " + gattCharacteristic.getUuid().toString());
						setCharacteristicNotification(gattCharacteristic, true);
						BluetoothGattDescriptor descriptor = gattCharacteristic
								.getDescriptor(UUID_NOTIFY_LED_CLIENT_CONFIG);
						if (descriptor != null) {
							descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
							mBluetoothGatt.writeDescriptor(descriptor);
						}
					}

				}

			}
		}
	}

	// Implements callback methods for GATT events that the app cares about. For
	// example,
	// connection change and services discovered.
	private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
			String intentAction;
			Log.i(TAG, "oldStatus=" + status + " NewStates=" + newState);
			if (status == BluetoothGatt.GATT_SUCCESS) {

				if (newState == BluetoothProfile.STATE_CONNECTED) {
					intentAction = ACTION_GATT_CONNECTED;

					broadcastUpdate(intentAction);
					Log.i(TAG, "Connected to GATT server.");
					// Attempts to discover services after successful
					// connection.
					Log.i(TAG, "Attempting to start service discovery:" + mBluetoothGatt.discoverServices());
				} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
					intentAction = ACTION_GATT_DISCONNECTED;
					mBluetoothGatt.close();
					mBluetoothGatt = null;
					Log.i(TAG, "Disconnected from GATT server.");
					broadcastUpdate(intentAction);
				}
			}
		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				Log.w(TAG, "onServicesDiscovered received: " + status);
				findService(gatt.getServices());
			} else {
				if (mBluetoothGatt.getDevice().getUuids() == null)
					Log.w(TAG, "onServicesDiscovered received: " + status);
			}
		}

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
			}
		}

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
			broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
			//Log.e(TAG, "OnCharacteristicWrite");
		}

		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
			//Log.e(TAG, "OnCharacteristicWrite");
		}

		@Override
		public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor bd, int status) {
			//Log.e(TAG, "onDescriptorRead");
		}

		@Override
		public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor bd, int status) {
			//Log.e(TAG, "onDescriptorWrite");
		}

		@Override
		public void onReadRemoteRssi(BluetoothGatt gatt, int a, int b) {
			//Log.e(TAG, "onReadRemoteRssi");
		}

		@Override
		public void onReliableWriteCompleted(BluetoothGatt gatt, int a) {
			//Log.e(TAG, "onReliableWriteCompleted");
		}

	};

	private void broadcastUpdate(final String action) {
		final Intent intent = new Intent(action);
		sendBroadcast(intent);
	}

	private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic) {
		final Intent intent = new Intent(action);

		// This is special handling for the Heart Rate Measurement profile. Data
		// parsing is
		// carried out as per profile specifications:
		// http://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.heart_rate_measurement.xml
		// For all other profiles, writes the data formatted in HEX.
		final byte[] data = characteristic.getValue();
		if (data != null && data.length > 0) {
			/*
			 * final StringBuilder stringBuilder = new
			 * StringBuilder(data.length); for(byte byteChar : data)
			 * stringBuilder.append(String.format("%02X", byteChar));
			 * intent.putExtra(EXTRA_DATA, stringBuilder.toString());
			 */
			intent.putExtra(EXTRA_DATA, data);
		}
		sendBroadcast(intent);
	}

	public class LocalBinder extends Binder {
		public BluetoothService getService() {
			return BluetoothService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// After using a given device, you should make sure that
		// BluetoothGatt.close() is called
		// such that resources are cleaned up properly. In this particular
		// example, close() is
		// invoked when the UI is disconnected from the Service.
		close();
		return super.onUnbind(intent);
	}

	private final IBinder mBinder = new LocalBinder();
	/**
	 * Initializes a reference to the local Bluetooth adapter.
	 *
	 * @return Return true if the initialization is successful.
	 */
	public boolean initialize() {
		// For API level 18 and above, get a reference to BluetoothAdapter
		// through
		// BluetoothManager.
		if (mBluetoothManager == null) {
			mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
			if (mBluetoothManager == null) {
				Log.e(TAG, "Unable to initialize BluetoothManager.");
				return false;
			}
		}

		mBluetoothAdapter = mBluetoothManager.getAdapter();
		if (mBluetoothAdapter == null) {
			Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
			return false;
		}

		return true;
	}

	/**
	 * Connects to the GATT server hosted on the Bluetooth LE device.
	 *
	 * @param address
	 *            The device address of the destination device.
	 *
	 * @return Return true if the connection is initiated successfully. The
	 *         connection result is reported asynchronously through the
	 *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
	 *         callback.
	 */
	public boolean connect(final String address) {
		if (mBluetoothAdapter == null || address == null) {
			Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
			return false;
		}
		/*
		 * // Previously connected device. Try to reconnect. if
		 * (mBluetoothDeviceAddress != null &&
		 * address.equals(mBluetoothDeviceAddress) && mBluetoothGatt != null) {
		 * Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection."
		 * ); if (mBluetoothGatt.connect()) { mConnectionState =
		 * STATE_CONNECTING; return true; } else { return false; } }
		 */
		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
		if (device == null) {
			Log.w(TAG, "Device not found.  Unable to connect.");
			return false;
		}
		// We want to directly connect to the device, so we are setting the
		// autoConnect
		// parameter to false.
		if (mBluetoothGatt != null) {
			mBluetoothGatt.close();
			mBluetoothGatt = null;
		}
		mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
		// mBluetoothGatt.connect();

		Log.d(TAG, "Trying to create a new connection.");
		return true;
	}

	/**
	 * Disconnects an existing connection or cancel a pending connection. The
	 * disconnection result is reported asynchronously through the
	 * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
	 * callback.
	 */
	public void disconnect() {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mBluetoothGatt.disconnect();
	}

	/**
	 * After using a given BLE device, the app must call this method to ensure
	 * resources are released properly.
	 */
	public void close() {
		if (mBluetoothGatt == null) {
			return;
		}
		mBluetoothGatt.close();
		mBluetoothGatt = null;
	}

	/**
	 * Request a read on a given {@code BluetoothGattCharacteristic}. The read
	 * result is reported asynchronously through the
	 * {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
	 * callback.
	 *
	 * @param characteristic
	 *            The characteristic to read from.
	 */
	public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mBluetoothGatt.readCharacteristic(characteristic);
	}

	/**
	 * Enables or disables notification on a give characteristic.
	 *
	 * @param characteristic
	 *            Characteristic to act on.
	 * @param enabled
	 *            If true, enable notification. False otherwise.
	 */
	public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
		/*
		 * // This is specific to Heart Rate Measurement. if
		 * (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
		 * BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
		 * UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
		 * descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
		 * ); mBluetoothGatt.writeDescriptor(descriptor); }
		 */
	}

	/**
	 * Retrieves a list of supported GATT services on the connected device. This
	 * should be invoked only after {@code BluetoothGatt#discoverServices()}
	 * completes successfully.
	 *
	 * @return A {@code List} of supported services.
	 */
	public List<BluetoothGattService> getSupportedGattServices() {
		if (mBluetoothGatt == null)
			return null;

		return mBluetoothGatt.getServices();
	}
}
