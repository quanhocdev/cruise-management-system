// src/modules/admin/components/port/PortMap.jsx

import { useEffect, useRef, useState } from "react";

import mapboxgl from "mapbox-gl";
import "mapbox-gl/dist/mapbox-gl.css";

const MAPBOX_TOKEN = import.meta.env.VITE_MAPBOX_PUBLIC_TOKEN;

mapboxgl.accessToken = MAPBOX_TOKEN;

export default function PortMap({
  ports = [],
  selectedLocation,
  onMapLocationSelect,
  onEditPort,
}) {
  const mapContainerRef = useRef(null);
  const mapRef = useRef(null);

  const markersRef = useRef(new Map());
  const temporaryMarkerRef = useRef(null);

  const [mapReady, setMapReady] = useState(false);

  // =====================================================
  // INITIALIZE MAP
  // =====================================================

  useEffect(() => {
    if (!mapContainerRef.current) {
      return;
    }

    if (mapRef.current) {
      return;
    }

    if (!MAPBOX_TOKEN) {
      console.error("❌ VITE_MAPBOX_PUBLIC_TOKEN chưa được cấu hình.");

      return;
    }

    const map = new mapboxgl.Map({
      container: mapContainerRef.current,
      style: "mapbox://styles/mapbox/streets-v12",

      // 965 Quang Trung, Gò Vấp, TP.HCM
      center: [106.6687, 10.8386],

      // Zoom vào khu vực thành phố
      zoom: 13,

      // Nghiêng nhẹ
      pitch: 35,

      // Hướng nhìn
      bearing: 0,
    });

    map.addControl(new mapboxgl.NavigationControl(), "top-right");

    map.addControl(new mapboxgl.FullscreenControl(), "top-right");

    map.on("load", () => {
      setMapReady(true);
    });

    map.on("click", (event) => {
      const { lng, lat } = event.lngLat;

      onMapLocationSelect?.({
        latitude: lat,
        longitude: lng,
        placeName: "",
      });
    });

    mapRef.current = map;

    return () => {
      markersRef.current.forEach((marker) => {
        marker.remove();
      });

      markersRef.current.clear();

      if (temporaryMarkerRef.current) {
        temporaryMarkerRef.current.remove();
        temporaryMarkerRef.current = null;
      }

      map.remove();

      mapRef.current = null;
    };
  }, [onMapLocationSelect]);

  // =====================================================
  // RENDER PORT MARKERS
  // =====================================================

  useEffect(() => {
    if (!mapReady || !mapRef.current) {
      return;
    }

    const map = mapRef.current;

    const activePortIds = new Set(
      ports
        .filter((port) => port.latitude != null && port.longitude != null)
        .map((port) => String(port.id)),
    );

    // Remove old markers
    markersRef.current.forEach((marker, id) => {
      if (!activePortIds.has(id)) {
        marker.remove();
        markersRef.current.delete(id);
      }
    });

    // Create/update markers
    ports.forEach((port) => {
      if (port.latitude == null || port.longitude == null) {
        return;
      }

      const id = String(port.id);

      const existingMarker = markersRef.current.get(id);

      if (existingMarker) {
        existingMarker.setLngLat([
          Number(port.longitude),
          Number(port.latitude),
        ]);

        return;
      }

      const markerElement = document.createElement("div");

      markerElement.className = "port-map-marker";

      markerElement.innerHTML = `
        <div class="port-map-marker-pin">
          <span>⚓</span>
        </div>
      `;

      const popup = new mapboxgl.Popup({
        offset: 25,
        closeButton: true,
        closeOnClick: false,
      }).setHTML(`
        <div class="port-map-popup">
          <div class="port-map-popup-title">
            ${escapeHtml(port.name || "Cảng")}
          </div>

          <div class="port-map-popup-address">
            ${escapeHtml(
              port.address || `${port.city || ""}, ${port.country || ""}`,
            )}
          </div>

          <div class="port-map-popup-coordinate">
            ${Number(port.latitude).toFixed(6)},
            ${Number(port.longitude).toFixed(6)}
          </div>

          <div class="port-map-popup-status ${
            port.status === "ACTIVE" ? "active" : "inactive"
          }">
            ${port.status === "ACTIVE" ? "Đang hoạt động" : "Ngừng hoạt động"}
          </div>

          <button
            type="button"
            class="port-map-popup-edit"
            data-port-id="${escapeHtml(id)}"
          >
            Chỉnh sửa
          </button>
        </div>
      `);

      popup.on("open", () => {
        const button = popup
          .getElement()
          ?.querySelector(".port-map-popup-edit");

        if (button) {
          button.addEventListener("click", () => {
            onEditPort?.(port);
          });
        }
      });

      const marker = new mapboxgl.Marker({
        element: markerElement,
        anchor: "bottom",
      })
        .setLngLat([Number(port.longitude), Number(port.latitude)])
        .setPopup(popup)
        .addTo(map);

      markersRef.current.set(id, marker);
    });
  }, [ports, mapReady, onEditPort]);

  // =====================================================
  // SELECTED LOCATION
  // =====================================================

  useEffect(() => {
    if (!mapReady || !mapRef.current || !selectedLocation) {
      return;
    }

    const latitude = Number(selectedLocation.latitude);

    const longitude = Number(selectedLocation.longitude);

    if (Number.isNaN(latitude) || Number.isNaN(longitude)) {
      return;
    }

    const map = mapRef.current;

    map.flyTo({
      center: [longitude, latitude],
      zoom: 14,
      duration: 900,
      essential: true,
    });

    if (temporaryMarkerRef.current) {
      temporaryMarkerRef.current.remove();
    }

    const element = document.createElement("div");

    element.className = "port-map-temporary-marker";

    element.innerHTML = `
      <div class="port-map-temporary-pin">
        +
      </div>
    `;

    temporaryMarkerRef.current = new mapboxgl.Marker({
      element,
      anchor: "bottom",
    })
      .setLngLat([longitude, latitude])
      .addTo(map);
  }, [selectedLocation, mapReady]);

  return (
    <div className="port-map-container">
      <div ref={mapContainerRef} className="port-map" />

      {!MAPBOX_TOKEN && (
        <div className="port-map-config-error">
          Chưa cấu hình VITE_MAPBOX_PUBLIC_TOKEN
        </div>
      )}

      <div className="port-map-hint">
        <span>💡</span>
        <span>
          Nhập địa chỉ để tìm kiếm hoặc click trực tiếp trên bản đồ để chọn tọa
          độ.
        </span>
      </div>
    </div>
  );
}

// =====================================================
// ESCAPE HTML
// =====================================================

function escapeHtml(value) {
  return String(value ?? "")
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#039;");
}
