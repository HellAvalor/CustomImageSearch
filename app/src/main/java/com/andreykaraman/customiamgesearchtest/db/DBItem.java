package com.andreykaraman.customiamgesearchtest.db;

import android.content.ContentValues;
import android.database.Cursor;

public interface DBItem {

    String getTableName();

    String getCreateQuery();

    long getId();

    Long getIdAsObject();

    void fillFromCursor(Cursor cursor);

    ContentValues getPictureUrl();

}
