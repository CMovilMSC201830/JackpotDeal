package com.example.nicolsrestrepo.safezone;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.nicolsrestrepo.safezone.ObjetosNegocio.EventInformation;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ReportEventActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private EventInformation eventInfo;

    private TextView tituloEvento;
    private EditText editText_zona, editText_hora, editText_fecha, editText_detalles;
    private Button button_loadReport;
    private Calendar myCalendar = Calendar.getInstance();

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

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        tituloEvento = findViewById(R.id.textView_tituloEvento);

        button_loadReport = findViewById(R.id.report_event);
        editText_zona = findViewById(R.id.editText_zonaEvento);
        editText_hora = findViewById(R.id.editText_horaEvento);
        editText_fecha = findViewById(R.id.editText_fechaEvento);
        editText_detalles = findViewById(R.id.editText_detallesEvento);
        tituloEvento.setText(evento);

        editText_fecha.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(ReportEventActivity.this, R.style.TimePickerTheme ,reportedDateListener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        editText_hora.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(ReportEventActivity.this, R.style.TimePickerTheme, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        editText_hora.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Hora del Evento");
                mTimePicker.show();
            }
        });

        button_loadReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String reportTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date());
                GeoPoint position = new GeoPoint(posicion.latitude,posicion.longitude);

                eventInfo = new EventInformation();
                eventInfo.setZone(editText_zona.getText().toString());
                eventInfo.setTime(editText_hora.getText().toString());
                eventInfo.setDate(editText_fecha.getText().toString());
                eventInfo.setDetails(editText_detalles.getText().toString());
                eventInfo.setPosition(position);
                eventInfo.setType(evento);

                //FirebaseUser currentFirebaseUser = mAuth.getCurrentUser();

                if (eventFormIsValid()){
                    db.collection(EVENTS_PATH).document(reportTime).set(eventInfo);

                    Intent intent = new Intent(ReportEventActivity.this,HomeActivity.class);
                    intent.putExtra("bundle",bundle);
                    startActivity(intent);
                    finish();
                }


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

    DatePickerDialog.OnDateSetListener reportedDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, i);
            myCalendar.set(Calendar.MONTH, i1);
            myCalendar.set(Calendar.DAY_OF_MONTH, i2);
            updateLabel();
        }
    };


    private void updateLabel() {
        String myFormat = "dd/MM/yyyy"; // This could be changed
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        editText_fecha.setText(sdf.format(myCalendar.getTime()));
    }

    private boolean validateTextField(EditText input) {
        boolean valid = true;
        String text = input.getText().toString();
        if (TextUtils.isEmpty(text)) {
            input.setError("Requerido");
            valid = false;
        } else {
            input.setError(null);
        }
        return valid;
    }

    private boolean eventFormIsValid() {
        return validateTextField(editText_zona) &
                validateTextField(editText_hora) &
                validateTextField(editText_fecha) &
                validateTextField(editText_detalles);
    }

}
