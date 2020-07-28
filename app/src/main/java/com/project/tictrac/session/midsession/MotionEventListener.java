package com.project.tictrac.session.midsession;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

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

    //TODO: Allow for low, normal, and high sensitivity (threshold = 3,2,1)
    private static final int THRESHOLD = 3;

    public MotionEventListener(Context context, SessionViewModel mViewModel) {
        this.context = context;
        this.mViewModel = mViewModel;
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


}
