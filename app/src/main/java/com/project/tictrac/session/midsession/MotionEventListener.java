package com.project.tictrac.session.midsession;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import androidx.appcompat.app.AppCompatActivity;

import com.project.tictrac.R;
import com.project.tictrac.Utils;

/**
 * This class and its required methods, onSensorChanged, and onAccuracyChanged, were
 * referenced (in part) from Professional Android Sensor Programming, Milette & Stroud,
 * and the example code that comes with the book. Class: AccelerationEventListener
 */
public class MotionEventListener extends AppCompatActivity implements SensorEventListener {

    private Context context;
    private SessionViewModel mViewModel;

    //TODO: Allow for low, normal, and high sensitivity (threshold = 3,2,1)
    private static final double THRESHOLD_LOW = 1.5;
    private static final double THRESHOLD_MED = 2.5;
    private static final double THRESHOLD_HIGH = 4;
    private static double THRESHOLD;

    public MotionEventListener(Context context, SessionViewModel mViewModel) {
        this.context = context;
        this.mViewModel = mViewModel;
        THRESHOLD = THRESHOLD_MED;
    }

    public MotionEventListener(Context context, SessionViewModel mViewModel, String sensitivity) {
        this.context = context;
        this.mViewModel = mViewModel;
        setTHRESHOLD(sensitivity);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] xyzValues = event.values.clone();

        double sumOfSquares =
                (xyzValues[0] * xyzValues[0])
                        + (xyzValues[1] * xyzValues[1])
                        + (xyzValues[2] * xyzValues[2]);

        double acceleration = Math.sqrt(sumOfSquares);

        if (acceleration > THRESHOLD) {
            Utils.vibrate(500, context);
            mViewModel.incrementMotionCounter();
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

}
