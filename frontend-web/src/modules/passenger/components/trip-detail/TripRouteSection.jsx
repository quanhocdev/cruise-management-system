// src/modules/passenger/components/trip-detail/TripRouteSection.jsx
import React, { useMemo, useState } from "react";
import { MapPin, Route, RefreshCw } from "lucide-react";
import "./TripRouteSection.css";
import TripRouteMap from "./TripRouteMap";

const formatDistance = (value) => {
  if (value == null || !Number.isFinite(Number(value))) {
    return "Chưa cập nhật";
  }

  return `${Number(value).toLocaleString("vi-VN", {
    maximumFractionDigits: 2,
  })} km`;
};

export default function TripRouteSection({
  overview,
  legs = [],
  vesselLocation,
  loading = false,
  error = "",
  onReload,
}) {
  const [selectedLegId, setSelectedLegId] = useState(null);

  const selectedLeg = useMemo(
    () => legs.find((leg) => leg.legId === selectedLegId) || null,
    [legs, selectedLegId],
  );

  const activeGeoJson =
    selectedLeg?.routeGeoJson || overview?.routeGeoJson || null;

  const handleSelectLeg = (leg) => {
    setSelectedLegId((current) => (current === leg.legId ? null : leg.legId));
  };

  return (
    <section className="trip-detail-section trip-route-section">
      <div className="trip-detail-section__header">
        <div>
          <span className="trip-detail-section__eyebrow">HÀNH TRÌNH</span>
          <h2>Tuyến đường chuyến đi</h2>
          <p>Theo dõi lộ trình và các chặng di chuyển của tàu.</p>
        </div>

        <button
          type="button"
          className="trip-detail-section__reload"
          onClick={onReload}
          disabled={loading}
        >
          <RefreshCw size={16} />
          Làm mới
        </button>
      </div>

      {error && <div className="trip-detail-error">{error}</div>}

      <div className="trip-route-section__summary">
        <div className="trip-route-section__port">
          <MapPin size={19} />
          <div>
            <span>Cảng khởi hành</span>
            <strong>{overview?.departurePortName || "Chưa cập nhật"}</strong>
          </div>
        </div>

        <div className="trip-route-section__distance">
          <Route size={19} />
          <div>
            <span>Tổng khoảng cách</span>
            <strong>{formatDistance(overview?.distanceKm)}</strong>
          </div>
        </div>

        <div className="trip-route-section__port">
          <MapPin size={19} />
          <div>
            <span>Cảng đích</span>
            <strong>{overview?.destinationPortName || "Chưa cập nhật"}</strong>
          </div>
        </div>
      </div>

      <TripRouteMap
        routeGeoJson={activeGeoJson}
        vesselLocation={vesselLocation}
        loading={loading}
      />

      {vesselLocation && (
        <div className="trip-route-section__gps">
          <span className="trip-route-section__gps-dot" />
          <span>Đã nhận dữ liệu vị trí tàu</span>
          {vesselLocation.updatedAt && (
            <time>
              {new Date(vesselLocation.updatedAt).toLocaleString("vi-VN")}
            </time>
          )}
        </div>
      )}

      {!vesselLocation && (
        <div className="trip-route-section__gps trip-route-section__gps--muted">
          Chưa có dữ liệu vị trí GPS của tàu.
        </div>
      )}

      <div className="trip-route-legs">
        <div className="trip-route-legs__header">
          <h3>Các chặng hành trình</h3>
          <span>{legs.length} chặng</span>
        </div>

        {legs.length === 0 ? (
          <div className="trip-route-legs__empty">
            Chưa có thông tin chi tiết các chặng.
          </div>
        ) : (
          <div className="trip-route-legs__list">
            {legs.map((leg, index) => {
              const selected = selectedLegId === leg.legId;

              return (
                <button
                  type="button"
                  key={leg.legId}
                  className={`trip-route-leg ${
                    selected ? "trip-route-leg--active" : ""
                  }`}
                  onClick={() => handleSelectLeg(leg)}
                >
                  <span className="trip-route-leg__order">
                    {leg.legOrder ?? index + 1}
                  </span>

                  <span className="trip-route-leg__content">
                    <strong>
                      {leg.fromPortName || "Chưa xác định"}
                      <span> → </span>
                      {leg.toPortName || "Chưa xác định"}
                    </strong>
                    <small>{formatDistance(leg.distanceKm)}</small>
                  </span>

                  <span className="trip-route-leg__action">
                    {selected ? "Bỏ chọn" : "Xem chặng"}
                  </span>
                </button>
              );
            })}
          </div>
        )}
      </div>
    </section>
  );
}
