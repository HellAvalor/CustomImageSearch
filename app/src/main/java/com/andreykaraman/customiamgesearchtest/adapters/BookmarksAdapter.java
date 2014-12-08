package com.andreykaraman.customiamgesearchtest.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.andreykaraman.customiamgesearchtest.R;
import com.andreykaraman.customiamgesearchtest.db.DBService;
import com.andreykaraman.customiamgesearchtest.utils.Constants;
import com.andreykaraman.customiamgesearchtest.utils.ImageLoader;

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
        View retView = inflater.inflate(R.layout.fragment_full_image_view, parent,
                false);

        ViewHolder holder = getViewHolder(retView);

        retView.setTag(holder);
        return retView;
    }

    @Override
    public void bindView(View view, Context context, final Cursor cursor) {

        final ViewHolder holder = (ViewHolder) view.getTag();
        holder.bookmark.setText(R.string.saved_button);
        holder.bookmark.setChecked(true);
        holder.bookmark.setTag(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(2))));

        holder.bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(activity, DBService.class)
                        .putExtra(Constants.CONST_DB_QUERY, R.id.delete_bookmark)
                        .putExtra(Constants.CONST_IMAGE_ID,
                                cursor.getLong(0));
                activity.startService(intent);
            }
        });

        holder.image.setTag(cursor.getString(2));
        imageLoader.bindImage(cursor.getString(2), activity, holder.image);
        holder.title.setText(Html.fromHtml(cursor.getString(1)));

    }

    private ViewHolder getViewHolder(View convertView) {
        ViewHolder holder = new ViewHolder();
        holder.image = (ImageView) convertView.findViewById(R.id.picture);
        holder.title = (TextView) convertView.findViewById(R.id.pictureTitle);
        holder.bookmark = (CheckBox) convertView.findViewById(R.id.saveToBookmarks);
        return holder;
    }

    protected static class ViewHolder {
        public TextView title;
        public ImageView image;
        public CheckBox bookmark;

    }
}
