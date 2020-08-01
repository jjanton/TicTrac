package com.project.tictrac.session.presession;

import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.project.tictrac.R;
import com.project.tictrac.Utils;
import com.project.tictrac.session.SessionActivityCallback;

public class SessionSetupFragment extends Fragment {

    private SessionSetupViewModel mViewModel;
    private Button startSessionButton;
    private SessionActivityCallback activityCallback;

    private TextView ticNameTextView;
    private TextView timerTextView;
    private ToggleButton motionSensorToggleButton;
    private ToggleButton audioSensorToggleButton;
    private ToggleButton hapticFeedbackToggleButton;
    private RadioGroup motionSensorRadioGroup;
    private RadioGroup audioSensorRadioGroup;

    public static SessionSetupFragment newInstance() {
        return new SessionSetupFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.session_setup_fragment, container, false);
    }

    /**
     * Defines interface object SessionActivityCallback so this fragment can callback to
     * functions in the SessionActivity
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            activityCallback = (SessionActivityCallback) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "Must Implement SessionActivityCallback");
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        clearAllSettings();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(SessionSetupViewModel.class);

        // Get UI elements, setup click listeners
        setupUI();
        setButtonListeners();
    }

    /**
     * This method sets up UI elements and click listeners, to be called in onActivityCreated
     */
    private void setupUI() {
        requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        timerTextView = getView().findViewById(R.id.timerTextView);
        ticNameTextView = getView().findViewById(R.id.ticNameTextView);
        motionSensorToggleButton = getView().findViewById(R.id.motionSensorToggleButton);
        audioSensorToggleButton = getView().findViewById(R.id.audioSensorToggleButton);
        hapticFeedbackToggleButton = getView().findViewById(R.id.hapticFeedbackToggleButton);
        audioSensorRadioGroup = getView().findViewById(R.id.audioSensorRadioGroup);
        motionSensorRadioGroup = getView().findViewById(R.id.motionSensorRadioGroup);
        startSessionButton = getView().findViewById(R.id.startSessionButton);

        Utils.setRadioGroup(motionSensorRadioGroup, false);
        Utils.setRadioGroup(audioSensorRadioGroup, false);
    }

    private void setButtonListeners() {
        motionSensorToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (motionSensorToggleButton.isChecked()) {
                    Utils.setRadioGroup(motionSensorRadioGroup, true);
                } else {
                    Utils.setRadioGroup(motionSensorRadioGroup, false);
                }
            }
        });

        // Listener for audioSensor toggle
        audioSensorToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (audioSensorToggleButton.isChecked()) {

                    if (Utils.isAudioPermissionGranted(getContext())) {
                        audioSensorToggleButton.setChecked(true);
                        Utils.setRadioGroup(audioSensorRadioGroup, true);
                    } else {
                        requestPermissions(new String[]{
                                Manifest.permission.RECORD_AUDIO}, 1);
                    }

                } else {
                    Utils.setRadioGroup(audioSensorRadioGroup, false);
                }
            }
        });

        // Set click listener for startSessionButton. Build a SessionDetails object,
        // and pass it back to the SessionActivity callback
        startSessionButton.setOnClickListener(v -> {

            // Referenced from https://stackoverflow.com/questions/18179124/android-getting-value-from-selected-radiobutton
            int selectedMotionSensorId = motionSensorRadioGroup.getCheckedRadioButtonId();
            int selectedAudioSensorId = audioSensorRadioGroup.getCheckedRadioButtonId();
            RadioButton motionSensorRadioButton = getView().findViewById(selectedMotionSensorId);
            RadioButton audioSensorRadioButton = getView().findViewById(selectedAudioSensorId);

            // Have to enter a timer value, timer value needs to be at least 1 minute,
            // have to enter a tic name
            if (timerTextView.getText().toString().equals("")
                    || Integer.parseInt(timerTextView.getText().toString()) < 1
                    || ticNameTextView.getText().toString().equals("")) {
                Toast.makeText(getContext(), "Please Enter A Tic Name And A Timer Value Of At Least 1 Minute",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            SessionDetails details = new SessionDetails(
                    Integer.parseInt(timerTextView.getText().toString()),
                    ticNameTextView.getText().toString(),
                    motionSensorToggleButton.isChecked(),
                    audioSensorToggleButton.isChecked(),
                    motionSensorRadioButton == null ? "Medium" : motionSensorRadioButton.getText().toString(),
                    audioSensorRadioButton == null ? "Medium" : audioSensorRadioButton.getText().toString(),
                    hapticFeedbackToggleButton.isChecked()
            );

            activityCallback.beginSessionButtonClicked(details);
        });
    }

    private void clearAllSettings() {
        ticNameTextView.setText(null);
        timerTextView.setText(null);
        motionSensorToggleButton.setChecked(false);
        audioSensorToggleButton.setChecked(false);
        hapticFeedbackToggleButton.setChecked(false);
        Utils.setRadioGroup(motionSensorRadioGroup, false);
        Utils.setRadioGroup(audioSensorRadioGroup, false);
        motionSensorRadioGroup.clearCheck();
        audioSensorRadioGroup.clearCheck();
    }

    // Referenced (in part) from: https://stackoverflow.com/questions/48762146/record-audio-permission-is-not-displayed-in-my-application-on-starting-the-appli/48762230
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getContext(), "Audio Permission Granted",
                    Toast.LENGTH_SHORT).show();

            audioSensorToggleButton.setChecked(true);
            Utils.setRadioGroup(audioSensorRadioGroup, true);
        } else {
            Toast.makeText(getContext(), "Audio Permissions Are Required To Use This Feature",
                    Toast.LENGTH_LONG).show();

            audioSensorToggleButton.setChecked(false);
            Utils.setRadioGroup(audioSensorRadioGroup, false);
        }
    }

}
