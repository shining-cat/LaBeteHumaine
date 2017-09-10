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
public class DialogFragmentAdminCodeRequest extends DialogFragment {

    private final String TAG = "LOGGING::" + this.getClass().getSimpleName();

    private String correctPass;
    private EditText codeRequest;
    private OnAdminCodeRequestListener mCallback;

    // Container Activity must implement this interface
    public interface OnAdminCodeRequestListener {
        void onPasswordCorrect();
        void onPasswordDismiss();
    }

    public DialogFragmentAdminCodeRequest(){
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "CONSTRUCTOR - EMPTY");
        }
    }

    public static DialogFragmentAdminCodeRequest newInstance(String title, String correctPass){
        DialogFragmentAdminCodeRequest frag = new DialogFragmentAdminCodeRequest();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("correctPass", correctPass);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "onCreateDialog");
        }
        correctPass = getArguments().getString("correctPass");
        View dialogBody =  getActivity().getLayoutInflater().inflate(R.layout.dialog_fragment_admin_code_request, null);
        codeRequest = (EditText) dialogBody.findViewById(R.id.txt_code_request);
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
            mCallback = (OnAdminCodeRequestListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnAdminCodeRequestListener");
        }
    }


    private View.OnClickListener onPositiveClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "onPositiveClickListener");
            }
            if(checkPassword(codeRequest.getText().toString())){
                //password correct => close dialog, MainActivity will handle the rest
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "onPositiveClickListener - password correct");
                }
                mCallback.onPasswordCorrect();
                dismiss();
            } else{
                //password incorrect : display Toast, clear textfield
                Toast message = Toast.makeText(getContext(), getString(R.string.wrong_pass_message), Toast.LENGTH_SHORT);
                message.setGravity(Gravity.CENTER, message.getXOffset()/2, message.getYOffset()/2);
                message.show();
                codeRequest.setText("");
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "onPositiveClickListener - password incorrect");
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
            mCallback.onPasswordDismiss();
            dismiss();
        }
    };

    private boolean checkPassword(String enteredPass){
        return enteredPass.equals(correctPass);
    }


}
