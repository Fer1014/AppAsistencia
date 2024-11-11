package com.example.appasi;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class HorSem extends Fragment {
    GridView gridView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_horsem,container,false);
        gridView = view.findViewById(R.id.gridView);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("SesionUsuario", getContext().MODE_PRIVATE);
        String codusu = sharedPreferences.getString("usuario_id", "0");
        String url = "https://demos.jyldigital.com/web-asi/_api_asistencias/horarioSemanal.php";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url + "?codusu="+codusu,
                new Response.Listener<String>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(String response) {
                        FormatHora formatHora=new FormatHora();
                        Log.d("JSON",response);
                        List<String> dias_semana = new ArrayList<>();
                        dias_semana.add("Lunes");
                        dias_semana.add("Martes");
                        dias_semana.add("Miercoles");
                        dias_semana.add("Jueves");
                        dias_semana.add("Viernes");
                        dias_semana.add("Sabado");
                        dias_semana.add("Domingo");
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            List<String> textList1 = new ArrayList<>();
                            List<String> textList2 = new ArrayList<>();
                            List<String> textList3 = new ArrayList<>();
                            List<String> textList4 = new ArrayList<>();
                            String dia_antes="";
                            for (int i=0;i<jsonArray.length();i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                if(dia_antes.equals("")){
                                    dia_antes=jsonObject.getString("dia_semana");
                                }
                                if (dia_antes.equals(jsonObject.getString("dia_semana")) && i!=0){
                                    textList1.add("");
                                }else{
                                    dia_antes=jsonObject.getString("dia_semana");
                                    textList1.add(dias_semana.get(Integer.valueOf(jsonObject.getString("dia_semana"))-1));
                                }
                                textList2.add(formatHora.getFormatHora(jsonObject.getString("hora_inicio"))+" a "+
                                        formatHora.getFormatHora(jsonObject.getString("hora_fin")));
                                textList3.add(jsonObject.getString("nombre_aula"));
                                textList4.add(jsonObject.getString("descripcion"));
                            }
                            GridAdapterHorSem adapter = new GridAdapterHorSem(getContext(), textList1, textList2, textList3, textList4);
                            gridView.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        MySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);

        return view;
    }
}
