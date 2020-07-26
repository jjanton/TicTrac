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
import android.widget.TimePicker;
import android.widget.ToggleButton;

import com.project.tictrac.R;
import com.project.tictrac.session.SessionActivityCallback;

public class SessionSetupFragment extends Fragment {

    private SessionSetupViewModel mViewModel;
    private TimePicker timePicker;
    private ToggleButton hapticFeedbackToggleButton;
    private ToggleButton audioFeedbackToggleButton;
    private ToggleButton visualFeedbackToggleButton;
    private Button startSessionButton;
    private SessionActivityCallback activityCallback;

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
        timePicker = getView().findViewById(R.id.timerPicker);
        hapticFeedbackToggleButton = getView().findViewById(R.id.hapticFeedbackToggleButton);
        audioFeedbackToggleButton = getView().findViewById(R.id.audioFeedbackToggleButton);
        visualFeedbackToggleButton = getView().findViewById(R.id.visualFeedbackToggleButton);
        startSessionButton = getView().findViewById(R.id.startSessionButton);

        // Set click listener for startSessionButton. Build a SessionDetails object,
        // and pass it back to the SessionActivity callback
        startSessionButton.setOnClickListener(v -> {
            SessionDetails details = new SessionDetails(
                    timePicker.getHour(),
                    timePicker.getMinute(),
                    hapticFeedbackToggleButton.isChecked(),
                    audioFeedbackToggleButton.isChecked(),
                    visualFeedbackToggleButton.isChecked());
            activityCallback.beginSessionButtonClicked(details);
        });
    }

}
