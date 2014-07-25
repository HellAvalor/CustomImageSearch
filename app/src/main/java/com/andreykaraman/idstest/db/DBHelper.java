package com.andreykaraman.idstest.db;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.andreykaraman.idstest.MyContentProvider;
import com.andreykaraman.idstest.R;
import com.andreykaraman.idstest.utils.Constants;

public class DBHelper extends IntentService {

	static final String LOG_SECTION = DBHelper.class.getName();

	public DBHelper() {
		super("DBHelper");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(LOG_SECTION, "onCreate");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(LOG_SECTION, "onDestroy");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		int query = intent.getIntExtra(Constants.CONST_DB_QUERY,
				R.id.add_bookmark);
		switch (query) {
			case R.id.add_bookmark:
				Log.d(LOG_SECTION, "Add element");
				addBookmark(intent.getStringExtra(Constants.CONST_TITLE), intent.getStringExtra(Constants.CONST_FULL_URL));
				break;
			default:
				Log.d(LOG_SECTION, "Query Error");
		}
	}

	public void addBookmark(String title, String url) {

		Log.d(LOG_SECTION, "addBookmark");
		ContentValues cv = new ContentValues();
		//cv.put(DBBookmarkPictures.PICTURE_ID, bean.get);
		cv.put(DBBookmarkPictures.PICTURE_TITLE, title);
		cv.put(DBBookmarkPictures.PICTURE_URL, url);

		Uri result = getContentResolver().insert(
				MyContentProvider.URI_BOOKMARK_TABLE, cv);
		Log.d(LOG_SECTION, result.toString());
	}
}


