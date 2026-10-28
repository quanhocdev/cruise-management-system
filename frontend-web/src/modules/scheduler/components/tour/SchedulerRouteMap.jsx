import { useEffect, useRef, useState } from "react";
import mapboxgl from "mapbox-gl";
import "mapbox-gl/dist/mapbox-gl.css";

const MAPBOX_TOKEN = import.meta.env.VITE_MAPBOX_PUBLIC_TOKEN;

const ROUTE_SOURCE_ID = "scheduler-route-source";
const ROUTE_LAYER_ID = "scheduler-route-layer";

const DEFAULT_CENTER = [106.7009, 10.7769];

function getPortCoordinates(port) {
  const longitude = Number(port?.longitude);
  const latitude = Number(port?.latitude);

  if (
    !Number.isFinite(longitude) ||
    !Number.isFinite(latitude) ||
    longitude < -180 ||
    longitude > 180 ||
    latitude < -90 ||
    latitude > 90
  ) {
    return null;
  }

  return [longitude, latitude];
}

function getRouteFeature(routeGeoJson) {
  if (!routeGeoJson) return null;

  if (routeGeoJson.type === "Feature") {
    return routeGeoJson;
  }

  if (routeGeoJson.type === "FeatureCollection") {
    return routeGeoJson.features?.[0] ?? null;
  }

  if (
    routeGeoJson.type === "LineString" ||
    routeGeoJson.type === "MultiLineString"
  ) {
    return {
      type: "Feature",
      properties: {},
      geometry: routeGeoJson,
    };
  }

  return null;
}

function createMarkerElement(type) {
  const element = document.createElement("div");

  element.className = `scheduler-map-marker scheduler-map-marker-${type}`;

  element.style.width = "20px";
  element.style.height = "20px";
  element.style.borderRadius = "50%";
  element.style.border = "3px solid #ffffff";
  element.style.boxShadow = "0 2px 8px rgba(0,0,0,0.35)";
  element.style.cursor = "pointer";

  if (type === "departure") {
    element.style.backgroundColor = "#16a34a";
  } else if (type === "destination") {
    element.style.backgroundColor = "#dc2626";
  } else {
    element.style.backgroundColor = "#2563eb";
  }

  return element;
}

function getPortId(portId) {
  return portId == null ? null : String(portId);
}

function SchedulerRouteMap({
  ports = [],
  departurePortId = null,
  destinationPortId = null,
  routeGeoJson = null,
  onSelectPort,
}) {
  const mapContainerRef = useRef(null);
  const mapRef = useRef(null);
  const markersRef = useRef([]);
  const onSelectPortRef = useRef(onSelectPort);

  const [mapReady, setMapReady] = useState(false);

  useEffect(() => {
    onSelectPortRef.current = onSelectPort;
  }, [onSelectPort]);

  // =====================================================
  // INITIALIZE MAP
  // =====================================================

  useEffect(() => {
    if (!mapContainerRef.current || mapRef.current) return;

    if (!MAPBOX_TOKEN) {
      console.error(
        "Không tìm thấy VITE_MAPBOX_PUBLIC_TOKEN trong environment.",
      );
      return;
    }

    mapboxgl.accessToken = MAPBOX_TOKEN;

    const map = new mapboxgl.Map({
      container: mapContainerRef.current,
      style: "mapbox://styles/mapbox/streets-v12",
      center: DEFAULT_CENTER,
      zoom: 5,
      attributionControl: true,
    });

    mapRef.current = map;

    map.addControl(new mapboxgl.NavigationControl(), "top-right");
    map.addControl(new mapboxgl.FullscreenControl(), "top-right");

    const handleMapLoad = () => {
      if (!map.getSource(ROUTE_SOURCE_ID)) {
        map.addSource(ROUTE_SOURCE_ID, {
          type: "geojson",
          data: {
            type: "FeatureCollection",
            features: [],
          },
        });
      }

      if (!map.getLayer(ROUTE_LAYER_ID)) {
        map.addLayer({
          id: ROUTE_LAYER_ID,
          type: "line",
          source: ROUTE_SOURCE_ID,
          layout: {
            "line-join": "round",
            "line-cap": "round",
          },
          paint: {
            "line-color": "#2563eb",
            "line-width": 4,
            "line-opacity": 0.9,
          },
        });
      }

      setMapReady(true);
    };

    map.on("load", handleMapLoad);

    return () => {
      map.off("load", handleMapLoad);

      markersRef.current.forEach((marker) => marker.remove());
      markersRef.current = [];

      map.remove();

      mapRef.current = null;
      setMapReady(false);
    };
  }, []);

  // =====================================================
  // UPDATE PORT MARKERS
  // =====================================================

  useEffect(() => {
    const map = mapRef.current;

    if (!map || !mapReady) return;

    // Xóa marker cũ trước khi tạo marker mới.
    markersRef.current.forEach((marker) => marker.remove());
    markersRef.current = [];

    const departureId = getPortId(departurePortId);
    const destinationId = getPortId(destinationPortId);

    const validPorts = ports
      .map((port) => ({
        port,
        coordinates: getPortCoordinates(port),
      }))
      .filter((item) => item.coordinates !== null);

    validPorts.forEach(({ port, coordinates }) => {
      const currentPortId = getPortId(port.id);

      let markerType = "normal";

      if (currentPortId === departureId) {
        markerType = "departure";
      } else if (currentPortId === destinationId) {
        markerType = "destination";
      }

      const element = createMarkerElement(markerType);

      const popupContent = document.createElement("div");
      popupContent.style.minWidth = "180px";
      popupContent.style.padding = "4px";

      const name = document.createElement("strong");
      name.textContent = port.name || "Cảng chưa có tên";
      name.style.display = "block";
      name.style.marginBottom = "5px";

      const location = document.createElement("div");
      location.textContent = [port.city, port.country]
        .filter(Boolean)
        .join(", ");
      location.style.fontSize = "12px";
      location.style.color = "#64748b";

      const action = document.createElement("div");
      action.style.marginTop = "8px";
      action.style.fontSize = "12px";
      action.style.fontWeight = "600";

      if (currentPortId === departureId) {
        action.textContent = "Cảng khởi hành";
        action.style.color = "#16a34a";
      } else if (currentPortId === destinationId) {
        action.textContent = "Cảng điểm đến";
        action.style.color = "#dc2626";
      } else {
        action.textContent = "Nhấn để chọn cảng";
        action.style.color = "#2563eb";
      }

      popupContent.append(name, location, action);

      const popup = new mapboxgl.Popup({
        offset: 16,
        closeButton: true,
      }).setDOMContent(popupContent);

      const marker = new mapboxgl.Marker({
        element,
        anchor: "center",
      })
        .setLngLat(coordinates)
        .setPopup(popup)
        .addTo(map);

      element.addEventListener("click", () => {
        onSelectPortRef.current?.(port);
      });

      markersRef.current.push(marker);
    });

    // Ưu tiên đưa cảng đang chọn đến vào giữa bản đồ.
    const selectedPort =
      ports.find((port) => getPortId(port.id) === destinationId) ||
      ports.find((port) => getPortId(port.id) === departureId);

    const selectedCoordinates = getPortCoordinates(selectedPort);

    if (selectedCoordinates) {
      map.flyTo({
        center: selectedCoordinates,
        zoom: Math.max(map.getZoom(), 5),
        duration: 800,
      });
    } else if (validPorts.length > 0) {
      const bounds = new mapboxgl.LngLatBounds();

      validPorts.forEach(({ coordinates }) => {
        bounds.extend(coordinates);
      });

      map.fitBounds(bounds, {
        padding: 60,
        maxZoom: 7,
        duration: 800,
      });
    }
  }, [mapReady, ports, departurePortId, destinationPortId]);

  // =====================================================
  // UPDATE SEA ROUTE
  // =====================================================

  useEffect(() => {
    const map = mapRef.current;

    if (!map || !mapReady) return;

    const source = map.getSource(ROUTE_SOURCE_ID);

    if (!source) return;

    const feature = getRouteFeature(routeGeoJson);

    source.setData({
      type: "FeatureCollection",
      features: feature ? [feature] : [],
    });

    if (!feature?.geometry?.coordinates) return;

    const bounds = new mapboxgl.LngLatBounds();

    const addCoordinates = (coordinates) => {
      if (
        Array.isArray(coordinates) &&
        typeof coordinates[0] === "number" &&
        typeof coordinates[1] === "number"
      ) {
        bounds.extend(coordinates);
        return;
      }

      if (Array.isArray(coordinates)) {
        coordinates.forEach(addCoordinates);
      }
    };

    addCoordinates(feature.geometry.coordinates);

    if (!bounds.isEmpty()) {
      map.fitBounds(bounds, {
        padding: {
          top: 70,
          bottom: 70,
          left: 70,
          right: 70,
        },
        maxZoom: 8,
        duration: 1000,
      });
    }
  }, [mapReady, routeGeoJson]);

  return (
    <div className="scheduler-route-map-wrapper">
      <div
        ref={mapContainerRef}
        className="scheduler-route-map"
        style={{
          width: "100%",
          height: "100%",
          minHeight: "500px",
        }}
      />

      <div className="scheduler-route-map-legend">
        <div>
          <span className="scheduler-route-legend-dot departure" />
          <span>Cảng đi</span>
        </div>

        <div>
          <span className="scheduler-route-legend-dot destination" />
          <span>Cảng đến</span>
        </div>

        <div>
          <span className="scheduler-route-legend-dot normal" />
          <span>Cảng khác</span>
        </div>
      </div>
    </div>
  );
}

export default SchedulerRouteMap;
