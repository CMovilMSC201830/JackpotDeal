package com.example.nicolsrestrepo.safezone.ObjetosNegocio;

import java.util.ArrayList;
import java.util.List;

public class Usuario {
    private String telefono = "";
    private List<String> listaContactos = new ArrayList<>();

    public Usuario () {}

    public Usuario (String telefono) {
        this.telefono = telefono;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public List<String> getListaContactos() {
        return listaContactos;
    }

    public void setListaContactos(List<String> listaContactos) {
        this.listaContactos = listaContactos;
    }
}
