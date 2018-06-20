package com.example.eltimmy.oneway2;

import android.*;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
        , LocationListener {

    private GoogleMap mMap;

    private FusedLocationProviderApi locationProviderApi = LocationServices.FusedLocationApi;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;

    ArrayList<LatLng> latLngs;
    ArrayList<Polyline> polylines;
    LatLng latLng;
    GetDirectionsData getDirctionsData;
    public static final int MINUTE_PER_SECOND = 1000;
    public static final int MINUTE = 1 * MINUTE_PER_SECOND;
    private Marker marker = null;
    private TextView textView;


    private Object dataTransfer[];
    private String url;
    private int waypoints;

    public boolean navigation = false;
    public Button NavigationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        NavigationButton = findViewById(R.id.Navigation_button);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        locationRequest = new LocationRequest();

        locationRequest.setInterval(MINUTE);
        locationRequest.setFastestInterval(1 * MINUTE_PER_SECOND);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }


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
        googleDirectionsUrl.append("&key=AIzaSyBlAFd3r2H62WgQNnmDMTMmiS7F9I-va5Q");

        return googleDirectionsUrl.toString();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

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
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngs.get(1)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngs.get(1), 12));

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (marker != null) {
            marker.remove();
        }

        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        marker = mMap.addMarker(new MarkerOptions().position(latLng).title("my Marker"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        dataTransfer = new Object[5];
        url = getDirctionsUrl();
        getDirctionsData = new GetDirectionsData();
        waypoints = latLngs.size();
        dataTransfer[0] = mMap;
        dataTransfer[1] = url;
        dataTransfer[2] = textView;
        dataTransfer[3] = waypoints;
        dataTransfer[4] = polylines;

        getDirctionsData.execute(dataTransfer);

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
        if (googleApiClient.isConnected()) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            }

            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }


    public void StartNavigation(View view) {
        navigation = true;
        NavigationButton.setVisibility(View.GONE);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {

        }
        mMap.setMyLocationEnabled(false);
    }
}
