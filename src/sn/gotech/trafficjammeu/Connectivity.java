package sn.gotech.trafficjammeu;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class Connectivity {
	Timer timer;
	LocationManager lm;
	LocationResult locationResult;
	boolean isGpsEnabled = false;
	boolean isNetworkEnabled = false;

	public boolean getLocation(Context context, LocationResult result) {
		// I use LocationResult callback class to pass location value from
		// Location to user code.
		locationResult = result;
		if (lm == null)
			lm = (LocationManager) context
					.getSystemService(Context.LOCATION_SERVICE);

		// exceptions will be thrown if provider is not permitted.
		try {
			isGpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		} catch (Exception ex) {
		}

		try {
			isNetworkEnabled = lm
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		} catch (Exception ex) {
		}

		// don't start listeners if no provider is enabled
		if (!isGpsEnabled && !isNetworkEnabled){
			return false;
		}

		if (isGpsEnabled){
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, gpsLocationListener);
		}
					
		if (isNetworkEnabled){
			lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, networkLocationListener);
		}
		
		timer = new Timer();
		timer.schedule(new GetLastLocation(), 30000); // 30 seconds
		return true;
	}

	LocationListener gpsLocationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			timer.cancel();
			locationResult.gotLocation(location);
			lm.removeUpdates(this);
			lm.removeUpdates(networkLocationListener);
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	LocationListener networkLocationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			timer.cancel();
			locationResult.gotLocation(location);
			lm.removeUpdates(this);
			lm.removeUpdates(gpsLocationListener);
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	class GetLastLocation extends TimerTask {
		@Override
		public void run() {
			lm.removeUpdates(gpsLocationListener);
			lm.removeUpdates(networkLocationListener);

			Location netLoc = null, gpsLoc = null;
			if (isGpsEnabled){
				gpsLoc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			}
			if (isNetworkEnabled){
				netLoc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			}

			// if there are both values use the latest one
			if (gpsLoc != null && netLoc != null) {
				if (gpsLoc.getTime() > netLoc.getTime())
					locationResult.gotLocation(gpsLoc);
				else
					locationResult.gotLocation(netLoc);
				return;
			}

			if (gpsLoc != null) {
				locationResult.gotLocation(gpsLoc);
				return;
			}
			
			if (netLoc != null) {
				locationResult.gotLocation(netLoc);
				return;
			}
			
			locationResult.gotLocation(null);
		}
	}

	public static abstract class LocationResult {
		public abstract void gotLocation(Location location);
	}
}