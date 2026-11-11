# Laboratorio: Google Maps Intents for Android

**Asignatura:** Desarrollo de aplicaciones para dispositivos convergentes
**Universidad:** Universidad Piloto de Colombia — Ingenieria de Sistemas
**Autor:** Diego Alejandro Betancur Herrera ([@Diegoab02](https://github.com/Diegoab02))

## Objetivo

Comprender el manejo del dispositivo GPS en Android mediante el uso de intents del servicio Google Maps, aplicado a la planeacion de recorridos diarios (encomiendas / tutorias) y a la busqueda del docente mas cercano a un estudiante.

## Requerimientos cumplidos

| # | Requerimiento | Implementacion |
|---|---------------|----------------|
| 1 | Invocar el servicio (intent) de Google Maps | Metodo `lanzar(Uri)` con `Intent.ACTION_VIEW` + `setPackage("com.google.android.apps.maps")` |
| 2 | Senalar la ubicacion de cada punto | `verUbicacion()` usando URI `geo:lat,lng?q=lat,lng(etiqueta)` |
| 3 | Alternativas de ruta al docente mas cercano | `rutaDocenteCercano()` con calculo Haversine + URI `maps/dir/?api=1&origin=...&destination=...` |
| + | Recorrido diario con varias paradas | `recorridoDiario()` con `&waypoints=A|B|C` ordenadas por vecino mas cercano |

## Estructura

```
app/src/main/
  java/com/unipiloto/mapsintents/MainActivity.java
  res/layout/activity_main.xml
  AndroidManifest.xml
```

## Puntos clave

- **`setPackage("com.google.android.apps.maps")`** garantiza que Google Maps maneje el intent.
- **`<queries>`** en el `AndroidManifest.xml` es obligatorio desde Android 11 (API 30) para que `setPackage` encuentre la app.
- **Fallback**: si Maps no esta instalado, se reintenta sin `setPackage` para delegar en navegador u otra app de mapas, y si no hay ninguna se muestra un `Toast`.
- **Algoritmo de cercania**: distancia Haversine (linea recta) sobre coordenadas conocidas. Para distancias por calles reales seria necesario consumir la Directions API con llave.

## Como ejecutar

1. Abrir el proyecto en Android Studio.
2. Compilar y desplegar en un dispositivo o emulador con Google Play Services (Google Maps instalado).
3. Pulsar cada boton para probar los 4 escenarios.
