package fr.shining_cat.labetehumaine.tools;

import android.app.Activity;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import fr.shining_cat.labetehumaine.ArtistDatas;
import fr.shining_cat.labetehumaine.MainActivity;
import fr.shining_cat.labetehumaine.R;

/**
 * Created by Shiva on 18/06/2016.
 */
public class BeteHumaineDatas {

    private final String TAG = "LOGGING::" + this.getClass().getSimpleName();

    private static BeteHumaineDatas beteHumaineDatasSingleton = null;

    public static final String XML_LOCAL_FILE_NAME = "bete_humaine_datas.xml";
    public static final String PICTURES_LOCAL_ROOT_FOLDER = "bete_humaine_pics";

    private Activity mActivity;
    private boolean haveDatasAvailable = false;
    private ArrayList<ArtistDatas> shop;
    private String waitingScreenText = "";

    private BeteHumaineDatas(Activity activity){
        if(MainActivity.DEBUG) {
            Log.i(TAG, "CONSTRUCTOR - EMPTY");
        }
        mActivity = activity;
    }

    public static BeteHumaineDatas getInstance(Activity activity){
        if(beteHumaineDatasSingleton == null){
            beteHumaineDatasSingleton = new BeteHumaineDatas(activity);
        }
        return beteHumaineDatasSingleton;
    }

    public ArrayList<ArtistDatas> getShop() {
        if(shop == null){
            parseXMLdatas();
        }
        return shop;
    }

    public String getWaitingScreenText(){
        //TODO : supprimer proprement les références à ce welcome text : il est maintenant paramétré par l'utilisateur et stocké dans les sharedpref
        if(waitingScreenText == null){
            parseXMLdatas();
        }
        return waitingScreenText;
    }
    public int getTotalNumberOfPictures(){
        int total = 0;
        for (ArtistDatas artist : shop){
            total += artist.getNumberOfTattoos();
            total += artist.getNumberOfDrawings();
        }
        return total;
    }

    public boolean goGrabLocalDatasNow(){
        if(MainActivity.DEBUG) {
            Log.i(TAG, "goGrabLocalDatasNow");
        }
        boolean haveDatasAvailable;
        haveDatasAvailable = parseXMLdatas();
        return haveDatasAvailable;
    }


    public boolean hasDatas() {
        return haveDatasAvailable;
    }

    private boolean parseXMLdatas(){
        if(MainActivity.DEBUG) {
            Log.i(TAG, "parseXMLdatas");
        }
        boolean success = false;
        try {
            if(MainActivity.DEBUG) {
                Log.i(TAG, "parseXMLdatas :: mActivity = " + mActivity.toString());
            }
            // access the xml file and convert it to input stream
            FileInputStream fileIS = mActivity.openFileInput(XML_LOCAL_FILE_NAME);
            try {
                XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
                xmlPullParserFactory.setNamespaceAware(false);
                XmlPullParser parser = xmlPullParserFactory.newPullParser();
                parser.setInput(fileIS, null);
                //send inputStream to parser
                getLoadedXmlValues(parser);
                success = true;
                if(MainActivity.DEBUG) {
                    Log.i(TAG, "parseXMLdatas ParseXML = SUCCESS!");
                }
            } catch (XmlPullParserException e) {
                e.printStackTrace();
                if(MainActivity.DEBUG) {
                    Log.i(TAG, "parseXMLdatas::XmlPullParserException = " + e.getMessage());
                    Log.i(TAG, "parseXMLdatas :: XmlPullParserException :: mActivity = " + mActivity.toString());
                }
                SimpleDialogs.displayErrorAlertDialog(mActivity, mActivity.getString(R.string.error_xml_file_parsing));
            }
        }catch (IOException ioe) {
            ioe.printStackTrace();
            if(MainActivity.DEBUG) {
                Log.i(TAG, "parseXMLdatas::IOException = " + ioe.getMessage());
            }
            SimpleDialogs.displayErrorAlertDialog(mActivity, mActivity.getString(R.string.error_xml_local_file_access));
        }
        return success;
    }
    private void getLoadedXmlValues(XmlPullParser parser) throws XmlPullParserException, IOException {
        if(MainActivity.DEBUG) {
            Log.i(TAG, "getLoadedXmlValues");
        }
        //reset datas :
        haveDatasAvailable = false;
        waitingScreenText = "";
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
                    if (name.equalsIgnoreCase("WELCOME_TEXT")) {
                        waitingScreenText = parser.nextText();
                    } else if (name.equalsIgnoreCase("ARTIST")) {
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
        if(MainActivity.DEBUG) {
            Log.i(TAG, "getLoadedXmlValues XML PARSED");
        }
    }

    @Override
    public String toString(){
        String stringDescription = "============================\n" + "shop DUMP-TO-STRING :\n";
        stringDescription += "WAITING TEXT : " + waitingScreenText + "\n";
        stringDescription += "NUMBER OF ARTISTS : " + shop.size() + "\n";
        for (ArtistDatas artist : shop){
            stringDescription += artist.toString();
        }
        return stringDescription;
    }
}
