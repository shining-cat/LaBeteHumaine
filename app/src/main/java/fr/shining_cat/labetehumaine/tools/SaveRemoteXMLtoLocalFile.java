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

import fr.shining_cat.labetehumaine.MainActivity;
import fr.shining_cat.labetehumaine.R;

/**
 * Created by Shiva on 18/06/2016.
 */
public class SaveRemoteXMLtoLocalFile extends AsyncTask<URL, Integer, Long> {

    private final String TAG = "LOGGING::" + this.getClass().getSimpleName();

    private Activity mActivity;
    private ProgressDialog loadingXmlDialog;
    private OnSaveRemoteToLocalListener mCallback;

    public SaveRemoteXMLtoLocalFile(Activity activity){
        mActivity = activity;
        try {
            mCallback = (OnSaveRemoteToLocalListener) mActivity;
        } catch (ClassCastException e) {
            throw new ClassCastException(mActivity.toString()
                    + " must implement OnAdminCodeRequestListener");
        }
    }

    @Override
    protected void onPreExecute(){
        if(MainActivity.DEBUG) {
            Log.i(TAG, "SaveRemoteXMLtoLocalFile::onPreExecute");
        }
        super.onPreExecute();
        loadingXmlDialog = new ProgressDialog(mActivity);
        loadingXmlDialog.setMessage(mActivity.getString(R.string.xml_file_downloading));
        loadingXmlDialog.show();
    }

    @Override
    protected Long doInBackground(URL… params) {
        if(MainActivity.DEBUG) {
            Log.i(TAG, "SaveRemoteXMLtoLocalFile::doInBackground");
        }
        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) params[0].openConnection();

            int response = connection.getResponseCode();

            if (response == HttpURLConnection.HTTP_OK) {

                StringBuilder builder = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()))) {
                            String line;
                            while ((line = reader.readLine()) != null) {
                                builder.append(line).append("\n"); //readline() removes the line breaks. We have to re-insert them (although here they're not vital)
                        }
                } catch (IOException e) {
                    SimpleDialogs.displayErrorAlertDialog(mActivity, mActivity.getString(R.string.error_no_connection));
                    e.printStackTrace();
                }

                //write content as string to file :
                String filename = BeteHumaineDatas.XML_LOCAL_FILE_NAME;
                String fileContent = builder.toString();
                FileOutputStream outputStream;
                try {
                    outputStream = mActivity.openFileOutput(filename, Context.MODE_PRIVATE);
                    outputStream.write(fileContent.getBytes());
                    outputStream.close();

                } catch (Exception e) {
                    SimpleDialogs.displayErrorAlertDialog(mActivity, mActivity.getString(R.string.error_write_local_file));
                    e.printStackTrace();
                }
                return System.currentTimeMillis();
            } else {
                SimpleDialogs.displayErrorAlertDialog(mActivity, mActivity.getString(R.string.error_no_connection));
            }
        } catch (Exception e) {
            SimpleDialogs.displayErrorAlertDialog(mActivity, mActivity.getString(R.string.error_no_connection));
            e.printStackTrace();
        } finally {
            connection.disconnect(); // close the HttpURLConnection
        }
        return null;
    }

    @Override
    protected void onPostExecute(Long result) {
        if(MainActivity.DEBUG) {
            Log.i(TAG, "SaveRemoteXMLtoLocalFile::onPostExecute");
        }
        super.onPostExecute(result);
        loadingXmlDialog.dismiss();
        //we are on the UI thread here, we can access objects normally
        SimpleDialogs.displayGenericConfirmToast(mActivity, mActivity.getString(R.string.xml_file_download_complete_message));
        mCallback.onSaveRemoteXMLtoLocalFileComplete(result);
    }
    // caller Activity must implement this interface
    public interface OnSaveRemoteToLocalListener {
        void onSaveRemoteXMLtoLocalFileComplete(Long result);
    }
}
