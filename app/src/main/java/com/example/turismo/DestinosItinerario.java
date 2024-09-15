package com.example.turismo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.example.turismo.adapters.adaptadorDestinos;
import com.example.turismo.models.Destino;
import com.example.turismo.models.Itinerario;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class DestinosItinerario extends AppCompatActivity {

    public TextView titulo, duracion, nombreDes;
    public ListView lista;
    public ArrayList<Destino> ListaDestinos;
    public adaptadorDestinos adapter;
    public BottomNavigationView menu;
    public String KeyUsuario;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destinos_itinerario);

        lista = findViewById(R.id.LVDestinos);
        menu = findViewById(R.id.menu_destinos);
        menu.setSelectedItemId(R.id.itemInicio_menu);
        titulo = findViewById(R.id.txtTituloItinerario);
        duracion = findViewById(R.id.txtDuracionIti);
        nombreDes = findViewById(R.id.txtDestinoIti);
        KeyUsuario = getIntent().getStringExtra("KeyUsuario");

        menu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.itemBusqueda_menu:
                        Intent intent = new Intent(DestinosItinerario.this,Buscar.class);
                        intent.putExtra("KeyUsuario",KeyUsuario);
                        startActivity(intent);
                        return true;

                    case R.id.itemInicio_menu:
                        Intent intent2 = new Intent(DestinosItinerario.this,MainActivity.class);
                        intent2.putExtra("KeyUsuario",KeyUsuario);
                        startActivity(intent2);
                        return true;

                    case R.id.itemItinerarios_menu:
                        Intent intent3 = new Intent(DestinosItinerario.this,TusItinerarios.class);
                        intent3.putExtra("KeyUsuario",KeyUsuario);
                        startActivity(intent3);
                        return true;
                }
                return false;
            }
        });
        ListaDestinos = new ArrayList<Destino>();
        cargarDestinos();
    }

    public void cargarDestinos(){
        ListaDestinos = new ArrayList<Destino>();
        Intent intent = getIntent();
        Itinerario itine = (Itinerario) intent.getSerializableExtra("ItiDes");

        ListaDestinos = itine.arrayDestinos;
        adapter = new adaptadorDestinos(ListaDestinos,DestinosItinerario.this);
        lista.setAdapter(adapter);

        titulo.setText(itine.Nombre_Itinerario);
        duracion.setText("DURACIÓN DEL VIAJE "+itine.Duracion+" DÍAS");
        nombreDes.setText("DESTINO: "+itine.Nombre_destino);

    }
}