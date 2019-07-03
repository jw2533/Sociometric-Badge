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


public class MagnetometerSensorReader implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mMagnetometer;
    private ValueStore mValueStore;
    private DisplaySensorValuesInterface mMagnetometerDisplay;
    private int sampleRate;
    private int countDisplayRounds = 0;
    private int displayThreshold = -1;
    private int countWriteRounds = 0;
    private int writeThreshold = -1;
    private FileWriter magnetometerFileWriter;
    private File magnetometerFile;
    private boolean onlyDisplay = false;

    public MagnetometerSensorReader(ValueStore parentValueStore,
                                    SensorManager parentSensorManager,
                                    int parentSampleRate) {
        mSensorManager = parentSensorManager;
        mValueStore = parentValueStore;
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sampleRate = parentSampleRate;
    }

    public MagnetometerSensorReader(boolean valueStoreOption,
                                    SensorManager parentSensorManager,
                                    DisplaySensorValuesInterface parentDisplay,
                                    int displayRate,
                                    int parentSampleRate) {
        mSensorManager = parentSensorManager;
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sampleRate = parentSampleRate;
        mMagnetometerDisplay = parentDisplay;
        displayThreshold = displayRate >= sampleRate ? displayRate/sampleRate : 1;
        onlyDisplay = valueStoreOption;
    }

    public MagnetometerSensorReader(ValueStore parentValueStore,
                                    SensorManager parentSensorManager,
                                    int parentSampleRate,
                                    int writeRate,
                                    File parentFile) {
        mSensorManager = parentSensorManager;
        mValueStore = parentValueStore;
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sampleRate = parentSampleRate;
        magnetometerFile = parentFile;
        writeThreshold = writeRate >= sampleRate ? writeRate/sampleRate : 1;
    }

    public MagnetometerSensorReader(ValueStore parentValueStore,
                                    DisplaySensorValuesInterface parentDisplay,
                                    SensorManager parentSensorManager,
                                    int parentSampleRate,
                                    int displayRate,
                                    int writeRate,
                                    File parentFile) {
        mSensorManager = parentSensorManager;
        mValueStore = parentValueStore;
        mMagnetometerDisplay = parentDisplay;
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sampleRate = parentSampleRate;
        magnetometerFile = parentFile;
        writeThreshold = writeRate >= sampleRate ? writeRate/sampleRate : 1;
        displayThreshold = displayRate >= sampleRate ? displayRate/sampleRate : 1;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] magnetometerValues = event.values;

        if(!onlyDisplay) {
            mValueStore.setMagnetometerValues(magnetometerValues);
        }

        if (displayThreshold > 0) {
            countDisplayRounds += 1;
            if (countDisplayRounds == displayThreshold) {
                display(magnetometerValues);
                countDisplayRounds = 0;
            }
        }

        if(writeThreshold > 0) {
            countWriteRounds += 1;
            if (countWriteRounds == writeThreshold) {
                countWriteRounds = 0;
                try {
                    magnetometerFileWriter.append(
                            String.format("%s,%s,%s,%s\n",
                                    String.valueOf(System.currentTimeMillis()),
                                    String.valueOf(magnetometerValues[0]),
                                    String.valueOf(magnetometerValues[1]),
                                    String.valueOf(magnetometerValues[2])));
                    magnetometerFileWriter.flush();
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
        mSensorManager.registerListener(this, mMagnetometer, sampleRate, sampleRate);
        if(!onlyDisplay) {
            try {
                magnetometerFileWriter = new FileWriter(magnetometerFile, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void close() {
        try {
            magnetometerFileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mSensorManager.unregisterListener(this);
    }

    private void display(float[] sensorValues) {
        mMagnetometerDisplay.execute(sensorValues);
    }

//    private void enableDisplay(int displayRate, DisplaySensorValuesInterface parentDisplay) {
//        mMagnetometerDisplay = parentDisplay;
//        displayThreshold = displayRate >= sampleRate ? displayRate/sampleRate : 1;
//    }
//
//    private void disableDisplay() {
//        countDisplayRounds = 0;
//        displayThreshold = -1;
//    }

}

