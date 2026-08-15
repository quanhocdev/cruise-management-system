import { useEffect, useState } from "react";
import { X, Save } from "lucide-react";

const initialForm = {
  code: "",
  name: "",
  description: "",
  startDate: "",
  endDate: "",
};

function TourFormModal({
  open,
  tour = null,
  loading = false,
  onClose,
  onSubmit,
}) {
  const [form, setForm] = useState(initialForm);
  const [errors, setErrors] = useState({});

  const isEdit = Boolean(tour);

  // =====================================================
  // LOAD FORM DATA
  // =====================================================

  useEffect(() => {
    if (!open) {
      return;
    }

    if (tour) {
      setForm({
        code: tour.code || "",
        name: tour.name || "",
        description: tour.description || "",
        startDate: tour.startDate || "",
        endDate: tour.endDate || "",
      });
    } else {
      setForm(initialForm);
    }

    setErrors({});
  }, [open, tour]);

  // =====================================================
  // CLOSE MODAL
  // =====================================================

  if (!open) {
    return null;
  }

  // =====================================================
  // HANDLE CHANGE
  // =====================================================

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

  // =====================================================
  // VALIDATION
  // =====================================================

  const validate = () => {
    const nextErrors = {};

    // Code
    if (!form.code.trim()) {
      nextErrors.code = "Vui lòng nhập mã tour.";
    }

    // Name
    if (!form.name.trim()) {
      nextErrors.name = "Vui lòng nhập tên tour.";
    }

    // Start date
    if (!form.startDate) {
      nextErrors.startDate = "Vui lòng chọn ngày bắt đầu.";
    }

    // End date
    if (!form.endDate) {
      nextErrors.endDate = "Vui lòng chọn ngày kết thúc.";
    }

    // Date range
    if (form.startDate && form.endDate && form.endDate < form.startDate) {
      nextErrors.endDate = "Ngày kết thúc phải lớn hơn hoặc bằng ngày bắt đầu.";
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

    const data = {
      code: form.code.trim(),
      name: form.name.trim(),
      description: form.description.trim() || null,
      startDate: form.startDate,
      endDate: form.endDate,
    };

    console.log("SUBMIT TOUR:", data);

    await onSubmit?.(data);
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
            <h2>{isEdit ? "Chỉnh sửa tour" : "Tạo tour mới"}</h2>

            <p>
              {isEdit
                ? "Cập nhật thông tin cơ bản của tour."
                : "Tạo thông tin tour trước khi xây dựng lịch trình."}
            </p>
          </div>

          <button
            type="button"
            className="tour-modal-close"
            onClick={onClose}
            disabled={loading}
            title="Đóng"
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
                BASIC INFORMATION
               ================================================= */}

            <div className="tour-form-grid">
              {/* CODE */}

              <div className="form-group">
                <label>
                  Mã tour <span>*</span>
                </label>

                <input
                  type="text"
                  name="code"
                  value={form.code}
                  onChange={handleChange}
                  placeholder="Ví dụ: TOUR-HCM-01"
                  maxLength={50}
                  disabled={loading}
                />

                {errors.code && (
                  <small className="form-error">{errors.code}</small>
                )}
              </div>

              {/* NAME */}

              <div className="form-group">
                <label>
                  Tên tour <span>*</span>
                </label>

                <input
                  type="text"
                  name="name"
                  value={form.name}
                  onChange={handleChange}
                  placeholder="Ví dụ: Tour Hồ Chí Minh - Singapore"
                  maxLength={200}
                  disabled={loading}
                />

                {errors.name && (
                  <small className="form-error">{errors.name}</small>
                )}
              </div>

              {/* START DATE */}

              <div className="form-group">
                <label>
                  Ngày bắt đầu <span>*</span>
                </label>

                <input
                  type="date"
                  name="startDate"
                  value={form.startDate}
                  onChange={handleChange}
                  disabled={loading}
                />

                {errors.startDate && (
                  <small className="form-error">{errors.startDate}</small>
                )}
              </div>

              {/* END DATE */}

              <div className="form-group">
                <label>
                  Ngày kết thúc <span>*</span>
                </label>

                <input
                  type="date"
                  name="endDate"
                  value={form.endDate}
                  onChange={handleChange}
                  min={form.startDate || undefined}
                  disabled={loading}
                />

                {errors.endDate && (
                  <small className="form-error">{errors.endDate}</small>
                )}
              </div>
            </div>

            {/* =================================================
                DESCRIPTION
               ================================================= */}

            <div className="form-group">
              <label>Mô tả</label>

              <textarea
                name="description"
                value={form.description}
                onChange={handleChange}
                placeholder="Mô tả tổng quan về tour..."
                rows={5}
                maxLength={5000}
                disabled={loading}
              />

              <small className="form-helper">Tối đa 5000 ký tự.</small>
            </div>

            {/* =================================================
                NOTICE
               ================================================= */}

            <div className="tour-form-notice">
              <strong>Lưu ý:</strong>

              <span>
                Du thuyền và thời gian mở/đóng đăng ký sẽ được Operation cấu
                hình sau khi tour được tạo.
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

            <button type="submit" className="primary-button" disabled={loading}>
              <Save size={17} />

              <span>
                {loading ? "Đang lưu..." : isEdit ? "Lưu thay đổi" : "Tạo tour"}
              </span>
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default TourFormModal;
