package sensorReaders;

public interface MicrophoneSensorDataListener {
    public void onAudioData(short[] buffer, int dataLength, double amp, double freq);
}
