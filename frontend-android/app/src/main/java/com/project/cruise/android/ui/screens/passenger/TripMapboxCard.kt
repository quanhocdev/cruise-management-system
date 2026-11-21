package com.project.cruise.android.ui.screens.passenger

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.createPolylineAnnotationManager
import com.project.cruise.android.data.dto.passenger.tripdetail.TripRouteLegDto
import com.project.cruise.android.data.dto.passenger.tripdetail.TripRouteOverviewDto
import com.project.cruise.android.data.dto.passenger.tripdetail.VesselLocationDto
import android.util.Log
import com.mapbox.common.MapboxOptions
import com.project.cruise.android.BuildConfig

private fun createCircleMarkerBitmap(
    colorInt: Int,
    strokeColorInt: Int = android.graphics.Color.WHITE,
    radiusPx: Float = 22f,
    strokeWidthPx: Float = 5f
): Bitmap {
    val size = ((radiusPx + strokeWidthPx) * 2).toInt().coerceAtLeast(24)
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val center = size / 2f

    val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = colorInt
    }
    canvas.drawCircle(center, center, radiusPx, fillPaint)

    if (strokeWidthPx > 0f) {
        val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = strokeWidthPx
            color = strokeColorInt
        }
        canvas.drawCircle(center, center, radiusPx - strokeWidthPx / 2f, strokePaint)
    }
    return bitmap
}

@Composable
fun TripMapboxCard(
    overview: TripRouteOverviewDto?,
    legs: List<TripRouteLegDto>,
    vesselLocation: VesselLocationDto?,
    routeGeoJson: com.google.gson.JsonElement? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val vesselLat = vesselLocation?.latitude ?: overview?.currentLatitude
    val vesselLon = vesselLocation?.longitude ?: overview?.currentLongitude
    val vesselPoint = if (vesselLat != null && vesselLon != null) {
        Point.fromLngLat(vesselLon, vesselLat)
    } else {
        null
    }

    val depPoint = overview?.departurePort?.let { port ->
        val lat = port.latitude
        val lon = port.longitude
        if (lat != null && lon != null) Point.fromLngLat(lon, lat) else null
    }

    val arrPoint = overview?.arrivalPort?.let { port ->
        val lat = port.latitude
        val lon = port.longitude
        if (lat != null && lon != null) Point.fromLngLat(lon, lat) else null
    }

    val geoJsonPoints = remember(routeGeoJson) {
        extractGeoJsonPoints(routeGeoJson)
    }

    val routePoints = remember(geoJsonPoints, depPoint, legs, arrPoint, vesselPoint) {
        if (geoJsonPoints.isNotEmpty()) {
            geoJsonPoints
        } else {
            buildList {
                depPoint?.let { add(it) }
                legs.forEach { leg ->
                    val fLat = leg.fromPort?.latitude
                    val fLon = leg.fromPort?.longitude
                    if (fLat != null && fLon != null) add(Point.fromLngLat(fLon, fLat))

                    val tLat = leg.toPort?.latitude
                    val tLon = leg.toPort?.longitude
                    if (tLat != null && tLon != null) add(Point.fromLngLat(tLon, tLat))
                }
                arrPoint?.let { add(it) }
                if (isEmpty()) {
                    vesselPoint?.let { add(it) }
                }
            }
        }
    }

    val vesselBitmap = remember {
        createCircleMarkerBitmap(colorInt = android.graphics.Color.parseColor("#0284C7"), radiusPx = 24f)
    }
    val portBitmap = remember {
        createCircleMarkerBitmap(colorInt = android.graphics.Color.parseColor("#0F172A"), radiusPx = 18f)
    }

    var pointManager by remember { mutableStateOf<PointAnnotationManager?>(null) }
    var polylineManager by remember { mutableStateOf<PolylineAnnotationManager?>(null) }
    var mapViewRef by remember { mutableStateOf<MapView?>(null) }

    DisposableEffect(lifecycleOwner, mapViewRef) {
        val mapView = mapViewRef ?: return@DisposableEffect onDispose {}
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(pointManager, polylineManager, vesselPoint, depPoint, arrPoint, routePoints, routeGeoJson) {
        val pm = pointManager ?: return@LaunchedEffect
        val lm = polylineManager ?: return@LaunchedEffect

        pm.deleteAll()
        lm.deleteAll()

        if (routePoints.size >= 2) {
            lm.create(
                PolylineAnnotationOptions()
                    .withPoints(routePoints)
                    .withLineColor("#0284C7")
                    .withLineWidth(4.5)
            )
        }

        depPoint?.let { pt ->
            pm.create(
                PointAnnotationOptions()
                    .withPoint(pt)
                    .withIconImage(portBitmap)
            )
        }

        arrPoint?.let { pt ->
            pm.create(
                PointAnnotationOptions()
                    .withPoint(pt)
                    .withIconImage(portBitmap)
            )
        }

        vesselPoint?.let { pt ->
            pm.create(
                PointAnnotationOptions()
                    .withPoint(pt)
                    .withIconImage(vesselBitmap)
            )
            mapViewRef?.mapboxMap?.setCamera(
                CameraOptions.Builder()
                    .center(pt)
                    .zoom(10.5)
                    .build()
            )
        } ?: run {
            routePoints.firstOrNull()?.let { firstPt ->
                mapViewRef?.mapboxMap?.setCamera(
                    CameraOptions.Builder()
                        .center(firstPt)
                        .zoom(9.0)
                        .build()
                )
            }
        }
    }

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
    ) {
        AndroidView(
            modifier = Modifier.fillMaxWidth().height(260.dp),
            factory = { ctx ->
                val rawToken = BuildConfig.MAPBOX_PUBLIC_TOKEN.trim()
                if (rawToken.isEmpty()) {
                    Log.e("TripMapboxCard", "MAPBOX_PUBLIC_TOKEN đang rỗng! Kiểm tra local.properties và app/build.gradle.kts")
                } else {
                    MapboxOptions.accessToken = rawToken
                    Log.d("TripMapboxCard", "Đã gán MapboxOptions.accessToken (len=${rawToken.length})")
                }

                MapView(ctx).apply {
                    mapViewRef = this
                    mapboxMap.loadStyle(Style.MAPBOX_STREETS) { style ->
                        Log.d("TripMapboxCard", "Mapbox style loaded: ${style.styleURI}")
                        pointManager = annotations.createPointAnnotationManager()
                        polylineManager = annotations.createPolylineAnnotationManager()
                    }
                }
            }
        )
    }
}
private fun extractGeoJsonPoints(element: com.google.gson.JsonElement?): List<Point> {
    if (element == null || element.isJsonNull) return emptyList()
    val result = mutableListOf<Point>()

    fun walkCoords(arr: com.google.gson.JsonArray?) {
        if (arr == null || arr.size() == 0) return
        val first = arr[0]
        if (first.isJsonPrimitive && arr.size() >= 2) {
            val lon = runCatching { arr[0].asDouble }.getOrNull()
            val lat = runCatching { arr[1].asDouble }.getOrNull()
            if (lon != null && lat != null && lat in -90.0..90.0 && lon in -180.0..180.0) {
                result.add(Point.fromLngLat(lon, lat))
            }
            return
        }
        for (item in arr) {
            if (item.isJsonArray) walkCoords(item.asJsonArray)
        }
    }

    fun walkGeo(obj: com.google.gson.JsonObject?) {
        if (obj == null) return
        val type = obj.get("type")?.asString.orEmpty()
        when (type) {
            "FeatureCollection" -> {
                obj.getAsJsonArray("features")?.forEach { feat ->
                    if (feat.isJsonObject) {
                        walkGeo(feat.asJsonObject.getAsJsonObject("geometry"))
                    }
                }
            }
            "Feature" -> walkGeo(obj.getAsJsonObject("geometry"))
            else -> walkCoords(obj.getAsJsonArray("coordinates"))
        }
    }

    if (element.isJsonObject) {
        walkGeo(element.asJsonObject)
    }
    return result
}