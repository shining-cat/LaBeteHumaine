package fr.shining_cat.labetehumaine;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;

import fr.shining_cat.labetehumaine.tools.BeteHumaineDatas;
import fr.shining_cat.labetehumaine.tools.ScreenSize;

/**
 * Created by Shiva on 24/06/2016.
 */
public class FragmentArtistCard extends Fragment {

    private final String TAG = "LOGGING::" + this.getClass().getSimpleName();

    public static String TATTOOS_WAS_CLICKED = "tattoos_was_clicked";
    public static String DRAWINGS_WAS_CLICKED = "drawings_was_clicked";

    private AnimatorSet mSetRightOut;
    private AnimatorSet mSetLeftIn;
    private boolean mIsBackVisible = false;
    private View mCardFrontLayout;
    private View mCardBackLayout;
    private Button openTattoos;
    private Button openDrawings;
    private int artistIndex;
    private int numberOfArtists;
    private Boolean forceFitArtistsCardsOnGalleryScreen;

    // used to inform the MainActivity when waitingscreen is clicked
    private FragmentArtistCardListener listener;

    public FragmentArtistCard(){}

    public interface FragmentArtistCardListener{
        void onArtistCardClicked(int artistIndex, String whatWasClicked);
    }

    public static FragmentArtistCard newInstance(int index, int numberOfArtists, Boolean forceFitArtistsCardsOnGalleryScreen){
        FragmentArtistCard f = new FragmentArtistCard();
        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putInt("index", index);
        args.putInt("numberOfArtists", numberOfArtists);
        args.putBoolean("forceFitArtistsCardsOnGalleryScreen", forceFitArtistsCardsOnGalleryScreen);
        f.setArguments(args);
        return f;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (MainActivity.DEBUG) {
            Log.i(TAG, "onCreateView");
        }
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_artist_card, container, false);
        //
        artistIndex = getArguments().getInt("index", 0);
        numberOfArtists = getArguments().getInt("numberOfArtists", 0);
        forceFitArtistsCardsOnGalleryScreen = getArguments().getBoolean("forceFitArtistsCardsOnGalleryScreen", false);
        //
        Point realScreenSize = ScreenSize.getRealScreenSize(getActivity());
        int screenWidth = realScreenSize.x;
        int screenHeight = realScreenSize.y;

        if(forceFitArtistsCardsOnGalleryScreen) {
            view.getLayoutParams().height = screenHeight / 2;
            int paddingInPixels = getResources().getDimensionPixelOffset(R.dimen.artist_card_horiz_margin_when_force_fit);
            view.setPadding(paddingInPixels,0,paddingInPixels,0);
            view.getLayoutParams().width = screenWidth / numberOfArtists;
        } else{
            view.getLayoutParams().width = screenWidth / 4;
            view.getLayoutParams().height = screenHeight / 2;
            int paddingInPixels = getResources().getDimensionPixelOffset(R.dimen.artist_card_horiz_margin_when_scrolling);
            view.setPadding(paddingInPixels,0,paddingInPixels,0);
        }
        //front layout
        mCardFrontLayout = view.findViewById(R.id.card_front);
        BeteHumaineDatas beteHumaineDatas = BeteHumaineDatas.getInstance(getActivity());
        ArtistDatas artistDatas = beteHumaineDatas.getShop().get(artistIndex);
        ImageView artistFaceImage = (ImageView) view.findViewById(R.id.artist_card_face_image_view);
        String artistFacePictureFileName = getParentFragment().getActivity().getFilesDir() + File.separator + artistDatas.getArtistLocalRootFolderName() + File.separator + artistDatas.getPictureLocalName();
        File artistFacePictureFile = new File(artistFacePictureFileName);
        if (artistFacePictureFile.exists()) {
            Picasso
                    .with(getActivity())
                    .load(artistFacePictureFile)
                    .fit()
                    .centerCrop()
                    .into(artistFaceImage);
        }
        TextView artistName = (TextView) view.findViewById(R.id.artist_card_name_textfield);
        artistName.setText(artistDatas.getName());
        TextView artistDescription = (TextView) view.findViewById(R.id.artist_card_description_textfield);
        artistDescription.setText(artistDatas.getDescription());
        mCardFrontLayout.setClipToOutline(true);
        //back layout
        mCardBackLayout = view.findViewById(R.id.card_back);
        //name of artist on back of cards removed :
        //TextView artistBackName = (TextView) view.findViewById(R.id.artist_card_back_name_textfield);
        //artistBackName.setText(artistDatas.getName());
        openTattoos = (Button) view.findViewById(R.id.artist_card_button_tatoos);
        openTattoos.setText(R.string.tattoos);
        if(artistDatas.getNumberOfTattoos()!=0) {
            openTattoos.setVisibility(View.VISIBLE);
        } else{
            openTattoos.setVisibility(View.INVISIBLE);
        }
        openDrawings = (Button) view.findViewById(R.id.artist_card_button_drawings);
        openDrawings.setText(R.string.drawings);
        if(artistDatas.getNumberOfDrawings()!=0) {
            openDrawings.setVisibility(View.VISIBLE);
        } else{
            openDrawings.setVisibility(View.INVISIBLE);
        }
        mCardBackLayout.setClipToOutline(true);
        //
        mCardFrontLayout.setVisibility(View.VISIBLE);
        mCardBackLayout.setVisibility(View.INVISIBLE);
        //
        loadAnimations();
        changeCameraDistance();
        activateInteractions();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        if(MainActivity.DEBUG) {
            Log.i(TAG, "onAttach");
        }
        super.onAttach(context);
        listener = (FragmentArtistCardListener) context;
    }

    @Override
    public void onDetach() {
        if(MainActivity.DEBUG) {
            Log.i(TAG, "onDetach");
        }
        super.onDetach();
        mHideHandler.removeMessages(0);
        listener = null;
    }
    private View.OnClickListener onClickFrontFace =new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            if(MainActivity.DEBUG) {
                Log.i(TAG, "onClickFrontFace");
            }
            flipCard(v);
        }

    };
    private View.OnClickListener onTattoosClicked =new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            if(MainActivity.DEBUG) {
                Log.i(TAG, "onTattoosClicked");
            }
            listener.onArtistCardClicked(artistIndex, TATTOOS_WAS_CLICKED);
        }
    };
    private View.OnClickListener onDrawingsClicked =new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            if(MainActivity.DEBUG) {
                Log.i(TAG, "onDrawingsClicked");
            }
            listener.onArtistCardClicked(artistIndex, DRAWINGS_WAS_CLICKED);
        }
    };
    private void changeCameraDistance() {
        int distance = 8000;
        float scale = getResources().getDisplayMetrics().density * distance;
        mCardFrontLayout.setCameraDistance(scale);
        mCardBackLayout.setCameraDistance(scale);
    }

    private void loadAnimations() {
        mSetRightOut = (AnimatorSet) AnimatorInflater.loadAnimator(getActivity(), R.animator.out_animation);
        mSetLeftIn = (AnimatorSet) AnimatorInflater.loadAnimator(getActivity(), R.animator.in_animation);
        //set a listener on only one (both will always play together
        mSetRightOut.addListener(onAnimationListener);
    }

    public void flipCard(View view) {
        if (!mIsBackVisible) {//front -> back
            mSetRightOut.setTarget(mCardFrontLayout);
            mSetLeftIn.setTarget(mCardBackLayout);
            mSetRightOut.start();
            mSetLeftIn.start();
            mIsBackVisible = true;
            //timer to auto flip back
            mHideHandler.removeMessages(0);
            mHideHandler.sendEmptyMessageDelayed(0, 5000);
        } else {//back -> front
            mSetRightOut.setTarget(mCardBackLayout);
            mSetLeftIn.setTarget(mCardFrontLayout);
            mSetRightOut.start();
            mSetLeftIn.start();
            mIsBackVisible = false;
        }
    }

    Animator.AnimatorListener onAnimationListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
            mCardFrontLayout.setVisibility(View.VISIBLE);
            mCardBackLayout.setVisibility(View.VISIBLE);
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if(!mIsBackVisible) {
                mCardFrontLayout.setVisibility(View.VISIBLE);
                mCardBackLayout.setVisibility(View.INVISIBLE);
            }else{
                mCardFrontLayout.setVisibility(View.INVISIBLE);
                mCardBackLayout.setVisibility(View.VISIBLE);

            }
            activateInteractions();
        }

        @Override
        public void onAnimationCancel(Animator animation) {}

        @Override
        public void onAnimationRepeat(Animator animation) {}
    };

    private void activateInteractions() {
        if(mIsBackVisible){
            if(MainActivity.DEBUG) {
                Log.i(TAG, "activateInteractions::BACK");
            }
            openTattoos.setOnClickListener(onTattoosClicked);
            openDrawings.setOnClickListener(onDrawingsClicked);
            mCardFrontLayout.setOnClickListener(null);
        } else{
            if(MainActivity.DEBUG) {
                Log.i(TAG, "activateInteractions::FRONT");
            }
            mCardFrontLayout.setOnClickListener(onClickFrontFace);
            openTattoos.setOnClickListener(null);
            openDrawings.setOnClickListener(null);
        }
    }

    private final Handler mHideHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            flipCard(getView());
        }
    };
}
