package com.project.tictrac.session.midsession;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SessionViewModel extends ViewModel {
    private MutableLiveData<Integer> motionCounter = new MutableLiveData<>();
    private MutableLiveData<Integer> audioCounter = new MutableLiveData<>();

    public SessionViewModel() {
        motionCounter.setValue(0);
        motionCounter.setValue(0);
    }

    public MutableLiveData<Integer> getMotionCounter() {
        return motionCounter;
    }

    public MutableLiveData<Integer> getAudioCounter() {
        return audioCounter;
    }

    void incrementMotionCounter () {
        Integer newValue = (motionCounter.getValue() == null) ? 0 : motionCounter.getValue() + 1;
        motionCounter.setValue(newValue);
    }

    void incrementAudioCounter () {
        Integer newValue = (audioCounter.getValue() == null) ? 0 : audioCounter.getValue() + 1;
        audioCounter.setValue(newValue);
    }

    public int getMotionCounterValue() {
        if (motionCounter == null) {
            return 0;
        } else {
            return (int) motionCounter.getValue();
        }
    }

    public int getAudioCounterValue() {
        if (audioCounter == null) {
            return 0;
        } else {
            return (int) audioCounter.getValue();
        }
    }




}