package com.example.sensordemodec20;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.hardware.SensorManager;
import android.hardware.Sensor;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private long previousSamplingTimeAcc, previousSamplingTimeMag, previousSamplingTimeGyr;
    private int samplingPeriod = 10;      //default samling period

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupSamplingControls();
        setupSensorListeners();
    }

    private void setupSensorListeners() {
        SensorManager sm = (SensorManager) this.getSystemService(SENSOR_SERVICE);

        Sensor accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor magnetometer = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        Sensor gyroscope = sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        sm.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sm.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
        sm.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void setupSamplingControls() {
        double samplingFrequency = 0.2;
        samplingPeriod = (int) (1000 / samplingFrequency);
        previousSamplingTimeAcc = System.currentTimeMillis();
        previousSamplingTimeMag = System.currentTimeMillis();
        previousSamplingTimeGyr = System.currentTimeMillis();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if ((sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) &&
                ((System.currentTimeMillis() - previousSamplingTimeAcc) > samplingPeriod)) {
            float accX = sensorEvent.values[0];
            float accY = sensorEvent.values[1];
            float accZ = sensorEvent.values[2];
            previousSamplingTimeAcc = System.currentTimeMillis();

            System.out.println("ACC: " + previousSamplingTimeAcc + "\t" + accX + "\t" + accY + "\t" + accZ); // Logcat
        }

        if ((sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) &&
                ((System.currentTimeMillis() - previousSamplingTimeMag) > samplingPeriod)) {
            float magX = sensorEvent.values[0];
            float magY = sensorEvent.values[1];
            float magZ = sensorEvent.values[2];
            previousSamplingTimeMag = System.currentTimeMillis();

            System.out.println("MAG: " + previousSamplingTimeMag + "\t" + magX + "\t" + magY + "\t" + magZ); // Logcat
        }

        if ((sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE) &&
                ((System.currentTimeMillis() - previousSamplingTimeGyr) > samplingPeriod)) {
            float gyrX = sensorEvent.values[0];
            float gyrY = sensorEvent.values[1];
            float gyrZ = sensorEvent.values[2];
            previousSamplingTimeGyr = System.currentTimeMillis();

            System.out.println("GYR: " + previousSamplingTimeGyr + "\t" + gyrX + "\t" + gyrY + "\t" + gyrZ); // Logcat
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        ;
    }
}