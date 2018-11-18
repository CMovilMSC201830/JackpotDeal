package com.example.nicolsrestrepo.safezone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.nicolsrestrepo.safezone.ObjetosNegocio.TripInformation;

public class MyTripDetail extends AppCompatActivity {
    private TextView destino,distancia,tiempo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_trip_detail);

        destino = findViewById(R.id.trip_destino);
        distancia = findViewById(R.id.trip_distancia);
        tiempo = findViewById(R.id.trip_tiempo);

        printInfo();
    }
    public void printInfo(){
        TripInformation tp = (TripInformation) getIntent().getSerializableExtra("trip");
        destino.setText("Destino: "+tp.getDestino());
        distancia.setText("Distancia: "+tp.getDistancia()+ " km");
        tiempo.setText("Tiempo: " + tp.getTime() + " minutos");

    }
}
