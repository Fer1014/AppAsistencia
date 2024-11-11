package com.example.appasi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.work.WorkManager;

import com.google.android.material.navigation.NavigationView;

public class ContentActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout drawerLayout;
    SharedPreferences sharedPreferences;
    TextView userEmailTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.content_main);
        sharedPreferences = getApplicationContext().getSharedPreferences("SesionUsuario", Context.MODE_PRIVATE);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout=findViewById(R.id.drawer_layout);
        NavigationView navigationView=findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle= new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open_nav,R.string.open_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        if (savedInstanceState==null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new Inicio()).commit();
            navigationView.setCheckedItem(R.id.nav_inicio);
        }
        View v = navigationView.getHeaderView(0);
        userEmailTV=v.findViewById(R.id.user_email);
        userEmailTV.setText(sharedPreferences.getString("usuario_email", "Ninguno"));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.nav_inicio){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new Inicio()).commit();
        }
        if (item.getItemId()==R.id.nav_reportes){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new Reportes()).commit();
        }
        if (item.getItemId()==R.id.nav_horario){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new Horario()).commit();
        }
        if (item.getItemId()==R.id.nav_resumenasi){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ResumenAsi()).commit();
        }
        if (item.getItemId()==R.id.nav_horsem){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HorSem()).commit();
        }
        if (item.getItemId()==R.id.nav_perfil){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new Perfil()).commit();
        }
        if (item.getItemId()==R.id.nav_salir){
            WorkManager.getInstance(this).cancelAllWork();
            sharedPreferences.edit().clear().commit();
            startActivity(new Intent(ContentActivity.this, MainActivity.class));
            finish();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();;
        }
    }
}
