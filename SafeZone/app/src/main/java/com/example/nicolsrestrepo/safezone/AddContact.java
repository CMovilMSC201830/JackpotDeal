package com.example.nicolsrestrepo.safezone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.example.nicolsrestrepo.safezone.ObjetosNegocio.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AddContact extends AppCompatActivity {

    private Context thisContext = this;
    private Activity thisActivity = this;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private final static String TAG = "LogAddContact";

    private final static String USERS_PATH = "usuarios";

    private ConstraintLayout parentLayout;
    private AutoCompleteTextView autoCompleteTextView_busquedaUsuario;

    private List<Usuario> usuarios = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        parentLayout = findViewById(R.id.layout_addContact);

        cargarUsuarios();
    }

    private void cargarUsuarios(){
        db.collection(USERS_PATH)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                String currentUserID = mAuth.getCurrentUser().getUid();

                                if( !document.getId().equals(currentUserID) ){
                                    Usuario usuario = document.toObject(Usuario.class);
                                    usuarios.add(usuario);
                                }
                            }

                            autoCompleteTextView_busquedaUsuario = findViewById(R.id.autoComplete_userSearch);
                            ArrayAdapter<Usuario> adapter = new ArrayAdapter<>(thisContext,android.R.layout.simple_list_item_1, usuarios);
                            autoCompleteTextView_busquedaUsuario.setAdapter(adapter);

                            autoCompleteTextView_busquedaUsuario.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    hideKeyboard(thisActivity);

                                    Object item = adapterView.getItemAtPosition(i);
                                    if( item instanceof Usuario){
                                        Usuario usuarioSeleccionado = (Usuario) item;
                                        agregarContacto(usuarioSeleccionado);
                                    }
                                }
                            });

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void agregarContacto(final Usuario usuarioSeleccionado){
        db.collection(USERS_PATH)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                final String idUsuario = document.getId();
                                Usuario usuario = document.toObject(Usuario.class);

                                if(usuario.getCorreo().equals(usuarioSeleccionado.getCorreo())){
                                    final FirebaseUser firebaseUser = mAuth.getCurrentUser();

                                    DocumentReference docRef = db.collection(USERS_PATH).document(firebaseUser.getUid());
                                    docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            Usuario usuarioActual = documentSnapshot.toObject(Usuario.class);
                                            Log.d(TAG,"Usuario actual:" + usuarioActual.toString());
                                            if(usuarioActual != null){
                                                if( !usuarioActual.getListaContactos().contains(idUsuario) ){
                                                    usuarioActual.getListaContactos().add(idUsuario);
                                                    db.collection(USERS_PATH).document(firebaseUser.getUid()).set(usuarioActual);
                                                }else{
                                                    //Snackbar.make(parentLayout, "Ya tenías este contacto", Snackbar.LENGTH_SHORT).show();
                                                    Toast.makeText(thisContext,"Ya tenías este contacto",Toast.LENGTH_SHORT).show();
                                                }
                                                startActivity(new Intent(AddContact.this,Notify_Emergency_Contact.class));
                                                finish();
                                            }
                                        }
                                    });
                                }
                            }

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
