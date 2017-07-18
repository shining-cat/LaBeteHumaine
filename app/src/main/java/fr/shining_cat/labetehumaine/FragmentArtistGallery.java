package fr.shining_cat.labetehumaine;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;

import fr.shining_cat.labetehumaine.tools.LocalFolderParser;
import fr.shining_cat.labetehumaine.tools.ScreenSize;
import fr.shining_cat.labetehumaine.tools.SimpleDialogs;
import fr.shining_cat.labetehumaine.tools.ThumbnailAdapter;

/**
 * Created by Shiva on 22/06/2016.
 */
public class FragmentArtistGallery  extends Fragment{

    private final String TAG = "LOGGING::" + this.getClass().getSimpleName();
    public final static int DEFAULT_NUM_OF_COLUMNS = 3;


    // used to inform the MainActivity when artist gallery is clicked
    private ArtistGalleryListener listener;

    private String artistName;

    public interface ArtistGalleryListener{
        void onArtistGalleryClicked(int position);
        void shouldDisplayLogo(Boolean shouldI);
        void updateActionBarTitle(String title);
        void updateCurrentFragmentShownVariable(Fragment fragment);
    }

    public static FragmentArtistGallery newInstance(String imagesFolder, String artistName){
        FragmentArtistGallery f = new FragmentArtistGallery();
        Bundle args = new Bundle();
        args.putString("imagesFolderPath", imagesFolder);
        args.putString("artistName", artistName);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(MainActivity.DEBUG) {
            Log.i(TAG, "onCreateView");
        }
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_artist_gallery, container, false);
        //
        Point screenSize = ScreenSize.getRealScreenSize(getActivity());
        int screenWidth = screenSize.x;
        SharedPreferences savedSettings = getActivity().getSharedPreferences(MainActivity.SETTINGS_FILE_NAME, getActivity().MODE_PRIVATE);
        int numberOfColumns = savedSettings.getInt(getString(R.string.number_of_columns_pref_key), DEFAULT_NUM_OF_COLUMNS);
        int columnWidth = screenWidth/ numberOfColumns;
        int spacing = (int) Math.round(columnWidth*.1);
        int thumbsize = columnWidth - spacing;

        String fullFolderPath = getArguments().getString("imagesFolderPath");
        LocalFolderParser parser = new LocalFolderParser();
        ArrayList<String> imagesPaths = parser.getFilePaths(fullFolderPath);
        String[] imagePathsArray = new String[imagesPaths.size()];
        if (imagesPaths != null) {
            imagePathsArray = imagesPaths.toArray(imagePathsArray);
            //
            ThumbnailAdapter gridAdapter = new ThumbnailAdapter(getActivity());
            gridAdapter.setParams(imagePathsArray, thumbsize);
            //
            GridView gridView = (GridView) view.findViewById(R.id.artist_gallery_grid_view);
            gridView.setNumColumns(numberOfColumns);
            gridView.setColumnWidth(columnWidth);
            gridView.setVerticalSpacing(spacing);
            gridView.setPadding(spacing, spacing, spacing, 0);
            gridView.setAdapter(gridAdapter);
            gridView.setOnItemClickListener(onThumnailClicked);
        } else{
            SimpleDialogs.displayErrorAlertDialog(getActivity(), getActivity().getString(R.string.error_reading_pictures));
        }
        //
        ((ArtistGalleryListener) getActivity()).shouldDisplayLogo(false);
        artistName = getArguments().getString("artistName");
        ((ArtistGalleryListener) getActivity()).updateActionBarTitle(artistName);
        listener.updateCurrentFragmentShownVariable(this);
        return view;
    }

    private AdapterView.OnItemClickListener onThumnailClicked = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(MainActivity.DEBUG) {
                Log.i(TAG, "onItemClick :: position = " + position + " // id = " + id);
            }
            listener.onArtistGalleryClicked(position);
        }
    };

    @Override
    public void onAttach(Context context) {
        if(MainActivity.DEBUG) {
            Log.i(TAG, "onAttach");
        }
        super.onAttach(context);
        listener = (ArtistGalleryListener) context;
        listener.shouldDisplayLogo(false);
        listener.updateActionBarTitle(artistName);
        listener.updateCurrentFragmentShownVariable(this);
    }

    // remove ArtistGalleryListener when Fragment detached
    @Override
    public void onDetach() {
        if(MainActivity.DEBUG) {
            Log.i(TAG, "onDetach");
        }
        super.onDetach();
        listener = null;
    }
}
