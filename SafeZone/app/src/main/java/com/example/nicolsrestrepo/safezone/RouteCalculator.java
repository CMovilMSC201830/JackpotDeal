package com.example.nicolsrestrepo.safezone;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.example.nicolsrestrepo.safezone.ObjetosNegocio.EventInformation;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RouteCalculator {

    private final static String TAG = "RouteCreation";

    private double beginLatitude;
    private double beginLongitud;
    private double endLatitude;
    private double endLongitud;
    private static GoogleMap mMap;

    private double eventsRadiusSize;
    private List<EventInformation> reportedEvents;

    public RouteCalculator(){
        reportedEvents = new ArrayList<>();
    }

    public RouteCalculator(double beginLatitude, double beginLongitud, double endLatitude, double endLongitud, GoogleMap mMap, double eventsRadiusSize, List<EventInformation> reportedEvents) {
        this.beginLatitude = beginLatitude;
        this.beginLongitud = beginLongitud;
        this.endLatitude = endLatitude;
        this.endLongitud = endLongitud;
        this.mMap = mMap;
        this.eventsRadiusSize = eventsRadiusSize;
        this.reportedEvents = reportedEvents;
    }

    public String requestUrl(){
        Log.i(TAG,"2 "+mMap);

        String api_key = "key=" + "AIzaSyAkvxAqu8N45ui8vtE_wx908odoPQomVUU";

        String alternatives = "alternatives=true";

        //Value of origin
        String str_org = "origin=" + beginLatitude +","+ beginLongitud;
        //Value of destination
        String str_dest = "destination=" + endLatitude +","+ endLongitud;
        //Set value enable the sensor
        String sensor = "sensor=false";
        //Mode for find direction
        String mode = "mode=driving";
        //Build the full param
        String param = str_org +"&" + str_dest + "&" +sensor+"&" +mode+ "&" + alternatives + "&" +api_key;
        //Output format
        String output = "json";


        //Create url to request
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param;
        Log.i(TAG,url);
        return url;

    }

    private static String requestDirection(String reqUrl) throws IOException {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try{
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            //Get the response result
            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            httpURLConnection.disconnect();
        }
        return responseString;
    }

    public static class TaskRequestDirections extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";
            try {
                responseString = requestDirection(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return  responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Parse json here
            TaskParser taskParser = new TaskParser();
            taskParser.execute(s);
        }
        public class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String, String>>> > {

            @Override
            protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
                JSONObject jsonObject = null;
                List<List<HashMap<String, String>>> routes = null;
                try {
                    jsonObject = new JSONObject(strings[0]);
                    DirectionsParser directionsParser = new DirectionsParser();
                    routes = directionsParser.parse(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.i(TAG,""+routes);
                return routes;
            }

            @Override
            protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
                //Get list route and display it into the map

                ArrayList points = null;

                PolylineOptions polylineOptions = null;

                for (List<HashMap<String, String>> path : lists) {
                    points = new ArrayList();
                    polylineOptions = new PolylineOptions();

                    for (HashMap<String, String> point : path) {
                        double lat = Double.parseDouble(point.get("lat"));
                        double lon = Double.parseDouble(point.get("lon"));

                        LatLng latLng_point = new LatLng(lat,lon);

                        points.add(latLng_point);
                    }

                    polylineOptions.addAll(points);
                    polylineOptions.width(15);
                    polylineOptions.color(Color.CYAN);
                    polylineOptions.geodesic(true);
                }

                if (polylineOptions!=null) {
                    mMap.addPolyline(polylineOptions);
                }

            }
        }
    }

    private static boolean isInside (double point_lat, double point_lon, double circleCenter_lat, double circleCenter_lon, double radiusSize){
        // Compare radius of circle with
        // distance of its center from
        // given point
        double d_2 = Math.pow((point_lat - circleCenter_lat),2) + Math.pow((point_lon - circleCenter_lon),2) ;

        if (  d_2 <= Math.pow(radiusSize,2) )
            return true;
        else
            return false;
    }

}
