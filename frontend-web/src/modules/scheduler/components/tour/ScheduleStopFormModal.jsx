import { useEffect, useState } from "react";
import { X, Save, MapPin } from "lucide-react";

const initialForm = {
  portId: "",
  stopOrder: 1,
  arriveAt: "",
  leaveAt: "",
};

const toInputDateTime = (value) => {
  if (!value) return "";

  return value.length >= 16 ? value.substring(0, 16) : value;
};

function ScheduleStopFormModal({
  open,
  stop = null,
  ports = [],
  loading = false,
  onClose,
  onSubmit,
}) {
  const [form, setForm] = useState(initialForm);
  const [errors, setErrors] = useState({});

  const isEdit = Boolean(stop);

  useEffect(() => {
    if (!open) return;

    if (stop) {
      setForm({
        portId: stop.portId || "",
        stopOrder: stop.stopOrder || 1,
        arriveAt: toInputDateTime(stop.arriveAt),
        leaveAt: toInputDateTime(stop.leaveAt),
      });
    } else {
      setForm(initialForm);
    }

    setErrors({});
  }, [open, stop]);

  if (!open) return null;

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

  const validate = () => {
    const nextErrors = {};

    if (!form.portId) {
      nextErrors.portId = "Vui lòng chọn cảng.";
    }

    if (!form.stopOrder || form.stopOrder < 1) {
      nextErrors.stopOrder = "Thứ tự phải lớn hơn hoặc bằng 1.";
    }

    if (!form.arriveAt) {
      nextErrors.arriveAt = "Vui lòng chọn giờ đến.";
    }

    if (!form.leaveAt) {
      nextErrors.leaveAt = "Vui lòng chọn giờ rời.";
    }

    if (
      form.arriveAt &&
      form.leaveAt &&
      new Date(form.leaveAt) <= new Date(form.arriveAt)
    ) {
      nextErrors.leaveAt = "Giờ rời phải sau giờ đến.";
    }

    setErrors(nextErrors);

    return Object.keys(nextErrors).length === 0;
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    if (!validate()) return;

    await onSubmit?.({
      portId: form.portId,
      stopOrder: form.stopOrder,
      arriveAt: form.arriveAt,
      leaveAt: form.leaveAt,
    });
  };

  return (
    <div className="tour-modal-overlay" onMouseDown={onClose}>
      <div
        className="tour-modal"
        onMouseDown={(event) => event.stopPropagation()}
      >
        <div className="tour-modal-header">
          <div>
            <h2>{isEdit ? "Chỉnh sửa điểm dừng" : "Thêm điểm dừng"}</h2>

            <p>
              Chọn cảng đã được Admin cấu hình và thiết lập thời gian ghé cảng.
            </p>
          </div>

          <button type="button" className="tour-modal-close" onClick={onClose}>
            <X size={20} />
          </button>
        </div>

        <form onSubmit={handleSubmit}>
          <div className="tour-modal-body">
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

            <div className="tour-form-grid">
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

              <div />

              <div className="form-group">
                <label>
                  Giờ đến <span>*</span>
                </label>

                <input
                  type="datetime-local"
                  name="arriveAt"
                  value={form.arriveAt}
                  onChange={handleChange}
                  disabled={loading}
                />

                {errors.arriveAt && (
                  <small className="form-error">{errors.arriveAt}</small>
                )}
              </div>

              <div className="form-group">
                <label>
                  Giờ rời <span>*</span>
                </label>

                <input
                  type="datetime-local"
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

            <div className="tour-form-notice">
              <strong>Lưu ý:</strong>

              <span>
                Cảng chỉ được chọn từ danh sách cảng đã được Admin tạo trong hệ
                thống.
              </span>
            </div>
          </div>

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
