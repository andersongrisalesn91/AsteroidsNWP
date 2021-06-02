package com.agndesarrollos.asteroidsnw.utils;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.agndesarrollos.asteroidsnw.R;
import com.agndesarrollos.asteroidsnw.utils.FuncionesGenerales;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Map;

public class PopUps extends AppCompatActivity {
    FuncionesGenerales fg;

    public void popUpConfirmar(final Context contex, LayoutInflater inflater, Map<String, String> datos) {

        final String ruta = "com.agndesarrollos.asteroidsnw." + datos.get("Clase");
        final String sesion =  datos.get("Sesion");
        View popUp = inflater.inflate(R.layout.popup_confirmacion, null);

        TextView TVTitulo = popUp.findViewById(R.id.TVTitulo);
        TextView TVMensaje = popUp.findViewById(R.id.TVTextoconf);
        Button cancel = popUp.findViewById(R.id.buttonConfNo);
        Button conf = popUp.findViewById(R.id.buttonConfSi);

        TVTitulo.setText(datos.get("Titulo"));
        TVMensaje.setText(datos.get("Mensaje"));
        cancel.setText(datos.get("TextoBTNo"));
        conf.setText(datos.get("TextoBTSi"));

        final PopupWindow popupWindow = new PopupWindow(popUp, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);

        //En caso de confirmar la salida.
        conf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Class<?> sigClass = null;
                try {
                    sigClass = Class.forName(ruta);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    fg = new FuncionesGenerales(contex);
                    if (sesion.equals("1")){
                        fg.actparam("sesion","0");
                        fg.actparam("ultP","Login");
                        FirebaseAuth.getInstance().signOut();
                    }
                    Intent intento = new Intent(contex, sigClass);
                    intento.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    contex.startActivity(intento);
                    popupWindow.dismiss();
                    finish();
                }

            }
        });
        //En caso de cancelar la salida.
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        popupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.showAtLocation(popUp, Gravity.CENTER, 0, 0);
    }


}