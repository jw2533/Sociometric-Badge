package sensorReaders;

public interface BluetoothApproachingListener {
    public void onScanned(boolean hasNearDevice, int deviceCount, double distanceThreshold);
}
