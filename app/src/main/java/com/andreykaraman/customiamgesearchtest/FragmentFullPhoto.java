package com.andreykaraman.customiamgesearchtest;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.andreykaraman.customiamgesearchtest.db.DBService;
import com.andreykaraman.customiamgesearchtest.utils.Constants;
import com.andreykaraman.customiamgesearchtest.utils.ImageLoader;
import com.andreykaraman.customiamgesearchtest.utils.Utils;

/**
 * Created by KaramanA on 04.12.2014.
 */
public class FragmentFullPhoto extends SherlockFragment {

    private static FragmentFullPhoto instance;

    private TextView titleView;
    private ImageView fullImage;
    private boolean bookmarked;
    private String url;
    private String title;
    private ImageLoader imageLoader;
    private CheckBox bookmarkSwitcher;

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
        View rootView = inflater.inflate(R.layout.fragment_full_image_view, container, false);

        titleView = (TextView) rootView.findViewById(R.id.pictureTitle);
        fullImage = (ImageView) rootView.findViewById(R.id.picture);
        bookmarkSwitcher = (CheckBox) rootView.findViewById(R.id.saveToBookmarks);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bookmarkSwitcher.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (bookmarked) {
                    Intent intent = new Intent(getActivity(), DBService.class)
                            .putExtra(Constants.CONST_DB_QUERY, R.id.delete_bookmark_by_url)
                            .putExtra(Constants.CONST_FULL_URL, url);
                    getActivity().startService(intent);
                } else {
                    Intent intent = new Intent(getActivity(), DBService.class)
                            .putExtra(Constants.CONST_DB_QUERY, R.id.add_bookmark)
                            .putExtra(Constants.CONST_TITLE, title)
                            .putExtra(Constants.CONST_FULL_URL, url);
                    getActivity().startService(intent);
                }

                bookmarked = !bookmarked;
                Utils.changeCheckBox(bookmarkSwitcher, bookmarked);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        Bundle bundle = this.getArguments();

        url = bundle.getString(Constants.CONST_FULL_URL);
        title = bundle.getString(Constants.CONST_TITLE);
        bookmarked = bundle.getBoolean(Constants.CONST_BOOKMARK);

        titleView.setText(title);
        fullImage.setTag(url);
        Utils.changeCheckBox(bookmarkSwitcher, bookmarked);
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
