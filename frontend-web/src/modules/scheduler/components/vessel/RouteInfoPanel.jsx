import { Anchor } from "lucide-react";
import "../../styles/RouteInfoPanel.css";

export default function RouteInfoPanel({
  overview,
  legs,
  selectedLegId,
  onSelectOverview,
  onSelectLeg,
}) {
  if (!overview) return null;

  return (
    <div className="route-info-card">
      <div className="route-info-header">
        <h3 className="route-info-title">
          <Anchor size={18} color="#2563eb" /> Thông tin Chi tiết Lộ trình
        </h3>

        <button
          onClick={onSelectOverview}
          style={{
            padding: "6px 14px",
            borderRadius: "8px",
            border: "1px solid #cbd5e1",
            background: !selectedLegId ? "#2563eb" : "#f8fafc",
            color: !selectedLegId ? "#fff" : "#475569",
            fontWeight: "600",
            fontSize: "12px",
            cursor: "pointer",
          }}
        >
          Tổng quan toàn tuyến
        </button>
      </div>

      {/* Hiển thị các trường của TourRouteOverviewResponse */}
      <div className="route-overview-grid">
        <div className="route-overview-item">
          <span className="label">Cảng Khởi Hành:</span>
          <strong>{overview.departurePortName}</strong>
          <span className="sub">ID: {overview.departurePortId}</span>
        </div>
        <div className="route-overview-item">
          <span className="label">Cảng Đích Đến:</span>
          <strong>{overview.destinationPortName}</strong>
          <span className="sub">ID: {overview.destinationPortId}</span>
        </div>
        <div className="route-overview-item">
          <span className="label">Tổng Khoảng Cách:</span>
          <strong style={{ color: "#2563eb", fontSize: "14px" }}>
            {overview.distanceKm?.toFixed(2)} km
          </strong>
        </div>
        <div className="route-overview-item">
          <span className="label">Cập Nhật Lần Cuối:</span>
          <span style={{ fontSize: "12px", color: "#334155" }}>
            {overview.updatedAt
              ? new Date(overview.updatedAt).toLocaleString("vi-VN")
              : "N/A"}
          </span>
        </div>
      </div>

      {/* Danh sách các chặng (Hứng từ List<TourRouteLegResponse>) */}
      {legs && legs.length > 0 && (
        <div>
          <span className="route-legs-section-title">
            Danh sách các chặng chi tiết ({legs.length} chặng):
          </span>
          <div className="route-legs-list">
            {legs.map((leg) => {
              const isSelected = selectedLegId === leg.legId;
              return (
                <div
                  key={leg.legId}
                  onClick={() => onSelectLeg(leg)}
                  className={`route-leg-item ${isSelected ? "selected" : ""}`}
                >
                  <div>
                    <div className="route-leg-title">
                      Chặng {leg.legOrder}: {leg.fromPortName} ➔{" "}
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
                    <div className="route-leg-date">
                      {leg.updatedAt
                        ? new Date(leg.updatedAt.toString()).toLocaleDateString(
                            "vi-VN",
                          )
                        : ""}
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
