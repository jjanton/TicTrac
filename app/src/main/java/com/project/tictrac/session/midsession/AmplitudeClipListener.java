package com.project.tictrac.session.midsession;


/**
 * This interface was referenced from Professional Android Sensor Programming, Milette & Stroud,
 * and the example code that comes with the book, Interface AmplitudeClipListener
 */
public interface AmplitudeClipListener {

    /**
     * Returns true if maxAmplitude is over a set threshold
     * @param maxAmplitude
     * @return
     */
    public boolean heard(int maxAmplitude);

}
