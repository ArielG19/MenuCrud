package com.example.got28.tabs;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.got28.tabs.Model.Persona;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.data;
import static android.media.CamcorderProfile.get;


public class Cuentas extends Fragment {
    //agregamos Firebase
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    //declaramos un lista tipo persona que es el modelo
    private List<Persona> listPerson = new ArrayList<Persona>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        //llmamos al fragment
        View view = inflater.inflate(R.layout.fragment_cuentas, container, false);

        ListView lv = (ListView) view.findViewById(R.id.listV2);
        myAdapter adapter = new myAdapter(getContext(),listPerson);
        lv.setAdapter(adapter);

        inicializarFirebase();
        listarDatos();

        //mostramos toas por cada item presionado
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                Toast.makeText(getContext(),"Clickeado" + position, Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
    //metodo para inicializar firebase
    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    private void listarDatos() {
        databaseReference.child("Persona").orderByChild("nombre").addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot){
                //limpiamos la cache para la persistencia de datos
                listPerson.clear();
                //cremos un ciclo for
                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()){
                    //inicializamos nuestro objeto persona que contiene nuestrod datos
                    Persona persona = objSnapshot.getValue(Persona.class);
                    listPerson.add(persona);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError dataEroor){

            }

        });
    }

    //clase adapter
     class myAdapter extends ArrayAdapter<Persona>{
         Context context;
         List<Persona> data;

         //constructor
         myAdapter(Context contex, List<Persona> object){
            super(contex,R.layout.list_row,R.id.list_txt,object);
            this.context = contex;
             this.data = object;

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater = Cuentas.this.getLayoutInflater();
           //LayoutInflater layoutInflater = (LayoutInflater)getContext().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = layoutInflater.inflate(R.layout.list_row,parent,false);

                TextView txt = view.findViewById(R.id.list_txt);
                TextView txt_telefon = view.findViewById(R.id.telefono_txt);
                Button bt_eliminar = view.findViewById(R.id.list_btn);

                txt.setText(getItem(position).getNombre());
                txt_telefon.setText(getItem(position).getTelefono());

                bt_eliminar.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        Toast.makeText(getContext(),"boton Clickeado", Toast.LENGTH_SHORT).show();
                    }
                });

            //return super.getView(position, convertView, parent);
            return view;
        }
    }


}

