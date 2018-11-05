package com.example.nicolsrestrepo.safezone;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SignUp extends AppCompatActivity {

    private final static String TAG = "LogSignUp";

    public EditText editText_email, editText_name, editText_phone, editText_pass, editText_conf_pass;
    public CheckBox checkbox;
    public Button signup;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        editText_email = findViewById(R.id.signupmail);
        editText_name = findViewById(R.id.signupname);
        editText_phone = findViewById(R.id.signupphone);
        editText_pass = findViewById(R.id.signuppass);
        editText_conf_pass = findViewById(R.id.signupconfirmpass);
        checkbox = findViewById(R.id.signupcheck);
        signup = findViewById(R.id.signupbtn);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateForm()) {
                    registrarUsuario(editText_email.getText().toString(), editText_pass.getText().toString());
                }
            }
        });

    }

    private void updateUI(FirebaseUser user){
        if( user != null ){
            Intent intent = new Intent(SignUp.this,HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private boolean validateForm() {
        boolean valido = true;

        if(editText_email.getText().toString().isEmpty()){
            editText_email.setError("Campo necesario");
            valido = false;
        }

        if(editText_name.getText().toString().isEmpty()){
            editText_name.setError("Campo necesario");
            valido = false;
        }

        if(editText_phone.getText().toString().isEmpty()){
            editText_phone.setError("Campo necesario");
            valido = false;
        }

        if(editText_pass.getText().toString().isEmpty()){
            editText_pass.setError("Campo necesario");
            valido = false;
        }

        if(editText_conf_pass.getText().toString().isEmpty()){
            editText_conf_pass.setError("Campo necesario");
            valido = false;
        }

        if(!checkbox.isChecked()){
            Toast.makeText(this, "Debes aceptar los términos y condiciones", Toast.LENGTH_SHORT).show();
            valido = false;
        }

        if(!editText_pass.getText().toString().equals(editText_conf_pass.getText().toString())){
            Toast.makeText(getBaseContext(), R.string.nomatchinspwrds, Toast.LENGTH_SHORT).show() ;
            valido = false;
        }

        return valido;

    }

    private void registrarUsuario(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            final FirebaseUser user = mAuth.getCurrentUser();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(editText_name.getText().toString())
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "User profile updated.");

                                                updateUI(user);
                                            }
                                        }
                                    });
                                    savePhone();



                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUp.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    public void savePhone(){
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        HashMap<String,String> phone = new HashMap<String,String>();
        phone.put("phone",editText_phone.getText().toString());
        db.collection("MyTrips-"+currentFirebaseUser.getUid()).document("Phone Number").set(phone);
    }


}
