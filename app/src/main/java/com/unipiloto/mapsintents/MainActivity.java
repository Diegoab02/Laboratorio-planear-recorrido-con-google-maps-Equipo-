package com.unipiloto.mapsintents;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Laboratorio: Google Maps Intents for Android
 * Desarrollo de aplicaciones para dispositivos convergentes
 * Universidad Piloto de Colombia
 *
 * Requerimientos cubiertos:
 *  1. Invocar el servicio (intent) de Google Maps.
 *  2. Señalar la ubicacion de cada punto del proyecto.
 *  3. Establecer alternativas de ruta para que el estudiante llegue
 *     al docente mas cercano, ahorrando tiempo y recursos.
 *  Extra: planear el recorrido diario de encomiendas / tutorias con
 *         multiples paradas (waypoints) ordenadas por cercania.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MapsIntents";
    private static final String MAPS_PKG = "com.google.android.apps.maps";

    // Ubicacion de referencia: Universidad Piloto de Colombia (sede Bogota)
    private static final double EST_LAT = 4.6286;
    private static final double EST_LNG = -74.0647;

    // Catalogo de docentes disponibles para tutoria
    private final List<Docente> docentes = Arrays.asList(
            new Docente("Ing. Pedraza",   4.6350, -74.0680),
            new Docente("Ing. Rodriguez", 4.6250, -74.0600),
            new Docente("Ing. Martinez",  4.6400, -74.0700)
    );

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);
    }

    // Req. 1 y 2: Invocar Google Maps mostrando la ubicacion de un punto
    public void verUbicacion(View v) {
        Uri uri = Uri.parse("geo:" + EST_LAT + "," + EST_LNG
                + "?q=" + EST_LAT + "," + EST_LNG + "(Universidad+Piloto)");
        lanzar(uri);
    }

    // Req. 3a: Ruta origen -> destino con modo de transporte
    public void rutaOrigenDestino(View v) {
        Uri.Builder b = new Uri.Builder();
        b.scheme("https").authority("www.google.com")
         .appendPath("maps").appendPath("dir").appendPath("")
         .appendQueryParameter("api", "1")
         .appendQueryParameter("origin", EST_LAT + "," + EST_LNG)
         .appendQueryParameter("destination", "4.6350,-74.0680")
         .appendQueryParameter("travelmode", "driving");
        lanzar(Uri.parse(b.build().toString()));
    }

    // Req. 3b: Alternativa de ruta hacia el docente mas cercano
    public void rutaDocenteCercano(View v) {
        Docente cercano = Collections.min(docentes,
                Comparator.comparingDouble(d -> distanciaKm(EST_LAT, EST_LNG, d.lat, d.lng)));

        Log.d(TAG, "Docente mas cercano: " + cercano.nombre
                + " (" + String.format("%.2f", distanciaKm(EST_LAT, EST_LNG, cercano.lat, cercano.lng)) + " km)");

        String uri = "https://www.google.com/maps/dir/?api=1"
                + "&origin=" + EST_LAT + "," + EST_LNG
                + "&destination=" + cercano.lat + "," + cercano.lng
                + "&travelmode=walking";
        lanzar(Uri.parse(uri));
    }

    // Extra: recorrido diario con varias paradas (waypoints)
    public void recorridoDiario(View v) {
        List<Docente> ruta = ordenarPorCercania(EST_LAT, EST_LNG, docentes);
        Docente destino = ruta.remove(ruta.size() - 1);

        StringBuilder waypoints = new StringBuilder();
        for (int i = 0; i < ruta.size(); i++) {
            if (i > 0) waypoints.append("|");
            waypoints.append(ruta.get(i).lat).append(",").append(ruta.get(i).lng);
        }

        String uri = "https://www.google.com/maps/dir/?api=1"
                + "&origin=" + EST_LAT + "," + EST_LNG
                + "&destination=" + destino.lat + "," + destino.lng
                + "&waypoints=" + waypoints
                + "&travelmode=driving";
        lanzar(Uri.parse(uri));
    }

    // --- Helpers ---------------------------------------------------------

    private void lanzar(Uri uri) {
        Log.d(TAG, "URI: " + uri);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setPackage(MAPS_PKG);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Intent libre = new Intent(Intent.ACTION_VIEW, uri);
            if (libre.resolveActivity(getPackageManager()) != null) {
                startActivity(libre);
            } else {
                Toast.makeText(this, "Instale una aplicacion de mapas", Toast.LENGTH_LONG).show();
            }
        }
    }

    /** Distancia en km con la formula del Haversine. */
    private double distanciaKm(double lat1, double lng1, double lat2, double lng2) {
        double R = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                 + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                 * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    /** Ordena por vecino mas cercano desde un punto de partida. */
    private List<Docente> ordenarPorCercania(double lat, double lng, List<Docente> origen) {
        List<Docente> pendientes = new ArrayList<>(origen);
        List<Docente> ruta = new ArrayList<>();
        double cx = lat, cy = lng;
        while (!pendientes.isEmpty()) {
            final double fx = cx, fy = cy;
            Docente sig = Collections.min(pendientes,
                    Comparator.comparingDouble(d -> distanciaKm(fx, fy, d.lat, d.lng)));
            ruta.add(sig);
            pendientes.remove(sig);
            cx = sig.lat; cy = sig.lng;
        }
        return ruta;
    }

    private static class Docente {
        final String nombre; final double lat, lng;
        Docente(String n, double la, double lo) { nombre = n; lat = la; lng = lo; }
    }
}
