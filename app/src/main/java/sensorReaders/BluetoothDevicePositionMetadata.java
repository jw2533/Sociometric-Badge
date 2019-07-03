package sensorReaders;

public class BluetoothDevicePositionMetadata {
    private String deviceMac;
    private DeviceCoord coord;

    public BluetoothDevicePositionMetadata(String deviceMac, double positionX, double positionY) {
        this.deviceMac = deviceMac;
        this.coord = new DeviceCoord(positionX, positionY);
    }

    public BluetoothDevicePositionMetadata(String deviceMac, DeviceCoord coord) {
        this.deviceMac = deviceMac;
        this.coord = coord;
    }

    public double getY() {
        return coord.getY();
    }

    public double getX() {
        return coord.getX();
    }

    public String getDeviceMac() {
        return deviceMac;
    }

    public DeviceCoord getCoord() {
        return coord;
    }
}
