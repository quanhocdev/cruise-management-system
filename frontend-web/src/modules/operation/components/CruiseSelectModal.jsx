import React, { useMemo, useState, useEffect } from "react";
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
  Users,
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
  const [maxPassengersInput, setMaxPassengersInput] = useState("");

  // Tự động điền số lượng tối đa hiện tại của tour
  useEffect(() => {
    if (tour?.maxPassengers) {
      setMaxPassengersInput(tour.maxPassengers);
    }
  }, [tour]);

  const currentAssignedCruiseId =
    tour?.cruiseId || tour?.cruise?.id || tour?.assignedCruiseId || null;

  // Gom các nguồn phân công (Đặt đúng thứ tự các Hook lên trên)
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

  const displayCruises = useMemo(() => {
    const list = [...(cruises || [])];

    const currentCruiseObj =
      tour?.cruise ||
      (tour?.cruiseId
        ? { id: tour.cruiseId, name: "Du thuyền hiện tại" }
        : null);

    if (currentCruiseObj && !list.some((c) => c.id === currentCruiseObj.id)) {
      list.unshift({
        ...currentCruiseObj,
        isAvailable: true,
      });
    }

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

  const selectedCruiseObj = displayCruises.find(
    (c) => c.id === selectedCruiseId,
  );

  // ĐẶT ĐIỀU KIỆN RETURN NULL SAU TẤT CẢ CÁC HOOK ĐỂ KHÔNG BỊ LỆCH THỨ TỰ
  if (!open || !tour) return null;

  // Xử lý gán du thuyền kèm maxPassengers
  const handleAssign = async () => {
    if (
      !selectedCruiseId ||
      !onAssignCruise ||
      hasAssignedAreas ||
      !maxPassengersInput
    ) {
      return;
    }

    const numericMax = parseInt(maxPassengersInput, 10);
    if (isNaN(numericMax) || numericMax <= 0) {
      alert("Vui lòng nhập số lượng hành khách tối đa hợp lệ lớn hơn 0.");
      return;
    }

    if (
      selectedCruiseObj &&
      selectedCruiseObj.maxPassengers &&
      numericMax > selectedCruiseObj.maxPassengers
    ) {
      alert(
        `Số lượng khách của tour (${numericMax}) không được vượt quá sức chứa của du thuyền (${selectedCruiseObj.maxPassengers}).`,
      );
      return;
    }

    try {
      await onAssignCruise(selectedCruiseId, numericMax);
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
            <h2>Chọn Du thuyền & Cấu hình Tour</h2>
            <p>Chọn tàu và cấu hình sức chứa tối đa cho hành khách.</p>
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
              <div>
                <strong>Không thể thay đổi du thuyền!</strong>
              </div>
              <p>
                Tour đã được gán <b>{assignedCount} khu vực/dịch vụ</b>. Vui
                lòng xóa phân công khu vực của Tour trước khi đổi du thuyền.
              </p>
            </div>
          </div>
        )}

        {/* CẤU HÌNH SỐ LƯỢNG HÀNH KHÁCH TỐI ĐA */}
        <div className="operation-cruise-capacity-box">
          <label className="capacity-box-label">
            <Users size={16} />
            <span>Số lượng hành khách tối đa cho phép đặt vé:</span>
          </label>
          <div className="capacity-input-wrapper">
            <input
              type="number"
              min="1"
              max={selectedCruiseObj?.maxPassengers || 9999}
              value={maxPassengersInput}
              onChange={(e) => setMaxPassengersInput(e.target.value)}
              placeholder="VD: 150"
              className="capacity-input-field"
            />
            {selectedCruiseObj?.maxPassengers && (
              <span className="capacity-input-hint">
                Trần sức chứa tàu chọn: <b>{selectedCruiseObj.maxPassengers}</b>{" "}
                khách
              </span>
            )}
          </div>
        </div>

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
          ) : displayCruises.length === 0 ? (
            <div className="operation-cruise-empty">
              <AlertCircle size={40} />
              <h3>Không tìm thấy du thuyền</h3>
              <p>Chưa có dữ liệu du thuyền nào cho Tour này.</p>
            </div>
          ) : (
            displayCruises.map((cruise) => {
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
                        Mã tàu: {cruise.code || "-"} | Sức chứa tối đa:{" "}
                        <b>{cruise.maxPassengers}</b> khách
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
              !maxPassengersInput ||
              (selectedCruiseId === currentAssignedCruiseId &&
                Number(maxPassengersInput) === Number(tour?.maxPassengers))
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
