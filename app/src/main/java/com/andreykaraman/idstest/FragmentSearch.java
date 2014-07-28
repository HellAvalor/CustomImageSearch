package com.andreykaraman.idstest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.andreykaraman.idstest.adapters.GoogleImageBean;
import com.andreykaraman.idstest.adapters.SearchAdapter;
import com.andreykaraman.idstest.utils.Constants;

import org.apache.http.conn.util.InetAddressUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;


public class FragmentSearch extends SherlockFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_search_holder);

		if (savedInstanceState == null) {
			// Do first time initialization -- add initial fragment.
			Fragment newFragment = SearchFragment.newInstance();
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			ft.add(R.id.holder_fragment, newFragment).commit();

		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

//	public static class HolderFragment extends SherlockFragment {
//
//		@Override
//		public void onCreate(Bundle savedInstanceState) {
//			super.onCreate(savedInstanceState);
//
//		}
//	}


//	void addFragmentToStack() {
//	//	mStackLevel++;
//
//		// Instantiate a new fragment.
//		Fragment newFragment = SearchFragment.newInstance();
//
//		// Add the fragment to the activity, pushing this transaction
//		// on to the back stack.
//		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//		ft.replace(R.id.holder_fragment, newFragment);
//		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//		ft.addToBackStack(null);
//		ft.commit();
//	}

	public static class SearchFragment extends SherlockFragment {

		private EditText searchText;
		private ImageView searchButton;
		private ListView resultList;
		private SearchAdapter adapter;
		private ArrayList<Object> listImages;
		private String strSearch;
		private int page = 0;
		private boolean newSearch = false;
		private getImagesTask loadingTask;

		public SearchFragment() {
		}

		public static SearchFragment newInstance() {
			return new SearchFragment();
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
		                         Bundle savedInstanceState) {
			View v = inflater.inflate(R.layout.fragment_search, container, false);

			searchText = (EditText) v.findViewById(R.id.editTextSearchQuery);
			searchButton = (ImageView) v.findViewById(R.id.imageViewSearch);
			resultList = (ListView) v.findViewById(R.id.listViewSearchResults);

			return v;
		}

		@Override
		public void onViewCreated(View view, Bundle savedInstanceState) {
			super.onViewCreated(view, savedInstanceState);
			searchButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					strSearch = searchText.getText().toString();
					strSearch = Uri.encode(strSearch);
					newSearch = true;
					page = 0;
					Log.d("Search", "Search string => " + strSearch);
					loadingTask = new getImagesTask();
					loadingTask.execute();
				}
			});
			resultList.setOnScrollListener(new AbsListView.OnScrollListener() {
				@Override
				public void onScrollStateChanged(AbsListView view, int scrollState) {

				}

				@Override
				public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
					boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;

					if (loadMore && (newSearch || page > 0) && loadingTask.getStatus() == AsyncTask.Status.FINISHED) {
						newSearch = false;
						page++;
						loadingTask = new getImagesTask();
						loadingTask.execute();
					}
				}
			});

			resultList.setOnItemClickListener(
					new AdapterView.OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							Log.d("ItemListener", position + " pressed");
							startActivity(new Intent(getActivity(), FullPhotoPreview.class).putExtra(Constants.CONST_FULL_URL, ((GoogleImageBean) listImages.get(position)).getFullUrl())
											.putExtra(Constants.CONST_TITLE, ((GoogleImageBean) listImages.get(position)).getTitle())
							);
						}
					}
			);
		}

		public void SetListViewAdapter(ArrayList<Object> images) {
			int index = resultList.getFirstVisiblePosition();
			int top = (resultList.getChildAt(0) == null) ? 0 : resultList.getChildAt(0).getTop();
			Log.d("SetListViewAdapter", "index " + index + " top " + top);
			adapter = new SearchAdapter(getActivity(), images);
			resultList.setAdapter(adapter);

			resultList.setSelectionFromTop(index, top);
		}

		public ArrayList<Object> getImageList(JSONArray resultArray) {
			ArrayList<Object> listImages = new ArrayList<Object>();
			GoogleImageBean bean;

			try {
				for (int i = 0; i < resultArray.length(); i++) {
					JSONObject obj;
					obj = resultArray.getJSONObject(i);
					bean = new GoogleImageBean();

					bean.setTitle(obj.getString("titleNoFormatting"));
					bean.setThumbUrl(obj.getString("tbUrl"));
					bean.setFullUrl(obj.getString("url"));
					bean.setBookmarked(false);
					Log.d("Search", "Thumb URL => " + obj.getString("tbUrl"));

					listImages.add(bean);

				}
				return listImages;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		public String getLocalIpAddress() {
			String ipv4;

			try {
				for (Enumeration<NetworkInterface> en = NetworkInterface
						.getNetworkInterfaces(); en.hasMoreElements(); ) {
					NetworkInterface intf = en.nextElement();
					for (Enumeration<InetAddress> enumIpAddr = intf
							.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
						InetAddress inetAddress = enumIpAddr.nextElement();
						System.out.println("ip1--:" + inetAddress);
						System.out.println("ip2--:" + inetAddress.getHostAddress());

						// for getting IPV4 format
						if (!inetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(ipv4 = inetAddress.getHostAddress())) {

							// return inetAddress.getHostAddress().toString();
							return ipv4;
						}
					}
				}
			} catch (Exception ex) {
				Log.e("IP Address", ex.toString());
			}
			return null;
		}

		public class getImagesTask extends AsyncTask<Void, Void, Void> {
			JSONObject json;
			ProgressDialog dialog;

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();

				dialog = ProgressDialog.show(getActivity(), "", "Please wait...");
			}

			@Override
			protected Void doInBackground(Void... params) {
				// TODO Auto-generated method stub

				URL url;
				try {
					url = new URL("https://ajax.googleapis.com/ajax/services/search/images?" +
							"v=1.0&q=" + strSearch + "&rsz=8"
							+ "&key=AIzaSyCfl7cx6oOkvbq9mFUiF12yni2V7ZelgWk"
							+ "&start=" + (page * 8)
							+ "&userip=" + getLocalIpAddress());

					Log.d("Get request", "url string => " + url);
					URLConnection connection = url.openConnection();
					connection.addRequestProperty("Referer", "http://andreykaraman.com/");

					String line;
					StringBuilder builder = new StringBuilder();
					BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					while ((line = reader.readLine()) != null) {
						builder.append(line);
					}

					Log.d("Get request", "Builder string => " + builder.toString());

					json = new JSONObject(builder.toString());
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);

				if (dialog.isShowing()) {
					dialog.dismiss();
				}

				try {
					JSONObject responseObject = json.getJSONObject("responseData");
					JSONArray resultArray = responseObject.getJSONArray("results");

					if (listImages == null || page == 0) {
						listImages = getImageList(resultArray);

					} else {
						listImages.addAll(getImageList(resultArray));
					}

					SetListViewAdapter(listImages);

					Log.d("Parse JSON", "Result array length => " + resultArray.length());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	}


}