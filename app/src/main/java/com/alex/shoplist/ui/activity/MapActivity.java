package com.alex.shoplist.ui.activity;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.alex.shoplist.R;
import com.alex.shoplist.sqlite.ShopsProvider;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor>, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private final String[] SHOPS_PROJECTION = new String[]{
            ShopsProvider.Columns._ID,
            ShopsProvider.Columns.NAME,
            ShopsProvider.Columns.LATITUDE,
            ShopsProvider.Columns.LONGITUDE
    };

    private static final int SHOPS_LOADER = 0;

    private MapFragment mMapFragment;
    private GoogleMap mMap;
    private Marker defaultPositionMarker;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private LocationManager locationManager;
    private Location bestLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        getLoaderManager().initLoader(SHOPS_LOADER, null, this);

        buildGoogleApiClient();
        initMap();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void initMap() {
        mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mMap = mMapFragment.getMap();
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 10, locationListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("PlayService", "Connected");
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        setDefaultMapPosition(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        setDefaultMarker(mLastLocation.getLatitude(), mLastLocation.getLongitude());
    }

    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            getBestLocation(location);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String provider) {
            getBestLocation(locationManager.getLastKnownLocation(provider));
        }

        @Override
        public void onProviderDisabled(String s) {
        }
    };

    private void getBestLocation(Location location) {
        if (bestLocation == null) {
            bestLocation = location;
        } else if ((location.getProvider()).equals(LocationManager.GPS_PROVIDER)) {
            bestLocation = location;
        } else if ((location.getProvider()).equals(LocationManager.NETWORK_PROVIDER)) {
            if ((bestLocation.getProvider()).equals(LocationManager.NETWORK_PROVIDER)) {
                bestLocation = location;
            } else if ((bestLocation.getProvider()).equals(LocationManager.GPS_PROVIDER) &&
                    !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                bestLocation = location;
            }
        }

        setDefaultMapPosition(bestLocation.getLatitude(), bestLocation.getLongitude());
        setDefaultMarker(bestLocation.getLatitude(), bestLocation.getLongitude());
    }

    private void setDefaultMapPosition(double lat, double lng) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(lat, lng))
                .zoom(15)
                .build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        mMap.animateCamera(cameraUpdate);
    }

    private void setDefaultMarker(double lat, double lng) {
        defaultPositionMarker = mMap.addMarker(new MarkerOptions().draggable(false).
                position(new LatLng(lat, lng)));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                this,
                ShopsProvider.URI,
                SHOPS_PROJECTION,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String currentLat = cursor.getString(cursor.getColumnIndex(ShopsProvider.Columns.LATITUDE));
                String currentLng = cursor.getString(cursor.getColumnIndex(ShopsProvider.Columns.LONGITUDE));
                double lat = Double.valueOf(currentLat) / 1000000;
                double lng = Double.valueOf(currentLng) / 1000000;
                showMarker(lat, lng);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    private void showMarker(double lat, double lng) {
        mMap.addMarker(new MarkerOptions().draggable(false).
                position(new LatLng(lat, lng)));
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
