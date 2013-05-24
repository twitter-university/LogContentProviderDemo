package com.marakana.android.logcontentclient;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

public class LogDetailsActivity extends Activity {
	private static final String TAG = "LogDetailsActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate()");
		super.onCreate(savedInstanceState);
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			super.finish();
		} else if (savedInstanceState == null) {
			LogDetailsFragment contactDetailsFragment = new LogDetailsFragment();
			contactDetailsFragment.setArguments(getIntent().getExtras());
			super.getFragmentManager().beginTransaction()
					.add(android.R.id.content, contactDetailsFragment).commit();
		}
	}
}
