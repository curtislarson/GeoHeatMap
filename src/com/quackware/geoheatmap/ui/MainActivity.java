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
package com.quackware.geoheatmap.ui;

import com.quackware.geoheatmap.R;
import com.quackware.geoheatmap.R.id;
import com.quackware.geoheatmap.R.layout;
import com.quackware.geoheatmap.service.GpsService;
import com.quackware.geoheatmap.service.GpsService.GpsBinder;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {
	
	private GpsService mBoundService;
	private boolean mIsBound;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        setupListeners();
    }
    
    private void setupListeners()
    {
    	((Button)findViewById(R.id.start)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view) {
				doBindService();
				
			}});
    	((Button)findViewById(R.id.stop)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view) {
				doUnbindService();
			}});
    	((Button)findViewById(R.id.map)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view) {
				Intent intent = new Intent(MainActivity.this,HeatmapActivity.class);
				startActivity(intent);
			}});
    	((Button)findViewById(R.id.prefs)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view) {
				Intent intent = new Intent(MainActivity.this,MyPreferenceActivity.class);
				startActivity(intent);
			}});
    }
    
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            mBoundService = ((GpsService.GpsBinder)service).getService();
        }

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mBoundService = null;
		}
    };
    

    private void doUnbindService()
    {
    	if(mIsBound)
    	{
    		mIsBound = false;
    		unbindService(mConnection);
    	}
    }
    private void doBindService()
    {
    	Intent intent = new Intent(MainActivity.this,GpsService.class);
    	bindService(intent,mConnection,Context.BIND_AUTO_CREATE);
    	mIsBound = true;
    }
    
}