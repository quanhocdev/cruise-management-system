// src/modules/admin/components/PortMap.jsx

import { useEffect, useRef, useState } from "react";
import mapboxgl from "mapbox-gl";
import "mapbox-gl/dist/mapbox-gl.css";

mapboxgl.accessToken = import.meta.env.VITE_MAPBOX_PUBLIC_TOKEN;

const DEFAULT_CENTER = [106.6297, 10.8231]; // Ho Chi Minh City
const DEFAULT_ZOOM = 10;

export default function PortMap({ latitude, longitude, onLocationChange }) {
  const mapContainerRef = useRef(null);
  const mapRef = useRef(null);
  const markerRef = useRef(null);

  const [searchText, setSearchText] = useState("");
  const [searching, setSearching] = useState(false);
  const [searchError, setSearchError] = useState("");

  /*
   * =====================================================
   * CREATE MAP
   * =====================================================
   */
  useEffect(() => {
    if (!mapContainerRef.current) {
      return;
    }

    if (mapRef.current) {
      return;
    }

    const hasInitialLocation =
      latitude !== null &&
      longitude !== null &&
      latitude !== undefined &&
      longitude !== undefined &&
      !Number.isNaN(Number(latitude)) &&
      !Number.isNaN(Number(longitude));

    const initialCenter = hasInitialLocation
      ? [Number(longitude), Number(latitude)]
      : DEFAULT_CENTER;

    const initialZoom = hasInitialLocation ? 14 : DEFAULT_ZOOM;

    const map = new mapboxgl.Map({
      container: mapContainerRef.current,
      style: "mapbox://styles/mapbox/streets-v12",
      center: initialCenter,
      zoom: initialZoom,
    });

    map.addControl(new mapboxgl.NavigationControl(), "top-right");

    map.on("click", (event) => {
      const { lng, lat } = event.lngLat;

      setMarkerPosition(lat, lng);
    });

    mapRef.current = map;

    return () => {
      if (markerRef.current) {
        markerRef.current.remove();
        markerRef.current = null;
      }

      map.remove();
      mapRef.current = null;
    };
  }, []);

  /*
   * =====================================================
   * SET MARKER
   * =====================================================
   */
  const setMarkerPosition = (newLatitude, newLongitude) => {
    if (!mapRef.current) {
      return;
    }

    const lat = Number(newLatitude);
    const lng = Number(newLongitude);

    if (Number.isNaN(lat) || Number.isNaN(lng)) {
      return;
    }

    if (lat < -90 || lat > 90) {
      return;
    }

    if (lng < -180 || lng > 180) {
      return;
    }

    if (!markerRef.current) {
      const marker = new mapboxgl.Marker({
        draggable: true,
      })
        .setLngLat([lng, lat])
        .addTo(mapRef.current);

      marker.on("dragend", () => {
        const position = marker.getLngLat();

        onLocationChange({
          latitude: position.lat,
          longitude: position.lng,
        });
      });

      markerRef.current = marker;
    } else {
      markerRef.current.setLngLat([lng, lat]);
    }

    onLocationChange({
      latitude: lat,
      longitude: lng,
    });
  };

  /*
   * =====================================================
   * SYNC MARKER WHEN LAT/LNG CHANGE
   * =====================================================
   */
  useEffect(() => {
    if (!mapRef.current) {
      return;
    }

    const hasLocation =
      latitude !== null &&
      longitude !== null &&
      latitude !== undefined &&
      longitude !== undefined &&
      latitude !== "" &&
      longitude !== "" &&
      !Number.isNaN(Number(latitude)) &&
      !Number.isNaN(Number(longitude));

    if (!hasLocation) {
      return;
    }

    const lat = Number(latitude);
    const lng = Number(longitude);

    if (lat < -90 || lat > 90 || lng < -180 || lng > 180) {
      return;
    }

    if (!markerRef.current) {
      const marker = new mapboxgl.Marker({
        draggable: true,
      })
        .setLngLat([lng, lat])
        .addTo(mapRef.current);

      marker.on("dragend", () => {
        const position = marker.getLngLat();

        onLocationChange({
          latitude: position.lat,
          longitude: position.lng,
        });
      });

      markerRef.current = marker;
    } else {
      markerRef.current.setLngLat([lng, lat]);
    }
  }, [latitude, longitude, onLocationChange]);

  /*
   * =====================================================
   * SEARCH ADDRESS
   * =====================================================
   */
  const handleSearch = async (event) => {
    event.preventDefault();

    const query = searchText.trim();

    if (!query) {
      setSearchError("Vui lòng nhập địa chỉ cần tìm.");
      return;
    }

    const token = import.meta.env.VITE_MAPBOX_PUBLIC_TOKEN;

    if (!token) {
      setSearchError("Chưa cấu hình Mapbox public token.");
      return;
    }

    setSearching(true);
    setSearchError("");

    try {
      const url =
        `https://api.mapbox.com/search/geocode/v6/forward` +
        `?q=${encodeURIComponent(query)}` +
        `&limit=1` +
        `&language=vi` +
        `&country=VN` +
        `&access_token=${encodeURIComponent(token)}`;

      const response = await fetch(url);

      if (!response.ok) {
        throw new Error("Mapbox search request failed.");
      }

      const data = await response.json();

      if (!data.features || data.features.length === 0) {
        setSearchError("Không tìm thấy địa điểm phù hợp.");
        return;
      }

      const feature = data.features[0];

      const coordinates = feature.geometry?.coordinates;

      if (
        !coordinates ||
        coordinates.length < 2 ||
        Number.isNaN(Number(coordinates[0])) ||
        Number.isNaN(Number(coordinates[1]))
      ) {
        setSearchError("Không lấy được tọa độ từ kết quả tìm kiếm.");
        return;
      }

      const [lng, lat] = coordinates;

      if (!mapRef.current) {
        return;
      }

      mapRef.current.flyTo({
        center: [lng, lat],
        zoom: 15,
        essential: true,
      });

      setMarkerPosition(lat, lng);
    } catch (error) {
      console.error("Mapbox search error:", error);

      setSearchError("Không thể tìm kiếm địa điểm. Vui lòng thử lại.");
    } finally {
      setSearching(false);
    }
  };

  /*
   * =====================================================
   * RENDER
   * =====================================================
   */
  return (
    <div>
      {/* SEARCH */}
      <form onSubmit={handleSearch} className="d-flex gap-2 mb-2">
        <input
          type="text"
          className="form-control"
          placeholder="Tìm kiếm địa chỉ, cảng, thành phố..."
          value={searchText}
          onChange={(event) => {
            setSearchText(event.target.value);
            setSearchError("");
          }}
          disabled={searching}
        />

        <button type="submit" className="btn btn-primary" disabled={searching}>
          {searching ? "Đang tìm..." : "Tìm"}
        </button>
      </form>

      {/* SEARCH ERROR */}
      {searchError && (
        <div className="alert alert-danger py-2 mb-2">{searchError}</div>
      )}

      {/* MAP */}
      <div
        ref={mapContainerRef}
        style={{
          width: "100%",
          height: "500px",
          borderRadius: "8px",
          overflow: "hidden",
        }}
      />

      {/* COORDINATES */}
      <div className="mt-2 p-2 bg-light border rounded">
        <div>
          <strong>Latitude:</strong>{" "}
          {latitude !== null && latitude !== undefined && latitude !== ""
            ? Number(latitude).toFixed(7)
            : "Chưa chọn"}
        </div>

        <div>
          <strong>Longitude:</strong>{" "}
          {longitude !== null && longitude !== undefined && longitude !== ""
            ? Number(longitude).toFixed(7)
            : "Chưa chọn"}
        </div>

        <small className="text-muted">
          Bạn có thể click trực tiếp trên bản đồ hoặc kéo marker để điều chỉnh
          vị trí.
        </small>
      </div>
    </div>
  );
}
