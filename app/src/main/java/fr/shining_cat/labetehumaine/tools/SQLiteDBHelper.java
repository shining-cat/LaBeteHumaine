package fr.shining_cat.labetehumaine.tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import fr.shining_cat.labetehumaine.BuildConfig;
import fr.shining_cat.labetehumaine.ClientDatas;

/**
 * Created by Shiva on 12/09/2017.
 */

public class SQLiteDBHelper extends SQLiteOpenHelper {

    private final String TAG = "LOGGING::" + this.getClass().getSimpleName();

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ClientsDB";

    public static final String TABLE_NAME = "fiches_clients";
    public static final String KEY_CLIENT_ID = "client_id";
    public static final String KEY_REGISTRATION_DATE = "date_inscription";
    public static final String KEY_ARTIST = "artiste";
    public static final String KEY_CLIENT_NAME = "client_nom_de_famille";
    public static final String KEY_CLIENT_FIRSTNAME = "client_prenom";
    public static final String KEY_CLIENT_EMAIL = "client_email";
    public static final String KEY_CLIENT_PRESTATION = "prestation";
    public static final String KEY_CLIENT_PHONE = "client_telephone";
    public static final String KEY_CLIENT_ADDRESS = "client_adresse";
    public static final String KEY_CLIENT_ZIPCODE = "client_code_postal";
    public static final String KEY_CLIENT_CITY = "client_ville";
    public static final String KEY_CLIENT_BIRTHDAY = "client_date_naissance";
    public static final String KEY_CLIENT_WAS_MAJOR_AT_REGISTRATION = "client_majeur";
    public static final String KEY_CLIENT_ID_NUMBER = "client_mineur_numero_carte_identite";
    public static final String KEY_PARENT_NAME = "client_mineur_parent_nom_de_famille";
    public static final String KEY_PARENT_FIRSTNAME = "client_mineur_parent_prenom";
    public static final String KEY_PARENT_ID_NUMBER = "client_mineur_parent_numero_carte_identite";
    public static final String KEY_UNNKNOWN_EMPTY_COLUMN_IN_SPECS = "colonne_vide_csv_modele";


    //Create Table Query
    //column order has been established in conformity with given specs (csv and xls template with no column names)
    private static final String SQL_CREATE_FICHES_CLIENTS =
            "CREATE TABLE " + TABLE_NAME                            + " ("
                            + KEY_CLIENT_ID                         + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                            + KEY_REGISTRATION_DATE                 + " INTEGER, "
                            + KEY_CLIENT_NAME                       + " TEXT, "
                            + KEY_CLIENT_FIRSTNAME                  + " TEXT, "
                            + KEY_CLIENT_BIRTHDAY                   + " TEXT, "
                            + KEY_CLIENT_ADDRESS                    + " TEXT, "
                            + KEY_CLIENT_ZIPCODE                    + " TEXT, "
                            + KEY_CLIENT_CITY                       + " TEXT, "
                            + KEY_UNNKNOWN_EMPTY_COLUMN_IN_SPECS    + " TEXT, "
                            + KEY_CLIENT_PHONE                      + " TEXT, "
                            + KEY_CLIENT_EMAIL                      + " TEXT, "
                            + KEY_CLIENT_PRESTATION                 + " TEXT, "
                            + KEY_ARTIST                            + " TEXT, "
                            + KEY_CLIENT_WAS_MAJOR_AT_REGISTRATION  + " TEXT, "
                            + KEY_CLIENT_ID_NUMBER                  + " TEXT, "
                            + KEY_PARENT_NAME                       + " TEXT, "
                            + KEY_PARENT_FIRSTNAME                  + " TEXT, "
                            + KEY_PARENT_ID_NUMBER                  + " TEXT);";

    //Select all records query
    private static final String SQL_SELECT_ALL_RECORDS = "SELECT * FROM " + TABLE_NAME + ";";

    //Delete table query
    private static final String SQL_DELETE_CLIENTS = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public SQLiteDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "onCreate \n QUERY = " + SQL_CREATE_FICHES_CLIENTS);
        }
        db.execSQL(SQL_CREATE_FICHES_CLIENTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Drop the table while upgrading the database
        // such as adding new column or changing existing constraint
        /*TODO : implementer un message d'avertissement demandant de faire un export avant, car perte des données enregistrées */
        db.execSQL(SQL_DELETE_CLIENTS);
        this.onCreate(db);
    }

    //delete whole Clients table
    public boolean deleteClientsDB(){
        SQLiteDatabase db = this.getWritableDatabase();
        Boolean success;
        try{
            db.execSQL(SQL_DELETE_CLIENTS);
            //re-create table here, otherwise next call will throw "table does not exist" because oncreate is not called if DB exist and we don't delete the DB but only drop the table
            onCreate(db);
            success = true;
        } catch(SQLException e){
            success = false;
        }
        db.close();
        return success;
    }
    //add a new record
    public long addClientRecord(ClientDatas clientDatas){
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();
        //Create a map having details to be inserted
        ContentValues client_infos = new ContentValues();
        client_infos.put(KEY_REGISTRATION_DATE, clientDatas.getRegistrationDate());
        client_infos.put(KEY_CLIENT_NAME, clientDatas.getClientName());
        client_infos.put(KEY_CLIENT_FIRSTNAME, clientDatas.getClientFirstname());
        client_infos.put(KEY_CLIENT_BIRTHDAY, clientDatas.getClientBirthdate());
        client_infos.put(KEY_CLIENT_ADDRESS, clientDatas.getClientAddress());
        client_infos.put(KEY_CLIENT_ZIPCODE, clientDatas.getClientZipCode());
        client_infos.put(KEY_CLIENT_CITY, clientDatas.getClientCity());
        client_infos.put(KEY_UNNKNOWN_EMPTY_COLUMN_IN_SPECS, clientDatas.getUnknownInfoInSpecs());
        client_infos.put(KEY_CLIENT_PHONE, clientDatas.getClientPhone());
        client_infos.put(KEY_CLIENT_EMAIL, clientDatas.getClientEmail());
        client_infos.put(KEY_CLIENT_PRESTATION, clientDatas.getClientPrestation());
        client_infos.put(KEY_ARTIST, clientDatas.getSelectedArtist());
        client_infos.put(KEY_CLIENT_WAS_MAJOR_AT_REGISTRATION, clientDatas.getClientWasMajorAtRegistrationToString());
        client_infos.put(KEY_CLIENT_ID_NUMBER, clientDatas.getClientIDNumber());
        client_infos.put(KEY_PARENT_NAME, clientDatas.getParentName());
        client_infos.put(KEY_PARENT_FIRSTNAME, clientDatas.getParentFirstname());
        client_infos.put(KEY_PARENT_ID_NUMBER, clientDatas.getParentIDNumber());
        //sql query
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "addClientRecord datas = \n" + client_infos.toString());
        }
        long newRowId = db.insert(TABLE_NAME, null, client_infos);
        db.close();
        return newRowId; //returns -1 on error
    }
    //get number of records
    public int getTotalNumberOfRecords(){
        SQLiteDatabase db = this.getReadableDatabase();
        int total = (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME);
        db.close();
        return total;
    }

    //get number of records after specific date
    public int getNumberOfRecordsSinceDate(long sinceDate){ //milliseconds since the epoch
        SQLiteDatabase db = this.getReadableDatabase();
        int total = (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME, KEY_REGISTRATION_DATE + " > ? ", new String[] {String.valueOf(sinceDate)});
        db.close();
        return total;
    }
    //get all records
    public List<ClientDatas> getAllClientsRecords(){
        List<ClientDatas> allClientsRecordsList= new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(SQL_SELECT_ALL_RECORDS, null);
        //if TABLE has rows
        if (cursor.moveToFirst()) {
            //Loop through the table rows
            do {
                ClientDatas clientRecord = new ClientDatas();
                clientRecord.setRegistrationDate(cursor.getLong(cursor.getColumnIndex(KEY_REGISTRATION_DATE)));
                clientRecord.setSelectedArtist(cursor.getString(cursor.getColumnIndex(KEY_ARTIST)));
                clientRecord.setClientFirstname(cursor.getString(cursor.getColumnIndex(KEY_CLIENT_FIRSTNAME)));
                clientRecord.setClientName(cursor.getString(cursor.getColumnIndex(KEY_CLIENT_NAME)));
                clientRecord.setClientBirthdate(cursor.getString(cursor.getColumnIndex(KEY_CLIENT_BIRTHDAY)));
                clientRecord.setClientEmail(cursor.getString(cursor.getColumnIndex(KEY_CLIENT_EMAIL)));
                clientRecord.setClientPhone(cursor.getString(cursor.getColumnIndex(KEY_CLIENT_PHONE)));
                clientRecord.setClientAddress(cursor.getString(cursor.getColumnIndex(KEY_CLIENT_ADDRESS)));
                clientRecord.setClientZipCode(cursor.getString(cursor.getColumnIndex(KEY_CLIENT_ZIPCODE)));
                clientRecord.setClientCity(cursor.getString(cursor.getColumnIndex(KEY_CLIENT_CITY)));
                clientRecord.setClientPrestation(cursor.getString(cursor.getColumnIndex(KEY_CLIENT_PRESTATION)));
                clientRecord.setClientWasMajorAtRegistrationFromString(cursor.getString(cursor.getColumnIndex(KEY_CLIENT_WAS_MAJOR_AT_REGISTRATION)));
                clientRecord.setClientIDNumber(cursor.getString(cursor.getColumnIndex(KEY_CLIENT_ID_NUMBER)));
                clientRecord.setParentFirstname(cursor.getString(cursor.getColumnIndex(KEY_PARENT_FIRSTNAME)));
                clientRecord.setParentName(cursor.getString(cursor.getColumnIndex(KEY_PARENT_NAME)));
                clientRecord.setParentIDNumber(cursor.getString(cursor.getColumnIndex(KEY_PARENT_ID_NUMBER)));
                clientRecord.setUnknownInfoFromSpecs(cursor.getString(cursor.getColumnIndex(KEY_UNNKNOWN_EMPTY_COLUMN_IN_SPECS)));
                allClientsRecordsList.add(clientRecord);
            } while (cursor.moveToNext());
        }
        db.close();
        return allClientsRecordsList;
    }

}
