package com.marakana.android.logcontentclient;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.os.StrictMode.VmPolicy;
import android.util.Log;
import android.view.Menu;

public class LogListActivity extends Activity {
	private static final String TAG = "LogDetailsActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate()");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.log_list_activity);

		StrictMode.setThreadPolicy(new ThreadPolicy.Builder().detectAll()
				.penaltyLog().build());
		StrictMode.setVmPolicy(new VmPolicy.Builder()
				.detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
				.penaltyLog().penaltyDeath().build());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.log_list, menu);
		return true;
	}
}
