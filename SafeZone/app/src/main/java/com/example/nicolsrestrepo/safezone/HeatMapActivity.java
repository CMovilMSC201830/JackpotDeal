package com.example.nicolsrestrepo.safezone;

import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.nicolsrestrepo.safezone.ObjetosNegocio.EventInformation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.util.ArrayList;
import java.util.List;

public class HeatMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng initialMarker;
    private FirebaseFirestore db;
    private ListenerRegistration queryListener;
    private final static String EVENTS_PATH = "eventos";
    private List<EventInformation> reportedEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heat_map);
        initialMarker = getIntent().getParcelableExtra("location");
        db = FirebaseFirestore.getInstance();
        reportedEvents = new ArrayList<>();
        loadRealTimeEvents();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        queryListener.remove();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRealTimeEvents();
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
        mMap.addMarker(new MarkerOptions()
                .position(initialMarker)
                .title("Tu posición"));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(10));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(initialMarker));
        loadRealTimeEvents();
    }

    public void loadRealTimeEvents(){
        Query query =  db.collection(EVENTS_PATH);
        queryListener = query.addSnapshotListener(new com.google.firebase.firestore.EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("TAG ERROR", "listen:error", e);
                    return;
                }
                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    EventInformation evento = dc.getDocument().toObject(EventInformation.class);
                        reportedEvents.add(evento);
                }

                drawHeatMap();
            }
        });

    }

    public void drawHeatMap(){
        ArrayList<LatLng> list = new ArrayList<LatLng>();

        for(EventInformation event: reportedEvents){
            LatLng item = new LatLng(
                    event.getPosition().getLatitude(),
                    event.getPosition().getLongitude());
            list.add(item);
        }

        HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder()
                .data(list)
                .build();

        TileOverlay mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));

    }


}
