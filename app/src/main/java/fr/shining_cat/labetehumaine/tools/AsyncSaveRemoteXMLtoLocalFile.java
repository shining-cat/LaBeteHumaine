package fr.shining_cat.labetehumaine.tools;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import fr.shining_cat.labetehumaine.BuildConfig;
import fr.shining_cat.labetehumaine.R;

/**
 * Created by Shiva on 18/06/2016.
 */
public class AsyncSaveRemoteXMLtoLocalFile extends AsyncTask<URL, Integer, String> {

    private final String TAG = "LOGGING::" + this.getClass().getSimpleName();

    private String errorNoConnection;
    private String errorReadingDistantXML;
    private String errorWriteLocalFile;
    private String errorWhileRetrievingDistantXML;
    private File pathToInternalAppDatas;
    private String fileName;

    private Activity mActivity;
    private ProgressDialog loadingXmlDialog;
    private OnSaveRemoteToLocalListener mCallback;

    public AsyncSaveRemoteXMLtoLocalFile(Activity activity){
        mActivity = activity;
        try {
            mCallback = (OnSaveRemoteToLocalListener) mActivity;
        } catch (ClassCastException e) {
            throw new ClassCastException(mActivity.toString()
                    + " must implement OnSaveRemoteToLocalListener");
        }
    }

    @Override
    protected void onPreExecute(){
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "AsyncSaveRemoteXMLtoLocalFile::onPreExecute");
        }
        super.onPreExecute();
        pathToInternalAppDatas = mActivity.getFilesDir();
        fileName = BeteHumaineDatas.XML_LOCAL_GENERAL_DATAS_FILE_NAME;
        errorNoConnection = mActivity.getString(R.string.error_no_internet_access);
        errorReadingDistantXML = mActivity.getString(R.string.error_reading_distant_xml);
        errorWriteLocalFile = mActivity.getString(R.string.error_write_local_file);
        errorWhileRetrievingDistantXML = mActivity.getString(R.string.error_retrieving_distant_xml);

        loadingXmlDialog = new ProgressDialog(mActivity);
        loadingXmlDialog.setMessage(mActivity.getString(R.string.xml_file_downloading));
        loadingXmlDialog.show();
    }

    @Override
    protected String doInBackground(URL... params) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "AsyncSaveRemoteXMLtoLocalFile::doInBackground");
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
                    e.printStackTrace();
                    return errorReadingDistantXML;
                }

                //write content as string to file :
                String fileContent = builder.toString();
                File exportFile = new File(pathToInternalAppDatas, fileName);
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "exportFile = " + exportFile.toString());


                }
                //write to file
                try {
                    FileWriter writer = new FileWriter(exportFile);
                    writer.append(fileContent);
                    writer.flush();
                    writer.close();
                } catch (IOException e){
                    e.printStackTrace();
                    return errorWriteLocalFile;
                }
                Long currentTimeMillis = System.currentTimeMillis();
                return currentTimeMillis.toString(); //SUCCESS => return current date
            } else {
                return errorNoConnection;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return errorWhileRetrievingDistantXML;
        } finally {
            connection.disconnect(); // close the HttpURLConnection
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "AsyncSaveRemoteXMLtoLocalFile::onPostExecute");
        }
        super.onPostExecute(result);
        loadingXmlDialog.dismiss();
        //we are on the UI thread here, we can access objects normally
        if(result.equals(errorNoConnection)||
                result.equals(errorReadingDistantXML)||
                result.equals(errorWriteLocalFile)||
                result.equals(errorWhileRetrievingDistantXML)){
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "::onPostExecute::ERROR:: " + result);
            }
            SimpleDialogs.displayParamConfirmAlertDialogWithListener(mActivity,
                    onErrorDismissClickListener,
                    mActivity.getString(R.string.error_title),
                    result,
                    mActivity.getString(R.string.cancel_button_label));
        } else {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "::onPostExecute::SUCCESS:: " + result);
            }
            SimpleDialogs.displayGenericConfirmToast(mActivity, mActivity.getString(R.string.xml_file_download_complete_message));
            mCallback.onSaveRemoteXMLtoLocalFileComplete(result);
        }
    }
    private DialogInterface.OnClickListener onErrorDismissClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "::onErrorDismissClickListener::onClick");
                mCallback.onSaveRemoteXMLtoLocalFileComplete(null);
            }
        }
    };

    // caller Activity must implement this interface
    public interface OnSaveRemoteToLocalListener {
        void onSaveRemoteXMLtoLocalFileComplete(String result);
    }
}
