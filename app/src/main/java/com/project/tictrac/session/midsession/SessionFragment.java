package com.project.tictrac.session.midsession;

import androidx.lifecycle.Observer;
import androidx.lifecycle.SavedStateViewModelFactory;
import androidx.lifecycle.ViewModelProviders;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Telephony;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.project.tictrac.R;
import com.project.tictrac.Utils;
import com.project.tictrac.session.presession.SessionDetails;

import java.io.File;
import java.io.IOException;

import static android.content.Context.SENSOR_SERVICE;
import static android.content.Context.VIBRATOR_SERVICE;

public class SessionFragment extends Fragment {
    final String LOG = "AudioRecorder";

    // Motion Sensor stuff
    private SensorManager sensorManager;
    private MotionEventListener motionEventListener;
    private boolean motionSensorActive;

    // Audio Recorder stuff
    private MediaRecorder mediaRecorder;
    private MaxAmplitudeRecorder amplitudeRecorder;

    private SessionViewModel mViewModel;
    private SessionDetails sessionDetails;
    private TextView testTextView;
    private TextView motionCounter;
    private TextView audioCounter;
    private Button vibratorButton;

    private Vibrator vibrator;

    //TODO: We aren't stopping the motion/audio listeners. Even if you go back to another activity,
    // they are still active. We need to stop the listeners when appropriate (eg. when a session
    // timer ends, or when the user closes the app, or navigates away from the session fragment)

    public static SessionFragment newInstance() {
        return new SessionFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.session_fragment, container, false);
    }

    private void setupUI() {
        // View model setup
        SavedStateViewModelFactory factory = new SavedStateViewModelFactory(
                getActivity().getApplication(), this);
        mViewModel = ViewModelProviders.of(this, factory).get(SessionViewModel.class);

        // Get UI elements
        vibratorButton = getView().findViewById(R.id.vibratorButton);
        motionCounter = getView().findViewById(R.id.motionCounter);
        audioCounter = getView().findViewById(R.id.audioCounter);
        motionCounter.setText("Motion Counter: 0");
        audioCounter.setText("Audio Counter: 0");

        // Observer for motionCounter
        final Observer<Integer> motionCounterObserver = new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                motionCounter.setText("Motion Counter: " + String.valueOf(integer));
            }
        };

        // Observer for audioCounter
        final Observer<Integer> audioCounterObserver = new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                audioCounter.setText("Audio Counter: " + String.valueOf(integer));
            }
        };

        // Tell observers to observe the data in mViewModel
        mViewModel.getMotionCounter().observe(getViewLifecycleOwner(), motionCounterObserver);
        mViewModel.getAudioCounter().observe(getViewLifecycleOwner(), audioCounterObserver);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Setup user interface
        setupUI();

        // Register a sensor manager to set up motion sensor, set sensor active
        sensorManager = (SensorManager) getContext().getSystemService(SENSOR_SERVICE);
        motionSensorActive = true;

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

        // Start the motion and audio detection
        startReadingMotionData();
        startReadingAudioData();
    }

    /**
     * This method was referenced (in part) from Professional Android Sensor Programming, Milette & Stroud,
     * and the example code that comes with the book, method startReadingAccelerationData() from
     * class DetermineMovementActivity.java
     */
    private void startReadingMotionData() {
//        if (!motionSensorActive) {
        motionEventListener = new MotionEventListener(getContext(), mViewModel);
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

    /**
     * This method was referenced (in part) from Google documentation on MediaRecorder,
     * https://developer.android.com/guide/topics/media/mediarecorder, and
     * from Professional Android Sensor Programming, Milette & Stroud,
     */
    private void startReadingAudioData() {
        // This storage needs to exist even though we are not saving the audio files
        String appStorageLocation =
                getContext().getExternalFilesDir("temp_audio").getAbsolutePath()
                        + File.separator + "audio.3gp";

        amplitudeRecorder = new MaxAmplitudeRecorder(10000, appStorageLocation, getContext(), mViewModel);

        RunnableThread runnableThread = new RunnableThread(amplitudeRecorder);
        new Thread(runnableThread).start();
    }

    private void stopReadingAudioData() {
        if (amplitudeRecorder != null) {
            amplitudeRecorder.stopRecording();
        }
    }


}