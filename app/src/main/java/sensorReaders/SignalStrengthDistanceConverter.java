package sensorReaders;

public interface SignalStrengthDistanceConverter {
    public double toDistance(int signalStrength, int txPower);
}
