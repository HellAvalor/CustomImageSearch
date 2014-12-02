package com.andreykaraman.idstest.db;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.andreykaraman.idstest.DBContentProvider;
import com.andreykaraman.idstest.R;
import com.andreykaraman.idstest.utils.Constants;

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
                addBookmark(intent.getStringExtra(Constants.CONST_TITLE), intent.getStringExtra(Constants.CONST_FULL_URL));
                break;
            case R.id.delete_bookmarks:
                delBookmarks(intent.getLongArrayExtra(Constants.CONST_IMAGE_ID));
                break;
            case R.id.delete_bookmark:
                delBookmark(intent.getLongExtra(Constants.CONST_IMAGE_ID, -1));
                break;
            default:
                Log.e(LOG_SECTION, "Query Error");
        }
    }

    public void addBookmark(String title, String url) {
        ContentValues cv = new ContentValues();
        //cv.put(DBBookmarkPictures.PICTURE_ID, bean.get);
        cv.put(DBBookmarkPictures.PICTURE_TITLE, title);
        cv.put(DBBookmarkPictures.PICTURE_URL, url);

        Uri result = getContentResolver().insert(
                DBContentProvider.URI_BOOKMARK_TABLE, cv);
        Log.d(LOG_SECTION, result.toString());
    }

    private void delBookmark(long bookmarkId) {

        getContentResolver().delete(DBContentProvider.URI_BOOKMARK_TABLE,
                DBBookmarkPictures.PICTURE_ID + "=" + bookmarkId, null);
    }

    private void delBookmarks(long[] ids) {
        for (long noteId : ids) {
            delBookmark(noteId);
        }
    }
}


