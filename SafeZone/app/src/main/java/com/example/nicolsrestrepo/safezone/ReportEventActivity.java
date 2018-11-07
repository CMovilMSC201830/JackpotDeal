package com.example.nicolsrestrepo.safezone;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.nicolsrestrepo.safezone.ObjetosNegocio.EventInformation;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ReportEventActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private EventInformation eventInfo;

    private TextView tituloEvento;
    private EditText editText_zona, editText_hora, editText_fecha, editText_detalles;
    private Button button_loadReport;

    private final static String USERS_PATH = "usuarios";
    private final static String EVENTS_PATH = "eventos";
    private final static int LOCATION_PERMISSON = 0;

    String evento = null;
    LatLng posicion = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_event);

        final Bundle bundle = getIntent().getBundleExtra("bundle");

        if( bundle != null){
            evento = bundle.getString("eventType");
            posicion = bundle.getParcelable("ubicacion");
        }


        String evento = getIntent().getStringExtra("eventType");

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        tituloEvento = findViewById(R.id.textView_tituloEvento);

        button_loadReport = findViewById(R.id.report_event);
        editText_zona = findViewById(R.id.editText_zonaEvento);
        editText_hora = findViewById(R.id.editText_horaEvento);
        editText_fecha = findViewById(R.id.editText_fechaEvento);
        editText_detalles = findViewById(R.id.editText_detallesEvento);
        tituloEvento.setText(evento);

        button_loadReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String reportTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date());

                eventInfo = new EventInformation();
                eventInfo.setZone(editText_zona.getText().toString());
                eventInfo.setTime(editText_hora.getText().toString());
                eventInfo.setDate(editText_hora.getText().toString());
                eventInfo.setDetails(editText_detalles.getText().toString());

                FirebaseUser currentFirebaseUser = mAuth.getCurrentUser();

                db.collection(EVENTS_PATH).document(reportTime).set(eventInfo);

                Intent intent = new Intent(ReportEventActivity.this,HomeActivity.class);
                intent.putExtra("bundle",bundle);
                startActivity(intent);
                finish();
            }


        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_PERMISSON: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    //getLocation();

                break;
            }
        }
    }
}
