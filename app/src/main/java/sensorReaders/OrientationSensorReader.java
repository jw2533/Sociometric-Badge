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


public class OrientationSensorReader implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mOrientationSensor;
    private ValueStore mValueStore;
    private DisplaySensorValuesInterface mOrientationSensorDisplay;
    private int sampleRate;
    private int countDisplayRounds = 0;
    private int displayThreshold = -1;
    private int countWriteRounds = 0;
    private int writeThreshold = -1;
    private FileWriter orientationSensorFileWriter;
    private File orientationSensorFile;
    private boolean onlyDisplay=false;

    public OrientationSensorReader(ValueStore parentValueStore,
                                         SensorManager parentSensorManager,
                                         int parentSampleRate) {
        mSensorManager = parentSensorManager;
        mValueStore = parentValueStore;
        mOrientationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        sampleRate = parentSampleRate;
    }

    public OrientationSensorReader(boolean valueStoreOption,
                                    SensorManager parentSensorManager,
                                    DisplaySensorValuesInterface parentDisplay,
                                    int displayRate,
                                    int parentSampleRate) {
        mSensorManager = parentSensorManager;
        mOrientationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        sampleRate = parentSampleRate;
        mOrientationSensorDisplay = parentDisplay;
        displayThreshold = displayRate >= sampleRate ? displayRate/sampleRate : 1;
        onlyDisplay = valueStoreOption;
    }

    public OrientationSensorReader(ValueStore parentValueStore,
                                     SensorManager parentSensorManager,
                                     int parentSampleRate,
                                     int writeRate,
                                     File parentFile) {
        mSensorManager = parentSensorManager;
        mValueStore = parentValueStore;
        mOrientationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        sampleRate = parentSampleRate;
        orientationSensorFile = parentFile;
        writeThreshold = writeRate >= sampleRate ? writeRate/sampleRate : 1;
    }

    public OrientationSensorReader(ValueStore parentValueStore,
                                         DisplaySensorValuesInterface parentDisplay,
                                         SensorManager parentSensorManager,
                                         int parentSampleRate,
                                         int displayRate,
                                         int writeRate,
                                         File parentFile) {
        mSensorManager = parentSensorManager;
        mValueStore = parentValueStore;
        mOrientationSensorDisplay = parentDisplay;
        mOrientationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        sampleRate = parentSampleRate;
        orientationSensorFile = parentFile;
        writeThreshold = writeRate >= sampleRate ? writeRate/sampleRate : 1;
        displayThreshold = displayRate >= sampleRate ? displayRate/sampleRate : 1;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] orientationSensorValues = event.values;
        if( !onlyDisplay ){
            mValueStore.setOrientationValues(orientationSensorValues);
        }
        if (displayThreshold > 0) {
            countDisplayRounds += 1;
            if (countDisplayRounds == displayThreshold) {
                display(orientationSensorValues);
                countDisplayRounds = 0;
            }
        }

        if(writeThreshold > 0) {
            countWriteRounds += 1;
            if (countWriteRounds == writeThreshold) {
                countWriteRounds = 0;
                try {
                    orientationSensorFileWriter.append(
                            String.format("%s,%s,%s,%s\n",
                                    String.valueOf(System.currentTimeMillis()),
                                    String.valueOf(orientationSensorValues[0]),
                                    String.valueOf(orientationSensorValues[1]),
                                    String.valueOf(orientationSensorValues[2])));
                    orientationSensorFileWriter.flush();
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
        mSensorManager.registerListener(this, mOrientationSensor, sampleRate, sampleRate);
        if(!onlyDisplay) {
            try {
                orientationSensorFileWriter = new FileWriter(orientationSensorFile, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void close() {
        try {
            orientationSensorFileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mSensorManager.unregisterListener(this);
    }

    private void display(float[] sensorValues) {
        mOrientationSensorDisplay.execute(sensorValues);
    }

}

