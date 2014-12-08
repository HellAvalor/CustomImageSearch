package com.andreykaraman.customiamgesearchtest.db;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.andreykaraman.customiamgesearchtest.DBContentProvider;
import com.andreykaraman.customiamgesearchtest.R;
import com.andreykaraman.customiamgesearchtest.utils.Constants;

public class DBService extends IntentService {

    static final String LOG_SECTION = DBService.class.getName();

    public DBService() {
        super("DBHelper");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int query = intent.getIntExtra(Constants.CONST_DB_QUERY,
                -1);
        switch (query) {
            case R.id.add_bookmark:
                tryToAddBookmark(intent.getStringExtra(Constants.CONST_TITLE), intent.getStringExtra(Constants.CONST_FULL_URL));
                break;
            case R.id.delete_bookmarks:
                delBookmarks(intent.getLongArrayExtra(Constants.CONST_IMAGE_ID));
                break;
            case R.id.delete_bookmark:
                delBookmarkById(intent.getLongExtra(Constants.CONST_IMAGE_ID, -1));
                break;
            case R.id.delete_bookmark_by_url:
                delBookmarkByFullUrl(intent.getStringExtra(Constants.CONST_FULL_URL));
                break;
            default:
                Log.e(LOG_SECTION, "Query Error");
        }
    }

    public void tryToAddBookmark(String title, String url) {
        ContentValues cv = new ContentValues();
        cv.put(DBBookmarkPictures.PICTURE_TITLE, title);
        cv.put(DBBookmarkPictures.PICTURE_URL, url);
        Uri result = null;

        if (getContentResolver().update(DBContentProvider.URI_BOOKMARK_TABLE, cv, DBBookmarkPictures.PICTURE_URL + "= ?", new String[]{url}) == 0) {
            result = getContentResolver().insert(DBContentProvider.URI_BOOKMARK_TABLE, cv);
        }

        Log.d(LOG_SECTION, "" + result);
    }

    private void delBookmarkById(long bookmarkId) {

        getContentResolver().delete(DBContentProvider.URI_BOOKMARK_TABLE,
                DBBookmarkPictures.PICTURE_ID + "=" + bookmarkId, null);
    }

    private void delBookmarkByFullUrl(String bookmarkFullUrl) {

        getContentResolver().delete(DBContentProvider.URI_BOOKMARK_TABLE,
                DBBookmarkPictures.PICTURE_URL + "= ?", new String[]{bookmarkFullUrl});
    }

    private void delBookmarks(long[] ids) {
        for (long noteId : ids) {
            delBookmarkById(noteId);
        }
    }
}


