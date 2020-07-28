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
import com.project.tictrac.session.SessionActivityCallback;

public class SessionSetupFragment extends Fragment {

    private SessionSetupViewModel mViewModel;
    private Button startSessionButton;
    private SessionActivityCallback activityCallback;

    private TextView ticNameTextView;
    private TextView timerTextView;
    private ToggleButton hapticFeedbackToggleButton;
    private ToggleButton audioFeedbackToggleButton;
    private RadioGroup hapticRadioGroup;
    private RadioGroup audioRadioGroup;

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

    /**
     * This method sets up UI elements and click listeners, to be called in onActivityCreated
     */
    private void setupUI() {
        timerTextView = getView().findViewById(R.id.timerTextView);
        ticNameTextView = getView().findViewById(R.id.ticNameTextView);
        hapticFeedbackToggleButton = getView().findViewById(R.id.hapticFeedbackToggleButton);
        audioFeedbackToggleButton = getView().findViewById(R.id.audioFeedbackToggleButton);
        audioRadioGroup = getView().findViewById(R.id.audioRadioGroup);
        hapticRadioGroup = getView().findViewById(R.id.hapticRadioGroup);
        startSessionButton = getView().findViewById(R.id.startSessionButton);


        // Set click listener for startSessionButton. Build a SessionDetails object,
        // and pass it back to the SessionActivity callback
        startSessionButton.setOnClickListener(v -> {

            // Referenced from https://stackoverflow.com/questions/18179124/android-getting-value-from-selected-radiobutton
            int selectedHapticId = hapticRadioGroup.getCheckedRadioButtonId();
            int selectedAudioId = audioRadioGroup.getCheckedRadioButtonId();
            RadioButton hapticRadioButton = getView().findViewById(selectedHapticId);
            RadioButton audioRadioButton = getView().findViewById(selectedAudioId);

            if (timerTextView.getText().toString().equals("")
                    || ticNameTextView.getText().toString().equals("")
                    || selectedHapticId == -1
                    || selectedAudioId == -1) {

                Toast.makeText(getContext(), "FILL IN ALL FIELDS!",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            SessionDetails details = new SessionDetails(
                    Integer.parseInt(timerTextView.getText().toString()),
                    ticNameTextView.getText().toString(),
                    hapticFeedbackToggleButton.isChecked(),
                    audioFeedbackToggleButton.isChecked(),
                    hapticRadioButton.getText().toString(),
                    audioRadioButton.getText().toString());

            activityCallback.beginSessionButtonClicked(details);
        });
    }

}
