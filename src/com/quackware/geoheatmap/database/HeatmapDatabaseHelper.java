package com.quackware.geoheatmap.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HeatmapDatabaseHelper extends SQLiteOpenHelper {
	
	private static final String DATABASE_NAME = "HeatMapDB";
	private static final String GPS_TABLE_NAME = "t_gpsData";
	private static final int DATABASE_VERSION = 1;
	
	public static boolean rebuild = true;
	
	private static final String GPS_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + GPS_TABLE_NAME
			+ " (_id INTEGER PRIMARY KEY, LATITUDE DOUBLE, LONGITUDE DOUBLE);";
	
	public HeatmapDatabaseHelper(Context context)
	{
		super(context,DATABASE_NAME,null,DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL(GPS_TABLE_CREATE);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		if(oldVersion < DATABASE_VERSION && rebuild)
		{
			db.execSQL("DROP TABLE IF EXISTS t_gpsData");
			onCreate(db);
		}
		
	}

}
