// src/modules/shore/components/visit-tour/VisitTourFormModal.jsx
import { useEffect, useState } from "react";
import { X, AlertCircle, Clock, Calendar, Info } from "lucide-react";
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

// Hàm format hiển thị ngày giờ đẹp mắt (VD: 10/10/2026 08:00)
function formatDisplayDateTime(dateTimeStr) {
  if (!dateTimeStr) return "—";
  try {
    const [datePart, timePart] = dateTimeStr.split("T");
    if (!datePart || !timePart) return dateTimeStr;
    const [year, month, day] = datePart.split("-");
    const timeOnly = timePart.substring(0, 5);
    return `${day}/${month}/${year} lúc ${timeOnly}`;
  } catch (e) {
    return dateTimeStr;
  }
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
      return;
    }

    // Trạng thái gán đủ ban đầu theo giờ cập cảng chuẩn
    setForm({
      ...EMPTY_FORM,
      startTime: toInputDateTime(arriveAt),
      endTime: toInputDateTime(leaveAt),
    });
  }, [visitTour, arriveAt, leaveAt]);

  const handleChange = (event) => {
    const { name, value } = event.target;
    setForm((previous) => ({
      ...previous,
      [name]: value,
    }));
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

    // Kiểm tra ràng buộc bắt buộc nằm trong khoảng giờ tàu cập bến
    if (arriveAt && form.startTime + ":00" < arriveAt) {
      setError(
        `Thời gian bắt đầu không được sớm hơn giờ tàu cập bến (${formatDisplayDateTime(arriveAt)}).`,
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
      await onSubmit(payload);
    } catch (submitError) {
      console.error("SUBMIT VISIT TOUR ERROR:", submitError);
      const message =
        submitError?.response?.data?.message ||
        submitError?.message ||
        "Không thể lưu Visit Tour.";
      setError(message);
    }
  };

  return (
    <div
      className="visit-tour-modal-overlay"
      onMouseDown={(event) => {
        if (event.target === event.currentTarget) {
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

        {/* KHU VỰC THÔNG TIN CẬP CẢNG KÈM DÒNG CHÚ THÍCH CÓ THỂ ĐIỀU CHỈNH */}
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
              Hệ thống tự động điền theo khung giờ cập cảng, bạn có thể tự điều
              chỉnh lại thời gian bên dưới cho phù hợp.
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
                ? "Đang lưu..."
                : isEdit
                  ? "Lưu thay đổi"
                  : "Xác nhận cấu hình"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default VisitTourFormModal;
