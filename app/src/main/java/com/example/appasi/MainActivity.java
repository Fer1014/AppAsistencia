package com.example.appasi;

import static androidx.core.app.ActivityCompat.requestPermissions;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import androidx.work.PeriodicWorkRequest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    EditText usuET,pasET;
    Button btnLogin;
    SharedPreferences sharedPreferences;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private Executor executor;
    Boolean sessionHuella = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        usuET=findViewById(R.id.usuario);
        pasET=findViewById(R.id.password);
        sharedPreferences = getApplicationContext().getSharedPreferences("SesionUsuario", Context.MODE_PRIVATE);

        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(MainActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(), "Bienvenido", Toast.LENGTH_SHORT).show();
                login();
            }

            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(), "Algo salió mal. Intente nuevamente", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Autenticación fallida", Toast.LENGTH_SHORT).show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Validación de Huella Digital")
                .setSubtitle("Coloca tu huella para autenticarte")
                .setNegativeButtonText("Cancelar")
                .build();

        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            }
        }

        if(sharedPreferences.getBoolean("sesionActiva", false)) {
            biometricPrompt.authenticate(promptInfo);
        }

        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usuario = String.valueOf(usuET.getText());
                String password = String.valueOf(pasET.getText());
                String url_login = "https://demos.jyldigital.com/web-asi/_api_asistencias/login.php";
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url_login + "?usuario=" + usuario + "&password=" + password,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if (response.equals("0")) {
                                    Toast.makeText(getBaseContext(), "Usuario incorrecto", Toast.LENGTH_SHORT).show();
                                } else {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        guardarSesionUsuario(getApplicationContext(), jsonObject.getString("usuario_id"),
                                                jsonObject.getString("email"));
                                        biometricPrompt.authenticate(promptInfo);
                                        //login();
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getBaseContext(), "ERROR", Toast.LENGTH_SHORT).show();
                    }
                });
                MySingleton.getInstance(getBaseContext()).addToRequestQueue(stringRequest);
            }
        });
    }

    public void login(){
        programarWorker();
        startActivity(new Intent(MainActivity.this, ContentActivity.class));
        finish();
    }

    public void guardarSesionUsuario(Context context, String usuario_id,String usuario_email) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("usuario_id", usuario_id);
        editor.putString("usuario_email", usuario_email);
        editor.putBoolean("sessionHuella", sessionHuella);
        editor.putBoolean("sesionActiva", true);
        editor.apply(); // O
    }

    public void programarWorker() {
        WorkManager.getInstance(this).cancelAllWork();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (getApplicationContext().checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(MyWorker.class, 30, TimeUnit.MINUTES).build();
        WorkManager.getInstance(this).enqueue(periodicWorkRequest);
    }
}