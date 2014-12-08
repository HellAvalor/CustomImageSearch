package com.andreykaraman.customiamgesearchtest.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.andreykaraman.customiamgesearchtest.R;
import com.andreykaraman.customiamgesearchtest.db.DBService;
import com.andreykaraman.customiamgesearchtest.utils.Constants;
import com.andreykaraman.customiamgesearchtest.utils.ImageLoader;
import com.andreykaraman.customiamgesearchtest.utils.Utils;

import java.util.ArrayList;

public class SearchAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    public ArrayList<ImageObj> listImages;
    public ImageLoader imageLoader;
    private Activity activity;

    public SearchAdapter(Activity activity, ArrayList<ImageObj> listImages) {
        this.activity = activity;
        this.listImages = listImages;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ImageLoader(activity.getApplicationContext());
    }

    public int getCount() {
        return listImages.size();
    }

    public Object getItem(int position) {
        return listImages.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        final ImageObj imageBean = listImages.get(position);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.fragment_full_image_view, null);
            holder = getViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.bookmark.setChecked(imageBean.isBookmarked());
        Utils.changeCheckBox(holder.bookmark, imageBean.isBookmarked());
        holder.bookmark.setTag(imageBean.getThumbUrl());
        holder.bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (imageBean.isBookmarked()) {
                    Intent intent = new Intent(activity, DBService.class)
                            .putExtra(Constants.CONST_DB_QUERY, R.id.delete_bookmark_by_url)
                            .putExtra(Constants.CONST_FULL_URL, listImages.get(position).getFullUrl());
                    activity.startService(intent);
                } else {
                    Intent intent = new Intent(activity, DBService.class)
                            .putExtra(Constants.CONST_DB_QUERY, R.id.add_bookmark)
                            .putExtra(Constants.CONST_TITLE, listImages.get(position).getTitle())
                            .putExtra(Constants.CONST_FULL_URL, listImages.get(position).getFullUrl());
                    activity.startService(intent);
                }

                imageBean.setBookmarked(!imageBean.isBookmarked());
                Utils.changeCheckBox(holder.bookmark, imageBean.isBookmarked());
            }
        });

        holder.image.setTag(imageBean.getThumbUrl());
        imageLoader.bindImage(imageBean.getThumbUrl(), activity, holder.image);
        holder.title.setText(Html.fromHtml(imageBean.getTitle()));

        return convertView;
    }


    private ViewHolder getViewHolder(View convertView) {
        ViewHolder holder = new ViewHolder();
        holder.image = (ImageView) convertView.findViewById(R.id.picture);
        holder.title = (TextView) convertView.findViewById(R.id.pictureTitle);
        holder.bookmark = (CheckBox) convertView.findViewById(R.id.saveToBookmarks);

        return holder;
    }

    public static class ViewHolder {
        public ImageView image;
        public TextView title;
        public CheckBox bookmark;
    }
}