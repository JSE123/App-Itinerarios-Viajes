package com.example.turismo.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.example.turismo.EditarItinerario;
import com.example.turismo.R;
import com.example.turismo.TusItinerarios;
import com.example.turismo.models.Itinerario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class adaptadorItinerario_propios extends BaseAdapter {

    public ArrayList<Itinerario> dataItinerario;
    public Context context;
    public DatabaseReference referencia;
    public Button btnEditar,btnEliminar;

    public adaptadorItinerario_propios(ArrayList<Itinerario> dataItinerario, Context context) {
        this.dataItinerario = dataItinerario;
        this.context = context;
    }

    @Override
    public int getCount() {
        return dataItinerario.size();
    }

    @Override
    public Object getItem(int i) {
        return dataItinerario.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        Itinerario iti = (Itinerario)getItem(i);
        view = LayoutInflater.from(context).inflate(R.layout.item_itinerario_propio,null);

        TextView Nombre = view.findViewById(R.id.txtNombreItemPr);
        TextView Fecha = view.findViewById(R.id.txtFechaItemPr);
        TextView Lugar = view.findViewById(R.id.txtLugarItemPr);
        TextView Privacidad = view.findViewById(R.id.txtPrivacidadItemPr);
        ImageView foto = view.findViewById(R.id.imgFotoItiP);
        btnEditar = view.findViewById(R.id.btnEditItinerario);
        btnEliminar = view.findViewById(R.id.btnDeleteItinerario);

        Nombre.setText("NOMBRE: "+iti.Nombre_Itinerario);
        Fecha.setText("FECHA: "+iti.Fecha_inicio);
        Lugar.setText("LUGAR: "+iti.Nombre_destino);
        Privacidad.setText("PRIVACIDAD: "+iti.Privacidad);

        referencia = FirebaseDatabase.getInstance().getReference("Usuarios").child(iti.keyUsuario).child("Itinerarios");
        referencia.child(iti.key).child("Destinos").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                for (DataSnapshot item : task.getResult().getChildren()) {
                    String portada = String.valueOf(item.child("Foto_lugar").getValue());

                    StorageReference referenciaStorage = FirebaseStorage.getInstance().getReference().child(("imagenes/")+portada);

                    referenciaStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).into(foto);
                        }
                    });
                    break;
                }
            }
        });

        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EditarItinerario.class);
                Itinerario Iti = dataItinerario.get(i);
                Bundle bundle = new Bundle();
                bundle.putSerializable("Itinerario",Iti);
                intent.putExtras(bundle);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Esta seguro que quiere eliminar este itinerario?")
                        .setTitle("Confirmacion eliminar");

                builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        referencia.child(iti.key).removeValue();
                        dataItinerario.remove(iti);
                        Intent intent = new Intent(context, TusItinerarios.class);
                        intent.putExtra("KeyUsuario",iti.keyUsuario);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                });
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(context, "No se ha eliminado", Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        return view;
    }
}
