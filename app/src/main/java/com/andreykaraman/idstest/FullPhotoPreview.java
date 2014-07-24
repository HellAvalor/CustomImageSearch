package com.andreykaraman.idstest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.andreykaraman.idstest.utils.ImageLoader;

public class FullPhotoPreview extends SherlockFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_full_photo_preview);
		if (savedInstanceState == null) {
			// Do first time initialization -- add initial fragment.
			Fragment newFragment = PlaceholderFragment.newInstance();
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			ft.add(R.id.container, newFragment).commit();
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		private TextView imageText;
		private ImageView fullImage;
		private String url;
		private ImageLoader imageLoader;

		public PlaceholderFragment() {
		}

		public static PlaceholderFragment newInstance() {
			return new PlaceholderFragment();
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			imageLoader = new ImageLoader(getActivity().getBaseContext());
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
		                         Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_full_photo_preview, container, false);

			imageText = (TextView) rootView.findViewById(R.id.textViewImageText);
			fullImage = (ImageView) rootView.findViewById(R.id.imageViewFull);

			return rootView;
		}

		@Override
		public void onViewCreated(View view, Bundle savedInstanceState) {
			super.onViewCreated(view, savedInstanceState);

			Intent intent = getActivity().getIntent();
			url = intent.getStringExtra("FullImageUrl");
			imageText.setText(intent.getStringExtra("ImageTitle"));
			Log.d("PhotoPreview", "url " + url);
			fullImage.setTag(url);
			imageLoader.DisplayImage(url, getActivity(), fullImage);
		}
	}
}
