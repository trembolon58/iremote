package ru.obsession.iremote;


import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;

public class ProgressDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setCancelable(false);
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(getActivity());
        final View root = getActivity().getLayoutInflater().inflate(R.layout.progress_dialog, null);
        builder.setView(root);
        builder.setInverseBackgroundForced(true);
        return builder.create();
    }
}
