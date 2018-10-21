package com.example.got28.tabs;

import com.google.firebase.database.FirebaseDatabase;
/**
 * Created by Got28 on 07/10/2018.
 * Creamos nuestra clase java para tener la persitencia y no de problemas
 * esta guardara los datos de forma local cuando no haya conexion.
 * tenemos que agregarla el android manifest haciendo uso de la etiqueta adroid:name
 */

public class FirebaseApp extends android.app.Application{
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    public static void initializeApp(Cliente cliente) {

    }

    public static void initializeApp(Cuentas cuentas) {

    }
}
