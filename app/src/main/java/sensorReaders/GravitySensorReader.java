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


public class GravitySensorReader implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mGravity;
    private ValueStore mValueStore;
    private DisplaySensorValuesInterface mGravityDisplay;
    private int sampleRate;
    private int countDisplayRounds = 0;
    private int displayThreshold = -1;
    private int countWriteRounds = 0;
    private int writeThreshold = -1;
    private FileWriter gravityFileWriter;
    private File gravityFile;
    private boolean onlyDisplay=false;

    public GravitySensorReader(ValueStore parentValueStore,
                                 SensorManager parentSensorManager,
                                 int parentSampleRate) {
        mSensorManager = parentSensorManager;
        mValueStore = parentValueStore;
        mGravity = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        sampleRate = parentSampleRate;
    }

    public GravitySensorReader(boolean valueStoreOption,
                                     SensorManager parentSensorManager,
                                     DisplaySensorValuesInterface parentDisplay,
                                     int displayRate,
                                     int parentSampleRate) {
        mSensorManager = parentSensorManager;
        mGravity = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sampleRate = parentSampleRate;
        mGravityDisplay = parentDisplay;
        displayThreshold = displayRate >= sampleRate ? displayRate/sampleRate : 1;
        onlyDisplay = valueStoreOption;
    }

    public GravitySensorReader(ValueStore parentValueStore,
                                 SensorManager parentSensorManager,
                                 int parentSampleRate,
                                 int writeRate,
                                 File parentFile) {
        mSensorManager = parentSensorManager;
        mValueStore = parentValueStore;
        mGravity = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        sampleRate = parentSampleRate;
        gravityFile = parentFile;
        writeThreshold = writeRate >= sampleRate ? writeRate/sampleRate : 1;
    }

    public GravitySensorReader(ValueStore parentValueStore,
                                 DisplaySensorValuesInterface parentDisplay,
                                 SensorManager parentSensorManager,
                                 int parentSampleRate,
                                 int displayRate,
                                 int writeRate,
                                 File parentFile) {
        mSensorManager = parentSensorManager;
        mValueStore = parentValueStore;
        mGravityDisplay = parentDisplay;
        mGravity = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        sampleRate = parentSampleRate;
        gravityFile = parentFile;
        writeThreshold = writeRate >= sampleRate ? writeRate/sampleRate : 1;
        displayThreshold = displayRate >= sampleRate ? displayRate/sampleRate : 1;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] gravityValues = event.values;
        if( !onlyDisplay ) {
            mValueStore.setGravityValues(gravityValues);
        }
        if (displayThreshold > 0) {
            countDisplayRounds += 1;
            if (countDisplayRounds == displayThreshold) {
                display(gravityValues);
                countDisplayRounds = 0;
            }
        }

        if(writeThreshold > 0) {
            countWriteRounds += 1;
            if (countWriteRounds == writeThreshold) {
                countWriteRounds = 0;
                try {
                    gravityFileWriter.append(
                            String.format("%s,%s,%s,%s\n",
                                    String.valueOf(System.currentTimeMillis()),
                                    String.valueOf(gravityValues[0]),
                                    String.valueOf(gravityValues[1]),
                                    String.valueOf(gravityValues[2])));
                    gravityFileWriter.flush();
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
        mSensorManager.registerListener(this, mGravity, sampleRate, sampleRate);
        if(!onlyDisplay) {
            try {
                gravityFileWriter = new FileWriter(gravityFile, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void close() {
        try {
            gravityFileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mSensorManager.unregisterListener(this);
    }

    private void display(float[] sensorValues) {
        mGravityDisplay.execute(sensorValues);
    }

}

