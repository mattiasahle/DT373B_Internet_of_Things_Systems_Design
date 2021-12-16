package com.example.sensordemodec20;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.hardware.SensorManager;
import android.hardware.Sensor;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    public TextView myTxt1;
    public Button myBtn1;
    private int globalVar = 0;
    private SensorManager sensorManager;
    private Sensor accelero;
    private long previousSamplingTime;
    private int samplingPeriod = 10;      //default samling period
    private final double samplingFrequency = 0.2;    //sampling frequency 0.2Hz

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupControls();
    }

    private void setupControls() {
        samplingPeriod = (int) (1000 / samplingFrequency);
        previousSamplingTime = System.currentTimeMillis();
        myTxt1 = (TextView) findViewById(R.id.mytxt);
        myBtn1 = (Button) findViewById(R.id.mybtn);
    }

    public void buttonClicked(View view) {
        switch (view.getId()) {
            case R.id.mybtn:
                if (globalVar == 0) myTxt1.setText("Accelerometer");
                if (globalVar == 1) myTxt1.setText("Magnetometer");
                if (globalVar == 2) myTxt1.setText("Gyrometer");
                globalVar++;
                if (globalVar > 2) globalVar = 0;
        }
    }

    //@Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if ((sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) && ((System.currentTimeMillis() - previousSamplingTime) > samplingPeriod)) {
            float xValue = sensorEvent.values[0];
            float yValue = sensorEvent.values[1];
            float zValue = sensorEvent.values[2];
            previousSamplingTime = System.currentTimeMillis();      //Finlly, save most recent sampling instant.

            myTxt1.setText("Accel x = " + xValue);

            System.out.println("Accelerometer change" + " X,Y,Z: " + xValue + "," + yValue + "," + zValue); //for debugging purposes

        }
//        System.out.println("Time: previous = " + previousSamplingTime); //for debugging purposes

/*
     repeat for gyro
           //else if ((sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE) && ((System.currentTimeMillis() - previousSamplingTime) > samplingPeriod)) {
           //...
    repeat for magnetometer
        //else if ((sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) && ((System.currentTimeMillis() - previousSamplingTime) > samplingPeriod)) {
        //...

*/
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        ;
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        //    sensorManager.registerListener(this, accelero, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), sensorManager.SENSOR_DELAY_GAME);
        //      sensorManager.registerListener((SensorEventListener) this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), sensorManager.SENSOR_DELAY_GAME);
    }
}