package sensorReaders;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;
import android.content.Intent;

@SuppressLint("NewApi")
public class BluetoothSignalMetadata {
    private String deviceName;
    private String deviceMac;
    private int signalStrength;
    private int txPower;
    private double distance;

    public BluetoothSignalMetadata(ScanResult scanResult, SignalStrengthDistanceConverter converter) {
        signalStrength = scanResult.getRssi();
        BluetoothDevice device = scanResult.getDevice();
        deviceName = device.getName();
        deviceMac = device.getAddress();
        txPower = scanResult.getTxPower();
        if (converter == null) {
            distance = 0;
        } else {
            distance = converter.toDistance(signalStrength, txPower == 127 ? 0 : txPower);
        }
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getDeviceMac() {
        return deviceMac;
    }

    public int getSignalStrength() {
        return signalStrength;
    }

    public int getTxPower() {
        return txPower;
    }

    public double getDistance() {
        return distance;
    }

    public double estimateDistance(SignalStrengthDistanceConverter converter) {
        if (converter == null) {
            distance = 0;
        } else {
            distance = converter.toDistance(signalStrength, txPower == 127 ? 0 : txPower);
        }
        return distance;
    }
}
