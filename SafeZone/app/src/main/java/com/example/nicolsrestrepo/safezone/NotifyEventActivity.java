package com.example.nicolsrestrepo.safezone;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ScrollView;

public class NotifyEventActivity extends Activity {

    private int screenWidth;
    private int screenHeight;

    private int newScreenWidth;
    private int newScreenHeight;

    private ScrollView scrollView_events;
    private GridLayout gridLayout_events;

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

        /*
        ViewGroup.LayoutParams layoutParamsGL = gridLayout_events.getLayoutParams();
        layoutParamsGL.width = newScreenWidth;
        layoutParamsGL.height = newScreenHeight;
        gridLayout_events.setLayoutParams(layoutParamsGL);
        */

    }
}
