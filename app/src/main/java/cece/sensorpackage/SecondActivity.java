package cece.sensorpackage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import sensorReaders.DisplaySensorValuesInterface;
import sensorReaders.LightSensorReader;
import sensorReaders.LinearAcceleratorSensorReader;
import sensorReaders.MagnetometerSensorReader;
import sensorReaders.OrientationSensorReader;


public class SecondActivity extends AppCompatActivity {

    private MagnetometerSensorReader mMagnetometerSensorReader;
    private OrientationSensorReader mOrientationSensorReader;
    private LinearAcceleratorSensorReader mLinearAcceleratorSensorReader;
    private LightSensorReader mLightSensorReader;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        int sampleRate = 10000;
        int displayRate = 500000;

        TextView[] magnetometerTextView = new TextView[3];
        magnetometerTextView[0] = findViewById(R.id.magnetometerXTextView);
        magnetometerTextView[1] = findViewById(R.id.magnetometerYTextView);
        magnetometerTextView[2] = findViewById(R.id.magnetometerZTextView);
        Display3ValuesInTextView magnetometerDisplay = new Display3ValuesInTextView(magnetometerTextView);

        TextView[] orientationTextView = new TextView[3];
        orientationTextView[0] = findViewById(R.id.orientationXTextView);
        orientationTextView[1] = findViewById(R.id.orientationYTextView);
        orientationTextView[2] = findViewById(R.id.orientationZTextView);
        Display3ValuesInTextView orientationDisplay = new Display3ValuesInTextView(orientationTextView);

        TextView[] linearAcceleratorTextView = new TextView[3];
        linearAcceleratorTextView[0] = findViewById(R.id.linearAcceleratorXTextView);
        linearAcceleratorTextView[1] = findViewById(R.id.linearAcceleratorYTextView);
        linearAcceleratorTextView[2] = findViewById(R.id.linearAcceleratorZTextView);
        Display3ValuesInTextView linearAcceleratorDisplay = new Display3ValuesInTextView(linearAcceleratorTextView);

        TextView[] lightTextView = new TextView[1];
        lightTextView[0] = findViewById(R.id.lightTextView);
        Display1ValueInTextView lightDisplay = new Display1ValueInTextView(lightTextView);


        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mMagnetometerSensorReader = new MagnetometerSensorReader(true,
                                                                                            sensorManager,
                                                                                            magnetometerDisplay,
                                                                                            sampleRate,
                                                                                            displayRate);
        mOrientationSensorReader = new OrientationSensorReader(true,
                                                                                        sensorManager,
                                                                                        orientationDisplay,
                                                                                        sampleRate,
                                                                                        displayRate);
        mLinearAcceleratorSensorReader = new LinearAcceleratorSensorReader(true,
                                                                                                        sensorManager,
                                                                                                        linearAcceleratorDisplay,
                                                                                                        sampleRate,
                                                                                                        displayRate);
        mLightSensorReader = new LightSensorReader(true,
                                                                        sensorManager,
                                                                        lightDisplay,
                                                                        sampleRate,
                                                                        displayRate);

        mMagnetometerSensorReader.open();
        mOrientationSensorReader.open();
        mLinearAcceleratorSensorReader.open();
        mLightSensorReader.open();
    }

    private class Display3ValuesInTextView implements DisplaySensorValuesInterface {
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

    private class Display1ValueInTextView implements DisplaySensorValuesInterface {
        private TextView[] mDisplayLocations;

        Display1ValueInTextView(TextView[] displayLocations) {
            mDisplayLocations = displayLocations;
        }

        @SuppressLint("DefaultLocale")
        public void execute(float[] values) {
            mDisplayLocations[0].setText(String.format("%.5f", values[0]));
        }
    }

    public void go2FirstActivity(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
