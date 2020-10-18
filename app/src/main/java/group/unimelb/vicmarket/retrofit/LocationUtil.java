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

import java.util.List;
import java.util.Locale;

public class LocationUtil {
    private LocationManager locationManager;
    private Context mContext;
    private static LocationUtil mInstance;
    private double latitude;
    private double longitude;
    private String addressLine;
    private List<Address> addresses;
    private static LocationListener mLocationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

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

    public List<Address> getAddresses() {
        getLocation();
        return addresses;
    }

    public String getAddressLine() {
        getLocation();
        return addressLine;
    }

    @SuppressLint("MissingPermission")
    public void getLocation() {
        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, mLocationListener);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            System.out.println(location.toString());
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

