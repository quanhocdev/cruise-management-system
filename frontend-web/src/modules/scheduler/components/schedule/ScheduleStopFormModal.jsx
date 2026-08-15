import { useEffect, useState } from "react";
import { X, Save, MapPin } from "lucide-react";

const initialForm = {
  portId: "",
  stopOrder: 1,
  arriveAt: "",
  leaveAt: "",
};

const toTimeInput = (value) => {
  if (!value) {
    return "";
  }

  const stringValue = String(value);

  // Backend:
  // 2026-08-20T08:30:00
  // => 08:30
  if (stringValue.includes("T")) {
    return stringValue.split("T")[1].substring(0, 5);
  }

  // Backend trả trực tiếp:
  // 08:30
  return stringValue.substring(0, 5);
};

const formatScheduleDate = (date) => {
  if (!date) {
    return "Chưa xác định";
  }

  const parts = String(date).substring(0, 10).split("-");

  if (parts.length !== 3) {
    return date;
  }

  const [year, month, day] = parts;

  if (!year || !month || !day) {
    return date;
  }

  return `${day}/${month}/${year}`;
};

function ScheduleStopFormModal({
  open,
  stop = null,
  scheduleDate = "",
  ports = [],
  loading = false,
  onClose,
  onSubmit,
}) {
  const [form, setForm] = useState(initialForm);
  const [errors, setErrors] = useState({});

  const isEdit = Boolean(stop);

  // =====================================================
  // LOAD FORM
  // =====================================================

  useEffect(() => {
    if (!open) {
      return;
    }

    if (stop) {
      setForm({
        portId: stop.portId || "",
        stopOrder: stop.stopOrder || 1,
        arriveAt: toTimeInput(stop.arriveAt),
        leaveAt: toTimeInput(stop.leaveAt),
      });
    } else {
      setForm({
        ...initialForm,
        stopOrder: 1,
      });
    }

    setErrors({});
  }, [open, stop]);

  if (!open) {
    return null;
  }

  // =====================================================
  // CHANGE
  // =====================================================

  const handleChange = (event) => {
    const { name, value } = event.target;

    setForm((prev) => ({
      ...prev,
      [name]: name === "stopOrder" ? Number(value) : value,
    }));

    setErrors((prev) => ({
      ...prev,
      [name]: undefined,
    }));
  };

  // =====================================================
  // VALIDATE
  // =====================================================

  const validate = () => {
    const nextErrors = {};

    if (!form.portId) {
      nextErrors.portId = "Vui lòng chọn cảng.";
    }

    if (!form.stopOrder || form.stopOrder < 1) {
      nextErrors.stopOrder = "Thứ tự phải lớn hơn hoặc bằng 1.";
    }

    if (!form.arriveAt) {
      nextErrors.arriveAt = "Vui lòng nhập giờ đến.";
    }

    if (!form.leaveAt) {
      nextErrors.leaveAt = "Vui lòng nhập giờ rời.";
    }

    if (form.arriveAt && form.leaveAt && form.leaveAt <= form.arriveAt) {
      nextErrors.leaveAt = "Giờ rời phải sau giờ đến.";
    }

    if (!scheduleDate) {
      nextErrors.scheduleDate = "Không xác định được ngày của lịch trình.";
    }

    setErrors(nextErrors);

    return Object.keys(nextErrors).length === 0;
  };

  // =====================================================
  // SUBMIT
  // =====================================================

  const handleSubmit = async (event) => {
    event.preventDefault();

    if (!validate()) {
      return;
    }

    /*
     * Người dùng chỉ nhập:
     *
     * Giờ đến: 08:00
     * Giờ rời: 10:30
     *
     * Ngày lấy tự động:
     *
     * scheduleDate = 2026-08-20
     *
     * Gửi backend:
     *
     * 2026-08-20T08:00:00
     * 2026-08-20T10:30:00
     */

    const arriveAt = `${scheduleDate}T${form.arriveAt}:00`;
    const leaveAt = `${scheduleDate}T${form.leaveAt}:00`;

    await onSubmit?.({
      portId: form.portId,
      stopOrder: form.stopOrder,
      arriveAt,
      leaveAt,
    });
  };

  // =====================================================
  // RENDER
  // =====================================================

  return (
    <div className="tour-modal-overlay" onMouseDown={onClose}>
      <div
        className="tour-modal"
        onMouseDown={(event) => event.stopPropagation()}
      >
        {/* =================================================
            HEADER
           ================================================= */}

        <div className="tour-modal-header">
          <div>
            <h2>{isEdit ? "Chỉnh sửa điểm dừng" : "Thêm điểm dừng"}</h2>

            <p>Chọn cảng và thiết lập thời gian tàu đến, rời cảng.</p>
          </div>

          <button
            type="button"
            className="tour-modal-close"
            onClick={onClose}
            disabled={loading}
          >
            <X size={20} />
          </button>
        </div>

        {/* =================================================
            FORM
           ================================================= */}

        <form onSubmit={handleSubmit}>
          <div className="tour-modal-body">
            {/* =================================================
                CẢNG
               ================================================= */}

            <div className="form-group">
              <label>
                Cảng <span>*</span>
              </label>

              <div className="select-with-icon">
                <MapPin size={17} />

                <select
                  name="portId"
                  value={form.portId}
                  onChange={handleChange}
                  disabled={loading}
                >
                  <option value="">-- Chọn cảng --</option>

                  {ports.map((port) => (
                    <option key={port.id} value={port.id}>
                      {port.name}

                      {port.city ? ` - ${port.city}` : ""}

                      {port.country ? `, ${port.country}` : ""}
                    </option>
                  ))}
                </select>
              </div>

              {errors.portId && (
                <small className="form-error">{errors.portId}</small>
              )}

              {!ports.length && (
                <small className="form-hint">
                  Chưa có cảng. Vui lòng liên hệ Admin để cấu hình cảng trước.
                </small>
              )}
            </div>

            {/* =================================================
                NGÀY LỊCH TRÌNH
               ================================================= */}

            <div className="form-group">
              <label>Ngày lịch trình</label>

              <input
                type="text"
                value={formatScheduleDate(scheduleDate)}
                disabled
                readOnly
              />

              <small className="form-hint">
                Ngày được lấy tự động từ lịch trình, không cần nhập lại.
              </small>

              {errors.scheduleDate && (
                <small className="form-error">{errors.scheduleDate}</small>
              )}
            </div>

            {/* =================================================
                THỨ TỰ
               ================================================= */}

            <div className="form-group">
              <label>
                Thứ tự điểm dừng <span>*</span>
              </label>

              <input
                type="number"
                name="stopOrder"
                min="1"
                value={form.stopOrder}
                onChange={handleChange}
                disabled={loading}
              />

              {errors.stopOrder && (
                <small className="form-error">{errors.stopOrder}</small>
              )}
            </div>

            {/* =================================================
                THỜI GIAN
               ================================================= */}

            <div className="tour-form-grid">
              {/* GIỜ ĐẾN */}

              <div className="form-group">
                <label>
                  Giờ đến <span>*</span>
                </label>

                <input
                  type="time"
                  name="arriveAt"
                  value={form.arriveAt}
                  onChange={handleChange}
                  disabled={loading}
                />

                {errors.arriveAt && (
                  <small className="form-error">{errors.arriveAt}</small>
                )}
              </div>

              {/* GIỜ RỜI */}

              <div className="form-group">
                <label>
                  Giờ rời <span>*</span>
                </label>

                <input
                  type="time"
                  name="leaveAt"
                  value={form.leaveAt}
                  onChange={handleChange}
                  disabled={loading}
                />

                {errors.leaveAt && (
                  <small className="form-error">{errors.leaveAt}</small>
                )}
              </div>
            </div>

            {/* =================================================
                NOTICE
               ================================================= */}

            <div className="tour-form-notice">
              <strong>Lưu ý:</strong>

              <span>
                Ngày của điểm dừng được lấy tự động theo ngày thực tế của lịch
                trình. Bạn chỉ cần nhập giờ đến và giờ rời.
              </span>
            </div>
          </div>

          {/* =================================================
              FOOTER
             ================================================= */}

          <div className="tour-modal-footer">
            <button
              type="button"
              className="secondary-button"
              onClick={onClose}
              disabled={loading}
            >
              Hủy
            </button>

            <button
              type="submit"
              className="primary-button"
              disabled={loading || !ports.length}
            >
              <Save size={17} />

              {loading ? "Đang lưu..." : "Lưu điểm dừng"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default ScheduleStopFormModal;
