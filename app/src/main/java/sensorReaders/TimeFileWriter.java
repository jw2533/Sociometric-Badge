package sensorReaders;

import android.content.Context;

import java.io.FileWriter;
import java.io.IOException;

public class TimeFileWriter {

    private DirectoryAndFile appFiles;
    private ValueStore valueStore;
    private FileWriter accelerometerFileWriter;

    public TimeFileWriter(Context parentContext, ValueStore parentValueStore) {
        appFiles = new DirectoryAndFile(parentContext);
        valueStore = parentValueStore;
    }

    public void writeAccelerometerFile() {
        try {
            accelerometerFileWriter = new FileWriter(appFiles.getAccelerometerFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeFileWriters() {
        try {
            accelerometerFileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
