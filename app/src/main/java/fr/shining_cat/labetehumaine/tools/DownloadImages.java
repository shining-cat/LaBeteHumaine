package fr.shining_cat.labetehumaine.tools;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import fr.shining_cat.labetehumaine.MainActivity;
import fr.shining_cat.labetehumaine.R;

/**
 * Created by Shiva on 19/06/2016.
 */
public class DownloadImages {

    private final String TAG = "LOGGING::" + this.getClass().getSimpleName();

    private Activity mActivity;
    private ProgressDialog progressDialog;
    private String mFolderPath;
    private Iterator<String> picturesIterator;
    private String currentlyLoadingURL;
    private int currentlyLoadingURLIndex;
    private File currentlyWritingFile;
    private int numberOfURLS;
    private String dialogTitle;


    // Caller must implement this interface
    public interface DownloadImagesListener {
        public void onDownloadImagesComplete();
        public void onDownloadImagesError(String errorURL);
        public void onSaveImagesError(String fileName, String errorMessage);
    }

    public DownloadImages(Activity activity) {
        mActivity = activity;
    }



    public void launchDownload(ArrayList<String> urls, String loadingTitle, String folderPath){
        if(MainActivity.DEBUG) {
            Log.i(TAG, "launchDownload :: " + loadingTitle);
        }
        mFolderPath = folderPath;
        dialogTitle = loadingTitle;

        picturesIterator = urls.iterator();
        numberOfURLS = urls.size();
        currentlyLoadingURLIndex = 0;

        downloadNextPicture();

    }

    private void downloadNextPicture(){
        //continue loading queue loop to next item
        if (picturesIterator.hasNext()){
            if(MainActivity.DEBUG) {
                Log.i(TAG, "downloadNextPicture :: NEXT PICTURE");
            }
            currentlyLoadingURL = picturesIterator.next();
            currentlyLoadingURLIndex +=1;
            if(MainActivity.DEBUG) {
                Log.i(TAG, "downloadNextPicture :: currentlyLoadingURL = " + currentlyLoadingURL);
            }
            final BasicImageDownloader downloader = new BasicImageDownloader(basicImageLoaderListener);
            updateProgressDialog(mActivity.getString(R.string.pictures_loading_message, currentlyLoadingURLIndex, numberOfURLS));
            downloader.download(currentlyLoadingURL, true);
        } else{
            //exit loading queue loop
            if(MainActivity.DEBUG) {
                Log.i(TAG, "downloadNextPicture :: QUEUE END");
            }
            resetVariables();
            ((DownloadImagesListener) mActivity).onDownloadImagesComplete();
        }
    }

    private void updateProgressDialog(String message){
        if(progressDialog != null){
            progressDialog.dismiss();
        }
        progressDialog = new ProgressDialog(mActivity);
        progressDialog.setTitle(dialogTitle);
        progressDialog.setMessage(message);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    private void resetVariables(){
        if(MainActivity.DEBUG) {
            Log.i(TAG, "resetVariables");
        }
        currentlyLoadingURL = "";
        picturesIterator = null;
        if(progressDialog != null) {
            progressDialog.dismiss();
        }
    }
    private BasicImageDownloader.OnImageLoaderListener basicImageLoaderListener = new BasicImageDownloader.OnImageLoaderListener() {
        @Override
        public void onError(BasicImageDownloader.ImageError error) {
            if(MainActivity.DEBUG) {
                Log.i(TAG, "onError :: Error code " + error.getErrorCode() + ": " + error.getMessage());
            }
            error.printStackTrace();
            ((DownloadImagesListener) mActivity).onDownloadImagesError(currentlyLoadingURL);
            resetVariables();
            //if an error occurs here, we stop and force user (=admin of the shop app) to begin again and find the problem
        }

        @Override
        public void onProgressChange(int percent) {
            progressDialog.setProgress(percent);
        }

        @Override
        public void onComplete(Bitmap result) {
            // save the image as JPEG
            final Bitmap.CompressFormat mFormat = Bitmap.CompressFormat.JPEG;
            //strip picture filename from its extension, then add the format ("JPEG") as lower case => pb if people send .jpg files, the stored name will not be consistent with the new filename
            //String fileName = currentlyLoadingURL.substring(currentlyLoadingURL.lastIndexOf('/')+1, currentlyLoadingURL.lastIndexOf('.'));//get original name of the picture from url
            //String filePathString = mActivity.getFilesDir() + File.separator + mFolderPath + File.separator + fileName + "." + mFormat.name().toLowerCase());
            //picture name conserving original extension : allows to use the common ".jpg" instead of forcing people to use ".JPEG"
            String fileName = currentlyLoadingURL.substring(currentlyLoadingURL.lastIndexOf('/')+1);//get original name of the picture from url
            String filePathString = mActivity.getFilesDir() + File.separator + mFolderPath + File.separator + fileName;
            //construct full path
            final File myImageFile = new File(filePathString);
            currentlyWritingFile = myImageFile;
            currentlyLoadingURL = "";
            BasicImageDownloader.writeToDisk(myImageFile, result, onBitmapSaveListener, mFormat, false);
        }
    };

    private BasicImageDownloader.OnBitmapSaveListener onBitmapSaveListener = new BasicImageDownloader.OnBitmapSaveListener() {
        @Override
        public void onBitmapSaveError(BasicImageDownloader.ImageError error) {
            if(MainActivity.DEBUG) {
                Log.i(TAG, "onBitmapSaveError :: Error code " + error.getErrorCode() + ": " + error.getMessage());
            }
            error.printStackTrace();
            String errorMessage = mActivity.getString(R.string.error_writing_picture_to_disk_generik);
            if(error.getErrorCode() == BasicImageDownloader.ImageError.ERROR_FILE_EXISTS){
                errorMessage = mActivity.getString(R.string.error_writing_picture_to_disk_double);
            }
            ((DownloadImagesListener) mActivity).onSaveImagesError(currentlyWritingFile.getName(), errorMessage);
            resetVariables();
            //if an error occurs here, we stop and force user (=admin of the shop app) to begin again and find the problem
        }
        @Override
        public void onBitmapSaved() {
            if(MainActivity.DEBUG) {
                Log.i(TAG, "onBitmapSaved :: " + currentlyWritingFile.getAbsolutePath());
            }
            currentlyWritingFile = null;
            //continue loop on loading queue
            downloadNextPicture();
        }
    };
}