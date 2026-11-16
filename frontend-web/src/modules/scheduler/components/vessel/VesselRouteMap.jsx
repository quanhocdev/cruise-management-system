import { useEffect, useRef, useState } from "react";
import mapboxgl from "mapbox-gl";
import "mapbox-gl/dist/mapbox-gl.css";
import "../../styles/VesselRouteMap.css";

const MAPBOX_TOKEN = import.meta.env.VITE_MAPBOX_PUBLIC_TOKEN;
const ROUTE_SOURCE_ID = "vessel-route-source";
const ROUTE_LAYER_ID = "vessel-route-layer";

export default function VesselRouteMap({ activeGeoJson, vesselPosition }) {
  const mapContainerRef = useRef(null);
  const mapRef = useRef(null);
  const vesselMarkerRef = useRef(null);
  const [mapReady, setMapReady] = useState(false);

  // Khởi tạo Mapbox
  useEffect(() => {
    if (!mapContainerRef.current || mapRef.current) return;
    if (!MAPBOX_TOKEN) {
      console.error("Thiếu VITE_MAPBOX_PUBLIC_TOKEN trong environment.");
      return;
    }

    mapboxgl.accessToken = MAPBOX_TOKEN;
    const map = new mapboxgl.Map({
      container: mapContainerRef.current,
      style: "mapbox://styles/mapbox/streets-v12",
      center: [106.7009, 10.7769],
      zoom: 5,
    });

    mapRef.current = map;
    map.addControl(new mapboxgl.NavigationControl(), "top-right");
    map.addControl(new mapboxgl.FullscreenControl(), "top-right");

    map.on("load", () => {
      if (!map.getSource(ROUTE_SOURCE_ID)) {
        map.addSource(ROUTE_SOURCE_ID, {
          type: "geojson",
          data: { type: "FeatureCollection", features: [] },
        });
      }

      if (!map.getLayer(ROUTE_LAYER_ID)) {
        map.addLayer({
          id: ROUTE_LAYER_ID,
          type: "line",
          source: ROUTE_SOURCE_ID,
          layout: { "line-join": "round", "line-cap": "round" },
          paint: {
            "line-color": "#2563eb",
            "line-width": 5,
            "line-opacity": 0.85,
          },
        });
      }
      setMapReady(true);
    });

    return () => {
      if (vesselMarkerRef.current) vesselMarkerRef.current.remove();
      map.remove();
      mapRef.current = null;
      setMapReady(false);
    };
  }, []);

  // Vẽ GeoJSON (Tổng quan hoặc Chặng) lên Mapbox
  useEffect(() => {
    const map = mapRef.current;
    if (!map || !mapReady || !activeGeoJson) return;

    const source = map.getSource(ROUTE_SOURCE_ID);
    if (!source) return;

    const feature = {
      type: "Feature",
      properties: {},
      geometry: activeGeoJson.geometry || activeGeoJson,
    };

    source.setData({
      type: "FeatureCollection",
      features: [feature],
    });

    // Fit bounds theo tọa độ line
    const coords = feature.geometry?.coordinates;
    if (coords && coords.length > 0) {
      const bounds = new mapboxgl.LngLatBounds();
      const flatten = (arr) => {
        if (typeof arr[0] === "number") bounds.extend(arr);
        else arr.forEach(flatten);
      };
      coords.forEach(flatten);

      if (!bounds.isEmpty()) {
        map.fitBounds(bounds, { padding: 60, maxZoom: 8, duration: 800 });
      }
    }
  }, [mapReady, activeGeoJson]);

  // Render icon tàu real-time từ Redis
  useEffect(() => {
    const map = mapRef.current;
    if (!map || !mapReady || !vesselPosition) return;

    const { latitude, longitude } = vesselPosition;
    if (!Number.isFinite(latitude) || !Number.isFinite(longitude)) return;

    if (!vesselMarkerRef.current) {
      const el = document.createElement("div");
      el.innerHTML = `
        <div class="vessel-realtime-badge">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M3 11l19-9-9 19-2-8-8-2z"/>
          </svg> 
          Tàu đang chạy
        </div>
      `;

      vesselMarkerRef.current = new mapboxgl.Marker({ element: el })
        .setLngLat([longitude, latitude])
        .addTo(map);
    } else {
      vesselMarkerRef.current.setLngLat([longitude, latitude]);
    }
  }, [mapReady, vesselPosition]);

  return (
    <div className="vessel-map-card">
      <h3 className="vessel-map-title">Bản đồ Giám sát Hành trình</h3>
      <div ref={mapContainerRef} className="vessel-map-container" />
    </div>
  );
}
