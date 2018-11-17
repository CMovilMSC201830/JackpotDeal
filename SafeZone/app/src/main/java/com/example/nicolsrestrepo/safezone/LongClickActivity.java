package com.example.nicolsrestrepo.safezone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.model.LatLng;

public class LongClickActivity extends Activity {

    private int screenWidth;
    private int screenHeight;
    private int newScreenWidth;
    private int newScreenHeight;

    private Button button_crearRuta;
    private Button button_reportarEvento;

    private LatLng ubicacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_long_click);

        // --------------------------------------------------------------------

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        newScreenWidth = (int) (screenWidth*0.9);
        newScreenHeight = (int) (screenHeight*0.2);

        getWindow().setLayout(newScreenWidth, newScreenHeight);

        // --------------------------------------------------------------------

        ubicacion = getIntent().getParcelableExtra("location");

        button_crearRuta = findViewById(R.id.button_here_createRoute);
        button_reportarEvento = findViewById(R.id.button_here_reportEvent);


        button_crearRuta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = new Intent();

                Bundle bundle = new Bundle();
                bundle.putParcelable("location",ubicacion);
                bundle.putString("action",getString(R.string.action_crearRuta));

                returnIntent.putExtra("result",bundle);

                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });

        button_reportarEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = new Intent();

                Bundle bundle = new Bundle();
                bundle.putParcelable("location",ubicacion);
                bundle.putString("action",getString(R.string.action_reportarEvento));

                returnIntent.putExtra("result",bundle);

                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });

    }
}
