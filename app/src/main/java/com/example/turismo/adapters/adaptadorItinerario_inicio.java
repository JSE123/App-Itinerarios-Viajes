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

public class adaptadorItinerario_inicio extends BaseAdapter {

    public ArrayList<Itinerario> dataItinerario;
    public Context context;
    public DatabaseReference referencia;

    public adaptadorItinerario_inicio(ArrayList<Itinerario> dataItinerario, Context context) {
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
        view = LayoutInflater.from(context).inflate(R.layout.item_itinerario_inicio,null);

        TextView Nombre = view.findViewById(R.id.txtNombreIti);
        TextView Fecha = view.findViewById(R.id.txtFechaIti);
        ImageView Foto = view.findViewById(R.id.imgFotoIti);

        Nombre.setText(iti.Nombre_Itinerario);
        Fecha.setText(iti.Fecha_inicio);

        referencia = FirebaseDatabase.getInstance().getReference("Usuarios");
        referencia.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(context, "No hay datos que mostrar", Toast.LENGTH_SHORT).show();
                } else {
                    for (DataSnapshot item : task.getResult().getChildren()) {
                        referencia = FirebaseDatabase.getInstance().getReference("Usuarios").child(item.getKey()).child("Itinerarios");
                        referencia.child(iti.key).child("Destinos").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                for (DataSnapshot item : task.getResult().getChildren()) {
                                    String portada = String.valueOf(item.child("Foto_lugar").getValue());

                                    StorageReference referenciaStorage = FirebaseStorage.getInstance().getReference().child(("imagenes/") + portada);

                                    referenciaStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Picasso.get().load(uri).into(Foto);
                                        }
                                    });
                                    break;
                                }
                            }
                        });
                    }
                }
            }
        });
        return view;
    }
}
