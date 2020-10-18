package group.unimelb.vicmarket.retrofit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

public class LocationUtil {
    private static LocationManager locationManager;
    private static Context mContext;
    private static LocationUtil mInstance;
    private static double latitude;
    private static double longitude;
    private static String addressLine;
    private static List<Address> addresses;
    private static LocationListener mLocationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            Log.i("updata location","change");

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.i("updata location","status change");
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.i("updata location","enable");
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.i("updata location","disable");
        }
    };

    private LocationUtil(Context context) {
        this.mContext = context;
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
    }

    public static LocationUtil getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new LocationUtil(context.getApplicationContext());
        }
        return mInstance;
    }

    public double getLatitude() {
        getLocation();
        return latitude;
    }

    public double getLongitude() {
        getLocation();
        return longitude;
    }


    public String getAddressLine() {
        getLocation();
        return addressLine;
    }

    @SuppressLint("MissingPermission")
    private static void getLocation() {
        try {
            List<String> list = locationManager.getProviders(true);

            String provider;

            if (list.contains(LocationManager.GPS_PROVIDER)) {
                provider = LocationManager.GPS_PROVIDER;
                Log.i("location service","GPS");
            }
            else if (list.contains(LocationManager.NETWORK_PROVIDER)) {
                provider = LocationManager.NETWORK_PROVIDER;
                Log.i("location service","network");

            } else {
                Toast.makeText(mContext, "Please check your GPS status", Toast.LENGTH_LONG).show();
                return;
            }
            locationManager.requestLocationUpdates(provider,10,0,mLocationListener);
            Location location = locationManager.getLastKnownLocation(provider);
            while (location == null){
                locationManager.requestLocationUpdates(provider,10,0,mLocationListener);
            }
            locationManager.removeUpdates(mLocationListener);
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
                addresses = geocoder.getFromLocation(latitude, longitude,1);
                addressLine = addresses.get(0).getAddressLine(0);
                Log.i("location",addresses.get(0).getAddressLine(0)+" "+ latitude+" "+longitude);
            } else {
                Log.i("Location","cannot find location");
        }} catch (Exception e){
            e.printStackTrace();
        }
    }


}

