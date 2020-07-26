package com.project.tictrac.session.presession;

import java.io.Serializable;

/**
 * This class is used to contain all details needed to run a session. The total runtime of
 * a session (can be infinite, no timer), and whether haptic, audio, or visual feedback are
 * enabled (any combination of the three, or none)
 */
public class SessionDetails implements Serializable {

    private int timerHour;
    private int timerMinute;
    private boolean hapticFeedback;
    private boolean audioFeedback;
    private boolean visualFeedback;

    public SessionDetails(int timerHour, int timerMinute, boolean hapticFeedback, boolean audioFeedback,
                          boolean visualFeedback) {
        this.timerHour = timerHour;
        this.timerMinute = timerMinute;
        this.hapticFeedback = hapticFeedback;
        this.audioFeedback = audioFeedback;
        this.visualFeedback = visualFeedback;
    }

    public int getTimerHour() {
        return timerHour;
    }

    public int getTimerMinute() { return timerMinute; }

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
