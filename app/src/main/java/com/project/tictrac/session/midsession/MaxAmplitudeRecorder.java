package com.project.tictrac.session.midsession;

import android.content.Context;
import android.media.MediaRecorder;

import com.project.tictrac.Utils;

import java.io.IOException;

/**
 * This class was referenced (in part) from Professional Android Sensor Programming, Milette & Stroud,
 * and the example code that comes with the book, Class MaxAmplitudeRecorder
 */

//TODO: request mic (and storage permissions?) from user. Check if they're available first

public class MaxAmplitudeRecorder {
    // Constructor arguments
    private int amplitudeThreshold;
    private String tmpAudioFile;
    private Context context;

    // Audio variables
    private AmplitudeClipListener clipListener;
    private MediaRecorder mediaRecorder;
    private boolean continueRecording;

    private static final int THRESHOLD_LOW = 10000;
    private static final int THRESHOLD_MED = 18000;
    private static final int THRESHOLD_HIGH = 25000;


    public MaxAmplitudeRecorder(int amplitudeThreshold, String tmpAudioFile, Context context) {
        // Class variables
        this.amplitudeThreshold = amplitudeThreshold;
        this.tmpAudioFile = tmpAudioFile;
        this.context = context;

        this.continueRecording = false;
        this.clipListener = createAmplitudeClipListener(amplitudeThreshold);
    }

    /**
     * This method was referenced (in part) from Professional Android Sensor Programming, Milette & Stroud,
     */
    public void startRecording() {
        prepareMediaRecorder();

        boolean heard = false;
        continueRecording = true;
        mediaRecorder.getMaxAmplitude();

        while (continueRecording) {
            int maxAmplitude = mediaRecorder.getMaxAmplitude();
            heard = clipListener.heard(maxAmplitude);

            if (heard) {
                Utils.vibrate(1000, context);
                System.out.println("HEARD A SOUND!!!!!");
            }
        }

        stopRecording();
    }

    /**
     * This method was referenced from Google documentation on MediaRecorder,
     * https://developer.android.com/guide/topics/media/mediarecorder
     */
    public void stopRecording() {
        mediaRecorder.stop();
        mediaRecorder.release();
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


    private AmplitudeClipListener createAmplitudeClipListener(int amplitudeThreshold) {

        return new AmplitudeClipListener() {
            @Override
            public boolean heard(int maxAmplitude) {
                return maxAmplitude >= amplitudeThreshold;
            }
        };
    }

}
