package com.example.appasi;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.output.ByteArrayOutputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class Perfil extends Fragment {
    TextView codigoTV,correoTV;
    ImageView fotoIV;
    Button updateFoto;
    private static final int REQUEST_PICK_IMAGE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    SharedPreferences sharedPreferences;
    CacheUtils cacheUtils=new CacheUtils();
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_perfil,container,false);
        codigoTV = view.findViewById(R.id.codigo);
        correoTV = view.findViewById(R.id.correo);
        fotoIV = view.findViewById(R.id.foto);
        updateFoto=view.findViewById(R.id.btnUpdateFoto);
        sharedPreferences = getContext().getSharedPreferences("SesionUsuario", getContext().MODE_PRIVATE);

        String codusu = sharedPreferences.getString("usuario_id", "0");
        String url = "https://demos.jyldigital.com/web-asi/_api_asistencias/select_usuario.php";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url + "?codusu="+codusu,
                new Response.Listener<String>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArrayBase = new JSONArray(response);
                            JSONObject jsonUsuario = jsonArrayBase.getJSONObject(0);
                            String email=jsonUsuario.getString("email");
                            String nombre=jsonUsuario.getString("nombre") + " "+jsonUsuario.getString("apellido");
                            String[] codigos = email.split("@");
                            codigoTV.setText(nombre);
                            correoTV.setText(email);
                            try {
                                File cachedFile = new File(getContext().getCacheDir(), "my_profile_photo.jpg");
                                Uri fileUri = FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".fileprovider", cachedFile);
                                fotoIV.setImageURI(fileUri);
                            } catch (Exception e) {
                                Log.e("ERRORY",e.getMessage());
                                Glide.with(getContext())
                                        .load(jsonUsuario.getString("foto_perfil_url"))
                                        .into(fotoIV);
                            }
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

        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }

        updateFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, REQUEST_PICK_IMAGE);
                }else{
                    ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                }
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                fotoIV.setImageBitmap(imageBitmap); // Muestra la imagen en un ImageView
            } else if (requestCode == REQUEST_PICK_IMAGE) {
                Uri selectedImage = data.getData();
                try {
                    cacheUtils.saveUriToCache(getContext(),selectedImage,"my_profile_photo.jpg");
                    Log.d("TRY","hola");
                } catch (Exception e) {
                    Log.e("ERRORY",e.getMessage());
                    throw new RuntimeException(e);
                }
                fotoIV.setImageURI(selectedImage); // Muestra la imagen seleccionada
            }
        }
    }
}
