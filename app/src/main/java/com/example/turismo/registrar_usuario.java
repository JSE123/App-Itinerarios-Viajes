package com.example.turismo;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.turismo.models.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class registrar_usuario extends AppCompatActivity {
    public EditText user, email, pass;
    public FirebaseAuth firebaseAuth;
    public DatabaseReference referencia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_usuario);
        user = findViewById(R.id.nombre_usuario);
        email = findViewById(R.id.email_registro);
        pass = findViewById(R.id.pass_registro);
        referencia = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void insertarUsuario(){
        String correo = email.getText().toString();
        String password = pass.getText().toString();
        String usuario = user.getText().toString();

        //        validar usuario con solo letras
        if(usuario != null && usuario.matches("^[a-zA-Z0-9]*$") && usuario.length() > 4){
            if(validarEmail(correo)){//validar email

                firebaseAuth.createUserWithEmailAndPassword(correo, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(registrar_usuario.this, "Se ha registrado correctamente", Toast.LENGTH_SHORT).show();
                            guardarInfoUsuario();
                            finish();
                        }else{
                            String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                            mensajeError(errorCode);
                        }
                    }
                });
            }else{
                Toast.makeText(registrar_usuario.this, "Ingrese un correo electronico valido", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this, "Error en el nombre de usuario: no se permiten puntos, tienen que tener mas de 4 caracteres y no estar vacio", Toast.LENGTH_SHORT).show();
        }
    }

    public void Registrar(View fv) {

        //Primero se valida que no haya un usuario con ese mismo nombre
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String usuario = user.getText().toString();


        referencia.child("Usuarios").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(!task.isSuccessful()){
                    Toast.makeText(registrar_usuario.this, "No hay datos", Toast.LENGTH_SHORT).show();
                }else{
//                            Toast.makeText(CrearItinerario.this, "Si hay datosss", Toast.LENGTH_SHORT).show();
                    for (DataSnapshot item:task.getResult().getChildren()) {
                        Usuario u = item.getValue(Usuario.class);
                        if(u.usuario.equals(usuario)){
                            Toast.makeText(registrar_usuario.this, "El nombre de usuario ya esta en uso.", Toast.LENGTH_LONG).show();
                            user.setError("El usuario ya existe.");
                            user.requestFocus();
                            break;
                        }else{
                            insertarUsuario();
                        }
                    }
                }
                insertarUsuario();
            }
        });
    }

    //funcion que valida el email
    private boolean validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    public void guardarInfoUsuario() {
        String correo = email.getText().toString();
        String usuario = user.getText().toString();

        Map<String, Object> dataUsuario = new HashMap<>();
        dataUsuario.put("correo", correo);
        dataUsuario.put("usuario", usuario);

        referencia.child("Usuarios").child(usuario).setValue(dataUsuario);

    }

    public void mensajeError(String error){
        switch (error) {

            case "ERROR_INVALID_CUSTOM_TOKEN":
                Toast.makeText(registrar_usuario.this, "El formato del token personalizado es incorrecto. Por favor revise la documentación", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_CUSTOM_TOKEN_MISMATCH":
                Toast.makeText(registrar_usuario.this, "El token personalizado corresponde a una audiencia diferente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_CREDENTIAL":
                Toast.makeText(registrar_usuario.this, "La credencial de autenticación proporcionada tiene un formato incorrecto o ha caducado.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_EMAIL":
                Toast.makeText(registrar_usuario.this, "La dirección de correo electrónico está mal formateada.", Toast.LENGTH_LONG).show();
                email.setError("La dirección de correo electrónico está mal formateada.");
                email.requestFocus();
                break;

            case "ERROR_WRONG_PASSWORD":
                Toast.makeText(registrar_usuario.this, "La contraseña no es válida o el usuario no tiene contraseña.", Toast.LENGTH_LONG).show();
                pass.setError("la contraseña es incorrecta ");
                pass.requestFocus();
                pass.setText("");
                break;

            case "ERROR_USER_MISMATCH":
                Toast.makeText(registrar_usuario.this, "Las credenciales proporcionadas no corresponden al usuario que inició sesión anteriormente..", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_REQUIRES_RECENT_LOGIN":
                Toast.makeText(registrar_usuario.this,"Esta operación es sensible y requiere autenticación reciente. Inicie sesión nuevamente antes de volver a intentar esta solicitud.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                Toast.makeText(registrar_usuario.this, "Ya existe una cuenta con la misma dirección de correo electrónico pero diferentes credenciales de inicio de sesión. Inicie sesión con un proveedor asociado a esta dirección de correo electrónico.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_EMAIL_ALREADY_IN_USE":
                Toast.makeText(registrar_usuario.this, "La dirección de correo electrónico ya está siendo utilizada por otra cuenta..   ", Toast.LENGTH_LONG).show();
                email.setError("La dirección de correo electrónico ya está siendo utilizada por otra cuenta.");
                email.requestFocus();
                break;

            case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                Toast.makeText(registrar_usuario.this, "Esta credencial ya está asociada con una cuenta de usuario diferente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_DISABLED":
                Toast.makeText(registrar_usuario.this, "La cuenta de usuario ha sido inhabilitada por un administrador..", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_TOKEN_EXPIRED":
                Toast.makeText(registrar_usuario.this, "La credencial del usuario ya no es válida. El usuario debe iniciar sesión nuevamente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_NOT_FOUND":
                Toast.makeText(registrar_usuario.this, "No hay ningún registro de usuario que corresponda a este identificador. Es posible que se haya eliminado al usuario.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_USER_TOKEN":
                Toast.makeText(registrar_usuario.this, "La credencial del usuario ya no es válida. El usuario debe iniciar sesión nuevamente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_OPERATION_NOT_ALLOWED":
                Toast.makeText(registrar_usuario.this, "Esta operación no está permitida. Debes habilitar este servicio en la consola.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_WEAK_PASSWORD":
                Toast.makeText(registrar_usuario.this, "La contraseña proporcionada no es válida..", Toast.LENGTH_LONG).show();
                pass.setError("La contraseña no es válida, debe tener al menos 6 caracteres");
                pass.requestFocus();
                break;

        }
    }
}