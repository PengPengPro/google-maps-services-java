package com.google.ui;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.app.demo.R;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.google.ui.utils.GoogleMapsLocationUtils;

public class LocationActivity extends AppCompatActivity {
    private String locationPermission = Manifest.permission.ACCESS_FINE_LOCATION;
    String TAG = LocationActivity.class.getSimpleName();
    EditText longitudeEditText;
    EditText latitudeEditText;
    TextView addressTextView;
    GoogleMapsLocationUtils mGoogleOauthUtils;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_activity);

        findViewById(R.id.getLocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        findViewById(R.id.getLocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestLocationPermission();
            }
        });

        findViewById(R.id.stopLocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGoogleOauthUtils.stopLocationUpdates();
            }
        });

        longitudeEditText = findViewById(R.id.longitudeEditText);
        latitudeEditText = findViewById(R.id.latitudeEditText);
        addressTextView = findViewById(R.id.addressTextView);

        mGoogleOauthUtils = GoogleMapsLocationUtils.getInstance(this, new GoogleMapsLocationUtils.LocationDelegate() {
            @Override
            public void callBack(LatLng latlng, GeocodingResult[] results) {
                if(latlng!=null){
                    longitudeEditText.setText(latlng.lng + "");
                    latitudeEditText.setText(latlng.lat + "");
                }

                Log.e(TAG, "latlng:" + latlng.toString());

                StringBuffer sb = new StringBuffer();
                for (GeocodingResult item :results) {
                    Log.e(TAG, "handleLocation item:" + item.toString());
                    // [GeocodingResult placeId=ChIJfZY1A9zC7zYRiWhbVzycKEs [Geometry: 30.66936000,103.95474000 (GEOMETRIC_CENTER) bounds=null, viewport=[30.67070898,103.95608898, 30.66801102,103.95339102]], formattedAddress=China, Cheng Du Shi, Qing Yang Qu, CN 四川省 成都市 青羊区 199 凯德·风尚 邮政编码: 610073, types=[establishment, laundry, point_of_interest], addressComponents=[[AddressComponent: "China" ("CN") (country, political)], [AddressComponent: "Qing Yang Qu" ("Qing Yang Qu") (political, sublocality, sublocality_level_1)], [AddressComponent: "Cheng Du Shi" ("Cheng Du Shi") (locality, political)], [AddressComponent: "610073" ("610073") (postal_code)]]]
                    sb.append("latlng:" + item.geometry.location.toString() + " formattedAddress:" + item.formattedAddress);
                    sb.append("\n");
                }

                addressTextView.setText("detail:" + "\n" + sb.toString());
            }
        });

    }

    /**
     * 判断是否有权限
     */
    public boolean hasPermissions(@NonNull Context context, @NonNull String... perms) {
        // Always return true for SDK < M, let the system deal with the permissions
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Log.w(TAG, "hasPermissions: API version < M, returning true by default");
            return true;
        }
        for (String perm : perms) {
            boolean hasPerm = (ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED);
            if (!hasPerm) {
                return false;
            }
        }
        return true;
    }

    private void handlePermissionDenied() {
        Log.e(TAG, "没有权限");
    }
    private void requestLocation() {
        Log.e(TAG, "requestLocationService()");
        mGoogleOauthUtils.startLocationUpdates();
        //if (LocationServiceEnable()) requestLocationUpdate();
    }

    /*override void onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            requestLocationUpdate()
        }
    }*/


    final int REQUEST_CODE_PERMISSIONS = 2;
    private void requestLocationPermission() {
        // 检查权限
        if (hasPermissions(this, locationPermission)) {
            requestLocation();
        } else {
            //ActivityCompat.requestPermissions(NewMainGridViewAct.this, Manifest.permission.CAMERA_ARRAY, 1);
            ActivityCompat.requestPermissions(LocationActivity.this, new String[]{locationPermission}, REQUEST_CODE_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) { // 替换为你的请求码
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocation();
            } else {
                // 权限被拒绝，处理拒绝的情况
                // 权限被拒绝，可以在这里处理逻辑，例如提示用户
                Toast.makeText(this, "定位权限被拒绝", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
