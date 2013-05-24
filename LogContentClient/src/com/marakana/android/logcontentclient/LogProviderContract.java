package com.marakana.android.logcontentclient;

import android.net.Uri;
import android.provider.BaseColumns;

public final class LogProviderContract {
	private LogProviderContract() {

	}

	public static final Uri CONTENT_URI = Uri
			.parse("content://com.marakana.android.logcontentprovider/logs");
	public static final String ID = BaseColumns._ID;
	public static final String PRIORITY = "priority";
	public static final String TAG = "tag";
	public static final String MESSAGE = "message";
	public static final String CREATED = "created";

}
