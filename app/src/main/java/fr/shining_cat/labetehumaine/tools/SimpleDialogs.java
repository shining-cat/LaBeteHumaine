package fr.shining_cat.labetehumaine.tools;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.StringDef;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.widget.Toast;

import fr.shining_cat.labetehumaine.R;

/**
 * Created by Shiva on 18/06/2016.
 */
public class SimpleDialogs {

    private final static String TAG = "LOGGING::SimpleDialogs";

    public static final void displayErrorAlertDialog(Context context, String message){
       AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setTitle(context.getString(R.string.error_title));
        builder.setNegativeButton(context.getString(R.string.cancel_button_label), null);
        builder.create().show();
    }

    public static final void displayConfirmAlertDialog(Context context, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setTitle(R.string.attention_title);
        builder.setNegativeButton(context.getString(R.string.confirm_button_label), null);
        builder.create().show();
    }

    public static final void displayParamConfirmAlertDialogWithListener(Context context, DialogInterface.OnClickListener confirmListener, String title, String message, String confirmButtonLabel){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setTitle(title);
        builder.setNegativeButton(confirmButtonLabel, confirmListener);
        builder.create().show();
    }

    public static final void displayParamAlertDialogWithTwoListeners(Context context, DialogInterface.OnClickListener confirmListener, DialogInterface.OnClickListener dismissListener, String title, String message, String confirmButtonLabel, String dismissButtonLabel){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setTitle(title);
        builder.setNegativeButton(dismissButtonLabel, dismissListener);
        builder.setPositiveButton(confirmButtonLabel, confirmListener);
        builder.create().show();
    }

    public static final void displayParamConfirmAlertDialog(Context context, String title, String message, String confirmButtonLabel){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setTitle(title);
        builder.setNegativeButton(confirmButtonLabel, null);
        builder.create().show();
    }

    public static final void displayValueSetConfirmToast(Context context) {
        displayGenericConfirmToast(context, context.getString(R.string.confirm_value_set_message));
    }

    public static final void displayGenericConfirmToast(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, toast.getXOffset() / 2, toast.getYOffset() / 2);
        toast.show();
    }


}
