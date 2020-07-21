package com.project.tictrac.session.midsession;

import androidx.lifecycle.ViewModelProviders;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Telephony;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.project.tictrac.R;
import com.project.tictrac.Utils;
import com.project.tictrac.session.presession.SessionDetails;

import static android.content.Context.SENSOR_SERVICE;
import static android.content.Context.VIBRATOR_SERVICE;

public class SessionFragment extends Fragment {

    // Motion Sensor variables
    private SensorManager sensorManager;
    private MotionEventListener motionEventListener;
    private boolean motionSensorActive;


    private SessionViewModel mViewModel;
    private SessionDetails sessionDetails;
    private TextView testTextView;
    private Button vibratorButton;

    private Vibrator vibrator;

    public static SessionFragment newInstance() {
        return new SessionFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.session_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Register a sensor manager to set up motion sensor, set sensor active
        sensorManager = (SensorManager) getContext().getSystemService(SENSOR_SERVICE);
        motionSensorActive = true;

        // Get UI elements
        mViewModel = ViewModelProviders.of(this).get(SessionViewModel.class);
        vibratorButton = getView().findViewById(R.id.vibratorButton);

        // Get get the SessionDetail object that was set as this fragment's arguments
        // in the SessionActivity
        Bundle bundle = getArguments();
        assert bundle != null;
        sessionDetails = (SessionDetails) bundle.getSerializable("details");

        // Extract info from SessionDetail object
        if (bundle.containsKey("details")) {
            sessionDetails = (SessionDetails) bundle.getSerializable("details");
            System.out.println(sessionDetails.getTimerValue());
        }

        // Make phone vibrate for 1/2 second every time button clicked
        vibratorButton.setOnClickListener(v ->
                Utils.vibrate(1000, getContext())
        );

        startReadingMotionData();
    }



    /**
     * This method was referenced (in part) from Professional Android Sensor Programming, Milette & Stroud,
     * and the example code that comes with the book, method startReadingAccelerationData() from
     * class DetermineMovementActivity.java
     */
    private void startReadingMotionData() {
//        if (!motionSensorActive) {
            motionEventListener = new MotionEventListener(getContext());
            sensorManager.registerListener(motionEventListener,
                    sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
                    SensorManager.SENSOR_DELAY_NORMAL);
//        }

        motionSensorActive = true;
    }

    /**
     * This method was referenced (in part) from Professional Android Sensor Programming, Milette & Stroud,
     * and the example code that comes with the book, method stopReadingAccelerationData() from
     * class DetermineMovementActivity.java
     */
    private void stopReadingMotionData() {
//        if (motionSensorActive) {
            sensorManager.unregisterListener(motionEventListener);
            motionSensorActive = false;
//        }

    }

}