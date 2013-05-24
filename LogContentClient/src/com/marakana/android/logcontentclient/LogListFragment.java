package com.marakana.android.logcontentclient;

import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;

public class LogListFragment extends ListFragment implements
		LoaderCallbacks<Cursor> {
	private static final String TAG = "LogListFragment";

	private static final String[] PROJECTION = new String[] {
			LogProviderContract.ID, LogProviderContract.PRIORITY,
			LogProviderContract.TAG, LogProviderContract.MESSAGE };

	private static final String[] FROM_COLUMNS_NAMES = {
			LogProviderContract.PRIORITY, LogProviderContract.TAG,
			LogProviderContract.MESSAGE };

	private static final int[] TO_VIEW_IDS = { R.id.priority, R.id.tag,
			R.id.message };

	private SimpleCursorAdapter simpleCursorAdapter;

	private boolean dualPane;

	private Uri currentUri = null;

	private int currentPosition = 0;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.d(TAG, "onActivityCreated(" + savedInstanceState + ")");
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState != null) {
			this.currentUri = savedInstanceState.getParcelable("currentUri");
			this.currentPosition = savedInstanceState.getInt("currentPosition");
		}
		View detailsFrame = super.getActivity().findViewById(R.id.log_details);
		this.dualPane = detailsFrame != null
				&& detailsFrame.getVisibility() == View.VISIBLE;
		super.getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		this.simpleCursorAdapter = new SimpleCursorAdapter(getActivity()
				.getApplicationContext(), R.layout.log_list_row, null,
				FROM_COLUMNS_NAMES, TO_VIEW_IDS, 0);

		final String[] logLevels = super.getActivity().getResources()
				.getStringArray(R.array.log_level_labels);

		this.simpleCursorAdapter.setViewBinder(new ViewBinder() {

			@Override
			public boolean setViewValue(View view, Cursor cursor,
					int columnIndex) {
				if (view.getId() == R.id.priority) {
					int priority = cursor.getInt(columnIndex);
					String priorityLabel = logLevels[priority];
					((TextView) view).setText(priorityLabel);
					return true;
				}
				return false;
			}
		});

		super.setListAdapter(this.simpleCursorAdapter);
		super.getLoaderManager().initLoader(0, null, this);

		if (this.dualPane) {
			this.showDetails(this.currentUri, this.currentPosition);
		} else {
			LogDetailsFragment f = (LogDetailsFragment) super
					.getFragmentManager().findFragmentById(R.id.log_details);
			if (f != null) {
				super.getFragmentManager().beginTransaction().remove(f)
						.commit();
			}
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		Log.d(TAG, "onSaveInstanceState(...)");
		super.onSaveInstanceState(outState);
		outState.putParcelable("currentUri", this.currentUri);
		outState.putInt("currentPosition", this.currentPosition);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView(...)");
		return inflater.inflate(R.layout.log_list_fragment, container, false);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Log.d(TAG, "onListItemClick(" + l + ", " + v + ", " + position + ", "
				+ id + ")");
		this.showDetails(
				ContentUris.withAppendedId(LogProviderContract.CONTENT_URI, id),
				position);
	}

	private void showDetails(Uri currentUri, int currentPosition) {
		Log.d(TAG, "showDetails(" + currentUri + ", " + currentPosition + ")");
		this.currentUri = currentUri;
		this.currentPosition = currentPosition;
		if (this.currentUri != null) {
			if (this.dualPane) {
				super.getListView().setItemChecked(this.currentPosition, true);
				LogDetailsFragment detailsFragment = (LogDetailsFragment) super
						.getFragmentManager()
						.findFragmentById(R.id.log_details);
				if (detailsFragment == null
						|| !currentUri.equals(detailsFragment.getUri())) {
					detailsFragment = LogDetailsFragment
							.newInstance(currentUri);
					super.getFragmentManager()
							.beginTransaction()
							.replace(R.id.log_details, detailsFragment)
							.setTransition(
									FragmentTransaction.TRANSIT_FRAGMENT_FADE)
							.commit();
				}
			} else {
				Intent intent = new Intent();
				intent.setClass(super.getActivity().getApplicationContext(),
						LogDetailsActivity.class);
				intent.putExtra("uri", currentUri);
				super.startActivity(intent);
			}
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Log.d(TAG, "onCreateLoader(" + id + ", " + args + ")");
		return new CursorLoader(super.getActivity().getApplicationContext(),
				LogProviderContract.CONTENT_URI, PROJECTION, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		Log.d(TAG, "onLoadFinished(...)");
		this.simpleCursorAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		Log.d(TAG, "onLoaderReset(...)");
		this.simpleCursorAdapter.swapCursor(null);
	}
}
