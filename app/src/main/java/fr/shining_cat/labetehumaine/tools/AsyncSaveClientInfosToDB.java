package fr.shining_cat.labetehumaine.tools;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import fr.shining_cat.labetehumaine.BuildConfig;
import fr.shining_cat.labetehumaine.ClientDatas;
import fr.shining_cat.labetehumaine.R;

/**
 * Created by Shiva on 18/06/2016.
 */
public class AsyncSaveClientInfosToDB extends AsyncTask<ClientDatas, Integer, Long> {

    private final String TAG = "LOGGING::" + this.getClass().getSimpleName();

    private Activity mActivity;
    private SQLiteDBHelper dbHelper;
    private ProgressDialog recordingClientInfosDialog;
    private OnSaveClientInfosToDBListener mCallback;

    public AsyncSaveClientInfosToDB(Activity activity){
        mActivity = activity;
        try {
            mCallback = (OnSaveClientInfosToDBListener) mActivity;
        } catch (ClassCastException e) {
            throw new ClassCastException(mActivity.toString()
                    + " must implement OnSaveClientInfosToDBListener");
        }
    }

    @Override
    protected void onPreExecute(){
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "AsyncSaveClientInfosToDB::onPreExecute");
        }
        super.onPreExecute();
        recordingClientInfosDialog = new ProgressDialog(mActivity);
        recordingClientInfosDialog.setIndeterminate(true); //there's no way to know progress for this operation, plus it should be extremely brief, this progressDialog is there just in case
        recordingClientInfosDialog.setMessage(mActivity.getString(R.string.recordingClientInfos));
        recordingClientInfosDialog.show();
        //connect to Database
        dbHelper = new SQLiteDBHelper(mActivity);
    }

    @Override
    protected Long doInBackground(ClientDatas... params) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "AsyncSaveClientInfosToDB::doInBackground");
        }
        Long result = dbHelper.addClientRecord(params[0]);
        return result;
    }

    @Override
    protected void onPostExecute(Long result) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "AsyncSaveClientInfosToDB::onPostExecute:: result = " + result);
        }
        super.onPostExecute(result);
        recordingClientInfosDialog.dismiss();
        mCallback.onSaveClientInfosToDBComplete(result);
    }
    // caller Activity must implement this interface to handle result
    public interface OnSaveClientInfosToDBListener {
        void onSaveClientInfosToDBComplete(Long result);
    }
}
