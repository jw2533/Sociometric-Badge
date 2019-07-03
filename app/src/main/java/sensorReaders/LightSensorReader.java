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


public class LightSensorReader implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mLightSensor;
    private ValueStore mValueStore;
    private DisplaySensorValuesInterface mLightSensorDisplay;
    private int sampleRate;
    private int countDisplayRounds = 0;
    private int displayThreshold = -1;
    private int countWriteRounds = 0;
    private int writeThreshold = -1;
    private FileWriter lightSensorFileWriter;
    private File lightSensorFile;
    private boolean onlyDisplay=false;

    public LightSensorReader(ValueStore parentValueStore,
                                     SensorManager parentSensorManager,
                                     int parentSampleRate) {
        mSensorManager = parentSensorManager;
        mValueStore = parentValueStore;
        mLightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        sampleRate = parentSampleRate;
    }

    public LightSensorReader(boolean valueStoreOption,
                                     SensorManager parentSensorManager,
                                     DisplaySensorValuesInterface parentDisplay,
                                     int displayRate,
                                     int parentSampleRate) {
        mSensorManager = parentSensorManager;
        mLightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        sampleRate = parentSampleRate;
        mLightSensorDisplay = parentDisplay;
        displayThreshold = displayRate >= sampleRate ? displayRate/sampleRate : 1;
        onlyDisplay = valueStoreOption;
    }

    public LightSensorReader(ValueStore parentValueStore,
                                     SensorManager parentSensorManager,
                                     int parentSampleRate,
                                     int writeRate,
                                     File parentFile) {
        mSensorManager = parentSensorManager;
        mValueStore = parentValueStore;
        mLightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        sampleRate = parentSampleRate;
        lightSensorFile = parentFile;
        writeThreshold = writeRate >= sampleRate ? writeRate/sampleRate : 1;
    }

    public LightSensorReader(ValueStore parentValueStore,
                                     DisplaySensorValuesInterface parentDisplay,
                                     SensorManager parentSensorManager,
                                     int parentSampleRate,
                                     int displayRate,
                                     int writeRate,
                                     File parentFile) {
        mSensorManager = parentSensorManager;
        mValueStore = parentValueStore;
        mLightSensorDisplay = parentDisplay;
        mLightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        sampleRate = parentSampleRate;
        lightSensorFile = parentFile;
        writeThreshold = writeRate >= sampleRate ? writeRate/sampleRate : 1;
        displayThreshold = displayRate >= sampleRate ? displayRate/sampleRate : 1;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] lightSensorValues = event.values;

        if(!onlyDisplay) {
            mValueStore.setLightValues(lightSensorValues);
        }

        if (displayThreshold > 0) {
            countDisplayRounds += 1;
            if (countDisplayRounds == displayThreshold) {
                display(lightSensorValues);
                countDisplayRounds = 0;
            }
        }

        if(writeThreshold > 0) {
            countWriteRounds += 1;
            if (countWriteRounds == writeThreshold) {
                countWriteRounds = 0;
                try {
                    lightSensorFileWriter.append(
                            String.format("%s,%s\n",
                                    String.valueOf(System.currentTimeMillis()),
                                    String.valueOf(lightSensorValues[0])));
                    lightSensorFileWriter.flush();
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
        mSensorManager.registerListener(this, mLightSensor, sampleRate, sampleRate);
        if(!onlyDisplay){
            try {
                lightSensorFileWriter = new FileWriter(lightSensorFile, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void close() {
        try {
            lightSensorFileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mSensorManager.unregisterListener(this);
    }

    private void display(float[] sensorValues) {
        mLightSensorDisplay.execute(sensorValues);
    }

}

