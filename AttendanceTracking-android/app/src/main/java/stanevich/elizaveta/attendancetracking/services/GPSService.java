package stanevich.elizaveta.attendancetracking.services;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class GPSService {

    public static final int GPS_ACCESS_REQUEST = 7813;
    private static GPSService INSTANCE;
    private final LocationManager locationManager;
    private Activity activity;
    private LocationListener locationListener;

    private GPSService(Activity activity) {
        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        this.activity = activity;
    }

    public static GPSService getInstance(Activity activity) {
        if (INSTANCE != null) {
            INSTANCE.activity = activity;
            return INSTANCE;
        }
        INSTANCE = new GPSService(activity);
        return INSTANCE;
    }

    public void locationChanged(final OnLocationChanged checkLocation) {
        // Define a listener that responds to location updates
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                checkLocation.changed(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        if (ActivityCompat.checkSelfPermission(activity, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        } else {
            requestGpsPermissions(activity);
        }
    }

    public void removeUpdates() {
        locationManager.removeUpdates(locationListener);
    }

    public static void requestGpsPermissions(Activity activity) {
        if (ActivityCompat.checkSelfPermission(activity, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, GPS_ACCESS_REQUEST);
            throw new IllegalStateException("Permission ACCESS_FINE_LOCATION or ACCESS_COARSE_LOCATION is not provided!");
        }
    }
}
