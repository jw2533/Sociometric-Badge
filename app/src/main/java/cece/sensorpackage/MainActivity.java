package cece.sensorpackage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;

import java.util.ArrayList;

import sensorReaders.AccelerometerSensorReader;
import sensorReaders.BluetoothApproachingListener;
import sensorReaders.BluetoothApproachingSensor;
import sensorReaders.BluetoothDevicePositionMetadata;
import sensorReaders.BluetoothPositionSensor;
import sensorReaders.LightSensorReader;
import sensorReaders.MagnetometerSensorReader;
import sensorReaders.LinearAcceleratorSensorReader;
import sensorReaders.DirectoryAndFile;
import sensorReaders.DisplaySensorValuesInterface;
import sensorReaders.GravitySensorReader;
import sensorReaders.GyroscopeSensorReader;
import sensorReaders.MicrophoneSensorReader;
import sensorReaders.OrientationSensorReader;
import sensorReaders.RotationalVectorSensorReader;
import sensorReaders.SensorPrinter;
import sensorReaders.ValueStore;
import utils.AudioHelper;
import utils.AutoFitTextureView;
import utils.CameraHelper;
import utils.CameraPreview;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private AccelerometerSensorReader mAccelerometerSensorReader;
    private GyroscopeSensorReader mGyroscopeSensorReader;
    private GravitySensorReader mGravitySensorReader;
    private RotationalVectorSensorReader mRotationalVectorSensorReader;
    private LinearAcceleratorSensorReader mLinearAcceleratorSensorReader;
    private MagnetometerSensorReader mMagnetometerSensorReader;
    private OrientationSensorReader mOrientationSensorReader;
    private LightSensorReader mLightSensorReader;
    private BluetoothPositionSensor mBluetoothPositionReader;
    private BluetoothApproachingSensor mApproachingReader;
    private MicrophoneSensorReader mMicrophoneReader;
    ValueStore mValueStore;

//    private int mInterval = 1000; // 5 seconds by default, can be changed later
//    private Handler mHandler;

    private DirectoryAndFile dataFiles;

    private Button startBtn;
    private Button stopBtn;
    private TextView result;
    private Button saoyisao;
    private RecordingThread recordingThread;
    private AutoFitTextureView cameraPreviewTextureView;
    private CameraPreview cameraPreview;
    private boolean isCameraPreviewEnabled;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        startBtn = (Button) findViewById(R.id.start_btn);
        startBtn.setOnClickListener(this);

        stopBtn = (Button) findViewById(R.id.stop_btn);
        stopBtn.setOnClickListener(this);

        saoyisao = findViewById(R.id.saoyisao);
        saoyisao.setOnClickListener(this);

        result = findViewById(R.id.pinglv);

        cameraPreviewTextureView = findViewById(R.id.cameraPreviewTextureView);
        cameraPreview = new CameraPreview(this, cameraPreviewTextureView);

        if (!CameraHelper.hasCameraPermission(this)) {
            CameraHelper.requestPermission(this, 10);
        }
        if (!AudioHelper.hasAudioPermission(this)) {
            AudioHelper.requestPermission(this, 10);
        }

        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//      mHandler = new Handler();

        int sampleRate = 10000;
        int displayRate = 500000;
        int writeRate = 100000;

        mValueStore = new ValueStore();
        dataFiles = new DirectoryAndFile(this);

        SensorPrinter mSensorPrinter = new SensorPrinter(sensorManager, dataFiles.getSensorTypeFile());
        mSensorPrinter.print();

        TextView[] accelerometerTextView = new TextView[3];
        accelerometerTextView[0] = findViewById(R.id.magnetometerXTextView);
        accelerometerTextView[1] = findViewById(R.id.magnetometerYTextView);
        accelerometerTextView[2] = findViewById(R.id.magnetometerZTextView);
        Display3ValuesInTextView accelerometerDisplay = new Display3ValuesInTextView(accelerometerTextView);

        TextView[] gyroscopeTextView = new TextView[3];
        gyroscopeTextView[0] = findViewById(R.id.linearAcceleratorXTextView);
        gyroscopeTextView[1] = findViewById(R.id.linearAcceleratorYTextView);
        gyroscopeTextView[2] = findViewById(R.id.linearAcceleratorZTextView);
        Display3ValuesInTextView gyroscopeDisplay = new Display3ValuesInTextView(gyroscopeTextView);

        TextView[] gravityTextView = new TextView[3];
        gravityTextView[0] = findViewById(R.id.orientationXTextView);
        gravityTextView[1] = findViewById(R.id.orientationYTextView);
        gravityTextView[2] = findViewById(R.id.orientationZTextView);
        Display3ValuesInTextView gravityDisplay = new Display3ValuesInTextView(gravityTextView);

        TextView[] rotationalVectorTextView = new TextView[3];
        rotationalVectorTextView[0] = findViewById(R.id.rotationalVectorXTextView);
        rotationalVectorTextView[1] = findViewById(R.id.rotationalVectorYTextView);
        rotationalVectorTextView[2] = findViewById(R.id.rotationalVectorZTextView);
        Display3ValuesInTextView rotationalVectorDisplay = new Display3ValuesInTextView(rotationalVectorTextView);

        mAccelerometerSensorReader = new AccelerometerSensorReader(mValueStore,
                                                                 accelerometerDisplay,
                                                                 sensorManager,
                                                                 sampleRate,
                                                                 displayRate,
                                                                 writeRate,
                                                                 dataFiles.getAccelerometerFile());
        mGyroscopeSensorReader = new GyroscopeSensorReader(mValueStore,
                                                         gyroscopeDisplay,
                                                         sensorManager,
                                                         sampleRate,
                                                         displayRate,
                                                         writeRate,
                                                         dataFiles.getGyroscopeFile());
        mGravitySensorReader = new GravitySensorReader(mValueStore,
                                                       gravityDisplay,
                                                       sensorManager,
                                                       sampleRate,
                                                       displayRate,
                                                       writeRate,
                                                       dataFiles.getGravityFile());
        mRotationalVectorSensorReader = new RotationalVectorSensorReader(mValueStore,
                                                                      rotationalVectorDisplay,
                                                                      sensorManager,
                                                                      sampleRate,
                                                                      displayRate,
                                                                      writeRate,
                                                                      dataFiles.getRotationalVectorFile());
        mLinearAcceleratorSensorReader = new LinearAcceleratorSensorReader(mValueStore,
                                                                            sensorManager,
                                                                            sampleRate,
                                                                            writeRate,
                                                                            dataFiles.getLinearAcceleratorFile());
        mMagnetometerSensorReader = new MagnetometerSensorReader(mValueStore,
                                                                sensorManager,
                                                                sampleRate,
                                                                writeRate,
                                                                dataFiles.getMagnetometerFile());
        mOrientationSensorReader = new OrientationSensorReader(mValueStore,
                                                                sensorManager,
                                                                sampleRate,
                                                                writeRate,
                                                                dataFiles.getOrientationFile());
        mLightSensorReader = new LightSensorReader(mValueStore,
                                                sensorManager,
                                                sampleRate,
                                                writeRate,
                                                dataFiles.getLightFile());

        mBluetoothPositionReader = initializePositionSensor(new DeviceCoordDisplay((TextView) findViewById(R.id.devicePositionTextView)), dataFiles);
        mApproachingReader = mBluetoothPositionReader.getApproachingSensor();
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

        mMicrophoneReader = new MicrophoneSensorReader(this, mValueStore,
                new AmpFreqDisplay((TextView)findViewById(R.id.microphoneDataTextView)),
                null, 10,44100,
                dataFiles.getMicrophoneFile());
    }

    private BluetoothPositionSensor initializePositionSensor(DisplaySensorValuesInterface display, DirectoryAndFile dataFiles)
    {
        ArrayList<BluetoothDevicePositionMetadata> beacons = new ArrayList<>();
        beacons.add(new BluetoothDevicePositionMetadata("1C:FB:3D:75:6A:4E", 0, 0));
        beacons.add(new BluetoothDevicePositionMetadata("18:42:68:58:E2:79", 4, 0));
        beacons.add(new BluetoothDevicePositionMetadata("35:8D:20:B8:56:65", 0, 4));
        beacons.add(new BluetoothDevicePositionMetadata("33:AB:AD:F8:DE:93", 2, 2));
        return new BluetoothPositionSensor(
                this,
                beacons,
                mValueStore,
                display,
                10,
                dataFiles.getPositionFile(),
                null
        );
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.start_btn:

                startBtn.setVisibility(View.INVISIBLE);
                stopBtn.setVisibility(View.VISIBLE);

                recordingThread = new RecordingThread();
                recordingThread.start();

                break;

            case R.id.stop_btn:

                startBtn.setVisibility(View.VISIBLE);
                stopBtn.setVisibility(View.INVISIBLE);

                recordingThread.stopRecording();

                break;
            case R.id.saoyisao:

                Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                startActivityForResult(intent, 0);
		}


    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (data != null) {
                String content = data.getStringExtra(Constant.CODED_CONTENT);
                result.setText("扫描结果为：" + content);
            }
        }
    }







    private class Display3ValuesInTextView implements DisplaySensorValuesInterface{
        private TextView[] mDisplayLocations;

        Display3ValuesInTextView(TextView[] displayLocations) {
            mDisplayLocations = displayLocations;
        }

        @SuppressLint("DefaultLocale")
        public void execute(float[] values) {
            mDisplayLocations[0].setText(String.format("%.5f", values[0]));
            mDisplayLocations[1].setText(String.format("%.5f", values[1]));
            mDisplayLocations[2].setText(String.format("%.5f", values[2]));
        }
    }

    private class DeviceCoordDisplay implements DisplaySensorValuesInterface {

        private TextView mDisplayingTextView;

        private DeviceCoordDisplay(TextView displayingTextView) {
            this.mDisplayingTextView = displayingTextView;
        }

        @Override
        public void execute(float[] values) {
            mDisplayingTextView.setText(String.format("%.2f, %.2f", values[0], values[1]));
        }
    }
    private class AmpFreqDisplay implements DisplaySensorValuesInterface {

        private TextView mDisplayingTextView;

        private AmpFreqDisplay(TextView displayingTextView) {
            this.mDisplayingTextView = displayingTextView;
        }

        @Override
        public void execute(float[] values) {
            mDisplayingTextView.setText(String.format("%.2f, %.2f", values[0], values[1]));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void startSensors(View view) {
        mAccelerometerSensorReader.open();
        mGyroscopeSensorReader.open();
        mGravitySensorReader.open();
        mRotationalVectorSensorReader.open();
        mLinearAcceleratorSensorReader.open();
        mMagnetometerSensorReader.open();
        mOrientationSensorReader.open();
        mLightSensorReader.open();
        mApproachingReader.open();
        mBluetoothPositionReader.open();
        mMicrophoneReader.open();

        dataFiles.startFileUploadService();
    //        mLogger.run();
    }

    public void endSensors(View view) {
        dataFiles.stopFileUploadService();

        mAccelerometerSensorReader.close();
        mGyroscopeSensorReader.close();
        mGravitySensorReader.close();
        mRotationalVectorSensorReader.close();
        mLinearAcceleratorSensorReader.close();
        mMagnetometerSensorReader.close();
        mOrientationSensorReader.close();
        mLightSensorReader.close();
        mBluetoothPositionReader.close();
        mApproachingReader.close();
        if (isCameraPreviewEnabled) {
            cameraPreview.close();
            isCameraPreviewEnabled = false;
        }
        mMicrophoneReader.stop();
    //        mHandler.removeCallbacks(mLogger);
    }

    public void go2SecondActivity(View view) {
        Intent intent = new Intent(this, SecondActivity.class);
        startActivity(intent);
    }

    //        private Runnable mLogger = new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    TextView temp = findViewById(R.id.testTextView);
//                    temp.setText(String.valueOf(mValueStore.getAccelerometerValues()[0]));
//                } finally {
//                    mHandler.postDelayed(mLogger, mInterval);
//                }
//            }
//        };
}
