package com.example.turismo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.turismo.adapters.adaptadorItinerario_inicio;
import com.example.turismo.models.Destino;
import com.example.turismo.models.Itinerario;
import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    public ListView lista;
    public ArrayList<Itinerario> arrayItinerarios;
    public ArrayList<Destino> ListaDestinos;
    public adaptadorItinerario_inicio adapter;
    public DatabaseReference referencia;
    public BottomNavigationView menuFondo;
    public ImageButton  Menu;
    public FirebaseAuth firebaseAuth;
    public String keyUsuario;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        referencia = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        keyUsuario = getIntent().getStringExtra("KeyUsuario");
        lista = findViewById(R.id.LVInicio);
        menuFondo = findViewById(R.id.menu_inicio);
        menuFondo.setSelectedItemId(R.id.itemInicio_menu);
        Menu = findViewById(R.id.btnMenuInicio);

        MenuItem itemTusItinerarios = menuFondo.getMenu().getItem(2);

        if(user == null){
            //si hay sesion activa entonces se muestra el inicio
            itemTusItinerarios.setEnabled(false);

            Menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popupMenu = new PopupMenu(MainActivity.this,view);

                    popupMenu.getMenuInflater().inflate(R.menu.menu_publico, popupMenu.getMenu());

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                case R.id.action_login:
                                    Intent intent = new Intent(MainActivity.this, login.class);
                                    startActivity(intent);
                                    return true;
                                case R.id.action_registrarse:
                                    Intent i = new Intent(MainActivity.this, registrar_usuario.class);
                                    startActivity(i);
                                    return true;
                            }
                                return false;
                            }
                    });
                    popupMenu.show();
                }
            });
        }
        else{
            Menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popupMenu = new PopupMenu(MainActivity.this,view);

                    popupMenu.getMenuInflater().inflate(R.menu.menu_usuario, popupMenu.getMenu());

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                case R.id.Agregar_itinerario:
                                    Intent intent = new Intent(MainActivity.this, CrearItinerario.class);
                                    intent.putExtra("KeyUsuario",keyUsuario);
                                    startActivity(intent);
                                    break;
                                case R.id.cerrar_sesion:
                                    //cerrar sesion
                                    firebaseAuth.getInstance().signOut();
                                    Intent i = new Intent(MainActivity.this, MainActivity.class);
                                    finish();
                                    startActivity(getIntent());
                                    break;
                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                }
            });

            referencia.child("Usuarios").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "No hay datos que mostrar", Toast.LENGTH_SHORT).show();
                    } else {
                        for (DataSnapshot itemUser : task.getResult().getChildren()) {
                            if(itemUser.child("correo").getValue().toString().equals(user.getEmail())){
                                keyUsuario = itemUser.getKey();
                                break;
                            }

                        }
                    }
                }
            });
        }

        menuFondo.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.itemBusqueda_menu:
                        Intent intent = new Intent(MainActivity.this,Buscar.class);
                        intent.putExtra("KeyUsuario",keyUsuario);
                        startActivity(intent);
                        return true;

                    case R.id.itemInicio_menu:
                        return true;

                    case R.id.itemItinerarios_menu:
                        Intent intent3 = new Intent(MainActivity.this,TusItinerarios.class);
                        intent3.putExtra("KeyUsuario",keyUsuario);
                        startActivity(intent3);
                        return true;
                }
                return false;
            }
        });
        arrayItinerarios = new ArrayList<Itinerario>();
        ListaDestinos = new ArrayList<Destino>();
        //cargarItinerarios();
    }

    public void cargarItinerarios(){
        arrayItinerarios = new ArrayList<Itinerario>();

        referencia = FirebaseDatabase.getInstance().getReference("Usuarios");
        referencia.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "No hay datos que mostrar", Toast.LENGTH_SHORT).show();
                } else {
                    for (DataSnapshot itemUser : task.getResult().getChildren()) {
                        referencia = FirebaseDatabase.getInstance().getReference("Usuarios").child(itemUser.getKey()).child("Itinerarios");
                        referencia.orderByChild("Fecha_inicio").startAt(String.valueOf(Calendar.getInstance().getTimeInMillis())).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(MainActivity.this, "No hay datos que mostrar", Toast.LENGTH_SHORT).show();
                                } else {
                                    for (DataSnapshot item : task.getResult().getChildren()) {
                                        if (item.child("Privacidad_itinerario").getValue().toString().equals("Público")){
                                            Itinerario ItiGet = new Itinerario();
                                            ItiGet.Nombre_Itinerario = item.child("Nombre_itinerario").getValue().toString();
                                            ItiGet.Fecha_inicio = item.child("Fecha_inicio").getValue().toString();
                                            ItiGet.key = item.getKey();
                                            ItiGet.keyUsuario = itemUser.getKey();
                                            ItiGet.Duracion = Integer.valueOf(item.child("Duracion_viaje").getValue().toString());
                                            ItiGet.Nombre_destino = item.child("Nombre_destino").getValue().toString();
                                            arrayItinerarios.add(ItiGet);
                                        }
                                    }
                                    adapter = new adaptadorItinerario_inicio(arrayItinerarios,MainActivity.this);
                                    lista.setAdapter(adapter);
                                }
                            }
                        } );
                }
                }
            }
        });

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ListaDestinos = new ArrayList<Destino>();
                Intent intent = new Intent(MainActivity.this,DestinosItinerario.class);
                Itinerario Iti = (Itinerario) adapter.getItem(i);

                referencia = FirebaseDatabase.getInstance().getReference("Usuarios").child(Iti.keyUsuario).child("Itinerarios").child(Iti.key).child("Destinos");
                referencia.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "No hay datos que mostrar", Toast.LENGTH_SHORT).show();
                        } else {
                            for (DataSnapshot item : task.getResult().getChildren()) {
                                Destino destino = new Destino();
                                destino.Nombre_Itinerario = Iti.Nombre_Itinerario;
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
                            intent.putExtra("ItiDes",Iti);
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
        menuFondo.setSelectedItemId(R.id.itemInicio_menu);
        cargarItinerarios();


    }



}