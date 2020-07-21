package com.project.tictrac.session.midsession;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.appcompat.app.AppCompatActivity;

import com.project.tictrac.Utils;

import static android.content.Context.VIBRATOR_SERVICE;
import static androidx.core.content.ContextCompat.createDeviceProtectedStorageContext;
import static androidx.core.content.ContextCompat.getSystemService;

/**
 * This class and its required methods, onSensorChanged, and onAccuracyChanged, were
 * referenced (in part) from Professional Android Sensor Programming, Milette & Stroud,
 * and the example code that comes with the book. Class: AccelerationEventListener
 */
public class MotionEventListener extends AppCompatActivity implements SensorEventListener {

    private Context context;

    //TODO: Allow for low, normal, and high sensitivity (threshold = 3,2,1)
    private static final int THRESHOLD = 2;

    public MotionEventListener(Context context) {
        this.context = context;
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
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No implementation
    }


}
