package com.andreykaraman.idstest;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.andreykaraman.idstest.adapters.BookmarksAdapter;
import com.andreykaraman.idstest.db.DBBookmarkPictures;

public class FragmentBookmarks extends SherlockFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	public static class BookmarksFragment extends SherlockFragment implements
			LoaderManager.LoaderCallbacks<Cursor> {

		private ListView resultList;
		private BookmarksAdapter adapter;
		private LoaderManager.LoaderCallbacks<Cursor> mCallbacks;

		public BookmarksFragment() {
		}

		public static BookmarksFragment newInstance() {
			return new BookmarksFragment();
		}

		/**
		 * When creating, retrieve this instance's number from its arguments.
		 */
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
		}

		/**
		 * The Fragment's UI is just a simple text view showing its
		 * instance number.
		 */
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
		                         Bundle savedInstanceState) {
			View v = inflater.inflate(R.layout.fragment_bookmarks, container, false);
			return v;
		}

		@Override
		public void onViewCreated(View view, Bundle savedInstanceState) {
			super.onViewCreated(view, savedInstanceState);

			adapter = new BookmarksAdapter(getActivity(), null, true, getActivity());

			resultList = (ListView) view.findViewById(R.id.listViewBookmarks);
			resultList.setAdapter(adapter);
			fillData();
			mCallbacks = this;
			getLoaderManager().initLoader(0, null, mCallbacks);

		}

		@Override
		public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
			String[] projection = {DBBookmarkPictures.PICTURE_ID, DBBookmarkPictures.PICTURE_TITLE,
					DBBookmarkPictures.PICTURE_URL};

			CursorLoader cursorLoader = new CursorLoader(getActivity(),
					MyContentProvider.URI_NOTE_TABLE, projection, null, null, null);
			return cursorLoader;
		}

		@Override
		public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
			adapter.swapCursor(data);
		}

		@Override
		public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
			adapter.swapCursor(null);
		}

		private void fillData() {
			getLoaderManager().initLoader(0, null, this);
		}
	}

}