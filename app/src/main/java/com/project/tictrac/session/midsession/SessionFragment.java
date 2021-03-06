package com.project.tictrac.session.midsession;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.SavedStateViewModelFactory;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.project.tictrac.R;
import com.project.tictrac.Utils;
import com.project.tictrac.session.presession.SessionDetails;

import java.io.File;
import java.util.concurrent.TimeUnit;

import static android.content.Context.SENSOR_SERVICE;

public class SessionFragment extends Fragment {
    private SessionDetails sessionDetails;

    private SensorManager sensorManager;
    private MaxAmplitudeRecorder amplitudeRecorder;

    private CountDownTimer countDownTimer;

    private SessionViewModel mViewModel;
    private TextView countdownTimerTextView;
    private TextView motionCounter;
    private TextView audioCounter;
    private SwitchMaterial motionSensorToggleButton2;
    private SwitchMaterial audioSensorToggleButton2;
    private RadioGroup motionSensorRadioGroup;
    private RadioGroup audioSensorRadioGroup;
    private SwitchMaterial hapticFeedbackToggleButton2;
    private ImageButton pauseButton;
    private ImageButton endSessionButton;

    private boolean firstTime;

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
        pauseEverything();
    }

    @Override
    public void onResume() {
        super.onResume();
        resumeEverything();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupUI();
        setButtonListeners();
        initializeUIState();
        countDownTimer = createCountDownTimer(mViewModel.getTimerValue() * 60000, 1000);
    }

    private void setupUI() {
        // Keep screen on while we are in this fragment
        requireActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // View model setup
        SavedStateViewModelFactory factory = new SavedStateViewModelFactory(
                getActivity().getApplication(), this);
        mViewModel = new ViewModelProvider(this, factory).get(SessionViewModel.class);

        sensorManager = (SensorManager) requireContext().getSystemService(SENSOR_SERVICE);

        // Get UI elements
        countdownTimerTextView = getView().findViewById(R.id.countdownTimerTextView);
        motionCounter = getView().findViewById(R.id.motionCounter);
        audioCounter = getView().findViewById(R.id.audioCounter);
        motionSensorToggleButton2 = getView().findViewById(R.id.motionSensorToggleButton2);
        audioSensorToggleButton2 = getView().findViewById(R.id.audioSensorToggleButton2);
        motionSensorRadioGroup = getView().findViewById(R.id.motionSensorRadioGroup);
        audioSensorRadioGroup = getView().findViewById(R.id.audioSensorRadioGroup);
        hapticFeedbackToggleButton2 = getView().findViewById(R.id.hapticFeedbackToggleButton2);
        motionCounter.setText("Motion Counter: 0");
        audioCounter.setText("Audio Counter: 0");
        pauseButton = getView().findViewById(R.id.pauseButton);
        endSessionButton = getView().findViewById(R.id.endSessionButton);

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
                displayNewPopup(getContext(),
                        "Warning",
                        "Any Changes Will Not Be Saved. Are You Sure?",
                        "Return To Main Menu",
                        "Cancel");
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

                    if (Utils.isAudioPermissionGranted(getContext())) {
                        Utils.setRadioGroup(audioSensorRadioGroup, true);
                        startReadingAudioData();
                    } else {
                        requestPermissions(new String[]{
                                Manifest.permission.RECORD_AUDIO}, 1);
                    }
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

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mViewModel.isTimerPaused() && countDownTimer != null) {
                    pauseEverything();
                } else {
                    resumeEverything();
                }
            }
        });

        endSessionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog popup = new AlertDialog.Builder(getContext())
                        .setTitle("Warning")
                        .setMessage("This Will End The Current Session. Are You Sure?")
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("End Session", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                pauseEverything();
                                endSession();
                            }
                        })
                        .create();
                popup.show();
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

    }

    /**
     * This method was referenced (in part) from Professional Android Sensor Programming, Milette & Stroud,
     * and the example code that comes with the book, method startReadingAccelerationData() from
     * class DetermineMovementActivity.java
     */
    private void startReadingMotionData() {
        mViewModel.setMotionSensorEnabled(true);
        mViewModel.setMotionEventListener(new MotionEventListener(getContext(), mViewModel, sensorManager));
        sensorManager.registerListener(mViewModel.getMotionEventListener(),
                sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void setMotionSensorSensitivity(String sensitivity) {
        if (mViewModel.getMotionEventListener() != null) {
            mViewModel.getMotionEventListener().setTHRESHOLD(sensitivity);
        }
    }

    /**
     * This method was referenced (in part) from Professional Android Sensor Programming, Milette & Stroud,
     * and the example code that comes with the book, method stopReadingAccelerationData() from
     * class DetermineMovementActivity.java
     */
    private void stopReadingMotionData() {
        sensorManager.unregisterListener(mViewModel.getMotionEventListener());
        mViewModel.setMotionSensorEnabled(false);
        mViewModel.setMotionEventListener(null);
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

    /**
     * This method was referenced (in part) from Google documentation on MediaRecorder,
     * https://developer.android.com/guide/topics/media/mediarecorder, and
     * from Professional Android Sensor Programming, Milette & Stroud,
     */
    private void stopReadingAudioData() {
        mViewModel.setAudioSensorEnabled(false);

        if (amplitudeRecorder != null) {
            amplitudeRecorder.stopRecording();
            amplitudeRecorder = null;
        }
    }

    // referenced (in part) from https://developer.android.com/reference/android/os/CountDownTimer
    private CountDownTimer createCountDownTimer(long millis, int interval) {
        mViewModel.setTimerPaused(false);
        firstTime = true;

        return new CountDownTimer(millis, interval) {

            @Override
            public void onTick(long millisUntilFinished) {
                if (mViewModel.isTimerPaused()) {
                    cancel();
                    mViewModel.setTimerPaused(true);
                    countDownTimer = null;
                } else {
                    firstTime = false;
                    mViewModel.setTimeRemaining(millisUntilFinished);
                    mViewModel.setTimerPaused(false);
                    countdownTimerTextView.setText(String.format("%s:%s",
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60,
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60
                    ));
                }

            }

            @Override
            public void onFinish() {
                if (!firstTime) {
                    endSession();
                }
            }
        }.start();

    }

    // Referenced (in part) from: https://www.sitepoint.com/starting-android-development-creating-todo-app/
    private void displayNewPopup(Context context, String title, String message, String positive, String negative) {
        AlertDialog popup = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(negative, null)
                .setPositiveButton(positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        stopReadingAudioData();
                        stopReadingMotionData();
                        Utils.returnToMainMenu(getContext());
                        requireActivity().finish();
                    }
                })
                .create();
        popup.show();
    }

    // Referenced (in part) from: https://stackoverflow.com/questions/48762146/record-audio-permission-is-not-displayed-in-my-application-on-starting-the-appli/48762230
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getContext(), "Audio Permission Granted",
                    Toast.LENGTH_SHORT).show();

            audioSensorToggleButton2.setChecked(true);
            Utils.setRadioGroup(audioSensorRadioGroup, true);
            startReadingAudioData();
        } else {
            Toast.makeText(getContext(), "Audio Permissions Are Required To Use This Feature",
                    Toast.LENGTH_LONG).show();

            audioSensorToggleButton2.setChecked(false);
            Utils.setRadioGroup(audioSensorRadioGroup, false);
            stopReadingAudioData();
        }
    }

    // Referenced from: https://guides.codepath.com/android/using-dialogfragment
    private void createDialogFragment() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        EndSessionDialogFragment dialogFragment = EndSessionDialogFragment.newInstance(
                mViewModel.getTicName(),
                String.valueOf(mViewModel.getMotionCounter().getValue()),
                String.valueOf(mViewModel.getAudioCounter().getValue())
        );
        dialogFragment.show(fragmentManager, "fragment_alert");
    }

    private void endSession() {
        stopReadingAudioData();
        stopReadingMotionData();
        createDialogFragment();
    }

    private void pauseTimer(long millisUntilFinished) {
        countDownTimer.cancel();
        mViewModel.setTimerPaused(true);
        mViewModel.setTimeRemaining(millisUntilFinished);
        countDownTimer = null;
    }

    private void resumeTimer() {
        mViewModel.setTimerPaused(false);
        countDownTimer = createCountDownTimer(mViewModel.getTimeRemaining(), 1000);
    }

    private void pauseEverything() {
        pauseButton.setImageResource(R.drawable.ic_play_circle_outline_24);

        if (countDownTimer != null) {
            pauseTimer(mViewModel.getTimeRemaining());
        }

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

    private void resumeEverything() {
        pauseButton.setImageResource(R.drawable.ic_pause_circle_outline_24);

        resumeTimer();

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


}