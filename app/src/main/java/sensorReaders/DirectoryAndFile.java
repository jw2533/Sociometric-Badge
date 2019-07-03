package sensorReaders;

import android.content.Context;
import android.os.Environment;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import utils.HttpHelper;

public class DirectoryAndFile {
    private File sensorTypeFile;
    private File accelerometerFile;
    private File gyroscopeFile;
    private File rotationalVectorFile;
    private File gravityFile;
    private File linearAcceleratorFile;
    private File magnetometerFile;
    private File orientationFile;
    private File lightFile;
    private File positionFile;
    private File microphoneFile;

    private boolean isFileUploadServiceStarted;
    private Thread fileUploadThread;

    public File getSensorTypeFile() { return sensorTypeFile; }
    public File getOrientationFile() { return orientationFile; }
    public File getMagnetometerFile() { return magnetometerFile; }
    public File getAccelerometerFile() {
        return accelerometerFile;
    }
    public File getGyroscopeFile() {
        return gyroscopeFile;
    }
    public File getRotationalVectorFile() {
        return rotationalVectorFile;
    }
    public File getGravityFile() {
        return gravityFile;
    }
    public File getLinearAcceleratorFile() { return linearAcceleratorFile; }
    public File getLightFile() { return lightFile; }
    public File getPositionFile() {
        return positionFile;
    }
    public File getMicrophoneFile() { return microphoneFile; }

    public DirectoryAndFile(Context parentContext) {
        if (isExternalStorageWritable()) {

            // The data directory
            File root = new File(parentContext.getExternalFilesDir(null), "SENSOR_DATA");
            if (!root.exists()) {
                root.mkdirs();
            }

            sensorTypeFile = new File(root, "sensorTypeData.txt");
            if (!sensorTypeFile.exists()) {
                try {
                    sensorTypeFile.createNewFile();
                    FileWriter mSensorTypeFile = new FileWriter(sensorTypeFile, true);
                    mSensorTypeFile.append("This File Meant To Show All Available Sensors\n");
                    mSensorTypeFile.flush();
                    mSensorTypeFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else  {
                try {
                    sensorTypeFile.createNewFile();
                    FileWriter mSensorTypeFile = new FileWriter(sensorTypeFile, false);
                    mSensorTypeFile.append("This File Meant To Show All Available Sensors\n");
                    mSensorTypeFile.flush();
                    mSensorTypeFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            // Check accelerometer file exists or create one
            accelerometerFile = new File(root, "accelerometerData.csv");
            if (!accelerometerFile.exists()) {
                try {
                    accelerometerFile.createNewFile();
                    FileWriter mAccelerometerFileWriter = new FileWriter(accelerometerFile, true);
                    mAccelerometerFileWriter.append("Time_in_millisec_from_1970_01_01,X,Y,Z\n");
                    mAccelerometerFileWriter.flush();
                    mAccelerometerFileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // Check gyroscope file exists or create one
            gyroscopeFile = new File(root, "gyroscopeData.csv");
            if (!gyroscopeFile.exists()) {
                try {
                    gyroscopeFile.createNewFile();
                    FileWriter mGyroscopeFileWriter = new FileWriter(gyroscopeFile, true);
                    mGyroscopeFileWriter.append("Time_in_millisec_from_1970_01_01,X,Y,Z\n");
                    mGyroscopeFileWriter.flush();
                    mGyroscopeFileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // Check rotational vector file exists or create one
            rotationalVectorFile = new File(root, "rotationalVectorData.csv");
            if (!rotationalVectorFile.exists()) {
                try {
                    rotationalVectorFile.createNewFile();
                    FileWriter mRotationalVectorFileWriter = new FileWriter(rotationalVectorFile, true);
                    mRotationalVectorFileWriter.append("Time_in_millisec_from_1970_01_01,X,Y,Z\n");
                    mRotationalVectorFileWriter.flush();
                    mRotationalVectorFileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // Check gravity file exists or create one
            gravityFile = new File(root, "gravityData.csv");
            if (!gravityFile.exists()) {
                try {
                    gravityFile.createNewFile();
                    FileWriter mGravityFileWriter = new FileWriter(gravityFile, true);
                    mGravityFileWriter.append("Time_in_millisec_from_1970_01_01,X,Y,Z\n");
                    mGravityFileWriter.flush();
                    mGravityFileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // Check linear accelerator file exists or create one
            linearAcceleratorFile = new File(root, "linearAcceleratorData.csv");
            if (!linearAcceleratorFile.exists()) {
                try {
                    linearAcceleratorFile.createNewFile();
                    FileWriter mLinearAcceleratorFileWriter = new FileWriter(linearAcceleratorFile, true);
                    mLinearAcceleratorFileWriter.append("Time_in_millisec_from_1970_01_01,X,Y,Z\n");
                    mLinearAcceleratorFileWriter.flush();
                    mLinearAcceleratorFileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // Check magnetometer file exists or create one
            magnetometerFile = new File(root, "magnetometerData.csv");
            if (!magnetometerFile.exists()) {
                try {
                    magnetometerFile.createNewFile();
                    FileWriter mLinearAcceleratorFileWriter = new FileWriter(magnetometerFile, true);
                    mLinearAcceleratorFileWriter.append("Time_in_millisec_from_1970_01_01,X,Y,Z\n");
                    mLinearAcceleratorFileWriter.flush();
                    mLinearAcceleratorFileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // Check orientation sensor file exists or create one
            orientationFile = new File(root, "orientationData.csv");
            if (!orientationFile.exists()) {
                try {
                    orientationFile.createNewFile();
                    FileWriter mLinearAcceleratorFileWriter = new FileWriter(orientationFile, true);
                    mLinearAcceleratorFileWriter.append("Time_in_millisec_from_1970_01_01,X,Y,Z\n");
                    mLinearAcceleratorFileWriter.flush();
                    mLinearAcceleratorFileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // Check orientation sensor file exists or create one
            lightFile = new File(root, "lightData.csv");
            if (!lightFile.exists()) {
                try {
                    lightFile.createNewFile();
                    FileWriter mLightFileWriter = new FileWriter(lightFile, true);
                    mLightFileWriter.append("Time_in_millisec_from_1970_01_01,value\n");
                    mLightFileWriter.flush();
                    mLightFileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // Check position sensor file exists or create one
            positionFile = new File(root, "positionData.csv");
            if (!positionFile.exists()) {
                try {
                    positionFile.createNewFile();
                    FileWriter mPositionFileWriter = new FileWriter(positionFile, true);
                    mPositionFileWriter.append("Time_in_millisec_from_1970_01_01,X,Y,Z\n");
                    mPositionFileWriter.flush();
                    mPositionFileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            microphoneFile = new File(root, "microphoneData.csv");
            if (!microphoneFile.exists()) {
                try {
                    microphoneFile.createNewFile();
                    FileWriter mMicrophoneFileWriter = new FileWriter(microphoneFile, true);
                    mMicrophoneFileWriter.append("Time_in_millisec_from_1970_01_01,Amp,Freq\n");
                    mMicrophoneFileWriter.flush();
                    mMicrophoneFileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public void startFileUploadService() {
        if (isFileUploadServiceStarted)
            return;
        fileUploadThread = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpHelper.UploadTaskCompleteToken completeToken = null;

                Map<String, File> csvFiles = new HashMap<>();
                csvFiles.put("orientation", orientationFile);
                csvFiles.put("magnetometer", magnetometerFile);
                csvFiles.put("accelerometer", accelerometerFile);
                csvFiles.put("gyroscope", gyroscopeFile);
                csvFiles.put("rotation", rotationalVectorFile);
                csvFiles.put("gravity", gravityFile);
                csvFiles.put("linear_accelerator", linearAcceleratorFile);
                csvFiles.put("light", lightFile);
                csvFiles.put("position", positionFile);
                csvFiles.put("microphone", microphoneFile);

                while (isFileUploadServiceStarted) {
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (completeToken != null && !completeToken.getIsCompleted())
                        continue;

                    if (!isFileUploadServiceStarted)
                        return;

                    completeToken = HttpHelper.postFiles(csvFiles);
                }
            }
        });
        isFileUploadServiceStarted = true;
        fileUploadThread.start();
    }

    public void stopFileUploadService() {
        if (!isFileUploadServiceStarted)
            return;
        isFileUploadServiceStarted = false;
        try {
            fileUploadThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        fileUploadThread = null;
    }
}
