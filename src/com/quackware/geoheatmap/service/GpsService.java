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
package com.quackware.geoheatmap.service;

import java.util.Timer;
import java.util.TimerTask;

import com.quackware.geoheatmap.database.HeatmapDatabaseHelper;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;

//Request gps and network provider.
//If receive network provider, wait 1 minute and see if we get gps provider.
//If we get gps provider in that time, use gps, else use network provider.
public class GpsService extends Service implements LocationListener {

	private LocationManager mLocationManager;
	private Handler mHandler;
	private HeatmapDatabaseHelper mDatabaseHelper;
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		mLocationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
		mHandler = new Handler();
		mDatabaseHelper = new HeatmapDatabaseHelper(this);
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		mLocationManager.removeUpdates(this);
	}
	
	@Override
	public void onLocationChanged(Location location) {
		if(location.getProvider().equals(LocationManager.GPS_PROVIDER))
		{
			double lat = location.getLatitude();
			double lon = location.getLongitude();
			mDatabaseHelper.insertNewGpsData(lat, lon);
		}
		else if(location.getProvider().equals(LocationManager.NETWORK_PROVIDER))
		{
			
		}
		
		//Schedule another check for gps.
		//Change 1 to whatever minute value we want.
		mLocationManager.removeUpdates(this);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

		mHandler.postDelayed(mGpsRunnable,1000*60*prefs.getInt("gpsCheckTime", 1));
	}
	
	private Runnable mGpsRunnable = new Runnable()
	{

		@Override
		public void run() {
			getNewLocation();
		}
		
	};
	
	private void getNewLocation()
	{
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle bundle) {
		// TODO Auto-generated method stub
		
	}
	
	private final IBinder mBinder = new GpsBinder();
	
	public class GpsBinder extends Binder
	{
		public GpsService getService()
		{
			return GpsService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
		return null;
	}

}
