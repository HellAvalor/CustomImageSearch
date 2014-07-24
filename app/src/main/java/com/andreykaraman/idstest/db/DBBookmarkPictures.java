package com.andreykaraman.idstest.db;

import android.content.ContentValues;
import android.database.Cursor;

public class DBBookmarkPictures extends AbsDBObject {

	public static final String TABLE_NAME = "DBbookmarkPicture";

	public static final String PICTURE_ID = "_id";
	public static final String PICTURE_TITLE = "title";
	public static final String PICTURE_URL = "url";

	public static final String[] FIELDS = {PICTURE_ID, PICTURE_TITLE, PICTURE_URL};

	public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME
			+ "(" + PICTURE_ID + " INTEGER PRIMARY KEY," + PICTURE_TITLE
			+ " TEXT," + PICTURE_URL + " TEXT NOT NULL );";

	public long id = EMPTY_ID;
	public String title;
	public String pictureUrl;

	public DBBookmarkPictures(final Cursor cursor) {
		fillFromCursor(cursor);
	}

	@Override
	public String toString() {
		return title;
	}

	@Override
	public void fillFromCursor(Cursor cursor) {
		this.id = cursor.getLong(0);
		this.title = cursor.getString(1);
		this.pictureUrl = cursor.getString(2);
	}

	/**
	 * Return the fields in a ContentValues object, suitable for insertion into
	 * the database.
	 */
	@Override
	public ContentValues getPictureUrl() {
		final ContentValues values = new ContentValues();
		if (id != EMPTY_ID) {
			values.put(PICTURE_ID, id);
		}

		values.put(PICTURE_TITLE, title);
		values.put(PICTURE_URL, pictureUrl);

		return values;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}

	@Override
	public String getCreateQuery() {
		return CREATE_TABLE;
	}
}
