package com.example.turismo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.turismo.adapters.adaptadorItinerario_propios;
import com.example.turismo.models.Destino;
import com.example.turismo.models.Itinerario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class TusItinerarios extends AppCompatActivity {

    public ListView lista;
    public FirebaseAuth firebaseAuth;
    public ArrayList<Itinerario> arrayItinerarios;
    public ArrayList<Destino> ListaDestinos;
    public adaptadorItinerario_propios adapter;
    public DatabaseReference referencia;
    public BottomNavigationView menu;
    public Button btnNuevoIti;
    public ImageButton btnMenu;
    public String KeyUsuario;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tus_itinerarios);

        lista = findViewById(R.id.LVTusItinerarios);
        menu = findViewById(R.id.menu_tusitinerarios);
        menu.setSelectedItemId(R.id.itemItinerarios_menu);
        btnNuevoIti = findViewById(R.id.btnNew_Itinerario);
        KeyUsuario = getIntent().getStringExtra("KeyUsuario");
        btnMenu = findViewById(R.id.btnMenuTusItinerarios);
        firebaseAuth = FirebaseAuth.getInstance();

//        btnMenu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                PopupMenu popupMenu = new PopupMenu(TusItinerarios.this,view);
//
//                popupMenu.getMenuInflater().inflate(R.menu.menu_usuario, popupMenu.getMenu());
//
//                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem menuItem) {
//                        switch (menuItem.getItemId()) {
//                            case R.id.Agregar_itinerario:
//                                Intent intent = new Intent(TusItinerarios.this, CrearItinerario.class);
//                                intent.putExtra("KeyUsuario",KeyUsuario);
//                                startActivity(intent);
//                                break;
////                            case R.id.cerrar_sesion:
////                                //cerrar sesion
////                                firebaseAuth.getInstance().signOut();
////                                Intent i = new Intent(TusItinerarios.this, MainActivity.class);
////                                i.putExtra("Sesion", "cerrar");
////                                startActivity(i);
////                                break;
//                        }
//                        return false;
//                    }
//                });
//                popupMenu.show();
//            }
//        });

        menu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.itemBusqueda_menu:
                        Intent intent = new Intent(TusItinerarios.this,Buscar.class);
                        intent.putExtra("KeyUsuario",KeyUsuario);
                        startActivity(intent);
                        return true;

                    case R.id.itemInicio_menu:
                        Intent intent2 = new Intent(TusItinerarios.this,MainActivity.class);
                        intent2.putExtra("KeyUsuario",KeyUsuario);
                        startActivity(intent2);
                        return true;

                    case R.id.itemItinerarios_menu:
                        return true;
                }
                return false;
            }
        });

        btnNuevoIti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TusItinerarios.this,CrearItinerario.class);
                intent.putExtra("KeyUsuario",KeyUsuario);
                startActivity(intent);
            }
        });

        arrayItinerarios = new ArrayList<Itinerario>();
        ListaDestinos = new ArrayList<Destino>();

       //cargarItinerarios();
    }


    public void cargarItinerarios(){
        arrayItinerarios = new ArrayList<Itinerario>();
        referencia = FirebaseDatabase.getInstance().getReference("Usuarios").child(KeyUsuario);
        referencia.child("Itinerarios").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(TusItinerarios.this, "No hay datos que mostrar", Toast.LENGTH_SHORT).show();
                } else {

                    for (DataSnapshot item : task.getResult().getChildren()) {
                        Itinerario ItiGet = new Itinerario();
                        ItiGet.Nombre_Itinerario = item.child("Nombre_itinerario").getValue().toString();
                        ItiGet.Fecha_inicio = item.child("Fecha_inicio").getValue().toString();
                        ItiGet.Privacidad = item.child("Privacidad_itinerario").getValue().toString();
                        ItiGet.Nombre_destino = item.child("Nombre_destino").getValue().toString();
                        ItiGet.Duracion = Integer.valueOf(item.child("Duracion_viaje").getValue().toString());
                        ItiGet.key = item.getKey();
                        ItiGet.keyUsuario = KeyUsuario;
                        arrayItinerarios.add(ItiGet);
                    }
                    adapter = new adaptadorItinerario_propios(arrayItinerarios,TusItinerarios.this);
                    lista.setAdapter(adapter);
                }
            }
        } );

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(TusItinerarios.this,MainActivity.class);
                startActivity(intent);
            }
        });
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                ListaDestinos = new ArrayList<Destino>();

                Intent intent = new Intent(TusItinerarios.this,DestinosItinerariosPropios.class);
                Itinerario Iti = (Itinerario) adapter.getItem(i);

                referencia = FirebaseDatabase.getInstance().getReference("Usuarios").child(KeyUsuario).child("Itinerarios").child(Iti.key).child("Destinos");
                referencia.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(TusItinerarios.this, "No hay datos que mostrar", Toast.LENGTH_SHORT).show();
                        } else {
                            for (DataSnapshot item : task.getResult().getChildren()) {
                                Destino destino = new Destino();
                                destino.Nombre_Itinerario = Iti.Nombre_Itinerario;
                                destino.keyDestino = item.getKey();
                                destino.keyItinerario = Iti.key;
                                destino.Usuario = Iti.keyUsuario;
                                destino.Nombre_destino = item.child("Nombre_destino").getValue().toString();
                                destino.Fecha = item.child("Fecha_destino").getValue().toString();
                                destino.Actividades = item.child("Actividades_planificadas").getValue().toString();
                                destino.Notas = item.child("Notas_adicionales").getValue().toString();
                                destino.foto_portada = item.child("Foto_lugar").getValue().toString();
                                ListaDestinos.add(destino);
                                //Iti.arrayDestinos.add(destino);
                            }
                            Iti.arrayDestinos = ListaDestinos;
                            intent.putExtra("key", Iti.key);
                            intent.putExtra("ItiDes",Iti);
                            intent.putExtra("KeyUsuario",KeyUsuario);
                            startActivity(intent);
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        menu.setSelectedItemId(R.id.itemItinerarios_menu);
        cargarItinerarios();
    }
}