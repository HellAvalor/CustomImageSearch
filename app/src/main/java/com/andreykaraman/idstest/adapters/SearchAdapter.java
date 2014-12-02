package com.andreykaraman.idstest.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.andreykaraman.idstest.R;
import com.andreykaraman.idstest.db.DBService;
import com.andreykaraman.idstest.utils.Constants;
import com.andreykaraman.idstest.utils.ImageLoader;

import java.util.ArrayList;

public class SearchAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    public ArrayList<Object> listImages;
    public ImageLoader imageLoader;
    private Activity activity;

    public SearchAdapter(Activity activity, ArrayList<Object> listImages) {
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

        View vi = convertView;
        ViewHolder holder;
        GoogleImageBean imageBean = (GoogleImageBean) listImages.get(position);
        if (convertView == null) {
            vi = inflater.inflate(R.layout.item_search, null);
            holder = new ViewHolder();

            holder.imgViewImage = (ImageView) vi.findViewById(R.id.imagePreview);
            holder.txtViewTitle = (TextView) vi.findViewById(R.id.textPictureName);
            holder.chSaveBookmark = (CheckBox) vi.findViewById(R.id.checkBoxSaveToBookmarks);
            vi.setTag(holder);

            holder.chSaveBookmark.setChecked(imageBean.isBookmarked());
            holder.chSaveBookmark.setTag(imageBean.getThumbUrl());
            holder.chSaveBookmark.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Toast.makeText(activity, "State " + isChecked + " " + ((GoogleImageBean) listImages.get(position)).getTitle(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(activity, DBService.class)
                            .putExtra(Constants.CONST_DB_QUERY, R.id.add_bookmark)
                            .putExtra(Constants.CONST_TITLE, ((GoogleImageBean) listImages.get(position)).getTitle())
                            .putExtra(Constants.CONST_FULL_URL, ((GoogleImageBean) listImages.get(position)).getFullUrl());
                    activity.startService(intent);
                }
            });
        } else {
            holder = (ViewHolder) vi.getTag();
        }

        holder.imgViewImage.setTag(imageBean.getThumbUrl());
        imageLoader.displayImage(imageBean.getThumbUrl(), activity, holder.imgViewImage);

        holder.txtViewTitle.setText(Html.fromHtml(imageBean.getTitle()));
        return vi;
    }

    public static class ViewHolder {
        public ImageView imgViewImage;
        public TextView txtViewTitle;
        public CheckBox chSaveBookmark;
    }


}