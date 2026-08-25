import { useCallback, useEffect, useState } from "react";
import { RefreshCw } from "lucide-react";

import "../styles/ShoreManagerTour.css";

import visitTourService from "../services/visitTourService";
import ShoreTourTable from "../components/ShoreTourTable";

function ShoreManagerTour() {
  const [visitTours, setVisitTours] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const loadVisitTours = useCallback(async () => {
    setLoading(true);
    setError(null);

    try {
      const data = await visitTourService.getAll();

      setVisitTours(data || []);
    } catch (err) {
      console.error("🔥 LOAD VISIT TOURS ERROR:", err);

      setError(err);
      setVisitTours([]);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadVisitTours();
  }, [loadVisitTours]);

  const handleConfiguration = (tourId, scheduleStopId) => {
    window.location.href =
      `/shore/visit-tour-configuration` +
      `?tourId=${tourId}` +
      `&scheduleStopId=${scheduleStopId}`;
  };

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

  return (
    <div className="shore-manager-tour">
      {/* HEADER */}
      <div className="shore-manager-tour-header">
        <div>
          <h1>Quản lý Tour bờ</h1>

          <p>Danh sách Visit Tour và cấu hình hoạt động tham quan trên bờ.</p>
        </div>

        <button
          type="button"
          className="shore-manager-tour-refresh"
          onClick={loadVisitTours}
          disabled={loading}
        >
          <RefreshCw size={17} />
          <span>Làm mới</span>
        </button>
      </div>

      {/* ERROR */}
      {error && (
        <div className="shore-manager-tour-error">
          <span>Không thể tải danh sách Visit Tour.</span>

          <button type="button" onClick={loadVisitTours}>
            Thử lại
          </button>
        </div>
      )}

      {/* SUMMARY */}
      {!error && (
        <div className="shore-manager-tour-toolbar">
          <div className="shore-manager-tour-summary-item">
            <span>Tổng số Visit Tour</span>
            <strong>{visitTours.length}</strong>
          </div>
        </div>
      )}

      {/* TABLE */}
      {!error && (
        <ShoreTourTable
          visitTours={visitTours}
          onConfigure={handleConfiguration}
        />
      )}
    </div>
  );
}

export default ShoreManagerTour;
