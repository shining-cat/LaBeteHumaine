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
    private String clientAddress = "";
    private String clientZipCode = "";
    private String clientBirthdate = "";
    private boolean clientWasMajorAtRegistration = true;
    private String clientIDNumber = "";
    private String parentName = "";
    private String parentFirstname = "";
    private String parentIDNumber = "";

    public ClientDatas(){
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "ArtistDatas object created");
        }
    }

    public String getRegistrationDate() {return registrationDate;}
    public void setRegistrationDate(String registrationDate) {this.registrationDate = registrationDate;}

    public String getSelectedArtist() {return selectedArtist;}
    public void setSelectedArtist(String selectedArtist) {this.selectedArtist = selectedArtist;}

    public String getClientName() {return clientName;}
    public void setClientName(String clientName) {this.clientName = clientName;}

    public String getClientFirstname() {return clientFirstname;}
    public void setClientFirstname(String clientFirstname) {this.clientFirstname = clientFirstname;}

    public String getClientEmail() {return clientEmail;}
    public void setClientEmail(String clientEmail) {this.clientEmail = clientEmail;}

    public String getClientPhone() {return clientPhone;}
    public void setClientPhone(String clientPhone) {this.clientPhone = clientPhone;}

    public String getClientAddress() {return clientAddress;}
    public void setClientAddress(String clientAddress) {this.clientAddress = clientAddress;}

    public String getClientZipCode() {return clientZipCode;}
    public void setClientZipCode(String clientZipCode) {this.clientZipCode = clientZipCode;}

    public String getClientBirthdate() {return clientBirthdate;}
    public void setClientBirthdate(String clientBirthdate) {this.clientBirthdate = clientBirthdate;}

    public boolean getClientWasMajorAtRegistration() {return clientWasMajorAtRegistration;}
    public void setClientWasMajorAtRegistration(boolean clientWasMajorAtRegistration) {this.clientWasMajorAtRegistration = clientWasMajorAtRegistration;}

    public String getClientIDNumber() {return clientIDNumber;}
    public void setClientIDNumber(String clientIDNumber) {this.clientIDNumber = clientIDNumber;}

    public String getParentName() {return parentName;}
    public void setParentName(String parentName) {this.parentName = parentName;}

    public String getParentFirstname() {return parentFirstname;}
    public void setParentFirstname(String parentFirstname) {this.parentFirstname = parentFirstname;}

    public String getParentIDNumber() {return parentIDNumber;}
    public void setParentIDNumber(String parentIDNumber) {this.parentIDNumber = parentIDNumber;}



    @Override
    public String toString(){
        String client = "============================\nCLIENT DATA DUMP-TO-STRING :\n";
        client += "\t\tDATE OF REGISTRATION : " + registrationDate + "\n";
        client += "\t\tARTIST : "               + selectedArtist + "\n";
        client += "\t\tFIRSTNAME : "            + clientFirstname + "\n";
        client += "\t\tEMAIL : "                + clientEmail + "\n";
        client += "\t\tPHONE : "                + clientPhone + "\n";
        client += "\t\tADDRESS : "              + clientAddress + "\n";
        client += "\t\tZIPCODE : "              + clientZipCode + "\n";
        client += "\t\tBIRTHDATE : "            + clientBirthdate + "\n";
        client += "\t\tISMAJOR : "              + clientWasMajorAtRegistration + "\n";
        client += "\t\tID : "                   + clientIDNumber + "\n";
        client += "\t\tPARENT FIRSTNAME : "     + parentFirstname + "\n";
        client += "\t\tPARENT NAME : "          + parentName + "\n";
        client += "\t\tPARENT ID : "            + parentIDNumber + "\n";
        return client;
    }
    public String getDatasReadyForCSV(){
        /*TODO: escape commas in fields to prevent csv misformatting
         */
        String clientDatasFormatted = "";
        clientDatasFormatted += registrationDate + ";";
        clientDatasFormatted += clientName + ";";
        clientDatasFormatted += clientFirstname + ";";
        clientDatasFormatted += clientBirthdate + ";";
        clientDatasFormatted += clientAddress + ";";
        clientDatasFormatted += clientZipCode + ";";
        clientDatasFormatted += ";"; // ville non collectée
        clientDatasFormatted += ";"; // colonne vide dans le csv modèle
        clientDatasFormatted += clientPhone + ";";
        clientDatasFormatted += clientEmail + ";";
        clientDatasFormatted += "TATTOO;"; //  = type de prestation, a priori on aura tjs tattoo ici
        clientDatasFormatted += selectedArtist + ";";
        clientDatasFormatted += clientIDNumber + ";";
        clientDatasFormatted += parentFirstname + ";";
        clientDatasFormatted += parentName + ";";
        clientDatasFormatted += parentIDNumber + ";";
        return clientDatasFormatted;
    }

    /*TODO : public function to save current client datas to database
    TODO: public static function to export database content to external storage as csv. Returns path
    TODO: add option in settings to erase database content once file export has been confirmed
    TODO: need to make ClientDatas a singleton?
     */
}