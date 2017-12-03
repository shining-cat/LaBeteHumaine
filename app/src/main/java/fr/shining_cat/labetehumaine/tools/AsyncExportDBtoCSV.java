package fr.shining_cat.labetehumaine.tools;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import fr.shining_cat.labetehumaine.BuildConfig;
import fr.shining_cat.labetehumaine.ClientDatas;
import fr.shining_cat.labetehumaine.R;

/**
 * Created by Shiva on 14/09/2017.
 */

public class AsyncExportDBtoCSV extends AsyncTask<String, String, String> {

    private final String TAG = "LOGGING::" + this.getClass().getSimpleName();

    public final String EXPORT_FOLDER_NAME = "BETE_HUMAINE_EXPORTS";

    private Activity mActivity;
    private SQLiteDBHelper dbHelper;
    private ProgressDialog exportingDBDialog;
    private OnExportDBListener mCallback;
    private String exportDBFolderName;
    private String exportDBFileBasename;
    private String errorExternalStorageNonAvailable;
    private String errorRetrievingDataFromDatabase;
    private String errorGeneratingCSVFilename;
    private String errorCreatingExportFolder;
    private String errorCreatingExportFile;
    private String errorWriteLocalFile;
    private String clientsDBComposingCSV;
    private String clientsDBWritingCSVFile;

    public AsyncExportDBtoCSV(Activity activity){
        mActivity = activity;
        try {
            mCallback = (OnExportDBListener) mActivity;
        } catch (ClassCastException e) {
            throw new ClassCastException(mActivity.toString()
                    + " must implement OnExportDBListener");
        }
    }

    @Override
    protected void onPreExecute(){
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "::onPreExecute");
        }
        super.onPreExecute();
        exportDBFolderName = mActivity.getString(R.string.export_db_folder_name);
        exportDBFileBasename = mActivity.getString(R.string.export_db_file_base_name);
        errorExternalStorageNonAvailable = mActivity.getString(R.string.error_external_storage_non_available);
        errorRetrievingDataFromDatabase = mActivity.getString(R.string.error_retrieving_data_from_database);
        errorGeneratingCSVFilename = mActivity.getString(R.string.error_generating_csv_filename);
        errorCreatingExportFolder = mActivity.getString(R.string.error_creating_DB_export_folder);
        errorCreatingExportFile = mActivity.getString(R.string.error_creating_DB_export_file);
        errorWriteLocalFile = mActivity.getString(R.string.error_write_local_file);
        clientsDBComposingCSV = mActivity.getString(R.string.clients_db_composing_csv);
        clientsDBWritingCSVFile = mActivity.getString(R.string.clients_db_writing_csv_file);
        exportingDBDialog = new ProgressDialog(mActivity);
        exportingDBDialog.setIndeterminate(true); //there's no way to know progress for this operation
        exportingDBDialog.setMessage(mActivity.getString(R.string.clients_db_retrieving));
        exportingDBDialog.show();
        dbHelper = new SQLiteDBHelper(mActivity);
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "::dbHelper::" + dbHelper);
        }
    }

    @Override
    protected String doInBackground(String... params) {
        //no reference to activity in the background thread!!
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "::doInBackground");
        }
        //
        if(!isExternalStorageWritable())return errorExternalStorageNonAvailable;
        // first retrieve all records from DBHelper
        List<ClientDatas> allClientRecordsList = dbHelper.getAllClientsRecords();
        if(allClientRecordsList.isEmpty()){
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "DATABASE IS EMPTY?!");
            }
            return errorRetrievingDataFromDatabase;
        }
        publishProgress(clientsDBComposingCSV);
        // compose string content
        StringBuilder builder = new StringBuilder();
        ClientDatas clientDatas = new ClientDatas();
        String line = clientDatas.getColumnHeadersCSVstring();
        builder.append(line).append("\n");
        Iterator iterator = allClientRecordsList.iterator();
        while(iterator.hasNext()){
            ClientDatas clientRecord = (ClientDatas) iterator.next();
            line = clientRecord.toCSVString();
            if (BuildConfig.DEBUG) {
                Log.i(TAG, line);
            }
            builder.append(line).append("\n");
        }
        String fileContent = builder.toString();
        //
        publishProgress(clientsDBWritingCSVFile);
        SimpleDateFormat dateFormatForFileName = new SimpleDateFormat("yyyyMMdd");
        String firstRegistrationDate = allClientRecordsList.get(0).getRegistrationDateFormattedString(dateFormatForFileName);
        String lastRegistrationDate = allClientRecordsList.get(allClientRecordsList.size()-1).getRegistrationDateFormattedString(dateFormatForFileName);
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "::firstRegistrationDate = " + firstRegistrationDate);
            Log.i(TAG, "::lastRegistrationDate = " + lastRegistrationDate);
        }
        if(firstRegistrationDate.isEmpty() || lastRegistrationDate.isEmpty())return errorGeneratingCSVFilename;
        String csvFileName = firstRegistrationDate + "-" + lastRegistrationDate + exportDBFileBasename;
        // Get the directory for the user's public pictures directory.
        File documentsFolderPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        documentsFolderPath.mkdir();
        File exportsStoragePath;
        if(documentsFolderPath.exists()){
            exportsStoragePath = new File(documentsFolderPath, exportDBFolderName);
            exportsStoragePath.mkdir();
            if(!exportsStoragePath.exists()){
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "exportsStoragePath exists : " + exportsStoragePath.exists());
                }
                return errorCreatingExportFolder;
            }
        } else{
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "documentsFolderPath exists : " + documentsFolderPath.exists());
            }
            return errorCreatingExportFolder;
        }
        File exportFile = new File(exportsStoragePath, csvFileName);
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "Trying to create file : " + exportFile.toString());
        }
        if(exportFile.exists()){
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "Export file exists already : " + exportFile.exists());
            }
            /*TODO? here we chose to overwrite existing file with same name, the name is composed using only day granularity, so if there are new records made on the same day after an export, they won't be taken into account, on the other side, past records will have been conserved until DB deletion */
            exportFile.delete();
        }
        try {
            exportFile.createNewFile();
        } catch (IOException e) {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "Export File not created");
            }
            e.printStackTrace();
            return errorCreatingExportFile;
        }
        //write to file
        try {
            FileWriter writer = new FileWriter(exportFile);
            writer.append(fileContent);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            return errorWriteLocalFile;
        }

        return exportFile.getName();
    }
    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "::isExternalStorageWritable::state = " + state);
        }
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        exportingDBDialog.setMessage(values[0]);
    }

    @Override
    protected void onPostExecute(String result) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "::onPostExecute");
        }
        super.onPostExecute(result);
        exportingDBDialog.dismiss();
        //we are on the UI thread here, we can access objects normally
        if(result.equals(errorExternalStorageNonAvailable)||
                        result.equals(errorRetrievingDataFromDatabase)||
                        result.equals(errorGeneratingCSVFilename)||
                        result.equals(errorCreatingExportFolder)||
                        result.equals(errorCreatingExportFile)||
                        result.equals(errorWriteLocalFile)){
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
            mCallback.onExportDBtoCSVComplete(result);
        }
    }
    private DialogInterface.OnClickListener onErrorDismissClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "::onErrorDismissClickListener::onClick");
                mCallback.onExportDBtoCSVComplete(null);
            }
        }
    };
    // caller Activity must implement this interface
    public interface OnExportDBListener {
        void onExportDBtoCSVComplete(String result);
    }
}
