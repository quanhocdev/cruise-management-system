import React from "react";
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
  AlertTriangle,
  Lock,
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
  cruises = [],
  assignments = [], // Có thể truyền trực tiếp assignments từ parent vào nếu tour không chứa sẵn
  loading,
  assigning,
  selectedCruiseId,
  onSelectCruise,
  onAssignCruise,
  onClose,
}) {
  if (!open || !tour) return null;

  // 1. Lấy ID du thuyền hiện tại đang được gán cho Tour này
  const currentAssignedCruiseId =
    tour?.cruiseId || tour?.cruise?.id || tour?.assignedCruiseId || null;

  // 2. Lấy danh sách các khu vực/phòng đã được phân công cho Tour này
  const tourAssignments =
    Array.isArray(assignments) && assignments.length > 0
      ? assignments
      : tour?.assignedAreas ||
        tour?.assignedZones ||
        tour?.assignedRooms || // 👈 Thêm kiểm tra phòng
        tour?.areaAssignments || // 👈 Thêm kiểm tra khu vực
        tour?.roomAssignments || // 👈 Thêm
        tour?.areas ||
        tour?.assignments ||
        [];

  const assignedCount = tourAssignments.length;
  const hasAssignedAreas = assignedCount > 0;

  // ✅ ĐẶT CONSOLE.LOG Ở ĐÂY (Sau khi các biến đã được khai báo)
  console.log("DEBUG MODAL DATA:", {
    tourObject: tour,
    assignmentsProp: assignments,
    detectedAssignments: tourAssignments,
    hasAssignedAreas: hasAssignedAreas,
  });

  // Xử lý gán du thuyền
  const handleAssign = async () => {
    // Chặn tuyệt đối nếu đã có khu vực phân công hoặc chưa chọn tàu mới
    if (
      !selectedCruiseId ||
      !onAssignCruise ||
      hasAssignedAreas ||
      selectedCruiseId === currentAssignedCruiseId
    ) {
      return;
    }

    try {
      await onAssignCruise(selectedCruiseId);
      onClose();
    } catch (error) {
      console.error("Lỗi khi gán du thuyền:", error);
    }
  };

  // SẮP XẾP DANH SÁCH DU THUYỀN
  const sortedCruises = [...(cruises || [])].sort((a, b) => {
    const isACurrent =
      currentAssignedCruiseId && currentAssignedCruiseId === a.id;
    const isBCurrent =
      currentAssignedCruiseId && currentAssignedCruiseId === b.id;

    if (isACurrent && !isBCurrent) return -1;
    if (!isACurrent && isBCurrent) return 1;

    const isAAvailable = a.isAvailable;
    const isBAvailable = b.isAvailable;
    if (isAAvailable && !isBAvailable) return -1;
    if (!isAAvailable && isBAvailable) return 1;

    return (a.name || "").localeCompare(b.name || "");
  });

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
            <strong>{tour.name || "-"}</strong>
          </div>

          <div>
            <span>Mã Tour</span>
            <strong>{tour.code || "-"}</strong>
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

        {/* THÔNG BÁO CẢNH BÁO BỊ KHÓA DO ĐÃ CÓ KHU VỰC PHÂN CÔNG */}
        {hasAssignedAreas && (
          <div className="operation-cruise-warning-banner">
            <AlertTriangle size={20} className="warning-icon" />
            <div>
              <strong>Không thể thay đổi du thuyền!</strong>
              <p>
                Tour đã được gán <b>{assignedCount} khu vực </b>. Vui lòng xóa
                khu vực được phân công của Tour trước khi đổi du thuyền
              </p>
            </div>
          </div>
        )}

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
              <Loader2 size={24} className="spinner-icon" />
              <span>Đang kiểm tra lịch khả dụng của các du thuyền...</span>
            </div>
          ) : sortedCruises.length === 0 ? (
            <div className="operation-cruise-empty">
              <AlertCircle size={40} />
              <h3>Không tìm thấy du thuyền</h3>
              <p>Chưa có dữ liệu du thuyền trong hệ thống.</p>
            </div>
          ) : (
            sortedCruises.map((cruise) => {
              const isSelected = selectedCruiseId === cruise.id;
              const isCurrentlyAssigned =
                currentAssignedCruiseId &&
                currentAssignedCruiseId === cruise.id;

              // Điều kiện khả dụng gốc từ lịch trùng
              const isScheduleAvailable =
                cruise.isAvailable || isCurrentlyAssigned;

              // 📍 Nếu đã có phân công khu vực => KHÓA tất cả các tàu KHÁC tàu hiện tại
              const isCardDisabled =
                assigning ||
                !isScheduleAvailable ||
                (hasAssignedAreas && !isCurrentlyAssigned);

              return (
                <div
                  key={cruise.id}
                  className={`operation-cruise-option-wrapper ${
                    isCurrentlyAssigned ? "currently-assigned" : ""
                  } ${isCardDisabled ? "unavailable" : ""}`}
                >
                  <button
                    type="button"
                    className={`operation-cruise-option ${
                      isSelected ? "selected" : ""
                    } ${isCardDisabled ? "disabled" : ""}`}
                    onClick={() => !isCardDisabled && onSelectCruise(cruise.id)}
                    disabled={isCardDisabled}
                  >
                    <div className="operation-cruise-option-icon">
                      <Ship size={22} />
                    </div>

                    <div className="operation-cruise-option-info">
                      <div className="cruise-header-row">
                        <strong>{cruise.name}</strong>

                        {isCurrentlyAssigned ? (
                          <span className="cruise-status-badge current">
                            Đang gán cho Tour
                          </span>
                        ) : hasAssignedAreas ? (
                          <span className="cruise-status-badge busy">
                            <Lock size={12} /> Không thể chọn
                          </span>
                        ) : (
                          <span
                            className={`cruise-status-badge ${
                              isScheduleAvailable ? "available" : "busy"
                            }`}
                          >
                            {isScheduleAvailable
                              ? "Khả dụng"
                              : "Không thể chọn"}
                          </span>
                        )}
                      </div>

                      <span className="cruise-code">Mã tàu: {cruise.code}</span>

                      {/* LÝ DO HỢP LỆ / KHÔNG HỢP LỆ */}
                      <div
                        className={`cruise-reason ${
                          isCurrentlyAssigned ||
                          (isScheduleAvailable && !hasAssignedAreas)
                            ? "text-success"
                            : "text-danger"
                        }`}
                      >
                        {isCurrentlyAssigned ? (
                          <span className="flex-center">
                            <CheckCircle size={13} /> Du thuyền hiện tại của
                            Tour
                          </span>
                        ) : hasAssignedAreas ? (
                          <span className="flex-center">
                            <Ban size={13} /> Cần hủy phân công khu vực trước
                            khi chọn
                          </span>
                        ) : isScheduleAvailable ? (
                          <span className="flex-center">
                            <CheckCircle size={13} />{" "}
                            {cruise.reason || "Sẵn sàng gán"}
                          </span>
                        ) : (
                          <span className="flex-center">
                            <Ban size={13} />{" "}
                            {cruise.reason || "Trùng lịch vận hành"}
                          </span>
                        )}
                      </div>
                    </div>

                    <div className="operation-cruise-option-check">
                      {isSelected && <CheckCircle size={22} />}
                    </div>
                  </button>

                  {/* DANH SÁCH TOUR TRÙNG LỊCH (NẾU CÓ) */}
                  {!isScheduleAvailable &&
                    cruise.conflictingTours?.length > 0 && (
                      <div className="operation-cruise-conflicts">
                        <div className="conflict-title">
                          <Info size={13} /> Danh sách Tour bị trùng lịch:
                        </div>
                        <ul>
                          {cruise.conflictingTours.map((conflict) => (
                            <li key={conflict.tourId || conflict.id}>
                              <strong>
                                {conflict.tourCode || conflict.code}
                              </strong>{" "}
                              - {conflict.tourName || conflict.name} (
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
            disabled={
              !selectedCruiseId ||
              assigning ||
              loading ||
              hasAssignedAreas ||
              selectedCruiseId === currentAssignedCruiseId
            }
          >
            {assigning ? (
              <>
                <Loader2 size={17} className="spinner-icon" />
                <span>Đang gán...</span>
              </>
            ) : (
              <>
                <Anchor size={17} />
                <span>
                  {hasAssignedAreas
                    ? "Đã có phân công khu vực (Đã khóa)"
                    : "Xác nhận gán du thuyền"}
                </span>
              </>
            )}
          </button>
        </div>
      </div>
    </div>
  );
}

export default CruiseSelectModal;
