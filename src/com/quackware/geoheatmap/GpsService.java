package com.quackware.geoheatmap;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

//Request gps and network provider.
//If receive network provider, wait 1 minute and see if we get gps provider.
//If we get gps provider in that time, use gps, else use network provider.
public class GpsService extends Service implements LocationListener {

	private LocationManager mLocationManager;
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		mLocationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
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
		}
		else if(location.getProvider().equals(LocationManager.NETWORK_PROVIDER))
		{
			
		}
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
		GpsService getService()
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
