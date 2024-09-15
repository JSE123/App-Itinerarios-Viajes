package com.example.turismo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class login extends AppCompatActivity {
    public EditText email, pass;
    public FirebaseAuth firebaseAuth;
    public DatabaseReference referencia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.emailLogin);
        pass = findViewById(R.id.passLogin);
        firebaseAuth = FirebaseAuth.getInstance();
        referencia = FirebaseDatabase.getInstance().getReference();
    }
    public void login(View view) {
        final String[] keyUsuario = new String[1];
        String correo = email.getText().toString();
        String password = pass.getText().toString();
        firebaseAuth.signInWithEmailAndPassword(correo, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    referencia.child("Usuarios").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(login.this, "No hay datos que mostrar", Toast.LENGTH_SHORT).show();
                            } else {
                                for (DataSnapshot itemUser : task.getResult().getChildren()) {
                                    if(itemUser.child("correo").getValue().toString().equals(correo)){
                                        keyUsuario[0] = itemUser.getKey();
                                        break;
                                    }
                                }

                            }
                        }
                    });
                    Intent i = new Intent(login.this, MainActivity.class);
                    i.putExtra("Email", correo);
                    i.putExtra("KeyUsuario",keyUsuario[0]);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();
                }else{
                    Toast.makeText(login.this, "Correo o contraseña incorrecta", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void irRegistro(View view) {
        Intent i = new Intent(this, registrar_usuario.class);
        startActivity(i);
//        Toast.makeText(this, "clic", Toast.LENGTH_SHORT).show();
    }
}