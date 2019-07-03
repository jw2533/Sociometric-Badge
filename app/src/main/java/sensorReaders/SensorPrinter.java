package sensorReaders;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class SensorPrinter {
    private SensorManager mSensorManager;
    private File sensorTypeFile;

    public SensorPrinter(SensorManager parentSensorManager, File parentFile){
        mSensorManager = parentSensorManager;
        sensorTypeFile = parentFile;
    }

//    public void print(){
//        List<Sensor> deviceSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
//        try {
//            FileWriter sensorTypeFileWriter = new FileWriter(sensorTypeFile, true);
//
//            for (Sensor x:deviceSensors) {
//                sensorTypeFileWriter.append(String.format("%s\n", String.valueOf(x)));
//                sensorTypeFileWriter.flush();
//            }
//            sensorTypeFileWriter.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public void print(){
        Sensor deviceSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        try {
            FileWriter sensorTypeFileWriter = new FileWriter(sensorTypeFile, true);
            sensorTypeFileWriter.append(String.format("%s\n", String.valueOf(deviceSensor)));
            sensorTypeFileWriter.flush();
            sensorTypeFileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
