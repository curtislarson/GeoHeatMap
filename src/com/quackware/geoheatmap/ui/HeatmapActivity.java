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

import java.util.ArrayList;
import java.util.Random;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.quackware.geoheatmap.R;
import com.quackware.geoheatmap.R.id;
import com.quackware.geoheatmap.R.layout;
import com.quackware.geoheatmap.database.HeatmapDatabaseHelper;
import com.quackware.geoheatmap.map.HeatMapOverlay;
import com.quackware.geoheatmap.map.HeatPoint;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class HeatmapActivity extends MapActivity{
	
	private MapView mMapView;
	
	private final static String TAG = "HeatmapActivity";
	
	private HeatMapOverlay mOverlay;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.heatmap);
		
		mMapView = (MapView)findViewById(R.id.mapview);
		mMapView.setBuiltInZoomControls(true);

		Log.i(TAG,mMapView.getWidth() + " " + mMapView.getHeight());
		
		//loadPointsFromDatabase();
	}
	
	private void loadPointsFromDatabase()
	{
		mOverlay = new HeatMapOverlay(mMapView);

		//HeatmapDatabaseHelper mDatabaseHelper = new HeatmapDatabaseHelper(this);
		//ArrayList<HeatPoint> heatPointList = mDatabaseHelper.getHeatPointList();
		ArrayList<HeatPoint> heatPointList = generateTestHeatPoints();
		mOverlay.update(heatPointList);
		
		mMapView.getOverlays().add(mOverlay);
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus)
	{
		super.onWindowFocusChanged(hasFocus);
		Log.i(TAG,mMapView.getWidth() + " " + mMapView.getHeight());
		GeoPoint gp = new GeoPoint((int)(37.573961*1E6),(int)(-77.539614*1E6));
		mMapView.getController().setZoom(11);
		mMapView.getController().setCenter(gp);
		mMapView.getController().animateTo(gp);
		loadPointsFromDatabase();
	}

	private ArrayList<HeatPoint> generateTestHeatPoints()
	{
		//37.573961,-77.539614
		float latseed = 37.573961f;
		float lonseed = -77.539614f;
		ArrayList<HeatPoint> returnList = new ArrayList<HeatPoint>();
		Random r = new Random(System.currentTimeMillis());
		for(int i = 0;i<1000;i++)
		{
			float newlat;
			float newlon;
			if(r.nextInt(10) > 5)
				newlat = latseed + r.nextFloat()/100.0f;
			else
				newlat = latseed - r.nextFloat()/100.0f;
			if(r.nextInt(10) > 5)
				newlon = lonseed + r.nextFloat()/100.0f;
			else
				newlon = lonseed - r.nextFloat()/100.0f;
			returnList.add(new HeatPoint(newlat,newlon,"10/10/2012"));
		}
		return returnList;
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

}
