package fr.shining_cat.labetehumaine;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import fr.shining_cat.labetehumaine.tools.FullScreenImageAdapter;
import fr.shining_cat.labetehumaine.tools.LocalFolderParser;
import fr.shining_cat.labetehumaine.tools.SimpleDialogs;

/**
 * Created by Shiva on 27/06/2016.
 */
public class FragmentFullscreenImage extends Fragment{

    private final String TAG = "LOGGING::" + this.getClass().getSimpleName();

    private FragmentFullscreenImageListener listener;

    public interface FragmentFullscreenImageListener{
        void updateCurrentFragmentShownVariable(Fragment fragment);
    }

    public static FragmentFullscreenImage newInstance(String imagesFolder, int startPosition, String artistName){
        FragmentFullscreenImage f = new FragmentFullscreenImage();
        Bundle args = new Bundle();
        args.putString("imagesFolderPath", imagesFolder);
        args.putInt("startPosition", startPosition);
        args.putString("artistName", artistName);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(MainActivity.DEBUG) {
            Log.i(TAG, "onCreateView");
        }
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_fullscreen_image, container, false);
        int startPosition = getArguments().getInt("startPosition");
        //
        String fullFolderPath = getArguments().getString("imagesFolderPath");
        LocalFolderParser parser = new LocalFolderParser();
        ArrayList<String> imagesPaths = parser.getFilePaths(fullFolderPath);
        if (imagesPaths != null) {
            String[] imagePathsArray = new String[imagesPaths.size()];
            imagePathsArray = imagesPaths.toArray(imagePathsArray);
            //
            FullScreenImageAdapter pagerAdapter = new FullScreenImageAdapter(getActivity());
            pagerAdapter.setParams(imagePathsArray);
            //
            ViewPager pager = (ViewPager) view.findViewById(R.id.fullscreen_viewpager);
            pager.setAdapter(pagerAdapter);
            pager.setCurrentItem(startPosition);
        } else{
            SimpleDialogs.displayErrorAlertDialog(getActivity(), getActivity().getString(R.string.error_reading_pictures));
        }
        //
        listener.updateCurrentFragmentShownVariable(this);
        return view;
    }



    @Override
    public void onAttach(Context context) {
        if(MainActivity.DEBUG) {
            Log.i(TAG, "onAttach");
        }
        super.onAttach(context);
        listener = (FragmentFullscreenImageListener) context;
        listener.updateCurrentFragmentShownVariable(this);
    }

    // remove ArtistGalleryListener when Fragment detached
    @Override
    public void onDetach() {
        if(MainActivity.DEBUG) {
            Log.i(TAG, "onDetach");
        }
        super.onDetach();

    }
}
