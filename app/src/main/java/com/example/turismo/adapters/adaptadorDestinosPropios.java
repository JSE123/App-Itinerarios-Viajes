package com.example.turismo.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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

import com.example.turismo.EditarDestino;
import com.example.turismo.R;
import com.example.turismo.TusItinerarios;
import com.example.turismo.models.Destino;
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

public class adaptadorDestinosPropios extends BaseAdapter {

    public ArrayList<Destino> dataDestinos;

    public adaptadorDestinosPropios(ArrayList<Destino> dataDestinos, Context context) {
        this.dataDestinos = dataDestinos;
        this.context = context;
    }

    public Context context;
    public DatabaseReference referencia;
    @Override
    public int getCount() {
        return dataDestinos.size();
    }

    @Override
    public Object getItem(int i) {
        return dataDestinos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Destino des = (Destino)getItem(i);
        view = LayoutInflater.from(context).inflate(R.layout.item_destino_propio,null);

        TextView NombreDestino = view.findViewById(R.id.txtDestino_vistaPropio);
        TextView Fecha = view.findViewById(R.id.txtFecha_Destino_vistaPropio);
        TextView Actividades = view.findViewById(R.id.txtActividades_Destino_vistaPropio);
        TextView Notas = view.findViewById(R.id.txtNotasAdi_Destino_VistaPropio);
        ImageView Foto = view.findViewById(R.id.imgFotoDestinoPropio);

        Button btnEdit = view.findViewById(R.id.btnEditDestino);
        Button btnDelete = view.findViewById(R.id.btnDeleteDestino);
        final String[] keyDestino = new String[1];

        NombreDestino.setText("Destino: "+des.Nombre_destino);
        Fecha.setText("Fecha: "+des.Fecha);
        Actividades.setText("Actividades Planificadas: "+des.Actividades);
        Notas.setText("Notas Adiconales: "+des.Notas);


        referencia = FirebaseDatabase.getInstance().getReference("Usuarios").child(des.Usuario).child("Itinerarios").child(des.keyItinerario).child("Destinos");
        referencia.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(context, "No hay datos que mostrar", Toast.LENGTH_SHORT).show();
                } else {
                    for (DataSnapshot item : task.getResult().getChildren()) {
                        keyDestino[0] = item.getKey();
                        StorageReference referenciaStorage = FirebaseStorage.getInstance().getReference().child("imagenes/").child(item.child("Foto_lugar").getValue().toString());

                        referenciaStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Picasso.get().load(uri).into(Foto);
                            }
                        });
                    }
                }
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EditarDestino.class);
                Destino Desti = dataDestinos.get(i);

                Bundle bundle = new Bundle();
                bundle.putSerializable("Destino",Desti);
                intent.putExtras(bundle);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Esta seguro que quiere eliminar este destino?")
                        .setTitle("Confirmacion eliminar");

                builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        referencia.child(keyDestino[0]).removeValue();
                        dataDestinos.remove(des);
                        Intent intent = new Intent(context, TusItinerarios.class);
                        intent.putExtra("KeyUsuario",des.Usuario);
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
