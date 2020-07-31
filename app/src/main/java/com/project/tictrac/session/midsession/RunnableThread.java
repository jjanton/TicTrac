package com.project.tictrac.session.midsession;

import android.hardware.Sensor;
import android.hardware.SensorManager;

/**
 * Referenced (in part) from Prof. Dan Feinberg, "Handlers and Runnable" video,
 * Network Example sample code, MAD module 7, and my own project
 */
public class RunnableThread implements Runnable {

    private MaxAmplitudeRecorder amplitudeRecorder;
    private MotionEventListener motionEventListener;
    private SensorManager sensorManager;

    public RunnableThread(MaxAmplitudeRecorder amplitudeRecorder) {
        this.amplitudeRecorder = amplitudeRecorder;
    }

//    public RunnableThread(MotionEventListener motionEventListener, SensorManager sensorManager) {
//        this.motionEventListener = motionEventListener;
//        this.sensorManager = sensorManager;
//    }

    @Override
    public void run() {
        if (amplitudeRecorder != null) {
            amplitudeRecorder.startRecording();
        }

//        if (motionEventListener != null) {
//            sensorManager.registerListener(motionEventListener,
//                    sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
//                    SensorManager.SENSOR_DELAY_NORMAL);
//        }
    }

}
