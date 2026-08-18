// src/modules/operation/components/CruiseSelectModal.jsx

import "../styles/CruiseSelectModal.css";
import {
  X,
  Ship,
  CheckCircle,
  AlertCircle,
  CalendarDays,
  Ban,
  Info,
  Anchor,
  Loader2,
} from "lucide-react";

function formatDate(value) {
  if (!value) return "-";
  try {
    return new Date(value).toLocaleDateString("vi-VN");
  } catch {
    return value;
  }
}

function CruiseSelectModal({
  open,
  tour,
  cruises,
  loading,
  assigning,
  selectedCruiseId,
  onSelectCruise,
  onAssignCruise,
  onClose,
}) {
  if (!open || !tour) return null;

  // Xử lý gán du thuyền thành công thì đóng Modal
  const handleAssign = async () => {
    if (!selectedCruiseId || !onAssignCruise) return;
    try {
      await onAssignCruise(selectedCruiseId);
      onClose(); // Tự động đóng modal sau khi gán thành công
    } catch (error) {
      // Bắt lỗi nếu có, giữ modal để người dùng thử lại
      console.error("Lỗi khi gán du thuyền:", error);
    }
  };

  return (
    <div className="operation-cruise-modal-overlay">
      <div className="operation-cruise-modal">
        {/* HEADER */}
        <div className="operation-cruise-modal-header">
          <div>
            <h2>Chọn Du thuyền cho Tour</h2>
            <p>Chọn du thuyền khả dụng để gán cho Tour này.</p>
          </div>

          <button
            type="button"
            className="operation-cruise-modal-close"
            onClick={onClose}
            disabled={assigning}
          >
            <X size={20} />
          </button>
        </div>

        {/* TOUR INFORMATION */}
        <div className="operation-cruise-tour-info">
          <div>
            <span>Tour</span>
            <strong>{tour.name}</strong>
          </div>

          <div>
            <span>Mã Tour</span>
            <strong>{tour.code}</strong>
          </div>

          <div>
            <span>Thời gian</span>
            <strong>
              <CalendarDays size={15} />
              {formatDate(tour.startDate)}
              <span>→</span>
              {formatDate(tour.endDate)}
            </strong>
          </div>
        </div>

        {/* TITLE */}
        <div className="operation-cruise-select-title">
          <div>
            <Ship size={20} />
            <h3>Danh sách du thuyền</h3>
          </div>
          <span>{cruises?.length || 0} du thuyền trong hệ thống</span>
        </div>

        {/* CRUISE LIST */}
        <div className="operation-cruise-list">
          {loading ? (
            <div className="operation-cruise-loading">
              Đang kiểm tra lịch khả dụng của các du thuyền...
            </div>
          ) : cruises?.length === 0 ? (
            <div className="operation-cruise-empty">
              <AlertCircle size={40} />
              <h3>Không tìm thấy du thuyền</h3>
              <p>Chưa có dữ liệu du thuyền trong hệ thống.</p>
            </div>
          ) : (
            cruises.map((cruise) => {
              const selected = selectedCruiseId === cruise.id;
              const available = cruise.isAvailable;

              return (
                <div
                  key={cruise.id}
                  className={`operation-cruise-option-wrapper ${
                    !available ? "unavailable" : ""
                  }`}
                >
                  <button
                    type="button"
                    className={`operation-cruise-option ${
                      selected ? "selected" : ""
                    } ${!available ? "disabled" : ""}`}
                    onClick={() => available && onSelectCruise(cruise.id)}
                    disabled={assigning || !available}
                  >
                    <div className="operation-cruise-option-icon">
                      <Ship size={22} />
                    </div>

                    <div className="operation-cruise-option-info">
                      <div className="cruise-header-row">
                        <strong>{cruise.name}</strong>
                        <span
                          className={`cruise-status-badge ${
                            available ? "available" : "busy"
                          }`}
                        >
                          {available ? "Khả dụng" : "Không thể chọn"}
                        </span>
                      </div>

                      <span className="cruise-code">Mã tàu: {cruise.code}</span>

                      <div
                        className={`cruise-reason ${
                          available ? "text-success" : "text-danger"
                        }`}
                      >
                        {available ? (
                          <span className="flex-center">
                            <CheckCircle size={13} /> {cruise.reason}
                          </span>
                        ) : (
                          <span className="flex-center">
                            <Ban size={13} /> {cruise.reason}
                          </span>
                        )}
                      </div>
                    </div>

                    <div className="operation-cruise-option-check">
                      {selected && <CheckCircle size={22} />}
                    </div>
                  </button>

                  {!available && cruise.conflictingTours?.length > 0 && (
                    <div className="operation-cruise-conflicts">
                      <div className="conflict-title">
                        <Info size={13} /> Danh sách Tour bị trùng lịch:
                      </div>
                      <ul>
                        {cruise.conflictingTours.map((conflict) => (
                          <li key={conflict.tourId}>
                            <strong>{conflict.tourCode}</strong> -{" "}
                            {conflict.tourName} (
                            {formatDate(conflict.startDate)} →{" "}
                            {formatDate(conflict.endDate)})
                          </li>
                        ))}
                      </ul>
                    </div>
                  )}
                </div>
              );
            })
          )}
        </div>

        {/* FOOTER */}
        <div className="operation-cruise-modal-footer">
          <button
            type="button"
            className="operation-cruise-cancel-button"
            onClick={onClose}
            disabled={assigning}
          >
            Hủy
          </button>

          <button
            type="button"
            className="operation-cruise-assign-button"
            onClick={handleAssign}
            disabled={!selectedCruiseId || assigning || loading}
          >
            {assigning ? (
              <>
                <Loader2 size={17} className="spinner-icon" />
                <span>Đang gán...</span>
              </>
            ) : (
              <>
                <Anchor size={17} />
                <span>Xác nhận gán du thuyền</span>
              </>
            )}
          </button>
        </div>
      </div>
    </div>
  );
}

export default CruiseSelectModal;
