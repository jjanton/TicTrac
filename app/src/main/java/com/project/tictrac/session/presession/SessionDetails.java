package com.project.tictrac.session.presession;

import java.io.Serializable;

/**
 * This class is used to contain all details needed to run a session. The total runtime of
 * a session (can be infinite, no timer), and whether haptic, audio, or visual feedback are
 * enabled (any combination of the three, or none)
 */
public class SessionDetails implements Serializable {

    private int timerValue;
    private String ticName;
    private boolean hapticFeedback;
    private boolean audioFeedback;
    private String motionSensitivity;
    private String audioSensitivity;

    public SessionDetails(int timerValue, String ticName, boolean hapticFeedback,
                          boolean audioFeedback, String motionSensitivity, String audioSensitivity) {
        this.timerValue = timerValue;
        this.ticName = ticName;
        this.hapticFeedback = hapticFeedback;
        this.audioFeedback = audioFeedback;
        this.motionSensitivity = motionSensitivity;
        this.audioSensitivity = audioSensitivity;
    }

    public int getTimerValue() { return timerValue; }

    public String getTicName() { return ticName; }

    public boolean getHapticFeedback() {
        return hapticFeedback;
    }

    public boolean getAudioFeedback() {
        return audioFeedback;
    }

    public String getMotionSensitivity() { return motionSensitivity; }

    public String getAudioSensitivity() { return audioSensitivity; }
}
