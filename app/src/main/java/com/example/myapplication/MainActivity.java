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

    // Rotation vector
    private Sensor mSensorRotationX;
    private Sensor mSensorRotationY;
    private Sensor mSensorRotationZ;


    // TextViews to display current sensor values.

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

    // device coordinate
    private final float[] linearAccelerometerArray = new float[3];
    private final float[] gravityArray = new float[3];
    private final float[] gyroscopeArray = new float[3];
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
        dir = new File(getExternalFilesDir(null),"");

        String entry = "Sensor, X, Y, Z\n";
        File gravfile = new File(dir, "Gravity.csv");
        File gyrofile = new File(dir, "Gyroscope.csv");
        File rotafile = new File(dir, "Orientation.csv");
        File magfile = new File(dir, "Magnetic.csv");
        File liaccefile = new File(dir, "LinearAccelerometer.csv");

//         write head entry to file
        ToFile(gravfile, false, entry);
        ToFile(gyrofile, false, entry);
        ToFile(rotafile, false, entry);
        ToFile(magfile, false, entry);
        ToFile(liaccefile, false, entry);

        // initialize all view variable
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

        mSensorRotationX = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mSensorRotationY = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mSensorRotationZ = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        // error message displayed
        String sensor_error = getResources().getString(R.string.error_no_sensor);

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

        if (mSensorRotationX != null) {
            mSensorManager.registerListener(this, mSensorRotationX,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (mSensorRotationY != null) {
            mSensorManager.registerListener(this, mSensorRotationY,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (mSensorRotationZ != null) {
            mSensorManager.registerListener(this, mSensorRotationZ,
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
        File outfile = new File(dir, "output.csv");

        switch (sensorType) {
            case Sensor.TYPE_ROTATION_VECTOR:
                SensorManager.getRotationMatrix(rotationMatrix, null, gravityArray, magnetometerArray);
                SensorManager.getOrientation(rotationMatrix, orientationAngles);
                mTextSensorAzimuth.setText(getResources().getString(R.string.azimuth, orientationAngles[0]));
                mTextSensorPitch.setText(getResources().getString(R.string.pitch, orientationAngles[1]));
                mTextSensorRoll.setText(getResources().getString(R.string.roll, orientationAngles[2]));
                outfile = new File(dir, "orientation.csv");
                entry = String.format(Locale.CHINA, "orientation angles, %f, %f, %f\n", orientationAngles[0], orientationAngles[1], orientationAngles[2]);
                break;
            case Sensor.TYPE_GRAVITY:
                mTextSensorGravityX.setText(getResources().getString(R.string.gravity_x, currentValue1));
                mTextSensorGravityY.setText(getResources().getString(R.string.gravity_y, currentValue2));
                mTextSensorGravityZ.setText(getResources().getString(R.string.gravity_z, currentValue3));
                System.arraycopy(sensorEvent.values, 0, gravityArray, 0, gravityArray.length);
                outfile = new File(dir, "Gravity.csv");
                entry = String.format(Locale.CHINA,"gravity, %f, %f, %f\n", currentValue1, currentValue2, currentValue3);
                break;
            case Sensor.TYPE_LINEAR_ACCELERATION:
                mTextSensorLinearAccelerometerX.setText(getResources().getString(R.string.linear_accelerometer_x, currentValue1));
                mTextSensorLinearAccelerometerY.setText(getResources().getString(R.string.linear_accelerometer_y, currentValue2));
                mTextSensorLinearAccelerometerZ.setText(getResources().getString(R.string.linear_accelerometer_z, currentValue3));
                outfile = new File(dir, "LinearAccelerometer.csv");
                System.arraycopy(sensorEvent.values, 0, linearAccelerometerArray, 0, linearAccelerometerArray.length);
                entry = String.format(Locale.CHINA,"linear accelerometer, %f, %f, %f\n", currentValue1, currentValue2, currentValue3);
                break;
            case Sensor.TYPE_GYROSCOPE:
                mTextSensorGyroscopeX.setText(getResources().getString(R.string.gyroscope_x, currentValue1));
                mTextSensorGyroscopeY.setText(getResources().getString(R.string.gyroscope_y, currentValue2));
                mTextSensorGyroscopeZ.setText(getResources().getString(R.string.gyroscope_z, currentValue3));
                System.arraycopy(sensorEvent.values, 0, gyroscopeArray, 0, gyroscopeArray.length);
                outfile = new File(dir, "Gyroscope.csv");
                entry = String.format(Locale.CHINA,"gyroscope, %f, %f, %f\n", currentValue1, currentValue2, currentValue3);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                mTextSensorMagneticX.setText(getResources().getString(R.string.magnetic_x, currentValue1));
                mTextSensorMagneticY.setText(getResources().getString(R.string.magnetic_y, currentValue2));
                mTextSensorMagneticZ.setText(getResources().getString(R.string.magnetic_z, currentValue3));
                outfile = new File(dir, "Magnetic.csv");
                System.arraycopy(sensorEvent.values, 0, magnetometerArray, 0, magnetometerArray.length);
                entry = String.format(Locale.CHINA, "magnetic, %f, %f, %f\n", currentValue1, currentValue2, currentValue3);
                break;
            default:
                // leave it
        }

//        // transfer to world coordinate
//        linearAccelerometerGlobalArray = transferDeviceToGlobal(linearAccelerometerArray, rotationMatrix);
//        gravityGlobalArray = transferDeviceToGlobal(gravityArray, rotationMatrix);
//        gyroscopeGlobalArray = transferDeviceToGlobal(gyroscopeGlobalArray, rotationMatrix);
//        magnetometerGlobalArray = transferDeviceToGlobal(magnetometerGlobalArray, rotationMatrix);

        ToFile(outfile, true, entry);
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

    public float[] transferDeviceToGlobal (float[] deviceArray, float[] rotationMatrix) {
        float [] global = new float[3];
        global[0] = rotationMatrix[0]*deviceArray[0]+rotationMatrix[1]*deviceArray[1]+rotationMatrix[2]*deviceArray[2];
        global[1] = rotationMatrix[3]*deviceArray[0]+rotationMatrix[4]*deviceArray[1]+rotationMatrix[5]*deviceArray[2];
        global[2] = rotationMatrix[6]*deviceArray[0]+rotationMatrix[7]*deviceArray[1]+rotationMatrix[8]*deviceArray[2];
        return global;
    }

    /**
     * Abstract method in SensorEventListener.  It must be implemented, but is
     * unused in this app.
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }
}