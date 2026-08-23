import React, { useMemo } from "react";
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
  assignments = [],
  loading,
  assigning,
  selectedCruiseId,
  onSelectCruise,
  onAssignCruise,
  onClose,
}) {
  if (!open || !tour) return null;

  const currentAssignedCruiseId =
    tour?.cruiseId || tour?.cruise?.id || tour?.assignedCruiseId || null;

  // ✅ Gom các nguồn phân công
  const tourAssignments = useMemo(() => {
    if (Array.isArray(assignments) && assignments.length > 0) {
      return assignments;
    }

    return [
      ...(tour?.activityAssignments || []),
      ...(tour?.productAssignments || []),
      ...(tour?.serviceAssignments || []),
      ...(tour?.assignedAreas || []),
      ...(tour?.areaAssignments || []),
      ...(tour?.roomAssignments || []),
    ];
  }, [assignments, tour]);

  const assignedCount = tourAssignments.length;
  const hasAssignedAreas =
    Boolean(currentAssignedCruiseId) && assignedCount > 0;

  // ✅ 1. SỬA TẠI ĐÂY: Đảm bảo luôn hiển thị tàu đã gán ngay cả khi prop cruises từ API bị rỗng []
  const displayCruises = useMemo(() => {
    const list = [...(cruises || [])];

    // Lấy object du thuyền hiện tại từ tour
    const currentCruiseObj =
      tour?.cruise ||
      (tour?.cruiseId
        ? { id: tour.cruiseId, name: "Du thuyền hiện tại" }
        : null);

    // Nếu có du thuyền hiện tại nhưng chưa nằm trong danh sách API trả về
    if (currentCruiseObj && !list.some((c) => c.id === currentCruiseObj.id)) {
      list.unshift({
        ...currentCruiseObj,
        isAvailable: true, // Coi tàu đang gán là khả dụng với tour này
      });
    }

    // Sắp xếp: Tàu hiện tại lên đầu
    return list.sort((a, b) => {
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
  }, [cruises, tour, currentAssignedCruiseId]);

  // Xử lý gán du thuyền
  const handleAssign = async () => {
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

  return (
    <div className="operation-cruise-modal-overlay">
      <div className="operation-cruise-modal">
        {/* HEADER */}
        <div className="operation-cruise-modal-header">
          <div>
            <h2>Chọn Du thuyền cho Tour</h2>
            <p>Thông tin và lịch khả dụng của du thuyền được gán.</p>
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

        {/* THÔNG BÁO CẢNH BÁO BỊ KHÓA */}
        {hasAssignedAreas && (
          <div className="operation-cruise-warning-banner">
            <AlertTriangle size={20} className="warning-icon" />
            <div>
              <strong>Không thể thay đổi du thuyền!</strong>
              <p>
                Tour đã được gán <b>{assignedCount} khu vực/dịch vụ</b>. Vui
                lòng xóa phân công khu vực của Tour trước khi đổi du thuyền.
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
          <span>{displayCruises?.length || 0} du thuyền trong hệ thống</span>
        </div>

        {/* CRUISE LIST */}
        <div className="operation-cruise-list">
          {loading ? (
            <div className="operation-cruise-loading">
              <Loader2 size={24} className="spinner-icon" />
              <span>Đang kiểm tra lịch khả dụng của các du thuyền...</span>
            </div>
          ) : displayCruises.length === 0 ? ( // ✅ 2. Dùng displayCruises
            <div className="operation-cruise-empty">
              <AlertCircle size={40} />
              <h3>Không tìm thấy du thuyền</h3>
              <p>Chưa có dữ liệu du thuyền nào cho Tour này.</p>
            </div>
          ) : (
            displayCruises.map((cruise) => {
              // ✅ 3. Dùng displayCruises
              const isSelected = selectedCruiseId === cruise.id;
              const isCurrentlyAssigned =
                currentAssignedCruiseId &&
                currentAssignedCruiseId === cruise.id;

              const isScheduleAvailable =
                cruise.isAvailable || isCurrentlyAssigned;

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
                    onClick={() =>
                      !isCardDisabled && onSelectCruise?.(cruise.id)
                    }
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

                      <span className="cruise-code">
                        Mã tàu: {cruise.code || "-"}
                      </span>

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
            Đóng
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
                    ? "Đã có phân công (Đã khóa)"
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
