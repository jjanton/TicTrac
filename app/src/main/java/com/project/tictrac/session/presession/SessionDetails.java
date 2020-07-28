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
    private boolean motionSensor;
    private boolean audioSensor;
    private String motionSensitivity;
    private String audioSensitivity;
    private boolean hapticFeedback;
    private boolean audioFeedback;

    public SessionDetails(int timerValue, String ticName, boolean motionSensor,
                          boolean audioSensor, String motionSensitivity, String audioSensitivity,
                          boolean hapticFeedback, boolean audioFeedback) {
        this.timerValue = timerValue;
        this.ticName = ticName;
        this.motionSensor = motionSensor;
        this.audioSensor = audioSensor;
        this.motionSensitivity = motionSensitivity;
        this.audioSensitivity = audioSensitivity;
        this.hapticFeedback = hapticFeedback;
        this.audioFeedback = audioFeedback;
    }

    public int getTimerValue() { return timerValue; }

    public String getTicName() { return ticName; }

    public boolean getMotionSensor() {
        return motionSensor;
    }

    public boolean getAudioSensor() {
        return audioSensor;
    }

    public String getMotionSensitivity() { return motionSensitivity; }

    public String getAudioSensitivity() { return audioSensitivity; }

    public boolean getHapticFeedback() { return hapticFeedback; }

    public boolean getAudioFeedback() {
        return audioFeedback;
    }

}
