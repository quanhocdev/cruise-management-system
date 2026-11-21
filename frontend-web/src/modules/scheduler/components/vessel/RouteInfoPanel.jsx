// src/modules/scheduler/components/vessel/RouteInfoPanel.jsx
import { Anchor, Calendar, MapPin, Navigation } from "lucide-react";
import "../../styles/RouteInfoPanel.css";

export default function RouteInfoPanel({
  overview,
  legs,
  selectedLegId,
  onSelectOverview,
  onSelectLeg,
  tourStartDate, // Truyền thêm ngày bắt đầu tour từ parent nếu có
  tourEndDate, // Truyền thêm ngày kết thúc tour từ parent nếu có
}) {
  if (!overview) return null;

  return (
    <div className="route-info-card">
      <div className="route-info-header">
        <h3 className="route-info-title">
          <Anchor size={18} color="#2563eb" /> Thông tin Chi tiết Lộ trình Hành
          trình
        </h3>

        <button
          onClick={onSelectOverview}
          className={`route-overview-btn ${!selectedLegId ? "active" : ""}`}
        >
          Tổng quan toàn tuyến
        </button>
      </div>

      {/* Hiển thị các trường của TourRouteOverviewResponse kèm Ngày tháng nổi bật */}
      <div className="route-overview-grid">
        <div className="route-overview-item departure">
          <span className="label">
            <MapPin size={13} color="#16a34a" /> Cảng Khởi Hành (Bắt đầu):
          </span>
          <strong className="port-name">{overview.departurePortName}</strong>
          {/* Badge ngày bắt đầu tour */}
          <div className="port-date-badge start">
            <Calendar size={12} /> Ngày đi:{" "}
            {tourStartDate
              ? new Date(tourStartDate).toLocaleDateString("vi-VN")
              : "Cập nhật theo lịch"}
          </div>
          <span className="sub">ID: {overview.departurePortId}</span>
        </div>

        <div className="route-overview-item destination">
          <span className="label">
            <MapPin size={13} color="#dc2626" /> Cảng Đích Đến (Kết thúc):
          </span>
          <strong className="port-name">{overview.destinationPortName}</strong>
          {/* Badge ngày kết thúc tour */}
          <div className="port-date-badge end">
            <Calendar size={12} /> Ngày về:{" "}
            {tourEndDate
              ? new Date(tourEndDate).toLocaleDateString("vi-VN")
              : "Cập nhật theo lịch"}
          </div>
          <span className="sub">ID: {overview.destinationPortId}</span>
        </div>

        <div className="route-overview-item summary-metric">
          <span className="label">Tổng Khoảng Cách Toàn Tuyến:</span>
          <strong className="metric-value">
            {overview.distanceKm?.toFixed(2)} km
          </strong>
          <span className="sub">
            Cập nhật lúc:{" "}
            {overview.updatedAt
              ? new Date(overview.updatedAt).toLocaleTimeString("vi-VN")
              : "N/A"}
          </span>
        </div>
      </div>

      {/* Danh sách các chặng và ngày tới cảng làm nổi bật phía trên */}
      {legs && legs.length > 0 && (
        <div className="route-legs-container">
          <span className="route-legs-section-title">
            Chi tiết các chặng hành trình & Thời gian ghé cảng ({legs.length}{" "}
            chặng):
          </span>
          <div className="route-legs-list">
            {legs.map((leg, index) => {
              const isSelected = selectedLegId === leg.legId;
              return (
                <div
                  key={leg.legId}
                  onClick={() => onSelectLeg(leg)}
                  className={`route-leg-item ${isSelected ? "selected" : ""}`}
                >
                  <div className="route-leg-left">
                    {/* Đưa mốc thời gian / Thứ tự chặng lên trên nổi bật */}
                    <div className="leg-badge-header">
                      <span className="leg-order-tag">
                        Chặng {leg.legOrder}
                      </span>
                      <span className="leg-date-highlight">
                        <Calendar size={12} /> Chặng kết nối tuyến {index + 1}
                      </span>
                    </div>
                    <div className="route-leg-title">
                      {leg.fromPortName} <span className="arrow">➔</span>{" "}
                      {leg.toPortName}
                    </div>
                    <div className="route-leg-subtitle">
                      ID Đi: {leg.fromPortId} | ID Đến: {leg.toPortId}
                    </div>
                  </div>
                  <div className="route-leg-right">
                    <div className="route-leg-distance">
                      {leg.distanceKm?.toFixed(2)} km
                    </div>
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      )}
    </div>
  );
}
