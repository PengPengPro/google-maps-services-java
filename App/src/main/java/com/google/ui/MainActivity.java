package com.google.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.google.app.demo.R;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.GeocodingApiRequest;
import com.google.maps.GeolocationApi;
import com.google.maps.PendingResult;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.GeolocationPayload;
import com.google.maps.model.GeolocationResult;
import com.google.maps.model.LatLng;

import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    final String TAG = MainActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        findViewById(R.id.startOtherMap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(MainActivity.this, LocationActivity.class));
                try {
                    testExperienceIdSample();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }


    private void testExperienceIdSample() throws IOException, InterruptedException, ApiException {

        // [START maps_experience_id]
        final String experienceId = UUID.randomUUID().toString();

        // instantiate context
        final GeoApiContext context = new GeoApiContext.Builder().apiKey("AIza-Maps-API-Key").build();

        // set the experience id on a request
        //final GeolocationApiRequest geocodingApiRequest = GeolocationApi.newRequest(context).experienceIds(experienceId);

        GeolocationPayload payload = new GeolocationPayload.GeolocationPayloadBuilder().createGeolocationPayload();
        GeolocationResult resultx = GeolocationApi.geolocate(context, payload).await();
        LatLng latlng = new LatLng(resultx.location.lat, resultx.location.lng);
        GeocodingResult[] results = GeocodingApi.newRequest(context).latlng(latlng).await().results;

        GeolocationApi.newRequest(context).setCallback(new PendingResult.Callback<GeolocationResult>() {
            @Override
            public void onResult(GeolocationResult result) {
                Log.e(TAG, "result:" + result);
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "e:" + e);
            }
        });

        final GeocodingApiRequest request = GeocodingApi.newRequest(context).experienceIds(experienceId);

        // set a new experience id on another request
        final String otherExperienceId = UUID.randomUUID().toString();
        final GeocodingApiRequest request2 = GeocodingApi.newRequest(context).experienceIds(otherExperienceId);

        // make API request, the client will set the header
        // X-GOOG-MAPS-EXPERIENCE-ID: experienceId,otherExperienceId
        // [END maps_experience_id]
    }
}
