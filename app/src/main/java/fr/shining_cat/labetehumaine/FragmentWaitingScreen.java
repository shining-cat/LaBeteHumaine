package fr.shining_cat.labetehumaine;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import fr.shining_cat.labetehumaine.tools.BeteHumaineDatas;
import fr.shining_cat.labetehumaine.tools.ScreenSize;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Shiva on 22/06/2016.
 */
public class FragmentWaitingScreen extends Fragment {

    private final String TAG = "LOGGING::" + this.getClass().getSimpleName();

    private TextView welcomeTextView;
    private Animation animBlink;

    // used to inform the MainActivity when waitingscreen is clicked
    private WaitingScreenListener listener;

    public interface WaitingScreenListener{
        void onWaitingScreenClicked();
        void shouldDisplayLogo(Boolean shouldI);
        void updateActionBarTitle(String title);
    }
    public FragmentWaitingScreen(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(MainActivity.DEBUG) {
            Log.i(TAG, "onCreateView");
        }
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_waiting_screen, container, false);
        //get screen size to adjust layout :
        Point realScreenSize = ScreenSize.getRealScreenSize(getActivity());
        int screenWidth = realScreenSize.x;
        int screenHeight = realScreenSize.y;
        if(MainActivity.DEBUG) {
            Log.i(TAG, "onCreateView::SCREEN SIZE : width = " + screenWidth + " X height = " + screenHeight);
        }
        //get reference to display elements
        welcomeTextView = (TextView) view.findViewById(R.id.waiting_screen_textView);
        SharedPreferences savedSettings = getActivity().getSharedPreferences(MainActivity.SETTINGS_FILE_NAME, MODE_PRIVATE);
        String welcomeText = savedSettings.getString(
                getString(R.string.custom_welcome_text_pref_key), getString(R.string.welcome_text_default));
        welcomeTextView.setText(welcomeText);
        welcomeTextView.getLayoutParams().width = screenWidth / 2;

        view.setOnClickListener(onWaitingScreenClicked);
        launchPulsingWelcomeText();
        ((WaitingScreenListener) getActivity()).shouldDisplayLogo(true);
        ((WaitingScreenListener) getActivity()).updateActionBarTitle(getString(R.string.app_name));
        return view;
    }


    private View.OnClickListener onWaitingScreenClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            listener.onWaitingScreenClicked();
        }
    };
    // set WaitingScreenListener when fragment attached
    @Override
    public void onAttach(Context context) {
        if(MainActivity.DEBUG) {
            Log.i(TAG, "onAttach");
        }
        super.onAttach(context);
        listener = (WaitingScreenListener) context;
        listener.shouldDisplayLogo(true);
        listener.updateActionBarTitle(getString(R.string.app_name));
    }

    // remove WaitingScreenListener when Fragment detached
    @Override
    public void onDetach() {
        if(MainActivity.DEBUG) {
            Log.i(TAG, "onDetach");
        }
        super.onDetach();
        listener = null;
        stopPulsingWelcomeText();
    }
    private void launchPulsingWelcomeText(){
        if(MainActivity.DEBUG) {
            Log.i(TAG, "launchPulsingWelcomeText - 1");
        }
        // load the animation
        animBlink = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.pulsing_welcome_text);
        welcomeTextView.startAnimation(animBlink);
    }
    private void stopPulsingWelcomeText(){
        if(MainActivity.DEBUG) {
            Log.i(TAG, "stopPulsingWelcomeText");
        }
        animBlink.cancel();
    }
}
