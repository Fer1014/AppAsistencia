package com.example.appasi;

import static androidx.core.app.ActivityCompat.requestPermissions;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MyWorker extends Worker {

    private static final String CHANNEL_ID = "canal_1";

    public MyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("SesionUsuario", Context.MODE_PRIVATE);
        String codusu = sharedPreferences.getString("usuario_id", "0");
        String url = "https://demos.jyldigital.com/web-asi/_api_asistencias/_verify_class.php";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url + "?codusu=" + codusu,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            if (jsonArray.length()>0){
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                String hora_act = jsonObject.getString("hora_act");
                                String hora_inicio = jsonObject.getString("hora_inicio");
                                int horini=Integer.valueOf(String.valueOf(hora_inicio.charAt(0))+String.valueOf(hora_inicio.charAt(1)))*60+
                                        Integer.valueOf(String.valueOf(hora_inicio.charAt(2))+String.valueOf(hora_inicio.charAt(3)));
                                int horact=Integer.valueOf(String.valueOf(hora_act.charAt(0))+String.valueOf(hora_act.charAt(1)))*60+
                                        Integer.valueOf(String.valueOf(hora_act.charAt(2))+String.valueOf(hora_act.charAt(3)));
                                if (horini-horact<60){
                                    sendNotification("Proxima clase", "Tienes una clase en "+String.valueOf(horini-horact)+" minutos.");
                                }
                                Log.d("MyWorkeri", String.valueOf(horini-horact));
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
        return Result.success();
    }

    private void sendNotification(String title, String message) {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Canal de Notificaciones", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .build();

        notificationManager.notify(1, notification);
    }
}