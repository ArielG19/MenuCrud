package com.example.got28.tabs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.got28.tabs.Model.Persona;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Venta extends Fragment {
    //agregamos Firebase
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    //declaramos un lista tipo persona que es el modelo
    private List<Persona> listCliente = new ArrayList<Persona>();
    ArrayAdapter<Persona> arrayAdapterPersona;
    AutoCompleteTextView autoCompleteTextView;
    //ImageButton searchImage;
    //Spinner comboSpinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_venta, container, false);
        autoCompleteTextView = (AutoCompleteTextView) view.findViewById(R.id.autoComplete);
        //llamamos nuestros metodo firebase que es el principal para cargar datos.
        inicializarFirebase();

        listarClientes();

        return view;
    }

    private void listarClientes() {
        databaseReference.child("Persona").orderByChild("nombre").addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot){
                //limpiamos la cache para la persistencia de datos
                listCliente.clear();
                //cremos un ciclo for
                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()){
                    //inicializamos nuestro objeto persona que contiene nuestrod datos
                    Persona persona = objSnapshot.getValue(Persona.class);
                   // listCliente.add(new Persona(0,"seleccion"));
                    listCliente.add(persona);
                    arrayAdapterPersona = new ArrayAdapter<Persona>(getActivity(), android.R.layout.simple_list_item_1,listCliente);
                    autoCompleteTextView.setAdapter(arrayAdapterPersona);
                    //arrayAdapterPersona = new ArrayAdapter<Persona>(getActivity(), R.layout.list_search,R.id.searchText,listCliente);
                    //comboSpinner.setAdapter(arrayAdapterPersona);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError dataEroor){

            }

        });
    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

}

