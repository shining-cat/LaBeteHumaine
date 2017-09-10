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
public class DialogFragmentNumberOfColumnsEdit extends DialogFragment {

    private final String TAG = "LOGGING::" + this.getClass().getSimpleName();

    private EditText numberOfColumnsValueText;

    public DialogFragmentNumberOfColumnsEdit(){
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "CONSTRUCTOR - EMPTY");
        }
    }

    public static DialogFragmentNumberOfColumnsEdit newInstance(String title, int currentNumberOfColumns){
        DialogFragmentNumberOfColumnsEdit frag = new DialogFragmentNumberOfColumnsEdit();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putInt("currentNumberOfColumns", currentNumberOfColumns);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int currentNumberOfColumns = getArguments().getInt("currentNumberOfColumns");
        View dialogBody =  getActivity().getLayoutInflater().inflate(R.layout.dialog_fragment_number_of_columns_edit, null);
        numberOfColumnsValueText = (EditText) dialogBody.findViewById(R.id.txt_number_of_columns_value);
        numberOfColumnsValueText.setText("" + currentNumberOfColumns);
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
        if (BuildConfig.DEBUG) {
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
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "onPositiveClickListener");
            }
            //close dialog, transmit new value to MainActivity which will handle the rest
            ((SettingsActivity)getActivity()).updateNumberOfColumns(Integer.parseInt(numberOfColumnsValueText.getText().toString()));
            dismiss();

        }
    };

    private DialogInterface.OnClickListener onNegativeClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int whichButton) {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "onNegativeClickListener");
            }
            //"cancel" => just close the dialog
            dismiss();
        }
    };



}
