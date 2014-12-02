package com.andreykaraman.idstest.adapters;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.andreykaraman.idstest.R;
import com.andreykaraman.idstest.utils.ImageLoader;

public class BookmarksAdapter extends CursorAdapter {
    public ImageLoader imageLoader;
    private Activity activity;

    public BookmarksAdapter(Context context, Cursor c, boolean autoRequery, Activity activity) {
        super(context, c, autoRequery);
        this.activity = activity;
        imageLoader = new ImageLoader(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View retView = inflater.inflate(R.layout.item_bookmarked, parent,
                false);
        RowViewHolder holder = new RowViewHolder();

        holder.imageTitle = (TextView) retView.findViewById(R.id.textPictureName);
        holder.imageView = (ImageView) retView
                .findViewById(R.id.imagePreview);

        retView.setTag(holder);
        return retView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        RowViewHolder holder = (RowViewHolder) view.getTag();
        holder.imageTitle.setText(cursor.getString(cursor.getColumnIndex(cursor
                .getColumnName(1))));

        holder.imageView.setTag(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(2))));
        imageLoader.displayImage(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(2))), activity, holder.imageView);
    }

    protected static class RowViewHolder {
        public TextView imageTitle;
        public ImageView imageView;

    }
}
