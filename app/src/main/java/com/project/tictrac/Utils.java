package com.project.tictrac;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.RadioGroup;

import androidx.core.content.ContextCompat;

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

    public static void setRadioGroup(RadioGroup radioGroup, boolean enabled) {
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            radioGroup.getChildAt(i).setClickable(enabled);

            int visible = enabled ? View.VISIBLE : View.INVISIBLE;
            radioGroup.getChildAt(i).setVisibility(visible);
        }
    }

    public static boolean isAudioPermissionGranted(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED;
    }

    // Referenced from: https://stackoverflow.com/questions/5794506/android-clear-the-back-stack
    public static void returnToMainMenu(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}
