package com.andreykaraman.idstest;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.andreykaraman.idstest.utils.Constants;
import com.andreykaraman.idstest.utils.ImageLoader;

/**
 * Created by KaramanA on 04.12.2014.
 */
public class FragmentFullPhoto extends SherlockFragment {

    private static FragmentFullPhoto instance;

    private TextView imageText;
    private ImageView fullImage;
    private String url;
    private ImageLoader imageLoader;

    public static FragmentFullPhoto getInstance() {
        if (instance == null) {
            instance = new FragmentFullPhoto();
        }
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageLoader = new ImageLoader(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_full_photo_preview, container, false);

        imageText = (TextView) rootView.findViewById(R.id.textViewImageText);
        fullImage = (ImageView) rootView.findViewById(R.id.imageViewFull);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = this.getArguments();

        url = bundle.getString(Constants.CONST_FULL_URL);
        imageText.setText(bundle.getString(Constants.CONST_TITLE));
        Log.d("PhotoPreview", "url " + url);
        fullImage.setTag(url);
        imageLoader.bindImage(url, getActivity(), fullImage);
    }


    @Override
    public void onDetach() {
        recycle();
        super.onDetach();
    }

    public void recycle() {
        if (fullImage.getDrawable() != null) {
            fullImage.getDrawable().setCallback(null);
            fullImage.setImageDrawable(null);
            fullImage.setImageBitmap(null);
        }
    }
}
