package com.example.sreeshav.prog02;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "TH3vrOG2D1pcE7IbA7aQcGJ2f";
    private static final String TWITTER_SECRET = "rEPp63s6JruEV6vAZ6r8Is3AKUg5Xyr6cj9DxsPrTXsQFB2OUD";

    private static Button button_go;
    private static Button button_current;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    String mLatitudeText;
    String mLongitudeText;
    EditText locationOrZip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        locationOrZip = (EditText) findViewById(R.id.zip_code);
        button_go = (Button) findViewById(R.id.button2);
        button_go.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             String zipString = locationOrZip.getText().toString();
                                             Intent sendIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
                                             sendIntent.putExtra("Clicked", "Yes");
                                             sendIntent.putExtra("nameAndParty", " ");
                                             Bundle locationInfo = new Bundle();
                                             locationInfo.putString("Location", zipString);
                                             locationInfo.putString("Latitude", mLatitudeText);
                                             locationInfo.putString("Longitude", mLongitudeText);
                                             Intent intent = new Intent("com.example.sreeshav.prog02.CongressionalView");
                                             intent.putExtras(locationInfo);
                                             sendIntent.putExtras(locationInfo);
                                             startActivity(intent);
                                             startService(sendIntent);
                                         }
                                     }
        );
        button_current = (Button) findViewById(R.id.button);
        button_current.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View v) {
                                                  String zipString = locationOrZip.getText().toString();
                                                  Intent sendIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
                                                  sendIntent.putExtra("Clicked", "Yes");
                                                  Bundle locationInfo = new Bundle();
                                                  locationInfo.putString("Location", null);
                                                  locationInfo.putString("Latitude", mLatitudeText);
                                                  locationInfo.putString("Longitude", mLongitudeText);
                                                  Intent intent = new Intent("com.example.sreeshav.prog02.CongressionalView");
                                                  intent.putExtras(locationInfo);
                                                  startActivity(intent);
//                                                  startService(sendIntent);
                                              }
                                          }
        );
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            mLatitudeText = String.valueOf(mLastLocation.getLatitude());
            mLongitudeText = String.valueOf(mLastLocation.getLongitude());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
