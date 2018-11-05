package com.example.got28.tabs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.got28.tabs.Model.Persona;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.varunest.sparkbutton.SparkButton;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class Cliente extends Fragment {
    //agregamos Firebase
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    //creamos nuestras variable para listar -------------------------------------------------------------------------------
    private List<Persona> listPerson = new ArrayList<Persona>();

    //cremos una varibale a nivel clase para editar de tipo Persona
    Persona personaSeleccionada;

    ImageButton Agregar;
    ListView listaV;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Para poder llamar un activity desde el framente tenemos que usar el view y retornarlo al final
        View view = inflater.inflate(R.layout.fragment_cliente, container, false);

        Agregar = (ImageButton) view.findViewById(R.id.btnAdd);
        listaV = (ListView) view.findViewById(R.id.listaClientes);

        //este metodo es el principal para cargar datos, todos los demas van despues
        inicializarFirebase();

        //metodo para listar      ------------------------------------------------------------------
        listarDatos();


        //agregamos header al listv ----------------------------------------------------------------
        View header_Cliente = getLayoutInflater().inflate(R.layout.header_cliente, null);
        listaV.addHeaderView(header_Cliente);

        //------------------------------------------------------------------------------------------

        Agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarDialog();

            }
        });
        //obtenemos los datos del item seleccionado ------------------------------------------------
        listaV.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //obtenemos la pocision y los datos
                personaSeleccionada = (Persona) parent.getItemAtPosition(position);

                //pasamos los datos al metodo edit----------------------------------------------------------------
                editarDialog(personaSeleccionada);


            }
        });
        return view;
    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }
    private void listarDatos() {
        //ordenar por nombre
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

                    myAdapter adapter = new myAdapter(getContext(),listPerson);
                    // llenamos nuestra lista con los datos y los text view
                    listaV.setAdapter(adapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError dataEroor){

            }

        });

    }
     class myAdapter extends ArrayAdapter<Persona>{
        Context context;
        List<Persona> data;

        myAdapter(Context contex, List<Persona> object){
            super(contex,R.layout.list_row,R.id.mostrar_nombre,object);
            this.context = contex;
            this.data = object;

        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            //LayoutInflater layoutInflater = (LayoutInflater)getContext().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LayoutInflater layoutInflater = Cliente.this.getLayoutInflater();
            final View view = layoutInflater.inflate(R.layout.btn_row,parent,false);

            TextView nombre_text = view.findViewById(R.id.mostrar_nombre);
            TextView telefo_txt = view.findViewById(R.id.mostrar_telefono);

           SparkButton  btn_eliminar = (SparkButton) view.findViewById(R.id.spark_button);
            //iniciamos la inimacion y q elimine a la vez
           btn_eliminar.playAnimation();
            //ImageButton btn_eliminar = view.findViewById(R.id.eliminarCliente);
            //varibale tipo persona, la cual obtiene la posicion del modelo para poder eliminar
            final Persona temp = listPerson.get(position);

            nombre_text.setText(getItem(position).getNombre());
            telefo_txt.setText(getItem(position).getTelefono());

            btn_eliminar.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    //Toast.makeText(getContext(),"" + temp.getUid() , Toast.LENGTH_SHORT).show();
                    Persona persona = new Persona();
                    persona.setUid(temp.getUid());//obtenemos el id que esta almacenado en personaSel
                    databaseReference.child("Persona").child(persona.getUid()).removeValue();
                    Toast.makeText(getContext(),"Eliminado", Toast.LENGTH_SHORT).show();

                }
            });


            //return super.getView(position, convertView, parent);
            return view;
        }
    }
    private void agregarDialog() {
        final Dialog alert;
        //getActivity es utilizada en fragment en el main puede ser this
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.agregar_layout_cliente, null);//asignas el xml que deseas llamar
        builder.setView(dialogView);

        final EditText addNombre = dialogView.findViewById(R.id.add_nombre);
        final EditText addTelefono = dialogView.findViewById(R.id.add_telefono);

        //bontones para guardar o cancelar -------------------------------------------------------------------------------------
        final Button buttonAcept = dialogView.findViewById(R.id.btnAceptar);
        final Button buttonDimiss = dialogView.findViewById(R.id.btnCancelar);

        alert =builder.show();//con esto mustras el dialogo en pantalla

        //btn aceptar y metodo cuando actualizamos ------------------------------------------------------------------------------
        buttonAcept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //creamos nuestra instacia de la bd para almacenar
                //trim sirve para omitir los espacios en blanco
                String nombre = addNombre.getText().toString().trim();
                String telefono = addTelefono.getText().toString().trim();
                //cremos el objeto de nuestro modelo para guardar
                Persona persona = new Persona();
                persona.setUid(UUID.randomUUID().toString());
                persona.setNombre(nombre);
                persona.setTelefono(telefono);
                databaseReference.child("Persona").child(persona.getUid()).setValue(persona);

                //pintamos un toast
                Toast.makeText(getContext(),"Guardado con exito",Toast.LENGTH_LONG).show();
                alert.dismiss();
            }
        });
        //btn para cancelar ----------------------------------------------------------------------------------------------------
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
    private void editarDialog(final Persona personaSeleccionada) {
        final Dialog alert;
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.edit_layout_cliente, null);//asignas el xml que deseas llamar
        builder.setView(dialogView);

        final EditText ed_nombre = dialogView.findViewById(R.id.c_cliente);
        final EditText ed_telefono = dialogView.findViewById(R.id.c_total);

        //llenamos nuestros edit text con los datos a editar
        ed_nombre.setText(personaSeleccionada.getNombre());//nombre de tu lista
        ed_telefono.setText(personaSeleccionada.getTelefono());//numero de tu lista


        //bontones para guardar o cancelar ---------------------------------------------------------------
        final Button buttonAcept = dialogView.findViewById(R.id.c_actualizar);
        final Button buttonDimiss = dialogView.findViewById(R.id.btn_cancelar);

        alert =builder.show();//con esto mustras el dialogo en pantalla

        //btn aceptar y metodo cuando actualizamos --------------------------------------------------------
        buttonAcept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //creamos nuestra instacia de la bd para almacenar
                //trim sirve para omitir los espacios en blanco
                Persona persona = new Persona();
                persona.setUid(personaSeleccionada.getUid());//obtenemos el id que esta almacenado en personaSel
                persona.setNombre(ed_nombre.getText().toString().trim());
                persona.setTelefono(ed_telefono.getText().toString().trim());
                databaseReference.child("Persona").child(persona.getUid()).setValue(persona);

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

}

