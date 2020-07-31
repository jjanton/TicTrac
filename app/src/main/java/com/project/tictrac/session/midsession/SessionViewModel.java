package com.project.tictrac.session.midsession;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SessionViewModel extends ViewModel {
    private MutableLiveData<Integer> motionCounter = new MutableLiveData<>();
    private MutableLiveData<Integer> audioCounter = new MutableLiveData<>();

    private int timerValue;
    private boolean isTimerPaused;
    private long timeRemaining;

    private String ticName;

    private boolean motionSensorEnabled;
    private boolean audioSensorEnabled;

    private boolean hapticFeedbackEnabled;
    private boolean audioFeedbackEnabled;

    private String motionSensitivity;
    private String audioSensitivity;

    private MotionEventListener motionEventListener;


    public SessionViewModel() {
        motionCounter.setValue(0);
        audioCounter.setValue(0);
        motionEventListener = null;
    }

    public MutableLiveData<Integer> getMotionCounter() { return motionCounter; }

    public MutableLiveData<Integer> getAudioCounter() { return audioCounter; }

    void incrementMotionCounter () {
        Integer newValue = (motionCounter == null) ? 0 : motionCounter.getValue() + 1;
        motionCounter.setValue(newValue);
    }

    void incrementAudioCounter () {
        Integer newValue = (audioCounter.getValue() == null) ? 0 : audioCounter.getValue() + 1;
        audioCounter.setValue(newValue);
    }

    public int getTimerValue() { return timerValue; }

    public void setTimerValue(int timerValue) { this.timerValue = timerValue; }

    public boolean isTimerPaused() { return isTimerPaused; }

    public void setTimerPaused(boolean timerPaused) { isTimerPaused = timerPaused; }

    public String getTicName() { return ticName; }

    public void setTicName(String ticName) { this.ticName = ticName; }

    public boolean isMotionSensorEnabled() { return motionSensorEnabled; }

    public void setMotionSensorEnabled(boolean motionSensorEnabled) { this.motionSensorEnabled = motionSensorEnabled; }

    public boolean isAudioSensorEnabled() { return audioSensorEnabled; }

    public void setAudioSensorEnabled(boolean audioSensorEnabled) { this.audioSensorEnabled = audioSensorEnabled; }

    public boolean isHapticFeedbackEnabled() { return hapticFeedbackEnabled; }

    public void setHapticFeedbackEnabled(boolean hapticFeedbackEnabled) { this.hapticFeedbackEnabled = hapticFeedbackEnabled; }

    public boolean isAudioFeedbackEnabled() { return audioFeedbackEnabled; }

    public void setAudioFeedbackEnabled(boolean audioFeedbackEnabled) { this.audioFeedbackEnabled = audioFeedbackEnabled; }

    public String getMotionSensitivity() { return motionSensitivity; }

    public void setMotionSensitivity(String motionSensitivity) { this.motionSensitivity = motionSensitivity; }

    public String getAudioSensitivity() { return audioSensitivity; }

    public void setAudioSensitivity(String audioSensitivity) { this.audioSensitivity = audioSensitivity; }

    public long getTimeRemaining() {
        return timeRemaining;
    }

    public void setTimeRemaining(long timeRemaining) {
        this.timeRemaining = timeRemaining;
    }

    public MotionEventListener getMotionEventListener() {
        return motionEventListener;
    }

    public void setMotionEventListener(MotionEventListener motionEventListener) {
        this.motionEventListener = motionEventListener;
    }
}