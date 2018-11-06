package com.example.nicolsrestrepo.safezone;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.nicolsrestrepo.safezone.ObjetosNegocio.EventInformation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ReportEventActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private EventInformation eventInfo;
    private Button loadReport;
    private final static String USERS_PATH = "usuarios";
    private final static String EVENTS_PATH = "eventos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_event);

        loadReport = findViewById(R.id.report_event);

        loadReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                String reportTime = String.valueOf(System.currentTimeMillis());
                db.collection(USERS_PATH).document(currentFirebaseUser.getUid()).collection(EVENTS_PATH).document(reportTime).set(eventInfo);
                Intent intent = new Intent(view.getContext(), HomeActivity.class);
                startActivity(intent);
            }


        });
    }
}
