package map.example.com;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.inputmethod.InputMethodManager;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    GoogleMap mMap;
    private static final int ERROR_DIALOG_REQUEST = 9001;
    public static final double
            CORK_LAT = 51.899441,
            CORK_LNG = -8.478183,
            DUBLIN_LAT = 53.347353,
            DUBLIN_LNG = -6.259028,
            TIPP_LAT = 52.516367,
            TIPP_LNG = -7.889292,
            LAT = 0,
            LNG = 0;

    private GoogleApiClient mLocationClient;
    private LocationListener mListener;
    private Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (servicesOK() && getConnectivityStatus(this)) {
            setContentView(R.layout.activity_map);
            if (initMap()) {
                //Toast.makeText(this, "Ready to map!", Toast.LENGTH_SHORT).show();
                //goToLocation(TIPP_LAT,TIPP_LNG);
                mLocationClient = new GoogleApiClient.Builder(this)
                        .addApi(LocationServices.API)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .build();
                mLocationClient.connect();


            } else {
                Toast.makeText(this, "Map not connected!", Toast.LENGTH_SHORT).show();
                setContentView(R.layout.activity_main);
            }
        } else {
            setContentView(R.layout.activity_main);

        }
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

    public boolean servicesOK() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int available = api.isGooglePlayServicesAvailable(this);
        if (available == ConnectionResult.SUCCESS) {
            return true;
        } else if (api.isUserResolvableError(available) &&
                api.showErrorDialogFragment(this, available,
                        ERROR_DIALOG_REQUEST)) {

        } else {
            Toast.makeText(this, "Google play services is required for this function to work!",
                    Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private boolean initMap() {
        if (mMap == null) {
            SupportMapFragment mapFragment =
                    (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            //Toast.makeText(this, "hi", Toast.LENGTH_SHORT).show();

        }
        return (true);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(CORK_LAT, CORK_LNG))
                .title("Cork")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(DUBLIN_LAT, DUBLIN_LNG))
                .title("Dublin")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        LatLng latlng = new LatLng(CORK_LAT, CORK_LNG);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latlng, 15);
        mMap.moveCamera(update);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
//        mMap.setMyLocationEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (mMap != null) {
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    View v = getLayoutInflater().inflate(R.layout.info_window, null);
                    TextView tvLocality = (TextView) v.findViewById(R.id.tvLocality);
                    TextView tvLat = (TextView) v.findViewById(R.id.tvLat);
                    TextView tvLng = (TextView) v.findViewById(R.id.tvLng);
                    TextView tvSnippet = (TextView) v.findViewById(R.id.tvSnippet);

                    LatLng latLng = marker.getPosition();
                    tvLocality.setText(marker.getTitle());
                    tvLat.setText("Latitude: " + latLng.latitude);
                    tvLng.setText("Longitude: " + latLng.longitude);
                    tvSnippet.setText(marker.getSnippet());

                    return v;
                }
            });

            mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
                    Geocoder gc = new Geocoder(MainActivity.this);
                    List<Address> list = null;

                    try {
                        list = gc.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }

                    Address add = list.get(0);
                    MainActivity.this.addMarker(add, latLng.latitude, latLng.longitude);
                }
            });

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    String msg = marker.getTitle() + " (" +
                            marker.getPosition().latitude + ", " +
                            marker.getPosition().longitude + ")";
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
        }


    }

    private void addMarker(Address add, double lat, double lng) {
        MarkerOptions options = new MarkerOptions()
                .title(add.getLocality())
                .position(new LatLng(lat, lng))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker));

        String country = add.getCountryName();
        if (country.length() > 0) {
            options.snippet(country);
        }

        marker = mMap.addMarker(options);
    }

    private void hideSoftKeyboard(View v) {
        InputMethodManager imm =
                (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public void geoLocate(View view) throws IOException {
        hideSoftKeyboard(view);
        TextView tv = (TextView) findViewById(R.id.editText1);
        String searchString = tv.getText().toString();
        Toast.makeText(this, "Searching for: " + searchString, Toast.LENGTH_SHORT).show();

        Geocoder gc = new Geocoder(this);
        List<Address> list = gc.getFromLocationName(searchString, 1);

        if (list.size() > 0) {
            Address add = list.get(0);
            String locality = add.getLocality();
            Toast.makeText(this, "Found: " + locality, Toast.LENGTH_SHORT).show();

            double lat = add.getLatitude();
            double lng = add.getLongitude();
            String country = add.getCountryName();
            LatLng latlng = new LatLng(lat, lng);
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latlng, 15);
            mMap.moveCamera(update);

            if (marker != null) {
                marker.remove();
            }
            marker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lat, lng))
                    .title(locality)
                    .snippet(country)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            //.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker)));
            //initMap();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Toast.makeText(this, "Ready to map", Toast.LENGTH_SHORT).show();
        mListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Toast.makeText(MainActivity.this, "Location Changed:" + location.getLatitude() +
                        ", " + location.getLongitude(), Toast.LENGTH_SHORT).show();
                LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latlng, 15);
                //mMap.moveCamera(update);
            }
        };
//        LocationRequest request = LocationRequest.create();
//        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        request.setInterval(5000);
//        request.setFastestInterval(1000);
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
//                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        LocationServices.FusedLocationApi.requestLocationUpdates(mLocationClient, request, mListener);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "No Connection" +
                "", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocationServices.FusedLocationApi.removeLocationUpdates(mLocationClient, mListener);
    }

    public static boolean getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)

                return true;

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return true;
        }
        Toast.makeText(context, "No network connection!", Toast.LENGTH_SHORT).show();
        return false;
    }

    public void showCurrentLocation(MenuItem item) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location currentLocation = LocationServices.FusedLocationApi.getLastLocation(mLocationClient);
        if (currentLocation == null) {
            Toast.makeText(this, "Couldn't connect!", Toast.LENGTH_SHORT).show();
        } else {
            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, 15);
            mMap.animateCamera(update);
        }
    }
}