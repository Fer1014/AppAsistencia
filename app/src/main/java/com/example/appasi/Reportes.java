package com.example.appasi;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import androidx.fragment.app.Fragment;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class Reportes extends Fragment {
    GridView gridView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_report,container,false);
        gridView = view.findViewById(R.id.gridView);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("SesionUsuario", getContext().MODE_PRIVATE);
        String codusu = sharedPreferences.getString("usuario_id", "0");
        String url = "https://demos.jyldigital.com/web-asi/_api_asistencias/historialAsistencia.php";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url + "?codusu="+codusu,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        FormatHora formatHora=new FormatHora();
                        Log.d("JSON",response);
                        try {
                            FormatHora fh=new FormatHora();
                            JSONArray jsonArray = new JSONArray(response);
                            List<String> textList1 = new ArrayList<>();
                            List<String> textList2 = new ArrayList<>();
                            List<String> textList3 = new ArrayList<>();
                            List<String> textList4 = new ArrayList<>();
                            for (int i=0;i<jsonArray.length();i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                textList1.add(formatHora.getFormatHora(jsonObject.getString("hora_inicio"))+" a "+
                                        formatHora.getFormatHora(jsonObject.getString("hora_fin")));
                                textList2.add(jsonObject.getString("codcurso")+" | "+jsonObject.getString("curso"));
                                textList3.add("Fecha: "+ jsonObject.getString("fecha"));
                                if (jsonObject.getString("estado").equals("G")){
                                    textList4.add(jsonObject.getString("presente"));
                                }else{
                                    textList4.add("Marcación: "+fh.getFormatHora(jsonObject.getString("hora_marca"))+" - "+jsonObject.getString("tardanza"));
                                }
                            }
                            GridAdapter adapter = new GridAdapter(getContext(), textList1, textList2, textList3, textList4);
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