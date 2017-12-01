package fr.shining_cat.labetehumaine;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Shiva on 23/07/2017.
 */

public class ClientDatas {

    private final String TAG = "LOGGING::" + this.getClass().getSimpleName();

    private String MAJEUR = "MAJEUR";
    private String MINEUR = "MINEUR";
    private String dateFormatStringForCSV = "dd/MM/yyyy";

    private String columnHeaderRegistrationDate = "Date";
    private String columnHeaderClientName = "Nom de famile";
    private String columnHeaderClientFirstname = "Prénom";
    private String columnHeaderClientBirthdate = "Date de naissance";
    private String columnHeaderClientAddress = "Adresse";
    private String columnHeaderClientZipcode = "Code postal";
    private String columnHeaderClientCity = "Ville";
    private String columnHeaderUnknownColumnFromSpecs = "Colonne vide";
    private String columnHeaderClientPhone = "Téléphone";
    private String columnHeaderClientEmail = "E-mail";
    private String columnHeaderClientPrestation = "Prestation";
    private String columnHeaderSelectedArtist = "Artiste";
    private String columnHeaderClientWasMajor = "Majeur/Mineur";
    private String columnHeaderClientIdNumber = "N° de carte d'identité du client mineur";
    private String columnHeaderParentName = "Nom du parent du client mineur";
    private String columnHeaderParentFirstname = "Prénom du parent du client mineur";
    private String columnHeaderParentIdNumber = "N° de carte d'identité du parent du client mineur";

    private long registrationDate;
    private String selectedArtist = "";
    private String clientName = "";
    private String clientFirstname = "";
    private String clientEmail = "";
    private String clientPrestation;
    private String clientPhone = "";
    private String clientAddress = "";
    private String clientZipCode = "";
    private String clientCity = "";
    private String clientBirthdate = "";
    private boolean clientWasMajorAtRegistration = true;
    private String clientIDNumber = "";
    private String parentName = "";
    private String parentFirstname = "";
    private String parentIDNumber = "";
    private String unknownInfoFromSpecs = "";

    public ClientDatas(){
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "CONSTRUCTOR");
        }
    }

    public long getRegistrationDate() {return registrationDate;}
    public String getRegistrationDateFormattedString(SimpleDateFormat formatter) {
        String formattedRegistrationDate = formatter.format(new Date(registrationDate));
        return formattedRegistrationDate;
    }
    public void setRegistrationDate(long registrationDate) {this.registrationDate = registrationDate;}

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

    public String getClientCity() {return clientCity;}
    public void setClientCity(String clientCity) {this.clientCity = clientCity;}

    public String getClientBirthdate() {return clientBirthdate;}
    public void setClientBirthdate(String clientBirthdate) {this.clientBirthdate = clientBirthdate;}

    public boolean getClientWasMajorAtRegistration() {return clientWasMajorAtRegistration;}
    public void setClientWasMajorAtRegistration(boolean clientWasMajorAtRegistration) {this.clientWasMajorAtRegistration = clientWasMajorAtRegistration;}
    public String getClientWasMajorAtRegistrationToString() {return (clientWasMajorAtRegistration) ? MAJEUR : MINEUR;}
    public void setClientWasMajorAtRegistrationFromString(String majeurMineur){
        this.clientWasMajorAtRegistration = (majeurMineur.equals(MAJEUR)) ? true : false;
    }

    public String getClientIDNumber() {return clientIDNumber;}
    public void setClientIDNumber(String clientIDNumber) {this.clientIDNumber = clientIDNumber;}

    public String getParentName() {return parentName;}
    public void setParentName(String parentName) {this.parentName = parentName;}

    public String getParentFirstname() {return parentFirstname;}
    public void setParentFirstname(String parentFirstname) {this.parentFirstname = parentFirstname;}

    public String getParentIDNumber() {return parentIDNumber;}
    public void setParentIDNumber(String parentIDNumber) {this.parentIDNumber = parentIDNumber;}

    public String getClientPrestation() {return clientPrestation;}
    public void setClientPrestation(String clientPrestation) {this.clientPrestation = clientPrestation;}

    public String getUnknownInfoInSpecs() {return unknownInfoFromSpecs;}
    public void setUnknownInfoFromSpecs(String unknownInfoFromSpecs) {this.unknownInfoFromSpecs = unknownInfoFromSpecs;}

    public String getColumnHeadersCSVstring(){
        String columnHeaders = "";
        columnHeaders += "\"" + columnHeaderRegistrationDate + "\";";
        columnHeaders += "\"" + columnHeaderClientName + "\";";
        columnHeaders += "\"" + columnHeaderClientFirstname + "\";";
        columnHeaders += "\"" + columnHeaderClientBirthdate + "\";";
        columnHeaders += "\"" + columnHeaderClientAddress + "\";";
        columnHeaders += "\"" + columnHeaderClientZipcode + "\";";
        columnHeaders += "\"" + columnHeaderClientCity + "\";";
        columnHeaders += "\"" + columnHeaderUnknownColumnFromSpecs + "\";";
        columnHeaders += "\"" + columnHeaderClientPhone + "\";";
        columnHeaders += "\"" + columnHeaderClientEmail + "\";";
        columnHeaders += "\"" + columnHeaderClientPrestation + "\";";
        columnHeaders += "\"" + columnHeaderSelectedArtist + "\";";
        columnHeaders += "\"" + columnHeaderClientWasMajor + "\";";
        columnHeaders += "\"" + columnHeaderClientIdNumber + "\";";
        columnHeaders += "\"" + columnHeaderParentName + "\";";
        columnHeaders += "\"" + columnHeaderParentFirstname + "\";";
        columnHeaders += "\"" + columnHeaderParentIdNumber + "\";";
        return columnHeaders;
    }

    public String toCSVString(){
        //This is actually the only place where the order of the items is important to comply to specifications
        //here we compose a string containing a CSV formatted line
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormatStringForCSV);
        //
        String client = "";
        client += "\"" + getRegistrationDateFormattedString(sdf) + "\";";
        client += "\"" + clientName + "\";";
        client += "\"" + clientFirstname + "\";";
        client += "\"" + clientBirthdate + "\";";
        client += "\"" + clientAddress + "\";";
        client += "\"" + clientZipCode + "\";";
        client += "\"" + clientCity + "\";";
        client += "\"" + unknownInfoFromSpecs + "\";";
        client += "\"" + clientPhone + "\";";
        client += "\"" + clientEmail + "\";";
        client += "\"" + clientPrestation + "\";";
        client += "\"" + selectedArtist + "\";";
        client += "\"" + getClientWasMajorAtRegistrationToString() + "\";";
        client += "\"" + clientIDNumber + "\";";
        client += "\"" + parentName + "\";";
        client += "\"" + parentFirstname + "\";";
        client += "\"" + parentIDNumber + "\";";
        return client;
    }

    @Override
    public String toString(){
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormatStringForCSV);
        String client = "============================\nCLIENT DATA DUMP-TO-STRING :\n";
        client += "\t\tDATE OF REGISTRATION : " + getRegistrationDateFormattedString(sdf) + "\n";
        client += "\t\tARTIST : "               + selectedArtist + "\n";
        client += "\t\tFIRSTNAME : "            + clientFirstname + "\n";
        client += "\t\tEMAIL : "                + clientEmail + "\n";
        client += "\t\tPHONE : "                + clientPhone + "\n";
        client += "\t\tADDRESS : "              + clientAddress + "\n";
        client += "\t\tZIPCODE : "              + clientZipCode + "\n";
        client += "\t\tBIRTHDATE : "            + clientBirthdate + "\n";
        client += "\t\tISMAJOR : "              + getClientWasMajorAtRegistrationToString() + "\n";
        client += "\t\tID : "                   + clientIDNumber + "\n";
        client += "\t\tPARENT FIRSTNAME : "     + parentFirstname + "\n";
        client += "\t\tPARENT NAME : "          + parentName + "\n";
        client += "\t\tPARENT ID : "            + parentIDNumber + "\n";
        return client;
    }
}