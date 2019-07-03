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


public class RotationalVectorSensorReader implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mRotationalVectorSensor;
    private ValueStore mValueStore;
    private DisplaySensorValuesInterface mRotationalVectorDisplay;
    private int sampleRate;
    private int countDisplayRounds = 0;
    private int displayThreshold = -1;
    private int countWriteRounds = 0;
    private int writeThreshold = -1;
    private FileWriter rotationalVectorFileWriter;
    private File rotationalVectorFile;
    private boolean onlyDisplay=false;

    public RotationalVectorSensorReader(ValueStore parentValueStore,
                               SensorManager parentSensorManager,
                               int parentSampleRate) {
        mSensorManager = parentSensorManager;
        mValueStore = parentValueStore;
        mRotationalVectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        sampleRate = parentSampleRate;
    }

    public RotationalVectorSensorReader(boolean valueStoreOption,
                                     SensorManager parentSensorManager,
                                     DisplaySensorValuesInterface parentDisplay,
                                     int displayRate,
                                     int parentSampleRate) {
        mSensorManager = parentSensorManager;
        mRotationalVectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sampleRate = parentSampleRate;
        mRotationalVectorDisplay = parentDisplay;
        displayThreshold = displayRate >= sampleRate ? displayRate/sampleRate : 1;
        onlyDisplay = valueStoreOption;
    }

    public RotationalVectorSensorReader(ValueStore parentValueStore,
                               SensorManager parentSensorManager,
                               int parentSampleRate,
                               int writeRate,
                               File parentFile) {
        mSensorManager = parentSensorManager;
        mValueStore = parentValueStore;
        mRotationalVectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        sampleRate = parentSampleRate;
        rotationalVectorFile = parentFile;
        writeThreshold = writeRate >= sampleRate ? writeRate/sampleRate : 1;
    }

    public RotationalVectorSensorReader(ValueStore parentValueStore,
                               DisplaySensorValuesInterface parentDisplay,
                               SensorManager parentSensorManager,
                               int parentSampleRate,
                               int displayRate,
                               int writeRate,
                               File parentFile) {
        mSensorManager = parentSensorManager;
        mValueStore = parentValueStore;
        mRotationalVectorDisplay = parentDisplay;
        mRotationalVectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        sampleRate = parentSampleRate;
        rotationalVectorFile = parentFile;
        writeThreshold = writeRate >= sampleRate ? writeRate/sampleRate : 1;
        displayThreshold = displayRate >= sampleRate ? displayRate/sampleRate : 1;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] rotaionalVectorValues = event.values;
        if(!onlyDisplay) {
            mValueStore.setRotationalVectorValues(rotaionalVectorValues);
        }
        if (displayThreshold > 0) {
            countDisplayRounds += 1;
            if (countDisplayRounds == displayThreshold) {
                display(rotaionalVectorValues);
                countDisplayRounds = 0;
            }
        }

        if(writeThreshold > 0) {
            countWriteRounds += 1;
            if (countWriteRounds == writeThreshold) {
                countWriteRounds = 0;
                try {
                    rotationalVectorFileWriter.append(
                            String.format("%s,%s,%s,%s\n",
                                    String.valueOf(System.currentTimeMillis()),
                                    String.valueOf(rotaionalVectorValues[0]),
                                    String.valueOf(rotaionalVectorValues[1]),
                                    String.valueOf(rotaionalVectorValues[2])));
                    rotationalVectorFileWriter.flush();
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
        mSensorManager.registerListener(this, mRotationalVectorSensor, sampleRate, sampleRate);
        if(!onlyDisplay) {
            try {
                rotationalVectorFileWriter = new FileWriter(rotationalVectorFile, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void close() {
        try {
            rotationalVectorFileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mSensorManager.unregisterListener(this);
    }

    private void display(float[] sensorValues) {
        mRotationalVectorDisplay.execute(sensorValues);
    }

}

