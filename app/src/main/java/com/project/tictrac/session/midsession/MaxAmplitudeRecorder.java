package com.project.tictrac.session.midsession;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Handler;

import com.project.tictrac.Utils;

import java.io.IOException;

//TODO: request mic (and storage permissions?) from user. Check if they're available first

/**
 * This class was referenced (in part) from Professional Android Sensor Programming, Milette & Stroud,
 * and the example code that comes with the book, Class MaxAmplitudeRecorder
 */
public class MaxAmplitudeRecorder {
    private String tmpAudioFile;
    private Context context;
    private SessionViewModel mViewModel;

    private MediaRecorder mediaRecorder;
    private boolean continueRecording;

    private static final int THRESHOLD_LOW = 6000;
    private static final int THRESHOLD_MED = 10000;
    private static final int THRESHOLD_HIGH = 20000;
    private static double THRESHOLD;

    private Handler mViewModelHandler;

    public MaxAmplitudeRecorder(String tmpAudioFile, Context context, SessionViewModel mViewModel) {
        this.tmpAudioFile = tmpAudioFile;
        this.context = context;
        this.mViewModel = mViewModel;

        mViewModelHandler = new Handler();
        setTHRESHOLD(mViewModel.getAudioSensitivity());

        this.continueRecording = false;
    }

    /**
     * This method was referenced (in part) from Professional Android Sensor Programming, Milette & Stroud,
     */
    public void startRecording()  {
        prepareMediaRecorder();
        continueRecording = true;
        mediaRecorder.getMaxAmplitude();

        while (continueRecording) {
            int maxAmplitude = mediaRecorder.getMaxAmplitude();
            System.out.println(maxAmplitude);

            if (maxAmplitude >= THRESHOLD) {
                if (mViewModel.isHapticFeedbackEnabled()) {
                    Utils.vibrate(1000, context);
                }

                mViewModelHandler.post(() -> {
                    mViewModel.incrementAudioCounter();
                });
            }

            // Sleep the thread for a short time so the counter doesn't increase more than 1 per tic
            try {
                Thread.sleep(700);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        stopRecording();
    }

    /**
     * This method was referenced from Google documentation on MediaRecorder,
     * https://developer.android.com/guide/topics/media/mediarecorder
     */

    public void setTHRESHOLD(String sensitivity) {
        // Low sensitivity = High threshold, High sensitivity = Low threshold
        switch (sensitivity) {
            case "Low":
                THRESHOLD = THRESHOLD_HIGH;
                break;
            case "High":
                THRESHOLD = THRESHOLD_LOW;
                break;
            default:
                THRESHOLD = THRESHOLD_MED;
        }
    }

    /**
     * This method was referenced (in part) from Professional Android Sensor Programming, Milette & Stroud,
     */
    private void prepareMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.reset();

        // These function calls need to be called in this order
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(tmpAudioFile);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            System.out.println("MEDIA RECORDER PREPARE() METHOD FAILED");
        }

        mediaRecorder.start();
    }

    public void stopRecording() {
        continueRecording = false;
        mediaRecorder.release();
    }

}
