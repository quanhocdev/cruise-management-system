// src/modules/shore/components/shore-tour-manager/ShoreTourHeader.jsx
// (FILE MỚI) Tiêu đề + nút Lịch sử/Làm mới + các thông báo lỗi/thành công.
// CSS: dùng chung styles/ActivityVisitTour.css (import ở page).

import { RefreshCw, History, AlertCircle, CheckCircle2 } from "lucide-react";

function ShoreTourHeader({
  loading,
  onRefresh,
  onOpenHistory,
  error,
  completeError,
  onCloseCompleteError,
  successMessage,
  onCloseSuccess,
}) {
  return (
    <>
      <div className="shore-manager-tour-header">
        <div>
          <h1>Quản lý Tour bờ</h1>
          <p>Danh sách Visit Tour và cấu hình hoạt động tham quan trên bờ.</p>
        </div>

        <div className="shore-manager-tour-header-actions">
          <button
            type="button"
            className="shore-manager-tour-history"
            onClick={onOpenHistory}
          >
            <History size={17} />
            <span>Lịch sử cấu hình</span>
          </button>

          <button
            type="button"
            className="shore-manager-tour-refresh"
            onClick={onRefresh}
            disabled={loading}
          >
            <RefreshCw size={17} />
            <span>Làm mới</span>
          </button>
        </div>
      </div>

      {error && (
        <div className="shore-manager-tour-error">
          <span>{error}</span>
          <button type="button" onClick={onRefresh}>
            Thử lại
          </button>
        </div>
      )}

      {completeError && (
        <div className="shore-manager-tour-complete-error">
          <AlertCircle size={17} />
          <span>{completeError}</span>
          <button type="button" onClick={onCloseCompleteError}>
            Đóng
          </button>
        </div>
      )}

      {successMessage && (
        <div className="shore-manager-tour-success">
          <CheckCircle2 size={17} />
          <span>{successMessage}</span>
          <button type="button" onClick={onCloseSuccess}>
            Đóng
          </button>
        </div>
      )}
    </>
  );
}

export default ShoreTourHeader;
