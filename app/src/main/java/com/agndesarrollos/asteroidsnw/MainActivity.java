package com.agndesarrollos.asteroidsnw;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static final int MULT_PERMISOS = 4;
    private String[] permissions = new String[]{Manifest.permission.INTERNET, Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE};
    Context context;
    static MainActivity activityA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activityA = this;
    }


    @Override
    protected void onStart() {
        super.onStart();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        revisarPermisos();
    }

    public void revisarPermisos() {
        //Verifica si los permisos establecidos se encuentran concedidos
        if (ActivityCompat.checkSelfPermission(MainActivity.this, permissions[0]) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(MainActivity.this, permissions[1]) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(MainActivity.this, permissions[2]) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(MainActivity.this, permissions[3]) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(MainActivity.this, permissions[4]) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(MainActivity.this, permissions[5]) != PackageManager.PERMISSION_GRANTED) {
            //Si alguno de los permisos no esta concedido lo solicita
            try {
                ActivityCompat.requestPermissions(MainActivity.this, permissions, MULT_PERMISOS);
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "Recuerde otorgar todos los permisos, de lo contrario la aplicación fallara.", Toast.LENGTH_LONG).show();
                revisarPermisos();
            } finally {
                revisarPermisos();
            }
        } else {
            IrLogin();
        }
    }


    public void IrLogin() {
        Intent p;
        p = new Intent(this, Login.class);
        startActivity(p);
    }

    public static MainActivity getInstance() {
        return activityA;
    }
}
