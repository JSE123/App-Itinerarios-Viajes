package com.example.turismo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class Buscar extends AppCompatActivity {
    public EditText cadenaBuscar;
    public FirebaseAuth firebaseAuth;


    public DatabaseReference referencia;
    public ListView lista_itinerario;

    public ArrayList<Itinerario> arreglo_itinerario;

    public adaptadorItinerario_inicio adapter;
    public ArrayList<Itinerario> arrayItinerarios;
    public BottomNavigationView menuFondo;
    public String key,nombreItinerario,KeyUsuario;
    public ImageButton Menu;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar);
        cadenaBuscar = findViewById(R.id.txtBuscar);
        referencia = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        lista_itinerario = findViewById(R.id.listview_itinerario_buscar);
        arreglo_itinerario = new ArrayList<>();
        menuFondo = findViewById(R.id.menu_inicio);
        menuFondo.setSelectedItemId(R.id.itemBusqueda_menu);
        Menu = findViewById(R.id.btnMenuBuscar);
        KeyUsuario = getIntent().getStringExtra("KeyUsuario");


        MenuItem itemTusItinerarios = menuFondo.getMenu().getItem(2);

        if(user == null){
            itemTusItinerarios.setEnabled(false);

            Menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popupMenu = new PopupMenu(Buscar.this,view);

                    popupMenu.getMenuInflater().inflate(R.menu.menu_publico, popupMenu.getMenu());

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                case R.id.action_login:
                                    Intent intent = new Intent(Buscar.this, login.class);
                                    startActivity(intent);
                                    return true;
                                case R.id.action_registrarse:
                                    Intent i = new Intent(Buscar.this, registrar_usuario.class);
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
//            Menu.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    PopupMenu popupMenu = new PopupMenu(Buscar.this,view);
//
//                    popupMenu.getMenuInflater().inflate(R.menu.menu_usuario, popupMenu.getMenu());
//
//                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                        @Override
//                        public boolean onMenuItemClick(MenuItem menuItem) {
//                            switch (menuItem.getItemId()) {
//                                case R.id.Agregar_itinerario:
//                                    Intent intent = new Intent(Buscar.this, CrearItinerario.class);
//                                    intent.putExtra("KeyUsuario",KeyUsuario);
//                                    startActivity(intent);
//                                    break;
//
//                            }
//                            return false;
//                        }
//                    });
//                    popupMenu.show();
//                }
//            });
        }

        menuFondo.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.itemBusqueda_menu:
                        return true;

                    case R.id.itemInicio_menu:
                        Intent intent2 = new Intent(Buscar.this, MainActivity.class);
                        intent2.putExtra("KeyUsuario",KeyUsuario);
                        startActivity(intent2);
                        finish();
                        return true;

                    case R.id.itemItinerarios_menu:
                        Intent intent3 = new Intent(Buscar.this,TusItinerarios.class);
                        intent3.putExtra("KeyUsuario",KeyUsuario);
                        startActivity(intent3);
                        return true;
                }
                return false;
            }
        });
    }
    public void buscarItinerario(View view){
//        Toast.makeText(this, "clic", Toast.LENGTH_SHORT).show();
        arrayItinerarios = new ArrayList<Itinerario>();
        String busqueda = cadenaBuscar.getText().toString().toLowerCase();
        referencia = FirebaseDatabase.getInstance().getReference("Usuarios");
        referencia.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(Buscar.this, "No hay datos que mostrar", Toast.LENGTH_SHORT).show();
                } else {
                    for (DataSnapshot item : task.getResult().getChildren()) {
//                        Toast.makeText(inicio.this, "llave: "+item.getKey(), Toast.LENGTH_SHORT).show();
                        referencia = FirebaseDatabase.getInstance().getReference("Usuarios").child(item.getKey()).child("Itinerarios");
                        referencia.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(Buscar.this, "No hay datos que mostrar", Toast.LENGTH_SHORT).show();
                                } else {

                                    for (DataSnapshot item : task.getResult().getChildren()) {
                                        if (item.child("Nombre_itinerario").getValue().toString().toLowerCase().contains(busqueda.toLowerCase())
                                        && item.child("Privacidad_itinerario").getValue().toString().equals("Público")){
                                            Itinerario ItiGet = new Itinerario();
                                            ItiGet.Nombre_Itinerario = item.child("Nombre_itinerario").getValue().toString();
                                            ItiGet.Fecha_inicio = item.child("Fecha_inicio").getValue().toString();
                                            ItiGet.Duracion = Integer.valueOf(item.child("Duracion_viaje").getValue().toString());
                                            ItiGet.Nombre_destino = item.child("Nombre_destino").getValue().toString();
                                            ItiGet.key = item.getKey();
                                            ItiGet.keyUsuario = KeyUsuario;
                                            arrayItinerarios.add(ItiGet);
                                        }
                                    }
                                    adapter = new adaptadorItinerario_inicio(arrayItinerarios,Buscar.this);
                                    lista_itinerario.setAdapter(adapter);
                                    if(arrayItinerarios.isEmpty()){
                                        Toast.makeText(Buscar.this, "No se han encontrado resultados", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                    }
                    lista_itinerario.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            ArrayList<Destino >ListaDestinos = new ArrayList<Destino>();
                            Intent intent = new Intent(Buscar.this,DestinosItinerario.class);
                            Itinerario Iti = (Itinerario) adapter.getItem(i);

                            referencia = FirebaseDatabase.getInstance().getReference("Usuarios").child(Iti.keyUsuario).child("Itinerarios").child(Iti.key).child("Destinos");
                            referencia.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(Buscar.this, "No hay datos que mostrar", Toast.LENGTH_SHORT).show();
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
                                        intent.putExtra("KeyUsuario",KeyUsuario);
                                        startActivity(intent);
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });
    }
}