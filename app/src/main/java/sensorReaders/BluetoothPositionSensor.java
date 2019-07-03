package sensorReaders;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

public class BluetoothPositionSensor {
    private final ValueStore parentValueStore;
    private DisplaySensorValuesInterface parentDisplay;
    private final int parentSampleRate;
    private BluetoothDevicesReader devicesReader;
    private Map<String, BluetoothSignalMetadata> deviceList;
    private DevicePositionEstimator positionEstimator;
    private DeviceCoord lastPosition;
    private DevicePositionListener listener;
    private File positionSensorFile;
    private FileWriter positionSensorFileWriter;

    private static class DefaultBluetoothApproachingSensor implements BluetoothApproachingSensor {
        private final Long MAX_DEVICE_LIVE_TIME = (long)5000;

        private BluetoothApproachingListener listener;
        private double distanceThreshold;
        private boolean started;
        private Timer checkTimer;
        private Map<String, Double> deviceDistance;
        private Map<String, Long> deviceFoundTime;
        private Handler mainHandler;

        public DefaultBluetoothApproachingSensor(Context context) {
            deviceDistance = new HashMap<>();
            deviceFoundTime = new HashMap<>();
            distanceThreshold = 2;
            mainHandler = new Handler(context.getMainLooper());
        }

        public DefaultBluetoothApproachingSensor(Context context, double distanceThreshold) {
            deviceDistance = new HashMap<>();
            this.distanceThreshold = distanceThreshold;
            mainHandler = new Handler(context.getMainLooper());
        }

        @Override
        public void setListener(BluetoothApproachingListener listener) {
            this.listener = listener;
        }

        @Override
        public double getDistanceThreshold() {
            return distanceThreshold;
        }

        @Override
        public void setDistanceThreshold(double threshold) {
            this.distanceThreshold = threshold;
        }

        @Override
        public boolean started() {
            return started;
        }

        @Override
        public void open() {
            if (started)
                return;
            started = true;

            checkTimer = new Timer(true);
            TimerTask task = new TimerTask() {
                public void run() {
                    synchronized (deviceDistance) {
                        Date currentTime = Calendar.getInstance().getTime();
                        Long timeMilli = currentTime.getTime();
                        List<String> keyToRemove = new ArrayList<>();
                        for (String key : deviceFoundTime.keySet()) {
                            Long lastTime = deviceFoundTime.get(key);
                            if (timeMilli - lastTime > MAX_DEVICE_LIVE_TIME)
                                keyToRemove.add(key);
                        }
                        for (String key : keyToRemove) {
                            deviceDistance.remove(key);
                            deviceFoundTime.remove(key);
                        }

                        int deviceCount = 0;
                        for (double d : deviceDistance.values()) {
                            if (d <= distanceThreshold)
                                deviceCount++;
                        }
                        final int dc = deviceCount;
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (listener != null)
                                    listener.onScanned(dc > 0, dc, distanceThreshold);
                            }
                        });
                    }
                }
            };
            checkTimer.schedule(task, 1000, 1000);
        }

        @Override
        public void close() {
            if (!started)
                return;
            started = false;
            checkTimer.cancel();
            checkTimer = null;
        }

        public void onDevice(String mac, double distance) {
            synchronized (deviceDistance) {
                Date currentTime = Calendar.getInstance().getTime();
                Long timeMilli = currentTime.getTime();
                deviceDistance.put(mac, distance);
                deviceFoundTime.put(mac, timeMilli);
            }
        }
    }

    private DefaultBluetoothApproachingSensor approachingSensor;

    public BluetoothPositionSensor(
            Context context,
            List<BluetoothDevicePositionMetadata> beacons,
            ValueStore parentValueStore,
            DisplaySensorValuesInterface parentDisplay,
            int parentSampleRate,
            File parentFile,
            DevicePositionListener listener) {
        this.parentValueStore = parentValueStore;
        this.parentDisplay = parentDisplay;
        this.parentSampleRate = parentSampleRate;
        this.positionSensorFile = parentFile;
        this.listener = listener;
        this.approachingSensor = new DefaultBluetoothApproachingSensor(context);
        initializeDevices(beacons);
    }

    public void open() {
        devicesReader.startScan();
        if (positionSensorFile != null) {
            try {
                positionSensorFileWriter = new FileWriter(positionSensorFile, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void close() {
        devicesReader.stopScan();
        if (positionSensorFileWriter != null) {
            try {
                positionSensorFileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setPositionListener(DevicePositionListener listener) {
        this.listener = listener;
    }

    public BluetoothApproachingSensor getApproachingSensor() {
        return approachingSensor;
    }

    private void initializeDevices(List<BluetoothDevicePositionMetadata> beacons) {
        deviceList = new TreeMap<>();
        positionEstimator = new DevicePositionEstimator(beacons);
        devicesReader = new BluetoothDevicesReader();
        devicesReader.SetDeviceFoundListener(new BluetoothDeviceFoundListener() {
            @Override
            public void onDeviceFound(BluetoothSignalMetadata signalData) {
                String mac = signalData.getDeviceMac();
                boolean isBeacon = positionEstimator.getBeaconKeys().contains(mac);
                if (isBeacon) {
                    synchronized (deviceList) {
                        deviceList.put(mac, signalData);
                        lastPosition = positionEstimator.Estimate(deviceList.values());
                        onPosition(lastPosition);
                    }
                } else {
                    if (approachingSensor.started) {
                        approachingSensor.onDevice(mac, signalData.getDistance());
                    }
                }
            }
        });
    }

    private void onPosition(DeviceCoord position) {
        float[] positionData = new float[] { (float)position.getX(), (float)position.getY(), 0 };
        if (parentValueStore != null)
            parentValueStore.setBluetoothPositionValues(positionData);
        if (parentDisplay != null)
            parentDisplay.execute(positionData);
        if (listener != null)
            listener.onPosition(position);

        if (positionSensorFileWriter != null) {
            try {
                positionSensorFileWriter.append(
                        String.format("%s,%s,%s,0\n",
                                String.valueOf(System.currentTimeMillis()),
                                String.valueOf(positionData[0]),
                                String.valueOf(positionData[1])));
                positionSensorFileWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
