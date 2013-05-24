package com.marakana.android.logcontentprovider;

import android.net.Uri;
import android.provider.BaseColumns;

public final class LogContract {

	public static final String AUTHORITY = "com.marakana.android.logcontentprovider";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/logs");
	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.marakana.android.logcontentprovider.log";
	public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.marakana.android.logcontentprovider.log";

	public static final class Columns {

		public static final String ID = BaseColumns._ID;
		public static final String PRIORITY = "priority";
		public static final String TAG = "tag";
		public static final String MESSAGE = "message";
		public static final String CREATED = "created";

		public static final String DEFAULT_SORT_ORDER = CREATED + " DESC";

		private Columns() {

		}
	}

	private LogContract() {

	}
}
