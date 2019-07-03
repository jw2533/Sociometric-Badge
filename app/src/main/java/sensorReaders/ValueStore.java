package sensorReaders;

public class ValueStore {

    private float[] accelerometerValues = new float[3];
    private float[] gyroscopeValues = new float[3];
    private float[] gravityValues = new float[3];
    private float[] rotationalVectorValues = new float[3];
    private float[] magnetometerValues = new float[3];
    private float[] linearAccelerometerValues = new float[3];
    private float[] orientationValues = new float[3];
    private float[] lightValues = new float[1];
    private float[] temperatureValues = new float[1];
    private float[] humidityValues = new float[1];
    private float[] bluetoothPositionValues = new float[3];
    private float[] microphoneValues = new float[2];


    public float[] getGyroscopeValues() {
        return gyroscopeValues;
    }
    public float[] getAccelerometerValues() {
        return accelerometerValues;
    }
    public float[] getGravityValues() {
        return gravityValues;
    }
    public float[] getRotationalVectorValues() {
        return rotationalVectorValues;
    }
    public float[] getMagnetometerValues() { return magnetometerValues; }
    public float[] getLinearAccelerometerValues() { return linearAccelerometerValues; }
    public float[] getOrientationValues() { return orientationValues; }
    public float[] getLightValues() { return lightValues; }
    public float[] getTemperatureValues() { return temperatureValues; }
    public float[] getHumidityValues() { return humidityValues; }
    public float[] getBluetoothPositionValues() { return bluetoothPositionValues; }
    public float[] getMicrophoneValues() { return microphoneValues; }


    public void setAccelerometerValues(float[] accelerometerValue) {
        this.accelerometerValues = accelerometerValue;
    }
    public void setGyroscopeValues(float[] gyroscopeValues) {
        this.gyroscopeValues = gyroscopeValues;
    }
    public void setGravityValues(float[] gravityValues) {
        this.gravityValues = gravityValues;
    }
    public void setRotationalVectorValues(float[] rotationalVectorValues) {
        this.rotationalVectorValues = rotationalVectorValues;
    }
    public void setMagnetometerValues(float[] magnetometerValues) {
        this.magnetometerValues = magnetometerValues;
    }
    public void setLinearAccelerometerValues(float[] linearAccelerometerValues) {
        this.linearAccelerometerValues = linearAccelerometerValues;
    }
    public void setOrientationValues(float[] orientationValues) {
        this.orientationValues = orientationValues;
    }
    public void setLightValues(float[] lightValues) {
        this.lightValues = lightValues;
    }
    public void setTemperatureValues(float[] temperatureValues) {
        this.temperatureValues = temperatureValues;
    }
    public void setHumidityValues(float[] humidityValues) {
        this.humidityValues = humidityValues;
    }
    public void setBluetoothPositionValues(float[] bluetoothPositionValues) {
        this.bluetoothPositionValues = bluetoothPositionValues;
    }
    public void setMicrophoneValues(float[] microphoneValues) {
        this.microphoneValues = microphoneValues;
    }

}
