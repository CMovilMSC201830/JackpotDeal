package com.example.nicolsrestrepo.safezone.ObjetosNegocio;

import org.json.JSONException;
import org.json.JSONObject;

public class EventInformation {

    private String zone;
    private String time;
    private String date;
    private String details;

    public EventInformation(String zone, String time, String date, String details) {
        this.zone = zone;
        this.time = time;
        this.date = date;
        this.details = details;
    }

    public EventInformation() {
        this.zone = new String();
        this.time = new String();
        this.date = new String();
        this.details = new String();
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

    public void setTime(String zone) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String zone) {
        this.date = date;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String zone) {
        this.details = details;
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
