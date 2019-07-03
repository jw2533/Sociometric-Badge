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


public class LinearAcceleratorSensorReader implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mLinearAccelerator;
    private ValueStore mValueStore;
    private DisplaySensorValuesInterface mLinearAcceleratorDisplay;
    private int sampleRate;
    private int countDisplayRounds = 0;
    private int displayThreshold = -1;
    private int countWriteRounds = 0;
    private int writeThreshold = -1;
    private FileWriter linearAcceleratorFileWriter;
    private File linearAcceleratorFile;
    private boolean onlyDisplay=false;

    public LinearAcceleratorSensorReader(ValueStore parentValueStore,
                                         SensorManager parentSensorManager,
                                         int parentSampleRate) {
        mSensorManager = parentSensorManager;
        mValueStore = parentValueStore;
        mLinearAccelerator = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sampleRate = parentSampleRate;
    }

    public LinearAcceleratorSensorReader(boolean valueStoreOption,
                                       SensorManager parentSensorManager,
                                       DisplaySensorValuesInterface parentDisplay,
                                       int displayRate,
                                       int parentSampleRate) {
        mSensorManager = parentSensorManager;
        mLinearAccelerator = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sampleRate = parentSampleRate;
        mLinearAcceleratorDisplay = parentDisplay;
        displayThreshold = displayRate >= sampleRate ? displayRate/sampleRate : 1;
        onlyDisplay = valueStoreOption;
    }

    public LinearAcceleratorSensorReader(ValueStore parentValueStore,
                                         SensorManager parentSensorManager,
                                         int parentSampleRate,
                                         int writeRate,
                                         File parentFile) {
        mSensorManager = parentSensorManager;
        mValueStore = parentValueStore;
        mLinearAccelerator = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sampleRate = parentSampleRate;
        linearAcceleratorFile = parentFile;
        writeThreshold = writeRate >= sampleRate ? writeRate/sampleRate : 1;
    }

    public LinearAcceleratorSensorReader(ValueStore parentValueStore,
                                         DisplaySensorValuesInterface parentDisplay,
                                         SensorManager parentSensorManager,
                                         int parentSampleRate,
                                         int displayRate,
                                         int writeRate,
                                         File parentFile) {
        mSensorManager = parentSensorManager;
        mValueStore = parentValueStore;
        mLinearAcceleratorDisplay = parentDisplay;
        mLinearAccelerator = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sampleRate = parentSampleRate;
        linearAcceleratorFile = parentFile;
        writeThreshold = writeRate >= sampleRate ? writeRate/sampleRate : 1;
        displayThreshold = displayRate >= sampleRate ? displayRate/sampleRate : 1;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] linearAcceleratorValues = event.values;
        if(!onlyDisplay){
            mValueStore.setLinearAccelerometerValues(linearAcceleratorValues);
        }
        if (displayThreshold > 0) {
            countDisplayRounds += 1;
            if (countDisplayRounds == displayThreshold) {
                display(linearAcceleratorValues);
                countDisplayRounds = 0;
            }
        }

        if(writeThreshold > 0) {
            countWriteRounds += 1;
            if (countWriteRounds == writeThreshold) {
                countWriteRounds = 0;
                try {
                    linearAcceleratorFileWriter.append(
                            String.format("%s,%s,%s,%s\n",
                                    String.valueOf(System.currentTimeMillis()),
                                    String.valueOf(linearAcceleratorValues[0]),
                                    String.valueOf(linearAcceleratorValues[1]),
                                    String.valueOf(linearAcceleratorValues[2])));
                    linearAcceleratorFileWriter.flush();
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
        mSensorManager.registerListener(this, mLinearAccelerator, sampleRate, sampleRate);
        if(!onlyDisplay){
            try {
                linearAcceleratorFileWriter = new FileWriter(linearAcceleratorFile, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void close() {
        try {
            linearAcceleratorFileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mSensorManager.unregisterListener(this);
    }

    private void display(float[] sensorValues) {
        mLinearAcceleratorDisplay.execute(sensorValues);
    }

}

