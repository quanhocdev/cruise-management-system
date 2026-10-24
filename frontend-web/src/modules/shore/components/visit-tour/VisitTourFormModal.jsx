// src/modules/shore/components/visit-tour/VisitTourFormModal.jsx

import { useEffect, useState } from "react";
import {
  X,
  AlertCircle,
  Clock,
  Calendar,
  Info,
  Users,
  UserRound,
  RefreshCw,
} from "lucide-react";

import visitTourService from "../../services/visitTourService";
import "../../styles/VisitTourFormModal.css";

const EMPTY_FORM = {
  name: "",
  description: "",
  startTime: "",
  endTime: "",
  maxPassengers: "",
  price: "",
};

function toInputDateTime(value) {
  if (!value) return "";
  return value.slice(0, 16);
}

function formatDisplayDateTime(dateTimeStr) {
  if (!dateTimeStr) return "—";

  try {
    const [datePart, timePart] = dateTimeStr.split("T");

    if (!datePart || !timePart) return dateTimeStr;

    const [year, month, day] = datePart.split("-");
    const timeOnly = timePart.substring(0, 5);

    return `${day}/${month}/${year} lúc ${timeOnly}`;
  } catch {
    return dateTimeStr;
  }
}

function formatPrice(price) {
  if (price == null || price === "") return "Chưa có giá";

  return `${Number(price).toLocaleString("vi-VN")} VND`;
}

function VisitTourFormModal({
  visitTour,
  scheduleStop,
  loading,
  onClose,
  onSubmit,
}) {
  const [form, setForm] = useState(EMPTY_FORM);
  const [error, setError] = useState("");

  const [staffList, setStaffList] = useState([]);
  const [assignments, setAssignments] = useState([]);
  const [selectedUserIds, setSelectedUserIds] = useState([]);

  const [staffLoading, setStaffLoading] = useState(false);
  const [staffError, setStaffError] = useState("");
  const [assignmentLoading, setAssignmentLoading] = useState(false);

  const isEdit = Boolean(visitTour);

  const arriveAt = scheduleStop?.arriveAt || visitTour?.arriveAt || "";
  const leaveAt = scheduleStop?.leaveAt || visitTour?.leaveAt || "";
  const stopName = scheduleStop?.portName || visitTour?.portName || "";
  const scheduleDate =
    scheduleStop?.scheduleDate || visitTour?.scheduleDate || "";

  useEffect(() => {
    if (visitTour) {
      setForm({
        name: visitTour.name || "",
        description: visitTour.description || "",
        startTime: toInputDateTime(visitTour.startTime),
        endTime: toInputDateTime(visitTour.endTime),
        maxPassengers: visitTour.maxPassengers ?? "",
        price: visitTour.price ?? "",
      });
    } else {
      setForm({
        ...EMPTY_FORM,
        startTime: toInputDateTime(arriveAt),
        endTime: toInputDateTime(leaveAt),
      });
    }

    setError("");
  }, [visitTour, arriveAt, leaveAt]);

  // Tải nhân viên có thể phân công và danh sách đã phân công.
  useEffect(() => {
    let active = true;

    const loadStaffData = async () => {
      setStaffLoading(true);
      setStaffError("");
      setStaffList([]);
      setAssignments([]);
      setSelectedUserIds([]);

      try {
        const staffData = await visitTourService.getShoreStaff();

        if (!active) return;

        const normalizedStaff = Array.isArray(staffData)
          ? staffData.filter(
              (staff) => staff?.userId && staff.role === "SHORE_STAFF",
            )
          : [];

        setStaffList(normalizedStaff);
      } catch (err) {
        if (!active) return;

        console.error("LOAD SHORE STAFF ERROR:", err);
        setStaffError(
          err.response?.data?.message ||
            "Không thể tải danh sách nhân viên Shore.",
        );
      }

      if (visitTour?.id) {
        setAssignmentLoading(true);

        try {
          const assignmentData = await visitTourService.getAssignments(
            visitTour.id,
          );

          if (!active) return;

          setAssignments(Array.isArray(assignmentData) ? assignmentData : []);
        } catch (err) {
          if (!active) return;

          console.error("LOAD SHORE ASSIGNMENTS ERROR:", err);
          setStaffError((previous) =>
            [previous, "Không thể tải danh sách nhân viên đã phân công."]
              .filter(Boolean)
              .join(" "),
          );
        } finally {
          if (active) setAssignmentLoading(false);
        }
      }

      if (active) setStaffLoading(false);
    };

    loadStaffData();

    return () => {
      active = false;
    };
  }, [visitTour?.id]);

  const assignedUserIds = new Set(
    assignments.map((item) => item.userId).filter(Boolean),
  );

  // Không hiển thị nhân viên đã được phân công trong danh sách chọn thêm.
  const availableStaff = staffList.filter(
    (staff) => !assignedUserIds.has(staff.userId),
  );

  const handleChange = (event) => {
    const { name, value } = event.target;

    setForm((previous) => ({
      ...previous,
      [name]: value,
    }));
  };

  const toggleStaff = (userId) => {
    setSelectedUserIds((previous) =>
      previous.includes(userId)
        ? previous.filter((id) => id !== userId)
        : [...previous, userId],
    );
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError("");

    if (!form.name.trim()) {
      setError("Vui lòng nhập tên Visit Tour.");
      return;
    }

    if (!form.startTime || !form.endTime) {
      setError("Vui lòng nhập đầy đủ thời gian bắt đầu và kết thúc.");
      return;
    }

    if (form.startTime >= form.endTime) {
      setError("Thời gian bắt đầu phải diễn ra trước thời gian kết thúc.");
      return;
    }

    if (arriveAt && form.startTime + ":00" < arriveAt) {
      setError(
        `Thời gian bắt đầu không được sớm hơn giờ tàu cập cảng (${formatDisplayDateTime(arriveAt)}).`,
      );
      return;
    }

    if (leaveAt && form.endTime + ":00" > leaveAt) {
      setError(
        `Thời gian kết thúc không được muộn hơn giờ tàu rời cảng (${formatDisplayDateTime(leaveAt)}).`,
      );
      return;
    }

    if (!form.maxPassengers || Number(form.maxPassengers) <= 0) {
      setError("Số lượng khách tối đa phải lớn hơn 0.");
      return;
    }

    if (form.price === "" || Number(form.price) < 0) {
      setError("Giá tiền phải lớn hơn hoặc bằng 0.");
      return;
    }

    const payload = {
      name: form.name.trim(),
      description: form.description.trim() || null,
      startTime: form.startTime + ":00",
      endTime: form.endTime + ":00",
      maxPassengers: Number(form.maxPassengers),
      price: Number(form.price),
    };

    try {
      await onSubmit(payload, selectedUserIds);
    } catch (submitError) {
      console.error("SUBMIT VISIT TOUR ERROR:", submitError);

      setError(
        submitError?.response?.data?.message ||
          submitError?.message ||
          "Không thể lưu Visit Tour hoặc phân công nhân viên.",
      );
    }
  };

  return (
    <div
      className="visit-tour-modal-overlay"
      onMouseDown={(event) => {
        if (event.target === event.currentTarget && !loading) {
          onClose();
        }
      }}
    >
      <div className="visit-tour-modal">
        <div className="visit-tour-modal-header">
          <div>
            <h2>{isEdit ? "Chỉnh sửa Visit Tour" : "Cấu hình Visit Tour"}</h2>
            <p>{stopName ? `Điểm dừng: ${stopName}` : ""}</p>
          </div>

          <button
            type="button"
            onClick={onClose}
            disabled={loading}
            title="Đóng"
          >
            <X size={20} />
          </button>
        </div>

        <div className="visit-tour-modal-notice-box">
          {scheduleDate && (
            <div className="notice-row">
              <Calendar size={15} className="notice-icon" />
              <span>Ngày lịch trình:</span>
              <strong>{scheduleDate}</strong>
            </div>
          )}

          {arriveAt && leaveAt && (
            <div className="notice-row">
              <Clock size={15} className="notice-icon" />
              <span>Khung giờ cập cảng cho phép:</span>

              <div className="time-badge-group">
                <span className="time-badge">
                  {formatDisplayDateTime(arriveAt)}
                </span>
                <span className="separator">→</span>
                <span className="time-badge">
                  {formatDisplayDateTime(leaveAt)}
                </span>
              </div>
            </div>
          )}

          <div className="notice-hint">
            <Info size={13} />
            <span>
              Hệ thống tự động điền thời gian theo lịch cập cảng. Bạn có thể
              điều chỉnh lại thời gian cho phù hợp.
            </span>
          </div>
        </div>

        {error && (
          <div className="visit-tour-modal-error">
            <AlertCircle size={16} />
            <span>{error}</span>
          </div>
        )}

        <form className="visit-tour-form" onSubmit={handleSubmit}>
          <div className="visit-tour-form-field">
            <label htmlFor="visit-tour-name">Tên Visit Tour</label>
            <input
              id="visit-tour-name"
              name="name"
              type="text"
              value={form.name}
              onChange={handleChange}
              placeholder="Nhập tên hoạt động tham quan"
              maxLength={200}
              disabled={loading}
            />
          </div>

          <div className="visit-tour-form-field">
            <label htmlFor="visit-tour-description">Mô tả</label>
            <textarea
              id="visit-tour-description"
              name="description"
              value={form.description}
              onChange={handleChange}
              placeholder="Nhập mô tả hoạt động..."
              rows={2}
              disabled={loading}
            />
          </div>

          <div className="visit-tour-form-row">
            <div className="visit-tour-form-field">
              <label htmlFor="visit-tour-start-time">
                Thời gian bắt đầu tham quan
              </label>
              <input
                id="visit-tour-start-time"
                name="startTime"
                type="datetime-local"
                value={form.startTime}
                min={toInputDateTime(arriveAt)}
                max={toInputDateTime(leaveAt)}
                onChange={handleChange}
                disabled={loading}
              />
            </div>

            <div className="visit-tour-form-field">
              <label htmlFor="visit-tour-end-time">
                Thời gian kết thúc tham quan
              </label>
              <input
                id="visit-tour-end-time"
                name="endTime"
                type="datetime-local"
                value={form.endTime}
                min={toInputDateTime(arriveAt)}
                max={toInputDateTime(leaveAt)}
                onChange={handleChange}
                disabled={loading}
              />
            </div>
          </div>

          <div className="visit-tour-form-row">
            <div className="visit-tour-form-field">
              <label htmlFor="visit-tour-max-passengers">Số khách tối đa</label>
              <input
                id="visit-tour-max-passengers"
                name="maxPassengers"
                type="number"
                min="1"
                value={form.maxPassengers}
                onChange={handleChange}
                placeholder="VD: 30"
                disabled={loading}
              />
            </div>

            <div className="visit-tour-form-field">
              <label htmlFor="visit-tour-price">Giá (VND)</label>
              <input
                id="visit-tour-price"
                name="price"
                type="number"
                min="0"
                step="1000"
                value={form.price}
                onChange={handleChange}
                placeholder="VD: 500000"
                disabled={loading}
              />
            </div>
          </div>

          {/* PHÂN CÔNG NHÂN VIÊN SHORE */}
          <section className="shore-staff-assignment">
            <div className="shore-staff-assignment-heading">
              <Users size={20} />
              <div>
                <h3>Phân công nhân viên Shore</h3>
                <p>
                  Chọn nhân viên phụ trách Visit Tour này. Có thể chọn nhiều
                  người.
                </p>
              </div>
            </div>

            {!isEdit && (
              <div className="shore-staff-info">
                <Info size={15} />
                <span>
                  Visit Tour sẽ được tạo trước, sau đó hệ thống lưu phân công
                  nhân viên.
                </span>
              </div>
            )}

            {staffError && (
              <div className="shore-staff-error">
                <AlertCircle size={16} />
                <span>{staffError}</span>
                <button
                  type="button"
                  disabled={staffLoading || loading}
                  onClick={() => {
                    // Tải lại dữ liệu bằng cách gọi API trực tiếp.
                    setStaffError("");
                    setStaffLoading(true);

                    Promise.all([
                      visitTourService.getShoreStaff(),
                      visitTour?.id
                        ? visitTourService.getAssignments(visitTour.id)
                        : Promise.resolve([]),
                    ])
                      .then(([staffData, assignmentData]) => {
                        setStaffList(
                          (Array.isArray(staffData) ? staffData : []).filter(
                            (staff) =>
                              staff?.userId && staff.role === "SHORE_STAFF",
                          ),
                        );
                        setAssignments(
                          Array.isArray(assignmentData) ? assignmentData : [],
                        );
                      })
                      .catch((err) => {
                        setStaffError(
                          err.response?.data?.message ||
                            "Không thể tải lại dữ liệu nhân viên.",
                        );
                      })
                      .finally(() => setStaffLoading(false));
                  }}
                >
                  Thử lại
                </button>
              </div>
            )}

            {isEdit && (
              <div className="shore-staff-current">
                <h4>Đã được phân công ({assignments.length})</h4>

                {assignmentLoading ? (
                  <p className="shore-staff-muted">
                    <RefreshCw size={15} className="shore-staff-spinner" />
                    Đang tải phân công...
                  </p>
                ) : assignments.length === 0 ? (
                  <p className="shore-staff-muted">
                    Chưa có nhân viên được phân công.
                  </p>
                ) : (
                  <div className="shore-staff-list">
                    {assignments.map((assignment) => (
                      <div
                        className="shore-staff-current-item"
                        key={assignment.id}
                      >
                        <UserRound size={19} />

                        <div className="shore-staff-details">
                          <strong>
                            {assignment.fullName ||
                              assignment.username ||
                              assignment.userId}
                          </strong>
                          <span>{assignment.email || "Không có email"}</span>
                        </div>

                        <span
                          className={`shore-staff-status shore-staff-status-${String(
                            assignment.status || "PENDING",
                          ).toLowerCase()}`}
                        >
                          {assignment.status || "PENDING"}
                        </span>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            )}

            <div className="shore-staff-available">
              <h4>
                Chọn nhân viên bổ sung
                {selectedUserIds.length > 0 &&
                  ` (${selectedUserIds.length} đã chọn)`}
              </h4>

              {staffLoading ? (
                <p className="shore-staff-muted">
                  <RefreshCw size={15} className="shore-staff-spinner" />
                  Đang tải danh sách nhân viên...
                </p>
              ) : staffError && staffList.length === 0 ? (
                <p className="shore-staff-muted">
                  Không thể tải danh sách. Bạn có thể lưu cấu hình Visit Tour
                  rồi thử tải nhân viên lại.
                </p>
              ) : availableStaff.length === 0 ? (
                <p className="shore-staff-muted">
                  {staffList.length === 0
                    ? "Chưa có nhân viên SHORE_STAFF nào trong danh sách."
                    : "Không còn nhân viên chưa được phân công."}
                </p>
              ) : (
                <div className="shore-staff-list">
                  {availableStaff.map((staff) => {
                    const checked = selectedUserIds.includes(staff.userId);

                    return (
                      <label
                        key={staff.userId}
                        className={`shore-staff-option ${
                          checked ? "selected" : ""
                        }`}
                      >
                        <input
                          type="checkbox"
                          checked={checked}
                          disabled={loading}
                          onChange={() => toggleStaff(staff.userId)}
                        />

                        <UserRound size={19} />

                        <div className="shore-staff-details">
                          <strong>
                            {staff.fullName || staff.username || staff.userId}
                          </strong>
                          <span>{staff.email || "Không có email"}</span>
                          {staff.phone && <span>{staff.phone}</span>}
                        </div>
                      </label>
                    );
                  })}
                </div>
              )}
            </div>

            {selectedUserIds.length > 0 && (
              <p className="shore-staff-selection-summary">
                Đã chọn thêm {selectedUserIds.length} nhân viên cho Visit Tour.
              </p>
            )}
          </section>

          <div className="visit-tour-form-actions">
            <button
              type="button"
              onClick={onClose}
              disabled={loading}
              className="btn-cancel"
            >
              Hủy
            </button>

            <button type="submit" disabled={loading} className="btn-submit">
              {loading
                ? "Đang lưu cấu hình và phân công..."
                : isEdit
                  ? "Lưu cấu hình và phân công"
                  : "Xác nhận cấu hình và phân công"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default VisitTourFormModal;
