package com.aditya.tweetradar.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import com.aditya.tweetradar.R;

/**
 * Created by amodi on 3/26/17.
 */

public class DraftDialogFragment extends DialogFragment {

    DraftDialogFragmentListener draftDialogFragmentListener;

    public interface DraftDialogFragmentListener {
        void onSave();
        void onDismiss();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        draftDialogFragmentListener = (DraftDialogFragmentListener) getTargetFragment();
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage(getString(R.string.save_draft));
        //null should be your on click listener
        alertDialogBuilder.setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dismiss();
                draftDialogFragmentListener.onSave();
            }
        });
        alertDialogBuilder.setNegativeButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                draftDialogFragmentListener.onDismiss();
            }
        });
        return alertDialogBuilder.create();
    }
}
