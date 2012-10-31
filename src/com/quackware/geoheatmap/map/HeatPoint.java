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

/**
 * @author evincar
 *
 */
public class HeatPoint {
        public HeatPoint(float lat, float lon, int intensity, String timestamp) {
                this.mLat = lat;
                this.mLon = lon;
                this.mIntensity = intensity;
                this.mTimestamp = timestamp;
        }

        public float mLat;
        public float mLon;
        public int mIntensity;
        public String mTimestamp;
        
        public HeatPoint(){
                this(0f,0f,0,"");
        }
        
        public HeatPoint(float lat, float lon, String timestamp){
                this(lat,lon,1,timestamp);
        }
        
}
