package fr.shining_cat.labetehumaine.tools;

import android.app.Activity;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import fr.shining_cat.labetehumaine.BuildConfig;
import fr.shining_cat.labetehumaine.MainActivity;

/**
 * Created by Shiva on 26/06/2016.
 */
public class LocalFolderParser {

    private final String TAG = "LOGGING::" + this.getClass().getSimpleName();
    // supported file formats
    public static final ArrayList<String> ACCEPTED_FILE_EXTN = new ArrayList<>(Arrays.asList("jpg", "jpeg"));


    // Reading files paths from internal storage folder given
    public ArrayList<String> getFilePaths(String fullFolderPath) {
        ArrayList<String> filePaths = new ArrayList<>();
        File directory = new File(fullFolderPath);
        // check for directory
        if (directory.isDirectory()) {
            // getting list of file paths
            File[] listFiles = directory.listFiles();
            // Check for count
            if (listFiles.length > 0) {
                // loop through all files
                for (int i = 0; i < listFiles.length; i++) {
                    // get file path
                    String filePath = listFiles[i].getAbsolutePath();
                    // check for supported file extension
                    if (IsSupportedFile(filePath)) {
                        // Add image path to array list
                        filePaths.add(filePath);
                    } else{
                        //file extension is not jpeg
                        Log.i(TAG, "An image was encountered with the wrong extension : File extension must be jpeg or jpg \n\t\t filePath");
                    }
                }
            } else {
                // image directory is empty
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "Image directory is empty");
                }
                return null;
            }
        } else {
            //path is not a directory
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "Path is not a directory");
            }
            return null;
        }
        return filePaths;
    }

    // Check supported file extensions
    private boolean IsSupportedFile(String filePath) {
        String ext = filePath.substring((filePath.lastIndexOf(".") + 1), filePath.length());
        return (ACCEPTED_FILE_EXTN.contains(ext));
    }
}
