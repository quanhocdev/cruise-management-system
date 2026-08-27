import { useState } from "react";
import { ClipboardList, Eye, Settings } from "lucide-react";
import "../styles/ShoreTourTable.css";

import ShoreTourDetailModal from "./ShoreTourDetailModal"; // Modal chi tiết khi click con mắt

function ShoreTourTable({ visitTours, onConfigure }) {
  const [viewDetail, setViewDetail] = useState(null); // State xem chi tiết

  const formatShortId = (str, maxLength = 10) => {
    if (!str) return "—";
    const value = String(str);
    if (value.length <= maxLength) return value;
    return `${value.substring(0, maxLength)}...`;
  };

  if (!visitTours || visitTours.length === 0) {
    return (
      <div className="shore-manager-tour-empty">
        <ClipboardList size={42} />

        <h2>Chưa có Visit Tour</h2>

        <p>Hiện tại chưa có Visit Tour nào trong hệ thống.</p>
      </div>
    );
  }

  const getStatusLabel = (status) => {
    switch (status) {
      case "WAITING_CONFIG":
        return "Chờ cấu hình";

      case "CONFIGURED":
        return "Đã cấu hình";

      default:
        return status || "Không xác định";
    }
  };

  const getStatusClass = (status) => {
    switch (status) {
      case "WAITING_CONFIG":
        return "waiting";

      case "CONFIGURED":
        return "configured";

      default:
        return "";
    }
  };

  return (
    <div className="shore-tour-table-wrapper">
      <table className="shore-tour-table">
        <thead>
          <tr>
            <th>Tour ID</th>
            <th>Schedule Stop ID</th>
            <th>Tên Visit Tour</th>
            <th>Trạng thái</th>
            <th>Thời gian</th>
            <th>Giá</th>
            <th></th>
          </tr>
        </thead>

        <tbody>
          {visitTours.map((visitTour) => (
            <tr key={visitTour.id}>
              {/* TOUR ID */}
              <td>
                <div className="shore-tour-table-id-cell">
                  <strong className="font-mono" title={visitTour.tourId || ""}>
                    {formatShortId(visitTour.tourId)}
                  </strong>
                  <button
                    type="button"
                    className="shore-tour-table-view-btn"
                    onClick={() => setViewDetail(visitTour)}
                    title="Xem chi tiết"
                  >
                    <Eye size={14} />
                  </button>
                </div>
              </td>

              {/* SCHEDULE STOP ID */}
              <td>
                <div className="shore-tour-table-id-cell">
                  <span
                    className="shore-tour-table-id"
                    title={visitTour.scheduleStopId || ""}
                  >
                    {formatShortId(visitTour.scheduleStopId)}
                  </span>
                  <button
                    type="button"
                    className="shore-tour-table-view-btn"
                    onClick={() => setViewDetail(visitTour)}
                    title="Xem chi tiết"
                  >
                    <Eye size={14} />
                  </button>
                </div>
              </td>

              <td>
                <span>{visitTour.name || "Chưa cấu hình"}</span>
              </td>

              <td>
                <span
                  className={`shore-tour-table-status ${getStatusClass(
                    visitTour.status,
                  )}`}
                >
                  {getStatusLabel(visitTour.status)}
                </span>
              </td>

              <td>
                {visitTour.startTime && visitTour.endTime
                  ? `${visitTour.startTime} → ${visitTour.endTime}`
                  : "—"}
              </td>

              <td>
                {visitTour.price != null
                  ? `${Number(visitTour.price).toLocaleString("vi-VN")} ₫`
                  : "—"}
              </td>

              <td>
                <button
                  type="button"
                  className="shore-tour-table-action"
                  onClick={() =>
                    onConfigure(visitTour.tourId, visitTour.scheduleStopId)
                  }
                  title="Xem / cấu hình Visit Tour"
                >
                  <Settings size={17} />
                  <span>Cấu hình</span>
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {/* MODAL XEM CHI TIẾT (EYE ICON) */}
      {viewDetail && (
        <ShoreTourDetailModal
          visitTour={viewDetail}
          onClose={() => setViewDetail(null)}
        />
      )}
    </div>
  );
}

export default ShoreTourTable;
