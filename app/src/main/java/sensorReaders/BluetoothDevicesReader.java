package sensorReaders;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;

@SuppressLint("NewApi")
public class BluetoothDevicesReader {
    private class BluetoothScanCallback extends ScanCallback {
        private BluetoothDeviceFoundListener listener;
        private SignalStrengthDistanceConverter converter;

        public BluetoothScanCallback(BluetoothDeviceFoundListener listener, SignalStrengthDistanceConverter converter) {
            this.listener = listener;
            this.converter = converter;
        }

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothSignalMetadata metadata = new BluetoothSignalMetadata(result, converter);
            listener.onDeviceFound(metadata);
        }
    }

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private BluetoothScanCallback scanCallback;
    private SignalStrengthDistanceConverter converter;

    public BluetoothDevicesReader() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        converter = new DefaultSignalStrengthDistanceConverter();
    }

    public void SetDeviceFoundListener(BluetoothDeviceFoundListener listener) {
        scanCallback = new BluetoothScanCallback(listener, converter);
    }

    public void startScan() {
        if (bluetoothLeScanner == null) {
            if (!bluetoothAdapter.isEnabled()) {
                bluetoothAdapter.enable();
            }
            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        }
        bluetoothLeScanner.startScan(scanCallback);
    }

    public void stopScan() {
        bluetoothLeScanner.stopScan(scanCallback);
    }
}
