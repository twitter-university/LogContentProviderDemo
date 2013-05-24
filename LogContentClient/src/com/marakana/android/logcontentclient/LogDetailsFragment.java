package com.marakana.android.logcontentclient;

import java.text.DateFormat;
import java.util.Date;

import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class LogDetailsFragment extends Fragment implements
		LoaderCallbacks<Cursor> {
	private static final String TAG = "LogDetailsFragment";

	public static LogDetailsFragment newInstance(Uri uri) {
		Log.d(TAG, "newInstance(" + uri + ")");
		LogDetailsFragment detailsFragment = new LogDetailsFragment();
		Bundle args = new Bundle();
		args.putParcelable("uri", uri);
		detailsFragment.setArguments(args);
		return detailsFragment;
	}

	private TextView priorityView;
	private TextView createdView;
	private TextView tagView;
	private TextView messageView;

	public Uri getUri() {
		return getArguments().getParcelable("uri");
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView(...)");
		if (container == null) {
			return null;
		}
		View view = inflater.inflate(R.layout.log_details_fragment, container,
				false);
		this.priorityView = (TextView) view.findViewById(R.id.priority);
		this.createdView = (TextView) view.findViewById(R.id.created);
		this.tagView = (TextView) view.findViewById(R.id.tag);
		this.messageView = (TextView) view.findViewById(R.id.message);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.d(TAG, "onActivityCreated(" + savedInstanceState + ")");
		super.onActivityCreated(savedInstanceState);
		super.getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Log.d(TAG, "onCreateLoader(" + id + ", " + args + ")");
		Context context = super.getActivity().getApplicationContext();
		return new CursorLoader(context, this.getUri(), null, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		Log.d(TAG, "onLoadFinished(" + loader.getId() + ", ...)");
		if (data.moveToFirst()) {
			long id = data.getLong(data.getColumnIndex(LogProviderContract.ID));
			int priority = data.getInt(data
					.getColumnIndex(LogProviderContract.PRIORITY));
			String tag = data.getString(data
					.getColumnIndex(LogProviderContract.TAG));
			String message = data.getString(data
					.getColumnIndex(LogProviderContract.MESSAGE));
			long createdMillis = data.getLong(data
					.getColumnIndex(LogProviderContract.CREATED));

			String created = DateFormat.getDateTimeInstance(DateFormat.LONG,
					DateFormat.LONG,
					super.getResources().getConfiguration().locale).format(
					new Date(createdMillis));

			String[] logLevels = super.getActivity().getResources()
					.getStringArray(R.array.log_level_labels);

			Log.d(TAG, "Loaded log item " + id);
			this.priorityView.setText(logLevels[priority]);
			this.createdView.setText(created);
			this.tagView.setText(tag);
			this.messageView.setText(message);
		} else {
			Log.d(TAG, "No log details");
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		Log.d(TAG, "onLoaderReset(" + loader.getId() + ", ...)");
		this.priorityView.setText("");
		this.createdView.setText("");
		this.tagView.setText("");
		this.messageView.setText("");
	}
}
