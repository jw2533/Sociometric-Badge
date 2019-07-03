package sensorReaders;

import android.annotation.SuppressLint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class DevicePositionEstimator {
    private Map<String, BluetoothDevicePositionMetadata> deviceMap;
    private Set<String> beaconKeys;

    public DevicePositionEstimator(List<BluetoothDevicePositionMetadata> devices) {
        TreeMap<String, BluetoothDevicePositionMetadata> deviceMap = new TreeMap<>();
        for (BluetoothDevicePositionMetadata device : devices) {
            deviceMap.put(device.getDeviceMac(), device);
        }
        this.deviceMap = deviceMap;
        this.beaconKeys = deviceMap.keySet();
    }

    public Set<String> getBeaconKeys() {
        return beaconKeys;
    }

    public DeviceCoord Estimate(Collection<BluetoothSignalMetadata> deviceSignals) {
        BluetoothDevicePositionMetadata[] selectedDevices = new BluetoothDevicePositionMetadata[3];
        double[] distances = new double[3];
        int index = 0;
        for (BluetoothSignalMetadata device : deviceSignals) {
            String mac = device.getDeviceMac();
            if (deviceMap.containsKey(mac)) {
                selectedDevices[index] = deviceMap.get(mac);
                distances[index] = device.getDistance();
                index++;
                if (index >= 3) {
                    break;
                }
            }
        }

        if (index == 0) {
            // no device found
            return new DeviceCoord(0, 0);
        } else if (index == 1) {
            // find only one device
            return selectedDevices[0].getCoord().copy();
        } else if (index == 2) {
            // find two devices
            DeviceCoord coordA = selectedDevices[0].getCoord();
            DeviceCoord coordB = selectedDevices[1].getCoord();
            double radiusA = distances[0];
            double radiusB = distances[1];
            DeviceCoord[] intersectionAB = getIntersections(coordA, radiusA, coordB, radiusB);
            if (intersectionAB.length <= 0) {
                return new DeviceCoord((coordA.getX() + coordB.getX()) / 2, (coordA.getY() + coordB.getY()) / 2);
            } else if (intersectionAB.length == 1) {
                return intersectionAB[0];
            } else {
                coordA = intersectionAB[0];
                coordB = intersectionAB[1];
                return new DeviceCoord((coordA.getX() + coordB.getX()) / 2, (coordA.getY() + coordB.getY()) / 2);
            }
        } else {
            // find three devices
            // namely a,b,c
            DeviceCoord coordA = selectedDevices[0].getCoord();
            DeviceCoord coordB = selectedDevices[1].getCoord();
            DeviceCoord coordC = selectedDevices[2].getCoord();
            double radiusA = distances[0];
            double radiusB = distances[1];
            double radiusC = distances[2];
            DeviceCoord[] intersectionAB = getIntersections(coordA, radiusA, coordB, radiusB);
            DeviceCoord[] intersectionAC = getIntersections(coordA, radiusA, coordC, radiusC);
            DeviceCoord[] intersectionBC = getIntersections(coordB, radiusB, coordC, radiusC);

            double minArea = Double.MAX_VALUE;
            DeviceCoord minCenter = null;
            // find the smallest triangle and its center point
            for (DeviceCoord pA : intersectionAB) {
                for (DeviceCoord pB : intersectionAC) {
                    for (DeviceCoord pC : intersectionBC) {
                        double a = DeviceCoord.distanceBetween(pA, pB);
                        double b = DeviceCoord.distanceBetween(pA, pC);
                        double c = DeviceCoord.distanceBetween(pB, pC);
                        double p = (a + b + c) / 2;
                        double area = Math.sqrt(p * (p - a) * (p - b) * (p - c));
                        if (area < minArea) {
                            double centerX = (pA.getX() + pB.getX() + pC.getX()) / 2;
                            double centerY = (pA.getY() + pB.getY() + pC.getY()) / 2;
                            minCenter = new DeviceCoord(centerX, centerY);
                        }
                    }
                }
            }
            return minCenter;
        }
    }

    private DeviceCoord[] getIntersections(DeviceCoord centerA, double radiusA, DeviceCoord centerB, double radiusB) {
        if (radiusA < radiusB)
            return getIntersections(centerB, radiusB, centerA, radiusA);

        double distance = DeviceCoord.distanceBetween(centerA, centerB);
        double radiusSum = radiusA + radiusB;
        double radiusDiff = radiusA - radiusB;
        double vecX = centerB.getX() - centerA.getX();
        double vecY = centerB.getY() - centerA.getY();
        if (doubleEqual(radiusDiff,  0) && doubleEqual(distance, 0)) {
            // circle a and circle b are identical
            return new DeviceCoord[0];
        }
        if (radiusDiff > distance) {
            // circle b is completely lied within the circle a
            // returns the point that is within a but lies outside of b
            double length = radiusA + (radiusA - distance - radiusB) / 2;
            vecX /= distance;
            vecY /= distance;
            return new DeviceCoord[] { centerA.offset(vecX * length, vecY * length) };
        }
        if (doubleEqual(radiusDiff, distance)) {
            // circle b and circle a are internally touched
            vecX /= distance;
            vecY /= distance;
            double offsetX = vecX * radiusA;
            double offsetY = vecY * radiusA;
            return new DeviceCoord[] { centerA.offset(offsetX, offsetY) };
        }
        if (radiusSum > distance) {
            // there are two crossing points
            double squareRadiusA = radiusA * radiusA;
            double l = (squareRadiusA - radiusB * radiusB + distance * distance) / 2 / distance;
            vecX /= distance;
            vecY /= distance;
            double offsetX = vecX * l;
            double offsetY = vecY * l;
            DeviceCoord center = centerA.offset(offsetX, offsetY);
            double vecFootX = -vecY;
            double vecFootY = vecX;
            double h = Math.sqrt(squareRadiusA - l * l);
            double footOffsetX = vecFootX * h;
            double footOffsetY = vecFootY * h;
            return new DeviceCoord[] {
                    center.offset(footOffsetX, footOffsetY),
                    center.offset(-footOffsetX, -footOffsetY)
            };
        }
        if (doubleEqual(radiusSum, distance)) {
            // circles are externally touched
            vecX /= distance;
            vecY /= distance;
            double offsetX = vecX * radiusA;
            double offsetY = vecY * radiusA;
            return new DeviceCoord[] { centerA.offset(offsetX, offsetY) };
        }
        // circles are independent to each other
        // returns the point which is the nearest to centerA and centerB
        {
            vecX /= distance;
            vecY /= distance;
            double length = (distance - radiusB + radiusA) / 2;
            return new DeviceCoord[] { centerA.offset(vecX * length, vecY * length) };
        }
    }

    private static boolean doubleEqual(double a, double b) {
        return Math.abs(a - b) <= 1E-4;
    }
}
