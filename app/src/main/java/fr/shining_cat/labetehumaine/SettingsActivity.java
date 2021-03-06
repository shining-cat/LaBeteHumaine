package fr.shining_cat.labetehumaine;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import fr.shining_cat.labetehumaine.tools.AsyncExportDBtoCSV;
import fr.shining_cat.labetehumaine.tools.AsyncSaveRemoteXMLtoLocalFile;
import fr.shining_cat.labetehumaine.tools.BeteHumaineDatas;
import fr.shining_cat.labetehumaine.tools.DownloadImages;
import fr.shining_cat.labetehumaine.tools.LocalXMLParser;
import fr.shining_cat.labetehumaine.tools.SQLiteDBHelper;
import fr.shining_cat.labetehumaine.tools.SimpleDialogs;

/**
 * Created by Shiva on 08/06/2016.
 */


public class SettingsActivity extends AppCompatActivity
            implements  DialogFragmentAdminCodeRequest.OnAdminCodeRequestListener,
                        DialogFragmentNewPassword.OnNewPasswordListener,
                        AsyncSaveRemoteXMLtoLocalFile.OnSaveRemoteToLocalListener,
                        DownloadImages.DownloadImagesListener,
                        AsyncExportDBtoCSV.OnExportDBListener{

    private final String TAG = "LOGGING::" + this.getClass().getSimpleName();

    private final static String FACE = "profile_picture";
    private final static String TATTOOS = "tattoos";
    private final static String DRAWINGS = "drawings";

    private SharedPreferences savedSettings;
    private BeteHumaineDatas beteHumaineDatas;
    private Iterator<ArtistDatas> shopIterator;
    private String currentlyLoadingCategory = "";
    private ArtistDatas currentArtistLoading;
    private String justExportedCSVFileName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "onCreate");
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "onStart");
        }
        beteHumaineDatas = BeteHumaineDatas.getInstance();
        //retrieve the SharedPreferences
        savedSettings = getSharedPreferences(MainActivity.SETTINGS_FILE_NAME, MODE_PRIVATE);
        //initialization of all the components
        updateXMLFileAddress("");
        Button xmlFileAddressEditButton = (Button) findViewById(R.id.xml_file_address_edit_button);
        xmlFileAddressEditButton.setOnClickListener(xmlFileAddressEditButtonListener);
        //
        updateLastDlXML(-1);
        Button downloadXMLFileButton = (Button) findViewById(R.id.xml_file_download_button);
        downloadXMLFileButton.setOnClickListener(downloadXMLFileButtonListener);
        //
        updateLastDldatas(null);
        Button downloadDatasButton = (Button) findViewById(R.id.datas_download_button);
        downloadDatasButton.setOnClickListener(downloadDatasButtonListener);
        //
        updateWaitingDelay(-1);
        Button waitingDelayEditButton = (Button) findViewById(R.id.waiting_delay_edit_button);
        waitingDelayEditButton.setOnClickListener(waitingDelayEditButtonListener);
        //
        String rest_screen = savedSettings.getString(getString(R.string.resting_screen_pref_key), MainActivity.REST_TO_WAITING_SCREEN);
        ToggleButton restScreenToggleButton = (ToggleButton) findViewById(R.id.rest_screen_choice_toggle_button);
        restScreenToggleButton.setChecked((rest_screen.equals(MainActivity.REST_TO_WAITING_SCREEN)));
        restScreenToggleButton.setOnClickListener(restScreenClicButtonlistener);
        //
        updateWelcomeText("");
        Button welcomeTextEditButton = (Button) findViewById(R.id.custom_welcome_text_edit_button);
        welcomeTextEditButton.setOnClickListener(welcomeTextEditButtonListener);
        //
        Boolean displayWelcomeTextOnGallery = savedSettings.getBoolean(getString(R.string.show_welcome_text_on_gallery_pref_key), false);
        ToggleButton displayWelcomeTextOnGalleryToggleButton = (ToggleButton) findViewById(R.id.display_welcome_text_on_gallery_screen_toggle_button);
        displayWelcomeTextOnGalleryToggleButton.setChecked(displayWelcomeTextOnGallery);
        displayWelcomeTextOnGalleryToggleButton.setOnClickListener(displayWelcomeTextOnGalleryClicButtonlistener);
        //
        Boolean forceFitArtistsCardsOnGalleryScreen = savedSettings.getBoolean(getString(R.string.force_fit_artists_cards_to_gallery_screen_pref_key), false);
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "onStart::forceFitArtistsCardsOnGalleryScreen = " + forceFitArtistsCardsOnGalleryScreen);
        }
        ToggleButton forceFitArtistsCardsOnGalleryScreenToggleButton = (ToggleButton) findViewById(R.id.force_fit_gallery_toggle_button);
        forceFitArtistsCardsOnGalleryScreenToggleButton.setChecked(forceFitArtistsCardsOnGalleryScreen);
        forceFitArtistsCardsOnGalleryScreenToggleButton.setOnClickListener(forceFitArtistsCardsOnGalleryScreenClicButtonlistener);
        //
        updateNumberOfColumns(-1);
        Button numberOfColumnsEditButton = (Button) findViewById(R.id.number_of_columns_edit_button);
        numberOfColumnsEditButton.setOnClickListener(numberOfColumnsEditButtonListener);
        //
        String current_mode = savedSettings.getString(getString(R.string.current_mode_pref_key), MainActivity.STANDARD_MODE);
        ToggleButton adminCodeToggleButton = (ToggleButton) findViewById(R.id.admin_code_toggle_button);
        adminCodeToggleButton.setChecked((current_mode.equals(MainActivity.KIOSK_MODE)));
        adminCodeToggleButton.setOnClickListener(adminCodeClicButtonlistener);
        //
        Boolean showClientFormAccessButton = savedSettings.getBoolean(getString(R.string.show_client_form_access_button_pref_key), false);
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "onStart::showClientFormAccessButton = " + showClientFormAccessButton);
        }
        ToggleButton showClientFormAccessButtonToggleButton = (ToggleButton) findViewById(R.id.activate_client_form_toggle_button);
        showClientFormAccessButtonToggleButton.setChecked(showClientFormAccessButton);
        showClientFormAccessButtonToggleButton.setOnClickListener(showClientFormAccessButtonClicButtonlistener);
        //
        updateIdleFormDelay(-1);
        Button updateIdleFormDelayEditButton = (Button) findViewById(R.id.form_idle_delay_edit_button);
        updateIdleFormDelayEditButton.setOnClickListener(idleFormDelayEditButtonListener);
        //
        updateEmailExportAddress("");
        Button emailExportAddressEditButton = (Button) findViewById(R.id.email_export_address_edit_button);
        emailExportAddressEditButton.setOnClickListener(emailExportAddressEditButtonListener);
        //
        updateLastExportDB(-1);
        Button exportDBtoCSVButton = (Button) findViewById(R.id.clients_db_export_button);
        SQLiteDBHelper dbHelper = new SQLiteDBHelper(this);
        int numberOfRecords = dbHelper.getTotalNumberOfRecords();
        if(numberOfRecords > 0) {
            exportDBtoCSVButton.setOnClickListener(exportDBtoCSVButtonListener);
        } else{
            exportDBtoCSVButton.setVisibility(View.INVISIBLE);
        }
        //
        updateDeleteDB();
        Button deleteDBButton = (Button) findViewById(R.id.erase_clients_db_button);
        deleteDBButton.setOnClickListener(deleteDBButtonListener);


    }

    private OnClickListener exportDBtoCSVButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "exportDBtoCSVButtonListener::onClick");
            }
            AsyncExportDBtoCSV asyncExportDBtoCSV = new AsyncExportDBtoCSV(SettingsActivity.this);
            asyncExportDBtoCSV.execute();
        }
    };
    public void onExportDBtoCSVComplete(String exportedCSVFileName){
        //AsyncExportDBtoCSV notifies us that DB export is complete, along with the resulting file path, or null;
        if(exportedCSVFileName != null && !exportedCSVFileName.isEmpty()){
            justExportedCSVFileName = exportedCSVFileName;
            updateLastExportDB(System.currentTimeMillis());
            updateDeleteDB();
            SimpleDialogs.displayParamAlertDialogWithTwoListeners(this, onConfirmSendCSVexported, onDismissGotoCSVExported,
                    getString(R.string.clients_db_export_complete_dialog_title), getString(R.string.clients_db_export_complete_dialog_message, exportedCSVFileName),
                    getString(R.string.confirm_button_label), getString(R.string.not_now_button_label));
        }else{
            SimpleDialogs.displayErrorAlertDialog(SettingsActivity.this, getString(R.string.error_exporting_db_to_csv));
        }
    }
    DialogInterface.OnClickListener onDismissGotoCSVExported = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "onDismissGotoCSVExported");
                dialog.dismiss();
            }
        }
    };
    DialogInterface.OnClickListener onConfirmSendCSVexported = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "onConfirmSendCSVexported");
            }
            sendExportCSVInEmail();
       }
    };
    private void sendExportCSVInEmail(){
        if(justExportedCSVFileName!=null && !justExportedCSVFileName.isEmpty()) {
            File documentsFolderPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            File exportsStoragePath = new File(documentsFolderPath, SettingsActivity.this.getString(R.string.export_db_folder_name));
            File exportFile = new File(exportsStoragePath, justExportedCSVFileName);
            Uri path = Uri.fromFile(exportFile);
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            // set the type to 'email'
            emailIntent.setType("vnd.android.cursor.dir/email");
            String to[] = {savedSettings.getString(getString(R.string.email_export_address_pref_key), "")};
            emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
            // the attachment
            emailIntent.putExtra(Intent.EXTRA_STREAM, path);
            // the mail subject
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, this.getString(R.string.email_export_subject));
            startActivity(Intent.createChooser(emailIntent, this.getString(R.string.email_export_intent_title)));
        } else{
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "no export csv file name!!!");
            }
        }
    }
    private OnClickListener deleteDBButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "deleteDBButtonListener::onClick");
            }
            SimpleDialogs.displayParamAlertDialogWithTwoListeners(SettingsActivity.this, onConfirmDeleteDB, onDismissDeleteDB,
                    getString(R.string.clients_db_erase_dialog_title), getString(R.string.clients_db_erase_dialog_message),
                    getString(R.string.clients_db_erase_button_label), getString(R.string.cancel_button_label));
        }
    };
    DialogInterface.OnClickListener onDismissDeleteDB = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "onDismissDeleteDB");
                dialog.dismiss();
            }
        }
    };
    DialogInterface.OnClickListener onConfirmDeleteDB = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "onConfirmDeleteDB");
            }
            deleteDBClients();
        }
    };
    private void deleteDBClients(){
        SQLiteDBHelper dbHelper = new SQLiteDBHelper(this);
        boolean deleteSuccess = dbHelper.deleteClientsDB();
        if(deleteSuccess){
            SimpleDialogs.displayGenericConfirmToast(this, this.getString(R.string.clients_db_erase_complete_message));
            updateDeleteDB();
        } else{
            SimpleDialogs.displayErrorAlertDialog(this, this.getString(R.string.clients_db_erase_error_message));
        }
    }
    private OnClickListener xmlFileAddressEditButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "xmlFileAddressEditButtonListener::onClick");
            }
            String currentXMLFileAddress = savedSettings.getString(
                    getString(R.string.xml_file_address_pref_key), getString(R.string.xml_file_address_text_default));
            FragmentManager fm = getSupportFragmentManager();
            DialogFragmentXMLFileAddressEdit dialogFragmentXMLFileAddressEdit =
                    DialogFragmentXMLFileAddressEdit.newInstance(getString(R.string.xml_file_address_label), currentXMLFileAddress);
            dialogFragmentXMLFileAddressEdit.show(fm, "dialog_fragment_xml_file_address_edit");
        }
    };
    private OnClickListener emailExportAddressEditButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "emailExportAddressEditButtonListener::onClick");
            }
            String currentEmailExportAddress = savedSettings.getString(
                    getString(R.string.email_export_address_pref_key), getString(R.string.email_export_address_text_default));
            FragmentManager fm = getSupportFragmentManager();
            DialogFragmentEmailExportAddressEdit dialogFragmentEmailExportAddressEdit =
                    DialogFragmentEmailExportAddressEdit.newInstance(getString(R.string.email_export_address_label), currentEmailExportAddress);
            dialogFragmentEmailExportAddressEdit.show(fm, "dialog_fragment_email_export_address_edit");
        }
    };
    private OnClickListener welcomeTextEditButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "welcomeTextEditButtonListener::onClick");
            }
            String currentWelcomeText = savedSettings.getString(
                    getString(R.string.custom_welcome_text_pref_key), getString(R.string.welcome_text_default));
            FragmentManager fm = getSupportFragmentManager();
            DialogFragmentWelcomeTextEdit dialogFragmentWelcomeTextEdit =
                    DialogFragmentWelcomeTextEdit.newInstance(getString(R.string.custom_welcome_text_label), currentWelcomeText);
            dialogFragmentWelcomeTextEdit.show(fm, "dialog_fragment_welcome_text_edit");
        }
    };

    private OnClickListener downloadXMLFileButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "downloadXMLFileButtonListener::onClick");
            }
            //launch XML Download :
            String xmlFileAddress = savedSettings.getString(
                    getString(R.string.xml_file_address_pref_key), getString(R.string.xml_file_address_text_default));
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "launchXMLDownload::xmlFileAddress = " + xmlFileAddress);
            }
            URL xmlFileURL;
            //checking if internet connection is available
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                try {
                    xmlFileURL = new URL(xmlFileAddress);
                    AsyncSaveRemoteXMLtoLocalFile getRemoteXmlFile = new AsyncSaveRemoteXMLtoLocalFile(SettingsActivity.this);
                    getRemoteXmlFile.execute(xmlFileURL);
                }
                catch (Exception e) {
                    SimpleDialogs.displayErrorAlertDialog(SettingsActivity.this, getString(R.string.error_bad_url));
                    e.printStackTrace();
                }
            } else {
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "launchXMLDownload:: no internet connection");
                }
                SimpleDialogs.displayErrorAlertDialog(SettingsActivity.this, getString(R.string.error_no_internet_access));
            }


        }
    };
    public void onSaveRemoteXMLtoLocalFileComplete(String aDate){
        //AsyncSaveRemoteXMLtoLocalFile notifies us that xml download is complete, along with the resulting dl date
        if(aDate!=null && !aDate.isEmpty()) {
            updateLastDlXML(new Long(aDate));
            parseLocalXML();
        } else{
            //do nothing, we have already notify the user of the error
        }
    }

    private OnClickListener downloadDatasButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "downloadDatasButtonListener::onClick");
            }
            if(parseLocalXML()){
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "downloadDatasButtonListener::onClick : content of shop : \n" + beteHumaineDatas.toString());
                }
                //checking if internet connection is available
                ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    //reset existing files
                    eraseExistingPictures();
                    //download pictures and store them
                    downloadPictures();
                } else{
                    if (BuildConfig.DEBUG) {
                        Log.i(TAG, "downloadDatasButtonListener:: no internet connection");
                    }
                    SimpleDialogs.displayErrorAlertDialog(SettingsActivity.this, getString(R.string.error_no_internet_access));
                }
            } else{
                SimpleDialogs.displayErrorAlertDialog(SettingsActivity.this, getString(R.string.error_xml_file_parsing));
            }
        }
    };

    private void downloadPictures(){
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "downloadPictures");
        }
        //download pictures and create folders to store them
        if(!beteHumaineDatas.hasDatasReady()){
            parseLocalXML();
        }
        ArrayList<ArtistDatas> shop = beteHumaineDatas.getShop();

        shopIterator = shop.iterator();
        downloadNextArtistPictures();
    }
    private boolean parseLocalXML(){
        LocalXMLParser localXMLParser = new LocalXMLParser();
        return localXMLParser.parseXMLdatas(this);
    }
    public void onDownloadImagesError(String errorURL){
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "onDownloadImagesError " + errorURL );
        }
        currentlyLoadingCategory = "";
        currentArtistLoading = null;
        shopIterator = null;
        SimpleDialogs.displayErrorAlertDialog(SettingsActivity.this, getString(R.string.pictures_loading_error_message) +
                                        "\n\n\t " + errorURL);
    }
    public void onSaveImagesError(String fileName, String errorMessage){
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "onSaveImagesError");
        }
        currentlyLoadingCategory = "";
        currentArtistLoading = null;
        shopIterator = null;
        SimpleDialogs.displayErrorAlertDialog(SettingsActivity.this, getString(R.string.pictures_saving_error_message) +
                "\n\n\t " + fileName + "\n\n " + errorMessage);
    }
    public void onDownloadImagesComplete(){
        downloadNextArtistPictures();
    }
    private void downloadNextArtistPictures(){
        //loading order is FACE -> TATTOOS -> DRAWINGS (current state stored in currentlyLoadingCategory)
        DownloadImages downloader = new DownloadImages(this);
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "downloadNextArtistPictures :: currentlyLoadingCategory = " + currentlyLoadingCategory);
        }
        if(currentlyLoadingCategory.equals(FACE)){//we have just loaded a face picture => launching tattoos folder loading
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "downloadNextArtistPictures :: same ARTIST..tattoos");
            }
            currentlyLoadingCategory = TATTOOS;
            downloader.launchDownload(
                    currentArtistLoading.getTattoosURL(),
                    getString(R.string.pictures_loading_title, getString(R.string.tattoos), currentArtistLoading.getName()),
                    currentArtistLoading.getTattoosLocalFolderPath());
        }else if(currentlyLoadingCategory.equals(TATTOOS)){//we have just loaded a tattoos folder => launching drawings folder loading
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "downloadNextArtistPictures :: same ARTIST..drawings");
            }
            currentlyLoadingCategory = DRAWINGS;
            downloader.launchDownload(
                    currentArtistLoading.getDrawingsURL(),
                    getString(R.string.pictures_loading_title, getString(R.string.drawings), currentArtistLoading.getName()),
                    currentArtistLoading.getDrawingsLocalFolderPath());
        }else { //we have just loaded a drawings folder, OR it's the first ARTIST => launching tattoos folder loading
            if (shopIterator.hasNext()){//if we have one more artist
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "downloadNextArtistPictures :: NEXT ARTIST..profile picture");
                }
                currentlyLoadingCategory = FACE;
                //get next artist
                currentArtistLoading = shopIterator.next();
                ArrayList<String> tempArrayFacePicture = new ArrayList<>();
                tempArrayFacePicture.add(currentArtistLoading.getPictureDistantURL());
                downloader.launchDownload(
                        tempArrayFacePicture,
                        getString(R.string.profile_picture_loading_title, currentArtistLoading.getName()),
                        currentArtistLoading.getArtistLocalRootFolderName());
            } else{
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "downloadNextArtistPictures :: QUEUE END");
                }
                currentlyLoadingCategory = "";
                currentArtistLoading = null;
                shopIterator = null;
                SimpleDialogs.displayConfirmAlertDialog(this, getString(R.string.pictures_loaded_message));
                updateLastDldatas(System.currentTimeMillis());
            }
        }

    }

    private void eraseExistingPictures(){ //will recursively erase all datas stored in /data/data/fr.shining_cat.labetehumaine/files/bete_humaine_pics/
        String pathToFolder = this.getFilesDir() + File.separator + BeteHumaineDatas.PICTURES_LOCAL_ROOT_FOLDER;
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "eraseExistingPictures trying : " + pathToFolder);
        }
        File localPicsToDelete = new File(pathToFolder);
        deleteRecursive(localPicsToDelete);
    }

    private void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }
        fileOrDirectory.delete();
    }

    private OnClickListener waitingDelayEditButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "waitingDelayEditButtonListener::onClick");
            }
            int currentWaitingDelay = savedSettings.getInt(getString(R.string.waiting_delay_pref_key), MainActivity.INITIAL_WAITING_DELAY);
            FragmentManager fm = getSupportFragmentManager();
            DialogFragmentWaitingDelayEdit dialogFragmentWaitingDelayEdit =
                    DialogFragmentWaitingDelayEdit.newInstance(getString(R.string.waiting_delay_title), currentWaitingDelay);
            dialogFragmentWaitingDelayEdit.show(fm, "dialog_fragment_waiting_delay_edit");
        }
    };
    private OnClickListener idleFormDelayEditButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "idleFormDelayEditButtonListener::onClick");
            }
            int currentIdleFormDelay = savedSettings.getInt(getString(R.string.idle_delay_on_client_form_pref_key), FormActivity.INITIAL_IDLE_DELAY);
            FragmentManager fm = getSupportFragmentManager();
            DialogFragmentIdleFormDelayEdit dialogFragmentIdleFormDelayEdit =
                    DialogFragmentIdleFormDelayEdit.newInstance(getString(R.string.form_idle_delay_title), currentIdleFormDelay);
            dialogFragmentIdleFormDelayEdit.show(fm, "dialog_fragment_waiting_delay_edit");
        }
    };

    private OnClickListener numberOfColumnsEditButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "numberOfColumnsEditButtonListener::onClick");
            }
            int currentNumberOfColumns = savedSettings.getInt(getString(R.string.number_of_columns_pref_key), FragmentArtistGallery.DEFAULT_NUM_OF_COLUMNS);
            FragmentManager fm = getSupportFragmentManager();
            DialogFragmentNumberOfColumnsEdit dialogFragmentNumberOfColumnsEdit =
                    DialogFragmentNumberOfColumnsEdit.newInstance(getString(R.string.number_of_column_title), currentNumberOfColumns);
            dialogFragmentNumberOfColumnsEdit.show(fm, "dialog_fragment_number_of_columns_edit");
        }
    };

    public void updateXMLFileAddress(String newXMLFileAddress) {
        // DialogFragmentXMLFileAddressEdit notifies us that a new value has been entered for waiting delay
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "updateXMLFileAddress::newXMLFileAddress = " + newXMLFileAddress);
        }
        TextView xmlFileAddressText = (TextView) findViewById(R.id.xml_file_address_editText);
        if(!newXMLFileAddress.equals("")) {//it's an edit
            //save new value to sharedPreferences
            SharedPreferences.Editor editor = savedSettings.edit();
            editor.putString(getString(R.string.xml_file_address_pref_key), newXMLFileAddress);
            editor.apply();
            //update displayed value
            xmlFileAddressText.setText(newXMLFileAddress);
            //show confirm message
            SimpleDialogs.displayValueSetConfirmToast(this);
        } else {//it's a first time setting
            xmlFileAddressText.setText(savedSettings.getString(
                    getString(R.string.xml_file_address_pref_key), getString(R.string.xml_file_address_text_default)));
        }
    }
    public void updateEmailExportAddress(String newEmailExportAddress) {
        // DialogFragmentXMLFileAddressEdit notifies us that a new value has been entered for waiting delay
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "updateEmailExportAddress::newEmailExportAddress = " + newEmailExportAddress);
        }
        TextView emailExportAddressText = (TextView) findViewById(R.id.email_export_address_editText);
        if(!newEmailExportAddress.equals("")) {//it's an edit
            //save new value to sharedPreferences
            SharedPreferences.Editor editor = savedSettings.edit();
            editor.putString(getString(R.string.email_export_address_pref_key), newEmailExportAddress);
            editor.apply();
            //update displayed value
            emailExportAddressText.setText(newEmailExportAddress);
            //show confirm message
            SimpleDialogs.displayValueSetConfirmToast(this);
        } else {//it's a first time setting
            emailExportAddressText.setText(savedSettings.getString(
                    getString(R.string.email_export_address_pref_key), getString(R.string.email_export_address_text_default)));
        }
    }
    private void updateLastDldatas(Long mDate){
        TextView lastDownloadDatasText = (TextView) findViewById(R.id.last_datas_download_text);
        if(mDate != null) {//it's an edit
            SharedPreferences.Editor editor = savedSettings.edit();
            editor.putLong(getString(R.string.datas_last_download_pref_key), mDate);
            editor.apply();
            //update displayed value
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy 'à' HH:mm:ss");
            String dateString = formatter.format(new Date(mDate));
            lastDownloadDatasText.setText(getString(R.string.datas_last_download_text, dateString));
        } else{//it's a first time setting
            Long lastDlXML = savedSettings.getLong(getString(R.string.datas_last_download_pref_key), 0);
            if(lastDlXML != 0){
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy 'à' HH:mm:ss");
                String dateString = formatter.format(new Date(lastDlXML));
                lastDownloadDatasText.setText(getString(R.string.datas_last_download_text, dateString));
            } else { // aucune date enregistrée : les données n'ont jamais été dl
                lastDownloadDatasText.setText(getString(R.string.datas_last_download_text_default));
            }
        }
    }
    private void updateLastExportDB(long mDate){ //
        //mdate as System.currentTimeMillis();
        TextView lastExportDBtoCSVText = (TextView) findViewById(R.id.clients_db_last_export_text);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy 'à' HH:mm:ss");
        SQLiteDBHelper dbHelper = new SQLiteDBHelper(this);
        String numberOfRecordsTotal = Integer.toString(dbHelper.getTotalNumberOfRecords());
        long lastDBExport = savedSettings.getLong(getString(R.string.last_db_export_to_csv_pref_key), 0);
        String numberOfRecordsSinceLastExport = Integer.toString(dbHelper.getNumberOfRecordsSinceDate(lastDBExport));
        if(mDate != -1) {//it's an edit, we just exported the DB, so we update the lastExportPref value and adjust the displayed value
            SharedPreferences.Editor editor = savedSettings.edit();
            editor.putLong(getString(R.string.last_db_export_to_csv_pref_key), mDate);
            editor.apply();
            //update displayed value
            lastDBExport = savedSettings.getLong(getString(R.string.last_db_export_to_csv_pref_key), 0);
            numberOfRecordsSinceLastExport = Integer.toString(dbHelper.getNumberOfRecordsSinceDate(lastDBExport));
            String dateString = formatter.format(new Date(mDate));
            lastExportDBtoCSVText.setText(getString(R.string.clients_db_last_export_text, dateString, numberOfRecordsSinceLastExport));
        } else{
            //long lastDlXML = savedSettings.getLong(getString(R.string.last_db_export_to_csv_pref_key), 0);
            if(lastDBExport != 0){//The DB has been exported previously, just display the correct date
                String dateString = formatter.format(new Date(lastDBExport));
                lastExportDBtoCSVText.setText(getString(R.string.clients_db_last_export_text, dateString, numberOfRecordsSinceLastExport));
            } else { // the DB has never been exported
                lastExportDBtoCSVText.setText(getString(R.string.clients_db_last_export_text_default, numberOfRecordsTotal));
            }
        }
    }
    private void updateDeleteDB(){ //
        SQLiteDBHelper dbHelper = new SQLiteDBHelper(this);
        int totalNumberOfRecords = dbHelper.getTotalNumberOfRecords();
        long lastDBExport = savedSettings.getLong(getString(R.string.last_db_export_to_csv_pref_key), 0);
        int numberOfRecordsSinceLastExport = dbHelper.getNumberOfRecordsSinceDate(lastDBExport);
        Button deleteDBButton = (Button) findViewById(R.id.erase_clients_db_button);
        TextView deleteDBText = (TextView) findViewById(R.id.erase_clients_db_text);
        if(totalNumberOfRecords > 0){
            if(numberOfRecordsSinceLastExport == 0) {//la base contient des fiches, aucune n'a été ajoutée depuis le dernier export => on peut effacer
                deleteDBButton.setVisibility(View.VISIBLE);
                deleteDBText.setText(getString(R.string.clients_db_erase_text_default, Integer.toString(totalNumberOfRecords)));
            }else{//la base contient des fiches, mais certaines ont été ajoutées après le dernier export => interdit d'effacer
                deleteDBButton.setVisibility(View.INVISIBLE);
                deleteDBText.setText(getString(R.string.clients_db_erase_forbidden_text, Integer.toString(numberOfRecordsSinceLastExport), Integer.toString(totalNumberOfRecords)));
            }
        } else {//la base ne contient aucune fiche => rien à effacer
            deleteDBButton.setVisibility(View.INVISIBLE);
            deleteDBText.setText(getString(R.string.clients_db_nothing_to_erase_text));
        }
    }
    private void updateLastDlXML(long mDate){
        TextView lastDownloadXMLFileText = (TextView) findViewById(R.id.last_xml_download_text);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy 'à' HH:mm:ss");
        if(mDate != -1) {//it's an edit
            SharedPreferences.Editor editor = savedSettings.edit();
            editor.putLong(getString(R.string.xml_file_last_download_pref_key), mDate);
            editor.apply();
            //update displayed value
            String dateString = formatter.format(new Date(mDate));
            lastDownloadXMLFileText.setText(getString(R.string.xml_file_last_download_text, dateString));
        } else{//it's a first time setting
            Long lastDlXML = savedSettings.getLong(getString(R.string.xml_file_last_download_pref_key), 0);
            if(lastDlXML != 0){
                String dateString = formatter.format(new Date(lastDlXML));
                lastDownloadXMLFileText.setText(getString(R.string.xml_file_last_download_text, dateString));
            } else { // aucune date enregistrée : le fichier n'a jamais été dl
                lastDownloadXMLFileText.setText(getString(R.string.xml_file_last_download_text_default));
            }
        }
    }
    public void updateWaitingDelay(int newWaitingDelay) {
        // DialogFragmentWaitingDelayEdit notifies us that a new value has been entered for waiting delay
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "updateWaitingDelay::newWaitingDelay = " + newWaitingDelay);
        }
        TextView waitingDelayText = (TextView) findViewById(R.id.waiting_screen_delay_textfield);
        if(newWaitingDelay != -1) {//it's an edit
            //save new value to sharedPreferences
            SharedPreferences.Editor editor = savedSettings.edit();
            editor.putInt(getString(R.string.waiting_delay_pref_key), newWaitingDelay);
            editor.apply();
            //update displayed value
            waitingDelayText.setText(getString(R.string.waiting_delay_value, newWaitingDelay));
            //show confirm message
            SimpleDialogs.displayValueSetConfirmToast(this);
        } else {//it's a first time setting
            int delayValue = savedSettings.getInt(getString(R.string.waiting_delay_pref_key), MainActivity.INITIAL_WAITING_DELAY);
            waitingDelayText.setText(getString(R.string.waiting_delay_value, delayValue));
        }
    }
    public void updateIdleFormDelay(int newIdleFormDelay) {
        // DialogFragmentIdleFormEdit notifies us that a new value has been entered for idleForm delay
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "updateIdleFormDelay::newIdleFormDelay = " + newIdleFormDelay);
        }
        TextView idleFormDelayText = (TextView) findViewById(R.id.form_idle_delay_textfield);
        if(newIdleFormDelay != -1) {//it's an edit
            //save new value to sharedPreferences
            SharedPreferences.Editor editor = savedSettings.edit();
            editor.putInt(getString(R.string.idle_delay_on_client_form_pref_key), newIdleFormDelay);
            editor.apply();
            //update displayed value
            idleFormDelayText.setText(getString(R.string.form_idle_delay_value, newIdleFormDelay));
            //show confirm message
            SimpleDialogs.displayValueSetConfirmToast(this);
        } else {//it's a first time setting
            int delayValue = savedSettings.getInt(getString(R.string.idle_delay_on_client_form_pref_key), FormActivity.INITIAL_IDLE_DELAY);
            idleFormDelayText.setText(getString(R.string.form_idle_delay_value, delayValue));
        }
    }
    public void updateNumberOfColumns(int newNumberOfColumns) {
        // DialogFragmentNumberOfColumnsEdit notifies us that a new value has been entered
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "updateNumberOfColumns::newNumberOfColumns = " + newNumberOfColumns);
        }
        TextView numberOfColumnsText = (TextView) findViewById(R.id.number_of_columns_value);
        if(newNumberOfColumns != -1) {//it's an edit
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "newNumberOfColumns != -1 ");
            }
            //save new value to sharedPreferences
            SharedPreferences.Editor editor = savedSettings.edit();
            editor.putInt(getString(R.string.number_of_columns_pref_key), newNumberOfColumns);
            editor.apply();
            //update displayed value
            numberOfColumnsText.setText(getString(R.string.number_of_column_value, newNumberOfColumns));
            //show confirm message
            SimpleDialogs.displayValueSetConfirmToast(this);
        } else {//it's a first time setting
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "first time setting " + getString(R.string.number_of_columns_pref_key) + " - " + FragmentArtistGallery.DEFAULT_NUM_OF_COLUMNS);
            }
            int numbOfCol = savedSettings.getInt(getString(R.string.number_of_columns_pref_key), FragmentArtistGallery.DEFAULT_NUM_OF_COLUMNS);
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "numbOfCol = " + numbOfCol);
            }
            numberOfColumnsText.setText(getString(R.string.number_of_column_value, numbOfCol));
        }
    }
    private OnClickListener restScreenClicButtonlistener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "restScreenClicButtonlistener::onClick");
            }
            ToggleButton restScreenToggle = (ToggleButton) v;
            SharedPreferences.Editor editor = savedSettings.edit();
            if (restScreenToggle.isChecked()) {
                editor.putString(getString(R.string.resting_screen_pref_key), MainActivity.REST_TO_WAITING_SCREEN);
            } else {
                editor.putString(getString(R.string.resting_screen_pref_key), MainActivity.REST_TO_GALLERY_SCREEN);
            }
            editor.apply();
        }
    };
    private OnClickListener displayWelcomeTextOnGalleryClicButtonlistener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "displayWelcomeTextOnGalleryClicButtonlistener::onClick");
            }
            ToggleButton displayWelcomeTextOnGalleryToggle = (ToggleButton) v;
            SharedPreferences.Editor editor = savedSettings.edit();
            if (displayWelcomeTextOnGalleryToggle.isChecked()) {
                editor.putBoolean(getString(R.string.show_welcome_text_on_gallery_pref_key), true);
            } else {
                editor.putBoolean(getString(R.string.show_welcome_text_on_gallery_pref_key), false);
            }
            editor.apply();
        }
    };
    private OnClickListener forceFitArtistsCardsOnGalleryScreenClicButtonlistener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "forceFitArtistsCardsOnGalleryScreenClicButtonlistener::onClick");
            }
            ToggleButton forceFitArtistsCardsOnGalleryScreenToggle = (ToggleButton) v;
            SharedPreferences.Editor editor = savedSettings.edit();
            if (forceFitArtistsCardsOnGalleryScreenToggle.isChecked()) {
                editor.putBoolean(getString(R.string.force_fit_artists_cards_to_gallery_screen_pref_key), true);
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "forceFitArtistsCardsOnGalleryScreenClicButtonlistener::onClick__ON");
                }
            } else {
                editor.putBoolean(getString(R.string.force_fit_artists_cards_to_gallery_screen_pref_key), false);
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "forceFitArtistsCardsOnGalleryScreenClicButtonlistener::onClick__OFF");
                }
            }
            editor.apply();
        }
    };
    private OnClickListener showClientFormAccessButtonClicButtonlistener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "showClientFormAccessButtonClicButtonlistener::onClick");
            }
            ToggleButton showClientFormAccessButtonToggle = (ToggleButton) v;
            SharedPreferences.Editor editor = savedSettings.edit();
            if (showClientFormAccessButtonToggle.isChecked()) {
                editor.putBoolean(getString(R.string.show_client_form_access_button_pref_key), true);
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "showClientFormAccessButtonClicButtonlistener::onClick__ON");
                }
            } else {
                editor.putBoolean(getString(R.string.show_client_form_access_button_pref_key), false);
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "showClientFormAccessButtonClicButtonlistener::onClick__OFF");
                }
            }
            editor.apply();
        }
    };

    public void updateWelcomeText(String newWelcomeText) {
        // DialogFragmentXMLFileAddressEdit notifies us that a new value has been entered for waiting delay
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "updateWelcomeText::newWelcomeText = " + newWelcomeText);
        }
        TextView welcomeTextTextfield = (TextView) findViewById(R.id.custom_welcome_text_editText);
        if(!newWelcomeText.equals("")) {//it's an edit
            //save new value to sharedPreferences
            SharedPreferences.Editor editor = savedSettings.edit();
            editor.putString(getString(R.string.custom_welcome_text_pref_key), newWelcomeText);
            editor.apply();
            //update displayed value
            welcomeTextTextfield.setText(newWelcomeText);
            //show confirm message
            SimpleDialogs.displayValueSetConfirmToast(this);
        } else {//it's a first time setting
            welcomeTextTextfield.setText(savedSettings.getString(
                    getString(R.string.custom_welcome_text_pref_key), getString(R.string.welcome_text_default)));
        }
    }

    private OnClickListener adminCodeClicButtonlistener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "adminCodeClicButtonlistener::onClick");
            }
            ToggleButton adminToggle = (ToggleButton) v;
            if (adminToggle.isChecked()) {
                //was inactive => ask for new password
                FragmentManager fm = getSupportFragmentManager();
                DialogFragmentNewPassword dialogFragmentNewPassword =
                        DialogFragmentNewPassword.newInstance(getString(R.string.new_password_step1_title), "");
                dialogFragmentNewPassword.show(fm, "dialog_fragment_new_password");
            } else {
                //was active => ask for password before continuing to password inactivation
                String correctPass = savedSettings.getString(getString(R.string.admin_code_pref_key), null);
                FragmentManager fm = getSupportFragmentManager();
                DialogFragmentAdminCodeRequest dialogFragmentAdminCodeRequest =
                        DialogFragmentAdminCodeRequest.newInstance(getString(R.string.admin_code_request_label), correctPass);
                dialogFragmentAdminCodeRequest.show(fm, "dialog_fragment_admin_code_request");
            }
        }
    };

    public void onNewPasswordDismiss() {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "onNewPasswordDismiss");
        }
        ToggleButton adminCodeToggleButton = (ToggleButton) findViewById(R.id.admin_code_toggle_button);
        adminCodeToggleButton.setChecked(false);
    }

    public void onNewPasswordFirstEntry(String password) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "onNewPasswordFirstEntry::password = " + password);
        }
        FragmentManager fm = getSupportFragmentManager();
        DialogFragmentNewPassword dialogFragmentNewPassword =
                DialogFragmentNewPassword.newInstance(getString(R.string.new_password_step2_title), password);
        dialogFragmentNewPassword.show(fm, "dialog_fragment_new_password");
    }


    public void onNewPasswordSecondEntryCorrect(String password) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "onNewPasswordSecondEntryCorrect::password = " + password);
        }
        SharedPreferences.Editor editor = savedSettings.edit();
        editor.putString(getString(R.string.current_mode_pref_key), MainActivity.KIOSK_MODE);
        editor.putString(getString(R.string.admin_code_pref_key), password);
        editor.apply();
        ToggleButton adminCodeToggleButton = (ToggleButton) findViewById(R.id.admin_code_toggle_button);
        adminCodeToggleButton.setChecked(true);
        //show confirm message
        SimpleDialogs.displayValueSetConfirmToast(this);
    }


    public void onPasswordDismiss() {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "onPasswordDismiss");
        }
        ToggleButton adminCodeToggleButton = (ToggleButton) findViewById(R.id.admin_code_toggle_button);
        adminCodeToggleButton.setChecked(true);
    }


    public void onPasswordCorrect() {
        // DialogFragmentAdminCodeRequest notifies us that the user has entered a correct password => proceed to change mode to standard
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "onPasswordCorrect");
        }
        SharedPreferences.Editor editor = savedSettings.edit();
        editor.putString(getString(R.string.current_mode_pref_key), MainActivity.STANDARD_MODE);
        editor.putString(getString(R.string.admin_code_pref_key), null);
        editor.apply();
        ToggleButton adminCodeToggleButton = (ToggleButton) findViewById(R.id.admin_code_toggle_button);
        adminCodeToggleButton.setChecked(false);
        //show confirm message
        SimpleDialogs.displayValueSetConfirmToast(this);
    }
}