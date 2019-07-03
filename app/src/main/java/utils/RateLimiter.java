package utils;

public class RateLimiter {
    private int frequency;
    private long lastAcquireTime;
    private long acquireThreshold;

    public RateLimiter(int frequency) {
        this.frequency = frequency;
        acquireThreshold = 1000 / frequency;
        if (acquireThreshold <= 0)
            acquireThreshold = 1;
    }

    public boolean acquire() {
        long currentTimeStamp = System.currentTimeMillis();
        long timeDiff = currentTimeStamp - lastAcquireTime;
        if (timeDiff >= acquireThreshold) {
            lastAcquireTime = currentTimeStamp;
            return true;
        } else {
            return false;
        }
    }
}
