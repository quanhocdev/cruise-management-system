package com.project.cruise.android.ui.passenger.components.mybookingdetail

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.mapbox.common.MapboxOptions
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
import com.project.cruise.android.BuildConfig
import com.project.cruise.android.data.dto.passenger.tripdetail.*
import kotlin.math.ln

private const val ROUTE_COLOR = "#0B7285"
private const val ROUTE_DIM_COLOR = "#8FB8C4"
private const val LEG_COLOR = "#F5B301"

private fun circleBitmap(fill: Int, stroke: Int, radiusPx: Float, strokePx: Float = 5f): Bitmap {
    val size = ((radiusPx + strokePx) * 2).toInt().coerceAtLeast(24)
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val c = size / 2f
    canvas.drawCircle(c, c, radiusPx, Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL; color = fill })
    canvas.drawCircle(
        c, c, radiusPx - strokePx / 2f,
        Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE; strokeWidth = strokePx; color = stroke },
    )
    return bitmap
}

private fun portPoint(p: TripPortDto?): Point? {
    val lat = p?.latitude
    val lon = p?.longitude
    return if (lat != null && lon != null) Point.fromLngLat(lon, lat) else null
}

private fun legPoints(leg: TripRouteLegDto): List<Point> {
    val geo = extractGeoJsonPoints(leg.routeGeoJson)
    if (geo.size >= 2) return geo
    return listOfNotNull(portPoint(leg.fromPort), portPoint(leg.toPort))
}

private fun buildRoutePoints(overview: TripRouteOverviewDto?, legs: List<TripRouteLegDto>): List<Point> {
    val geo = extractGeoJsonPoints(overview?.routeGeoJson)
    if (geo.size >= 2) return geo
    val fromLegs = legs.sortedBy { it.legOrder ?: Int.MAX_VALUE }.flatMap { legPoints(it) }
    if (fromLegs.size >= 2) return fromLegs
    return listOfNotNull(portPoint(overview?.departurePort), portPoint(overview?.arrivalPort))
}

private fun buildPortPoints(overview: TripRouteOverviewDto?, legs: List<TripRouteLegDto>): List<Point> =
    (listOfNotNull(portPoint(overview?.departurePort), portPoint(overview?.arrivalPort)) +
            legs.flatMap { listOfNotNull(portPoint(it.fromPort), portPoint(it.toPort)) }).distinct()

/** Camera ôm trọn danh sách điểm (không phụ thuộc phiên bản API cameraForCoordinates). */
private fun cameraFor(points: List<Point>): CameraOptions {
    if (points.size == 1) return CameraOptions.Builder().center(points[0]).zoom(9.0).build()
    val minLat = points.minOf { it.latitude() }
    val maxLat = points.maxOf { it.latitude() }
    val minLon = points.minOf { it.longitude() }
    val maxLon = points.maxOf { it.longitude() }
    val span = maxOf(maxLat - minLat, maxLon - minLon, 0.02)
    val zoom = (ln(200.0 / span) / ln(2.0)).coerceIn(2.0, 14.0)
    return CameraOptions.Builder()
        .center(Point.fromLngLat((minLon + maxLon) / 2, (minLat + maxLat) / 2))
        .zoom(zoom)
        .build()
}

/**
 * Bản đồ lộ trình. Chọn chặng ([selectedLegKey]) thì tô vàng chặng đó và zoom vào;
 * null = cả hành trình. Vị trí tàu cập nhật không làm camera giật về chỗ tàu nữa.
 */
@Composable
fun TripRouteMap(
    overview: TripRouteOverviewDto?,
    legs: List<TripRouteLegDto>,
    vesselLocation: VesselLocationDto?,
    selectedLegKey: String?,
    modifier: Modifier = Modifier,
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    val allPoints = remember(overview, legs) { buildRoutePoints(overview, legs) }
    val selectedLeg = remember(legs, selectedLegKey) { legs.firstOrNull { it.resolvedKey == selectedLegKey } }
    val legLine = remember(selectedLeg) { selectedLeg?.let { legPoints(it) }.orEmpty() }
    val ports = remember(overview, legs) { buildPortPoints(overview, legs) }

    val vesselLat = vesselLocation?.latitude ?: overview?.currentLatitude
    val vesselLon = vesselLocation?.longitude ?: overview?.currentLongitude
    val vesselPoint = remember(vesselLat, vesselLon) {
        if (vesselLat != null && vesselLon != null) Point.fromLngLat(vesselLon, vesselLat) else null
    }

    val vesselBitmap = remember {
        circleBitmap(android.graphics.Color.parseColor("#F5B301"), android.graphics.Color.parseColor("#0B1F3A"), 24f, 6f)
    }
    val portBitmap = remember {
        circleBitmap(android.graphics.Color.parseColor("#0B1F3A"), android.graphics.Color.WHITE, 16f)
    }

    var mapView by remember { mutableStateOf<MapView?>(null) }
    var lineManager by remember { mutableStateOf<PolylineAnnotationManager?>(null) }
    var portManager by remember { mutableStateOf<PointAnnotationManager?>(null) }
    var vesselManager by remember { mutableStateOf<PointAnnotationManager?>(null) }
    var mapReady by remember { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner, mapView) {
        val view = mapView ?: return@DisposableEffect onDispose {}
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> view.onStart()
                Lifecycle.Event.ON_STOP -> view.onStop()
                Lifecycle.Event.ON_DESTROY -> view.onDestroy()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // Vẽ tuyến + cảng; chặng đang chọn tô vàng đè lên tuyến mờ.
    LaunchedEffect(lineManager, portManager, allPoints, legLine, ports) {
        val lines = lineManager ?: return@LaunchedEffect
        val pm = portManager ?: return@LaunchedEffect
        lines.deleteAll()
        pm.deleteAll()
        val hasLeg = legLine.size >= 2
        if (allPoints.size >= 2) {
            lines.create(
                PolylineAnnotationOptions()
                    .withPoints(allPoints)
                    .withLineColor(if (hasLeg) ROUTE_DIM_COLOR else ROUTE_COLOR)
                    .withLineWidth(if (hasLeg) 3.5 else 5.0)
            )
        }
        if (hasLeg) {
            lines.create(PolylineAnnotationOptions().withPoints(legLine).withLineColor(LEG_COLOR).withLineWidth(6.5))
        }
        ports.forEach { pm.create(PointAnnotationOptions().withPoint(it).withIconImage(portBitmap)) }
    }

    // Marker tàu: chỉ đổi marker, không đụng tới camera.
    LaunchedEffect(vesselManager, vesselPoint) {
        val vm = vesselManager ?: return@LaunchedEffect
        vm.deleteAll()
        vesselPoint?.let { vm.create(PointAnnotationOptions().withPoint(it).withIconImage(vesselBitmap)) }
    }

    // Camera: ôm chặng đang chọn hoặc cả tuyến. Chỉ bám tàu khi không có dữ liệu tuyến.
    val cameraTarget = legLine.ifEmpty { allPoints }
    val followVessel = if (cameraTarget.isEmpty()) vesselPoint else null
    LaunchedEffect(mapReady, cameraTarget, followVessel) {
        val view = mapView ?: return@LaunchedEffect
        if (!mapReady) return@LaunchedEffect
        val camera = when {
            cameraTarget.isNotEmpty() -> cameraFor(cameraTarget)
            followVessel != null -> CameraOptions.Builder().center(followVessel).zoom(8.0).build()
            else -> null
        }
        camera?.let { view.mapboxMap.setCamera(it) }
    }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            val token = BuildConfig.MAPBOX_PUBLIC_TOKEN.trim()
            if (token.isEmpty()) {
                Log.e("TripRouteMap", "MAPBOX_PUBLIC_TOKEN đang rỗng! Kiểm tra local.properties và app/build.gradle.kts")
            } else {
                MapboxOptions.accessToken = token
            }
            MapView(ctx).apply {
                mapView = this
                mapboxMap.loadStyle(Style.MAPBOX_STREETS) {
                    lineManager = annotations.createPolylineAnnotationManager()
                    portManager = annotations.createPointAnnotationManager()
                    vesselManager = annotations.createPointAnnotationManager()
                    mapReady = true
                }
            }
        },
    )
}

private fun extractGeoJsonPoints(element: JsonElement?): List<Point> {
    if (element == null || element.isJsonNull) return emptyList()
    val result = mutableListOf<Point>()

    fun walkCoords(arr: JsonArray?) {
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
        for (item in arr) if (item.isJsonArray) walkCoords(item.asJsonArray)
    }

    fun walkGeo(obj: JsonObject?) {
        if (obj == null) return
        when (obj.get("type")?.asString.orEmpty()) {
            "FeatureCollection" -> obj.getAsJsonArray("features")?.forEach { feat ->
                if (feat.isJsonObject) walkGeo(feat.asJsonObject.getAsJsonObject("geometry"))
            }
            "Feature" -> walkGeo(obj.getAsJsonObject("geometry"))
            else -> walkCoords(obj.getAsJsonArray("coordinates"))
        }
    }

    if (element.isJsonObject) walkGeo(element.asJsonObject)
    return result
}