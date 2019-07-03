package sensorReaders;

public class DeviceCoord {
    private double x;
    private double y;

    public DeviceCoord(double x, double y) {

        this.x = x;
        this.y = y;
    }

    public DeviceCoord offset(double x, double y) {
        return new DeviceCoord(this.x + x, this.y + y);
    }

    public DeviceCoord copy() {
        return new DeviceCoord(x, y);
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public static double distanceBetween(DeviceCoord coord1, DeviceCoord coord2) {
        double xdiff = coord2.x - coord1.x;
        double ydiff = coord2.y - coord1.y;
        return Math.sqrt(xdiff * xdiff + ydiff * ydiff);
    }

    public static double squareDistanceBetween(DeviceCoord coord1, DeviceCoord coord2) {
        double xdiff = coord2.x - coord1.x;
        double ydiff = coord2.y - coord1.y;
        return xdiff * xdiff + ydiff * ydiff;
    }


}
