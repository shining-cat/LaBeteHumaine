package fr.shining_cat.labetehumaine;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Shiva on 08/06/2016.
 */
public class DialogFragmentNewPassword extends DialogFragment {

    private final String TAG = "LOGGING::" + this.getClass().getSimpleName();

    private EditText newPasswordText;
    private String previousPass;
    private OnNewPasswordListener mCallback;

    // Container Activity must implement this interface
    public interface OnNewPasswordListener {
        void onNewPasswordFirstEntry(String password);
        void onNewPasswordSecondEntryCorrect(String password);
        void onNewPasswordDismiss();
    }

    public DialogFragmentNewPassword(){
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "CONSTRUCTOR - EMPTY");
        }
    }

    public static DialogFragmentNewPassword newInstance(String title, String previousPass){
        DialogFragmentNewPassword frag = new DialogFragmentNewPassword();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("previousPass", previousPass);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        previousPass = getArguments().getString("previousPass");
        View dialogBody =  getActivity().getLayoutInflater().inflate(R.layout.dialog_fragment_new_password, null);
        newPasswordText = (EditText) dialogBody.findViewById(R.id.new_password_field);
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
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnNewPasswordListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnNewPasswordListener");
        }
    }

    private View.OnClickListener onPositiveClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "onPositiveClickListener - 1");
            }
            String newPass = newPasswordText.getText().toString();
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "onPositiveClickListener - 2 newPass = " + newPass);
            }
            if(newPass.equals("")){
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "onPositiveClickListener - 3 newPass EMPTY");
                }
                Toast message = Toast.makeText(getContext(), getString(R.string.empty_password), Toast.LENGTH_SHORT);
                message.setGravity(Gravity.CENTER, message.getXOffset()/2, message.getYOffset()/2);
                message.show();
                newPasswordText.setText("");
                return;
            }
            if(previousPass.equals("")){
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "onPositiveClickListener - 4 previousPass EMPTY => first step");
                }
                mCallback.onNewPasswordFirstEntry(newPass);
                dismiss();
            } else if(checkPassword(newPass)){
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "onPositiveClickListener - 5 passwords consistent");
                }
                //password correct => close dialog, MainActivity will handle the rest
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "onPositiveClickListener - password correct");
                }
                mCallback.onNewPasswordSecondEntryCorrect(newPass);
                dismiss();
            } else{
                //second password  not the same : display Toast, clear textfield
                Toast message = Toast.makeText(getContext(), getString(R.string.different_passwords), Toast.LENGTH_SHORT);
                message.setGravity(Gravity.CENTER, message.getXOffset()/2, message.getYOffset()/2);
                message.show();
                newPasswordText.setText("");
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "onPositiveClickListener - second password incorrect");
                }
            }
        }
    };

    private DialogInterface.OnClickListener onNegativeClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int whichButton) {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "onNegativeClickListener");
            }
            //"cancel" => notify activity, then close the dialog
            mCallback.onNewPasswordDismiss();
            dismiss();
        }
    };

    private boolean checkPassword(String enteredPass){
        //if previousPass == null => first time we enter a pass, otherwise, check if both are equals
        return (enteredPass.equals(previousPass));
    }

}
