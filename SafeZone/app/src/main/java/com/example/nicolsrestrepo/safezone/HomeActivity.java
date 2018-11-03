package com.example.nicolsrestrepo.safezone;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class HomeActivity extends FragmentActivity implements OnMapReadyCallback {
    private final static int LOCATION_PERMISSON = 0;
    private GoogleMap mMap;
    private ImageButton imageButton_notifyContact;
    private ImageButton imageButton_notifyEvent;
    private ImageButton imageButton_emergencyButton;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient mFusedLocationClient;
    private LatLng begin, end;
    private int REQUEST_CHECK_SETTINGS = 1;
    private EditText route;
    private Marker m;

    //GEOCODER LIMITS
    public static final double lowerLeftLatitude = 1.396967;
    public static final double lowerLeftLongitude = -78.903968;
    public static final double upperRightLatitude = 11.983639;
    public static final double upperRigthLongitude = -71.869905;

    private int first;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        m=null;
        first=0;
        route = findViewById(R.id.routeText);
        mLocationRequest = createLocationRequest();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        imageButton_notifyContact = findViewById(R.id.button_notifyContact);
        imageButton_notifyEvent = findViewById(R.id.button_notifyEvent);
        imageButton_emergencyButton = findViewById(R.id.button_emergencyButton);

        mLocationCallback = new LocationCallback() {

            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if (location != null) {

                    LatLng actual = new LatLng(location.getLatitude(), location.getLongitude());
                    if(first==0) {

                        m = mMap.addMarker(new MarkerOptions().position(actual)
                                .icon(BitmapDescriptorFactory
                                        .fromResource(R.drawable.carmarker)));

                        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(actual));
                    }
                    m.setPosition(actual);
                    first=1;
                    begin = actual;
                }
            }
        };

        imageButton_notifyContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(),Notify_Emergency_Contact.class));

            }
        });

        imageButton_notifyEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),NotifyEventActivity.class);
                startActivity(intent);
            }
        });

        imageButton_emergencyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        route.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            geoCoderFind();

                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
    }

//CALLBACKS

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_PERMISSON: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    locateMap();

                break;
            }
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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapStyle(MapStyleOptions
                .loadRawResourceStyle(this, R.raw.mapstyle));
        if (Utils.requestPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, "Se necesita acceder a la cámara", LOCATION_PERMISSON))
            locateMap();
    }
    @Override
    protected void onResume() {
        super.onResume();
        locateMap();
    }
    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    //METHODS
    protected LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(16); //tasa de refresco en milisegundos
        mLocationRequest.setFastestInterval(16); //máxima tasa de refresco
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }
    public void locateMap() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationSettingsRequest.Builder builder = new
                    LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
            SettingsClient client = LocationServices.getSettingsClient(this);

            Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

            task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                    if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
                    }
                }
            });
            task.addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    int statusCode = ((ApiException) e).getStatusCode();
                    switch (statusCode) {
                        case CommonStatusCodes.RESOLUTION_REQUIRED:
                            try {// Show the dialog by calling startResolutionForResult(), and check the result in onActivityResult().
                                ResolvableApiException resolvable = (ResolvableApiException) e;
                                resolvable.startResolutionForResult(HomeActivity.this,
                                        REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException sendEx) {
                            } break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            break;
                    }
                }
            });

        }
    }
    private void stopLocationUpdates(){
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }
    private void geoCoderFind() {
        Geocoder mGeocoder = new Geocoder(getBaseContext());
        String addressString = route.getText().toString();
        if (!addressString.isEmpty()) {
            try {
                List<Address> addresses = mGeocoder.getFromLocationName(addressString, 2, lowerLeftLatitude, lowerLeftLongitude,
                        upperRightLatitude, upperRigthLongitude);
                if (addresses != null && !addresses.isEmpty()) {
                    Address addressResult = addresses.get(0);
                    LatLng position = new LatLng(addressResult.getLatitude(), addressResult.getLongitude());
                    if (mMap != null) {
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(begin)
                                .icon(BitmapDescriptorFactory
                                        .fromResource(R.drawable.carmarker)));

                        mMap.addMarker(new MarkerOptions().position(position)
                                .title(addressString)
                                .icon(BitmapDescriptorFactory
                                        .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                        mMap.moveCamera(CameraUpdateFactory.zoomTo(10));
                        end = position;
                        LatLng mitad = new LatLng( (begin.latitude+end.latitude)/2 ,(begin.longitude+end.longitude)/2 );
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(position));

                        routeCalculate();

                    }
                } else {
                    Toast.makeText(getBaseContext(), "Dirección no encontrada", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getBaseContext(), "La dirección esta vacía", Toast.LENGTH_SHORT).show();
        }
    }

    private void routeCalculate() {
        RouteCalculator rc = new RouteCalculator(begin,end,mMap);
        String url = rc.requestUrl();
        RouteCalculator.TaskRequestDirections taskRequestDirections = new RouteCalculator.TaskRequestDirections();
        taskRequestDirections.execute(url);
    }
}

