package com.marakana.android.logcontentprovider;

import java.util.Arrays;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class LogContentProvider extends ContentProvider {
	private static final String TAG = "LogContentProvider";

	private static final int LOGS_MATCH = 1;
	private static final int LOG_ID_MATCH = 2;

	private static final UriMatcher URI_MATCHER = new UriMatcher(
			UriMatcher.NO_MATCH);
	static {
		URI_MATCHER.addURI(LogContract.AUTHORITY, "logs", LOGS_MATCH);
		URI_MATCHER.addURI(LogContract.AUTHORITY, "logs/#", LOG_ID_MATCH);
	}

	private LogDb logDb;

	@Override
	public boolean onCreate() {
		Log.d(TAG, "onCreate()");
		this.logDb = new LogDb(super.getContext());
		return true;
	}

	@Override
	public String getType(Uri uri) {
		Log.d(TAG, "getType(" + uri + ")");
		switch (URI_MATCHER.match(uri)) {
		case LOGS_MATCH:
			return LogContract.CONTENT_TYPE;
		case LOG_ID_MATCH:
			return LogContract.CONTENT_ITEM_TYPE;
		default:
			throw new IllegalArgumentException(
					"Cannot determine type. Unsupported uri: " + uri);
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		Log.d(TAG, "query(" + uri + "," + Arrays.toString(projection) + ","
				+ selection + "," + Arrays.toString(selectionArgs) + ","
				+ sortOrder + ")");
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setStrict(true);
		qb.setTables(LogDb.LOGS_TABLE);

		switch (URI_MATCHER.match(uri)) {
		case LOGS_MATCH:
			if (TextUtils.isEmpty(sortOrder)) {
				sortOrder = LogContract.Columns.DEFAULT_SORT_ORDER;
			}
			break;
		case LOG_ID_MATCH:
			qb.appendWhere(LogContract.Columns.ID + "="
					+ uri.getLastPathSegment());
			break;
		default:
			throw new IllegalArgumentException(
					"Cannot query. Unsupported uri: " + uri);
		}
		SQLiteDatabase db = this.logDb.getReadableDatabase();
		Cursor cursor = qb.query(db, projection, selection, selectionArgs,
				null, null, sortOrder);
		cursor.setNotificationUri(super.getContext().getContentResolver(), uri);
		return cursor;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		Log.d(TAG, "insert(" + uri + "," + values + ")");
		if (URI_MATCHER.match(uri) != LOGS_MATCH) {
			throw new IllegalArgumentException(
					"Cannot insert. Unsupported uri: " + uri);
		}
		SQLiteDatabase db = this.logDb.getWritableDatabase();
		long rowId = db.insert(LogDb.LOGS_TABLE, null, values);
		if (rowId == -1) {
			Log.d(TAG, "Failed to insert " + values + " to " + uri);
			return null;
		} else {
			Uri itemUri = ContentUris.withAppendedId(LogContract.CONTENT_URI,
					rowId);
			super.getContext().getContentResolver().notifyChange(itemUri, null);
			return itemUri;
		}
	}

	private String getSelectionForUri(Uri uri, String selection) {
		switch (URI_MATCHER.match(uri)) {
		case LOGS_MATCH:
			return selection;
		case LOG_ID_MATCH:
			String idSelection = LogContract.Columns.ID + "="
					+ uri.getLastPathSegment();
			return selection == null ? idSelection : selection + " AND ("
					+ idSelection + ")";
		default:
			throw new IllegalArgumentException("Unsupported uri: " + uri);
		}
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		Log.d(TAG,
				"delete(" + uri + "," + selection + ","
						+ Arrays.toString(selectionArgs) + ")");
		SQLiteDatabase db = this.logDb.getWritableDatabase();
		selection = this.getSelectionForUri(uri, selection);
		int count = db.delete(LogDb.LOGS_TABLE, selection, selectionArgs);
		if (count > 0) {
			super.getContext().getContentResolver().notifyChange(uri, null);
		}
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		Log.d(TAG, "update(" + uri + "," + values + "," + selection + ","
				+ Arrays.toString(selectionArgs) + ")");
		SQLiteDatabase db = this.logDb.getWritableDatabase();
		selection = this.getSelectionForUri(uri, selection);
		int count = db.update(LogDb.LOGS_TABLE, values, selection,
				selectionArgs);
		if (count > 0) {
			super.getContext().getContentResolver().notifyChange(uri, null);
		}
		return count;
	}
}
