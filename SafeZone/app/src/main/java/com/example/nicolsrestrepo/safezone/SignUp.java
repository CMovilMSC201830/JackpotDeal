package com.example.nicolsrestrepo.safezone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;

public class SignUp extends AppCompatActivity {
    public EditText name;
    public EditText phone;
    public EditText pass;
    public EditText conf_pass;
    public CheckBox checkbox;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        name=(EditText) findViewById(R.id.signupname);
        phone=(EditText) findViewById(R.id.signupphone);
        pass=(EditText) findViewById(R.id.signuppass);
        conf_pass=(EditText) findViewById(R.id.signupconfirmpass);
        checkbox = (CheckBox) findViewById(R.id.signupcheck);


    }

}
