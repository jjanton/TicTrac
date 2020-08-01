package com.project.tictrac.session;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.project.tictrac.R;
import com.project.tictrac.session.midsession.SessionFragment;
import com.project.tictrac.session.presession.SessionDetails;
import com.project.tictrac.session.presession.SessionSetupFragment;

public class SessionActivity extends AppCompatActivity implements SessionActivityCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, SessionSetupFragment.newInstance())
                    .commitNow();
        }
    }

    /**
     * This is a callback method, called from SessionSetupFragment in order to pass user
     * input values for timer, haptic, audio, and visual feedback from the SessionSetupFragment,
     * to SessionActivity, and then to a SessionFragment we create here
     *
     * @param details SessionDetails object with values for timer, haptic/audio/visual feedback
     */
    @Override
    public void beginSessionButtonClicked(SessionDetails details) {

        Bundle bundle = new Bundle();
        bundle.putSerializable("details", details);

        SessionFragment sessionFragment = new SessionFragment();
        sessionFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, sessionFragment)
                .addToBackStack(null).commit();
    }

}