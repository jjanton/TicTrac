package com.project.tictrac.session.midsession;

import android.annotation.SuppressLint;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.SavedStateViewModelFactory;
import androidx.lifecycle.ViewModelProvider;

import com.project.tictrac.R;
import com.project.tictrac.session.presession.SessionDetails;

import java.io.File;
import java.util.concurrent.TimeUnit;

import static android.content.Context.SENSOR_SERVICE;

//TODO: We aren't stopping the motion/audio listeners. Even if you go back to another activity,
// they are still active. We need to stop the listeners when appropriate (eg. when a session
// timer ends, or when the user closes the app, or navigates away from the session fragment)

public class SessionFragment extends Fragment {
    final String LOG = "AudioRecorder";

    // Tic name, timer value, haptic feedback enabled, audio feedback enabled
    private SessionDetails sessionDetails;
    private int timerValue;
    private String ticName;
    private boolean hapticFeedback;
    private boolean audioFeedback;
    private String motionSensitivity;
    private String audioSensitivity;

    // Motion Sensor stuff
    private SensorManager sensorManager;
    private MotionEventListener motionEventListener;
    private boolean motionSensorActive;

    // Audio Recorder stuff
    private MediaRecorder mediaRecorder;
    private MaxAmplitudeRecorder amplitudeRecorder;
    private boolean mediaRecorderActive;

    // Timer stuff
    private CountDownTimer countDownTimer;

    // UI Stuff
    private SessionViewModel mViewModel;
    private TextView countdownTimerTextView;
    private TextView motionCounter;
    private TextView audioCounter;
    private ToggleButton hapticFeedbackToggleButton2;
    private ToggleButton audioFeedbackToggleButton2;
    private RadioGroup hapticRadioGroup;
    private RadioGroup audioRadioGroup;

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
        mViewModel = new ViewModelProvider(this, factory).get(SessionViewModel.class);

        // Get UI elements
        countdownTimerTextView = getView().findViewById(R.id.countdownTimerTextView);
        motionCounter = getView().findViewById(R.id.motionCounter);
        audioCounter = getView().findViewById(R.id.audioCounter);
        hapticFeedbackToggleButton2 = getView().findViewById(R.id.hapticFeedbackToggleButton2);
        audioFeedbackToggleButton2 = getView().findViewById(R.id.audioFeedbackToggleButton2);
        audioRadioGroup = getView().findViewById(R.id.audioRadioGroup);
        hapticRadioGroup = getView().findViewById(R.id.hapticRadioGroup);
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

        hapticFeedbackToggleButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hapticFeedbackToggleButton2.isChecked()) {
                    startReadingMotionData();
                } else {
                    stopReadingMotionData();
                }
            }
        });

        audioFeedbackToggleButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (audioFeedbackToggleButton2.isChecked()) {
                    startReadingAudioData();
                } else {
                    stopReadingAudioData();
                }
            }
        });
    }

    private void initializeUIState() {
        if (this.hapticFeedback) {
            this.hapticFeedbackToggleButton2.setChecked(true);
            startReadingMotionData();
        }

        if (this.audioFeedback) {
            this.audioFeedbackToggleButton2.setChecked(true);
            startReadingAudioData();
        }

        switch (motionSensitivity) {
            case "Low":
                hapticRadioGroup.check(R.id.lowHaptic);
                break;
            case "High":
                hapticRadioGroup.check(R.id.highHaptic);
                break;
            default:
                hapticRadioGroup.check(R.id.mediumHaptic);
        }

        switch (audioSensitivity) {
            case "Low":
                audioRadioGroup.check(R.id.lowAudio);
                break;
            case "High":
                audioRadioGroup.check(R.id.highAudio);
                break;
            default:
                audioRadioGroup.check(R.id.mediumAudio);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sensorManager = (SensorManager) getContext().getSystemService(SENSOR_SERVICE);

        // Setup user interface
        setupUI();

        // Get get the SessionDetail object that was set as this fragment's arguments
        Bundle bundle = getArguments();
        assert bundle != null;

        // Extract info from SessionDetail object
        if (bundle.containsKey("details")) {
            sessionDetails = (SessionDetails) bundle.getSerializable("details");
            this.timerValue = sessionDetails.getTimerValue();
            this.ticName = sessionDetails.getTicName();
            this.hapticFeedback = sessionDetails.getHapticFeedback();
            this.audioFeedback = sessionDetails.getAudioFeedback();
            this.motionSensitivity = sessionDetails.getMotionSensitivity();
            this.audioSensitivity = sessionDetails.getAudioSensitivity();
        }

        initializeUIState();
        createCountDownTimer();
    }

    /**
     * This method was referenced (in part) from Professional Android Sensor Programming, Milette & Stroud,
     * and the example code that comes with the book, method startReadingAccelerationData() from
     * class DetermineMovementActivity.java
     */
    private void startReadingMotionData() {

//        if (!motionSensorActive) {
        motionSensorActive = true;

        motionEventListener = new MotionEventListener(getContext(), mViewModel);

        RunnableThread runnableThread = new RunnableThread(motionEventListener, sensorManager);
        new Thread(runnableThread).start();
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

        mediaRecorderActive = true;

        String appStorageLocation =
                getContext().getExternalFilesDir("temp_audio").getAbsolutePath()
                        + File.separator + "audio.3gp";

        amplitudeRecorder = new MaxAmplitudeRecorder(10000, appStorageLocation, getContext(), mViewModel);

        RunnableThread runnableThread = new RunnableThread(amplitudeRecorder);
        new Thread(runnableThread).start();
    }

    private void stopReadingAudioData() {
        mediaRecorderActive = false;

        if (amplitudeRecorder != null) {
            amplitudeRecorder.stopRecording();
            amplitudeRecorder = null;
        }
    }


    // referenced from https://developer.android.com/reference/android/os/CountDownTimer
    private void createCountDownTimer() {
        countDownTimer = new CountDownTimer(timerValue * 60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                countdownTimerTextView.setText(String.format("%s:%s",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60,
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60
                ));
            }

            @Override
            public void onFinish() {
                //TODO: Probably launch an explicit intent for the post session
                Toast.makeText(getContext(), "TIMER IS DONE!",
                        Toast.LENGTH_SHORT).show();
            }
        }.start();
    }

}