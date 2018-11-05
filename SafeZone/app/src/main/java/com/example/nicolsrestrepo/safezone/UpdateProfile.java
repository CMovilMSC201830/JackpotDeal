package com.example.nicolsrestrepo.safezone;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.EmbossMaskFilter;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class UpdateProfile extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;

    private TextView tvname,tvphone,tvpassword,tvconfirpw,tvmail;
    private ImageView profilePic;
    private Button camera,gallery,update;
    private Boolean pictureUpdated;
    private StorageReference mStorageRef;
    private static Boolean loading = false;

    private final static int GALLERY_PERMISSON = 0;
    private final static int CAMERA_PERMISSON = 1;

    private static final int IMAGE_PICKER_REQUEST = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        Log.d("updatedProfile", ""+savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        db = FirebaseFirestore.getInstance();
        tvmail = findViewById(R.id.email);
        tvname = findViewById(R.id.signupname);
        tvphone = findViewById(R.id.signupphone);
        tvpassword = findViewById(R.id.signuppass);
        tvconfirpw = findViewById(R.id.signupconfirmpass);
        profilePic = findViewById(R.id.signuppic);
        camera = findViewById(R.id.cameraBtn);
        gallery = findViewById(R.id.galerryBtn);
        update = findViewById(R.id.updateProfileBtn);

        pictureUpdated = false;
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.requestPermission(UpdateProfile.this, Manifest.permission.CAMERA, "Se necesita acceder a la cámara", CAMERA_PERMISSON))
                    initCamera();
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.requestPermission(UpdateProfile.this, Manifest.permission.READ_EXTERNAL_STORAGE, "Se necesita acceder a la galeria", GALLERY_PERMISSON))
                    initGallery();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
            }
        });

        updateViews(savedInstanceState);
    }

    //METHODS

    public void updateViews(Bundle savedInstanceState){
        String name = new String();
        String email = new String();
        if (firebaseUser != null) {
            email = firebaseUser.getEmail();
            name = firebaseUser.getDisplayName();
        }
        DocumentReference docRef = db.collection("MyTrips-"+firebaseUser.getUid())
                .document("Phone Number");

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        tvphone.setText((String)document.get("phone"));
                    }
                }
            }
        });

        tvname.setText(name);
        tvmail.setText(email);
        if(savedInstanceState == null) {
            loadPicture();

        }
    }

    public void initCamera() {
        Log.d("updatedProfile", "Init Camera");
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    //CALLBACKS



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case CAMERA_PERMISSON: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    initCamera();
                break;
            }
            case GALLERY_PERMISSON: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    initGallery();

                break;
            }
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case IMAGE_PICKER_REQUEST:
                if (resultCode == RESULT_OK) {
                    try {
                        final Uri imageUri = data.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        profilePic.setImageDrawable(null);
                        profilePic.setImageBitmap(selectedImage);
                        pictureUpdated = true;
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            case REQUEST_IMAGE_CAPTURE:
                if (resultCode == RESULT_OK){
                    profilePic.setImageDrawable(null);
                    Log.d("updatedProfile", "Poniendo imagen.");
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    profilePic.setImageBitmap(imageBitmap);
                    pictureUpdated = true;


                }
                break;

        }
    }

    public void initGallery() {
        Intent pickImage = new Intent(Intent.ACTION_PICK);
        pickImage.setType("image/*");
        startActivityForResult(pickImage, IMAGE_PICKER_REQUEST);
    }

    public void updateProfile(){

        if(pictureUpdated){
            uploadPicture();
        }

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(tvname.getText().toString())
                .build();

        firebaseUser.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("updatedProfile", "User profile updated.");
                        }
                    }
                });

        HashMap<String,String> phone = new HashMap<String,String>();
        phone.put("phone",tvphone.getText().toString());
        db.collection("MyTrips-"+firebaseUser.getUid()).document("Phone Number").set(phone);

        startActivity(new Intent(UpdateProfile.this,HomeActivity.class));
    }

    public void uploadPicture(){
        StorageReference imageRef = mStorageRef.child(firebaseUser.getUid()+"/profilePicture.jpg");
        profilePic.setDrawingCacheEnabled(true);
        profilePic.buildDrawingCache();

        Bitmap bitmap = ((BitmapDrawable) profilePic.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = imageRef.putBytes(data);
    }

    public void loadPicture(){

        Log.d("updatedProfile", "lOADING.");
        StorageReference imageRef = mStorageRef.child(firebaseUser.getUid()+"/profilePicture.jpg");
        final long ONE_MEGABYTE = 1024 * 1024;
        imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                profilePic.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("updatedProfile", "No profile Picture.");
            }
        });
    }

}
