package com.example.nicolsrestrepo.safezone;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

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

    private static Context callingContext;
    private static GoogleMap mMap;
    private static double eventsRadiusSize_meters = 0;
    private static List<EventInformation> reportedEvents = new ArrayList<>();

    public RouteCalculator(double beginLatitude, double beginLongitud, double endLatitude, double endLongitud, Context callingContext, GoogleMap mMap, double eventsRadiusSize_meters, List<EventInformation> reportedEvents) {
        this.beginLatitude = beginLatitude;
        this.beginLongitud = beginLongitud;
        this.endLatitude = endLatitude;
        this.endLongitud = endLongitud;
        this.callingContext = callingContext;
        this.mMap = mMap;
        this.eventsRadiusSize_meters = eventsRadiusSize_meters;
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
        String param = str_org +"&" + str_dest + "&" +sensor+"&" +mode+ "&" +api_key;
        param += "&" + alternatives;
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

                List<HashMap<String, String>> firstPath = lists.get( lists.size() - 1 );
                List<HashMap<String, String>> lastPath = lists.get( lists.size() - 1 );
                for (List<HashMap<String, String>> path : lists) {
                    points = new ArrayList();


                    for (HashMap<String, String> point : path) {
                        double lat = Double.parseDouble(point.get("lat"));
                        double lon = Double.parseDouble(point.get("lon"));

                        LatLng latLng_point = new LatLng(lat,lon);

                        points.add(latLng_point);

                    }

                    if( pathIsSecure(points) ){
                        polylineOptions = new PolylineOptions();
                        polylineOptions.addAll(points);
                        polylineOptions.width(15);
                        polylineOptions.color(Color.rgb(143, 171, 216));
                        polylineOptions.geodesic(true);

                        if( !path.equals(firstPath) ){
                            Toast.makeText(callingContext,"Ruta alterna escogida para evadir eventos peligrosos", Toast.LENGTH_SHORT).show();
                        }

                        break;
                    }

                    if( !pathIsSecure(points) && path.equals(lastPath) ){
                        polylineOptions = new PolylineOptions();
                        polylineOptions.addAll(points);
                        polylineOptions.width(15);
                        polylineOptions.color(Color.rgb(143, 171, 216));
                        polylineOptions.geodesic(true);

                        Toast.makeText(callingContext,"Conduce con cuidado, no encontramos una ruta que evada los eventos", Toast.LENGTH_SHORT).show();
                    }

                }

                if (polylineOptions!=null) {
                    mMap.addPolyline(polylineOptions);
                }



            }
        }
    }

    private static boolean pathIsSecure(ArrayList<LatLng> points){

        for(LatLng point: points){

            EventInformation surroundingEvent = getSurroundingEvent(point.latitude, point.longitude);

            if(surroundingEvent != null){
                return  false;
            }
        }

        return true;
    }

    private static EventInformation getSurroundingEvent(double point_lat, double point_lon){
        for (EventInformation evento: reportedEvents) {
            double centro_lat = evento.getPosition().getLatitude();
            double centro_lon = evento.getPosition().getLongitude();
            if(isInside(point_lat, point_lon, centro_lat, centro_lon, eventsRadiusSize_meters)){
                Log.d(TAG,point_lat+","+point_lon+" está dentro de "+ evento.getPosition().toString());
                return evento;
            }
        }
        return null;
    }

    private static boolean isInside (double point_lat, double point_lon, double circleCenter_lat, double circleCenter_lon, double radiusSize_meters){
        // Compare radius of circle with
        // distance of its center from
        // given point
        double d_2 = Math.pow((point_lat - circleCenter_lat),2) + Math.pow((point_lon - circleCenter_lon),2) ;
        double radiusSize_coordinates = radiusSize_meters / 111111;
        double r_2 = Math.pow(radiusSize_coordinates,2);

        if (  d_2 <= r_2 ) {
            Log.d(TAG, d_2 + "<=" + r_2);
            return true;
        }
        else{
            return false;
        }

    }

}
