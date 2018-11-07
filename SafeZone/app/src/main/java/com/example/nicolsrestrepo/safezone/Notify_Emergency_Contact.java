package com.example.nicolsrestrepo.safezone;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.nicolsrestrepo.safezone.ObjetosNegocio.Usuario;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Notify_Emergency_Contact extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private Context thisContext = this;

    private final static String USERS_PATH = "usuarios";

    private final int CONTACTS_PERMISSION = 1;

    public ListView listView;
    private Button button_notificar, button_agregarContacto;

    /*
    String[] listContent = {"Emergency Contact 1","Emergency Contact 2","Emergency Contact 3" ,
            "Emergency Contact 4","Emergency Contact 5","Emergency Contact 6","Emergency Contact 7",
            "Emergency Contact 8","Emergency Contact 9","Emergency Contact 10","Emergency Contact 11"
    };
    */

    List<Usuario> contactos = new ArrayList<>();

    Usuario usuarioActual = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify__emergency__contact);
        listView = (ListView) findViewById(R.id.emergency_contacts_lists);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        button_notificar = findViewById(R.id.button_notificar);
        button_agregarContacto = findViewById(R.id.button_agregarContacto);

        button_agregarContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),AddContact.class);
                startActivity(intent);
                finish();
            }
        });

        ArrayAdapter<Usuario> adapter = new ArrayAdapter<Usuario>(thisContext, android.R.layout.simple_list_item_multiple_choice, contactos);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setAdapter(adapter);
        button_notificar.setOnClickListener(new Button.OnClickListener() {
            @Override

            public void onClick(View v) {
                validateNotification();
            }
        });

        cargarDatosUsuario();
    }

    private void cargarDatosUsuario() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        DocumentReference docRef = db.collection(USERS_PATH).document(firebaseUser.getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Usuario usuario = documentSnapshot.toObject(Usuario.class);
                if(usuario != null){
                    usuarioActual = usuario;

                    for (String idUsuario:usuarioActual.getListaContactos()) {
                        DocumentReference docRef = db.collection(USERS_PATH).document(idUsuario);
                        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                Usuario usuario = documentSnapshot.toObject(Usuario.class);
                                if(usuario != null){
                                    contactos.add(usuario);
                                    ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    private void validateNotification() {
        ArrayList<String> selected = new ArrayList<String>();
        int cntChoice = listView.getCount();

        SparseBooleanArray sparseBooleanArray = listView.getCheckedItemPositions();

        for (int i = 0; i < cntChoice; i++) {

            if (sparseBooleanArray.get(i)) {
                selected.add(listView.getItemAtPosition(i).toString());
            }
        }

        if(selected.size() > 0){
            makeDialog(selected);
        }

    }

    private void makeDialog(ArrayList<String> selected) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        Toast.makeText(getBaseContext(), "Notificaciónes enviadas", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Notify_Emergency_Contact.this, HomeActivity.class));
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(Notify_Emergency_Contact.this);
        builder.setMessage("Desea enviar notificación a " + selected.size() + " contactos?").setPositiveButton("Si", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }
}