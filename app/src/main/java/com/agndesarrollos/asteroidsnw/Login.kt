package com.agndesarrollos.asteroidsnw

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.agndesarrollos.asteroidsnw.utils.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_logeo.*


class Login : AppCompatActivity() {
    var context: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logeo)
        instance = this
        try {
            val cons = ConsultaGeneral()
            val queryUP = "SELECT va FROM PA WHERE pa=?"
            val pant = cons.queryObjeto(baseContext, queryUP, arrayOf("ultP"))
            if (pant != null) {
                val ultima = pant[0][0]
                Ir(ultima)
            } else {
                Toast.makeText(baseContext, "No se puede acceder a la base de datos", Toast.LENGTH_LONG).show()
            }
        } catch (c: Exception) {
            Log.i("Error ocreate:", c.toString())
        }
    }

    override fun onStart() {
        super.onStart()
        val ivlogox = findViewById<ImageView>(R.id.ivLogo)
        ivlogox.isSelected = true

        Login_button.setOnClickListener() {
            login()
        }
        Registro_button.setOnClickListener() {
            registro()
        }
    }

    private fun login() {
        val connex = VerificarConex.revisarconexion(baseContext)
        if (connex) {
            login_online()
        } else {
            login_offline()
        }
    }

    fun login_online() {
        var fg = FuncionesGenerales(this)
        if (userLogin.text.isEmpty() || passLogin.text.isEmpty()) {
            Toast.makeText(this, "Debe Ingresar el Usuario y la contraseña para Registrarse", Toast.LENGTH_SHORT).show()
            return
        }
        if (fg.validarEmail(userLogin.text.toString().trim())) {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(userLogin.text.toString().trim(), StringEncryption.SHA1(passLogin.text.toString().trim())).addOnCompleteListener {
                if (it.isSuccessful) {
                    val sqluser = "insert or ignore into users (email , encrypted_password,login_online)" +
                            " values ('" + userLogin.text.toString().trim() + "' , " +
                            "'" + StringEncryption.SHA1(passLogin.text.toString().trim()) + "' ,1);"
                    val operaciones = OperacionesBDInterna(this)
                    operaciones.queryNoData(sqluser)
                    IngresoCompletado(1)
                } else {
                    Toast.makeText(baseContext, "Logeo Fallido, verifique los datos e intentelo nuevamente, si no esta registrado, registrese primero.",
                            Toast.LENGTH_LONG).show()
                }
            }
        } else {
            Toast.makeText(this, "Correo invalido", Toast.LENGTH_SHORT).show()
        }


    }

    fun login_offline() {
        val usuario: String
        val pass: String
        var fg = FuncionesGenerales(baseContext)
        if (userLogin.text.isEmpty() || passLogin.text.isEmpty()) {
            Toast.makeText(this, "Debe Ingresar el Usuario y la contraseña para ingresar", Toast.LENGTH_SHORT).show()
            return
        }
        if (!fg.validarEmail(userLogin.text.toString())) {
            Toast.makeText(this, "Correo invalido", Toast.LENGTH_SHORT).show()
            return
        }

        usuario = userLogin.text.toString().trim()
        pass = passLogin.text.toString().trim()
        val cGeneral = ConsultaGeneral()

        val query = "SELECT encrypted_password FROM users WHERE email='" + usuario + "'"
        val obj = cGeneral.queryObjeto2val(baseContext, query, null)
        if (obj == null) {
            userLogin.setText("")
            passLogin.setText("")
            Toast.makeText(this, "Usuario no encontrado, por favor registrese o verifique la informacion ingresada, si esta intentando realizar el logeo offline, recuerde que debe haber realizado login online al menos 1 vez", Toast.LENGTH_LONG).show()
        } else {
            if (obj[0][0] == StringEncryption.SHA1(pass)) {
                IngresoCompletado(2)
            } else {
                passLogin.setText("")
                Toast.makeText(this, "Contraseña incorrecta, intentelo nuevamente", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun registro() {
        val fg = FuncionesGenerales(baseContext)
        if (userLogin.text.isEmpty() || passLogin.text.isEmpty()) {
            Toast.makeText(this, "Debe Ingresar el Usuario y la contraseña para Registrarse", Toast.LENGTH_SHORT).show()
            return
        }
        if (!fg.validarEmail(userLogin.text.toString())) {
            Toast.makeText(this, "Correo invalido", Toast.LENGTH_SHORT).show()
            return
        }
        val connex = VerificarConex.revisarconexion(baseContext)
        if (connex) {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(userLogin.text.toString().trim(), StringEncryption.SHA1(passLogin.text.toString().trim())).addOnCompleteListener {
                if (it.isSuccessful) {
                    val sqluser = "insert or ignore into users (email , encrypted_password,login_online)" +
                            " values ('" + userLogin.text.toString().trim() + "' , " +
                            "'" + StringEncryption.SHA1(passLogin.text.toString().trim()) + "' ,1);"
                    val operaciones = OperacionesBDInterna(this)
                    operaciones.queryNoData(sqluser)
                    IngresoCompletado(1)
                } else {
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(userLogin.text.toString().trim(), StringEncryption.SHA1(passLogin.text.toString().trim())).addOnCompleteListener() {
                        if (it.isSuccessful) {
                            IngresoCompletado(1)
                        } else {
                            Toast.makeText(baseContext, "Registro Fallido, intentelo nuevamente.",
                                    Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        } else {
            Toast.makeText(baseContext, "Registro Fallido, verifique su conexion.",
                    Toast.LENGTH_SHORT).show()
        }


    }

    fun IngresoCompletado(tipologeo: Int) {
        val fg = FuncionesGenerales(baseContext)
        val operaciones = OperacionesBDInterna(this)
        fg.ultimaPantalla("Asteroides")
        fg.actparam("userid", fg.getQ1("select id from users where email='" + userLogin.text.toString().trim() + "';"))
        fg.actparam("user", userLogin.text.toString().trim())
        fg.actparam("pass", StringEncryption.SHA1(passLogin.text.toString().trim()))
        fg.actparam("login_online", tipologeo.toString())
        fg.actparam("sesion", "1")
        operaciones.queryNoData("UPDATE users SET login_online=" + tipologeo.toString() + " WHERE email='" + userLogin.text.toString().trim() + "'")
        Toast.makeText(this, "Bienvenido:" + userLogin.text.toString(), Toast.LENGTH_SHORT).show()
        val intlogeo = Intent(this, AsteroidesActivity::class.java)
        intlogeo.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intlogeo, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        instance?.finishAfterTransition()
    }

    fun Ir(pantalla: String?) {
        val p: Intent
        when (pantalla) {
            "Login" -> {
                try {
                    FirebaseAuth.getInstance().signOut()
                } catch (e: java.lang.Exception) {
                    Log.d("Error:", e.message.toString())
                }
                return
            }
            "MisAsteroides" -> {
                p = Intent(this, AsteroidesActivity::class.java)
                startActivity(p)
            }
            "DesAtributos" -> {
                p = Intent(this, DesAsteroideActivity::class.java)
                startActivity(p)
            }
        }
    }

    companion object {
        var instance: Login? = null
    }
}