package cece.sensorpackage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;

import sensorReaders.BluetoothDeviceFoundListener;
import sensorReaders.BluetoothDevicesReader;
import sensorReaders.BluetoothPositionSensor;
import sensorReaders.BluetoothSignalMetadata;
import utils.AutoFitTextureView;
import utils.CameraPreview;
import utils.Throttle;

// used for testing bluetooth device discovery
public class BluetoothDevicesActivity extends AppCompatActivity {
    private class ScrollViewScrollRunnable implements Runnable {
        private ScrollView scrollView;
        public ScrollViewScrollRunnable(ScrollView scrollView) {
            this.scrollView = scrollView;
        }

        @Override
        public void run() {
            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }
    }

    private BluetoothDevicesReader devicesReader;
    private Button scanStartButton;
    private Button scanStopButton;
    private TextView logTextView;
    private Throttle throttle;
    private AutoFitTextureView cameraPreviewTextureView;
    private CameraPreview cameraPreview;
    private boolean isCameraPreviewEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_devices);

        scanStartButton = findViewById(R.id.bluetoothScanStartButton);
        scanStopButton = findViewById(R.id.bluetoothScanStopButton);
        logTextView = findViewById(R.id.bluetoothScanHistory);

        devicesReader = new BluetoothDevicesReader();
        devicesReader.SetDeviceFoundListener(new BluetoothDeviceFoundListener() {
            @Override
            public void onDeviceFound(BluetoothSignalMetadata signalData) {
                String deviceInfo = formatMetadata(signalData);
                appendLog(deviceInfo);

            }
        });

        scanStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                devicesReader.startScan();
            }
        });

        scanStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                devicesReader.stopScan();
            }
        });

        appendLog("Everything is ready");

        cameraPreviewTextureView = findViewById(R.id.cameraPreviewTextureView);
        cameraPreview = new CameraPreview(this, cameraPreviewTextureView);
    }

    private void appendLog(String information) {
        String oldText = logTextView.getText().toString();
        if (oldText.length() <= 0)
            logTextView.setText(information);
        else
            logTextView.setText(oldText + '\n' + information);

        ScrollView scrollView = (ScrollView)logTextView.getParent();
        scrollView.post(new ScrollViewScrollRunnable(scrollView));
    }

    private String formatMetadata(BluetoothSignalMetadata metadata) {
        StringBuilder builder = new StringBuilder();
        builder.append("name: ");
        builder.append(metadata.getDeviceName());
        builder.append('\t');
        builder.append("mac: ");
        builder.append(metadata.getDeviceMac());
        builder.append('\t');
        builder.append("signal: ");
        builder.append(metadata.getSignalStrength());
        builder.append("dBm");
        builder.append('\t');
        builder.append("tx: ");
        int txPower = metadata.getTxPower();
        if (txPower == 127) {
            txPower = 0;
            builder.append("null");
        } else {
            builder.append(txPower);
            builder.append("dBm");
        }
        builder.append('\t');
        builder.append("dist: ");
        builder.append(metadata.getDistance());
        return builder.toString();
    }
}
