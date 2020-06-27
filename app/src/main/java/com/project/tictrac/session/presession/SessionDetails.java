package com.project.tictrac.session.presession;

import java.io.Serializable;

/**
 * This class is used to contain all details needed to run a session. The total runtime of
 * a session (can be infinite, no timer), and whether haptic, audio, or visual feedback are
 * enabled (any combination of the three, or none)
 */
public class SessionDetails implements Serializable {

    private String timerValue;
    private boolean hapticFeedback;
    private boolean audioFeedback;
    private boolean visualFeedback;

    public SessionDetails(String timerValue, boolean hapticFeedback, boolean audioFeedback,
                          boolean visualFeedback) {
        this.timerValue = timerValue;
        this.hapticFeedback = hapticFeedback;
        this.audioFeedback = audioFeedback;
        this.visualFeedback = visualFeedback;
    }

    public String getTimerValue() {
        return timerValue;
    }

    public boolean getHapticFeedback() {
        return hapticFeedback;
    }

    public boolean getAudioFeedback() {
        return audioFeedback;
    }

    public boolean getVisualFeedback() {
        return visualFeedback;
    }

}
