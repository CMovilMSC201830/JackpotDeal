package com.example.nicolsrestrepo.safezone;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class SignUp extends AppCompatActivity {
    public EditText name;
    public EditText phone;
    public EditText pass;
    public EditText conf_pass;
    public CheckBox checkbox;
    public Button signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        name=(EditText) findViewById(R.id.signupname);
        phone=(EditText) findViewById(R.id.signupphone);
        pass=(EditText) findViewById(R.id.signuppass);
        conf_pass=(EditText) findViewById(R.id.signupconfirmpass);
        checkbox = (CheckBox) findViewById(R.id.signupcheck);
        signup = (Button) findViewById(R.id.signupbtn);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(true)
                    startActivity(new Intent(getBaseContext(),HomeActivity.class));
            }
        });

    }

    private boolean validateForm() {
        if(TextUtils.isEmpty(name.getText().toString()) || TextUtils.isEmpty(phone.getText().toString()) ||
                TextUtils.isEmpty(pass.getText().toString()) || TextUtils.isEmpty(conf_pass.getText().toString()) || !checkbox.isChecked()){
            Toast.makeText(getBaseContext(), getString(R.string.allfields), Toast.LENGTH_SHORT).show() ;
            return false;
        }else if(!name.getText().toString().equals(phone.getText().toString())){
            Toast.makeText(getBaseContext(), getString(R.string.nomatchinspwrds), Toast.LENGTH_SHORT).show() ;
            return false;
        }
        return true;

    }

}
