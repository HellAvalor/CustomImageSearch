package com.andreykaraman.customiamgesearchtest;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.andreykaraman.customiamgesearchtest.adapters.BookmarksAdapter;
import com.andreykaraman.customiamgesearchtest.db.DBBookmarkPictures;
import com.andreykaraman.customiamgesearchtest.db.DBService;
import com.andreykaraman.customiamgesearchtest.utils.Constants;

public class FragmentBookmarks extends SherlockFragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private ListView resultList;
    private BookmarksAdapter adapter;
    private LoaderManager.LoaderCallbacks<Cursor> callbacks;

    public FragmentBookmarks() {
    }

    public static FragmentBookmarks newInstance() {
        return new FragmentBookmarks();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bookmarks, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new BookmarksAdapter(getActivity(), null, true, getActivity());

        resultList = (ListView) view.findViewById(R.id.listViewBookmarks);
        resultList.setAdapter(adapter);
        fillData();
        callbacks = this;
        getLoaderManager().initLoader(0, null, callbacks);

        resultList.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Log.d("ItemListener", position + " pressed");
                        Cursor cursor = (Cursor) parent.getItemAtPosition(position);

                        Fragment fragment = FragmentFullPhoto.getInstance();
                        Bundle bundle = new Bundle();
                        bundle.putString(Constants.CONST_FULL_URL, cursor.getString(cursor.getColumnIndex(DBBookmarkPictures.PICTURE_URL)));
                        bundle.putString(Constants.CONST_TITLE, cursor.getString(cursor.getColumnIndex(DBBookmarkPictures.PICTURE_TITLE)));
                        bundle.putBoolean(Constants.CONST_BOOKMARK, true);
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

        resultList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("LongClickItemListener", "" + id);
                Intent intent = new Intent(getActivity(), DBService.class)
                        .putExtra(Constants.CONST_DB_QUERY, R.id.delete_bookmark)
                        .putExtra(Constants.CONST_IMAGE_ID,
                                id);
                getActivity().startService(intent);
                return true;
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {DBBookmarkPictures.PICTURE_ID, DBBookmarkPictures.PICTURE_TITLE,
                DBBookmarkPictures.PICTURE_URL};

        CursorLoader cursorLoader = new CursorLoader(getActivity(),
                com.andreykaraman.customiamgesearchtest.DBContentProvider.URI_BOOKMARK_TABLE, projection, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    private void fillData() {
        getLoaderManager().initLoader(0, null, this);
    }
}
