package com.example.nicolsrestrepo.safezone;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class Notify_Emergency_Contact extends AppCompatActivity {
    private final int CONTACTS_PERMISSION = 1;
    public ListView list;
    private Button getChoice;
    String[] listContent = {"Emergency Contact 1","Emergency Contact 2","Emergency Contact 3" ,
            "Emergency Contact 4","Emergency Contact 5","Emergency Contact 6","Emergency Contact 7",
            "Emergency Contact 8","Emergency Contact 9","Emergency Contact 10","Emergency Contact 11"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify__emergency__contact);
        list = (ListView) findViewById(R.id.emergency_contacts_lists);

        getChoice = (Button) findViewById(R.id.getchoice);
        ArrayAdapter < String > adapter = new ArrayAdapter < String > (this, android.R.layout.simple_list_item_multiple_choice, listContent);

      list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        list.setAdapter(adapter);
        getChoice.setOnClickListener(new Button.OnClickListener() {
            @Override

            public void onClick(View v) {
                validateNotification();
            }
        });
    }

    private void validateNotification() {
        ArrayList<String> selected = new ArrayList<String>();
        int cntChoice = list.getCount();

        SparseBooleanArray sparseBooleanArray = list.getCheckedItemPositions();

        for (int i = 0; i < cntChoice; i++) {

            if (sparseBooleanArray.get(i)) {
                selected.add(list.getItemAtPosition(i).toString());
            }
        }

        makeDialog(selected);

    }

    private void makeDialog(ArrayList<String> selected) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        Toast.makeText(getBaseContext(),"Notificaciónes enviadas",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Notify_Emergency_Contact.this,HomeActivity.class));
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(Notify_Emergency_Contact.this);
        builder.setMessage("Desea enviar notificación a "+ selected.size()+ " contactos?").setPositiveButton("Si", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }
}