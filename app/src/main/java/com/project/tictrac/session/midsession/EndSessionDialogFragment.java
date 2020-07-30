package com.project.tictrac.session.midsession;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.se.omapi.Session;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.project.tictrac.MainActivity;
import com.project.tictrac.Utils;
import com.project.tictrac.session.SessionActivity;

import java.util.concurrent.TimeUnit;

// This class referenced (in part) from: https://guides.codepath.com/android/using-dialogfragment
public class EndSessionDialogFragment extends DialogFragment {

    public EndSessionDialogFragment() {

    }

    public static EndSessionDialogFragment newInstance(String ticName, String motionCounterValue, String audioCounterValue) {
        EndSessionDialogFragment fragment = new EndSessionDialogFragment();
        Bundle args = new Bundle();

        args.putString("ticName", ticName);
        args.putString("motionCounterValue", motionCounterValue);
        args.putString("audioCounterValue", audioCounterValue);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        String ticName = getArguments().getString("ticName");
        String motionCounterValue = getArguments().getString("motionCounterValue");
        String audioCounterValue = getArguments().getString("audioCounterValue");

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        setCancelable(false);

        alertDialogBuilder.setTitle("Session Complete!");
        alertDialogBuilder.setMessage(String.format("%s\n%s\n%s",
                "Tic Name: " + ticName,
                "Motor Tics Detected: " + motionCounterValue,
                "Vocal Tics Detected: " + audioCounterValue
        ));

        alertDialogBuilder.setPositiveButton("Return To Main Menu", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Utils.returnToMainMenu(getContext());
                requireActivity().finish();
            }
        });

        return alertDialogBuilder.create();

    }
}
