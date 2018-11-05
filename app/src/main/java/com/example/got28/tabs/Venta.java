package com.example.got28.tabs;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.icu.text.DateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.got28.tabs.Model.Factura;
import com.example.got28.tabs.Model.Persona;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Venta extends Fragment {
    //agregamos Firebase
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    //declaramos un lista tipo persona que es el modelo
    private List<Persona> listCliente = new ArrayList<Persona>();
    ArrayAdapter<Persona> arrayAdapterPersona;

    //EditText
    AutoCompleteTextView autoCompleteTextView;
    EditText e_venta,e_total;
    TextView e_fecha;
    Button guardar;
    ImageButton btnCalendario;
    Calendar calendar;
    int day, month, year;


    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_venta, container, false);

        autoCompleteTextView = (AutoCompleteTextView) view.findViewById(R.id.autoComplete);
        e_fecha = (TextView) view.findViewById(R.id.e_fecha);
        e_venta = (EditText) view.findViewById(R.id.e_venta);
        e_total = (EditText) view.findViewById(R.id.e_total);
        guardar = (Button) view.findViewById(R.id.btGuardar);

        btnCalendario = (ImageButton) view.findViewById(R.id.c_calendario);

        calendar = Calendar.getInstance();
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month= calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);

        month = month;

       // e_fecha.setText(day +" / "+month+" / "+year);


        //llamamos nuestros metodo firebase que es el principal para cargar datos.
        inicializarFirebase();

        listarClientes();

        //llamamos al modal calendario
        btnCalendario.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),new DatePickerDialog.OnDateSetListener(){

                    @Override
                    public void onDateSet(DatePicker view,int year,int monthOfYear, int dayOfMonth) {
                        monthOfYear = monthOfYear + 1;
                        e_fecha.setText(dayOfMonth +" / "+monthOfYear+" / "+ year);

                    }
                },year,month,day);
                datePickerDialog.show();
            }

        });

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //obtenemos el la posicion del item seleccionado
                Persona nombreSeleccionado = (Persona) parent.getItemAtPosition(position);
                //obtenemos el id
                final String nombreId = nombreSeleccionado.getUid();

                guardar.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        // obtenemos los datos de los input y los convertimos en cadenas
                            //String autoComplete = autoCompleteTextView.getText().toString();
                            String nombreCliente = autoCompleteTextView.getText().toString().trim();
                            String fecha = e_fecha.getText().toString().trim();
                            String venta = e_venta.getText().toString().trim();
                            String total = e_total.getText().toString().trim();

                        if (autoCompleteTextView.getText().toString().compareToIgnoreCase("") == 0) {
                            //Toast.makeText(getContext(), "You did not enter a value!", Toast.LENGTH_SHORT).show();
                            autoCompleteTextView.setError("Requerido");
                            //return;
                        }
                        if (e_venta.getText().toString().compareToIgnoreCase("") == 0) {
                            //Toast.makeText(getContext(), "You did not enter a value!", Toast.LENGTH_SHORT).show();
                            e_venta.setError("Requerido");
                            //return;
                        }
                        if (e_fecha.getText().toString().compareToIgnoreCase("") == 0) {
                            //Toast.makeText(getContext(), "You did not enter a value!", Toast.LENGTH_SHORT).show();
                            e_fecha.setError("Requerido");
                            //return;
                        }
                        else {
                            //creamos el objeto de nuestro modelo
                            Factura factura = new Factura();
                            factura.setId(UUID.randomUUID().toString());
                            factura.setIdCliente(nombreId);
                            factura.setNombreCliente(nombreCliente);
                            factura.setFecha(fecha);
                            factura.setTipoVenta(venta);
                            factura.setTotal(total);
                            databaseReference.child("Factura").child(factura.getId()).setValue(factura);
                            //mostramos mensaje en pantalla
                            Toast.makeText(getContext(), "Cuenta registrada ", Toast.LENGTH_SHORT).show();
                            limpiarInput();
                        }
                    }
                });
            }
        });




        return view;
    }
    private void limpiarInput() {
        autoCompleteTextView.setText("");
        e_fecha.setText("");
        e_venta.setText("");
        e_total.setText("");
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

