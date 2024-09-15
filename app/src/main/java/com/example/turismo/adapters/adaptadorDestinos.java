package com.example.turismo.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.turismo.R;
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

public class adaptadorDestinos extends BaseAdapter {
    public ArrayList<Destino> dataDestinos;

    public adaptadorDestinos(ArrayList<Destino> dataDestinos, Context context) {
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
        view = LayoutInflater.from(context).inflate(R.layout.item_destino,null);

        TextView NombreDestino = view.findViewById(R.id.txtDestino_vista);
        TextView Fecha = view.findViewById(R.id.txtFecha_Destino_vista);
        TextView Actividades = view.findViewById(R.id.txtActividades_Destino_vista);
        TextView Notas = view.findViewById(R.id.txtNotasAdi_Destino_Vista);
        ImageView Foto = view.findViewById(R.id.imgFotoDestino);

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

        return view;
    }
}
