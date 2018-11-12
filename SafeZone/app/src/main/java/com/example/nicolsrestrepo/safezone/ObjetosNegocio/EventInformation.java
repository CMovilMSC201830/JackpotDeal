package com.example.nicolsrestrepo.safezone.ObjetosNegocio;

import com.google.firebase.firestore.GeoPoint;

import org.json.JSONException;
import org.json.JSONObject;

public class EventInformation {

    private String zone;
    private String time;
    private String date;
    private String details;
    private GeoPoint position;
    private String type;

    public EventInformation(String zone, String time, String date, String details) {
        this.zone = zone;
        this.time = time;
        this.date = date;
        this.details = details;
    }

    public EventInformation() {
    }

    public GeoPoint getPosition() {
        return position;
    }

    public String getType() {
        return type;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public void setPosition(GeoPoint position) {
        this.position = position;
    }

    public void setType(String type) {
        this.type = type;
    }

    public JSONObject ToJson() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("Zone", getZone());
            obj.put("Date", getDate());
            obj.put("Hour", getTime());
            obj.put("Details", getDetails());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }
}
