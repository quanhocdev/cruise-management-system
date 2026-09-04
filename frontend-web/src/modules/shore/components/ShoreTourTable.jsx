// src/modules/shore/components/ShoreTourTable.jsx
import { useState } from "react";
import { ClipboardList, Eye, Settings } from "lucide-react";
import "../styles/ShoreTourTable.css";

import ShoreTourDetailModal from "./ShoreTourDetailModal";

function ShoreTourTable({
  visitTours,
  masterToursMap = new Map(),
  onConfigure,
}) {
  const [viewDetail, setViewDetail] = useState(null);

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
            <th>Tour</th>
            <th>Schedule Stop ID</th>
            <th>Tên Visit Tour</th>
            <th>Trạng thái</th>
            <th>Thời gian</th>
            <th>Giá</th>
            <th></th>
          </tr>
        </thead>

        <tbody>
          {visitTours.map((visitTour) => {
            const masterTour = masterToursMap.get(visitTour.tourId);
            const displayTourName =
              masterTour?.name || formatShortId(visitTour.tourId);

            return (
              <tr key={visitTour.id}>
                {/* TOUR NAME / ID */}
                <td>
                  <div className="shore-tour-table-id-cell">
                    <strong
                      className="font-mono"
                      title={
                        masterTour?.name
                          ? `${masterTour.name} (${visitTour.tourId})`
                          : visitTour.tourId
                      }
                    >
                      {displayTourName}
                    </strong>
                    <button
                      type="button"
                      className="shore-tour-table-view-btn"
                      onClick={() =>
                        setViewDetail({ ...visitTour, masterTour })
                      }
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
                      onClick={() =>
                        setViewDetail({ ...visitTour, masterTour })
                      }
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
            );
          })}
        </tbody>
      </table>

      {/* MODAL XEM CHI TIẾT (EYE ICON) */}
      {viewDetail && (
        <ShoreTourDetailModal
          visitTour={viewDetail}
          masterTour={viewDetail.masterTour}
          onClose={() => setViewDetail(null)}
        />
      )}
    </div>
  );
}

export default ShoreTourTable;
