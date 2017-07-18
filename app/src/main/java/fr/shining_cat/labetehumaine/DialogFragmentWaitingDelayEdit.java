package fr.shining_cat.labetehumaine;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Shiva on 08/06/2016.
 */
public class DialogFragmentWaitingDelayEdit extends DialogFragment {

    private final String TAG = "LOGGING::" + this.getClass().getSimpleName();

    private EditText waitingDelayValueText;

    public DialogFragmentWaitingDelayEdit(){
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
        if(MainActivity.DEBUG) {
            Log.i(TAG, "CONSTRUCTOR - EMPTY");
        }
    }

    public static DialogFragmentWaitingDelayEdit newInstance(String title, int currentWaitingDelay){
        DialogFragmentWaitingDelayEdit frag = new DialogFragmentWaitingDelayEdit();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putInt("currentWaitingDelay", currentWaitingDelay);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int currentWaitingDelay = getArguments().getInt("currentWaitingDelay");
        View dialogBody =  getActivity().getLayoutInflater().inflate(R.layout.dialog_fragment_waiting_delay_edit, null);
        waitingDelayValueText = (EditText) dialogBody.findViewById(R.id.txt_waiting_value);
        waitingDelayValueText.setText("" + currentWaitingDelay);
        AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity());
        builder.setTitle(getArguments().getString("title"));
        builder.setView(dialogBody);
        builder.setPositiveButton(getString(R.string.confirm_button_label),null);//null, because we want to override default behavior to control dismissal on positive click
        builder.setNegativeButton(getString(R.string.cancel_button_label),onNegativeClickListener);
        return builder.create();
    }

    //piggyback onStart to implement custom behavior on positive button (with controlled dismissal)
    @Override
    public void onStart(){
        if(MainActivity.DEBUG) {
            Log.i(TAG, "onStart");
        }
        super.onStart();
        final AlertDialog dialog = (AlertDialog)getDialog();
        if(dialog!=null){
            Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(onPositiveClickListener);
        }
    }

    private View.OnClickListener onPositiveClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(MainActivity.DEBUG) {
                Log.i(TAG, "onPositiveClickListener");
            }
            //close dialog, transmit new value to MainActivity which will handle the rest
            ((SettingsActivity)getActivity()).updateWaitingDelay(Integer.parseInt(waitingDelayValueText.getText().toString()));
            dismiss();

        }
    };

    private DialogInterface.OnClickListener onNegativeClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int whichButton) {
            if(MainActivity.DEBUG) {
                Log.i(TAG, "onNegativeClickListener");
            }
            //"cancel" => just close the dialog
            dismiss();
        }
    };



}
