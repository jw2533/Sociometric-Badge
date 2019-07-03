package sensorReaders;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import utils.FFT;
import utils.RateLimiter;

public class MicrophoneSensorReader {
    private final ValueStore parentValueStore;
    private DisplaySensorValuesInterface parentDisplay;
    private final int parentSampleRate;
    private final int audioSampleRate;
    private File microphoneDataFile;
    private FileWriter microphoneDataFileWriter;
    private MicrophoneSensorDataListener listener;
    private Handler mainHandler;

    private boolean shouldRecording;
    private Thread runningThread;
    private FFT fft;
    private RateLimiter rateLimiter;
    private double[] fftReal;
    private double[] fftImage;
    private double[] fftAbs;

    public MicrophoneSensorReader(
            Context context,
            ValueStore parentValueStore,
            DisplaySensorValuesInterface parentDisplay,
            MicrophoneSensorDataListener listener,
            int parentSampleRate,
            int audioSampleRate,
            File parentFile) {
        this.parentValueStore = parentValueStore;
        this.parentDisplay = parentDisplay;
        this.parentSampleRate = parentSampleRate;
        this.audioSampleRate = audioSampleRate;
        this.microphoneDataFile = parentFile;
        mainHandler = new Handler(context.getMainLooper());
        this.listener = listener;
        rateLimiter = new RateLimiter(parentSampleRate);
    }

    public void setListener(MicrophoneSensorDataListener listener) {
        this.listener = listener;
    }

    public void open() {
        shouldRecording = true;
        runningThread = new Thread(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);

                int bufferSize = AudioRecord.getMinBufferSize(audioSampleRate,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT);
                if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
                    bufferSize = audioSampleRate * 2;
                }
                int arraySize = bufferSize / 2;
                short[] audioBuffer = new short[arraySize];
                fft = FFT.createFFTWithLength(audioBuffer.length);
                fftReal = new double[fft.getN()];
                fftImage = new double[fft.getN()];
                fftAbs = new double[fft.getN()];
                AudioRecord record = new AudioRecord(MediaRecorder.AudioSource.DEFAULT,
                        audioSampleRate,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT,
                        bufferSize);
                if (record.getState() != AudioRecord.STATE_INITIALIZED) {
                    Log.e("RECORD", "Audio Record can't initialize!");
                    return;
                }
                record.startRecording();
                Log.v("RECORD", "Start recording");

                long shortsRead = 0;
                while (shouldRecording) {
                    int numberOfShort = record.read(audioBuffer, 0, audioBuffer.length);
                    shortsRead += numberOfShort;
                    onAudioDataRead(audioBuffer, numberOfShort);
                }

                record.stop();
                record.release();
                Log.v("RECORD", String.format("Recording stopped. Samples read: %d", shortsRead));
            }
        });
        if (microphoneDataFile != null) {
            try {
                microphoneDataFileWriter = new FileWriter(microphoneDataFile, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        runningThread.start();
    }

    public void stop() {
        shouldRecording = false;
        try {
            runningThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        runningThread = null;
        if (microphoneDataFileWriter != null) {
            try {
                microphoneDataFileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void onAudioDataRead(final short[] buffer, final int length) {
        if (!rateLimiter.acquire())
            return;

        Arrays.fill(fftReal, 0);
        Arrays.fill(fftImage, 0);
        Arrays.fill(fftAbs, 0);
        for (int i = 0; i < length; i++)
            fftReal[i] = buffer[i];
        fft.fft(fftReal, fftImage);

        int arrayLength = fftReal.length;
        double a, b, abs;
        for (int i = 0; i < arrayLength; i++) {
            a = fftReal[i];
            b = fftImage[i];
            abs = Math.sqrt(a * a + b * b);
            fftAbs[i] = abs;
        }

        int halfArrayLength = arrayLength / 2;
        double freqStep = (double) audioSampleRate / halfArrayLength;
        int freqWindowSize = 10;
        int windowSampleSize = (int) Math.ceil(freqWindowSize / freqStep);
        double absSum = 0;
        for (int i = 0; i < windowSampleSize; i++)
            absSum += fftAbs[i];
        double maxSum = absSum;
        int maxIndex = 0;
        for (int i = windowSampleSize, j = 0; i < halfArrayLength; i++, j++) {
            absSum -= fftAbs[j];
            absSum += fftAbs[i];
            if (absSum > maxSum) {
                maxSum = absSum;
                maxIndex = j + 1;
            }
        }
        maxIndex += windowSampleSize / 2;
        final double maxFreq = maxIndex * freqStep;
        final double maxAmp = maxSum;
        final float[] microphoneValues = new float[]{(float) maxAmp, (float) maxFreq};
        parentValueStore.setMicrophoneValues(microphoneValues);
        if (microphoneDataFileWriter != null) {
            try {
                microphoneDataFileWriter.append(
                        String.format("%s,%s,%s\n",
                                String.valueOf(System.currentTimeMillis()),
                                String.valueOf(microphoneValues[0]),
                                String.valueOf(microphoneValues[1])));
                microphoneDataFileWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (this.listener != null || this.parentDisplay != null) {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (listener != null)
                        listener.onAudioData(buffer, length, maxAmp, maxFreq);
                    if (parentDisplay != null)
                        parentDisplay.execute(microphoneValues);
                }
            });
        }
    }
}
