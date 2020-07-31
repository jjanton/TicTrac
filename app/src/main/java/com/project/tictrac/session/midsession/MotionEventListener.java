package com.project.tictrac.session.midsession;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.project.tictrac.Utils;

/**
 * This class and its required methods, onSensorChanged, and onAccuracyChanged, were
 * referenced (in part) from Professional Android Sensor Programming, Milette & Stroud,
 * and the example code that comes with the book. Class: AccelerationEventListener
 */
public class MotionEventListener extends AppCompatActivity implements SensorEventListener {

    private Context context;
    private SessionViewModel mViewModel;
    private SensorManager sensorManager;

    private Handler mViewModelHandler;

    private static final double THRESHOLD_LOW = 2;
    private static final double THRESHOLD_MED = 3.2;
    private static final double THRESHOLD_HIGH = 4.5;
    private static double THRESHOLD;

    public MotionEventListener(Context context, SessionViewModel mViewModel, SensorManager sensorManager) {
        this.context = context;
        this.mViewModel = mViewModel;
        this.sensorManager = sensorManager;
        mViewModelHandler = new Handler();
        setTHRESHOLD(mViewModel.getMotionSensitivity());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (mViewModel.isMotionSensorEnabled()) {
            float[] xyzValues = event.values.clone();

            double sumOfSquares =
                    (xyzValues[0] * xyzValues[0])
                            + (xyzValues[1] * xyzValues[1])
                            + (xyzValues[2] * xyzValues[2]);

            double acceleration = Math.sqrt(sumOfSquares);

            if (acceleration > THRESHOLD) {
                if (mViewModel.isHapticFeedbackEnabled()) {
                    Utils.vibrate(500, context);
                }

                mViewModelHandler.post(() -> {
                    mViewModel.incrementMotionCounter();
                });

                resetSensor();
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No implementation
    }

    public void setTHRESHOLD(String sensitivity) {
        // Low sensitivity = High threshold, High sensitivity = Low threshold
        switch (sensitivity) {
            case "Low":
                THRESHOLD = THRESHOLD_HIGH;
                break;
            case "High":
                THRESHOLD = THRESHOLD_LOW;
                break;
            default:
                THRESHOLD = THRESHOLD_MED;
        }
    }

    public void resetSensor() {

        if (! mViewModel.isTimerPaused()) {
            sensorManager.unregisterListener(mViewModel.getMotionEventListener());
            mViewModel.setMotionEventListener(null);

            mViewModel.setMotionSensorEnabled(true);
            mViewModel.setMotionEventListener(new MotionEventListener(context, mViewModel, sensorManager));
            sensorManager.registerListener(mViewModel.getMotionEventListener(),
                    sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
                    SensorManager.SENSOR_DELAY_NORMAL);
        }


//       sensorManager.unregisterListener(this);
//       sensorManager.flush(this);
//
//        MotionEventListener newMotionEventListener = new MotionEventListener(context, mViewModel, sensorManager);
//
//        sensorManager.registerListener(newMotionEventListener,
//                sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
//                SensorManager.SENSOR_DELAY_NORMAL);

    }


}
