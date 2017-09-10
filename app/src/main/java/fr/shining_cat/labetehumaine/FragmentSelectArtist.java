package fr.shining_cat.labetehumaine;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import fr.shining_cat.labetehumaine.tools.BeteHumaineDatas;
import fr.shining_cat.labetehumaine.tools.LocalXMLParser;

import static android.content.Context.MODE_PRIVATE;
import static fr.shining_cat.labetehumaine.MainActivity.SETTINGS_FILE_NAME;

/**
 * Created by Shiva on 22/06/2016.
 */
public class FragmentSelectArtist extends Fragment {

    private final String TAG = "LOGGING::" + this.getClass().getSimpleName();

    public FragmentSelectArtist(){}
    private BeteHumaineDatas beteHumaineDatas;
    private FragmentSelectArtistListener listener;

    private Boolean firstInit = true;

    public interface FragmentSelectArtistListener{
        void shouldDisplayLogo(Boolean shouldI);
        void updateActionBarTitle(String title);
        void updateCurrentFragmentShownVariable(Fragment fragment);
    }
    private boolean parseLocalXML(){
        LocalXMLParser localXMLParser = new LocalXMLParser();
        return localXMLParser.parseXMLdatas(this.getActivity());
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "onCreateView");
        }
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_select_artist, container, false);
        view.invalidate();
        beteHumaineDatas = BeteHumaineDatas.getInstance();
        if(!beteHumaineDatas.hasDatasReady()){
            parseLocalXML();
        }
        ArrayList<ArtistDatas> shop = beteHumaineDatas.getShop();

        SharedPreferences savedSettings = getActivity().getSharedPreferences(SETTINGS_FILE_NAME, MODE_PRIVATE);
        Boolean forceFitArtistsCardsOnGalleryScreen = savedSettings.getBoolean(getString(R.string.force_fit_artists_cards_to_gallery_screen_pref_key), false);
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "onCreateView::forceFitArtistsCardsOnGalleryScreen = " + forceFitArtistsCardsOnGalleryScreen);
        }
        int numberOfArtists = shop.size();
        if(firstInit) {
            for (int artistIndex = 0; artistIndex < numberOfArtists; artistIndex++) {
                FragmentTransaction fragmentTransaction = this.getChildFragmentManager().beginTransaction();
                FragmentArtistCard fragmentArtistCard = FragmentArtistCard.newInstance(artistIndex, numberOfArtists, forceFitArtistsCardsOnGalleryScreen);
                //
                fragmentTransaction.add(R.id.select_artist_cards_holder, fragmentArtistCard);
                fragmentTransaction.commit();
            }
            firstInit = false;
        }
        ((FragmentSelectArtistListener) getActivity()).shouldDisplayLogo(true);
        ((FragmentSelectArtistListener) getActivity()).updateActionBarTitle(getString(R.string.select_artist_title));
        listener.updateCurrentFragmentShownVariable(this);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "onAttach");
        }
        super.onAttach(context);
        listener = (FragmentSelectArtistListener) context;
        listener.shouldDisplayLogo(true);
        listener.updateActionBarTitle(getString(R.string.select_artist_title));
        listener.updateCurrentFragmentShownVariable(this);
    }

    // remove listeners etc when Fragment detached
    @Override
    public void onDetach() {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "onDetach");
        }
        super.onDetach();
    }

}
