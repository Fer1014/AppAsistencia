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

public class ResumenAsi extends Fragment {
    GridView gridView;
    TextView titleTV;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_horario,container,false);
        gridView = view.findViewById(R.id.gridView);
        titleTV = view.findViewById(R.id.titleHorario);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("SesionUsuario", getContext().MODE_PRIVATE);
        String codusu = sharedPreferences.getString("usuario_id", "0");
        String url = "https://demos.jyldigital.com/web-asi/_api_asistencias/resumenAsistencias.php";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url + "?codusu="+codusu,
                new Response.Listener<String>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArrayBase = new JSONArray(response);
                            JSONObject jsonFecha = jsonArrayBase.getJSONObject(0);
                            titleTV.setText("Resumen de asistencias al "+jsonFecha.getString("fecha"));
                            JSONArray jsonArray = jsonArrayBase.getJSONArray(1);
                            List<String> textList1 = new ArrayList<>();
                            List<String> textList2 = new ArrayList<>();
                            List<String> textList3 = new ArrayList<>();
                            List<String> textList4 = new ArrayList<>();
                            List<String> textList5 = new ArrayList<>();
                            for (int i=0;i<jsonArray.length();i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                textList1.add(jsonObject.getString("curso"));
                                double clases=Double.valueOf(jsonObject.getString("clases"));
                                double claPre=Double.valueOf(jsonObject.getString("presente"));
                                double claAus=Double.valueOf(jsonObject.getString("ausente"));
                                double claTar=Double.valueOf(jsonObject.getString("tardanza"));
                                double porPre=claPre*10000/clases;
                                porPre=Math.round(porPre);
                                double porAus=claAus*10000/clases;
                                porAus=Math.round(porAus);
                                double porTar=claTar*10000/clases;
                                porTar=Math.round(porTar);
                                textList2.add("Número de clases: "+jsonObject.getString("clases"));
                                textList3.add("Presente: "+jsonObject.getString("presente")+" ("+porPre/100+"%)");
                                textList4.add("Ausente: "+jsonObject.getString("ausente")+" ("+porAus/100+"%)");
                                textList5.add("Tardanza: "+jsonObject.getString("tardanza")+" ("+porTar/100+"%)");
                            }
                            GridAdapterResumenAsi adapter = new GridAdapterResumenAsi(getContext(), textList1, textList2, textList3
                                    , textList4, textList5);
                            gridView.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("ERROR",e.getMessage());
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
