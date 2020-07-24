package com.project.tictrac.session.midsession;

/**
 * Referenced (in part) from Prof. Dan Feinberg, "Handlers and Runnable" video,
 * Network Example sample code, MAD module 7, and my own project
 */
public class RunnableThread implements Runnable {

    private MaxAmplitudeRecorder amplitudeRecorder;

    public RunnableThread(MaxAmplitudeRecorder amplitudeRecorder) {
        this.amplitudeRecorder = amplitudeRecorder;
    }

    @Override
    public void run() {
        amplitudeRecorder.startRecording();
    }
}
