package com.example.nicolsrestrepo.safezone.ObjetosNegocio;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class TripInformation implements Serializable {

    private String destino;
    private String distancia;
    private String time;

    public TripInformation(String destino, String distancia, String time) {
        this.destino = destino;
        this.distancia = distancia;
        this.time = time;
    }
    public TripInformation() {
        this.destino = new String();
        this.distancia = new String();
        this.time = new String();
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getDistancia() {
        return distancia;
    }

    public void setDistancia(String distancia) {
        this.distancia = distancia;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public JSONObject ToJson() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("Destino", getDestino());
            obj.put("Distancia", getDistancia());
            obj.put("Tiempo", getTime());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

}
