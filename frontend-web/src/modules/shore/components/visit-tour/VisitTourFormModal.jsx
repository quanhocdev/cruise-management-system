// src/modules/shore/components/visit-tour/VisitTourFormModal.jsx
import { useEffect, useState } from "react";
import { X, AlertCircle, Clock } from "lucide-react";
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

  // Lấy thông tin thời gian cập cảng từ scheduleStop hoặc bản ghi visitTour hiện có
  const arriveAt = scheduleStop?.arriveAt || visitTour?.arriveAt || "";
  const leaveAt = scheduleStop?.leaveAt || visitTour?.leaveAt || "";
  const stopName = scheduleStop?.portName || visitTour?.portName || "";

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

    // Nếu tạo mới, tự động gợi ý điền sẵn thời gian bắt đầu bằng thời gian tàu cập bến (nếu có)
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

    // Kiểm tra ràng buộc không được lệch khỏi khung giờ cập cảng của tàu
    if (arriveAt && form.startTime + ":00" < arriveAt) {
      setError(
        `Thời gian bắt đầu không được sớm hơn giờ tàu cập bến (${arriveAt}).`,
      );
      return;
    }

    if (leaveAt && form.endTime + ":00" > leaveAt) {
      setError(
        `Thời gian kết thúc không được muộn hơn giờ tàu rời cảng (${leaveAt}).`,
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

        {/* HIỂN THỊ KHUNG GIỜ TÀU CẬP CẢNG ĐỂ NGƯỜI DÙNG DỄ THEO DÕI */}
        {arriveAt && leaveAt && (
          <div
            className="visit-tour-modal-stop-time"
            style={{
              display: "flex",
              alignItems: "center",
              gap: "8px",
              background: "var(--bg-accent, #f0f4f8)",
              padding: "10px 14px",
              borderRadius: "6px",
              margin: "12px 0",
              fontSize: "0.875rem",
            }}
          >
            <Clock size={16} color="var(--primary-color)" />
            <span>Khung giờ cập cảng cho phép:</span>
            <strong>{arriveAt}</strong>
            <span>→</span>
            <strong>{leaveAt}</strong>
          </div>
        )}

        {error && (
          <div
            className="visit-tour-modal-error"
            style={{ display: "flex", alignItems: "center", gap: "6px" }}
          >
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
              rows={3}
              disabled={loading}
            />
          </div>

          <div className="visit-tour-form-row">
            <div className="visit-tour-form-field">
              <label htmlFor="visit-tour-start-time">Thời gian bắt đầu</label>
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
              <label htmlFor="visit-tour-end-time">Thời gian kết thúc</label>
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
