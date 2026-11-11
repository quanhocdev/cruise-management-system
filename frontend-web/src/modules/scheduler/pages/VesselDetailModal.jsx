// src/modules/scheduler/components/VesselDetailModal.jsx
import React from "react";
import {
  X,
  Anchor,
  MapPin,
  Navigation,
  CloudSun,
  RefreshCw,
} from "lucide-react";
import useVesselDetail from "../hooks/useVesselDetail";
import useWeatherForecast from "../hooks/useWeatherForecast";
import "../styles/VesselDetail.css";

export default function VesselDetailModal({ tourId, onClose }) {
  const { tourDetails, loading, error, reload } = useVesselDetail(tourId);

  // Lấy tọa độ từ tàu (Redis) hoặc cảng đi
  const lat =
    tourDetails?.currentPosition?.latitude ||
    tourDetails?.departurePort?.latitude;
  const lon =
    tourDetails?.currentPosition?.longitude ||
    tourDetails?.departurePort?.longitude;

  // Sử dụng hook thời tiết nhưng kích hoạt thủ công qua nút bấm hoặc state
  const [fetchWeatherEnabled, setFetchWeatherEnabled] = React.useState(false);
  const {
    weather,
    loading: loadingWeather,
    reloadWeather,
  } = useWeatherForecast(
    fetchWeatherEnabled ? lat : null,
    fetchWeatherEnabled ? lon : null,
    tourDetails?.startDate,
    tourDetails?.endDate,
  );

  const handleGetWeather = () => {
    setFetchWeatherEnabled(true);
    if (fetchWeatherEnabled) {
      reloadWeather();
    }
  };

  return (
    <div className="vessel-modal-overlay">
      <div className="vessel-modal-content">
        <div className="vessel-modal-header">
          <div>
            <span className="modal-tour-code">
              {tourDetails?.code || "Đang tải..."}
            </span>
            <h2>{tourDetails?.name || "Chi tiết giám sát tour"}</h2>
          </div>
          <button className="close-btn" onClick={onClose}>
            <X size={20} />
          </button>
        </div>

        {loading ? (
          <div className="modal-loading">Đang tải dữ liệu chi tiết...</div>
        ) : error ? (
          <div className="modal-error">{error}</div>
        ) : tourDetails ? (
          <div className="modal-body">
            {/* Vị trí GPS Real-time từ Redis */}
            {tourDetails.currentPosition && (
              <div className="gps-live-banner">
                <Navigation size={18} className="pulse-icon" />
                <div>
                  <strong>Tọa độ GPS Tàu (Redis Real-time):</strong>{" "}
                  {tourDetails.currentPosition.latitude.toFixed(4)},{" "}
                  {tourDetails.currentPosition.longitude.toFixed(4)}
                  <span className="gps-time">
                    Cập nhật:{" "}
                    {new Date(
                      tourDetails.currentPosition.updatedAt,
                    ).toLocaleTimeString("vi-VN")}
                  </span>
                </div>
              </div>
            )}

            {/* THANH TIẾN TRÌNH NGANG (ROUTE PROGRESS BAR) */}
            <div className="route-progress-section">
              <h4>
                <Anchor size={16} /> Hành trình Tuyến đường (Cảng Đi ➔ Cảng Đến)
              </h4>

              <div className="progress-bar-container">
                <div className="progress-line" />

                {/* Cảng đi */}
                <div className="progress-node departure">
                  <div className="node-icon">
                    <Anchor size={16} />
                  </div>
                  <div className="node-label">
                    <strong>{tourDetails.departurePort?.name}</strong>
                    <span>{tourDetails.departurePort?.city}</span>
                  </div>
                </div>

                {/* Các điểm dừng chân */}
                {tourDetails.schedules
                  ?.flatMap((s) => s.stops || [])
                  .map((stop, idx) => (
                    <div
                      key={stop.stopId || idx}
                      className="progress-node stop"
                    >
                      <div className="node-icon">
                        {stop.stopOrder || idx + 1}
                      </div>
                      <div className="node-label">
                        <strong>{stop.port?.name}</strong>
                        <span>
                          {stop.arriveAt
                            ? new Date(stop.arriveAt).toLocaleDateString(
                                "vi-VN",
                                { day: "2-digit", month: "2-digit" },
                              )
                            : ""}
                        </span>
                      </div>
                    </div>
                  ))}

                {/* Cảng đến */}
                <div className="progress-node arrival">
                  <div className="node-icon">
                    <MapPin size={16} />
                  </div>
                  <div className="node-label">
                    <strong>{tourDetails.arrivalPort?.name}</strong>
                    <span>{tourDetails.arrivalPort?.city}</span>
                  </div>
                </div>
              </div>
            </div>

            {/* PHẦN THỜI TIẾT VỚI NÚT BẤM */}
            <div className="weather-section">
              <div className="weather-header">
                <h4>
                  <CloudSun size={18} /> Dự báo thời tiết tuyến đường
                  (Open-Meteo)
                </h4>
                <button
                  className="get-weather-btn"
                  onClick={handleGetWeather}
                  disabled={loadingWeather}
                >
                  <RefreshCw
                    size={14}
                    className={loadingWeather ? "spin" : ""}
                  />
                  <span>
                    {weather ? "Cập nhật thời tiết" : "Xem thời tiết khu vực"}
                  </span>
                </button>
              </div>

              {loadingWeather && (
                <div className="weather-loading">
                  Đang tải dữ liệu thời tiết...
                </div>
              )}

              {weather && !loadingWeather && (
                <div className="weather-result-card">
                  <div className="weather-item">
                    <span>Nhiệt độ trung bình:</span>
                    <strong>
                      {weather.temperature ??
                        weather.current?.temperature ??
                        "N/A"}
                      °C
                    </strong>
                  </div>
                  <div className="weather-item">
                    <span>Trạng thái:</span>
                    <strong>
                      {weather.condition || weather.summary || "Bình thường"}
                    </strong>
                  </div>
                  <div className="weather-item">
                    <span>Độ ẩm:</span>
                    <strong>
                      {weather.humidity ? `${weather.humidity}%` : "N/A"}
                    </strong>
                  </div>
                </div>
              )}
            </div>
          </div>
        ) : null}
      </div>
    </div>
  );
}
