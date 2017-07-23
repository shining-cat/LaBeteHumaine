package fr.shining_cat.labetehumaine;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;

import fr.shining_cat.labetehumaine.tools.BeteHumaineDatas;

/**
 * Created by Shiva on 17/06/2016.
 */
public class ArtistDatas {

    private final String TAG = "LOGGING::" + this.getClass().getSimpleName();

    private String name = "Meh. I have no name";
    private String pictureDistantURL = "Meh. I have no pictureDistantURL";
    private String description = "Meh. I have no description";
    private ArrayList<String> tattoosURL;
    private ArrayList<String> drawingsURL;
    private String artistLocalRootFolderName;
    private String tattoosLocalFolderPath;
    private String drawingsLocalFolderPath;



    public ArtistDatas(){
        if(MainActivity.DEBUG) {
            Log.i(TAG, "ArtistDatas object created");
        }
    }

    public void setName(String name) {
        this.name = name;
        /*TODO : add capability to change this string by capitalizing every firdst letter of every name,
        to prevent having smth like pierre-gilles Romieu
         */
        artistLocalRootFolderName = BeteHumaineDatas.PICTURES_LOCAL_ROOT_FOLDER + File.separator + name.replaceAll("[^A-Za-z0-9]", "");
        tattoosLocalFolderPath = artistLocalRootFolderName + File.separator + "tattoos";
        drawingsLocalFolderPath = artistLocalRootFolderName + File.separator + "drawings";
    }
    public String getName() {
        return name;
    }
    public void setPictureDistantURL(String pictureDistantURL) {
        this.pictureDistantURL = pictureDistantURL;
    }
    public String getPictureDistantURL() {
        return pictureDistantURL;
    }
    public String getPictureLocalName() {
        return pictureDistantURL.substring(pictureDistantURL.lastIndexOf('/')+1);
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }
    public void setTattoosURL(ArrayList<String> tattoosURL) {
        this.tattoosURL = tattoosURL;
    }
    public ArrayList<String> getTattoosURL() {
        return tattoosURL;
    }
    public void setDrawingsURL(ArrayList<String> drawingsURL) {
        this.drawingsURL = drawingsURL;
    }
    public ArrayList<String> getDrawingsURL() {
        return drawingsURL;
    }
    public String getArtistLocalRootFolderName() {
        return artistLocalRootFolderName;
    }
    public String getTattoosLocalFolderPath() {
        return tattoosLocalFolderPath;
    }
    public String getDrawingsLocalFolderPath() {
        return drawingsLocalFolderPath;
    }
    public int getNumberOfTattoos(){
        return tattoosURL.size();
    }
    public int getNumberOfDrawings(){
        return drawingsURL.size();
    }
    public void setArtistLocalRootFolderName(String artistLocalRootFolderName){

    }

    @Override
    public String toString(){
        String artistDescription = "============================\nARTIST DUMP-TO-STRING :\n";
        artistDescription += "\t\tNAME : " + name + "\n";
        artistDescription += "\t\tPICTURE : " + pictureDistantURL + "\n";
        artistDescription += "\t\tDESCRIPTION : " + description + "\n";
        artistDescription += "\t\tNombre de tattoos : " + tattoosURL.size() + "\n";
        artistDescription += "\t\tNombre de dessins : " + drawingsURL.size() + "\n====================";
        artistDescription += "\t\tDossier racine : " + artistLocalRootFolderName + "\n====================";
        artistDescription += "\t\tDossier de tattoos : " + tattoosLocalFolderPath + "\n====================";
        artistDescription += "\t\tDossier de dessins : " + drawingsLocalFolderPath + "\n====================";
        return artistDescription;
    }
}

