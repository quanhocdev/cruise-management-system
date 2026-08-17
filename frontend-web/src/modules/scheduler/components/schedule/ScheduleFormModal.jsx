import { useEffect, useState } from "react";
import { X, Save, CalendarDays } from "lucide-react";

const initialForm = {
  name: "",
  description: "",
};

function ScheduleFormModal({
  open,
  schedule = null,
  dayNumber = null,
  realDay = "",
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
      });
    } else {
      setForm(initialForm);
    }

    setErrors({});
  }, [open, schedule]);

  if (!open) {
    return null;
  }

  const handleChange = (event) => {
    const { name, value } = event.target;

    setForm((prev) => ({
      ...prev,
      [name]: value,
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

    setErrors(nextErrors);

    return Object.keys(nextErrors).length === 0;
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    if (!validate()) {
      return;
    }

    await onSubmit?.({
      name: form.name.trim(),
      description: form.description.trim() || null,

      ...(isEdit
        ? {}
        : {
            dayNumber,
            realDay,
          }),
    });
  };

  const formatDate = (dateString) => {
    if (!dateString) {
      return "-";
    }

    const date = new Date(`${dateString}T00:00:00`);

    return date.toLocaleDateString("vi-VN", {
      weekday: "long",
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
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
            <h2>
              {isEdit ? "Chỉnh sửa lịch trình" : "Tạo lịch trình trong ngày"}
            </h2>

            <p>
              {isEdit
                ? "Cập nhật thông tin của lịch trình."
                : "Cấu hình nội dung cho ngày đã chọn trong tour."}
            </p>
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

        <form onSubmit={handleSubmit}>
          <div className="tour-modal-body">
            {!isEdit && (
              <div className="schedule-selected-day">
                <div className="schedule-selected-day-icon">
                  <CalendarDays size={20} />
                </div>

                <div>
                  <span>Ngày trong tour</span>

                  <strong>
                    Ngày {dayNumber} — {formatDate(realDay)}
                  </strong>
                </div>
              </div>
            )}

            {isEdit && schedule && (
              <div className="schedule-selected-day">
                <div className="schedule-selected-day-icon">
                  <CalendarDays size={20} />
                </div>

                <div>
                  <span>Ngày trong tour</span>

                  <strong>
                    Ngày {schedule.dayNumber} — {formatDate(schedule.realDay)}
                  </strong>
                </div>
              </div>
            )}

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
                maxLength={150}
                disabled={loading}
              />

              {errors.name && (
                <small className="form-error">{errors.name}</small>
              )}
            </div>

            <div className="form-group">
              <label>Mô tả</label>

              <textarea
                name="description"
                value={form.description}
                onChange={handleChange}
                placeholder="Mô tả tổng quan lịch trình trong ngày..."
                rows={5}
                disabled={loading}
              />
            </div>

            <div className="tour-form-notice">
              <strong>Lưu ý:</strong>

              <span>
                Ngày và thứ tự ngày được xác định tự động dựa trên thời gian của
                tour. Scheduler không cần nhập lại ngày thực tế.
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

            <button type="submit" className="primary-button" disabled={loading}>
              <Save size={17} />

              {loading
                ? "Đang lưu..."
                : isEdit
                  ? "Lưu thay đổi"
                  : "Tạo lịch trình"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default ScheduleFormModal;
