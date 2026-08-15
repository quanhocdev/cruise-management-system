import { useEffect, useState } from "react";
import { X, Save } from "lucide-react";

const initialForm = {
  name: "",
  description: "",
  dayNumber: 1,
  realDay: "",
  status: "ACTIVE",
};

function ScheduleFormModal({
  open,
  schedule = null,
  loading = false,
  onClose,
  onSubmit,
}) {
  const [form, setForm] = useState(initialForm);
  const [errors, setErrors] = useState({});

  const isEdit = Boolean(schedule);

  useEffect(() => {
    if (!open) return;

    if (schedule) {
      setForm({
        name: schedule.name || "",
        description: schedule.description || "",
        dayNumber: schedule.dayNumber || 1,
        realDay: schedule.realDay || "",
        status: schedule.status || "ACTIVE",
      });
    } else {
      setForm(initialForm);
    }

    setErrors({});
  }, [open, schedule]);

  if (!open) return null;

  const handleChange = (event) => {
    const { name, value } = event.target;

    setForm((prev) => ({
      ...prev,
      [name]: name === "dayNumber" ? Number(value) : value,
    }));

    setErrors((prev) => ({
      ...prev,
      [name]: undefined,
    }));
  };

  const validate = () => {
    const nextErrors = {};

    if (!form.name.trim()) {
      nextErrors.name = "Vui lòng nhập tên lịch trình.";
    }

    if (!form.dayNumber || form.dayNumber < 1) {
      nextErrors.dayNumber = "Ngày phải lớn hơn hoặc bằng 1.";
    }

    if (!form.realDay) {
      nextErrors.realDay = "Vui lòng chọn ngày thực tế.";
    }

    setErrors(nextErrors);

    return Object.keys(nextErrors).length === 0;
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    if (!validate()) return;

    await onSubmit?.({
      name: form.name.trim(),
      description: form.description.trim() || null,
      dayNumber: form.dayNumber,
      realDay: form.realDay,
      status: form.status,
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
            <h2>{isEdit ? "Chỉnh sửa lịch trình" : "Thêm lịch trình"}</h2>

            <p>Cấu hình một ngày trong hành trình của tour.</p>
          </div>

          <button type="button" className="tour-modal-close" onClick={onClose}>
            <X size={20} />
          </button>
        </div>

        <form onSubmit={handleSubmit}>
          <div className="tour-modal-body">
            <div className="tour-form-grid">
              <div className="form-group">
                <label>
                  Tên lịch trình <span>*</span>
                </label>

                <input
                  type="text"
                  name="name"
                  value={form.name}
                  onChange={handleChange}
                  placeholder="Ví dụ: Ngày 1 - Khởi hành"
                  disabled={loading}
                />

                {errors.name && (
                  <small className="form-error">{errors.name}</small>
                )}
              </div>

              <div className="form-group">
                <label>
                  Ngày thứ <span>*</span>
                </label>

                <input
                  type="number"
                  name="dayNumber"
                  min="1"
                  value={form.dayNumber}
                  onChange={handleChange}
                  disabled={loading}
                />

                {errors.dayNumber && (
                  <small className="form-error">{errors.dayNumber}</small>
                )}
              </div>

              <div className="form-group">
                <label>
                  Ngày thực tế <span>*</span>
                </label>

                <input
                  type="date"
                  name="realDay"
                  value={form.realDay}
                  onChange={handleChange}
                  disabled={loading}
                />

                {errors.realDay && (
                  <small className="form-error">{errors.realDay}</small>
                )}
              </div>

              <div className="form-group">
                <label>Trạng thái</label>

                <select
                  name="status"
                  value={form.status}
                  onChange={handleChange}
                  disabled={loading}
                >
                  <option value="ACTIVE">Đang hoạt động</option>

                  <option value="INACTIVE">Không hoạt động</option>
                </select>
              </div>
            </div>

            <div className="form-group">
              <label>Mô tả</label>

              <textarea
                name="description"
                value={form.description}
                onChange={handleChange}
                placeholder="Mô tả hoạt động trong ngày..."
                rows={4}
                disabled={loading}
              />
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

            <button type="submit" className="primary-button" disabled={loading}>
              <Save size={17} />

              {loading ? "Đang lưu..." : "Lưu lịch trình"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default ScheduleFormModal;
