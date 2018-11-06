package com.example.nicolsrestrepo.safezone.ObjetosNegocio;

import java.util.ArrayList;
import java.util.List;

public class Usuario {
    private String telefono = "";
    private String correo = "";
    private List<String> listaContactos = new ArrayList<>();

    public Usuario() {
    }

    public Usuario(String telefono, String correo) {
        this.telefono = telefono;
        this.correo = correo;
    }

    public Usuario(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public List<String> getListaContactos() {
        return listaContactos;
    }

    public void setListaContactos(List<String> listaContactos) {
        this.listaContactos = listaContactos;
    }

    @Override
    public String toString() {
        return this.correo;
    }
}
