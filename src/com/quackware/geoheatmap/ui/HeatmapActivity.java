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

public class HeatmapActivity extends MapActivity {
	
	private MapView mMapView;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.heatmap);
		
		mMapView = (MapView)findViewById(R.id.mapview);
		mMapView.setBuiltInZoomControls(true);
		
		loadPointsFromDatabase();
	}
	
	private void loadPointsFromDatabase()
	{
		HeatMapOverlay overlay = new HeatMapOverlay(20000,mMapView);

		HeatmapDatabaseHelper mDatabaseHelper = new HeatmapDatabaseHelper(this);
		ArrayList<HeatPoint> heatPointList = mDatabaseHelper.getHeatPointList();
		overlay.update(heatPointList);
		
		mMapView.getOverlays().add(overlay);
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

}
