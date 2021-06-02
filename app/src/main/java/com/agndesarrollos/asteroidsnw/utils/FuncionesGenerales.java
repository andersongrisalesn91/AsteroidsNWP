package com.agndesarrollos.asteroidsnw.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.agndesarrollos.asteroidsnw.AsteroidesActivity;
import com.agndesarrollos.asteroidsnw.DesAsteroideActivity;
import com.agndesarrollos.asteroidsnw.R;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

public class FuncionesGenerales extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    ConsultaGeneral conGen = new ConsultaGeneral();
    OperacionesBDInterna operaciones;
    Context contexto;

    public FuncionesGenerales(Context contexto) {
        this.contexto = contexto;
        this.operaciones = new OperacionesBDInterna(contexto);
    }

    public String pantallaactual() {
        String pact = "";
        String queryCli = "SELECT va FROM PA WHERE pa='ultP'";
        ArrayList<String>[] objeto = conGen.queryObjeto2val(contexto, queryCli, null);
        if ((objeto == null) || (objeto.length < 1)) {
            return null;
        } else {
            pact = objeto[0].get(0);
        }
        return pact;
    }

    public Map<String, Object> getparametros() {
        ArrayList<String>[] registros = conGen.queryObjeto(contexto, "select pa,va from 'PA' order by _id asc", null);
        Map<String, Object> tablag = new HashMap<>();

        for (int i = 0; i < registros.length; i++) {
            tablag.put(registros[i].get(0), registros[i].get(1));
        }
        return tablag;
    }

    public Integer existeast(String nast) {
        ArrayList<String>[] registro = conGen.queryObjeto(contexto, "select name from asteroids where name='" + nast + "'", null);

        if ((registro == null) || (registro.length < 1)) {
            return 0;
        } else {
            return 1;
        }
    }

    public Integer existeastusr(String refid , String iduser) {
        ArrayList<String>[] registro = conGen.queryObjeto(contexto, "select id from asteroids_users where user_id=" + iduser + " and id in (select id from asteroids where neo_reference_id='" + refid + "')" , null);
        if ((registro == null) || (registro.length < 1)) {
            return 0;
        } else {
            return 1;
        }
    }

   public void ultimaPantalla(String pantalla) {
        ContentValues cv = new ContentValues();
        cv.put("va", pantalla);
        boolean update = operaciones.actualizar("PA", cv, "pa=?", new String[]{"ultP"});
        if (!update) {
            Toast.makeText(contexto, "Registro no actualizado", Toast.LENGTH_SHORT).show();
        }
    }

    public void actparam(String pa,String va) {
        ContentValues cv = new ContentValues();
        cv.put("va", va);
        boolean update = operaciones.actualizar("PA", cv, "pa=?", new String[]{pa});
        if (!update) {
            Toast.makeText(contexto, "Registro no actualizado", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu1:
                return true;
            case R.id.menu2:
                return true;
            case R.id.menu3:
                operaciones.queryNoData("UPDATE ACT SET VAL='" + pantallaactual() + "' WHERE VA='PANT'");
                return true;
        }
        return false;
    }

    public String fechaActual(int acc) {
        String fecha = "";
        if (acc == 1) {
            //SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            Date date = new Date();
            fecha = dateFormat.format(date);
        } else if (acc == 2) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
            Date date = new Date();
            String fechaTemp = dateFormat.format(date);
            StringTokenizer st = new StringTokenizer(fechaTemp);
            String fechaT = st.nextToken();
            String horaT = st.nextToken();
            st = new StringTokenizer(fechaT, "-");
            fecha = st.nextToken() + st.nextToken() + st.nextToken();
            st = new StringTokenizer(horaT, ":");
            fecha += st.nextToken() + st.nextToken() + st.nextToken();
        } else if (acc == 3) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
            Date date = new Date();
            fecha = dateFormat.format(date);
        } else if (acc == 4) {
            //SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = new Date();
            fecha = dateFormat.format(date);
        }
        return fecha;
    }

    public String getQ1(String SQL) {
        ConsultaGeneral conGen = new ConsultaGeneral();
        String va = "";
        ArrayList<String>[] objV = conGen.queryObjeto(contexto, SQL, null);
        if (objV != null) {
            va = objV[0].get(0).toString();
        }
        return va;
    }

    public boolean validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    public void Ir(String pantalla) {
        Intent p;
        switch(pantalla){
            case "Login":
                try{
                    FirebaseAuth.getInstance().signOut();
                }catch (Exception e){
                    Log.d("Error:",e.getMessage().toString());
                }
                return;
            case "MisAsteroides":
                p = new Intent(this, AsteroidesActivity.class);
                startActivity(p);
                break;
            case "DesAtributos":
                p = new Intent(this, DesAsteroideActivity.class);
                startActivity(p);
                break;
        }
    }

}

