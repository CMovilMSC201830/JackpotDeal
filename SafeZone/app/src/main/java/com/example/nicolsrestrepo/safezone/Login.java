package com.example.nicolsrestrepo.safezone;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends AppCompatActivity {

    public EditText name;
    public EditText password;
    public Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        name = (EditText) findViewById(R.id.loginname);
        password = (EditText) findViewById(R.id.loginpassword);
        login = (Button) findViewById(R.id.loginbtn);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(verifyLogin())
                    startActivity(new Intent(getBaseContext(),HomeActivity.class));
            }
        });
    }

    private boolean verifyLogin() {

        if( TextUtils.isEmpty(name.getText().toString()) || TextUtils.isEmpty(password.getText().toString())) {
            Toast.makeText(getBaseContext(), getString(R.string.allfields), Toast.LENGTH_SHORT).show();
            return false;
        }
        //MOCK ACCOUNT
        if(name.getText().toString().equals("test") && password.getText().toString().equals("123"))
            return true;
        else
            Toast.makeText(getBaseContext(),getString(R.string.invalidaccount),Toast.LENGTH_SHORT).show();
        return false;
    }
}
