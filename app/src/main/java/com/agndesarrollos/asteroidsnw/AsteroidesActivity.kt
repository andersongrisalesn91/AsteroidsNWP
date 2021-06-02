package com.agndesarrollos.asteroidsnw

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.agndesarrollos.asteroidsnw.utils.*
import com.apptakk.http_request.HttpRequest
import com.apptakk.http_request.HttpRequestTask
import com.apptakk.http_request.HttpResponse
import com.google.gson.JsonParser
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_asteroides.*
import org.json.JSONException
import java.util.*

class AsteroidesActivity : AppCompatActivity() {
    lateinit var operaciones: OperacionesBDInterna
    lateinit var fg: FuncionesGenerales
    internal var context: Context? = null
    lateinit var recyclerAsteroid: RecyclerView
    internal lateinit var ListaA: ArrayList<AsteuModel>
    internal var ARid = mutableListOf<String>()
    internal var ARfechaaprox = mutableListOf<String>()
    internal var ARnombre = mutableListOf<String>()
    internal var ARtamaño = mutableListOf<String>()
    internal var param: Map<String, Any> = HashMap()
    lateinit var cGeneral: ConsultaGeneral
    lateinit var RelativeLAS: RelativeLayout

    internal inner class Asteu(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imgAste: ImageView
        var nAsteroid: TextView
        var tAsteroid: TextView
        var fAsteroid: TextView
        var cAsteroid: ConstraintLayout

        init {
            this.imgAste = itemView.findViewById(R.id.IVAste)
            this.nAsteroid = itemView.findViewById(R.id.TVNomAsteuc)
            this.tAsteroid = itemView.findViewById(R.id.TVTamano)
            this.fAsteroid = itemView.findViewById(R.id.TVFapproach)
            this.cAsteroid = itemView.findViewById(R.id.CLASTX)
        }
    }

    internal inner class AsteuModel
    (var npro: String, var tpro: String, var fpro: String)


    internal inner class AsteuAdapter(var ListaA: ArrayList<AsteuModel>) : RecyclerView.Adapter<Asteu>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Asteu {
            return Asteu(LayoutInflater.from(parent.context).inflate(R.layout.ver_ast, parent, false))
        }

        override fun onBindViewHolder(holder: Asteu, position: Int) {
            val opcion = ListaA[position]
            holder.nAsteroid.text = "Nombre : " + opcion.npro
            holder.tAsteroid.text = "Magni. KM : " + opcion.tpro
            holder.fAsteroid.text = "F. Aprox : " + opcion.fpro
            Picasso.get()
                    .load(R.drawable.astico1)
                    .centerCrop()
                    .transform(CircleTransform(50, 0))
                    .fit()
                    .into(holder.imgAste)
            holder.imgAste.setOnClickListener { IrAsteroid(position) }
            holder.nAsteroid.setOnClickListener { IrAsteroid(position) }
            holder.tAsteroid.setOnClickListener { IrAsteroid(position) }
            holder.fAsteroid.setOnClickListener { IrAsteroid(position) }
            holder.cAsteroid.setOnClickListener { IrAsteroid(position) }
        }

        override fun getItemCount(): Int {
            return ListaA.size
        }
    }


    private fun IrAsteroid(position: Int) {
        fg.actparam("astsel", ARid[position])
        fg.actparam("nomast", ARnombre[position])
        val intPA = Intent(this, DesAsteroideActivity::class.java)
        startActivity(intPA)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_asteroides)
        instance = this
    }

    override fun onStart() {
        super.onStart()

        fg = FuncionesGenerales(baseContext)
        fg.ultimaPantalla("MisAsteroides")
        param = fg.getparametros()

        fechacons_textView.setText(fg.fechaActual(4))

        //Definicion relative Asteroides
        RelativeLAS = findViewById(R.id.RLAsteroides)
        val inflaterA = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val vA = inflaterA.inflate(R.layout.ver_recycler, null)
        recyclerAsteroid = vA.findViewById(R.id.RVGeneral)
        RelativeLAS.addView(vA)
        val llmA = LinearLayoutManager(this)
        llmA.orientation = LinearLayoutManager.VERTICAL
        recyclerAsteroid.layoutManager = llmA

        fechacons_textView.setOnClickListener(){
            invocarDataPicker()
        }

        consultar_button.setOnClickListener(){
            DescargarAsteroides()
        }

        CerrarSesion_imagebutton.setOnClickListener(){
            CerrarSesion()
        }

        DescargarAsteroides()

    }

    fun DescargarAsteroides() {

        cGeneral = ConsultaGeneral()
        operaciones = OperacionesBDInterna(baseContext)
        fg = FuncionesGenerales(baseContext)
        var sqlinserta = ""
        var castn = 0
        var sqlinsertb = ""
        var castusr = 0
        val sqlAsteuc = "select count(id) from asteroids " +
                "where date='" + fechacons_textView.text.toString() + "' " +
                "and id in (select id from asteroids_users where user_id=" + param.get("userid").toString() +")"
        var cant_ast = fg.getQ1(sqlAsteuc)
            //Validar caracters en el evento de presionar una tecla
        if (cant_ast.toInt() > 0){
            MostrarAsteroides()
        }else{
            val connex = VerificarConex.revisarconexion(baseContext)
            if (connex) {
                try {
                    HttpRequestTask(
                            HttpRequest(ConstantRestApi.ROOT_URL + ConstantRestApi.KEY_GET_FI + fechacons_textView.text.toString() + ConstantRestApi.KEY_GET_FF + fechacons_textView.text.toString() + ConstantRestApi.API_KEY, HttpRequest.GET, null),
                            object : HttpRequest.Handler {
                                override fun response(response: HttpResponse) {
                                    if (response.body !== "" && response.body != null) {
                                        Log.d(this.javaClass.toString(), "Request successful!")
                                        var parser = JsonParser()

                                        // Obtain Array
                                        var gsonObj = parser.parse(response.body).asJsonObject
                                        var near = gsonObj.get("near_earth_objects").asJsonObject
                                        var diasel = near.get(fechacons_textView.text.toString()).asJsonArray

                                        for (ast in diasel) {
                                            var gsonobjdia = ast.asJsonObject
                                            var name = gsonobjdia.get("name").asString
                                            var refid = gsonobjdia.get("neo_reference_id").asString
                                            var urljsp = gsonobjdia.get("nasa_jpl_url").asString
                                            var magabs = gsonobjdia.get("absolute_magnitude_h").asString
                                            var ipha = gsonobjdia.get("is_potentially_hazardous_asteroid").asString
                                            var iso = gsonobjdia.get("is_sentry_object").asString

                                            var estimated = gsonobjdia.get("estimated_diameter").asJsonObject
                                            var estimatedkm = estimated.get("kilometers").asJsonObject
                                            var estimatedkmmin = estimatedkm.get("estimated_diameter_min").asString
                                            var estimatedkmmax = estimatedkm.get("estimated_diameter_max").asString

                                            var closeapproach = gsonobjdia.get("close_approach_data").asJsonArray
                                            var cadf: String?
                                            var relvelkms: String?
                                            var missdiskm: String?
                                            for (cad in closeapproach) {
                                                var gsonobjcad = cad.asJsonObject
                                                cadf = gsonobjcad.get("close_approach_date_full").asString
                                                var relvel = gsonobjcad.get("relative_velocity").asJsonObject
                                                relvelkms = relvel.get("kilometers_per_second").asString
                                                var missdis = gsonobjcad.get("miss_distance").asJsonObject
                                                missdiskm = missdis.get("kilometers").asString
                                                if (fg.existeast(name) == 0) {
                                                    castn++
                                                    sqlinserta = sqlinserta + "('" + name + "' , " +
                                                            "'" + urljsp + "' , " +
                                                            "'" + magabs + "' , " +
                                                            "'" + estimatedkmmin + "' , " +
                                                            "'" + estimatedkmmax + "' , " +
                                                            "'" + ipha + "' , " +
                                                            "'" + cadf + "' , " +
                                                            "'" + relvelkms + "' , " +
                                                            "'" + missdiskm + "' , " +
                                                            "'" + iso + "' , " +
                                                            "'" + refid + "' , " +
                                                            "'" + fechacons_textView.text.toString() + "') , "
                                                }
                                                if (fg.existeastusr(refid, param.get("userid").toString()) == 0) {
                                                    castusr++
                                                    sqlinsertb = sqlinsertb + "((select id from asteroids where neo_reference_id=" + refid + ") , " + param.get("userid").toString() + ") , "
                                                }

                                            }
                                        }
                                        try{
                                            if (castn > 0) {
                                                var sqlinsertnc = "insert or ignore into asteroids (name, nasa_jpl_url,absolute_magnitude_h, estimated_diameter_km_min, estimated_diameter_km_max, is_potentially_hazardous_asteroid, close_approach_date_full, relative_velocity_kms, missing_distance_km, is_sentry_object, neo_reference_id,date) values " + sqlinserta.substring(0, (sqlinserta.length - 3)) + ";"
                                                operaciones.queryNoData(sqlinsertnc)
                                                operaciones.close()
                                            }
                                            if (castusr > 0) {
                                                operaciones = OperacionesBDInterna(baseContext)
                                                var sqlinsertnc = "insert or ignore into asteroids_users (id, user_id) values " + sqlinsertb.substring(0, (sqlinsertb.length - 3)) + ";"
                                                operaciones.queryNoData(sqlinsertnc)
                                                operaciones.close()
                                            }
                                        }finally {
                                            MostrarAsteroides()
                                        }
                                    } else {
                                        Log.e(this.javaClass.toString(), "Request unsuccessful: $response")

                                    }
                                }
                            }).execute()
                } catch (e: JSONException) {
                    Toast.makeText(baseContext, "Error al descargar los asteroides : " + e.message, Toast.LENGTH_SHORT).show()
                    return
                }
            }else{
                Toast.makeText(baseContext, "No hay conexion ni asteroides guardados para esta fecha. ", Toast.LENGTH_SHORT).show()
            }
        }

    }

    fun MostrarAsteroides(){
        ListaA = ArrayList()
        val sqlAsteuc = "select id , name, absolute_magnitude_h, close_approach_date_full " +
                "from asteroids where date='" + fechacons_textView.text.toString() + "' and id in (select id from asteroids_users where user_id=" + param.get("userid").toString() +")"
        val OAsteuc = cGeneral.queryObjeto2val(baseContext, sqlAsteuc, null)
        if (OAsteuc != null) {
            ARid = mutableListOf<String>()
            ARnombre = mutableListOf<String>()
            ARtamaño = mutableListOf<String>()
            ARfechaaprox = mutableListOf<String>()

            for (cli in OAsteuc.indices) {
                ARid.add(OAsteuc[cli][0])
                ARnombre.add(OAsteuc[cli][1])
                ARtamaño.add(OAsteuc[cli][2])
                ARfechaaprox.add(OAsteuc[cli][3])

                val modelAC = AsteuModel(ARnombre[cli], ARtamaño[cli], ARfechaaprox[cli])
                ListaA.add(modelAC)
            }
            val maAC = AsteuAdapter(ListaA)
            recyclerAsteroid.adapter = maAC
        } else {
            Toast.makeText(baseContext, "No hay asteroides para mostrar. ", Toast.LENGTH_SHORT).show()
        }
    }

    fun CerrarSesion() {
        val mapPop = HashMap<String, String>()
        mapPop["Titulo"] = "Cerrar Sesion"
        mapPop["Mensaje"] = "Desea Cerrar Sesion"
        mapPop["TextoBTNo"] = "Cancelar"
        mapPop["TextoBTSi"] = "Confirmar"
        mapPop["Clase"] = "MainActivity"
        mapPop["Sesion"] = "1"
        PopUps().popUpConfirmar(baseContext, getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater, mapPop)
    }

    fun invocarDataPicker() {
        showDatePickerDialog()
    }

    private fun showDatePickerDialog() {
        val newFragment = DatePickerFragmento.newInstance { datePicker, year, month, day ->
            val selectedDate = year.toString() + "-" + dosDigitos(month + 1) + "-" + dosDigitos(day)
            fechacons_textView.setText(selectedDate)
        }
        newFragment.show(instance.supportFragmentManager, "datePicker")
    }

    private fun dosDigitos(n: Int): String? {
        return if (n <= 9) "0$n" else n.toString()
    }

    companion object {

        lateinit var instance: AsteroidesActivity
            internal set
    }
}
