package group.unimelb.vicmarket.util;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import group.unimelb.vicmarket.common.MarketApplication;

public class LocationUtil {
    private static final double EARTH_RADIUS = 6378137;
    private static LocationManager locationManager;
    private static Context mContext;
    private static LocationUtil mInstance;
    private static LocationListener mLocationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            Log.i("updata location", "change");

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.i("updata location", "status change");
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.i("updata location", "enable");
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.i("updata location", "disable");
        }
    };
    private LocationInfo locationInfo;

    private LocationUtil(Context context) {
        mContext = context;
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
    }

    public static LocationUtil getInstance() {
        if (mInstance == null) {
            mInstance = new LocationUtil(MarketApplication.getAppContext());
        }
        return mInstance;
    }

    private double rad(double d) {
        return d * Math.PI / 180.0;
    }

    public double getDistance(double lon1, double lat1, double lon2, double lat2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lon1) - rad(lon2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
                Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        return s / 1000;
    }

    public LocationInfo getLocationInfo() {
        getLocation();
        return locationInfo;
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        try {
            List<String> list = locationManager.getProviders(true);

            String provider;

            if (list.contains(LocationManager.GPS_PROVIDER)) {
                provider = LocationManager.GPS_PROVIDER;
            } else if (list.contains(LocationManager.NETWORK_PROVIDER)) {
                provider = LocationManager.NETWORK_PROVIDER;
            } else {
                Toast.makeText(mContext, "Please check your GPS status", Toast.LENGTH_LONG).show();
                return;
            }
            locationManager.requestLocationUpdates(provider, 10, 0, mLocationListener);
            Location location = locationManager.getLastKnownLocation(provider);
            if (location == null){
                provider = LocationManager.NETWORK_PROVIDER;
                locationManager.requestLocationUpdates(provider, 10, 0, mLocationListener);
                location = locationManager.getLastKnownLocation(provider);
            }
            locationManager.removeUpdates(mLocationListener);
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
                List<String> addresses = new ArrayList<>();
                for (Address address : geocoder.getFromLocation(latitude, longitude, 20)) {
                    addresses.add(address.getAddressLine(0));
                }
                locationInfo = new LocationInfo(latitude, longitude, addresses);

                Log.i("location", locationInfo.toString());
            } else {
                Log.i("Location", "cannot find location");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static class LocationInfo {
        private double latitude;
        private double longitude;
        private List<String> addresses;

        public LocationInfo(double latitude, double longitude, List<String> addresses) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.addresses = addresses;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public List<String> getAddresses() {
            return addresses;
        }

        public void setAddresses(List<String> addresses) {
            this.addresses = addresses;
        }

        @Override
        public String toString() {
            return "LocationInfo{" +
                    "latitude=" + latitude +
                    ", longitude=" + longitude +
                    ", addresses=" + addresses +
                    '}';
        }
    }
}

