package fr.shining_cat.labetehumaine;

import android.util.Log;

/**
 * Created by Shiva on 23/07/2017.
 */

public class ClientDatas {

    private final String TAG = "LOGGING::" + this.getClass().getSimpleName();

    private String registrationDate = "";
    private String selectedArtist = "";
    private String clientName = "";
    private String clientFirstname = "";
    private String clientEmail = "";
    private String clientPhone = "";
    private String clientZipCode = "";
    private String clientBirthdate = "";
    private boolean clientWasMajorAtRegistration = true;
    private String clientIDNumber = "";
    private String parentName = "";
    private String parentFirstname = "";
    private String parentIDNumber = "";

    public ClientDatas(){
        if(MainActivity.DEBUG) {
            Log.i(TAG, "ArtistDatas object created");
        }
    }
    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }
    public void setSelectedArtist(String selectedArtist) {
        this.selectedArtist = selectedArtist;
    }
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
    public void setClientFirstname(String clientFirstname) {
        this.clientFirstname = clientFirstname;
    }
    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }
    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }
    public void setClientZipCode(String clientZipCode) {
        this.clientZipCode = clientZipCode;
    }
    public String getClientBirthdate() {
        return clientBirthdate;
    }
    public void setClientBirthdate(String clientBirthdate) {
        this.clientBirthdate = clientBirthdate;
    }
    public void setClientWasMajorAtRegistration(boolean clientWasMajorAtRegistration) {
        this.clientWasMajorAtRegistration = clientWasMajorAtRegistration;
    }
    public void setClientIDNumber(String clientIDNumber) {
        this.clientIDNumber = clientIDNumber;
    }
    public void setParentName(String parentName) {
        this.parentName = parentName;
    }
    public void setParentFirstname(String parentFirstname) {
        this.parentFirstname = parentFirstname;
    }
    public void setParentIDNumber(String parentIDNumber) {
        this.parentIDNumber = parentIDNumber;
    }
    @Override
    public String toString(){
        String client = "============================\nCLIENT DATA DUMP-TO-STRING :\n";
        client += "\t\tDATE OF REGISTRATION : " + registrationDate + "\n";
        client += "\t\tARTIST : " + selectedArtist + "\n";
        client += "\t\tFIRSTNAME : " + clientFirstname + "\n";
        client += "\t\tEMAIL : " + clientEmail + "\n";
        client += "\t\tPHONE : " + clientPhone + "\n";
        client += "\t\tZIPCODE : " + clientZipCode + "\n";
        client += "\t\tBIRTHDATE : " + clientBirthdate + "\n";
        client += "\t\tISMAJOR : " + clientWasMajorAtRegistration + "\n";
        client += "\t\tID : " + clientIDNumber + "\n";
        client += "\t\tPARENT FIRSTNAME : " + parentFirstname + "\n";
        client += "\t\tPARENT NAME : " + parentName + "\n";
        client += "\t\tPARENT ID : " + parentIDNumber + "\n";
        return client;
    }
    public String getDatasReadyForCSV(){
        String clientDatasFormatted = "";
        clientDatasFormatted += registrationDate + ";";
        clientDatasFormatted += clientName + ";";
        clientDatasFormatted += clientFirstname + ";";
        clientDatasFormatted += clientBirthdate + ";";
        clientDatasFormatted += ";"; //adresse non collectée
        clientDatasFormatted += clientZipCode + ";";
        clientDatasFormatted += ";"; //ville non collectée
        clientDatasFormatted += ";"; //?
        clientDatasFormatted += clientPhone + ";";
        clientDatasFormatted += clientEmail + ";";
        clientDatasFormatted += ";"; //?? "tattoo"??
        clientDatasFormatted += selectedArtist + ";";
        clientDatasFormatted += clientIDNumber + ";";
        clientDatasFormatted += parentFirstname + ";";
        clientDatasFormatted += parentName + ";";
        clientDatasFormatted += parentIDNumber + ";";
        return clientDatasFormatted;
    }
}