package fr.shining_cat.labetehumaine.tools;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;

import fr.shining_cat.labetehumaine.R;


/**
 * Created by Shiva on 27/06/2016.
 */
public class FullScreenImageAdapter extends PagerAdapter {

    private final String TAG = "LOGGING::" + this.getClass().getSimpleName();

    private Context mContext;
    private String[] picturesPaths;

    public FullScreenImageAdapter(Context c) {
        mContext = c;
    }

    public void setParams(String[] picturesLocalPaths){
        picturesPaths = picturesLocalPaths;
    }

    public int getCount() {
        return picturesPaths.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (object);
    }
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imgDisplay;

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.fullscreen_imageview, container, false);
        imgDisplay = (ImageView) viewLayout.findViewById(R.id.fullscreen_imageview);
        File pictureToLoad = new File(picturesPaths[position]);
        final ProgressBar progressView = (ProgressBar) viewLayout.findViewById(R.id.fullscreen_progressBar);
        progressView.setVisibility(View.VISIBLE);
        final Callback loadedCallback = new Callback() {
                        @Override
                        public void onSuccess() {
                            progressView.setVisibility(View.GONE);
                        }
                        @Override
                        public void onError() {
                            SimpleDialogs.displayErrorAlertDialog(mContext, mContext.getString(R.string.error_loading_picture));
                        }
        };
        imgDisplay.setTag(loadedCallback);

        Picasso.
                with(mContext)
                .load(pictureToLoad)
                .fit()
                .centerInside()
                .error(R.drawable.ic_error_outline_black_24dp)
                .into(imgDisplay, loadedCallback);
        (container).addView(viewLayout);

        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        (container).removeView((FrameLayout) object);

    }
}