package com.marakana.android.logcontentprovider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

class LogDb extends SQLiteOpenHelper {
	private static final String TAG = "LogDb";
	private static final String DB_NAME = "logs.db";
	private static final int DB_VERSION = 1;
	public static final String LOGS_TABLE = "logs";

	public LogDb(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		StringBuilder sql = new StringBuilder();
		sql.append("CREATE TABLE ").append(LOGS_TABLE).append('(');
		sql.append(LogContract.Columns.ID).append(
				" LONG PRIMARY KEY NOT NULL, ");
		sql.append(LogContract.Columns.PRIORITY).append(" INT NOT NULL, ");
		sql.append(LogContract.Columns.TAG).append(" TEXT NOT NULL, ");
		sql.append(LogContract.Columns.MESSAGE).append(" TEXT NOT NULL, ");
		sql.append(LogContract.Columns.CREATED).append(" LONG NOT NULL");
		sql.append(')');
		Log.d(TAG, "Creating schema: " + sql);
		db.execSQL(sql.toString());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(TAG, "Upgrading schema from " + oldVersion + " to " + newVersion);
		// change if you don't want to loose your data
		db.execSQL("DROP TABLE " + LOGS_TABLE);
		onCreate(db);
	}
}
