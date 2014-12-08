package com.andreykaraman.customiamgesearchtest;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.andreykaraman.customiamgesearchtest.db.DBBookmarkPictures;
import com.andreykaraman.customiamgesearchtest.db.DBHelper;
import com.andreykaraman.customiamgesearchtest.db.DBHelper.Tables;
import com.andreykaraman.customiamgesearchtest.db.SelectionBuilder;

import java.util.ArrayList;

public class DBContentProvider extends ContentProvider {

    // All URIs share these parts
    public static final String AUTHORITY = "com.andreykaraman.idstest";
    public static final String SCHEME = "content://";
    // URIs
    public static final String BOOKMARKS_TABLE = "DBbookmarkPicture";
    public static final String BOOKMARKS_TABLES = SCHEME + AUTHORITY + "/"
            + BOOKMARKS_TABLE;
    public static final Uri URI_BOOKMARK_TABLE = Uri.parse(BOOKMARKS_TABLES);
    private static final UriMatcher sURIMatcher = new UriMatcher(
            UriMatcher.NO_MATCH);

    static {
        addURI(BOOKMARKS_TABLE, false, QueryId.DB_BOOKMARK_IMAGES);
    }

    protected final String TAG = this.getClass().getSimpleName();
    private DBHelper mDatabaseHelper;

    private static void addURI(String uri, boolean id, QueryId query) {
        if (id) {
            sURIMatcher.addURI(AUTHORITY, uri + "/#", query.ordinal());
        } else {
            sURIMatcher.addURI(AUTHORITY, uri, query.ordinal());
        }
    }

    private static QueryId matchQuery(Uri uri) {
        int id = sURIMatcher.match(uri);
        return id == -1 ? QueryId.NONE : QueryId.values()[id];
    }

    @Override
    public String getType(Uri uri) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean onCreate() {
        mDatabaseHelper = new DBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor result;
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();

        switch (matchQuery(uri)) {
            default: {
                final SelectionBuilder builder = buildSimpleSelection(uri);
                result = builder.where(selection, selectionArgs).query(db,
                        projection, sortOrder);
            }
        }

        result.setNotificationUri(getContext().getContentResolver(), uri);
        return result;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri result;

        Log.d(TAG, uri.toString());
        Log.d(TAG, values.toString());

        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        long id;

        switch (matchQuery(uri)) {
            case DB_BOOKMARK_IMAGES:
                id = db.insertOrThrow(DBBookmarkPictures.TABLE_NAME, null, values);
                result = Uri.parse(URI_BOOKMARK_TABLE + "/" + id);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return result;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        final SelectionBuilder builder = buildSimpleSelection(uri);
        int rowsDeleted = builder.where(selection, selectionArgs).delete(db);

        if (rowsDeleted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        final SelectionBuilder builder = buildSimpleSelection(uri);
        int rowsUpdated = builder.where(selection, selectionArgs).update(db,
                values);

        if (rowsUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    private SelectionBuilder buildSimpleSelection(Uri uri) {
        final SelectionBuilder builder = new SelectionBuilder();

        switch (matchQuery(uri)) {
            case DB_BOOKMARK_IMAGES:
                return builder.table(Tables.TABLES_DB_BOOKMARKS);

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    private String simpleGetTable(Uri uri) {
        switch (matchQuery(uri)) {
            case DB_BOOKMARK_IMAGES:
                return Tables.TABLES_DB_BOOKMARKS;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    /**
     * Apply the given set of {@link android.content.ContentProviderOperation}, executing inside
     * a {@link android.database.sqlite.SQLiteDatabase} transaction. All changes will be rolled back if
     * any single one fails. Copy-paste from iosched's ScheduleProvider.
     */
    @Override
    public ContentProviderResult[] applyBatch(
            ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            final int numOperations = operations.size();
            final ContentProviderResult[] results = new ContentProviderResult[numOperations];
            for (int i = 0; i < numOperations; i++) {
                results[i] = operations.get(i).apply(this, results, i);
            }
            db.setTransactionSuccessful();
            return results;
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public final int bulkInsert(Uri url, ContentValues[] values) {
        int result = 0;
        String table = simpleGetTable(url);
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        db.beginTransaction();
        try {
            for (ContentValues data : values) {
                try {
                    db.insertWithOnConflict(table, null, data,
                            SQLiteDatabase.CONFLICT_REPLACE);
                    result++;
                } catch (SQLiteConstraintException e) {
                    throw e;
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        getContext().getContentResolver().notifyChange(url, null);

        return result;
    }

    private void notifyURI(Uri uri) {
        getContext().getContentResolver().notifyChange(uri, null);
    }

    private enum QueryId {
        NONE, DB_BOOKMARK_IMAGES
    }
}
