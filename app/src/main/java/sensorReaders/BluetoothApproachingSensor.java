package sensorReaders;

public interface BluetoothApproachingSensor {
    public void setListener(BluetoothApproachingListener listener);
    public double getDistanceThreshold();
    public void setDistanceThreshold(double threshold);
    public boolean started();

    public void open();
    public void close();
}
