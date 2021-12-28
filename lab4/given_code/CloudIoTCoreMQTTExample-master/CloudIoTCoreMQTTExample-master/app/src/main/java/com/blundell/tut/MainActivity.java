package com.blundell.tut;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;

import com.blundell.iotcore.IotCoreCommunicator;

import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity implements SensorEventListener {

    private IotCoreCommunicator communicator;
    private Handler handler;

    private long previousSamplingTimeAcc, previousSamplingTimeMag, previousSamplingTimeGyr;
    private int samplingPeriod = 10;      //default sampling period
    private float accX, accY, accZ, magX, magY, magZ, gyrX, gyrY, gyrZ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup the communication with your Google IoT Core details
        communicator = new IotCoreCommunicator.Builder()
                .withContext(this)
                .withCloudRegion("europe-west1") // ex: europe-west1  your region
                .withProjectId("triple-router-335423")   // ex: supercoolproject23236  your project id
                .withRegistryId("mattias_registry") // ex: my-devices       your-registry-id
                .withDeviceId("mattias_phone") // ex: my-test-raspberry-pi        //a-device-id
                .withPrivateKeyRawFileId(R.raw.rsa_private)
                .build();

        HandlerThread thread = new HandlerThread("MyBackgroundThread");
        thread.start();
        handler = new Handler(thread.getLooper());
        handler.post(connectOffTheMainThread); // Use whatever threading mechanism you want

        setupSamplingControls();
        setupSensorListeners();
    }

    private final Runnable connectOffTheMainThread = new Runnable() {
        @Override
        public void run() {
            communicator.connect();
            handler.post(sendMqttMessage);
        }
    };

    private final Runnable sendMqttMessage = new Runnable() {
//        private int i;

        /**
         * We post 100 messages as an example, 1 a second
         */
        @Override
        public void run() {
            String subtopic = "events";
            String message = "acc:\t" + accX + "\t" + accY + "\t" + accZ + "\n" +
                    "mag:\t" + magX + "\t" + magY + "\t" + magZ + "\n" +
                    "gyr:\t" + gyrX + "\t" + gyrY + "\t" + gyrZ;
            communicator.publishMessage(subtopic, message);

            handler.postDelayed(this, TimeUnit.SECONDS.toMillis(5));
        }
    };


    private void setupSamplingControls() {
        double samplingFrequency = 0.2;
        samplingPeriod = (int) (1000 / samplingFrequency);
        previousSamplingTimeAcc = System.currentTimeMillis();
        previousSamplingTimeMag = System.currentTimeMillis();
        previousSamplingTimeGyr = System.currentTimeMillis();
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

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if ((sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) &&
                ((System.currentTimeMillis() - previousSamplingTimeAcc) > samplingPeriod)) {
            accX = sensorEvent.values[0];
            accY = sensorEvent.values[1];
            accZ = sensorEvent.values[2];
            previousSamplingTimeAcc = System.currentTimeMillis();

            System.out.println("MyACC: " + previousSamplingTimeAcc + "\t" + accX + "\t" + accY + "\t" + accZ); // Logcat
        }

        if ((sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) &&
                ((System.currentTimeMillis() - previousSamplingTimeMag) > samplingPeriod)) {
            magX = sensorEvent.values[0];
            magY = sensorEvent.values[1];
            magZ = sensorEvent.values[2];
            previousSamplingTimeMag = System.currentTimeMillis();

            System.out.println("MyMAG: " + previousSamplingTimeMag + "\t" + magX + "\t" + magY + "\t" + magZ); // Logcat
        }

        if ((sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE) &&
                ((System.currentTimeMillis() - previousSamplingTimeGyr) > samplingPeriod)) {
            gyrX = sensorEvent.values[0];
            gyrY = sensorEvent.values[1];
            gyrZ = sensorEvent.values[2];
            previousSamplingTimeGyr = System.currentTimeMillis();

            System.out.println("MyGYR: " + previousSamplingTimeGyr + "\t" + gyrX + "\t" + gyrY + "\t" + gyrZ); // Logcat
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        ;
    }

    @Override
    protected void onDestroy() {
        communicator.disconnect();
        super.onDestroy();
    }
}
