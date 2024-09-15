package com.example.turismo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.turismo.models.Itinerario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditarItinerario extends AppCompatActivity {
    public DatabaseReference referencia;
    public Itinerario iti;
    public EditText nombre,duracion,destino,fecha;
    public Spinner spPriv;
    public Button btnGuardar;
    public String key, keyUsuario;
    public BottomNavigationView menu;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_itinerario);

        referencia = FirebaseDatabase.getInstance().getReference();
        nombre = findViewById(R.id.ET_Nombre_Edit);
        duracion = findViewById(R.id.ET_Duracion_Edit);
        destino = findViewById(R.id.ET_Destino_Edit);
        fecha = findViewById(R.id.ET_FechaInicio_Edit);
        spPriv = findViewById(R.id.spnPrivacidad_Edit);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.opcioines_privacidad,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        spPriv.setAdapter(adapter);
        Bundle respuesta = getIntent().getExtras();

        if(respuesta != null){
            iti = (Itinerario) respuesta.get("Itinerario");
            nombre.setText(iti.Nombre_Itinerario);
            duracion.setText(String.valueOf(iti.Duracion));
            fecha.setText(iti.Fecha_inicio);
            destino.setText(iti.Nombre_destino);
            if ("Público".equals(iti.Privacidad)){
                spPriv.setSelection(0);
            }
            else{
                spPriv.setSelection(1);
            }
            key = iti.key;
            keyUsuario = iti.keyUsuario;
        }

        menu = findViewById(R.id.menu_edititinerario);
        menu.setSelectedItemId(R.id.itemItinerarios_menu);
        menu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.itemBusqueda_menu:
                        //Pendiente
                        return true;

                    case R.id.itemInicio_menu:
                        Intent intent2 = new Intent(EditarItinerario.this,MainActivity.class);
                        startActivity(intent2);
                        return true;

                    case R.id.itemItinerarios_menu:
                        Intent intent3 = new Intent(EditarItinerario.this,TusItinerarios.class);
                        startActivity(intent3);
                        return true;
                }
                return false;
            }
        });

        fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePicker datePicker = findViewById(R.id.DP_Editar_Itinerario);
                Calendar calendar = Calendar.getInstance();

                datePicker.setMinDate(calendar.getTimeInMillis());
                datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH),null);

                datePicker.setVisibility(View.VISIBLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                        @Override
                        public void onDateChanged(DatePicker datePicker, int year, int month, int day) {
                            if (month<=8 & day <=9){
                                fecha.setText(year+"-0"+(month+1)+"-0"+day);
                            }
                            else if(month <= 8){
                                fecha.setText(year+"-0"+(month+1)+"-"+day);
                            }
                            else if(day <=9){
                                fecha.setText(year+"-"+(month+1)+"-0"+day);
                            }
                            else{
                                fecha.setText(year+"-"+(month+1)+"-"+day);
                            }
                            datePicker.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        });

        btnGuardar = findViewById(R.id.btnGuardar_Itinerario);

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatabaseReference referencia2 = FirebaseDatabase.getInstance().getReference("Usuarios").child(keyUsuario).child("Itinerarios");
                Query query = referencia2.orderByChild("Nombre_itinerario").equalTo(nombre.getText().toString());

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                            key = snapshot.getKey();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                //Validar campos de itinerario
                if(nombre.getText().toString().equals("") || duracion.getText().toString().equals("") || destino.getText().toString().equals("") || fecha.getText().toString().equals("")){
                    Toast.makeText(EditarItinerario.this, "Llene todos los campos", Toast.LENGTH_SHORT).show();

                }else {
                    Map<String,Object> insertItinerario = new HashMap<String, Object>();
                    insertItinerario.put("Nombre_itinerario",nombre.getText().toString());
                    insertItinerario.put("Duracion_viaje",duracion.getText().toString());
                    insertItinerario.put("Nombre_destino",destino.getText().toString());
                    insertItinerario.put("Fecha_inicio",fecha.getText().toString());
                    insertItinerario.put("Privacidad_itinerario",spPriv.getSelectedItem().toString());

                    referencia.child("Usuarios").child(keyUsuario).child("Itinerarios/"+key).updateChildren(insertItinerario).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(EditarItinerario.this, "ITINERARIO ACTUALIZADO CON EXITO", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(EditarItinerario.this,TusItinerarios.class);
                            intent.putExtra("key",key);
                            intent.putExtra("NombreItinerario",nombre.getText().toString());
                            startActivity(intent);
                        }
                    });
                }


            }
        });
    }
}