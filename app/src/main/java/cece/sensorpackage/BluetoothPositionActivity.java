package cece.sensorpackage;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;

import sensorReaders.BluetoothApproachingListener;
import sensorReaders.BluetoothApproachingSensor;
import sensorReaders.BluetoothDeviceFoundListener;
import sensorReaders.BluetoothDevicePositionMetadata;
import sensorReaders.BluetoothDevicesReader;
import sensorReaders.BluetoothPositionSensor;
import sensorReaders.BluetoothSignalMetadata;
import sensorReaders.DeviceCoord;
import sensorReaders.DevicePositionListener;
import utils.AutoFitTextureView;
import utils.CameraPreview;
import utils.Throttle;

// used for testing bluetooth device discovery
public class BluetoothPositionActivity extends AppCompatActivity {
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

    private BluetoothPositionSensor positionSensor;
    private BluetoothApproachingSensor mApproachingReader;
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


        positionSensor = initializePositionSensor();
        positionSensor.setPositionListener(new DevicePositionListener() {
            @Override
            public void onPosition(DeviceCoord position) {
                String log = String.format("X: %.2f, Y: %.2f", (float)position.getX(), (float)position.getY());
                appendLog(log);
            }
        });
        mApproachingReader = positionSensor.getApproachingSensor();
        mApproachingReader.setDistanceThreshold(250);
        mApproachingReader.setListener(new BluetoothApproachingListener() {
            @Override
            public void onScanned(boolean hasNearDevice, int deviceCount, double distanceThreshold) {
                if (hasNearDevice) {
                    if (isCameraPreviewEnabled)
                        return;
                    cameraPreview.open();
                    isCameraPreviewEnabled = true;
                } else {
                    if (!isCameraPreviewEnabled)
                        return;
                    cameraPreview.close();
                    isCameraPreviewEnabled = false;
                }
            }
        });

        scanStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                positionSensor.open();
                mApproachingReader.open();
            }
        });

        scanStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApproachingReader.close();
                positionSensor.close();
            }
        });

        appendLog("Everything is ready");

        cameraPreviewTextureView = findViewById(R.id.cameraPreviewTextureView);
        cameraPreview = new CameraPreview(this, cameraPreviewTextureView);
    }

    private BluetoothPositionSensor initializePositionSensor()
    {
        ArrayList<BluetoothDevicePositionMetadata> beacons = new ArrayList<>();
        beacons.add(new BluetoothDevicePositionMetadata("1A:2C:D9:DD:17:13", 0, 0));
        beacons.add(new BluetoothDevicePositionMetadata("0E:59:1C:A3:BF:36", 4, 0));
        beacons.add(new BluetoothDevicePositionMetadata("2C:58:57:DD:3E:42", 0, 4));
        beacons.add(new BluetoothDevicePositionMetadata("48:79:BD:3E:1F:96", 2, 2));
        return new BluetoothPositionSensor(
                this,
                beacons,
                null,
                null,
                10,
                null,
                null
        );
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
}
