package com.example.appasi;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Inicio extends Fragment {

    TextView posTV, posAulaTV, distanciaTV;
    EditText cursoET, horarioET, aulaET, estadoET;
    Button btnConAsi,btnCalcular;
    private LocationManager lm;
    double latitudeUser = 0;
    double longitudeUser = 0;
    double latitudeAula = 0.0;
    double longitudAula = 0.0;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    String asistencia_id="";
    String estado_asi="G";
    double par_dis_aula=0.0;
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_inicio,container,false);

        posTV = view.findViewById(R.id.textPos);
        posAulaTV = view.findViewById(R.id.textPosAula);
        distanciaTV = view.findViewById(R.id.textDistancia);
        cursoET = view.findViewById(R.id.iptCurso);
        horarioET = view.findViewById(R.id.iptHorario);
        aulaET = view.findViewById(R.id.iptAula);
        estadoET = view.findViewById(R.id.iptEstAsi);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // Definir el callback de ubicación
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                //Toast.makeText(getContext(), "GPS", Toast.LENGTH_SHORT).show();
                if (locationResult == null) {
                    posTV.setText("No se pudo obtener la ubicación. Enciende tu ubicación y actualiza la distancia");
                    return;
                }
                if (estado_asi.equals("G")) {
                    for (Location location : locationResult.getLocations()) {
                        latitudeUser = location.getLatitude();
                        longitudeUser = location.getLongitude();
                        posTV.setText("Lat: " + String.valueOf(latitudeUser) + " / Lng: " + String.valueOf(longitudeUser));
                        if (latitudeAula != 0.0) {
                            double longitude1 = longitudeUser;
                            double latitude1 = latitudeUser;
                            if (longitude1 != 0) {
                                double longitude2 = longitudAula;
                                double latitude2 = latitudeAula;
                                double theta = longitude1 - longitude2;
                                double distance = 60 * 1.1515 * (180 / Math.PI) * Math.acos(
                                        Math.sin(latitude1 * (Math.PI / 180)) * Math.sin(latitude2 * (Math.PI / 180)) +
                                                Math.cos(latitude1 * (Math.PI / 180)) * Math.cos(latitude2 * (Math.PI / 180)) * Math.cos(theta * (Math.PI / 180))
                                );
                                distance = distance * 1609.344;
                                String disString = String.valueOf(Math.round(distance));
                                if (distance <= par_dis_aula) {
                                    btnConAsi.setVisibility(View.VISIBLE);
                                    distanciaTV.setText("APROBADO: Estas a " + disString + " metros del aula");
                                } else {
                                    btnConAsi.setVisibility(View.GONE);
                                    distanciaTV.setText("Debes estar mas cerca al aula. Estas a " + disString + "m. del aula");
                                }
                            } else {
                                btnConAsi.setVisibility(View.GONE);
                                if (longitudeUser == 0.0) {
                                    distanciaTV.setText("Debes encender tu ubicación y vuelve a calcular");
                                } else {
                                    distanciaTV.setText("No se pudo calcular la distancia");
                                }
                            }
                        }
                    }
                }
            }
        };

        String url = "https://demos.jyldigital.com/web-asi/_api_asistencias/select_parametros.php";
        StringRequest stringRequest2 = new StringRequest(Request.Method.GET, url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    FormatHora formatHora=new FormatHora();
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        par_dis_aula = jsonObject.getDouble("distancia_aula");
                        //Toast.makeText(getContext(), String.valueOf(par_dis_aula), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        MySingleton.getInstance(getContext()).addToRequestQueue(stringRequest2);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("SesionUsuario", getContext().MODE_PRIVATE);
        String codusu = sharedPreferences.getString("usuario_id", "0");
        String url2 = "https://demos.jyldigital.com/web-asi/_api_asistencias/select_horarioActual.php";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url2 + "?codusu=" + codusu,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        FormatHora formatHora=new FormatHora();
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            asistencia_id = jsonObject.getString("asistencia_id");
                            String nombre_aula = jsonObject.getString("nombre_aula");
                            String hora_marca = formatHora.getFormatHora(jsonObject.getString("hora_marca"));
                            String nombre_curso = jsonObject.getString("nombre_curso");
                            String descripcion = jsonObject.getString("descripcion");
                            String hora_inicio = formatHora.getFormatHora(jsonObject.getString("hora_inicio"));
                            String hora_fin = formatHora.getFormatHora(jsonObject.getString("hora_fin"));
                            String estado_tardanza = jsonObject.getString("estado_tardanza");
                            String estado = jsonObject.getString("estado");
                            aulaET.setText(nombre_aula);
                            cursoET.setText(nombre_curso + " - " + descripcion);
                            horarioET.setText("De " + hora_inicio + " a " + hora_fin);
                            if (estado.equals("G")) {
                                estado_asi="G";
                                estadoET.setText("PENDIENTE");
                            } else {
                                estado_asi="P";
                                btnConAsi.setVisibility(View.GONE);
                                if (estado_tardanza.equals("1")) {
                                    estadoET.setText("TARDANZA (" + hora_marca + ")");
                                } else {
                                    estadoET.setText("Registrado a las " + hora_marca);
                                }
                            }
                            String aula_lat = jsonObject.getString("coord_latitud");
                            String aula_lng = jsonObject.getString("coord_longitud");
                            latitudeAula = Double.valueOf(aula_lat);
                            longitudAula = Double.valueOf(aula_lng);
                            posAulaTV.setText("Lat: " + String.valueOf(aula_lat) + " / Lng: " + String.valueOf(aula_lng));

                            double longitude1 = longitudeUser;
                            double latitude1 = latitudeUser;
                            if (longitude1 != 0) {
                                double longitude2 = longitudAula;
                                double latitude2 = latitudeAula;
                                double theta = longitude1 - longitude2;
                                double distance = 60 * 1.1515 * (180 / Math.PI) * Math.acos(
                                        Math.sin(latitude1 * (Math.PI / 180)) * Math.sin(latitude2 * (Math.PI / 180)) +
                                                Math.cos(latitude1 * (Math.PI / 180)) * Math.cos(latitude2 * (Math.PI / 180)) * Math.cos(theta * (Math.PI / 180))
                                );
                                distance=distance * 1609.344;
                                String disString = String.valueOf(Math.round(distance));
                                if (distance <= par_dis_aula) {
                                    distanciaTV.setText("APROBADO: Estas a " + disString + " metros del aula");
                                } else {
                                    btnConAsi.setVisibility(View.GONE);
                                    distanciaTV.setText("Debes estar mas cerca al aula. Estas a " + disString + "m. del aula");
                                }
                            } else {
                                btnConAsi.setVisibility(View.GONE);
                                if (longitudeUser==0.0){
                                    distanciaTV.setText("Debes encender tu ubicación y vuelve a calcular");
                                }else{
                                    distanciaTV.setText("No se pudo calcular la distancia");
                                }
                            }
                            //Toast.makeText(getContext(), "INFO WEB", Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            horarioET.setText("");
                            aulaET.setText("");
                            cursoET.setText("");
                            estadoET.setText("");
                            posTV.setText("");
                            posAulaTV.setText("");
                            distanciaTV.setText("No hay horario pendiente");
                            btnConAsi.setVisibility(View.GONE);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Manejo del error
                //cursoEditView.setText("Error: " + error.toString());
            }
        });
        MySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);

        btnCalcular = view.findViewById(R.id.btnCalcular);
        btnCalcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new Inicio()).commit();
            }
        });

        btnConAsi = view.findViewById(R.id.btnAceptar);
        btnConAsi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url_updateAsistencia = "https://demos.jyldigital.com/web-asi/_api_asistencias/update_usuarioAsistencia.php";
                StringRequest stringRequest2 = new StringRequest(Request.Method.GET, url_updateAsistencia + "?asistencia_id=" + asistencia_id,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if (response.equals("1")) {
                                    Toast.makeText(getContext(), "REGISTRADO", Toast.LENGTH_SHORT).show();
                                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new Inicio()).commit();
                                } else {
                                    Toast.makeText(getContext(), "ERROR", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
                MySingleton.getInstance(getContext()).addToRequestQueue(stringRequest2);
            }
        });
        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    return;
                }
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {
                                    double latitudeUser = location.getLatitude();
                                    double longitudeUser = location.getLongitude();
                                    posTV.setText("Lat: " + String.valueOf(latitudeUser) + " / Lng: " + String.valueOf(longitudeUser));
                                } else {
                                    posTV.setText("No se pudo obtener la ubicación");
                                }
                            }
                        });
            } else {
                Toast.makeText(getContext(), "Permiso de ubicación denegado.", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(getContext(), "Permiso de ubicación requerido, la aplicación no podrá funcionar", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Iniciar las actualizaciones de ubicación
        startLocationUpdates();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Detener las actualizaciones de ubicación para ahorrar batería
        stopLocationUpdates();
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }
}