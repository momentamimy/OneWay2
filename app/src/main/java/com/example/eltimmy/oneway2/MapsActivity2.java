package com.example.eltimmy.oneway2;

import android.*;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public class MapsActivity2 extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
        , LocationListener, SensorEventListener {

    private GoogleMap mMap;

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;

    ArrayList<LatLng> latLngs;
    ArrayList<Polyline> polylines;
    LatLng latLng=null;
    GetDirectionsData getDirctionsData;
    public static final double MINUTE_PER_SECOND = 1000;
    public static final double MINUTE = .2 * MINUTE_PER_SECOND;
    public static final int REQUEST_LOCATION_CODE = 99;
    private Marker marker = null;


    private Object dataTransfer[];
    private String url;
    private int waypoints;

    public boolean navigation = false;
    public Button NavigationButton;
    public RelativeLayout l_dis, l_dur;
    public TextView distance, duration;

    private float currentDegree = 0f;
    private SensorManager mSensorManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
      /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermmission();
        }
        */
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        locationRequest = new LocationRequest();

        locationRequest.setInterval((long) MINUTE);
        locationRequest.setFastestInterval((long) (1 * MINUTE_PER_SECOND));
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        NavigationButton = findViewById(R.id.Navigation_button);
        distance = findViewById(R.id.distance);
        duration = findViewById(R.id.duration);
        l_dis = findViewById(R.id.distance_layout);
        l_dur = findViewById(R.id.duration_layout);

        l_dis.setVisibility(View.GONE);
        l_dur.setVisibility(View.GONE);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        if (savedInstanceState!=null)
        {
            navigation=savedInstanceState.getBoolean("nav");
            latLng=new LatLng(savedInstanceState.getDouble("lat"),savedInstanceState.getDouble("long"));
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */


    private String getDirctionsUrl() {
        StringBuilder googleDirectionsUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");

        if (navigation == false) {
            googleDirectionsUrl.append("origin=" + latLngs.get(0).latitude + "," + latLngs.get(0).longitude);
        } else if (navigation == true) {
            googleDirectionsUrl.append("origin=" + latLng.latitude + "," + latLng.longitude);
        }

        googleDirectionsUrl.append("&destination=" + latLngs.get(0).latitude + "," + latLngs.get(0).longitude);
        googleDirectionsUrl.append("&waypoints=optimize:true|");
        for (int i = 1; i < latLngs.size(); i++) {
            googleDirectionsUrl.append(+latLngs.get(i).latitude + "," + latLngs.get(i).longitude + "|");
        }
        googleDirectionsUrl.append("&key=AIzaSyAQOrfl7l7Tc4_lEobbGvkR2mUckLT9afE");

        return googleDirectionsUrl.toString();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setBuildingsEnabled(true);



            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            }


            latLngs = new ArrayList<>();
            polylines = new ArrayList<>();


            latLngs.add(new LatLng(31.2468149, 32.3195051));
            latLngs.add(new LatLng(31.240448, 32.323623));
            latLngs.add(new LatLng(31.2438146, 32.3165811));
            latLngs.add(new LatLng(31.241462, 32.316670));


            mMap = googleMap;
            mMap.addMarker(new MarkerOptions().position(latLngs.get(0)).title("Marker 0"));
            mMap.addMarker(new MarkerOptions().position(latLngs.get(1)).title("Marker 1"));
            mMap.addMarker(new MarkerOptions().position(latLngs.get(2)).title("Marker 2"));
            mMap.addMarker(new MarkerOptions().position(latLngs.get(3)).title("Marker 3"));

        if (navigation == false)
        {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngs.get(1)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngs.get(0), 15));
            dataTransfer = new Object[7];
            url = getDirctionsUrl();
            getDirctionsData = new GetDirectionsData();
            waypoints = latLngs.size();
            dataTransfer[0] = mMap;
            dataTransfer[1] = url;
            dataTransfer[2] = distance;
            dataTransfer[3] = duration;
            dataTransfer[4] = waypoints;
            dataTransfer[5] = polylines;
            dataTransfer[6] = navigation;

            getDirctionsData.execute(dataTransfer);
        }
       else if (navigation==true)
        {
            StartNavigation(NavigationButton);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);

        }



    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (navigation == true) {


            if (marker != null) {
                marker.remove();
            }

            latLng = new LatLng(location.getLatitude(), location.getLongitude());

            marker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.navigation)).position           (latLng).title("my Marker"));
            CameraPosition cameraPosition = new CameraPosition.Builder().
                    target(new LatLng(location.getLatitude(), location.getLongitude())).
                    tilt(45).
                    zoom(40).
                    bearing(currentDegree).
                    build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));




            dataTransfer = new Object[7];
            url = getDirctionsUrl();
            getDirctionsData = new GetDirectionsData();
            waypoints = latLngs.size();
            dataTransfer[0] = mMap;
            dataTransfer[1] = url;
            dataTransfer[2] = distance;
            dataTransfer[3] = duration;
            dataTransfer[4] = waypoints;
            dataTransfer[5] = polylines;
            dataTransfer[6] = navigation;

            getDirctionsData.execute(dataTransfer);

        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (googleApiClient.isConnected())
        {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);

            }
        }


        mSensorManager.registerListener((SensorEventListener) this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (googleApiClient.isConnected())
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient,this);
        }
        mSensorManager.unregisterListener((SensorEventListener) this);

    }


    public void StartNavigation(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(false);
        }
        navigation = true;
        NavigationButton.setVisibility(View.GONE);
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,R.raw.navstyle2));
        Animation uptodown,downtoup;
        uptodown= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.uptodown);
        downtoup= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.downtoup);
        l_dis.setVisibility(View.VISIBLE);
        l_dur.setVisibility(View.VISIBLE);
        l_dis.setAnimation(uptodown);
        l_dur.setAnimation(downtoup);
        distance.setText("");
        duration.setText("");

        if (marker != null) {
            marker.remove();
        }


        marker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.navigation)).position           (latLng).title("my Marker"));
        CameraPosition cameraPosition = new CameraPosition.Builder().
                target(new LatLng(latLng.latitude,latLng.longitude)).
                tilt(45).
                zoom(40).
                bearing(currentDegree).
                build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));




        dataTransfer = new Object[7];
        url = getDirctionsUrl();
        getDirctionsData = new GetDirectionsData();
        waypoints = latLngs.size();
        dataTransfer[0] = mMap;
        dataTransfer[1] = url;
        dataTransfer[2] = distance;
        dataTransfer[3] = duration;
        dataTransfer[4] = waypoints;
        dataTransfer[5] = polylines;
        dataTransfer[6] = navigation;

        getDirctionsData.execute(dataTransfer);

    }
    public void close_navigation(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
        navigation=false;
        NavigationButton.setVisibility(View.VISIBLE);
        Animation uptodown,downtoup;
        uptodown= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.downtoup_close);
        downtoup= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.uptodown_close);
        l_dis.setAnimation(uptodown);
        l_dur.setAnimation(downtoup);
        l_dis.setVisibility(View.GONE);
        l_dur.setVisibility(View.GONE);
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,R.raw.standardstyle));

        marker.remove();

        dataTransfer = new Object[7];
        url = getDirctionsUrl();
        getDirctionsData = new GetDirectionsData();
        waypoints = latLngs.size();
        dataTransfer[0] = mMap;
        dataTransfer[1] = url;
        dataTransfer[2] = distance;
        dataTransfer[3] = duration;
        dataTransfer[4] = waypoints;
        dataTransfer[5] = polylines;
        dataTransfer[6] = navigation;

        getDirctionsData.execute(dataTransfer);


        CameraPosition cameraPosition = new CameraPosition.Builder().
                target(new LatLng(latLngs.get(0).latitude, latLngs.get(0).longitude)).
                tilt(0).
                zoom(15).
                bearing(0).
                build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    public boolean checkLocationPermmission()
    {
        if ( ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED&& ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)&& ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions( this, new String[] {  Manifest.permission.ACCESS_FINE_LOCATION  },
                        REQUEST_LOCATION_CODE);
            }
            else
            {
                ActivityCompat.requestPermissions( this, new String[] {  Manifest.permission.ACCESS_FINE_LOCATION  },
                        REQUEST_LOCATION_CODE);
            }
            return false;
        }
        else
            return true;

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // get the angle around the z-axis rotated

        float degree = Math.round(sensorEvent.values[0]);
        currentDegree=degree;
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("nav", navigation);
        if (latLng!=null)
        {
            outState.putDouble("lat",latLng.latitude);
            outState.putDouble("long",latLng.longitude);
        }

        super.onSaveInstanceState(outState);
    }
}
