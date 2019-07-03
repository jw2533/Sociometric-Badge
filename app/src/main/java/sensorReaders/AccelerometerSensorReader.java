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


public class AccelerometerSensorReader implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ValueStore mValueStore;
    private DisplaySensorValuesInterface mAccelerometerDisplay;
    private int sampleRate;
    private int countDisplayRounds = 0;
    private int displayThreshold = -1;
    private int countWriteRounds = 0;
    private int writeThreshold = -1;
    private FileWriter accelerometerFileWriter;
    private File accelerometerFile;
    private boolean onlyDisplay=false;

    public AccelerometerSensorReader(ValueStore parentValueStore,
                                     SensorManager parentSensorManager,
                                     int parentSampleRate) {
        mSensorManager = parentSensorManager;
        mValueStore = parentValueStore;
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sampleRate = parentSampleRate;
    }

    public AccelerometerSensorReader(boolean valueStoreOption,
                                    SensorManager parentSensorManager,
                                    DisplaySensorValuesInterface parentDisplay,
                                    int displayRate,
                                    int parentSampleRate) {
        mSensorManager = parentSensorManager;
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sampleRate = parentSampleRate;
        mAccelerometerDisplay = parentDisplay;
        displayThreshold = displayRate >= sampleRate ? displayRate/sampleRate : 1;
        onlyDisplay = valueStoreOption;
    }

    public AccelerometerSensorReader(ValueStore parentValueStore,
                                     SensorManager parentSensorManager,
                                     int parentSampleRate,
                                     int writeRate,
                                     File parentFile) {
        mSensorManager = parentSensorManager;
        mValueStore = parentValueStore;
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sampleRate = parentSampleRate;
        accelerometerFile = parentFile;
        writeThreshold = writeRate >= sampleRate ? writeRate/sampleRate : 1;
    }

    public AccelerometerSensorReader(ValueStore parentValueStore,
                                     DisplaySensorValuesInterface parentDisplay,
                                     SensorManager parentSensorManager,
                                     int parentSampleRate,
                                     int displayRate,
                                     int writeRate,
                                     File parentFile) {
        mSensorManager = parentSensorManager;
        mValueStore = parentValueStore;
        mAccelerometerDisplay = parentDisplay;
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sampleRate = parentSampleRate;
        accelerometerFile = parentFile;
        writeThreshold = writeRate >= sampleRate ? writeRate/sampleRate : 1;
        displayThreshold = displayRate >= sampleRate ? displayRate/sampleRate : 1;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] accelerometerValues = event.values;

        if(!onlyDisplay) {
            mValueStore.setAccelerometerValues(accelerometerValues);
        }

        if (displayThreshold > 0) {
            countDisplayRounds += 1;
            if (countDisplayRounds == displayThreshold) {
                display(accelerometerValues);
                countDisplayRounds = 0;
            }
        }

        if(writeThreshold > 0) {
            countWriteRounds += 1;
            if (countWriteRounds == writeThreshold) {
                countWriteRounds = 0;
                try {
                    accelerometerFileWriter.append(
                            String.format("%s,%s,%s,%s\n",
                                    String.valueOf(System.currentTimeMillis()),
                                    String.valueOf(accelerometerValues[0]),
                                    String.valueOf(accelerometerValues[1]),
                                    String.valueOf(accelerometerValues[2])));
                    accelerometerFileWriter.flush();
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
        mSensorManager.registerListener(this, mAccelerometer, sampleRate, sampleRate);
        if(!onlyDisplay){
            try {
                accelerometerFileWriter = new FileWriter(accelerometerFile, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void close() {
        try {
            accelerometerFileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mSensorManager.unregisterListener(this);
    }

    private void display(float[] sensorValues) {
        mAccelerometerDisplay.execute(sensorValues);
    }

}

