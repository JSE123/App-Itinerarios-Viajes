package com.example.turismo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.turismo.adapters.adaptadorDestinosPropios;
import com.example.turismo.models.Destino;
import com.example.turismo.models.Itinerario;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class DestinosItinerariosPropios extends AppCompatActivity {

    public TextView titulo, duracion, nombreDes;
    public ListView lista;
    public ArrayList<Destino> ListaDestinos;
    public adaptadorDestinosPropios adapter;
    public BottomNavigationView menu;
    public Button btnNuevoDestino;
    public String key,nombreItinerario,KeyUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destinos_itinerarios_propios);
        Intent intent = getIntent();


        lista = findViewById(R.id.LVDestinosPropios);
        menu = findViewById(R.id.menu_destinos_propios);
        menu.setSelectedItemId(R.id.itemItinerarios_menu);
        titulo = findViewById(R.id.txtTituloItinerarioPropio);
        duracion = findViewById(R.id.txtDuracionItiPropio);
        nombreDes = findViewById(R.id.txtDestinoItiPropio);
        btnNuevoDestino = findViewById(R.id.btnNuevoDestino);
        key = getIntent().getStringExtra("key");//llave itinerario
        nombreItinerario = getIntent().getStringExtra("NombreItinerario");
        KeyUsuario = getIntent().getStringExtra("KeyUsuario");

        btnNuevoDestino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DestinosItinerariosPropios.this,CrearDestino.class);
                intent.putExtra("key",key);
                intent.putExtra("NombreItinerario",nombreItinerario);
                intent.putExtra("KeyUsuario",KeyUsuario);
                startActivity(intent);
            }
        });

        menu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.itemBusqueda_menu:
                        Intent intent = new Intent(DestinosItinerariosPropios.this,Buscar.class);
                        //intent.putExtra("KeyUsuario",KeyUsuario);
                        startActivity(intent);
                        return true;

                    case R.id.itemInicio_menu:
                        Intent intent2 = new Intent(DestinosItinerariosPropios.this,MainActivity.class);
                        intent2.putExtra("KeyUsuario",KeyUsuario);
                        startActivity(intent2);
                        return true;

                    case R.id.itemItinerarios_menu:
                        Intent intent3 = new Intent(DestinosItinerariosPropios.this,TusItinerarios.class);
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
        adapter = new adaptadorDestinosPropios(ListaDestinos,DestinosItinerariosPropios.this);
        lista.setAdapter(adapter);

        titulo.setText(itine.Nombre_Itinerario);
        duracion.setText("DURACIÓN DEL VIAJE "+itine.Duracion+" DÍAS");
        nombreDes.setText("DESTINO: "+itine.Nombre_destino);
    }
}