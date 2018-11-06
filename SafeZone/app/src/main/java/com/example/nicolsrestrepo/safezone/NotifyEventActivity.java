package com.example.nicolsrestrepo.safezone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ScrollView;

public class NotifyEventActivity extends Activity {

    private int screenWidth;
    private int screenHeight;

    private int newScreenWidth;
    private int newScreenHeight;

    private ScrollView scrollView_events;
    private GridLayout gridLayout_events;

    private ImageButton stealPersonEvent;
    private ImageButton stealCompanyEvent;
    private ImageButton homicideEvent;
    private ImageButton homicideIntentEvent;
    private ImageButton kidnappingEvent;
    private ImageButton extorsionEvent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify_event);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        newScreenWidth = (int) (screenWidth*0.9);
        newScreenHeight = (int) (screenHeight*0.8);

        getWindow().setLayout(newScreenWidth, newScreenHeight);

        scrollView_events = findViewById(R.id.scrollView_events);
        gridLayout_events = findViewById(R.id.gridLayout_events);

        ViewGroup.LayoutParams layoutParamsSV = scrollView_events.getLayoutParams();
        layoutParamsSV.width = newScreenWidth;
        layoutParamsSV.height = newScreenHeight;
        scrollView_events.setLayoutParams(layoutParamsSV);

        stealPersonEvent = (ImageButton) findViewById(R.id.button_evento_hurtoPersonas);
        stealPersonEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startReportEventActivity(view);
            }
        });

        stealCompanyEvent = (ImageButton) findViewById(R.id.button_evento_hurtoEmpresas);
        stealCompanyEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startReportEventActivity(view);
            }
        });

        homicideEvent = (ImageButton) findViewById(R.id.button_evento_homicidio);
        homicideEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startReportEventActivity(view);
            }
        });

        kidnappingEvent = (ImageButton) findViewById(R.id.button_evento_secuestro);
        kidnappingEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startReportEventActivity(view);
            }
        });

        extorsionEvent = (ImageButton) findViewById(R.id.button_evento_extorsion);
        extorsionEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startReportEventActivity(view);
            }
        });

        homicideIntentEvent = (ImageButton) findViewById(R.id.button_evento_intentoHomicidio);
        homicideIntentEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startReportEventActivity(view);
            }
        });


        /*
        ViewGroup.LayoutParams layoutParamsGL = gridLayout_events.getLayoutParams();
        layoutParamsGL.width = newScreenWidth;
        layoutParamsGL.height = newScreenHeight;
        gridLayout_events.setLayoutParams(layoutParamsGL);
        */

    }

    public void startReportEventActivity(View view) {
        Intent intent = new Intent(view.getContext(), ReportEventActivity.class);
        startActivity(intent);
    }
}
