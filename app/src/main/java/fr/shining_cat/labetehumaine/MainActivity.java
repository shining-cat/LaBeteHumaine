package fr.shining_cat.labetehumaine;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import fr.shining_cat.labetehumaine.tools.BeteHumaineDatas;
import fr.shining_cat.labetehumaine.tools.LocalXMLParser;
import fr.shining_cat.labetehumaine.tools.ScreenSize;
import fr.shining_cat.labetehumaine.tools.SimpleDialogs;

public class MainActivity extends AppCompatActivity
                    implements FragmentManager.OnBackStackChangedListener,
                                DialogFragmentAdminCodeRequest.OnAdminCodeRequestListener,
                                FragmentWaitingScreen.WaitingScreenListener,
                                FragmentSelectArtist.FragmentSelectArtistListener,
                                FragmentArtistCard.FragmentArtistCardListener,
                                FragmentFullscreenImage.FragmentFullscreenImageListener,
                                FragmentArtistGallery.ArtistGalleryListener{


    private final String TAG = "LOGGING::" + this.getClass().getSimpleName();


    private final int INITIAL_HIDE_NORMAL_DELAY = 1500;
    private final int INITIAL_HIDE_SHORT_DELAY = 1000;
    private final int ACTIONBAR_Y_SHOWN = 0;
    private final int ACTIONBAR_Y_HIDDEN = -10;
    private final String WELCOME_TEXT_COMPACT = "welcome text compact";
    private final String WELCOME_TEXT_LARGE = "welcome text large et centré";
    protected static final int INITIAL_WAITING_DELAY = 20;

    public static final String SETTINGS_FILE_NAME = "settings_bete_humaine";
    public static final String KIOSK_MODE = "app is in kiosk mode";
    public static final String STANDARD_MODE = "app is in standard mode";
    public static final String REST_TO_WAITING_SCREEN = "app will rest on waiting screen";
    public static final String REST_TO_GALLERY_SCREEN = "app will rest on chose artist screen";


    private View decorView;
    private View toolbarHolder;
    private Toolbar toolbar;
    private SharedPreferences savedSettings;
    private Fragment currentShownFragment;

    private TextView welcomeTextView;
    private Animation animBlink;

    private int artistCurrentlyDisplayed;
    private String whatIsCurrentlyDisplayed;
    private int idleDelay;
    private String welcomeText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "onCreate");
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbarHolder = findViewById(R.id.toolbarHolder);
        toolbar.setOnClickListener(onActionBarTitleClicked);
        //
        setBackgroundDecor();
        //get SharedPreferences object
        savedSettings = getSharedPreferences(SETTINGS_FILE_NAME, MODE_PRIVATE);
        welcomeTextView = (TextView) findViewById(R.id.gallery_welcome_text_textView);
        welcomeText = savedSettings.getString(
                getString(R.string.custom_welcome_text_pref_key), getString(R.string.welcome_text_default));
        welcomeTextView.setText(welcomeText);

        idleDelay = savedSettings.getInt(getString(R.string.waiting_delay_pref_key), INITIAL_WAITING_DELAY);
        Long lastDlXML = savedSettings.getLong(getString(R.string.xml_file_last_download_pref_key), 0);
        Long lastDldatas = savedSettings.getLong(getString(R.string.datas_last_download_pref_key), 0);
        BeteHumaineDatas beteHumaineDatas = BeteHumaineDatas.getInstance();
        if(lastDlXML==0 || lastDldatas==0){
            SimpleDialogs.displayErrorAlertDialog(this, getString(R.string.error_never_downloaded));
        } else if(lastDlXML > lastDldatas) {
            SimpleDialogs.displayErrorAlertDialog(this, getString(R.string.error_pictures_older_than_xml));
        } else if(!beteHumaineDatas.hasDatasReady()){
            //grab local datas if existant, the parser will store them into BeteHumaineDatas
            parseLocalXML();
        }
        //setting needed decorView for fullscreen behavior
        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(onSystemUiVisibilityChangeListener);
        //Listen for changes in the back stack
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        //Handle when activity is recreated like on orientation Change
        shouldDisplayHomeUp();
    }
    private boolean parseLocalXML(){
        LocalXMLParser localXMLParser = new LocalXMLParser();
        return localXMLParser.parseXMLdatas(this);
    }
    private View.OnClickListener onActionBarTitleClicked = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "onActionBarTitleClicked");
            }
            getSupportFragmentManager().popBackStack();
        }
    };

    private void setBackgroundDecor() {
        //get screen size to adjust layout :
        Point realScreenSize = ScreenSize.getRealScreenSize(this);
        int screenWidth = realScreenSize.x;
        int screenHeight = realScreenSize.y;
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "setBackgroundDecor::SCREEN SIZE : width = " + screenWidth + " X height = " + screenHeight);
        }
        //get reference to display elements
        ImageView backgroundImageView = (ImageView) findViewById(R.id.main_background_imageView);
        ImageView logoImageView = (ImageView) findViewById(R.id.main_logo_imageView);
        backgroundImageView.getLayoutParams().width = screenWidth;
        Picasso.
                with(this)
                .load("file:///android_asset/background.jpg")
                .fit()
                .centerCrop()
                .into(backgroundImageView);
        //
        logoImageView.getLayoutParams().width = screenWidth / 2;
        Picasso.
                with(this)
                .load("file:///android_asset/logo.png")
                .into(logoImageView);

    }

    @Override
    protected void onStart(){
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "onStart");
        }
        super.onStart();
        goToRestingScreen();
        hideSystemUI();
    }

    @Override
    public void onBackStackChanged() {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "onBackStackChanged");
        }
        shouldDisplayHomeUp();
    }

    public void shouldDisplayHomeUp(){
        //Enable Up button only  if there are entries in the back stack
        boolean canBack = getSupportFragmentManager().getBackStackEntryCount()>0;
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "shouldDisplayHomeUp::canBack = " + canBack);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(canBack);
    }
    public void shouldDisplayLogo(Boolean shouldI){ //called by the fragments
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "shouldDisplayLogo::shouldI = " + shouldI);
        }
        if (shouldI) {
            showLogo();
        } else {
            hideLogo();
        }
    }
    private void showLogo(){
        ImageView logoImageView = (ImageView) findViewById(R.id.main_logo_imageView);
        logoImageView.setVisibility(View.VISIBLE);
    }
    private void hideLogo(){
        ImageView logoImageView = (ImageView) findViewById(R.id.main_logo_imageView);
        logoImageView.setVisibility(View.GONE);
    }
    public void updateActionBarTitle(String title){
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "updateActionBarTitle::title = " + title);
        }
        getSupportActionBar().setTitle(title);
    }
    @Override
    public boolean onSupportNavigateUp() {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "onSupportNavigateUp");
        }
        //This method is called when the up button is pressed. Just the pop back stack.
        getSupportFragmentManager().popBackStack();
        return true;
    }
    private void showWaitingScreen(){
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "showWaitingScreen");
            Log.i(TAG, "handleMessage::currentShownFragment = " + currentShownFragment);
        }
        //this is the basic/root display screen
        //this screen will just show a background image and a welcome text, and will react to a touch event anywhere on the screen
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if(currentShownFragment != null) {
            fragmentTransaction.remove(currentShownFragment);
        }
        fragmentTransaction.commit();
        currentShownFragment = null;
        updateActionBarTitle(getString(R.string.app_name));
        shouldDisplayLogo(true);
        displayWelcomeText(WELCOME_TEXT_LARGE);
        RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.activity_main_layout);
        mainLayout.setOnClickListener(onMainLayoutClicked);
    }
    private View.OnClickListener onMainLayoutClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onWaitingScreenClicked();
        }
    };
    @Override
        public void onWaitingScreenClicked() {
        //TODO : rattacher et détacher le listener pour un fonctionnement sans le fragment waitingscreen
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "onWaitingScreenClicked");
        }
        Long lastDlXML = savedSettings.getLong(getString(R.string.xml_file_last_download_pref_key), 0);
        Long lastDldatas = savedSettings.getLong(getString(R.string.datas_last_download_pref_key), 0);
        if(lastDlXML==0 || lastDldatas==0){
            SimpleDialogs.displayErrorAlertDialog(this, getString(R.string.error_never_downloaded));
        } else if(lastDlXML > lastDldatas) {
            SimpleDialogs.displayErrorAlertDialog(this, getString(R.string.error_pictures_older_than_xml));
        } else{
            showSelectArtistScreen();
        }
    }
    private void displayWelcomeText(String welcomeTextStyle){
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "displayWelcomeText::welcomeTextStyle = " + welcomeTextStyle);
        }
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) welcomeTextView.getLayoutParams();
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        welcomeText = savedSettings.getString(getString(R.string.custom_welcome_text_pref_key), getString(R.string.welcome_text_default));
        switch(welcomeTextStyle){
            case WELCOME_TEXT_COMPACT :
                welcomeTextView.setText(welcomeText.replace("\n", " ").replace("\r", " "));
                layoutParams.addRule(RelativeLayout.BELOW, R.id.main_logo_imageView);
                break;
            case WELCOME_TEXT_LARGE :
                welcomeTextView.setText(welcomeText);
                layoutParams.addRule(RelativeLayout.BELOW);
                break;
        }
        welcomeTextView.setVisibility(View.VISIBLE);
        animBlink = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.pulsing_welcome_text);
        welcomeTextView.startAnimation(animBlink);
    }

    private void hideWelcomeText(){
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "hideWelcomeText");
        }
        if(animBlink!=null){
            welcomeTextView.clearAnimation();
        }
        welcomeTextView.setVisibility(View.GONE);
    }

    private void showSelectArtistScreen(){
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "showSelectArtistScreen");
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        FragmentSelectArtist fragmentSelectArtist = new FragmentSelectArtist();
        //
        if(currentShownFragment != null) {
            fragmentTransaction.replace(R.id.mainFragmentContainer, fragmentSelectArtist);
        } else{
            fragmentTransaction.add(R.id.mainFragmentContainer, fragmentSelectArtist);
        }
        //not adding to backstack this transaction : user shall not force the display of waiting screen
        //fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        Boolean shallShowWelcomeText = savedSettings.getBoolean(getString(R.string.show_welcome_text_on_gallery_pref_key), false);//checking user setting for display welcome text on select artist page
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "showSelectArtistScreen::shallShowWelcomeText = " + shallShowWelcomeText);
        }
        if(shallShowWelcomeText){
            displayWelcomeText(WELCOME_TEXT_COMPACT);
        } else{
            hideWelcomeText();
        }
    }
    @Override
    public void onArtistCardClicked(int artistIndex, String whatWasClicked) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "onArtistCardToGallery::artistIndex = " + artistIndex + " // whatWasClicked = " + whatWasClicked);
        }
        artistCurrentlyDisplayed = artistIndex;
        whatIsCurrentlyDisplayed = whatWasClicked;
        BeteHumaineDatas beteHumaineDatas = BeteHumaineDatas.getInstance();
        if(!beteHumaineDatas.hasDatasReady()){
            parseLocalXML();
        }
        ArrayList<ArtistDatas> shop = beteHumaineDatas.getShop();

        ArtistDatas artistSelected = shop.get(artistIndex);
        if(whatWasClicked.equals(FragmentArtistCard.TATTOOS_WAS_CLICKED)) {
            showGallery(artistSelected.getTattoosLocalFolderPath(), artistSelected.getName());
        } else{
            showGallery(artistSelected.getDrawingsLocalFolderPath(), artistSelected.getName());
        }
        hideWelcomeText();
    }
    private void showGallery(String imageFolder, String artistName){
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "showGallery");
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        FragmentArtistGallery fragmentArtistGallery = FragmentArtistGallery.newInstance(getFilesDir() + File.separator +imageFolder, artistName);
        //
        fragmentTransaction.replace(R.id.mainFragmentContainer, fragmentArtistGallery);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onArtistGalleryClicked(int position) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "onArtistGalleryClicked position = " + position);
        }
        BeteHumaineDatas beteHumaineDatas = BeteHumaineDatas.getInstance();
        if(!beteHumaineDatas.hasDatasReady()){
            parseLocalXML();
        }
        ArrayList<ArtistDatas> shop = beteHumaineDatas.getShop();

        ArtistDatas artistSelected = shop.get(artistCurrentlyDisplayed);
        String imagesFolder;
        String artistName = artistSelected.getName();
        if(whatIsCurrentlyDisplayed.equals(FragmentArtistCard.TATTOOS_WAS_CLICKED)) {
            imagesFolder = artistSelected.getTattoosLocalFolderPath();
        } else{
            imagesFolder = artistSelected.getDrawingsLocalFolderPath();
        }
        showFullScreenImage(position, imagesFolder, artistName);
    }
    private void showFullScreenImage(int position, String imagesFolder, String artistName){
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "onArtistGalleryClicked position = " + position);
        }

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        FragmentFullscreenImage fragmentFullScreenImage = FragmentFullscreenImage.newInstance(getFilesDir() + File.separator + imagesFolder, position, artistName);
        //
        fragmentTransaction.replace(R.id.mainFragmentContainer, fragmentFullScreenImage);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "onCreateOptionsMenu");
        }
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "onOptionsItemSelected, id = " + id);
        }
        if (id == R.id.action_settings) {
            askForAdminCode();
            return true;
        } else if (id == R.id.open_form_button) {
            openForm();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void askForAdminCode() {
        //get current mode, if set, or standard if not set
        String current_mode = savedSettings.getString(getString(R.string.current_mode_pref_key), STANDARD_MODE);
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "askForAdminCode::current_mode = " + current_mode);
        }
        //open a dialog invite to ask for admin code, if one is present in Preferences = if we are in KIOSK_MODE
        if (current_mode.equals(KIOSK_MODE)) {
            String correctPass = savedSettings.getString(getString(R.string.admin_code_pref_key), null);
            FragmentManager fm = getSupportFragmentManager();
            DialogFragmentAdminCodeRequest dialogFragmentAdminCodeRequest =
                    DialogFragmentAdminCodeRequest.newInstance(getString(R.string.admin_code_request_label), correctPass);
            dialogFragmentAdminCodeRequest.show(fm, "dialog_fragment_admin_code_request");
        } else {
            openSettings();
        }
    }
    public void onPasswordDismiss() {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "AdminCode Dialog : dismiss");
        }
    }
    public void onPasswordCorrect() {
        // DialogFragmentAdminCodeRequest notifies us that the user has entered a correct password => proceed to open settings
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "AdminCode Dialog : Password correct!");
        }
        openSettings();
    }
    private void openSettings() {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "openSettings");
        }
        Intent preferencesIntent = new Intent(this, SettingsActivity.class);
        startActivity(preferencesIntent);
    }

    private void openForm(){
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "openForm");
        }
        Intent formIntent = new Intent(this, FormActivity.class);
        startActivity(formIntent);
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "onWindowFocusChanged::hasFocus = " + hasFocus);
        }
        if (hasFocus) {// When the window gains focus, hide the system UI.
                //delayedHideSystemUI(INITIAL_HIDE_NORMAL_DELAY);
            //new behaviour : we don't want the system UI to show at all anymore, only the actionbar
            delayedHideSystemUI(INITIAL_HIDE_SHORT_DELAY);
            showActionBar();
            delayedHideActionBar();
        } else {// When the window loses focus, cancel any pending hide action.
            //mHideSytemUIHandler.removeMessages(0);
            mHideActionBarHandler.removeMessages(0);
        }
    }

    @Override
    public void onUserInteraction(){
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "onUserInteraction");
        }
        //reset idle timer
        mIdleHandler.removeMessages(0);
        mIdleHandler.sendEmptyMessageDelayed(0, idleDelay*1000);
        //briefly show action bar
        showActionBar();
        delayedHideActionBar();
    }
    private void stopIdleTimer(){
        mIdleHandler.removeMessages(0);
    }
    private final Handler mIdleHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "handleMessage::currentShownFragment = " + currentShownFragment);
            }
            if(currentShownFragment!=null) {
                //commented => actually, simply reloading even on select artist fragment is good :
                // scroller is reset, and the welcome text comes back (if set to be displayed)
                //if (!currentShownFragment.getClass().getSimpleName().equals("FragmentSelectArtist")) {
                    goToRestingScreen();
                //}
            }
        }
    };
    private void goToRestingScreen(){
        //Emptying the Fragment Backstack by poping all entries up to and including the first
        FragmentManager manager = getSupportFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
            manager.popBackStackImmediate(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        String rest_screen = savedSettings.getString(getString(R.string.resting_screen_pref_key), MainActivity.REST_TO_WAITING_SCREEN);
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "goToRestingScreen :: " + rest_screen);
        }
        if(rest_screen.equals(REST_TO_WAITING_SCREEN)) {
            showWaitingScreen();
        } else{
            showSelectArtistScreen();
        }
    }

    private void delayedHideSystemUI(int delayMillis) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "delayedHideSystemUI");
        }
        mHideSytemUIHandler.removeMessages(0);
        mHideSytemUIHandler.sendEmptyMessageDelayed(0, delayMillis);
    }
    private final Handler mHideSytemUIHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            hideSystemUI();
        }
    };
    private void hideSystemUI() {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "hideSystemUI");
        }
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        uiOptions |= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        uiOptions |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        uiOptions |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        uiOptions |= View.SYSTEM_UI_FLAG_FULLSCREEN;
        uiOptions |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
        uiOptions |= View.SYSTEM_UI_FLAG_IMMERSIVE;
        decorView.setSystemUiVisibility(uiOptions);
    }

    protected void showSystemUI() {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "showSystemUI");
        }
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        uiOptions |= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        uiOptions |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }
    private void delayedHideActionBar() {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "delayedHideActionBar");
        }
        mHideActionBarHandler.removeMessages(0);
        mHideActionBarHandler.sendEmptyMessageDelayed(0, INITIAL_HIDE_NORMAL_DELAY);
    }
    private final Handler mHideActionBarHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            hideActionBar();
        }
    };
    private void hideActionBar(){
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "hideActionBar");
        }
        toolbarHolder.animate().translationY(ACTIONBAR_Y_HIDDEN).alpha(0);
    }
    private void showActionBar(){
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "showActionBar");
        }
        toolbarHolder.animate().translationY(ACTIONBAR_Y_SHOWN).alpha(1);
    }

    private View.OnSystemUiVisibilityChangeListener onSystemUiVisibilityChangeListener =
            new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                        if (BuildConfig.DEBUG) {
                            Log.i(TAG, "onSystemUiVisibilityChange :: The system bars are visible");
                        }
                        // The system bars are visible
                        //hideSystemUI();
                        delayedHideSystemUI(INITIAL_HIDE_SHORT_DELAY);
                        showActionBar();
                        delayedHideActionBar();
                    } else {
                        if (BuildConfig.DEBUG) {
                            Log.i(TAG, "onSystemUiVisibilityChange :: The system bars are NOT visible");
                        }
                        // The system bars are NOT visible
                    }
                }
            };

    @Override
    public void onBackPressed() {
        //if in kiosk mode : let user go back to waiting screen, but not out of app
        String current_mode = savedSettings.getString(getString(R.string.current_mode_pref_key), STANDARD_MODE);
        if (current_mode.equals(STANDARD_MODE)) {
            super.onBackPressed();
        } else {
            FragmentManager fm = getSupportFragmentManager();
            if(fm.getBackStackEntryCount() != 0){
                fm.popBackStack();
            }
        }
    }
    public void updateCurrentFragmentShownVariable(Fragment fragment){
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "updateCurrentFragmentShownVariable::currentShownFragment = " + fragment.getClass().getSimpleName());
        }
        currentShownFragment = fragment;
    }
    @Override
    public void onPause(){
        super.onPause();
        stopIdleTimer();
    }
    @Override
    public void onResume(){
        super.onResume();
        mIdleHandler.sendEmptyMessageDelayed(0, idleDelay*1000);
    }
}
