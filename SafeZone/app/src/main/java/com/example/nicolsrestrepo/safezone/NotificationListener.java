package com.example.nicolsrestrepo.safezone;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.nicolsrestrepo.safezone.ObjetosNegocio.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationListener extends IntentService {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    public static String CHANNEL_ID = "MyApp";
    int notificationId = 0;
    Boolean changed;


    public NotificationListener() {
        super("NotificationListener");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        listen();
    }

    private void listen() {

        DocumentReference docRef = db.collection("notificaciones").document(mAuth.getCurrentUser().getEmail());
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot snapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d("listenerlog",("Listen failed: " + e));
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    changed = false;
                    createNotification(snapshot.getData());
                } else {
                    Log.d("listenerlog","Current data: null");
                }
            }
        });
    }

    public void createNotification(Map<String, Object> data){
        createNotificationChannel();
        int notificationId = 000;
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if(entry.getKey()!=null && entry.getValue()!= null && entry != null)
            Log.d("listenerlog",entry.getKey() + ":" + entry.getValue().toString());
            else {
               changed = true;
                getUserName(entry.getKey());
                Map<String, Object> hm = new HashMap<>();
                hm.put(entry.getKey(),true);
                db.collection("notificaciones").document(mAuth.getCurrentUser().getEmail()).update(hm);
                if(!HomeActivity.interest.contains(entry.getKey())) {
                    HomeActivity.interest.add(entry.getKey());
                    if (changed) {
                        notifyHome();
                        changed = false;
                    }
                }
            }
        }


    }

    private void notifyHome() {
        Intent intent = new Intent("newFriendNot");
// You can also include some extra data.
        intent.putExtra("key", "change");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void getUserName(String key) {
        db.collection("usuarios").document(key).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Usuario usuario = document.toObject(Usuario.class);
                        notifyContact(usuario.getCorreo());
                        Log.d("listenerlog", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("listenerlog", "No such document");
                    }
                } else {
                    Log.d("listenerlog", "get failed with ", task.getException());
                }
            }
        });


    }

    private void notifyContact(String correo) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
        mBuilder.setSmallIcon(R.drawable.ic_notify_contact);
        mBuilder.setContentTitle(correo+" te notifico sobre un viaje");
        mBuilder.setContentText("Rastrea su ubicación");
        mBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setAutoCancel(true); //Remueve la notificación cuando se toca
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);
// notificationId es un entero unico definido para cada notificacion que se lanza
        notificationManager.notify(notificationId++, mBuilder.build());

    }

    private void createNotificationChannel() {
// Create the NotificationChannel, but only on API 26+ because
// the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "channel";
            String description = "channel description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
//IMPORTANCE_MAX MUESTRA LA NOTIFICACIÓN ANIMADA
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
// Register the channel with the system; you can't change the importance
// or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}

