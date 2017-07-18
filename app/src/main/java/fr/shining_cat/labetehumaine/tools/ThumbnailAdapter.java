package fr.shining_cat.labetehumaine.tools;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;

import fr.shining_cat.labetehumaine.MainActivity;
import fr.shining_cat.labetehumaine.R;

/**
 * Created by Shiva on 27/06/2016.
 */
public class ThumbnailAdapter extends BaseAdapter {

    private final String TAG = "LOGGING::" + this.getClass().getSimpleName();

    private Context mContext;
    private String[] picturesPaths;
    private int thumbSize;

    public ThumbnailAdapter(Context c) {
        mContext = c;
    }

    public void setParams(String[] picturesLocalPaths, int displaySize){
        picturesPaths = picturesLocalPaths;
        thumbSize = displaySize;
    }

    public int getCount() {
        return picturesPaths.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    @Override
    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        if(MainActivity.DEBUG) {
            Log.i(TAG, "getView");
        }
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(thumbSize, thumbSize));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) convertView;
        }
        File pictureToLoad = new File(picturesPaths[position]);
        Picasso.
                with(mContext)
                .load(pictureToLoad)
                .resize(thumbSize, thumbSize)
                .centerCrop()
                .placeholder(mContext.getDrawable(R.drawable.card_background))
                .error(R.drawable.ic_error_outline_black_24dp)
                .into(imageView);
        return imageView;
    }
}