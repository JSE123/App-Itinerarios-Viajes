package com.example.turismo.models;

import android.net.Uri;

import com.example.turismo.models.Destino;

import java.io.Serializable;
import java.util.ArrayList;

public class Itinerario implements Serializable {
    public String Nombre_Itinerario;
    public int Duracion;
    public String Nombre_destino;
    public String Fecha_inicio;
    public String Privacidad;
    public int Cantidad_votos;
    public int Total_puntaje;
    public Uri foto_portada;
    public String key;
    public String keyUsuario;
    public ArrayList<Destino> arrayDestinos;

    public Itinerario() {
    }
}
