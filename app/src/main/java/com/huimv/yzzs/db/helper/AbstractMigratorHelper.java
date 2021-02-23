package com.huimv.yzzs.db.helper;

import android.database.sqlite.SQLiteDatabase;

public abstract class AbstractMigratorHelper {
	public abstract void onUpgrade(SQLiteDatabase db);
}
