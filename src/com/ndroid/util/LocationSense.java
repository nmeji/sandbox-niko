package com.ndroid.util;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Looper;

import com.google.android.maps.GeoPoint;

public class LocationSense {
	
	public static final String ACTION_GET_LOCATION = "feed-location";
	
	public static final String KEY_LATITUDE = "latitude";
	public static final String KEY_LONGITUDE = "longitude";
	
	private static boolean isCurrentlyRetrievingLocation;
	
	private static LocationManager getLocationManager(Context context) {
		return (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
	}
	
	static GeoPoint getLocationGeoPoint(Location location) {
		return new GeoPoint((int)(location.getLatitude()*1E6), (int)(location.getLongitude()*1E6));
	}
	
	/**
	 * Request for single update.
	 * @param context
	 * @return last known location
	 */
	public static GeoPoint requestCurrentLocation(Context context) {
		if(context == null) {
			return null;
		}
		
		LocationManager locMgr = getLocationManager(context);
		if(!isCurrentlyRetrievingLocation) {
			isCurrentlyRetrievingLocation = true;
			LocationObserver observer = new LocationObserver(context);
			locMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, observer, observer.getAsyncLooper());
		}
		
		return getLocationGeoPoint(locMgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
	}

	private static class LocationObserver implements LocationListener {
		private final Context context;
		private final HandlerThread asyncWorker;
		
		public LocationObserver(Context context) {
			this.context = context;
			
			asyncWorker = new HandlerThread("AsyncLocationSenseProvider");
			asyncWorker.setDaemon(true);
			asyncWorker.start();
		}
		
		private void close() {
			asyncWorker.quit();
			
			getLocationManager(context).removeUpdates(this);
		}
		
		Looper getAsyncLooper() {
			return asyncWorker.getLooper();
		}
		
		public void onLocationChanged(Location locationUpdate) {
			close();
			
			GeoPoint point = getLocationGeoPoint(locationUpdate);
			context.sendBroadcast(new Intent(ACTION_GET_LOCATION)
					.putExtra(KEY_LATITUDE, point.getLatitudeE6())
					.putExtra(KEY_LONGITUDE, point.getLongitudeE6()));
			
			isCurrentlyRetrievingLocation = false;
		}

		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
	}
	
}
