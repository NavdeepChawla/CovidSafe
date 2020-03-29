package com.example.android.covidhack;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.*;
import android.provider.Settings;

public class LocationService {
    private Context mContext;
    private Location mLocation;
    private LocationManager locationManager;
    private PendingIntent intent;

    private static long distance=5;
    private static long minutes=2;

    public LocationService(Context context) {
        mContext = context;

        // Set up to capture the location updates
        Intent smsIntent = new Intent(mContext, BroadcastLocationService.class);
        intent = PendingIntent.getService(mContext, 0, smsIntent, 0);
    }

    public static long getDistance() {
        return distance;
    }

    public static long getMinutes() {
        return minutes;
    }

    public void cancelUpdates() {
        if(locationManager != null) {
            locationManager.removeUpdates(intent);
        }
    }

    public Location getLocation() {
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        // Check if the tracking is enabled.
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnabled && !isNetworkEnabled) {
            // Prompt to get the settings enabled by the user.
            showSettingsDialog();
        } else {
            // Either one is enabled
            // 10 * 60 * 1000 = 10 minutes
            // 1000 = 1 km
            // this = listener

            if (isGPSEnabled) {
                // Get the location from GPS
                try {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            getMinutes() * 1000 * 60,
                            getDistance(),
                            intent);

                    if(mLocation == null) {
                        mLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    }
                } catch (SecurityException e) {
                    showSettingsDialog();
                }
            } else {
                // Get the location from GPS
                try {
                    // Set up to capture the location updates
                    Intent smsIntent = new Intent(mContext, BroadcastLocationService.class);
                    PendingIntent intent = PendingIntent.getService(mContext, 0, smsIntent, 0);

                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            getMinutes() * 1000 * 60,
                            getDistance(),
                            intent);

                    if(mLocation == null) {
                        mLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                } catch (SecurityException e) {
                    showSettingsDialog();
                }
            }
        }

        return mLocation;
    }

    public void showSettingsDialog() {
        new AlertDialog.Builder(mContext)
                .setTitle("Enable GPS")
                .setMessage("Enable GPS in your settings for receiving active location details.")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        mContext.startActivity(intent);
                    }
                })
                .create().show();
    }
}
