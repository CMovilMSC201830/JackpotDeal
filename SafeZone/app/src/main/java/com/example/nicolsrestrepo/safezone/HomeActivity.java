package com.example.nicolsrestrepo.safezone;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.nicolsrestrepo.safezone.ObjetosNegocio.EventInformation;
import com.example.nicolsrestrepo.safezone.ObjetosNegocio.TripInformation;
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
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback {

    private final static int LOCATION_PERMISSON = 0;

    private final static String USERS_PATH = "usuarios";
    private final static String TRIPS_PATH = "viajes";
    private final static String EVENTS_PATH = "eventos";

    private final static double eventRadiusSize = 100; //metros

    private GoogleMap mMap;
    private ImageButton imageButton_notifyContact;
    private ImageButton imageButton_notifyEvent;
    private ImageButton imageButton_emergencyButton;
    private ImageView screenShoot;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient mFusedLocationClient;
    private LatLng begin, end;
    private int REQUEST_CHECK_SETTINGS = 1;
    private EditText route;
    private Marker m;
    private FirebaseAuth mAuth;
    private Bitmap trip;
    private StorageReference mStorageRef;
    private FirebaseFirestore db;

    private TripInformation tripInfo;
    private Date current;
    private List<EventInformation> reportedEvents;

    //GEOCODER LIMITS
    public static final double lowerLeftLatitude = 1.396967;
    public static final double lowerLeftLongitude = -78.903968;
    public static final double upperRightLatitude = 11.983639;
    public static final double upperRigthLongitude = -71.869905;

    private int first;

    private boolean comesFromReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        db = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        getSupportActionBar().setTitle("Safe Zone");
        mAuth = FirebaseAuth.getInstance();
        reportedEvents = new ArrayList<EventInformation>();

        comesFromReport = getIntent().hasExtra("bundle");

        m = null;
        end=null;
        first = 0;
        current = new Date();
        tripInfo = new TripInformation();
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
                    if (first == 0) {

                        m = mMap.addMarker(new MarkerOptions().position(actual)
                                .icon(BitmapDescriptorFactory
                                        .fromResource(R.drawable.carmarker)));

                        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(actual));
                    }
                    m.remove();
                    m = mMap.addMarker(new MarkerOptions().position(actual)
                            .icon(BitmapDescriptorFactory
                                    .fromResource(R.drawable.carmarker)));


                    first = 1;
                    begin = actual;
                    if(end != null)
                    checkIfEnd();
                }
            }
        };

        imageButton_notifyContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(), Notify_Emergency_Contact.class));

            }
        });

        imageButton_notifyEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), NotifyEventActivity.class);
                intent.putExtra("actualLocation",begin);
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

        loadReportedEvents();
    }



//CALLBACKS


    //MENU OPTIONS
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.safezonemenu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int itemClicked = item.getItemId();
        if(itemClicked == R.id.logout){
            mAuth.signOut();
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }else if (itemClicked == R.id.profileUpdate){
            startActivity(new Intent(HomeActivity.this,UpdateProfile.class));
        }
        else if (itemClicked == R.id.myTrips){
            startActivity(new Intent(HomeActivity.this,MyTrips.class));
        }
        return super.onOptionsItemSelected(item);
    }

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

        locateInitialMap();
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if (mMap != null) {
                    mMap.clear();
                    drawReportedEvents();
                    mMap.addMarker(new MarkerOptions().position(latLng)
                            .title("Destino Personalizado")
                            .icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                    end = latLng;
                    tripInfo.setDestino("Destino Personalizado");
                    tripInfo.setDistancia(String.format("%.2f", calculateDistance() / 1000));
                }
               routeCalculate();
            }
        });
        if (Utils.requestPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, "Se necesita acceder a la cámara", LOCATION_PERMISSON))
            locateMap();

        if( comesFromReport ){
            Bundle bundle = getIntent().getBundleExtra("bundle");

            LatLng posicionReporte = bundle.getParcelable("ubicacion");
            String tipoDeEvento = bundle.getString("eventType");

            int colorReporte = Color.GRAY;

            switch (tipoDeEvento){
                case "Hurto a personas":
                    colorReporte = Color.argb(50, 255, 255, 0);
                    break;
                case "Hurto a empresas":
                    colorReporte = Color.argb(50, 255, 255, 0);
                    break;
                case "Homicidio":
                    colorReporte = Color.argb(50, 255, 255, 255);;
                    break;
                case "Secuestro":
                    colorReporte = Color.argb(50, 255, 100, 110);
                    break;
                case "Extorsión":
                    colorReporte = Color.argb(50, 0, 0, 255);
                    break;
                case "Intento de homicidio":
                    colorReporte = Color.argb(50, 255, 0, 0);
                    break;
            }

            markDangerZone(posicionReporte.latitude,posicionReporte.longitude,colorReporte,tipoDeEvento);
        }


    }

    private void locateInitialMap() {
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(4.637442,-74.085507)));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(10));
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
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            break;
                    }
                }
            });

        }
    }

    private void stopLocationUpdates() {
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
                        mMap.addMarker(new MarkerOptions().position(position)
                                .title(addressString)
                                .icon(BitmapDescriptorFactory
                                        .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                        mMap.moveCamera(CameraUpdateFactory.zoomTo(10));
                        end = position;
                        tripInfo.setDestino(addressResult.getAddressLine(0));
                        tripInfo.setDistancia(String.format("%.2f", calculateDistance()/1000));
                        LatLng mitad = new LatLng((begin.latitude + end.latitude) / 2, (begin.longitude + end.longitude) / 2);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
                        routeCalculate();
                        current = new Date();


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
        RouteCalculator rc = new RouteCalculator(begin.latitude, begin.longitude, end.latitude, end.longitude, this, mMap, eventRadiusSize, reportedEvents);
        String url = rc.requestUrl();
        RouteCalculator.TaskRequestDirections taskRequestDirections = new RouteCalculator.TaskRequestDirections();
        taskRequestDirections.execute(url);
    }
    private void checkIfEnd() {
        float mts_to_end = 10;
        float distance = calculateDistance();
        if(calculateDistance() <= mts_to_end){
            Toast.makeText(getBaseContext(),"¡Gracias por utilizas Safe Zone para llegar a tu destino!",Toast.LENGTH_LONG).show();
            savePicture();
        }

    }



    //Distance in mts
    private float calculateDistance(){
        Location loc1 = new Location("");
        loc1.setLatitude(begin.latitude);
        loc1.setLongitude(begin.longitude);

        Location loc2 = new Location("");
        loc2.setLatitude(end.latitude);
        loc2.setLongitude(end.longitude);

        return loc1.distanceTo(loc2);
    }

    private void savePicture() {
        takeScreen();
        end = null;

    }

    //Google Map ScreenShot
    public void takeScreen(){
        GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap bitmap) {
                trip = bitmap;
                uploadPicture(trip);
            }
        };
        mMap.snapshot(callback);

    }

    private void uploadPicture(Bitmap bitmap) {
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        String nameFile = String.valueOf(System.currentTimeMillis());
        StorageReference imageRef = mStorageRef.child(currentFirebaseUser.getUid()+"/MyTrips/"+nameFile+".jpg");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = imageRef.putBytes(data);
        trip = null;
        saveInFirestore(currentFirebaseUser.getUid(),nameFile);


    }

    public void saveInFirestore(String uid, String nameFile){
        Date now = new Date();
        double difference = now.getTime() - current.getTime();
        difference = difference/1000;
        difference =  difference/60;
        tripInfo.setTime(String.format("%.2f", difference));
        db.collection(USERS_PATH).document(uid).collection(TRIPS_PATH).document(nameFile).set(tripInfo);
        //db.collection("MyTrips-"+uid).document(nameFile).set(tripInfo);

        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(begin)
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.carmarker)));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(begin));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
    }



    public void markDangerZone(double danger_lat, double danger_lng, int color, String evento){
        LatLng dangerZone = new LatLng(danger_lat, danger_lng);

        mMap.addMarker(new MarkerOptions().position(new LatLng(danger_lat,danger_lng))
                .title(evento)
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));

        CircleOptions circleOptions = new CircleOptions()
                .center(dangerZone)
                .radius(eventRadiusSize) //metros
                .strokeWidth(10)
                .strokeColor(Color.argb(50, 127, 0, 0))
                .fillColor(color)
                .clickable(true);
        mMap.addCircle(circleOptions);
    }

    public void loadReportedEvents(){
        db.collection(EVENTS_PATH)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<DocumentSnapshot> myListOfDocuments = task.getResult().getDocuments();
                            for(DocumentSnapshot sd: myListOfDocuments){
                                reportedEvents.add(sd.toObject(EventInformation.class));;
                            }

                            drawReportedEvents();
                        }
                    }
                });
    }

    public void drawReportedEvents(){
        for(EventInformation event: reportedEvents){


            int colorReporte = Color.GRAY;

            switch (event.getType()){
                case "Hurto a personas":
                    colorReporte = Color.argb(50, 255, 255, 0);
                    break;
                case "Hurto a empresas":
                    colorReporte = Color.argb(50, 255, 255, 0);
                    break;
                case "Homicidio":
                    colorReporte = Color.argb(50, 255, 255, 255);;
                    break;
                case "Secuestro":
                    colorReporte = Color.argb(50, 255, 100, 110);
                    break;
                case "Extorsión":
                    colorReporte = Color.argb(50, 0, 0, 255);
                    break;
                case "Intento de homicidio":
                    colorReporte = Color.argb(50, 255, 0, 0);
                    break;
            }

            markDangerZone(event.getPosition().getLatitude(),
                    event.getPosition().getLongitude(),
                    colorReporte,
                    event.getDetails());
        }
    }
}

