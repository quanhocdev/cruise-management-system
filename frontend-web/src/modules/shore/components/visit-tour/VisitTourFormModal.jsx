// src/modules/shore/components/visit-tour/VisitTourFormModal.jsx
import { useEffect, useState } from "react";
import { X } from "lucide-react";
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
  if (!value) {
    return "";
  }

  /*
   * Backend trả:
   *
   * 2026-10-23T08:00:00
   *
   * HTML datetime-local cần:
   *
   * 2026-10-23T08:00
   */

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

  /*
   * =====================================================
   * INITIAL FORM
   * =====================================================
   */

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

    setForm(EMPTY_FORM);
  }, [visitTour]);

  /*
   * =====================================================
   * CHANGE
   * =====================================================
   */

  const handleChange = (event) => {
    const { name, value } = event.target;

    setForm((previous) => ({
      ...previous,
      [name]: value,
    }));
  };

  /*
   * =====================================================
   * SUBMIT
   * =====================================================
   */

  const handleSubmit = async (event) => {
    event.preventDefault();

    setError("");

    /*
     * Frontend validation cơ bản.
     * Backend vẫn là nơi validate chính.
     */

    if (!form.name.trim()) {
      setError("Vui lòng nhập tên Visit Tour.");

      return;
    }

    if (!form.startTime || !form.endTime) {
      setError("Vui lòng nhập thời gian bắt đầu và kết thúc.");

      return;
    }

    if (form.startTime >= form.endTime) {
      setError("Thời gian bắt đầu phải trước thời gian kết thúc.");

      return;
    }

    if (!form.maxPassengers || Number(form.maxPassengers) <= 0) {
      setError("Số lượng khách phải lớn hơn 0.");

      return;
    }

    if (form.price === "" || Number(form.price) < 0) {
      setError("Giá phải lớn hơn hoặc bằng 0.");

      return;
    }

    /*
     * Convert dữ liệu về đúng DTO backend.
     *
     * CREATE:
     *
     * {
     *   name,
     *   description,
     *   startTime,
     *   endTime,
     *   maxPassengers,
     *   price
     * }
     *
     * UPDATE:
     * cũng gửi những field này.
     *
     * Không gửi status khi create.
     */

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

  /*
   * =====================================================
   * STOP INFO
   * =====================================================
   */

  const stopName = scheduleStop?.portName || visitTour?.portName || "";

  const arriveAt = scheduleStop?.arriveAt || visitTour?.arriveAt || "";

  const leaveAt = scheduleStop?.leaveAt || visitTour?.leaveAt || "";

  /*
   * =====================================================
   * RENDER
   * =====================================================
   */

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
        {/* =================================================
            HEADER
            ================================================= */}

        <div className="visit-tour-modal-header">
          <div>
            <h2>{isEdit ? "Chỉnh sửa Visit Tour" : "Thêm Visit Tour"}</h2>

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

        {/* =================================================
            STOP TIME
            ================================================= */}

        {arriveAt && leaveAt && (
          <div className="visit-tour-modal-stop-time">
            <span>Thời gian tàu:</span>

            <strong>{arriveAt}</strong>

            <span>→</span>

            <strong>{leaveAt}</strong>
          </div>
        )}

        {/* =================================================
            ERROR
            ================================================= */}

        {error && <div className="visit-tour-modal-error">{error}</div>}

        {/* =================================================
            FORM
            ================================================= */}

        <form className="visit-tour-form" onSubmit={handleSubmit}>
          {/* NAME */}

          <div className="visit-tour-form-field">
            <label htmlFor="visit-tour-name">Tên Visit Tour</label>

            <input
              id="visit-tour-name"
              name="name"
              type="text"
              value={form.name}
              onChange={handleChange}
              placeholder="Nhập tên Visit Tour"
              maxLength={200}
              disabled={loading}
            />
          </div>

          {/* DESCRIPTION */}

          <div className="visit-tour-form-field">
            <label htmlFor="visit-tour-description">Mô tả</label>

            <textarea
              id="visit-tour-description"
              name="description"
              value={form.description}
              onChange={handleChange}
              placeholder="Nhập mô tả"
              rows={4}
              disabled={loading}
            />
          </div>

          {/* TIME */}

          <div className="visit-tour-form-row">
            <div className="visit-tour-form-field">
              <label htmlFor="visit-tour-start-time">Bắt đầu</label>

              <input
                id="visit-tour-start-time"
                name="startTime"
                type="datetime-local"
                value={form.startTime}
                onChange={handleChange}
                disabled={loading}
              />
            </div>

            <div className="visit-tour-form-field">
              <label htmlFor="visit-tour-end-time">Kết thúc</label>

              <input
                id="visit-tour-end-time"
                name="endTime"
                type="datetime-local"
                value={form.endTime}
                onChange={handleChange}
                disabled={loading}
              />
            </div>
          </div>

          {/* PASSENGERS + PRICE */}

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
                placeholder="30"
                disabled={loading}
              />
            </div>

            <div className="visit-tour-form-field">
              <label htmlFor="visit-tour-price">Giá</label>

              <input
                id="visit-tour-price"
                name="price"
                type="number"
                min="0"
                step="1000"
                value={form.price}
                onChange={handleChange}
                placeholder="500000"
                disabled={loading}
              />
            </div>
          </div>

          {/* =================================================
              ACTIONS
              ================================================= */}

          <div className="visit-tour-form-actions">
            <button type="button" onClick={onClose} disabled={loading}>
              Hủy
            </button>

            <button type="submit" disabled={loading}>
              {loading
                ? "Đang lưu..."
                : isEdit
                  ? "Lưu thay đổi"
                  : "Tạo Visit Tour"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default VisitTourFormModal;
