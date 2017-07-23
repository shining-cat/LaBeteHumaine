package fr.shining_cat.labetehumaine;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import fr.shining_cat.labetehumaine.tools.BeteHumaineDatas;
import fr.shining_cat.labetehumaine.tools.SimpleDialogs;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;


public class FormActivity extends AppCompatActivity {

    private final String TAG = "LOGGING::" + this.getClass().getSimpleName();

    private static int LEGAL_AGE_OF_CONSENT = 18;

    private Calendar clientBirthDate;
    private Calendar todayCalendar;
    private boolean clientIsMajor = true;
    private boolean clientIsMinorAndHasFilledHisPart = false;
    private String selectedArtist;
    private String clientName;
    private String clientFirstname;
    private String clientEmail;
    private String clientPhone;
    private String clientZipCode;
    private String clientBirthdate;
    private String clientIDNumber;
    private String parentName;
    private String parentFirstname;
    private String parentIDNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        if(MainActivity.DEBUG) {
            Log.i(TAG, "onCreate");
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SimpleDialogs.displayParamConfirmAlertDialog(
                FormActivity.this, getString(R.string.please_notify_title),
                getString(R.string.please_notify_list),
                getString(R.string.understood_button_label));
        clientIsMajor = true;
        clientIsMinorAndHasFilledHisPart = false;
        clientBirthDate = Calendar.getInstance();
        todayCalendar = Calendar.getInstance();
        todayCalendar.setTime(new Date());
        populateRadioGroupFormWhichArtist();
        EditText clientBirthDateEditText = (EditText) findViewById(R.id.editTextFormClientBirthDate);
        clientBirthDateEditText.setOnClickListener(clientBirthDateEditTextClickListener);
        clientBirthDateEditText.setOnFocusChangeListener(clientBirthDateEditTextOnFocusListener);
        TextView linkLegalMentions = (TextView)  findViewById(R.id.linkLegalMentions);
        linkLegalMentions.setOnClickListener(linkLegalMentionsClickListener);
        Button validationButton = (Button) findViewById(R.id.formValidateButton);
        validationButton.setOnClickListener(validationButtonClickListener);
        /*TODO:
        -ne pas oublier de stocker la date
        -bouton pour lire le texte officiel dans une popup
        -envoi email confirmation ok - gestion erreurs
        -écriture des données dans le fichier - gestion erreurs
        -popup de confirmation
         */
    }

    private void populateRadioGroupFormWhichArtist(){
        RadioGroup radioGroupFormWhichArtist = (RadioGroup) findViewById(R.id.radioGroupFormWhichArtist);
        BeteHumaineDatas beteHumaineDatas = BeteHumaineDatas.getInstance(this);
        int numberOfArtists = beteHumaineDatas.getShop().size();
        RadioGroup.LayoutParams buttonParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT, 1);
        for (int artistIndex = 0; artistIndex < numberOfArtists; artistIndex++) {
            ArtistDatas artistDatas = beteHumaineDatas.getShop().get(artistIndex);
            String artistName = artistDatas.getName();
            RadioButton artistButton = new RadioButton(this);
            //artistButton.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS); //=>does not work, try to format the name when the data is downloaded
            artistButton.setText(artistName);
            artistButton.setLayoutParams(buttonParams);
            radioGroupFormWhichArtist.addView(artistButton);
        }
    }
    private OnClickListener clientBirthDateEditTextClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (MainActivity.DEBUG) {
                Log.i(TAG, "clientBirthDateEditTextClickListener::onClick");
            }
            showDatePicker();
        }
    };
    private OnFocusChangeListener clientBirthDateEditTextOnFocusListener = new OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(hasFocus) {
                if (MainActivity.DEBUG) {
                    Log.i(TAG, "clientBirthDateEditTextClickListener::onFocus");
                }
                showDatePicker();
            }
        }
    };
    private void showDatePicker(){
        DatePickerDialog dialog = new DatePickerDialog(this,
                onDateSetListener,
                clientBirthDate.get(YEAR),
                clientBirthDate.get(Calendar.MONTH),
                clientBirthDate.get(Calendar.DAY_OF_MONTH));
        dialog.setTitle(getString(R.string.formClientBirthDate));
        dialog.getDatePicker().setCalendarViewShown(false); //hide calendar from dialog, the spinner are added via style xml, only solution working
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis()-1000);
        dialog.setCancelable(false);
        dialog.show();
    }
    DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            clientBirthDate.set(YEAR, year);
            clientBirthDate.set(MONTH, monthOfYear);
            clientBirthDate.set(DAY_OF_MONTH, dayOfMonth);
            clientBirthDateEditTextUpdate();
        }
    };

    private void clientBirthDateEditTextUpdate(){
        //here we set a human friendly format to display birth date in textedit
        String myFormat = "dd MMMM yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRANCE);
        EditText clientBirthDateEditText = (EditText) findViewById(R.id.editTextFormClientBirthDate);
        clientBirthDateEditText.setText(sdf.format(clientBirthDate.getTime()));
        checkClientMajority();
    }
    private void checkClientMajority(){
        int diff = todayCalendar.get(YEAR) - clientBirthDate.get(YEAR);
        if (todayCalendar.get(MONTH) < clientBirthDate.get(MONTH) ||
                (todayCalendar.get(MONTH) == clientBirthDate.get(MONTH) && todayCalendar.get(DAY_OF_MONTH) < clientBirthDate.get(DAY_OF_MONTH))) {
            diff--;
        }
        if(MainActivity.DEBUG) {
            Log.i(TAG, "checkClientMajority::age = " + diff);
        }
        clientIsMajor =!(diff < LEGAL_AGE_OF_CONSENT);
        clientIsMinorAndHasFilledHisPart = false;
        updateFormForLegalAge();
    }
    private void updateFormForLegalAge() {
        if (MainActivity.DEBUG) {
            Log.i(TAG, "updateFormForLegalAge");
        }
        RelativeLayout layoutFormClientIDNumberLine = (RelativeLayout) findViewById(R.id.layoutFormClientIDNumberLine);
        if (clientIsMajor) {
            layoutFormClientIDNumberLine.setVisibility(View.GONE);
            changeVisibilityOfParentFields(View.GONE);

        } else {
            layoutFormClientIDNumberLine.setVisibility(View.VISIBLE);
        }
    }
    private OnClickListener validationButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(MainActivity.DEBUG) {
                Log.i(TAG, "validationButtonClickListener::onClick::clientIsMajor = " + clientIsMajor + " ; clientIsMinorAndHasFilledHisPart = " + clientIsMinorAndHasFilledHisPart);
            }
            if(clientIsMajor){
                if(checkFormIsCorrectlyFilled()){
                    if(checkEmailFieldConformity()) {
                        SimpleDialogs.displayParamConfirmAlertDialog(
                                FormActivity.this, getString(R.string.prevalidation_warning_title),
                                getString(R.string.prevalidation_warning_text_major),
                                getString(R.string.understood_button_label));
                        registerClientDatas(); /*todo : only register datas on confirm clicked */
                    } else{
                        SimpleDialogs.displayErrorAlertDialog(FormActivity.this, getString(R.string.prevalidation_warning_email_error));
                    }
                } else{
                    SimpleDialogs.displayErrorAlertDialog(FormActivity.this, getString(R.string.prevalidation_warning_text_error));
                }
            } else if(clientIsMinorAndHasFilledHisPart){
                if(checkFormIsCorrectlyFilled()){ //we re-check the whole form in case a field has been changed
                    if(checkEmailFieldConformity()) {
                        //compose the custom message with datas insertion
                        SimpleDialogs.displayParamConfirmAlertDialog(
                                FormActivity.this, getString(R.string.prevalidation_warning_title),
                                getString(R.string.prevalidation_warning_text_underage_tutor, parentFirstname, parentName, clientFirstname, clientName),
                                getString(R.string.understood_button_label));
                        registerClientDatas(); /*todo : only register datas on confirm clicked */
                    } else{
                        SimpleDialogs.displayErrorAlertDialog(FormActivity.this, getString(R.string.prevalidation_warning_email_error));
                    }
                } else{
                    SimpleDialogs.displayErrorAlertDialog(FormActivity.this, getString(R.string.prevalidation_warning_text_error));
                }
            } else{
                if(checkFormIsCorrectlyFilled()){
                    if(checkEmailFieldConformity()) {
                        SimpleDialogs.displayParamConfirmAlertDialog(
                                FormActivity.this, getString(R.string.prevalidation_warning_title),
                                getString(R.string.prevalidation_warning_text_underage),
                                getString(R.string.understood_button_label));
                        clientIsMinorAndHasFilledHisPart = true;
                        changeVisibilityOfParentFields(View.VISIBLE);
                    } else{
                        SimpleDialogs.displayErrorAlertDialog(FormActivity.this, getString(R.string.prevalidation_warning_email_error));
                    }
                } else{
                    SimpleDialogs.displayErrorAlertDialog(FormActivity.this, getString(R.string.prevalidation_warning_text_error));
                }
            }
        }
    };
    private void changeVisibilityOfParentFields(int visibilityAttribute){

        RelativeLayout layoutFormParentFirstnameLine = (RelativeLayout) findViewById(R.id.layoutFormParentFirstnameLine);
        RelativeLayout layoutFormParentNameLine = (RelativeLayout) findViewById(R.id.layoutFormParentNameLine);
        RelativeLayout layoutFormParentIDNumberLine = (RelativeLayout) findViewById(R.id.layoutFormParentIDNumberLine);
        layoutFormParentFirstnameLine.setVisibility(visibilityAttribute);
        layoutFormParentNameLine.setVisibility(visibilityAttribute);
        layoutFormParentIDNumberLine.setVisibility(visibilityAttribute);
    }
    private boolean checkFormIsCorrectlyFilled(){
        if (MainActivity.DEBUG) {
            Log.i(TAG, "checkFormIsCorrectlyFilled");
        }
        boolean isOK = true;
        //grab artist choice
        RadioGroup radioGroupFormWhichArtist = (RadioGroup) findViewById(R.id.radioGroupFormWhichArtist);
        int selectedRadio = radioGroupFormWhichArtist.getCheckedRadioButtonId();
        if(selectedRadio != -1){
            RadioButton checkedButton = (RadioButton) findViewById(selectedRadio);
            selectedArtist = checkedButton.getText().toString();
        } else{
            if (MainActivity.DEBUG) {
                Log.i(TAG, "checkFormIsCorrectlyFilled::NO selectedArtist");
            }
            isOK = false;
            return isOK;
        }
        //grab all edittexts contents
        EditText editTextFormClientName = (EditText) findViewById(R.id.editTextFormClientName);
        clientName = editTextFormClientName.getText().toString();
        EditText editTextFormClientFirstname = (EditText) findViewById(R.id.editTextFormClientFirstname);
        clientFirstname = editTextFormClientFirstname.getText().toString();
        EditText editTextFormClientEmail = (EditText) findViewById(R.id.editTextFormClientEmail);
        clientEmail = editTextFormClientEmail.getText().toString();
        EditText editTextFormClientPhone = (EditText) findViewById(R.id.editTextFormClientPhone);
        clientPhone = editTextFormClientPhone.getText().toString();
        EditText editTextFormClientZipcode = (EditText) findViewById(R.id.editTextFormClientZipcode);
        clientZipCode = editTextFormClientZipcode.getText().toString();
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRANCE);
        //here we get the birthdate from the calendar object rather than the textedit, to allow for a more standard format
        clientBirthdate = sdf.format(clientBirthDate.getTime());
        EditText editTextFormClientIDNumber = (EditText) findViewById(R.id.editTextFormClientIDNumber);
        clientIDNumber = editTextFormClientIDNumber.getText().toString();
        EditText editTextFormParentName = (EditText) findViewById(R.id.editTextFormParentName);
        parentName = editTextFormParentName.getText().toString();
        EditText editTextFormParentFirstname = (EditText) findViewById(R.id.editTextFormParentFirstname);
        parentFirstname = editTextFormParentFirstname.getText().toString();
        EditText editTextFormParentIDNumber = (EditText) findViewById(R.id.editTextFormParentIDNumber);
        parentIDNumber = editTextFormParentIDNumber.getText().toString();
        //in every case do :
        if(clientName.equals("") || clientFirstname.equals("") || clientEmail.equals("") || clientPhone.equals("") || clientZipCode.equals("") || clientBirthdate.equals("")){
            if (MainActivity.DEBUG) {
                Log.i(TAG, "checkFormIsCorrectlyFilled::there is one client field empty");
            }
            isOK = false;
            return isOK;
        } //we only check here that email field is non-empty, conformity check will be done later, allowing for a custom error message

        if(!clientIsMajor){
            if (MainActivity.DEBUG) {
                Log.i(TAG, "checkFormIsCorrectlyFilled::!clientIsMajor");
            }
            //add testing for fields specific to underage client
            if(clientIDNumber.equals("")){
                if (MainActivity.DEBUG) {
                    Log.i(TAG, "checkFormIsCorrectlyFilled::clientIDNumber is empty");
                }
                isOK = false;
                return isOK;
            }
            if(clientIsMinorAndHasFilledHisPart){
                if(parentFirstname.equals("") || parentName.equals("") || parentIDNumber.equals("")){
                    if (MainActivity.DEBUG) {
                        Log.i(TAG, "checkFormIsCorrectlyFilled::there is one parent field empty");
                    }
                    isOK = false;
                    return isOK;
                }
            }
        }
        return isOK;
    }
    private boolean checkEmailFieldConformity(){
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(clientEmail).matches()){
            if (MainActivity.DEBUG) {
                Log.i(TAG, "checkEmailFieldConformity::email field is not conform");
            }
            return false;
        }
        return true;
    }
    private OnClickListener linkLegalMentionsClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (MainActivity.DEBUG) {
                Log.i(TAG, "linkLegalMentionsClickListener::onClick");
            }
            SimpleDialogs.displayParamConfirmAlertDialog(
                    FormActivity.this, getString(R.string.legal_mention_title),
                    getString(R.string.legal_mention_text),
                    getString(R.string.understood_button_label));
        }
    };
    private void registerClientDatas(){
        ClientDatas clientDatas = new ClientDatas();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String today = sdf.format(Calendar.getInstance().getTime());
        clientDatas.setRegistrationDate(today);
        clientDatas.setSelectedArtist(selectedArtist);
        clientDatas.setClientFirstname(clientFirstname);
        clientDatas.setClientName(clientName);
        clientDatas.setClientBirthdate(clientBirthdate);
        clientDatas.setClientEmail(clientEmail);
        clientDatas.setClientPhone(clientPhone);
        clientDatas.setClientZipCode(clientZipCode);
        if(!clientIsMajor){
            clientDatas.setClientWasMajorAtRegistration(false);
            clientDatas.setClientIDNumber(clientIDNumber);
            clientDatas.setParentFirstname(parentFirstname);
            clientDatas.setParentName(parentName);
            clientDatas.setParentIDNumber(parentIDNumber);
        }
        if (MainActivity.DEBUG) {
            Log.i(TAG, "registerClientDatas" + clientDatas.toString());
        }
        String ClientDatasFormatted = clientDatas.getDatasReadyForCSV();
        if (MainActivity.DEBUG) {
            Log.i(TAG, "registerClientDatas" + ClientDatasFormatted);
        }
    }
}
