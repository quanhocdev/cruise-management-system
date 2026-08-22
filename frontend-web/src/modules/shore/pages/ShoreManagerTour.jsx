// src/modules/shore/pages/ShoreManagerTour.jsx

import { useState, useMemo } from "react";
import { useNavigate } from "react-router-dom";
import {
  CalendarDays,
  ChevronRight,
  Clock,
  RefreshCw,
  Ship,
  Filter,
} from "lucide-react";
import "../styles/ShoreManagerTour.css";
import useShoreTours from "../hooks/useShoreTours";

function ShoreManagerTour() {
  const navigate = useNavigate();
  const { tours, loading, error, reload } = useShoreTours();

  // State cho bộ lọc trạng thái
  const [selectedStatus, setSelectedStatus] = useState("ALL");

  /*
   * =====================================================
   * STATUS OPTIONS & LABELS
   * =====================================================
   */
  const statusOptions = [
    { value: "ALL", label: "Tất cả trạng thái" },
    { value: "IN_PROGRESS", label: "Đang diễn ra" },
    { value: "READY", label: "Sẵn sàng" },
    { value: "APPROVED", label: "Đã duyệt" },
    { value: "COMPLETED", label: "Đã hoàn thành" },
  ];

  const getStatusLabel = (status) => {
    switch (status) {
      case "APPROVED":
        return "Đã duyệt";
      case "READY":
        return "Sẵn sàng";
      case "IN_PROGRESS":
        return "Đang diễn ra";
      case "COMPLETED":
        return "Đã hoàn thành";
      default:
        return status || "Không xác định";
    }
  };

  const getStatusClass = (status) => {
    switch (status) {
      case "APPROVED":
        return "approved";
      case "READY":
        return "ready";
      case "IN_PROGRESS":
        return "in-progress";
      case "COMPLETED":
        return "completed";
      default:
        return "";
    }
  };

  /*
   * =====================================================
   * FILTER & SORT TOURS
   * =====================================================
   */
  const filteredAndSortedTours = useMemo(() => {
    // 1. Lọc theo trạng thái được chọn
    const filtered = tours.filter((tour) => {
      if (selectedStatus === "ALL") return true;
      return tour.statusTrip === selectedStatus;
    });

    // 2. Sắp xếp thứ tự ưu tiên
    const statusOrder = {
      IN_PROGRESS: 1,
      READY: 2,
      APPROVED: 3,
      COMPLETED: 4,
    };

    return filtered.sort((a, b) => {
      const statusA = statusOrder[a.statusTrip] || 99;
      const statusB = statusOrder[b.statusTrip] || 99;

      if (statusA !== statusB) {
        return statusA - statusB;
      }

      return new Date(a.startDate || 0) - new Date(b.startDate || 0);
    });
  }, [tours, selectedStatus]);

  /*
   * =====================================================
   * HANDLERS
   * =====================================================
   */
  const handleConfiguration = (tourId) => {
    navigate(`/shore/visit-tour-configuration?tourId=${tourId}`);
  };

  const formatDate = (date) => {
    if (!date) return "—";
    const parsedDate = new Date(date);
    if (Number.isNaN(parsedDate.getTime())) return date;
    return parsedDate.toLocaleDateString("vi-VN");
  };

  /*
   * =====================================================
   * RENDER LOADING
   * =====================================================
   */
  if (loading) {
    return (
      <div className="shore-manager-tour">
        <div className="shore-manager-tour-loading">
          <RefreshCw size={22} className="shore-manager-tour-spinner" />
          <span>Đang tải danh sách Tour...</span>
        </div>
      </div>
    );
  }

  /*
   * =====================================================
   * MAIN RENDER
   * =====================================================
   */
  return (
    <div className="shore-manager-tour">
      {/* HEADER */}
      <div className="shore-manager-tour-header">
        <div>
          <h1>Quản lý Tour bờ</h1>
          <p>
            Xem các Tour được phân công và cấu hình hoạt động tham quan trên bờ.
          </p>
        </div>

        <button
          type="button"
          className="shore-manager-tour-refresh"
          onClick={reload}
          disabled={loading}
        >
          <RefreshCw size={17} />
          <span>Làm mới</span>
        </button>
      </div>

      {/* ERROR STATE */}
      {error && (
        <div className="shore-manager-tour-error">
          <span>Không thể tải danh sách Tour.</span>
          <button type="button" onClick={reload}>
            Thử lại
          </button>
        </div>
      )}

      {/* FILTER & SUMMARY BAR */}
      {!error && (
        <div className="shore-manager-tour-toolbar">
          <div className="shore-manager-tour-summary-item">
            <span>Tổng số Tour</span>
            <strong>{filteredAndSortedTours.length}</strong>
          </div>

          <div className="shore-manager-tour-filter-box">
            <Filter size={16} />
            <label htmlFor="tour-status-filter">Trạng thái:</label>
            <select
              id="tour-status-filter"
              value={selectedStatus}
              onChange={(e) => setSelectedStatus(e.target.value)}
            >
              {statusOptions.map((option) => (
                <option key={option.value} value={option.value}>
                  {option.label}
                </option>
              ))}
            </select>
          </div>
        </div>
      )}

      {/* EMPTY STATE */}
      {!error && filteredAndSortedTours.length === 0 && (
        <div className="shore-manager-tour-empty">
          <Ship size={42} />
          <h2>Khôn tìm thấy Tour phù hợp</h2>
          <p>
            {selectedStatus === "ALL"
              ? "Hiện tại chưa có Tour nào được phép cấu hình cho Shore."
              : "Không có Tour nào phù hợp với trạng thái đã lọc."}
          </p>
        </div>
      )}

      {/* TOUR LIST */}
      {!error && filteredAndSortedTours.length > 0 && (
        <div className="shore-manager-tour-list">
          {filteredAndSortedTours.map((tour) => (
            <article key={tour.id} className="shore-manager-tour-card">
              {/* CARD HEADER */}
              <div className="shore-manager-tour-card-header">
                <div className="shore-manager-tour-card-title">
                  <span className="shore-manager-tour-code">
                    {tour.code || "—"}
                  </span>
                  <h2>{tour.name || "Tour không có tên"}</h2>
                </div>

                <span
                  className={`shore-manager-tour-status ${getStatusClass(
                    tour.statusTrip,
                  )}`}
                >
                  {getStatusLabel(tour.statusTrip)}
                </span>
              </div>

              {/* DESCRIPTION */}
              {tour.description && (
                <p className="shore-manager-tour-description">
                  {tour.description}
                </p>
              )}

              {/* INFORMATION */}
              <div className="shore-manager-tour-info">
                <div className="shore-manager-tour-info-item">
                  <CalendarDays size={18} />
                  <div>
                    <span>Thời gian</span>
                    <strong>
                      {formatDate(tour.startDate)}
                      {" → "}
                      {formatDate(tour.endDate)}
                    </strong>
                  </div>
                </div>

                <div className="shore-manager-tour-info-item">
                  <Ship size={18} />
                  <div>
                    <span>Du thuyền</span>
                    <strong>{tour.cruiseName || "—"}</strong>
                  </div>
                </div>

                <div className="shore-manager-tour-info-item">
                  <Clock size={18} />
                  <div>
                    <span>Trạng thái</span>
                    <strong>{getStatusLabel(tour.statusTrip)}</strong>
                  </div>
                </div>
              </div>

              {/* ACTION */}
              <div className="shore-manager-tour-card-footer">
                <button
                  type="button"
                  className="shore-manager-tour-configure"
                  onClick={() => handleConfiguration(tour.id)}
                >
                  <span>Cấu hình Visit Tour</span>
                  <ChevronRight size={18} />
                </button>
              </div>
            </article>
          ))}
        </div>
      )}
    </div>
  );
}

export default ShoreManagerTour;
