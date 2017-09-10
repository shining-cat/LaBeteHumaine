package fr.shining_cat.labetehumaine.tools;

import android.app.Activity;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.FileInputStream;
import java.io.IOException;

import fr.shining_cat.labetehumaine.BuildConfig;
import fr.shining_cat.labetehumaine.MainActivity;
import fr.shining_cat.labetehumaine.R;

/**
 * Created by Shiva on 26/07/2017.
 */

public class LocalXMLParser {

    private final String TAG = "LOGGING::" + this.getClass().getSimpleName();



    public boolean parseXMLdatas(Activity activity){
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "parseXMLdatas");
        }
        boolean success = false;
        BeteHumaineDatas beteHumaineDatas = BeteHumaineDatas.getInstance();
        try {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "parseXMLdatas :: activity = " + activity.toString());
            }
            // access the xml file and convert it to input stream
            FileInputStream fileIS = activity.openFileInput(BeteHumaineDatas.XML_LOCAL_GENERAL_DATAS_FILE_NAME);
            try {
                XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
                xmlPullParserFactory.setNamespaceAware(false);
                XmlPullParser parser = xmlPullParserFactory.newPullParser();
                parser.setInput(fileIS, null);
                //send inputStream to be parsed and stored as ArrayList
                beteHumaineDatas.storeLoadedXmlValues(parser);
                success = true;
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "parseXMLdatas ParseXML = SUCCESS!");
                }
            } catch (XmlPullParserException e) {
                e.printStackTrace();
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "parseXMLdatas::XmlPullParserException = " + e.getMessage());
                    Log.i(TAG, "parseXMLdatas :: XmlPullParserException :: activity = " + activity.toString());
                }
                SimpleDialogs.displayErrorAlertDialog(activity, activity.getString(R.string.error_xml_file_parsing));
            }
        }catch (IOException ioe) {
            ioe.printStackTrace();
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "parseXMLdatas::IOException = " + ioe.getMessage());
            }
            SimpleDialogs.displayErrorAlertDialog(activity, activity.getString(R.string.error_xml_local_file_access));
        }
        return success;
    }
}
