package com.example.appasi;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
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

public class Horario extends Fragment {
    GridView gridView;
    TextView titleTV;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_horario,container,false);
        gridView = view.findViewById(R.id.gridView);
        titleTV = view.findViewById(R.id.titleHorario);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("SesionUsuario", getContext().MODE_PRIVATE);
        String codusu = sharedPreferences.getString("usuario_id", "0");
        String url = "https://demos.jyldigital.com/web-asi/_api_asistencias/consultaHorario.php";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url + "?codusu="+codusu,
                new Response.Listener<String>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(String response) {
                        FormatHora formatHora=new FormatHora();
                        try {
                            JSONArray jsonArrayBase = new JSONArray(response);
                            JSONObject jsonFecha = jsonArrayBase.getJSONObject(0);
                            titleTV.setText("Horario "+jsonFecha.getString("fecha"));
                            JSONArray jsonArray = jsonArrayBase.getJSONArray(1);
                            List<String> textList1 = new ArrayList<>();
                            List<String> textList2 = new ArrayList<>();
                            List<String> textList3 = new ArrayList<>();
                            textList1.add("HORA");
                            textList2.add("AULA");
                            textList3.add("CURSO");
                            for (int i=0;i<jsonArray.length();i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                textList1.add(formatHora.getFormatHora(jsonObject.getString("hora_inicio"))+" a "+
                                        formatHora.getFormatHora(jsonObject.getString("hora_fin")));
                                textList2.add(jsonObject.getString("aula"));
                                textList3.add(jsonObject.getString("curso"));
                            }
                            GridAdapterHorario adapter = new GridAdapterHorario(getContext(), textList1, textList2, textList3);
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
