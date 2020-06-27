package com.project.tictrac.session.midsession;

import androidx.lifecycle.ViewModelProviders;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.project.tictrac.R;
import com.project.tictrac.session.presession.SessionDetails;

import static android.content.Context.VIBRATOR_SERVICE;

public class SessionFragment extends Fragment {

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
        vibratorButton.setOnClickListener(v -> vibrate(500));
    }


    /**
     * Make the phone vibrate for a given duration
     * @param duration length of time to make phone vibrate, in ms (1000ms = 1s)
     */
    private void vibrate(int duration) {
        ((Vibrator) getContext().getSystemService(VIBRATOR_SERVICE)).vibrate(
                VibrationEffect.createOneShot(duration,VibrationEffect.DEFAULT_AMPLITUDE));
    }

}