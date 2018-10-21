package com.example.got28.tabs.Model;

/**
 * Created by Got28 on 10/10/2018.
 */

public class Persona {
    //agregamos nuestras propiedades tienen que ser privadas.
    private String uid;
    private String Nombre;
    private String Telefono;
    //agremamos un contructor

    public Persona() {
    }
    //agregamos los getter y setter
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        this.Nombre = nombre;
    }

    public String getTelefono() {
        return Telefono;
    }

    public void setTelefono(String telefono) {
        this.Telefono = telefono;
    }
    //agregamos el toString

    @Override
    public String toString() {
        return Nombre  + Telefono;
    }
}
