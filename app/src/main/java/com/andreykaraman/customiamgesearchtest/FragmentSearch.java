package com.andreykaraman.customiamgesearchtest;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.andreykaraman.customiamgesearchtest.adapters.ImageObj;
import com.andreykaraman.customiamgesearchtest.adapters.SearchAdapter;
import com.andreykaraman.customiamgesearchtest.db.DBBookmarkPictures;
import com.andreykaraman.customiamgesearchtest.utils.Constants;
import com.andreykaraman.customiamgesearchtest.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class FragmentSearch extends SherlockFragment {
    private static final int QUERY_SIZE = 8;
    private String ipAddress;
    private EditText searchText;
    private ImageView searchButton;
    private ListView resultList;
    private SearchAdapter adapter;
    private ArrayList<ImageObj> listImages;
    private String strSearch;
    private int page = 0;
    private boolean newSearch = false;
    private GetImagesTask loadingTask;

    public FragmentSearch() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search, container, false);

        searchText = (EditText) v.findViewById(R.id.editTextSearchQuery);
        searchButton = (ImageView) v.findViewById(R.id.imageViewSearch);
        resultList = (ListView) v.findViewById(R.id.searchResults);

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ipAddress = Utils.getLocalIpAddress();


//        searchText.setOnKeyListener(new View.OnKeyListener() {
//
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                // If the event is a key-down event on the "enter" button
//                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
//                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
//                    searchAction();
//                    return true;
//                }
//                return false;
//            }
//        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchAction();
            }
        });

        resultList.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;

                if (loadMore && (newSearch || page > 0) && loadingTask.getStatus() == AsyncTask.Status.FINISHED) {
                    newSearch = false;
                    page++;
                    loadingTask = new GetImagesTask();
                    loadingTask.execute(strSearch, String.valueOf(page * QUERY_SIZE));
                }
            }
        });

        resultList.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Log.d("ItemListener", position + " pressed");

                        Fragment fragment = FragmentFullPhoto.getInstance();
                        Bundle bundle = new Bundle();
                        bundle.putString(Constants.CONST_FULL_URL, listImages.get(position).getFullUrl());
                        bundle.putString(Constants.CONST_TITLE, listImages.get(position).getTitle());
                        bundle.putBoolean(Constants.CONST_BOOKMARK, listImages.get(position).isBookmarked());
                        fragment.setArguments(bundle);

                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        ft.hide(getActivity().getSupportFragmentManager().findFragmentByTag("tabs"));
                        ft.add(R.id.content_container, fragment);
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        ft.addToBackStack(null);
                        ft.commit();
                    }
                }
        );
    }

    private void searchAction() {
        hideKeyboard();
        strSearch = searchText.getText().toString();
        strSearch = Uri.encode(strSearch);
        newSearch = true;
        page = 0;

        loadingTask = new GetImagesTask();
        loadingTask.execute(strSearch, String.valueOf(page * QUERY_SIZE));
    }


    private void hideKeyboard() {
        if (getActivity().getCurrentFocus() == null)
            return;
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    }

    private void setListViewAdapter(ArrayList<ImageObj> images) {
        int index = resultList.getFirstVisiblePosition();
        int top = (resultList.getChildAt(0) == null) ? 0 : resultList.getChildAt(0).getTop();
        adapter = new SearchAdapter(getActivity(), images);
        resultList.setAdapter(adapter);
        resultList.setSelectionFromTop(index, top);
    }

    private ArrayList<ImageObj> getImageList(JSONArray resultArray) {
        ArrayList<ImageObj> listImages = null;
        ImageObj image;

        try {
            listImages = new ArrayList<ImageObj>();
            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject obj;
                obj = resultArray.getJSONObject(i);

                image = new ImageObj();
                image.setTitle(obj.getString("titleNoFormatting"));
                image.setThumbUrl(obj.getString("tbUrl"));
                image.setFullUrl(obj.getString("url"));
                image.setBookmarked(
                        getActivity().getContentResolver().query(
                                DBContentProvider.URI_BOOKMARK_TABLE,
                                new String[]{DBBookmarkPictures.PICTURE_URL},
                                DBBookmarkPictures.PICTURE_URL + "= ?",
                                new String[]{image.getFullUrl()}, null).getCount() > 0);

                listImages.add(image);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listImages;
    }

    private class GetImagesTask extends AsyncTask<String, Void, ArrayList<ImageObj>> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(getActivity(), "", getResources().getString(R.string.please_wait));
        }

        @Override
        protected ArrayList<ImageObj> doInBackground(String... strings) {
            ArrayList<ImageObj> imageList = null;
            String searchString = strings[0];
            String page = strings[1];

            try {
                URL url = new URL("https://ajax.googleapis.com/ajax/services/search/images?" +
                        "v=1.0&q=" + searchString + "&rsz=" + QUERY_SIZE
                        + "&key=AIzaSyCfl7cx6oOkvbq9mFUiF12yni2V7ZelgWk"
                        + "&start=" + page
                        + "&userip=" + ipAddress);

                Log.d("Get request", "url string => " + url);
                URLConnection connection = url.openConnection();
                connection.addRequestProperty("Referrer", "http://andreykaraman.com/");

                String line;
                StringBuilder builder = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }

                Log.d("Get request", "Builder string => " + builder.toString());

                JSONObject json = new JSONObject(builder.toString());

                JSONObject responseObject = json.getJSONObject("responseData");
                JSONArray resultArray = responseObject.getJSONArray("results");

                if (resultArray.length() > 0) {
                    imageList = getImageList(resultArray);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return imageList;
        }

        @Override
        protected void onPostExecute(ArrayList<ImageObj> result) {
            super.onPostExecute(result);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            if (result == null) {
                Toast.makeText(getActivity(), R.string.nothing_found, Toast.LENGTH_SHORT).show();
            } else {
                if (listImages == null || page == 0) {
                    listImages = result;
                } else {
                    listImages.addAll(result);
                }
                setListViewAdapter(listImages);
            }
        }
    }
}
