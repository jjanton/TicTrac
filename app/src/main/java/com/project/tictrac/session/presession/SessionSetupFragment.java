package com.project.tictrac.session.presession;

import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private ToggleButton audioFeedbackToggleButton;
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(SessionSetupViewModel.class);

        // Get UI elements, setup click listeners
        setupUI();
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

    /**
     * This method sets up UI elements and click listeners, to be called in onActivityCreated
     */
    private void setupUI() {
        timerTextView = getView().findViewById(R.id.timerTextView);
        ticNameTextView = getView().findViewById(R.id.ticNameTextView);
        motionSensorToggleButton = getView().findViewById(R.id.motionSensorToggleButton);
        audioSensorToggleButton = getView().findViewById(R.id.audioSensorToggleButton);
        hapticFeedbackToggleButton = getView().findViewById(R.id.hapticFeedbackToggleButton);
        audioFeedbackToggleButton = getView().findViewById(R.id.audioFeedbackToggleButton);
        audioSensorRadioGroup = getView().findViewById(R.id.audioSensorRadioGroup);
        motionSensorRadioGroup = getView().findViewById(R.id.motionSensorRadioGroup);
        startSessionButton = getView().findViewById(R.id.startSessionButton);

        Utils.setRadioGroup(motionSensorRadioGroup, false);
        Utils.setRadioGroup(audioSensorRadioGroup, false);

        motionSensorToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (motionSensorToggleButton.isChecked()) {
                    Utils.setRadioGroup(motionSensorRadioGroup,true);
                } else {
                    Utils.setRadioGroup(motionSensorRadioGroup,false);
                }
            }
        });

        // Listener for audioSensor toggle
        audioSensorToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (audioSensorToggleButton.isChecked()) {
                    Utils.setRadioGroup(audioSensorRadioGroup,true);
                } else {
                    Utils.setRadioGroup(audioSensorRadioGroup,false);
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

            if (timerTextView.getText().toString().equals("")
                    || ticNameTextView.getText().toString().equals("")) {
                Toast.makeText(getContext(), "Enter a tic name and a timer value!",
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
                    hapticFeedbackToggleButton.isChecked(),
                    audioFeedbackToggleButton.isChecked()
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
        audioFeedbackToggleButton.setChecked(false);
        Utils.setRadioGroup(motionSensorRadioGroup,false);
        Utils.setRadioGroup(audioSensorRadioGroup,false);
        motionSensorRadioGroup.clearCheck();
        audioSensorRadioGroup.clearCheck();
    }

}
