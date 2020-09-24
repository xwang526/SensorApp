package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements SensorEventListener {

    // initialize instances
    private SensorManager mSensorManager;

    private Sensor mSensorLinearAccelerometer;
    private Sensor mSensorGyroscope;
    private Sensor mSensorMagnetometer;
    private Sensor mSensorRotationVector;
    private Sensor mSensorGravity;

    private float[] mLinearAccelerometerData = new float[3];
    private float[] mGravityData = new float[3];
    private float[] mGyroscopeData = new float[3];
    private float[] mMagnetometerData = new float[3];

    private float[] rotationMatrix = new float[9];
    private float[] rotationAngles = new float[3];
    private float[] velocity = new float[3];
    private float[] displacement = new float[3];

    File dir;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSION_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorLinearAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mSensorMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mSensorGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mSensorRotationVector = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mSensorGravity = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        clearInfoForFile("globalAcce.csv");
        clearInfoForFile("gyro.csv");
        clearInfoForFile("rotationAngles.csv");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mSensorLinearAccelerometer != null) {
            mSensorManager.registerListener(this, mSensorLinearAccelerometer,
                    2500);
        }
        if (mSensorMagnetometer != null) {
            mSensorManager.registerListener(this, mSensorMagnetometer,
                    2500);
        }
        if (mSensorGyroscope != null) {
            mSensorManager.registerListener(this, mSensorGyroscope,
                    2500);
        }
        if (mSensorRotationVector != null) {
            mSensorManager.registerListener(this, mSensorRotationVector,
                    2500);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        int sensorType = sensorEvent.sensor.getType();

        switch (sensorType) {
            case Sensor.TYPE_LINEAR_ACCELERATION:
                float[] A = sensorEvent.values.clone();
                mLinearAccelerometerData[0] = rotationMatrix[0]*A[0] + rotationMatrix[1]*A[1] + rotationMatrix[2]*A[2];
                mLinearAccelerometerData[1] = rotationMatrix[3]*A[0] + rotationMatrix[4]*A[1] + rotationMatrix[5]*A[2];
                mLinearAccelerometerData[2] = rotationMatrix[6]*A[0] + rotationMatrix[7]*A[1] + rotationMatrix[8]*A[2];
                filterAcceleration();
                ToFile(mLinearAccelerometerData, "globalAcce.csv");
                java.text.DecimalFormat df = new java.text.DecimalFormat("#0.000");
                Log.i("Sensor","x=" +  df.format(mLinearAccelerometerData[0]) +
                        " y=" + df.format(mLinearAccelerometerData[1])  + " z=" +df.format(mLinearAccelerometerData[2]));
                break;
            case Sensor.TYPE_ROTATION_VECTOR:
                SensorManager.getRotationMatrixFromVector(rotationMatrix, sensorEvent.values);
                SensorManager.getOrientation(rotationMatrix,rotationAngles);
                ToFile(rotationAngles, "rotationAngles.csv");
//                Log.i("Sensor","yaw= "+Math.toDegrees(rotationAngles[0])+" pitch= "+Math.toDegrees(rotationAngles[1])+
//                         " roll= "+Math.toDegrees(rotationAngles[2]));
                break;
            case Sensor.TYPE_GRAVITY:
                mGravityData = sensorEvent.values.clone();
                break;
            case Sensor.TYPE_GYROSCOPE:
                mGyroscopeData = sensorEvent.values.clone();
                ToFile(mGyroscopeData,"gyro.csv");
                break;
            default:
                // leave it~
        }
    }

    public void ToFile(float[] data, String name) {
        try {
            dir = new File(getExternalFilesDir(null),"");
            String entry = String.format(Locale.CHINA,"sensor, %.4f,%.4f,%.4f\n",
                    data[0],data[1],data[2]);
            File outfile = new File(dir, name);
            FileOutputStream f = new FileOutputStream(outfile, true);
            f.write(entry.getBytes());
            f.flush();
            f.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void clearInfoForFile(String fileName) {
        dir = new File(getExternalFilesDir(null),"");
        File file =new File(dir,fileName);
        try {
            if(!file.exists()) {
                file.createNewFile();
            }
            FileWriter f =new FileWriter(file);
            f.write("");
            f.flush();
            f.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void filterAcceleration() {
        float mag = (float) Math.sqrt(mLinearAccelerometerData[0]*mLinearAccelerometerData[0] +
                mLinearAccelerometerData[1]*mLinearAccelerometerData[1] +
                mLinearAccelerometerData[2]*mLinearAccelerometerData[2]);
        float[] zeros = new float[3];
        mLinearAccelerometerData = (mag < 0.08)? zeros :mLinearAccelerometerData;
    }

    /**
     * Abstract method in SensorEventListener.  It must be implemented, but is
     * unused in this app.
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        Log.i("Sensor", "accuracy="+i);
    }
}