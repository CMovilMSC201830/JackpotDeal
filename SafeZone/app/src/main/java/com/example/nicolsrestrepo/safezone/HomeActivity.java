package com.example.nicolsrestrepo.safezone;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class HomeActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ImageButton imageButton_notifyContact;
    private ImageButton imageButton_notifyEvent;
    private ImageButton imageButton_emergencyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        imageButton_notifyContact = findViewById(R.id.button_notifyContact);
        imageButton_notifyEvent = findViewById(R.id.button_notifyEvent);
        imageButton_emergencyButton = findViewById(R.id.button_emergencyButton);

        imageButton_notifyContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

        // Add a marker in Bogota and move the camera
        LatLng bogota = new LatLng(4.65, -74.05);
        mMap.addMarker(new MarkerOptions().position(bogota).title("Marcador en Bogotá"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(bogota));

        mMap.moveCamera(CameraUpdateFactory.zoomTo(10));
    }
}
