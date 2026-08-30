// src/modules/convenience/components/tour-config/ConvenienceServiceTourDetail.jsx

import React, { useCallback, useEffect, useMemo, useState } from "react";
import { AlertCircle, Eye, RefreshCw, Wrench } from "lucide-react";
import "../../styles/history/ConvenienceServiceTourDetail.css";
import serviceTourService from "../../services/serviceTourService";
import ServiceTourDetailModal from "./ServiceTourDetailModal";

const STATUS_TABS = [
  { value: "ALL", label: "Tất cả" },
  { value: "WAITING_CONFIG", label: "Chờ cấu hình" },
  { value: "NOT_STARTED", label: "Đã cấu hình" },
  { value: "IN_PROGRESS", label: "Đang phục vụ" },
  { value: "COMPLETED", label: "Đã kết thúc" },
];

const ConvenienceServiceTourDetail = () => {
  const [serviceTours, setServiceTours] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const [statusFilter, setStatusFilter] = useState("ALL");
  const [viewDetailAssignment, setViewDetailAssignment] = useState(null);

  const loadServiceTours = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);

      const data = await serviceTourService.getConfigurationHistory();

      setServiceTours(data || []);
    } catch (err) {
      console.error("LOAD SERVICE TOUR HISTORY ERROR:", err);

      setError(
        err.response?.data?.message ||
          "Không thể tải lịch sử cấu hình dịch vụ.",
      );
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadServiceTours();
  }, [loadServiceTours]);

  const filteredServiceTours = useMemo(() => {
    if (statusFilter === "ALL") {
      return serviceTours;
    }

    return serviceTours.filter((item) => item.status === statusFilter);
  }, [serviceTours, statusFilter]);

  const getStatusLabel = (status) => {
    switch (status) {
      case "WAITING_CONFIG":
        return "Chờ cấu hình";

      case "NOT_STARTED":
        return "Đã cấu hình";

      case "IN_PROGRESS":
        return "Đang phục vụ";

      case "COMPLETED":
        return "Đã kết thúc";

      default:
        return status || "Không xác định";
    }
  };

  if (loading && serviceTours.length === 0) {
    return (
      <div className="convenience-service-tour-detail-loading">
        <RefreshCw size={20} className="spin" />
        <span>Đang tải lịch sử dịch vụ...</span>
      </div>
    );
  }

  return (
    <>
      {/* HEADER */}
      <div className="convenience-service-tour-detail-toolbar">
        <div>
          <h2>
            <Wrench size={20} />
            Lịch sử dịch vụ Tour
          </h2>

          <p>Danh sách các dịch vụ đã được cấu hình cho các Tour.</p>
        </div>

        <button
          type="button"
          className="convenience-service-tour-detail-refresh"
          onClick={loadServiceTours}
          disabled={loading}
        >
          <RefreshCw size={16} className={loading ? "spin" : ""} />
          Làm mới
        </button>
      </div>

      {/* ERROR */}
      {error && (
        <div className="convenience-service-tour-detail-error">
          <AlertCircle size={18} />
          <span>{error}</span>
        </div>
      )}

      {/* FILTER */}
      <div className="convenience-service-tour-detail-filters">
        {STATUS_TABS.map((tab) => (
          <button
            key={tab.value}
            type="button"
            className={
              statusFilter === tab.value
                ? "convenience-service-tour-detail-filter active"
                : "convenience-service-tour-detail-filter"
            }
            onClick={() => setStatusFilter(tab.value)}
          >
            {tab.label}
          </button>
        ))}
      </div>

      {/* EMPTY */}
      {filteredServiceTours.length === 0 && !error ? (
        <div className="convenience-service-tour-detail-empty">
          <Wrench size={32} />

          <h3>Chưa có dữ liệu</h3>

          <p>Không có dịch vụ nào phù hợp với trạng thái đang chọn.</p>
        </div>
      ) : (
        <div className="convenience-service-tour-detail-table-wrapper">
          <table className="convenience-service-tour-detail-table">
            <thead>
              <tr>
                <th>STT</th>
                <th>Mã Tour</th>
                <th>Tên Tour</th>
                <th>Khu vực</th>
                <th>Dịch vụ</th>
                <th>Khách tối đa</th>
                <th>Thời lượng</th>
                <th>Trạng thái</th>
                <th>Chi tiết</th>
              </tr>
            </thead>

            <tbody>
              {filteredServiceTours.map((item, index) => (
                <tr key={item.id}>
                  <td>{index + 1}</td>

                  <td>
                    <span className="font-mono">
                      {item.tourCode || item.tourId || "—"}
                    </span>
                  </td>

                  <td>{item.tourName || "—"}</td>

                  <td>
                    {item.cruiseAreaName || item.cruiseAreaId || "—"}

                    {item.deckNumber != null && (
                      <div>Tầng {item.deckNumber}</div>
                    )}
                  </td>

                  <td>
                    <div>
                      <strong>
                        {item.serviceName || item.serviceId || "—"}
                      </strong>

                      {item.serviceDescription && (
                        <div>{item.serviceDescription}</div>
                      )}
                    </div>
                  </td>

                  <td>{item.maxPassengers ?? "—"}</td>

                  <td>
                    {item.durationMinutes != null
                      ? `${item.durationMinutes} phút`
                      : "Không giới hạn"}
                  </td>

                  <td>
                    <span
                      className={`convenience-service-tour-detail-status ${String(
                        item.status || "",
                      ).toLowerCase()}`}
                    >
                      {getStatusLabel(item.status)}
                    </span>
                  </td>

                  <td>
                    <button
                      type="button"
                      className="convenience-service-tour-detail-view"
                      onClick={() => setViewDetailAssignment(item)}
                      title="Xem chi tiết"
                    >
                      <Eye size={15} />
                      Xem
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* DETAIL */}
      {viewDetailAssignment && (
        <ServiceTourDetailModal
          assignmentId={viewDetailAssignment.id}
          onClose={() => setViewDetailAssignment(null)}
        />
      )}
    </>
  );
};

export default ConvenienceServiceTourDetail;
