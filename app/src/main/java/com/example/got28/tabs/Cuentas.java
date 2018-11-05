package com.example.got28.tabs;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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

import static android.media.CamcorderProfile.get;


public class Cuentas extends Fragment {
    //agregamos Firebase
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    //declaramos un lista tipo factura que es el modelo
    private List<Factura> listaCuenta = new ArrayList<Factura>();
    //variable tipo Factura para editar
    Factura facturaSeleccionada;

    ListView listVFactura;
    TextView c_nombre, c_descripcion,c_fecha,c_total;
    EditText buscador;
    myAdapter adapter;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        //llmamos al fragment
        View view = inflater.inflate(R.layout.fragment_cuentas, container, false);

        buscador = (EditText) view.findViewById(R.id.c_buscador);
        listVFactura = (ListView) view.findViewById(R.id.listV2);
        View header_Cuenta = getLayoutInflater().inflate(R.layout.header_cuentas, null);
        listVFactura.addHeaderView(header_Cuenta);

        inicializarFirebase();
        //metodo para listar
        listarDatos();

        //mostramos toas por cada item presionado
        listVFactura.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                //Toast.makeText(getContext(),"Clickeado" + position, Toast.LENGTH_SHORT).show();
                //obtenemos la posicion del item seleccionado
                facturaSeleccionada = (Factura) parent.getItemAtPosition(position);
                editarCuenta(facturaSeleccionada);
            }
        });



        return view;
    }

    private void editarCuenta(final Factura facturaSeleccionada) {
        final Dialog alert;
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.edit_cuentas, null);//asignas el xml que deseas llamar
        builder.setView(dialogView);

        final EditText cu_cliente = dialogView.findViewById(R.id.c_cliente);
        final EditText cu_total = dialogView.findViewById(R.id.c_total);
        final EditText cu_descripcion = dialogView.findViewById(R.id.c_descripcion);
        // final TextView fechaAct = dialogView.findViewById(R.id.cu_fecha);

        //llenamos nuestros edit text con los datos a editar
        cu_cliente.setText(facturaSeleccionada.getNombreCliente());
        cu_total.setText(facturaSeleccionada.getTotal());
        cu_descripcion.setText(facturaSeleccionada.getTipoVenta());
        //fechaAct.setText(facturaSeleccionada.getFecha());


        //bontones para guardar o cancelar ---------------------------------------------------------------
        final Button buttonAcept = dialogView.findViewById(R.id.c_actualizar);
        final Button buttonDimiss = dialogView.findViewById(R.id.c_cancelar);

        alert =builder.show();//con esto mustras el dialogo en pantalla

        //btn aceptar y metodo cuando actualizamos --------------------------------------------------------
        buttonAcept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //creamos nuestra instacia de la bd para almacenar
                //trim sirve para omitir los espacios en blanco
                Factura factura = new Factura();
                factura.setId(facturaSeleccionada.getId());//obtenemos el id que esta almacenado en facturaSel
                factura.setIdCliente(facturaSeleccionada.getIdCliente());
                factura.setNombreCliente(cu_cliente.getText().toString().trim());
                factura.setTotal(cu_total.getText().toString().trim());
                factura.setTipoVenta(cu_descripcion.getText().toString().trim());
                factura.setFecha(facturaSeleccionada.getFecha());
                databaseReference.child("Factura").child(factura.getId()).setValue(factura);

                //pintamos un toast
                Toast.makeText(getContext(),"Actualizado",Toast.LENGTH_LONG).show();
                alert.dismiss();
            }
        });
        //btn para cancelar -------------------------------------------------------------------------------
        buttonDimiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //accion para cancelar
                alert.dismiss();
            }
        });
        //indica si se puede cancelar con el boton fisico de atras o al darle click fuera del dialogo---------------------------
        alert.setCancelable(false);
        alert.show();

    }

    //metodo para inicializar firebase
    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        //Para leer y escribir en la base de datos, necesitas una instancia de DatabaseReference
    }

    private void listarDatos() {
        databaseReference.child("Factura").orderByChild("nombreCliente").addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot){
                listaCuenta.clear();
                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()){
                    Factura factura = objSnapshot.getValue(Factura.class);
                    listaCuenta.add(factura);
                    //llenamos nuestro adapter
                    adapter = new myAdapter(getContext(),listaCuenta);
                    listVFactura.setAdapter(adapter);
                }
                buscador.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        adapter.getFilter().filter(charSequence);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError dataEroor){

            }

        });

    }

    //creamos nuestro adapter personalizado
    class myAdapter extends ArrayAdapter<Factura> {
        Context context;
        //obtenemos la lista con los datos de nuestro objeto
        List<Factura> data;

        myAdapter(Context contex, List<Factura> object) {
            super(contex, R.layout.list_row, R.id.c_nombre, object);
            this.context = contex;
            this.data = object;

        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater = Cuentas.this.getLayoutInflater();
            final View view = layoutInflater.inflate(R.layout.list_row, parent, false);

            //inicializar las variables
            c_nombre = view.findViewById(R.id.c_nombre);
            c_descripcion = view.findViewById(R.id.c_descripcion);
            c_total = view.findViewById(R.id.c_total);
            c_fecha = view.findViewById(R.id.c_fecha);

            //metemos los datos en los textview
            c_nombre.setText(getItem(position).getNombreCliente());
            c_descripcion.setText(getItem(position).getTipoVenta());
            c_total.setText(getItem(position).getTotal());
            c_fecha.setText(getItem(position).getFecha());


            return view;
        }
    }


}

