package sensorReaders;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class GyroscopeSensorReader implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mGyroscope;
    private ValueStore mValueStore;
    private DisplaySensorValuesInterface mGyroscopeDisplay;
    private int sampleRate;
    private int countDisplayRounds = 0;
    private int displayThreshold = -1;
    private int countWriteRounds = 0;
    private int writeThreshold = -1;
    private FileWriter gyroscopeFileWriter;
    private File gyroscopeFile;
    private boolean onlyDisplay=false;

    public GyroscopeSensorReader(ValueStore parentValueStore,
                                     SensorManager parentSensorManager,
                                     int parentSampleRate) {
        mSensorManager = parentSensorManager;
        mValueStore = parentValueStore;
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sampleRate = parentSampleRate;
    }

    public GyroscopeSensorReader(boolean valueStoreOption,
                                     SensorManager parentSensorManager,
                                     DisplaySensorValuesInterface parentDisplay,
                                     int displayRate,
                                     int parentSampleRate) {
        mSensorManager = parentSensorManager;
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sampleRate = parentSampleRate;
        mGyroscopeDisplay = parentDisplay;
        displayThreshold = displayRate >= sampleRate ? displayRate/sampleRate : 1;
        onlyDisplay = valueStoreOption;
    }

    public GyroscopeSensorReader(ValueStore parentValueStore,
                                     SensorManager parentSensorManager,
                                     int parentSampleRate,
                                     int writeRate,
                                     File parentFile) {
        mSensorManager = parentSensorManager;
        mValueStore = parentValueStore;
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sampleRate = parentSampleRate;
        gyroscopeFile = parentFile;
        writeThreshold = writeRate >= sampleRate ? writeRate/sampleRate : 1;
    }

    public GyroscopeSensorReader(ValueStore parentValueStore,
                                     DisplaySensorValuesInterface parentDisplay,
                                     SensorManager parentSensorManager,
                                     int parentSampleRate,
                                     int displayRate,
                                     int writeRate,
                                     File parentFile) {
        mSensorManager = parentSensorManager;
        mValueStore = parentValueStore;
        mGyroscopeDisplay = parentDisplay;
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sampleRate = parentSampleRate;
        gyroscopeFile = parentFile;
        writeThreshold = writeRate >= sampleRate ? writeRate/sampleRate : 1;
        displayThreshold = displayRate >= sampleRate ? displayRate/sampleRate : 1;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] gyroscopeValues = event.values;
        if( !onlyDisplay ) {
            mValueStore.setGyroscopeValues(gyroscopeValues);
        }
        if (displayThreshold > 0) {
            countDisplayRounds += 1;
            if (countDisplayRounds == displayThreshold) {
                display(gyroscopeValues);
                countDisplayRounds = 0;
            }
        }

        if(writeThreshold > 0) {
            countWriteRounds += 1;
            if (countWriteRounds == writeThreshold) {
                countWriteRounds = 0;
                try {
                    gyroscopeFileWriter.append(
                            String.format("%s,%s,%s,%s\n",
                                    String.valueOf(System.currentTimeMillis()),
                                    String.valueOf(gyroscopeValues[0]),
                                    String.valueOf(gyroscopeValues[1]),
                                    String.valueOf(gyroscopeValues[2])));
                    gyroscopeFileWriter.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void open() {
        mSensorManager.registerListener(this, mGyroscope, sampleRate, sampleRate);
        if(!onlyDisplay) {
            try {
                gyroscopeFileWriter = new FileWriter(gyroscopeFile, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void close() {
        try {
            gyroscopeFileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mSensorManager.unregisterListener(this);
    }

    private void display(float[] sensorValues) {
        mGyroscopeDisplay.execute(sensorValues);
    }

}

