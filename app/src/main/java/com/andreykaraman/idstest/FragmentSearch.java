package com.andreykaraman.idstest;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.andreykaraman.idstest.adapters.ImageObj;
import com.andreykaraman.idstest.adapters.SearchAdapter;
import com.andreykaraman.idstest.utils.Constants;

import org.apache.http.conn.util.InetAddressUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;

public class FragmentSearch extends SherlockFragment {

    private EditText searchText;
    private ImageView searchButton;
    private ListView resultList;
    private SearchAdapter adapter;
    private ArrayList<Object> listImages;
    private String strSearch;
    private int page = 0;
    private boolean newSearch = false;
    private GetImagesTask loadingTask;

    public FragmentSearch() {
    }

    public static FragmentSearch newInstance() {
        return new FragmentSearch();
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

        searchText.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    searchAction();
                    return true;
                }
                return false;
            }
        });

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
                    loadingTask.execute();
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
                        bundle.putString(Constants.CONST_FULL_URL, ((ImageObj) listImages.get(position)).getFullUrl());
                        bundle.putString(Constants.CONST_TITLE, ((ImageObj) listImages.get(position)).getTitle());
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
        strSearch = searchText.getText().toString();
        strSearch = Uri.encode(strSearch);
        newSearch = true;
        page = 0;
        Log.d("Search", "Search string => " + strSearch);
        loadingTask = new GetImagesTask();
        loadingTask.execute();
    }

    private void SetListViewAdapter(ArrayList<Object> images) {
        int index = resultList.getFirstVisiblePosition();
        int top = (resultList.getChildAt(0) == null) ? 0 : resultList.getChildAt(0).getTop();
        Log.d("SetListViewAdapter", "index " + index + " top " + top);
        adapter = new SearchAdapter(getActivity(), images);
        resultList.setAdapter(adapter);

        resultList.setSelectionFromTop(index, top);
    }

    private ArrayList<Object> getImageList(JSONArray resultArray) {
        ArrayList<Object> listImages = null;
        ImageObj image;

        try {
            listImages = new ArrayList<Object>();
            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject obj;
                obj = resultArray.getJSONObject(i);
                image = new ImageObj();

                image.setTitle(obj.getString("titleNoFormatting"));
                image.setThumbUrl(obj.getString("tbUrl"));
                image.setFullUrl(obj.getString("url"));
//                image.setBookmarked(false);
//                image.setBookmarked(
//                        getActivity().getContentResolver().query(
//                                DBContentProvider.URI_BOOKMARK_TABLE,
//                                new String[]{DBBookmarkPictures.PICTURE_URL},
//                                "COUNT ("+ DBBookmarkPictures.PICTURE_URL +  "= ?);",
//                                new String[]{image.getFullUrl()},
//                                null).getCount()>0);

                Log.d("Search", "Thumb URL => " + obj.getString("tbUrl"));
//
                listImages.add(image);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listImages;
    }

    private String getLocalIpAddress() {
        String ipv4;
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    // for getting IPV4 format
                    if (!inetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(ipv4 = inetAddress.getHostAddress())) {
                        return ipv4;
                    }
                }
            }
        } catch (Exception ex) {
            Log.e("IP Address", ex.toString());
        }
        return null;
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    private class GetImagesTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(getActivity(), "", getResources().getString(R.string.please_wait));
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL url = new URL("https://ajax.googleapis.com/ajax/services/search/images?" +
                        "v=1.0&q=" + strSearch + "&rsz=8"
                        + "&key=AIzaSyCfl7cx6oOkvbq9mFUiF12yni2V7ZelgWk"
                        + "&start=" + (page * 8)
                        + "&userip=" + getLocalIpAddress());

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

                if (listImages == null || page == 0) {
                    listImages = getImageList(resultArray);
                } else {
                    listImages.addAll(getImageList(resultArray));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            SetListViewAdapter(listImages);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

        }
    }
}
