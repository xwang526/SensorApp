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
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
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

    private float[] rotationMatrix = new float[16];
    private float[] invRotationMatrix = new float[16];
    private float[] velocity = new float[3];
    private float[] displacement = new float[3];

    private final float alpha = 0.8f;

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
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mSensorLinearAccelerometer != null) {
            mSensorManager.registerListener(this, mSensorLinearAccelerometer,
                    10000);
        }
        if (mSensorMagnetometer != null) {
            mSensorManager.registerListener(this, mSensorMagnetometer,
                    10000);
        }
        if (mSensorGyroscope != null) {
            mSensorManager.registerListener(this, mSensorGyroscope,
                    10000);
        }
        if (mSensorRotationVector != null) {
            mSensorManager.registerListener(this, mSensorRotationVector,
                    10000);
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
                mLinearAccelerometerData[0] = invRotationMatrix[0]*A[0] + invRotationMatrix[1]*A[1] + invRotationMatrix[2]*A[2];
                mLinearAccelerometerData[1] = invRotationMatrix[4]*A[0] + invRotationMatrix[5]*A[1] + invRotationMatrix[6]*A[2];
                mLinearAccelerometerData[2] = invRotationMatrix[8]*A[0] + invRotationMatrix[9]*A[1] + invRotationMatrix[10]*A[2];
//                filterAcceleration();
                ToFile(mLinearAccelerometerData, "globalAcce.csv");
                java.text.DecimalFormat df = new java.text.DecimalFormat("#0.000");
                Log.i("Sensor","x=" +  df.format(mLinearAccelerometerData[0]) +
                        " y=" + df.format(mLinearAccelerometerData[1])  + " z=" +df.format(mLinearAccelerometerData[2]));
                break;
            case Sensor.TYPE_ROTATION_VECTOR:
                SensorManager.getRotationMatrixFromVector(rotationMatrix, sensorEvent.values);
                android.opengl.Matrix.invertM(invRotationMatrix,0,rotationMatrix,0);
//                Log.i("Sensor","r[8]"+rotationMatrix[8]+"i[2]"+invRotation[2]);
                break;
            case Sensor.TYPE_GRAVITY:
                mGravityData = sensorEvent.values.clone();
                break;
            case Sensor.TYPE_GYROSCOPE:
                mGyroscopeData = sensorEvent.values.clone();
                ToFile(mGyroscopeData, "Gyro.csv");
                break;
            default:
                return;
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

    public void filterAcceleration() {
        mLinearAccelerometerData[0] = ((mLinearAccelerometerData[0] < 0.02)&&(mLinearAccelerometerData[0] > -0.02))? 0:mLinearAccelerometerData[0];
        mLinearAccelerometerData[1] = ((mLinearAccelerometerData[1] < 0.02)&&(mLinearAccelerometerData[1] > -0.02))? 0:mLinearAccelerometerData[1];
        mLinearAccelerometerData[2] = ((mLinearAccelerometerData[2] < 0.02)&&(mLinearAccelerometerData[2] > -0.02))? 0:mLinearAccelerometerData[2];
    }

    public void getVelocity() {
        mLinearAccelerometerData[0] = ((mLinearAccelerometerData[0] < 0.02)&&(mLinearAccelerometerData[0] > -0.02))? 0:mLinearAccelerometerData[0];
        mLinearAccelerometerData[1] = ((mLinearAccelerometerData[1] < 0.02)&&(mLinearAccelerometerData[1] > -0.02))? 0:mLinearAccelerometerData[1];
        mLinearAccelerometerData[2] = ((mLinearAccelerometerData[2] < 0.02)&&(mLinearAccelerometerData[2] > -0.02))? 0:mLinearAccelerometerData[2];
        velocity[0] = (float) (velocity[0]+mLinearAccelerometerData[0]*0.02);
        velocity[1] = (float) (velocity[1]+mLinearAccelerometerData[1]*0.02);
        velocity[2] = (float) (velocity[2]+mLinearAccelerometerData[2]*0.02);
    }
    public void getDisplacement() {
        velocity[0] = ((mLinearAccelerometerData[0] < 0.02)&&(mLinearAccelerometerData[0] > -0.02))? 0:velocity[0];
        velocity[1] = ((mLinearAccelerometerData[1] < 0.02)&&(mLinearAccelerometerData[1] > -0.02))? 0:velocity[1];
        velocity[2] = ((mLinearAccelerometerData[2] < 0.02)&&(mLinearAccelerometerData[2] > -0.02))? 0:velocity[2];
        displacement[0] = (float) (displacement[0] + velocity[0]*0.02);
        displacement[1] = (float) (displacement[1] + velocity[1]*0.02);
        displacement[2] = (float) (displacement[2] + velocity[2]*0.02);
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