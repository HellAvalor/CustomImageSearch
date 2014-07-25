package com.andreykaraman.idstest;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.andreykaraman.idstest.adapters.BookmarksAdapter;
import com.andreykaraman.idstest.db.DBBookmarkPictures;
import com.andreykaraman.idstest.db.DBHelper;
import com.andreykaraman.idstest.utils.Constants;

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
			resultList.setOnItemClickListener(
					new AdapterView.OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							Log.d("ItemListener", position + " pressed");
							Cursor cursor = (Cursor) parent.getItemAtPosition(position);
							startActivity(new Intent(getActivity(), FullPhotoPreview.class).putExtra(Constants.CONST_FULL_URL, cursor.getString(cursor.getColumnIndex(DBBookmarkPictures.PICTURE_URL)))
											.putExtra(Constants.CONST_TITLE, cursor.getString(cursor.getColumnIndex(DBBookmarkPictures.PICTURE_TITLE)))
							);
						}
					}
			);
			resultList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
			resultList.setMultiChoiceModeListener(new MultiSelectListener());
		}

		@Override
		public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
			String[] projection = {DBBookmarkPictures.PICTURE_ID, DBBookmarkPictures.PICTURE_TITLE,
					DBBookmarkPictures.PICTURE_URL};

			CursorLoader cursorLoader = new CursorLoader(getActivity(),
					MyContentProvider.URI_BOOKMARK_TABLE, projection, null, null, null);
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

		private void delete() {

			Intent intent = new Intent(getActivity(), DBHelper.class)
					.putExtra(Constants.CONST_DB_QUERY, R.id.delete_bookmarks)
					.putExtra(Constants.CONST_IMAGE_ID,
							resultList.getCheckedItemIds());
			getActivity().startService(intent);

		}

		private class MultiSelectListener implements AbsListView.MultiChoiceModeListener {
			@Override
			public void onItemCheckedStateChanged(ActionMode mode, int position,
			                                      long id, boolean checked) {
				// Capture total checked items
				final int checkedCount = resultList.getCheckedItemCount();
				// Set the CAB title according to total checked items
				//mode.setTitle(checkedCount + " Selected");
				switch (checkedCount) {
					case 0:
						mode.setTitle(null);
						break;
					case 1:
						mode.setTitle("One item selected");
						break;
					default:
						mode.setTitle(checkedCount + " items selected");
						break;
				}
			}

			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				switch (item.getItemId()) {
					case R.id.item_delete:
						delete();
						// Close CAB
						mode.finish();
						return true;
					default:
						return false;
				}
			}

			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				mode.getMenuInflater().inflate(R.menu.menu_action, menu);
				return true;
			}

			@Override
			public void onDestroyActionMode(ActionMode mode) {
				resultList.clearChoices();
			}

			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				return false;
			}
		}
	}


}