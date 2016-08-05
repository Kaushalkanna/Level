package com.kaushal.level;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.List;

/**
 * Created by xkxd061 on 8/5/16.
 */
public class LevelManager {

    private static float threshold = 0.2f;
    private static int interval = 1000;

    private static SensorManager sensorManager;
    // you could use an OrientationListener array instead
    // if you plans to use more than one listener
    private static LevelListener listener;

    private static Boolean supported;
    private static boolean running = false;

    public static boolean isListening() {
        return running;
    }

    public static void stopListening() {
        running = false;
        try {
            if (sensorManager != null && sensorEventListener != null) {
                sensorManager.unregisterListener(sensorEventListener);
            }
        } catch (Exception e) {
        }
    }

    public static boolean isSupported() {
        if (supported == null) {
            if (LevelActivity.getContext() != null) {
                sensorManager = (SensorManager) LevelActivity.getContext().
                        getSystemService(Context.SENSOR_SERVICE);
                List<Sensor> sensors = sensorManager.getSensorList(
                        Sensor.TYPE_ACCELEROMETER);
                supported = sensors.size() > 0;
            } else {
                supported = Boolean.FALSE;
            }
        }
        return supported;
    }

    public static void configure(int threshold, int interval) {
        LevelManager.threshold = threshold;
        LevelManager.interval = interval;
    }

    public static void startListening(
            LevelListener accelerometerListener) {
        sensorManager = (SensorManager) LevelActivity.getContext().
                getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensors = sensorManager.getSensorList(
                Sensor.TYPE_ACCELEROMETER);
        if (sensors.size() > 0) {
            Sensor sensor = sensors.get(0);
            running = sensorManager.registerListener(
                    sensorEventListener, sensor,
                    SensorManager.SENSOR_DELAY_GAME);
            listener = accelerometerListener;
        }
    }

    public static void startListening(
            LevelListener accelerometerListener,
            int threshold, int interval) {
        configure(threshold, interval);
        startListening(accelerometerListener);
    }

    private static SensorEventListener sensorEventListener =
            new SensorEventListener() {

                private long now = 0;
                private long timeDiff = 0;
                private long lastUpdate = 0;
                private long lastShake = 0;

                private float x = 0;
                private float y = 0;
                private float z = 0;
                private float lastX = 0;
                private float lastY = 0;
                private float lastZ = 0;
                private float force = 0;

                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                }

                public void onSensorChanged(SensorEvent event) {
                    now = event.timestamp;

                    x = event.values[0];
                    y = event.values[1];
                    z = event.values[2];

                    if (lastUpdate == 0) {
                        lastUpdate = now;
                        lastShake = now;
                        lastX = x;
                        lastY = y;
                        lastZ = z;
                    } else {
                        timeDiff = now - lastUpdate;
                        if (timeDiff > 0) {
                            force = Math.abs(x + y + z - lastX - lastY - lastZ)
                                    / timeDiff;
                            if (force > threshold) {
                                if (now - lastShake >= interval) {
                                    listener.onShake(force);
                                }
                                lastShake = now;
                            }
                            lastX = x;
                            lastY = y;
                            lastZ = z;
                            lastUpdate = now;
                        }
                    }
                    listener.onAccelerationChanged(x, y, z);
                }

            };

}