package com.project.tictrac;

import android.content.Context;
import android.os.VibrationEffect;
import android.os.Vibrator;

import static android.content.Context.VIBRATOR_SERVICE;

public class Utils {

    /**
     * Make the phone vibrate for a given duration
     *
     * @param duration length of time to make phone vibrate, in ms (1000ms = 1s)
     */
    public static void vibrate(int duration, Context context) {
        ((Vibrator) context.getSystemService(VIBRATOR_SERVICE)).vibrate(
                VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE));
    }

}
