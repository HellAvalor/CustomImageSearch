package com.andreykaraman.idstest.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.andreykaraman.idstest.R;
import com.andreykaraman.idstest.utils.ImageLoader;

public class ListViewImageAdapter extends BaseAdapter {
    
	private Activity activity;
	public ArrayList<Object> listImages;
	private static LayoutInflater inflater=null;
	public ImageLoader imageLoader; 
	
    public ListViewImageAdapter(Activity a, ArrayList<Object> listImages) {
        activity = a;
        this.listImages = listImages;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new ImageLoader(activity.getApplicationContext());
    }

    public int getCount() {
        return listImages.size();
    }

    public Object getItem(int position) {
    	return listImages.get(position);
    }

    public long getItemId(int position) 
    {
    	return position;
    }
    
    public static class ViewHolder{
    	public ImageView imgViewImage;
    	public TextView txtViewTitle;
    	
    }

    public View getView(int position, View convertView, ViewGroup parent) {
    	View vi=convertView;
    	ViewHolder holder;
		if(convertView==null){
			vi = inflater.inflate(R.layout.item_search, null);
			holder=new ViewHolder();
			
			holder.imgViewImage=(ImageView)vi.findViewById(R.id.imagePreview);
			holder.txtViewTitle=(TextView)vi.findViewById(R.id.textPictureName);
			
			vi.setTag(holder);
		}
		else
			holder=(ViewHolder)vi.getTag();
		
		GoogleImageBean imageBean = (GoogleImageBean) listImages.get(position);
		holder.imgViewImage.setTag(imageBean.getThumbUrl());
		imageLoader.DisplayImage(imageBean.getThumbUrl(), activity, holder.imgViewImage);
		
		holder.txtViewTitle.setText(Html.fromHtml(imageBean.getTitle()));
		return vi;
    }
}