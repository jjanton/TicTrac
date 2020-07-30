package com.project.tictrac.session.midsession;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.SensorManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.SavedStateViewModelFactory;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;
import com.project.tictrac.R;
import com.project.tictrac.Utils;
import com.project.tictrac.session.presession.SessionDetails;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static android.content.Context.SENSOR_SERVICE;

public class SessionFragment extends Fragment {
    final String LOG = "AudioRecorder";

    private SessionDetails sessionDetails;

    private SensorManager sensorManager;
    private MotionEventListener motionEventListener;

    private MediaRecorder mediaRecorder;
    private MaxAmplitudeRecorder amplitudeRecorder;

    private CountDownTimer countDownTimer;

    private SessionViewModel mViewModel;
    private TextView countdownTimerTextView;
    private TextView motionCounter;
    private TextView audioCounter;
    private ToggleButton motionSensorToggleButton2;
    private ToggleButton audioSensorToggleButton2;
    private RadioGroup motionSensorRadioGroup;
    private RadioGroup audioSensorRadioGroup;
    private ToggleButton hapticFeedbackToggleButton2;
    private ToggleButton audioFeedbackToggleButton2;

    private boolean motionSensorPreviouslyEnabled = false;
    private boolean audioSensorPreviouslyEnabled = false;


    public static SessionFragment newInstance() {
        return new SessionFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.session_fragment, container, false);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mViewModel.isMotionSensorEnabled()) {
            mViewModel.setMotionSensorEnabled(false);
            motionSensorPreviouslyEnabled = true;
            stopReadingMotionData();
        }

        if (mViewModel.isAudioSensorEnabled()) {
            mViewModel.setAudioSensorEnabled(false);
            audioSensorPreviouslyEnabled = true;
            stopReadingAudioData();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (motionSensorPreviouslyEnabled) {
            mViewModel.setMotionSensorEnabled(true);
            motionSensorPreviouslyEnabled = false;
            startReadingMotionData();
        }

        if (audioSensorPreviouslyEnabled) {
            mViewModel.setAudioSensorEnabled(true);
            audioSensorPreviouslyEnabled = false;
            startReadingAudioData();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupUI();
        setButtonListeners();
        initializeUIState();
        createCountDownTimer();
    }

    private void setupUI() {
        // View model setup
        SavedStateViewModelFactory factory = new SavedStateViewModelFactory(
                getActivity().getApplication(), this);
        mViewModel = new ViewModelProvider(this, factory).get(SessionViewModel.class);

        sensorManager = (SensorManager) getContext().getSystemService(SENSOR_SERVICE);

        // Get UI elements
        countdownTimerTextView = getView().findViewById(R.id.countdownTimerTextView);
        motionCounter = getView().findViewById(R.id.motionCounter);
        audioCounter = getView().findViewById(R.id.audioCounter);
        motionSensorToggleButton2 = getView().findViewById(R.id.motionSensorToggleButton2);
        audioSensorToggleButton2 = getView().findViewById(R.id.audioSensorToggleButton2);
        motionSensorRadioGroup = getView().findViewById(R.id.motionSensorRadioGroup);
        audioSensorRadioGroup = getView().findViewById(R.id.audioSensorRadioGroup);
        hapticFeedbackToggleButton2 = getView().findViewById(R.id.hapticFeedbackToggleButton2);
        audioFeedbackToggleButton2 = getView().findViewById(R.id.audioFeedbackToggleButton2);
        motionCounter.setText("Motion Counter: 0");
        audioCounter.setText("Audio Counter: 0");

        // Get get the SessionDetail object that was set as this fragment's arguments
        Bundle bundle = getArguments();
        assert bundle != null;

        // Extract info from SessionDetail object
        if (bundle.containsKey("details")) {
            sessionDetails = (SessionDetails) bundle.getSerializable("details");

            mViewModel.setTimerValue(sessionDetails.getTimerValue());
            mViewModel.setTicName(sessionDetails.getTicName());

            mViewModel.setMotionSensorEnabled(sessionDetails.getMotionSensor());
            mViewModel.setAudioSensorEnabled(sessionDetails.getAudioSensor());

            mViewModel.setHapticFeedbackEnabled(sessionDetails.getHapticFeedback());
            mViewModel.setAudioFeedbackEnabled(sessionDetails.getAudioFeedback());

            mViewModel.setMotionSensitivity(sessionDetails.getMotionSensitivity());
            mViewModel.setAudioSensitivity(sessionDetails.getAudioSensitivity());
        }

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

        // referenced (in part) from https://stackoverflow.com/questions/56319759/how-to-show-warning-message-when-back-button-is-pressed-in-fragments
        OnBackPressedCallback backPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                displayNewLinkPopup(getContext());
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), backPressedCallback);

    }

    private void setButtonListeners() {
        // Listener for motionSensor toggle
        motionSensorToggleButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (motionSensorToggleButton2.isChecked()) {
                    startReadingMotionData();
                    Utils.setRadioGroup(motionSensorRadioGroup, true);
                } else {
                    stopReadingMotionData();
                    Utils.setRadioGroup(motionSensorRadioGroup, false);
                }
            }
        });

        // Listener for audioSensor toggle
        audioSensorToggleButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (audioSensorToggleButton2.isChecked()) {
                    startReadingAudioData();
                    Utils.setRadioGroup(audioSensorRadioGroup, true);
                } else {
                    stopReadingAudioData();
                    Utils.setRadioGroup(audioSensorRadioGroup, false);
                }
            }
        });

        // Listener for motionSensor radioGroup
        motionSensorRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton motionSensorRadioButton = getView().findViewById(checkedId);

                mViewModel.setMotionSensitivity(motionSensorRadioButton.getText().toString());
                setMotionSensorSensitivity(mViewModel.getMotionSensitivity());
            }
        });

        // Listener for audioSensor radioGroup
        audioSensorRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton audioSensorRadioButton = getView().findViewById(checkedId);

                mViewModel.setAudioSensitivity(audioSensorRadioButton.getText().toString());
                setAudioSensorSensitivity(mViewModel.getAudioSensitivity());
            }
        });

        hapticFeedbackToggleButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.setHapticFeedbackEnabled(hapticFeedbackToggleButton2.isChecked());
            }
        });

        audioFeedbackToggleButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.setAudioFeedbackEnabled(audioFeedbackToggleButton2.isChecked());
            }
        });
    }

    private void initializeUIState() {
        if (mViewModel.isMotionSensorEnabled()) {
            this.motionSensorToggleButton2.setChecked(true);
            Utils.setRadioGroup(motionSensorRadioGroup, true);
            startReadingMotionData();
        } else {
            Utils.setRadioGroup(motionSensorRadioGroup, false);
        }

        if (mViewModel.isAudioSensorEnabled()) {
            this.audioSensorToggleButton2.setChecked(true);
            Utils.setRadioGroup(audioSensorRadioGroup, true);
            startReadingAudioData();
        } else {
            Utils.setRadioGroup(audioSensorRadioGroup, false);
        }

        switch (mViewModel.getMotionSensitivity()) {
            case "Low":
                motionSensorRadioGroup.check(R.id.lowHaptic);
                break;
            case "High":
                motionSensorRadioGroup.check(R.id.highHaptic);
                break;
            default:
                motionSensorRadioGroup.check(R.id.mediumHaptic);
        }
        switch (mViewModel.getAudioSensitivity()) {
            case "Low":
                audioSensorRadioGroup.check(R.id.lowAudio);
                break;
            case "High":
                audioSensorRadioGroup.check(R.id.highAudio);
                break;
            default:
                audioSensorRadioGroup.check(R.id.mediumAudio);
        }

        if (mViewModel.isHapticFeedbackEnabled()) {
            this.hapticFeedbackToggleButton2.setChecked(true);
        }
        if (mViewModel.isAudioFeedbackEnabled()) {
            this.audioFeedbackToggleButton2.setChecked(true);
        }
    }

    /**
     * This method was referenced (in part) from Professional Android Sensor Programming, Milette & Stroud,
     * and the example code that comes with the book, method startReadingAccelerationData() from
     * class DetermineMovementActivity.java
     */
    private void startReadingMotionData() {
        mViewModel.setMotionSensorEnabled(true);

        motionEventListener = new MotionEventListener(getContext(), mViewModel);

        RunnableThread runnableThread = new RunnableThread(motionEventListener, sensorManager);
        new Thread(runnableThread).start();


    }

    private void setMotionSensorSensitivity(String sensitivity) {
        if (motionEventListener != null) {
            motionEventListener.setTHRESHOLD(sensitivity);
        }
    }

    /**
     * This method was referenced (in part) from Professional Android Sensor Programming, Milette & Stroud,
     * and the example code that comes with the book, method stopReadingAccelerationData() from
     * class DetermineMovementActivity.java
     */
    private void stopReadingMotionData() {
        sensorManager.unregisterListener(motionEventListener);
        mViewModel.setMotionSensorEnabled(false);
        motionEventListener = null;
    }

    /**
     * This method was referenced (in part) from Google documentation on MediaRecorder,
     * https://developer.android.com/guide/topics/media/mediarecorder, and
     * from Professional Android Sensor Programming, Milette & Stroud,
     */
    private void startReadingAudioData() {
        mViewModel.setAudioSensorEnabled(true);

        // This storage needs to exist even though we are not saving the audio files
        String appStorageLocation =
                getContext().getExternalFilesDir("temp_audio").getAbsolutePath()
                        + File.separator + "audio.3gp";

        amplitudeRecorder = new MaxAmplitudeRecorder(appStorageLocation,
                getContext(), mViewModel);

        RunnableThread runnableThread = new RunnableThread(amplitudeRecorder);
        new Thread(runnableThread).start();
    }

    private void setAudioSensorSensitivity(String sensitivity) {
        if (amplitudeRecorder != null) {
            amplitudeRecorder.setTHRESHOLD(sensitivity);
        }
    }

    private void stopReadingAudioData() {
        mViewModel.setAudioSensorEnabled(false);

        if (amplitudeRecorder != null) {
            amplitudeRecorder.stopRecording();
            amplitudeRecorder = null;
        }
    }

    // referenced from https://developer.android.com/reference/android/os/CountDownTimer
    private void createCountDownTimer() {
        countDownTimer = new CountDownTimer(mViewModel.getTimerValue() * 60000, 1000) {
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

    private void pauseTimer() {
        countDownTimer.cancel();
    }

    private void resumeTimer() {

    }

    // Referenced (in part) from: https://www.sitepoint.com/starting-android-development-creating-todo-app/
    private void displayNewLinkPopup(Context context) {
        AlertDialog popup = new AlertDialog.Builder(context)
                .setTitle("Warning")
                .setMessage("Any changes will not be saved. Are you sure?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Go Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        stopReadingAudioData();
                        stopReadingMotionData();
                        requireActivity().getSupportFragmentManager().popBackStack();
                    }
                })
                .create();
        popup.show();
    }

}