// src/modules/passenger/components/trip-detail/TripRouteMap.jsx
import React, { useEffect, useRef, useState } from "react";
import mapboxgl from "mapbox-gl";
import "mapbox-gl/dist/mapbox-gl.css";
import "./TripRouteMap.css";

const MAPBOX_TOKEN = import.meta.env.VITE_MAPBOX_PUBLIC_TOKEN;

const SOURCE_ID = "passenger-trip-route-source";
const ROUTE_LAYER_ID = "passenger-trip-route-layer";

function extractCoordinates(geoJson) {
  if (!geoJson) return [];

  const coordinates = [];

  const visit = (value) => {
    if (!Array.isArray(value)) return;

    if (
      value.length >= 2 &&
      typeof value[0] === "number" &&
      typeof value[1] === "number"
    ) {
      coordinates.push(value);
      return;
    }

    value.forEach(visit);
  };

  if (geoJson.type === "FeatureCollection") {
    geoJson.features?.forEach((feature) => {
      visit(feature?.geometry?.coordinates);
    });
  } else if (geoJson.type === "Feature") {
    visit(geoJson.geometry?.coordinates);
  } else {
    visit(geoJson.coordinates);
  }

  return coordinates;
}

function normalizeGeoJson(geoJson) {
  if (!geoJson) return null;

  if (geoJson.type === "FeatureCollection") {
    return geoJson;
  }

  if (geoJson.type === "Feature") {
    return {
      type: "FeatureCollection",
      features: [geoJson],
    };
  }

  if (geoJson.type && geoJson.coordinates) {
    return {
      type: "FeatureCollection",
      features: [
        {
          type: "Feature",
          properties: {},
          geometry: geoJson,
        },
      ],
    };
  }

  return null;
}

function createRouteLayer(map) {
  if (!map.getLayer(ROUTE_LAYER_ID)) {
    map.addLayer({
      id: ROUTE_LAYER_ID,
      type: "line",
      source: SOURCE_ID,
      layout: {
        "line-join": "round",
        "line-cap": "round",
      },
      paint: {
        "line-color": "#2563eb",
        "line-width": 4,
        "line-opacity": 0.85,
      },
    });
  }
}

export default function TripRouteMap({
  routeGeoJson,
  vesselLocation,
  loading = false,
}) {
  const mapContainerRef = useRef(null);
  const mapRef = useRef(null);
  const vesselMarkerRef = useRef(null);

  const [mapError, setMapError] = useState("");

  useEffect(() => {
    if (!MAPBOX_TOKEN) {
      setMapError("Chưa cấu hình VITE_MAPBOX_PUBLIC_TOKEN.");
      return undefined;
    }

    if (!mapContainerRef.current || mapRef.current) {
      return undefined;
    }

    mapboxgl.accessToken = MAPBOX_TOKEN;

    const map = new mapboxgl.Map({
      container: mapContainerRef.current,
      style: "mapbox://styles/mapbox/streets-v12",
      center: [0, 0],
      zoom: 1,
      attributionControl: true,
    });

    map.addControl(new mapboxgl.NavigationControl(), "top-right");

    map.on("error", (event) => {
      if (event?.error?.message) {
        setMapError(event.error.message);
      }
    });

    mapRef.current = map;

    return () => {
      vesselMarkerRef.current?.remove();
      vesselMarkerRef.current = null;

      map.remove();
      mapRef.current = null;
    };
  }, []);

  useEffect(() => {
    const map = mapRef.current;
    if (!map) return;

    const normalized = normalizeGeoJson(routeGeoJson);
    const coordinates = extractCoordinates(routeGeoJson);

    const updateRoute = () => {
      const existingSource = map.getSource(SOURCE_ID);

      if (existingSource) {
        existingSource.setData(
          normalized || {
            type: "FeatureCollection",
            features: [],
          },
        );
      } else {
        map.addSource(SOURCE_ID, {
          type: "geojson",
          data: normalized || {
            type: "FeatureCollection",
            features: [],
          },
        });
      }

      createRouteLayer(map);

      if (coordinates.length > 0) {
        const bounds = new mapboxgl.LngLatBounds();

        coordinates.forEach(([longitude, latitude]) => {
          bounds.extend([longitude, latitude]);
        });

        map.fitBounds(bounds, {
          padding: 60,
          maxZoom: 12,
          duration: 800,
        });
      }
    };

    if (map.isStyleLoaded()) {
      updateRoute();
    } else {
      map.once("load", updateRoute);
    }
  }, [routeGeoJson]);

  useEffect(() => {
    const map = mapRef.current;
    if (!map) return;

    const latitude = Number(vesselLocation?.latitude);
    const longitude = Number(vesselLocation?.longitude);

    const validPosition =
      vesselLocation?.latitude != null &&
      vesselLocation?.longitude != null &&
      Number.isFinite(latitude) &&
      Number.isFinite(longitude) &&
      latitude >= -90 &&
      latitude <= 90 &&
      longitude >= -180 &&
      longitude <= 180;

    if (!validPosition) {
      vesselMarkerRef.current?.remove();
      vesselMarkerRef.current = null;
      return;
    }

    if (!vesselMarkerRef.current) {
      const markerElement = document.createElement("div");
      markerElement.className = "passenger-vessel-marker";
      markerElement.innerHTML = `
        <div class="passenger-vessel-marker__pulse"></div>
        <div class="passenger-vessel-marker__icon">🚢</div>
      `;

      vesselMarkerRef.current = new mapboxgl.Marker({
        element: markerElement,
        anchor: "center",
      })
        .setLngLat([longitude, latitude])
        .addTo(map);
    } else {
      vesselMarkerRef.current.setLngLat([longitude, latitude]);
    }
  }, [vesselLocation]);

  return (
    <div className="trip-route-map">
      <div ref={mapContainerRef} className="trip-route-map__canvas" />

      {loading && (
        <div className="trip-route-map__overlay">
          Đang tải dữ liệu tuyến đường...
        </div>
      )}

      {mapError && <div className="trip-route-map__error">{mapError}</div>}

      {!routeGeoJson && !loading && (
        <div className="trip-route-map__empty">
          Chưa có dữ liệu bản đồ tuyến đường.
        </div>
      )}
    </div>
  );
}
