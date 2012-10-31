/*
 * GeoHeatMap - Heatmap generator based off of gps locations
 * Copyright (C) 2012 Curtis Larson
 *
 *
 * This file is part of GeoHeatMap.
 *
 * GeoHeatMap is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package com.quackware.geoheatmap.database;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.quackware.geoheatmap.map.HeatPoint;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HeatmapDatabaseHelper extends SQLiteOpenHelper {
	
	private static final String DATABASE_NAME = "HeatMapDB";
	private static final String GPS_TABLE_NAME = "t_gpsData";
	private static final int DATABASE_VERSION = 1;
	
	public static boolean rebuild = true;
	
	private static final String GPS_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + GPS_TABLE_NAME
			+ " (_id INTEGER PRIMARY KEY, LATITUDE DOUBLE, LONGITUDE DOUBLE, TIMESTAMP DATE);";
	
	public HeatmapDatabaseHelper(Context context)
	{
		super(context,DATABASE_NAME,null,DATABASE_VERSION);
	}
	
	public ArrayList<HeatPoint> getHeatPointList()
	{
		ArrayList<HeatPoint> points = new ArrayList<HeatPoint>();
		
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(GPS_TABLE_NAME, new String[] {"LATITUDE","LONGITUDE"},
				null, null, null, null, null);
		if(cursor == null)
		{
			return points;
		}
		if(cursor.moveToFirst())
		{
			do
			{
				double lat = cursor.getDouble(0);
				double lon = cursor.getDouble(1);
				String timestamp = cursor.getString(2);
				HeatPoint hp = new HeatPoint((float)lat,(float)lon,timestamp);
				points.add(hp);
			}
			while(cursor.moveToNext());
		}
		return points;
	}
	
	public void insertNewGpsData(double latitude, double longitude)
	{
		SQLiteDatabase db = getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put("LATITUDE",latitude);
		values.put("LONGITUDE",longitude);
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		values.put("TIMESTAMP",dateFormat.format(date));
		
		db.insert(GPS_TABLE_NAME, null, values);
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
