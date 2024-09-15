package com.example.turismo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.turismo.models.Destino;
import com.example.turismo.models.Itinerario;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EditarDestino extends AppCompatActivity {
    public DatabaseReference referencia;
    public Destino destino;
    public EditText nombre,actividades,fecha,notas;
    public Spinner spPriv;
    public Button btnGuardar;
    public ImageButton btnSubirEdit;
    public String key, keyUsuario,keyDestino, keyItinerario;
    public BottomNavigationView menu;
    private TextView foto;
    public FirebaseStorage referenceStorage;

    public Uri direccionImagen;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_destino);

        referencia = FirebaseDatabase.getInstance().getReference();
        nombre = findViewById(R.id.ET_NombreDestino_Edit);
        actividades = findViewById(R.id.ET_Actividades_Edit);
        fecha = findViewById(R.id.ET_FechaVisita_Edit);
        notas = findViewById(R.id.ET_Notas_Edit);
        foto = findViewById(R.id.txtFoto1);
        btnGuardar = findViewById(R.id.btnEditar_Destino);
        btnSubirEdit = findViewById(R.id.btnSubirEdit);
        referenceStorage = FirebaseStorage.getInstance();
        key = getIntent().getStringExtra("key"); // key destino
        keyUsuario = getIntent().getStringExtra("KeyUsuario");

        Bundle respuesta = getIntent().getExtras();

        if(respuesta != null){
            destino = (Destino) respuesta.get("Destino");
            nombre.setText(destino.Nombre_destino);
            fecha.setText(destino.Fecha);
            actividades.setText(destino.Actividades);
            notas.setText(destino.Notas);
            keyDestino = destino.keyDestino;
            keyItinerario = destino.keyItinerario;
            keyUsuario = destino.Usuario;
        }


        Toast.makeText(this, "llave Destino:"+keyDestino, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "llave usuario:"+keyUsuario, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "llave itinerario:"+keyItinerario, Toast.LENGTH_SHORT).show();

//        Toast.makeText(this, "llave usuario: "+keyUsuario, Toast.LENGTH_SHORT).show();



        menu = findViewById(R.id.menu_editardestino);
        menu.setSelectedItemId(R.id.itemItinerarios_menu);
        menu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.itemBusqueda_menu:
                        Intent intent1 = new Intent(EditarDestino.this, Buscar.class);
                        startActivity(intent1);
                        finish();
                        return true;

                    case R.id.itemInicio_menu:
                        Intent intent2 = new Intent(EditarDestino.this,MainActivity.class);
                        startActivity(intent2);
                        return true;

                    case R.id.itemItinerarios_menu:
                        Intent intent3 = new Intent(EditarDestino.this,TusItinerarios.class);
                        startActivity(intent3);
                        return true;
                }
                return false;
            }
        });


        fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePicker datePicker = findViewById(R.id.DP_Editar_Destino);
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

        btnSubirEdit.setOnClickListener(new View.OnClickListener() {
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

//                DatabaseReference referencia2 = FirebaseDatabase.getInstance().getReference("Usuarios").child(keyUsuario).child("Itinerarios").child(keyI);
//                Query query = referencia2.orderByChild("Nombre_itinerario").equalTo(nombre.getText().toString());
//
//                query.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        for (DataSnapshot snapshot: dataSnapshot.getChildren()){
//                            key = snapshot.getKey();
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
                //validar entradas
                if(nombre.getText().toString().equals("") || fecha.getText().toString().equals("")){
                    Toast.makeText(EditarDestino.this, "Llene los campos", Toast.LENGTH_SHORT).show();
                }else{
                    Map<String,Object> insertDestino= new HashMap<>();
                    insertDestino.put("Nombre_destino",nombre.getText().toString());
                    insertDestino.put("Fecha_destino",fecha.getText().toString());
                    insertDestino.put("Actividades_planificadas",actividades.getText().toString());
                    insertDestino.put("Notas_adicionales",notas.getText().toString());
                    insertDestino.put("Foto_lugar",uploadImage(direccionImagen));

//                referencia.child("Usuarios").child(keyUsuario).child("Itinerarios").child(key).child("Destinos").push().setValue(insertDestino).addOnCompleteListener(new OnCompleteListener<Void>() {
                    referencia.child("Usuarios").child(keyUsuario).child("Itinerarios").child(keyItinerario).child("Destinos/"+keyDestino).updateChildren(insertDestino).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(EditarDestino.this, "DESTINO ACTUALIZADO CON EXITO", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(EditarDestino.this,EditarItinerario.class); //se regresa a la ventana de editar itinerario
                            intent.putExtra("key",key);
                            intent.putExtra("KeyUsuario",keyUsuario);
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
                Toast.makeText(EditarDestino.this, "La imagen se subio correctamente", Toast.LENGTH_SHORT).show();
            }
        });
        return nombreImagen;
    }
}