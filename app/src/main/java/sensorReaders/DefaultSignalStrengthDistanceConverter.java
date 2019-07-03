package sensorReaders;

// signal = -10 * n * log(d) + txPower
// where d is the distance to the beacon
// generally, n is 2 in free space
public class DefaultSignalStrengthDistanceConverter implements  SignalStrengthDistanceConverter {
    private int n;

    public DefaultSignalStrengthDistanceConverter(int n) {
        this.n = n;
    }

    public DefaultSignalStrengthDistanceConverter() {
        n = 2;
    }

    @Override
    public double toDistance(int signalStrength, int txPower) {
        return Math.pow(10, (txPower - signalStrength) / (10.0 * n));
    }
}
