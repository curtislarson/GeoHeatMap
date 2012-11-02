/*
 * Copyright (C) 2011 by Vinicius Carvalho (vinnie@androidnatic.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.quackware.geoheatmap.map;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RadialGradient;
import android.graphics.Shader.TileMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;
import com.quackware.geoheatmap.MyApplication;

/**
 * @author Vinicius Carvalho An overlay that draws a heatmap using a set of
 *         HeatPoints provided at the update method.
 * 
 */
public class HeatMapOverlay extends Overlay {

	private Bitmap layer;
	// private float radius;
	private MapView mapView;
	private ReentrantLock lock;

	private List<HeatPoint> heatPoints;
	
	private GeoPoint lastCenter = null;

	private static final String TAG = "HeatMapOverlay";

	public HeatMapOverlay(MapView mapview) {
		// this.radius = radius;
		this.mapView = mapview;
		this.lock = new ReentrantLock();
		lastCenter = mapView.getMapCenter();
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, shadow);
		Log.i(TAG,lastCenter.toString() + " " + mapView.getMapCenter().toString());
		if (layer != null && lastCenter.equals(mapView.getMapCenter())) {
			canvas.drawBitmap(layer, 0, 0, null);
		}
		if(layer != null)
		{
			lastCenter = mapView.getMapCenter();
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent e, MapView mapView) {

		if (e.getAction() == MotionEvent.ACTION_DOWN) {
			lock.lock();
			this.layer = null;
			lock.unlock();
		} 
		if(e.getAction() == MotionEvent.ACTION_UP)
		{
			redraw();
		}
		//this.layer = null;
		lastCenter = mapView.getMapCenter();
		//else if (e.getAction() == MotionEvent.ACTION_UP) {
			//redraw();
		//}

		return super.onTouchEvent(e, mapView);
	}

	/**
	 * Updates the heatmap canvas. Note that for each point, it's lat/lon values
	 * should be in decimal format, not 1E6 as used by GoogleMaps. The class
	 * will convert it.
	 * 
	 * @param points
	 */
	public void update(List<HeatPoint> points) {
		heatPoints = points;
		redraw();
	}

	public void redraw() {
		if (heatPoints == null) {
			// warning need to call update
		} else {
			// Factor in screen dimensions.
			//float radius = 100 * (20 - mapView.getZoomLevel());
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MyApplication.getInstance());

			float radius = prefs.getInt("heatPointRadius", 1000) / mapView.getZoomLevel();
			Log.i(TAG, "Zoomlevel: " + mapView.getZoomLevel());
			float pxRadius = (float) (mapView.getProjection()
					.metersToEquatorPixels(radius) * 1 / Math.cos(Math
					.toRadians(mapView.getMapCenter().getLatitudeE6() / 1E6)));
			HeatTask task = new HeatTask(mapView.getWidth(),
					mapView.getHeight(), pxRadius, heatPoints);
			new Thread(task).start();
		}
	}

	private class HeatTask implements Runnable {

		private Canvas myCanvas;
		private Bitmap backbuffer;
		private int width;
		private int height;
		private float radius;
		private List<HeatPoint> points;

		public HeatTask(int width, int height, float radius,
				List<HeatPoint> points) {
			backbuffer = Bitmap.createBitmap(width, height,
					Bitmap.Config.ARGB_8888);
			myCanvas = new Canvas(backbuffer);
			Paint p = new Paint();
			p.setStyle(Paint.Style.FILL);
			p.setColor(Color.TRANSPARENT);
			this.width = width;
			this.height = height;
			this.points = points;
			myCanvas.drawRect(0, 0, width, height, p);
			this.radius = radius;
		}

		@Override
		public void run() {
			Projection proj = mapView.getProjection();
			for (HeatPoint p : points) {
				GeoPoint in = new GeoPoint((int) (p.mLat * 1E6),
						(int) (p.mLon * 1E6));
				Point out = proj.toPixels(in, null);
				addPoint(out.x, out.y, p.mIntensity);
			}
			colorize(0, 0);
			lock.lock();
			layer = backbuffer;
			lock.unlock();
			mapView.postInvalidate();
		}

		private void addPoint(float x, float y, int times) {
			RadialGradient g = new RadialGradient(x, y, radius, Color.argb(
					Math.max(10 * times, 255), 0, 0, 0), Color.TRANSPARENT,
					TileMode.CLAMP);
			Paint gp = new Paint();
			gp.setShader(g);
			myCanvas.drawCircle(x, y, radius, gp);
		}

		private void colorize(float x, float y) {
			int[] pixels = new int[(int) (this.width * this.height)];
			backbuffer.getPixels(pixels, 0, this.width, 0, 0, this.width,
					this.height);

			for (int i = 0; i < pixels.length; i++) {
				int r = 0, g = 0, b = 0, tmp = 0;
				int alpha = pixels[i] >>> 24;
				if (alpha == 0) {
					continue;
				}
				if (alpha <= 255 && alpha >= 235) {
					tmp = 255 - alpha;
					r = 255 - tmp;
					g = tmp * 12;
				} else if (alpha <= 234 && alpha >= 200) {
					tmp = 234 - alpha;
					r = 255 - (tmp * 8);
					g = 255;
				} else if (alpha <= 199 && alpha >= 150) {
					tmp = 199 - alpha;
					g = 255;
					b = tmp * 5;
				} else if (alpha <= 149 && alpha >= 100) {
					tmp = 149 - alpha;
					g = 255 - (tmp * 5);
					b = 255;
				} else
					b = 255;
				pixels[i] = Color.argb((int) alpha / 2, r, g, b);
			}
			backbuffer.setPixels(pixels, 0, this.width, 0, 0, this.width,
					this.height);
		}
	}

}
