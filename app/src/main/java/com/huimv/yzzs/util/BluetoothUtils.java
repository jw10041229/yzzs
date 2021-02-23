package com.huimv.yzzs.util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BluetoothUtils {
    private final String TAG = "BLE";
    private BluetoothManager _bluetoothManager = null;
    private BluetoothAdapter _bluetoothAdapter = null;
    private ParcelUuid BLUETOOTH_SERVICE_UUID = null;
    private BluetoothLeScanner _bluetoothLeScanner = null;
    private BleScanCallback _bleScanCallback = null;

    private static volatile BluetoothUtils _instance = null;
    private Context context;
    private OnBleScanListener _listener = null;

    public static BluetoothUtils sharedInstance(Context context) {
        if (_instance == null) {   // 判空1
            synchronized (BluetoothUtils.class) {
                if (_instance == null) {   // 判空2
                    _instance = new BluetoothUtils(context);
                }
            }
        }
        return _instance;
    }

    private BluetoothUtils(Context context) {
        this.context = context.getApplicationContext();
    }

    public boolean initBleManager() {
        if (_bluetoothManager == null) {
            _bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        }
        return _bluetoothManager != null;
    }

    /**
     * 开始扫描
     *
     * @param timeout
     * @param listener
     * @return
     */
    public boolean startScanning(final int timeout, OnBleScanListener listener) {
        this._listener = listener;
        _bluetoothAdapter = _bluetoothManager.getAdapter();
        if (_bluetoothAdapter == null) {
            Toast.makeText(context,"手机蓝牙未打开",Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!_bluetoothAdapter.isEnabled()) {
            _bluetoothAdapter.enable();  //打开蓝牙，需要BLUETOOTH_ADMIN权限
            int count = 0;
            while (!_bluetoothAdapter.isEnabled() && count < 50){
                try {
                    Thread.sleep(20);
                    count++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        _bluetoothLeScanner = _bluetoothAdapter.getBluetoothLeScanner();
        if (_bluetoothLeScanner == null) {
            return false;
        }

        if (_bleScanCallback == null) {
            _bleScanCallback = new BleScanCallback(_bluetoothLeScanner, listener);
        } else {
            _bleScanCallback.mapDevice.clear();
            _bleScanCallback.mapBeaconDevice.clear();
        }
        _bleScanCallback.setScanBeginTime(JxDate.Now());
        _bleScanCallback.setTimeout(timeout);
        Log.d(TAG, "Starting Scanning");
        //List scanFilters = buildScanFilters();
        _bluetoothLeScanner.startScan(null, buildScanSettings(), _bleScanCallback);
        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopScanning();
                if (_listener != null) {
                    _listener.onScanStop();
                }
            }
        }, timeout * 1000);
        return true;
    }

    public void removeCallbacks() {
        handler.removeCallbacksAndMessages(null);
    }


    private Handler handler = new Handler();

    public void stopScanning() {
        if (_bluetoothLeScanner != null && _bleScanCallback != null) {
            handler.removeCallbacksAndMessages(null);
            if (_bluetoothAdapter.isEnabled()) {
                _bluetoothLeScanner.stopScan(_bleScanCallback);
            }
        }
    }

    /***************************************************************************************************
     *
     */

    private ScanSettings buildScanSettings() {
        ScanSettings.Builder builder = new ScanSettings.Builder();
        builder.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);
        return builder.build();
    }

    private List<ScanFilter> buildScanFilters() {
        List<ScanFilter> scanFilters = new ArrayList<>();
        ScanFilter.Builder builder = new ScanFilter.Builder();
        // Comment out the below line to see all BLE devices around you
        builder.setServiceUuid(BLUETOOTH_SERVICE_UUID);
        scanFilters.add(builder.build());
        return scanFilters;
    }

    /***************************************************************************************************
     *
     */
    public static class BeaconDevice {
        private String UUID = null;
        private int major = 0;
        private int minor = 0;
        private String address = null;
        private String name = null;
        private int rssi = 0;
        private int measuredPower = 0;

        public BeaconDevice(final String uuid, int major, int minor) {
            this.UUID = uuid;
            this.major = major;
            this.minor = minor;
        }

        public void setAddress(final String address) {
            this.address = address;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public void setRssi(final int rssi) {
            this.rssi = rssi;
        }

        public void setMeasuredPower(final int power) {
            this.measuredPower = power;
        }

        public String UUID() {
            return UUID;
        }

        public int Major() {
            return major;
        }

        public int Minor() {
            return minor;
        }

        public int Power() {
            return measuredPower;
        }

        public String Address() {
            return address;
        }

        public String Name() {
            return name;
        }

        public int rssi() {
            return rssi;
        }
    }

    public interface OnBleScanListener {
        void onScanDevice(BluetoothDevice device);

        void onScanBeaconDevice(BeaconDevice device);

        void onScanStop();
    }

    public class BleScanCallback extends ScanCallback {
        final String TAG = "BLE";
        Map<String, BluetoothDevice> mapDevice = new HashMap<>();
        Map<String, BeaconDevice> mapBeaconDevice = new HashMap<>();
        private BluetoothLeScanner _scanner = null;
        private JxDate _scanBeginTime = null;
        private int _timeout = 3;
        public BleScanCallback(BluetoothLeScanner scanner, OnBleScanListener listener) {
            super();
            _scanner = scanner;
            _listener = listener;
        }

        public void setScanBeginTime(final JxDate beginTime) {
            _scanBeginTime = beginTime;
        }

        public void setTimeout(final int timeout) {
            _timeout = timeout;
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            _scanner.stopScan(this);
            Log.d(TAG, "Batch scan results. ");
        }

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            Log.e(TAG, "Scan result: ");
            BluetoothDevice device = result.getDevice();
            if (!findDevice(device) && !findBeaconDevice(device)) {
                mapDevice.put(device.getAddress(), device);
                if (_listener != null) {
                    _listener.onScanDevice(device);
                }

                ScanRecord record = result.getScanRecord();
                String deviceName = device.getName();
                if (deviceName == null)
                    deviceName = record.getDeviceName();
                if (deviceName == null)
                    deviceName = "N/A";
                Log.d(TAG, "Device:{ name:" + deviceName + ",address:" + device.getAddress() + ",rssi:" + result.getRssi() + " }");

                /*List<ParcelUuid> uuids = record.getServiceUuids();
                if(uuids != null) {
                    for(ParcelUuid uuid : uuids) {
                        Log.d(TAG, "Parcel UUID:"+uuid.toString());
                    }
                }*/
                //Map<ParcelUuid, byte[]> data = record.getServiceData();

                SparseArray<byte[]> manufacturerData = record.getManufacturerSpecificData();
                if (manufacturerData != null) {
                    int size_1 = manufacturerData.size();
                    for (int i = 0; i < size_1; i++) {
                        int key = manufacturerData.keyAt(i);
                        byte[] value = manufacturerData.get(key);
                        if (key == 0x004C) {         //0x004c is apple company id
                            if (value != null && value.length == 23 && value[0] == 0x02 && value[1] == 0x15) {
                                //Log.d(TAG, record.toString());
                                BeaconDevice bd = parseToBeacon(value);
                                bd.setAddress(device.getAddress());
                                bd.setName(deviceName);
                                bd.setRssi(result.getRssi());
                                mapBeaconDevice.put(bd.UUID(), bd);
                                Log.e(TAG, "ibeacon:{ uuid:" + bd.UUID() + ",major:" + bd.Major()+ ",mac:" + device.getAddress() + ",minor:" + bd.Minor() + " }");
                                if (_listener != null) {
                                    _listener.onScanBeaconDevice(bd);
                                }
                            }
                        }
                    }
                }
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.e(TAG, "Scan onScanFailed: ");
            // _scanner.stopScan(this);
            if (_listener != null) {
                //  _listener.onScanStop();
            }
        }

        /*******************************************************************************************
         *
         */

        boolean findDevice(BluetoothDevice device) {
            return mapDevice.containsKey(device.getAddress());
        }

        boolean findBeaconDevice(BluetoothDevice device) {
            return mapBeaconDevice.containsKey(device.getAddress());
        }

        BeaconDevice parseToBeacon(byte[] value) {
            byte[] uuidValue = new byte[16];
            // params: src, srcpos, dest, destpos, length
            System.arraycopy(value, 2, uuidValue, 0, 16);
            String uuid = "";
            String hexStr = BytesToHexString(uuidValue);
            uuid = hexStr.substring(0, 8);
            uuid += "-";
            uuid += hexStr.substring(8, 12);
            uuid += "-";
            uuid += hexStr.substring(12, 16);
            uuid += "-";
            uuid += hexStr.substring(16, 20);
            uuid += "-";
            uuid += hexStr.substring(20, 32);
            int major = buildUint16(value[18], value[19]);
            int minor = buildUint16(value[20], value[21]);
            int measuredPower = 0xFF & value[22];

            BeaconDevice beaconDevice = new BeaconDevice(uuid, major, minor);
            beaconDevice.setMeasuredPower(measuredPower);
            return beaconDevice;
        }

        /*******************************************************************************************
         *
         */
        private final byte[] hex = "0123456789ABCDEF".getBytes();

        public String BytesToHexString(byte[] b) {
            if (b == null) {
                return null;
            }
            byte[] buff = new byte[2 * b.length];
            for (int i = 0; i < b.length; i++) {
                buff[2 * i] = hex[(b[i] >> 4) & 0x0f];
                buff[2 * i + 1] = hex[b[i] & 0x0f];
            }
            return new String(buff);
        }

        public int buildUint16(byte hi, byte lo) {
            return (int) (((hi & 0xff) << 8) + (lo & 0xff));
        }
    }
}
