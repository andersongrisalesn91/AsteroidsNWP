package com.agndesarrollos.asteroidsnw;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agndesarrollos.asteroidsnw.utils.ConsultaGeneral;
import com.agndesarrollos.asteroidsnw.utils.FuncionesGenerales;
import com.agndesarrollos.asteroidsnw.utils.PopUps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DesAsteroideActivity extends AppCompatActivity {
    ConsultaGeneral cGeneral;
    FuncionesGenerales fg;


    Context context;
    RecyclerView recyclerAtribuc;
    RelativeLayout RelativeL;
    static DesAsteroideActivity activityA;

    TextView TVNAst;
    ArrayList<AtribuModel> lista;
    String[] desAtrib,  ARvalorU;
    Map<String,Object> param;

    class Atribu extends RecyclerView.ViewHolder {
        TextView atributoc, valoruni;

        public Atribu(View itemView) {
            super(itemView);
            this.atributoc = itemView.findViewById(R.id.TVAtributoC);
            this.valoruni = itemView.findViewById(R.id.TVValorU);
        }
    }

    class AtribuModel {
        String atrib, valu;

        public AtribuModel(String atribuct, String valoru) {
            this.atrib = atribuct;
            this.valu = valoru;
        }
    }

    class AtribAdapter extends RecyclerView.Adapter<Atribu> {
        ArrayList<AtribuModel> lista;

        public AtribAdapter(ArrayList<AtribuModel> lista) {
            this.lista = lista;
        }

        @Override
        public Atribu onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Atribu(LayoutInflater.from(parent.getContext()).inflate(R.layout.ver_atributo, parent, false));
        }

        @Override
        public void onBindViewHolder(Atribu holder, final int position) {
            final AtribuModel opcion = lista.get(position);
            final Atribu holder2 = holder;

            holder.atributoc.setText(opcion.atrib);
            holder.valoruni.setText(opcion.valu);
        }

        @Override
        public int getItemCount() {
            return lista.size();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_des_asteroide);
        activityA = this;
        cGeneral = new ConsultaGeneral();
    }

    @Override
    protected void onStart() {
        super.onStart();
        cGeneral = new ConsultaGeneral();
        fg = new FuncionesGenerales(getBaseContext());
        fg.ultimaPantalla("DesAtributos");
        param = fg.getparametros();
        lista = new ArrayList<>();
        TVNAst = findViewById(R.id.TVNPantalla);
        TVNAst.setText(param.get("nomast").toString());
        //Definicion relative Actividades
        RelativeL = findViewById(R.id.RLDescnAsteroide);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.ver_recycler, null);
        recyclerAtribuc = v.findViewById(R.id.RVGeneral);
        RelativeL.addView(v);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerAtribuc.setLayoutManager(llm);
        CargarAtributos();

    }

    public void CargarAtributos() {

        cGeneral = new ConsultaGeneral();
        fg = new FuncionesGenerales(getBaseContext());
        String QAtribucA = "SELECT  natrib,valor FROM astsel order by natrib asc;";
        ArrayList<String>[] OAtribA = cGeneral.queryObjeto2val(getBaseContext(), QAtribucA, null);

        lista.clear();
        if (OAtribA != null) {
            desAtrib = new String[OAtribA.length];
            ARvalorU = new String[OAtribA.length];
            for (int cc = 0; cc < OAtribA.length; cc++) {
                desAtrib[cc] = OAtribA[cc].get(0);
                ARvalorU[cc] = OAtribA[cc].get(1);
                AtribuModel model = new AtribuModel(desAtrib[cc], ARvalorU[cc]);
                lista.add(model);
            }
            AtribAdapter ma = new AtribAdapter(lista);
            recyclerAtribuc.setAdapter(ma);
        }
    }

    public static DesAsteroideActivity getInstance() {
        return activityA;
    }
}


