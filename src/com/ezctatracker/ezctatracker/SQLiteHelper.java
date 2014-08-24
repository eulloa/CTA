package com.ezctatracker.ezctatracker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {
	//Table Name
	public static final String TABLE_ROUTES = "routes";
	
	//Table Columns
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_ROUTE_NAME = "routeName";
	public static final String COLUMN_ROUTE_TYPE = "routeType";
	
	//DB Name and Version
	private static final String DATABASE_NAME = "routes.db";
	private static final int DATABASE_VERSION = 1;
	
	//Create DB Statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_ROUTES + "(" + COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_ROUTE_NAME + " text not null, " 
			+ COLUMN_ROUTE_TYPE + " text);";	

	public SQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROUTES);
		onCreate(db);
	}

}