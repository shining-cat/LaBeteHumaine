package fr.shining_cat.labetehumaine.tools;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

import fr.shining_cat.labetehumaine.ArtistDatas;
import fr.shining_cat.labetehumaine.BuildConfig;

/**
 * Created by Shiva on 18/06/2016.
 */
public class BeteHumaineDatas {

    private final String TAG = "LOGGING::" + this.getClass().getSimpleName();

    private static BeteHumaineDatas beteHumaineDatasSingleton = null;

    static final String XML_LOCAL_GENERAL_DATAS_FILE_NAME = "bete_humaine_datas.xml";
    public static final String PICTURES_LOCAL_ROOT_FOLDER = "bete_humaine_pics";

    private boolean haveDatasAvailable = false;
    private ArrayList<ArtistDatas> shop;

    private BeteHumaineDatas(){
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "CONSTRUCTOR - EMPTY");
        }
    }

    public static BeteHumaineDatas getInstance(){
        if(beteHumaineDatasSingleton == null){
            beteHumaineDatasSingleton = new BeteHumaineDatas();
        }
        return beteHumaineDatasSingleton;
    }

    public boolean hasDatasReady(){
        return haveDatasAvailable;
    }
    public ArrayList<ArtistDatas> getShop() {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "getShop::shop = " + shop);
        }
        if(shop == null){
            return null;
        }
        return shop;
    }


    public void storeLoadedXmlValues(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "storeLoadedXmlValues");
        }
        //reset datas :
        haveDatasAvailable = false;
        shop = null;
        //used only for the while loop :
        ArtistDatas currentArtist = null;
        ArrayList<String> currentTattoos = null;
        ArrayList<String> currentDrawings = null;
        //parse xml
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            String name;
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    shop = new ArrayList();
                    break;
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    if (name.equalsIgnoreCase("ARTIST")) {
                        currentArtist = new ArtistDatas();
                    } else if (currentArtist != null) {
                        if (name.equalsIgnoreCase("NAME")) {
                            currentArtist.setName(parser.nextText());
                        } else if (name.equalsIgnoreCase("PICTURE")) {
                            currentArtist.setPictureDistantURL(parser.nextText());
                        } else if (name.equalsIgnoreCase("DESCRIPTION")) {
                            currentArtist.setDescription(parser.nextText());
                        } else if (name.equalsIgnoreCase("TATTOOS")) {
                            currentTattoos = new ArrayList<>();
                        } else if (name.equalsIgnoreCase("TATTOO")) {
                            currentTattoos.add(parser.nextText());
                        } else if (name.equalsIgnoreCase("DRAWINGS")) {
                            currentDrawings = new ArrayList<>();
                        } else if (name.equalsIgnoreCase("DRAWING")) {
                            currentDrawings.add(parser.nextText());
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    name = parser.getName();
                    if (name.equalsIgnoreCase("TATTOOS")&& currentArtist !=null&& currentTattoos !=null) {
                        currentArtist.setTattoosURL(currentTattoos);
                    } else if (name.equalsIgnoreCase("DRAWINGS")&& currentArtist !=null&& currentDrawings !=null) {
                        currentArtist.setDrawingsURL(currentDrawings);
                    } else if (name.equalsIgnoreCase("ARTIST")&& currentArtist !=null) {
                        shop.add(currentArtist);
                    }
                    break;
            }
            eventType = parser.next();
        }
        haveDatasAvailable = true;
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "storeLoadedXmlValues XML PARSED");
        }
    }

    @Override
    public String toString(){
        String stringDescription = "============================\n" + "shop DUMP-TO-STRING :\n";
        stringDescription += "NUMBER OF ARTISTS : " + shop.size() + "\n";
        for (ArtistDatas artist : shop){
            stringDescription += artist.toString();
        }
        return stringDescription;
    }
}
