package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements SensorEventListener {

    // System sensor manager instance.
    private SensorManager mSensorManager;

    // Accelerometer sensor
    private Sensor mSensorAccelerometerX;
    private Sensor mSensorAccelerometerY;
    private Sensor mSensorAccelerometerZ;

    // Gravity sensor
    private Sensor mSensorGravityX;
    private Sensor mSensorGravityY;
    private Sensor mSensorGravityZ;

    // Linear accelerometer sensor
    private Sensor mSensorLinearAccelerometerX;
    private Sensor mSensorLinearAccelerometerY;
    private Sensor mSensorLinearAccelerometerZ;

    // Gyroscope sensor
    private Sensor mSensorGyroscopeX;
    private Sensor mSensorGyroscopeY;
    private Sensor mSensorGyroscopeZ;

    // Magnetometer sensor
    private Sensor mSensorMagneticX;
    private Sensor mSensorMagneticY;
    private Sensor mSensorMagneticZ;


    // TextViews to display current sensor values.
    private TextView mTextSensorAccelerometerX;
    private TextView mTextSensorAccelerometerY;
    private TextView mTextSensorAccelerometerZ;

    private TextView mTextSensorGravityX;
    private TextView mTextSensorGravityY;
    private TextView mTextSensorGravityZ;

    private TextView mTextSensorLinearAccelerometerX;
    private TextView mTextSensorLinearAccelerometerY;
    private TextView mTextSensorLinearAccelerometerZ;

    private TextView mTextSensorGyroscopeX;
    private TextView mTextSensorGyroscopeY;
    private TextView mTextSensorGyroscopeZ;

    private TextView mTextSensorMagneticX;
    private TextView mTextSensorMagneticY;
    private TextView mTextSensorMagneticZ;

    private TextView mTextSensorAzimuth;
    private TextView mTextSensorPitch;
    private TextView mTextSensorRoll;

    // try to save sensor data into files
    File dir;

    // storage permission
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSION_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    // accelerometer array
    private final float[] accelerometerArray = new float[3];
    // magnetometer array
    private final float[] magnetometerArray = new float[3];
    // rotation matrix
    private final float[] rotationMatrix = new float[9];
    // orientation angels
    private final float[] orientationAngles = new float[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // check storage permission
        int permission = ActivityCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this, PERMISSION_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }

        // set file path
        dir = new File(getExternalFilesDir(null),"/SensorData");

        String entry = "SENSOR, X_AXIS, Y_AXIS, Z_AXIS\n";
        File accefile = new File(dir, "Accelerometer.txt");
        File gravfile = new File(dir, "Gravity.txt");
        File gyrofile = new File(dir, "Gyroscope.txt");
        File rotafile = new File(dir, "Orientation.txt");
        File magfile = new File(dir, "Magnetic.txt");
        File liaccefile = new File(dir, "LinearAccelerometer.txt");

        // write head entry to file
        ToFile(accefile, false, entry);
        ToFile(gravfile, false, entry);
        ToFile(gyrofile, false, entry);
        ToFile(rotafile, false, entry);
        ToFile(magfile, false, entry);
        ToFile(liaccefile, false, entry);

        // initialize all view variable
        mTextSensorAccelerometerX = (TextView) findViewById(R.id.accelerometer_x);
        mTextSensorAccelerometerY = (TextView) findViewById(R.id.accelerometer_y);
        mTextSensorAccelerometerZ = (TextView) findViewById(R.id.accelerometer_z);

        mTextSensorGravityX = (TextView) findViewById(R.id.gravity_x);
        mTextSensorGravityY = (TextView) findViewById(R.id.gravity_y);
        mTextSensorGravityZ = (TextView) findViewById(R.id.gravity_z);

        mTextSensorLinearAccelerometerX = (TextView) findViewById(R.id.linear_accelerometer_x);
        mTextSensorLinearAccelerometerY = (TextView) findViewById(R.id.linear_accelerometer_y);
        mTextSensorLinearAccelerometerZ = (TextView) findViewById(R.id.linear_accelerometer_z);

        mTextSensorGyroscopeX = (TextView) findViewById(R.id.gyroscope_x);
        mTextSensorGyroscopeY = (TextView) findViewById(R.id.gyroscope_y);
        mTextSensorGyroscopeZ = (TextView) findViewById(R.id.gyroscope_z);

        mTextSensorMagneticX = (TextView) findViewById(R.id.magnetic_x);
        mTextSensorMagneticY = (TextView) findViewById(R.id.magnetic_y);
        mTextSensorMagneticZ = (TextView) findViewById(R.id.magnetic_z);

        mTextSensorAzimuth = (TextView) findViewById(R.id.azimuth);
        mTextSensorPitch = (TextView) findViewById(R.id.pitch);
        mTextSensorRoll = (TextView) findViewById(R.id.roll);

        // sensor manager instance
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // retrieve sensors from sensor manager
        // return null if the sensor is not available in this device
        mSensorAccelerometerX = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorAccelerometerY = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorAccelerometerZ = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mSensorGravityX = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        mSensorGravityY = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        mSensorGravityZ = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

        mSensorLinearAccelerometerX = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mSensorLinearAccelerometerY = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mSensorLinearAccelerometerZ = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        mSensorGyroscopeX = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mSensorGyroscopeY = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mSensorGyroscopeZ = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        mSensorMagneticX = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mSensorMagneticY = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mSensorMagneticZ = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);


        // error message displayed
        String sensor_error = getResources().getString(R.string.error_no_sensor);

        if (mSensorAccelerometerX == null) { mTextSensorAccelerometerX.setText(sensor_error); }
        if (mSensorAccelerometerY == null) { mTextSensorAccelerometerY.setText(sensor_error); }
        if (mSensorAccelerometerZ == null) { mTextSensorAccelerometerZ.setText(sensor_error); }

        if (mSensorGravityX == null) { mTextSensorGravityX.setText(sensor_error); }
        if (mSensorGravityY == null) { mTextSensorGravityY.setText(sensor_error); }
        if (mSensorGravityZ == null) { mTextSensorGravityZ.setText(sensor_error); }

        if (mSensorLinearAccelerometerX == null) {mTextSensorLinearAccelerometerX.setText(sensor_error);}
        if (mSensorLinearAccelerometerY == null) {mTextSensorLinearAccelerometerX.setText(sensor_error);}
        if (mSensorLinearAccelerometerZ == null) {mTextSensorLinearAccelerometerX.setText(sensor_error);}

        if (mSensorGyroscopeX == null) { mTextSensorGyroscopeX.setText(sensor_error); }
        if (mSensorGyroscopeY == null) { mTextSensorGyroscopeY.setText(sensor_error); }
        if (mSensorGyroscopeZ == null) { mTextSensorGyroscopeZ.setText(sensor_error); }

        if (mSensorMagneticX == null) {mTextSensorMagneticX.setText(sensor_error);}
        if (mSensorMagneticY == null) {mTextSensorMagneticY.setText(sensor_error);}
        if (mSensorMagneticZ == null) {mTextSensorMagneticZ.setText(sensor_error);}

    }

    @Override
    protected void onStart() {
        super.onStart();

        // register sensor listener
        if (mSensorAccelerometerX != null) {
            mSensorManager.registerListener(this, mSensorAccelerometerX,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (mSensorAccelerometerY != null) {
            mSensorManager.registerListener(this, mSensorAccelerometerY,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (mSensorAccelerometerZ != null) {
            mSensorManager.registerListener(this, mSensorAccelerometerZ,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }

        if (mSensorGravityX != null) {
            mSensorManager.registerListener(this, mSensorGravityX,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (mSensorGravityY != null) {
            mSensorManager.registerListener(this, mSensorGravityY,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (mSensorGravityZ != null) {
            mSensorManager.registerListener(this, mSensorGravityZ,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }

        if (mSensorLinearAccelerometerX != null) {
            mSensorManager.registerListener(this, mSensorLinearAccelerometerX,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (mSensorLinearAccelerometerY != null) {
            mSensorManager.registerListener(this, mSensorLinearAccelerometerY,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (mSensorLinearAccelerometerY != null) {
            mSensorManager.registerListener(this, mSensorLinearAccelerometerY,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }

        if (mSensorGyroscopeX != null) {
            mSensorManager.registerListener(this, mSensorGyroscopeX,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (mSensorGyroscopeY != null) {
            mSensorManager.registerListener(this, mSensorGyroscopeY,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (mSensorGyroscopeZ != null) {
            mSensorManager.registerListener(this, mSensorGyroscopeZ,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }

        if (mSensorMagneticX != null) {
            mSensorManager.registerListener(this, mSensorMagneticX,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (mSensorMagneticY != null) {
            mSensorManager.registerListener(this, mSensorMagneticY,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (mSensorMagneticZ != null) {
            mSensorManager.registerListener(this, mSensorMagneticZ,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        // unregister all sensor listeners
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        int sensorType = sensorEvent.sensor.getType();

        // get each data value of the sensor.
        float currentValue1 = sensorEvent.values[0];
        float currentValue2 = sensorEvent.values[1];
        float currentValue3 = sensorEvent.values[2];

        String entry = "";
        File outfile = new File(dir, "null.txt");

        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                mTextSensorAccelerometerX.setText(getResources().getString(R.string.accelerometer_x, currentValue1));
                mTextSensorAccelerometerY.setText(getResources().getString(R.string.accelerometer_y, currentValue2));
                mTextSensorAccelerometerZ.setText(getResources().getString(R.string.accelerometer_z, currentValue3));
                outfile = new File(dir, "Accelerometer.txt");
                System.arraycopy(sensorEvent.values, 0, accelerometerArray, 0, accelerometerArray.length);
                entry = String.format(Locale.CHINA,"accelerometer, %f, %f, %f\n", currentValue1, currentValue2, currentValue3);
                break;
            case Sensor.TYPE_GRAVITY:
                mTextSensorGravityX.setText(getResources().getString(R.string.gravity_x, currentValue1));
                mTextSensorGravityY.setText(getResources().getString(R.string.gravity_y, currentValue2));
                mTextSensorGravityZ.setText(getResources().getString(R.string.gravity_z, currentValue3));
                outfile = new File(dir, "Gravity.txt");
                entry = String.format(Locale.CHINA,"gravity, %f, %f, %f\n", currentValue1, currentValue2, currentValue3);
                break;
            case Sensor.TYPE_LINEAR_ACCELERATION:
                mTextSensorLinearAccelerometerX.setText(getResources().getString(R.string.linear_accelerometer_x, currentValue1));
                mTextSensorLinearAccelerometerY.setText(getResources().getString(R.string.linear_accelerometer_y, currentValue2));
                mTextSensorLinearAccelerometerZ.setText(getResources().getString(R.string.linear_accelerometer_z, currentValue3));
                outfile = new File(dir, "LinearAccelerometer.txt");
                System.arraycopy(sensorEvent.values, 0, accelerometerArray, 0, accelerometerArray.length);
                entry = String.format(Locale.CHINA,"linear accelerometer, %f, %f, %f\n", currentValue1, currentValue2, currentValue3);
                break;
            case Sensor.TYPE_GYROSCOPE:
                mTextSensorGyroscopeX.setText(getResources().getString(R.string.gyroscope_x, currentValue1));
                mTextSensorGyroscopeY.setText(getResources().getString(R.string.gyroscope_y, currentValue2));
                mTextSensorGyroscopeZ.setText(getResources().getString(R.string.gyroscope_z, currentValue3));
                outfile = new File(dir, "Gyroscope.txt");
                entry = String.format(Locale.CHINA,"gyroscope, %f, %f, %f\n", currentValue1, currentValue2, currentValue3);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                mTextSensorMagneticX.setText(getResources().getString(R.string.magnetic_x, currentValue1));
                mTextSensorMagneticY.setText(getResources().getString(R.string.magnetic_y, currentValue2));
                mTextSensorMagneticZ.setText(getResources().getString(R.string.magnetic_z, currentValue3));
                outfile = new File(dir, "Magnetic.txt");
                System.arraycopy(sensorEvent.values, 0, magnetometerArray, 0, magnetometerArray.length);
                entry = String.format(Locale.CHINA, "magnetic, %f, %f, %f\n", currentValue1, currentValue2, currentValue3);
                break;
            default:
                // leave it
        }

        // update orientation angles
        SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerArray, magnetometerArray);
        SensorManager.getOrientation(rotationMatrix, orientationAngles);
        mTextSensorAzimuth.setText(getResources().getString(R.string.azimuth, orientationAngles[0]));
        mTextSensorPitch.setText(getResources().getString(R.string.pitch, orientationAngles[1]));
        mTextSensorRoll.setText(getResources().getString(R.string.roll, orientationAngles[2]));

        // save data (except orientation data) to files
        ToFile(outfile, true, entry);
        // save orientation angels to data
        String entry2 = String.format(Locale.CHINA, "orientation angles, %f, %f, %f\n", orientationAngles[0], orientationAngles[1], orientationAngles[2]);
        outfile = new File(dir, "orientation.txt");
        ToFile(outfile, true, entry2);
    }

    public void ToFile(File file, boolean append, String entry) {
        try {
            FileOutputStream f = new FileOutputStream(file, append);
            f.write(entry.getBytes());
            f.flush();
            f.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Abstract method in SensorEventListener.  It must be implemented, but is
     * unused in this app.
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }
}