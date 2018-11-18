package com.example.nicolsrestrepo.safezone;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nicolsrestrepo.safezone.ObjetosNegocio.TripInformation;
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

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class MyTrips extends AppCompatActivity {

    private final static int EXTERNAL_PERMISSON = 0;

    private final static String USERS_PATH = "usuarios";
    private final static String TRIPS_PATH = "viajes";

    private LinearLayout linear;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    FirebaseUser currentFirebaseUser ;
    private ArrayList<TripInformation> trips;
    private ArrayList<String> ids;
    private StorageReference mStorageRef;
    private Button localCopy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_trips);


        ids=new ArrayList<String>();
        trips = new ArrayList<TripInformation>();
        db = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        linear = findViewById(R.id.myLinear);
        localCopy = findViewById(R.id.savaLocalBtn);
        localCopy.setVisibility(View.INVISIBLE);

        Toast.makeText(getBaseContext(),"Haga Click en la imagen para ver mas detalles",Toast.LENGTH_SHORT).show();
        localCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveLocal();
            }
        });
        loadData();

    }



    //METHODS

    public void loadData(){
        //db.collection("MyTrips-"+currentFirebaseUser.getUid())
        db.collection(USERS_PATH).document(currentFirebaseUser.getUid()).collection(TRIPS_PATH)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<DocumentSnapshot> myListOfDocuments = task.getResult().getDocuments();
                                for(DocumentSnapshot sd: myListOfDocuments){
                                    trips.add(sd.toObject(TripInformation.class));
                                    ids.add(sd.getId());
                                }

                            printData();
                        }
                    }
                });
    }


    private void printData() {

        int index =0;
        for(final TripInformation tp: trips){
            ImageButton iv = new ImageButton(this);
            iv.setBackgroundResource(R.drawable.loading);
            setPicture(iv,index);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    details(tp);
                }
            });
            linear.addView(iv);
            index++;
        }

        localCopy.setVisibility(View.VISIBLE);

    }

    private void details(TripInformation tp) {
        Intent i = new Intent(MyTrips.this,MyTripDetail.class);
        i.putExtra("trip", (Serializable) tp);
        startActivity(i);
    }

    public void setPicture(final ImageView iv, int index){
        StorageReference imageRef = mStorageRef.child(currentFirebaseUser.getUid()+"/MyTrips/"+ids.get(index)+".jpg");
        final long ONE_MEGABYTE = 1024 * 1024;
        imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                iv.setImageResource(android.R.color.transparent);
                iv.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

    private void saveLocal() {

        if (Utils.requestPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, "Se necesita acceder a el almacenamiento", EXTERNAL_PERMISSON))
            writeJSONObject();

    }

    private void writeJSONObject(){
        ArrayList<JSONObject> Jtrips = new ArrayList<>();
        for(TripInformation tp:trips)
        Jtrips.add(tp.ToJson());
        Writer output = null;
        String filename= currentFirebaseUser.getUid()+"MisViajes.json";
        try {
            File file = new File(getBaseContext().getExternalFilesDir(null), filename);
            output = new BufferedWriter(new FileWriter(file));
            output.write(Jtrips.toString());
            output.close();
            Toast.makeText(getApplicationContext(), "Copia Local Creada",
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //CALLBACKS

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case EXTERNAL_PERMISSON: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    saveLocal();

                break;
            }
        }
    }


}
