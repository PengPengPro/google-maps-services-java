package com.google.ui.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.GeolocationApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.GeolocationPayload;
import com.google.maps.model.GeolocationResult;
import com.google.maps.model.LatLng;

import java.io.IOException;
import java.util.UUID;

public class GoogleMapsLocationUtils {

    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private GoogleMap googleMap = null;

    private static String TAG = GoogleMapsLocationUtils.class.getSimpleName();
    private static GoogleMapsLocationUtils _instance;

    //private LocationManager locationManager;
    public static GoogleMapsLocationUtils getInstance(Context mContext, LocationDelegate mLocationDelegate) {
        if (_instance == null) {
            synchronized (GoogleMapsLocationUtils.class) {
                if (_instance == null) {
                    _instance = new GoogleMapsLocationUtils(mContext, mLocationDelegate);
                }
            }
        }
        return _instance;
    }

    private Context mContext;

    public GoogleMapsLocationUtils(Context mContext, LocationDelegate mLocationDelegate) {
        //mContext = BaseApp.getInstance().getBaseContext();
        this.mContext = mContext;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000); // 请求时间间隔（毫秒）
        locationRequest.setFastestInterval(5000); // 最快时间间隔（毫秒）
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); // 高精度模式

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                handleLocation(locationResult.getLastLocation(), mLocationDelegate);
            }
        };
        //locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
    }

    public void handleLocation(Location location, LocationDelegate mLocationDelegate){
        stopLocationUpdates();

        try {
            // [START maps_experience_id]
            final String experienceId = UUID.randomUUID().toString();

            // instantiate context
            final GeoApiContext context = new GeoApiContext.Builder().apiKey("AIza-Maps-API-Key").build();

            // set the experience id on a request
            //final GeolocationApiRequest geocodingApiRequest = GeolocationApi.newRequest(context).experienceIds(experienceId);

            //GeolocationPayload payload = new GeolocationPayload.GeolocationPayloadBuilder().createGeolocationPayload();
            //GeolocationResult location = GeolocationApi.geolocate(context, payload).await();
            LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
            GeocodingResult[] results = GeocodingApi.newRequest(context).latlng(latlng).await().results;
            if(mLocationDelegate!=null){
                mLocationDelegate.callBack(latlng, results);
            }

        } catch (Exception e){
            e.printStackTrace();
        }

        /*//longitudeEditText.setText(location.getLongitude() + "");
        //latitudeEditText.setText(location.getLatitude() + "");
        //addressTextView.text = url;
        //addressTextView.setText(url + "");
        //请求网络即可
        Log.e(TAG, "--->url:" + url);
        Log.e(TAG, "--->longitude:" + location.getLongitude() + "");
        Log.e(TAG, "--->latitude:" + location.getLatitude() + "");
        Log.e(TAG, "--->latitude:" + location.toString() + "");*/

        //开发发送网络请求：
    }

    @SuppressLint("MissingPermission")
    public void startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }

    /**
     * 停止获取位置更新
     */
    public void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    public interface LocationDelegate {
        void callBack(LatLng latlng, GeocodingResult[] results);
    }

}
