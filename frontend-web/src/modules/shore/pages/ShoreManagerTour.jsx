// src/modules/shore/pages/ShoreManagerTour.jsx

import { useMemo } from "react";
import { useNavigate } from "react-router-dom";
import {
  CalendarDays,
  ChevronRight,
  Clock,
  RefreshCw,
  Ship,
} from "lucide-react";

import useShoreTours from "../hooks/useShoreTours";

function ShoreManagerTour() {
  const navigate = useNavigate();

  const { tours, loading, error, reload } = useShoreTours();

  /*
   * =====================================================
   * SORT TOURS
   * =====================================================
   *
   * Ưu tiên Tour đang chạy / sắp chạy,
   * sau đó mới đến Tour đã hoàn thành.
   */

  const sortedTours = useMemo(() => {
    const statusOrder = {
      IN_PROGRESS: 1,
      READY: 2,
      APPROVED: 3,
      COMPLETED: 4,
    };

    return [...tours].sort((a, b) => {
      const statusA = statusOrder[a.statusTrip] || 99;
      const statusB = statusOrder[b.statusTrip] || 99;

      if (statusA !== statusB) {
        return statusA - statusB;
      }

      return new Date(a.startDate || 0) - new Date(b.startDate || 0);
    });
  }, [tours]);

  /*
   * =====================================================
   * STATUS
   * =====================================================
   */

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

  /*
   * =====================================================
   * STATUS CLASS
   * =====================================================
   */

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
   * OPEN CONFIGURATION
   * =====================================================
   */

  const handleConfiguration = (tourId) => {
    navigate(`/shore/visit-tour-configuration?tourId=${tourId}`);
  };

  /*
   * =====================================================
   * FORMAT DATE
   * =====================================================
   */

  const formatDate = (date) => {
    if (!date) {
      return "—";
    }

    const parsedDate = new Date(date);

    if (Number.isNaN(parsedDate.getTime())) {
      return date;
    }

    return parsedDate.toLocaleDateString("vi-VN");
  };

  /*
   * =====================================================
   * LOADING
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
   * RENDER
   * =====================================================
   */

  return (
    <div className="shore-manager-tour">
      {/* =================================================
          HEADER
          ================================================= */}

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

      {/* =================================================
          ERROR
          ================================================= */}

      {error && (
        <div className="shore-manager-tour-error">
          <span>Không thể tải danh sách Tour.</span>

          <button type="button" onClick={reload}>
            Thử lại
          </button>
        </div>
      )}

      {/* =================================================
          SUMMARY
          ================================================= */}

      {!error && (
        <div className="shore-manager-tour-summary">
          <div className="shore-manager-tour-summary-item">
            <span>Tổng số Tour</span>

            <strong>{sortedTours.length}</strong>
          </div>
        </div>
      )}

      {/* =================================================
          EMPTY
          ================================================= */}

      {!error && sortedTours.length === 0 && (
        <div className="shore-manager-tour-empty">
          <Ship size={42} />

          <h2>Chưa có Tour</h2>

          <p>Hiện tại chưa có Tour nào được phép cấu hình cho Shore.</p>
        </div>
      )}

      {/* =================================================
          TOUR LIST
          ================================================= */}

      {!error && sortedTours.length > 0 && (
        <div className="shore-manager-tour-list">
          {sortedTours.map((tour) => (
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
