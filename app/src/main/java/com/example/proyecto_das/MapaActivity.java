package com.example.proyecto_das;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MapaActivity extends AppCompatActivity {

    private MapView map;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx,PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.activity_mapa);

        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);

        GeoPoint startPoint = new GeoPoint(42.8467, -2.6731);
        map.getController().setZoom(15.0);
        map.getController().setCenter(startPoint);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Si no hay permiso, lo pedimos
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
        } else {
            // Si ya hay permiso, obtenemos la ubicación
            obtenerUbicacion();
        }
    }

    @SuppressLint("MissingPermission")
    private void obtenerUbicacion() {
        // Obtenemos la última ubicación
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    // Creamos el punto con latitud y longitud
                    GeoPoint miPosicion = new GeoPoint(location.getLatitude(), location.getLongitude());

                    // Centramos el mapa y aplicamos zoom para que se vea bien
                    map.getController().setZoom(17.0);
                    map.getController().setCenter(miPosicion);

                    // Añadimos un marcador en la posición
                    Marker miMarcador = new Marker(map);
                    miMarcador.setPosition(miPosicion);
                    miMarcador.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                    miMarcador.setTitle(getString(R.string.mi_ubi));
                    map.getOverlays().add(miMarcador);

                    buscarCinesCercanos(miPosicion);

                    map.invalidate(); // Refrescar el mapa para que aparezca el marcador
                } else {
                    Toast.makeText(MapaActivity.this, R.string.noUbicacion, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void buscarCinesCercanos(GeoPoint centro) {
        new Thread(() -> {
            try {
                String query = "[out:json];node[\"amenity\"=\"cinema\"](around:5000," + centro.getLatitude() + "," + centro.getLongitude() + ");out;";
                String urlStr = "https://overpass-api.de/api/interpreter?data=" + java.net.URLEncoder.encode(query, "UTF-8");

                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .build();

                Request request = new Request.Builder()
                        .url(urlStr)
                        .header("Accept", "application/json")
                        .header("User-Agent", "Mozilla/5.0")
                        .build();

                Response response = client.newCall(request).execute();

                android.util.Log.d("CINES", "Código: " + response.code());

                if (response.isSuccessful()) {
                    JSONArray elementos = new JSONObject(response.body().string()).getJSONArray("elements");

                    runOnUiThread(() -> {
                        for (int i = 0; i < elementos.length(); i++) {
                            try {
                                JSONObject cine = elementos.getJSONObject(i);
                                Marker m = new Marker(map);
                                m.setPosition(new GeoPoint(cine.getDouble("lat"), cine.getDouble("lon")));
                                m.setIcon(getResources().getDrawable(R.drawable.marcador_cine, null));
                                map.getOverlays().add(m);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        map.invalidate();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            obtenerUbicacion();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
    }
}
