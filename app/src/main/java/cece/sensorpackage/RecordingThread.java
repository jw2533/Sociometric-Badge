package cece.sensorpackage;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;
import android.widget.Toast;

public class RecordingThread extends Thread {

	private static int FREQUENCY = 16000;
	private static int CHANNEL = AudioFormat.CHANNEL_IN_MONO;
	private static int ENCODING = AudioFormat.ENCODING_PCM_16BIT;
	private static int bufferSize = AudioRecord.getMinBufferSize(FREQUENCY,
			CHANNEL, ENCODING);

	private volatile boolean setToStopped = false;

	public void stopRecording() {
		this.setToStopped = true;
	}
	
	
	private double calculateVolume(short[] buffer){
		
		double sumVolume = 0.0;
		double avgVolume = 0.0;
		double volume = 0.0;
		for(short b : buffer){
			sumVolume += Math.abs(b);
		}
		avgVolume = sumVolume / buffer.length;	
		volume = Math.log10(1 + avgVolume) * 10;
		
		return volume;
	}
	
	private double calculateVolume(byte[] buffer){
		
		double sumVolume = 0.0;
		double avgVolume = 0.0;
		double volume = 0.0;
		for(int i = 0; i < buffer.length; i+=2){
			int v1 = buffer[i] & 0xFF;
			int v2 = buffer[i + 1] & 0xFF;
			int temp = v1 + (v2 << 8);
			if (temp >= 0x8000) {
				temp = 0xffff - temp;
			}
			sumVolume += Math.abs(temp);
		}
		avgVolume = sumVolume / buffer.length / 2;	
		volume = Math.log10(1 + avgVolume) * 10;
		
		return volume;
	}
	
	

	@Override
	public void run() {

		AudioRecord audioRecord = null;

		try {

			short[] buffer = new short[bufferSize];
			//byte[] buffer = new byte[bufferSize];
			
			audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
					FREQUENCY, CHANNEL, ENCODING, bufferSize);

			int state = audioRecord.getState();
			if (state == AudioRecord.STATE_INITIALIZED) {

				audioRecord.startRecording();

				while (!setToStopped) {

					int len = audioRecord.read(buffer, 0, buffer.length);
					short[] data = new short[len];
					//byte[] data = new byte[len];
					System.arraycopy(buffer, 0, data, 0, len);
					double volume = calculateVolume(data);
					Log.v("volume", "volume=" + volume);
				}

				audioRecord.stop();

			}

		} catch (Exception e) {

		} finally {

			audioRecord.release();
			audioRecord = null;

		}

	}

}
