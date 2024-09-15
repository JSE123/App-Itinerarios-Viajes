package com.example.turismo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CrearDestino extends AppCompatActivity {

    public DatabaseReference referencia;
    public EditText nombreDestino,Actividades,Notas,fecha;
    public Button btnGuardar;
    public TextView foto, titulo;
    public ImageButton btnSubir;
    public FirebaseStorage referenceStorage;
    public String key,nombreItinerario,KeyUsuario;
    public Uri direccionImagen;
    public BottomNavigationView menu;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_destino);

        referencia = FirebaseDatabase.getInstance().getReference();
        nombreDestino = findViewById(R.id.ET_NombreDestino_Register);
        fecha = findViewById(R.id.ET_FechaVisita_Register);
        Actividades = findViewById(R.id.ET_Actividades_Register);
        Notas = findViewById(R.id.ET_Notas_Register);
        btnGuardar = findViewById(R.id.btnReg_Destino);
        btnSubir = findViewById(R.id.btnSubir);
        foto = findViewById(R.id.txtFoto);
        referenceStorage = FirebaseStorage.getInstance();
        key = getIntent().getStringExtra("key");
        nombreItinerario = getIntent().getStringExtra("NombreItinerario");
        KeyUsuario = getIntent().getStringExtra("KeyUsuario");
        titulo = findViewById(R.id.txtTituloDes_Register);



        titulo.setText("CREAR DESTINOS PARA EL ITINERARIO: "+nombreItinerario);

        menu = findViewById(R.id.menu_creardestino);

        menu.setSelectedItemId(R.id.itemItinerarios_menu);

        menu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.itemBusqueda_menu:
                        Intent intent = new Intent(CrearDestino.this,Buscar.class);
                        intent.putExtra("KeyUsuario",KeyUsuario);
                        startActivity(intent);
                        return true;

                    case R.id.itemInicio_menu:
                        Intent intent2 = new Intent(CrearDestino.this,MainActivity.class);
                        intent2.putExtra("KeyUsuario",KeyUsuario);
                        startActivity(intent2);
                        return true;

                    case R.id.itemItinerarios_menu:
                        Intent intent3 = new Intent(CrearDestino.this,TusItinerarios.class);
                        intent3.putExtra("KeyUsuario",KeyUsuario);
                        startActivity(intent3);
                        return true;
                }
                return false;
            }
        });

        fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePicker datePicker = findViewById(R.id.DP_Crear_Destino);
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

        btnSubir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newGaleria = new Intent(Intent.ACTION_PICK);
                newGaleria.setType("image/");
                startActivityForResult(newGaleria,1);
            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //validar los campos
                if(nombreDestino.getText().toString().equals("") || fecha.getText().toString().equals("")){
                    Toast.makeText(CrearDestino.this, "Los campos nombre de destino y fecha son obligatorios", Toast.LENGTH_SHORT).show();
                }else{
                    Map<String,Object> insertDestino= new HashMap<>();
                    insertDestino.put("Nombre_destino",nombreDestino.getText().toString());
                    insertDestino.put("Fecha_destino",fecha.getText().toString());
                    insertDestino.put("Actividades_planificadas",Actividades.getText().toString());
                    insertDestino.put("Notas_adicionales",Notas.getText().toString());
                    insertDestino.put("Foto_lugar",uploadImage(direccionImagen));

                    referencia.child("Usuarios").child(KeyUsuario).child("Itinerarios").child(key).child("Destinos").push().setValue(insertDestino).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(CrearDestino.this, "DESTINO CREADO CON EXITO", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(CrearDestino.this,TusItinerarios.class);
                            intent.putExtra("key",key);
                            intent.putExtra("KeyUsuario",KeyUsuario);
                            startActivity(intent);
                        }
                    });
                }

            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable
    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null){
            direccionImagen = data.getData();
        }
    }

    public String uploadImage(Uri imageUri){
        String nombreImagen = UUID.randomUUID().toString();
        StorageReference reference = referenceStorage.getReference()
                .child("imagenes/"+ nombreImagen);
        UploadTask sube = reference.putFile(direccionImagen);
        Task<Uri> uriTask = sube.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if(!task.isSuccessful()){
                    throw task.getException();
                }
                return reference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                Toast.makeText(CrearDestino.this, "La imagen se subio correctamente", Toast.LENGTH_SHORT).show();
            }
        });
        return nombreImagen;
    }
}