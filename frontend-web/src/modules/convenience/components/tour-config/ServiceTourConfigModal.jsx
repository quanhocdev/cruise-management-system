// src/modules/convenience/tour-config/ServiceTourConfigModal.jsx

import React, { useEffect, useState } from "react";
import { Clock3, Save, Users, Wrench, X } from "lucide-react";

import ServiceSelect from "./ServiceSelect";
import "../../styles/tour-config/ServiceTourConfigModal.css";

const ServiceTourConfigModal = ({
  assignment,
  onClose,
  onSubmit,
  submitting = false,
}) => {
  const [formData, setFormData] = useState({
    serviceId: "",
    maxPassengers: "",
    durationMinutes: "",
    unlimitedDuration: false,
  });

  const [errors, setErrors] = useState({});

  // =====================================================
  // RESET
  // =====================================================

  useEffect(() => {
    if (!assignment) {
      return;
    }

    const unlimited =
      assignment.durationMinutes == null || assignment.durationMinutes === 0;

    setFormData({
      serviceId: assignment.serviceId || "",

      maxPassengers:
        assignment.maxPassengers != null
          ? String(assignment.maxPassengers)
          : "",

      durationMinutes:
        !unlimited && assignment.durationMinutes != null
          ? String(assignment.durationMinutes)
          : "",

      unlimitedDuration: unlimited,
    });

    setErrors({});
  }, [assignment]);

  if (!assignment) {
    return null;
  }

  // =====================================================
  // CHANGE
  // =====================================================

  const handleChange = (event) => {
    const { name, value } = event.target;

    setFormData((previous) => ({
      ...previous,
      [name]: value,
    }));

    setErrors((previous) => ({
      ...previous,
      [name]: "",
    }));
  };

  // =====================================================
  // SERVICE
  // =====================================================

  const handleServiceChange = (serviceId) => {
    setFormData((previous) => ({
      ...previous,
      serviceId,
    }));

    setErrors((previous) => ({
      ...previous,
      serviceId: "",
    }));
  };

  // =====================================================
  // DURATION MODE
  // =====================================================

  const handleDurationModeChange = (event) => {
    const unlimited = event.target.value === "UNLIMITED";

    setFormData((previous) => ({
      ...previous,
      unlimitedDuration: unlimited,

      // Nếu chọn không giới hạn -> null khi submit
      // Nếu chuyển lại có giới hạn -> giữ giá trị cũ,
      // nếu chưa có thì mặc định 60 phút
      durationMinutes: unlimited ? "" : previous.durationMinutes || "60",
    }));

    setErrors((previous) => ({
      ...previous,
      durationMinutes: "",
    }));
  };

  // =====================================================
  // VALIDATE
  // =====================================================

  const validate = () => {
    const nextErrors = {};

    if (!formData.serviceId) {
      nextErrors.serviceId = "Vui lòng chọn dịch vụ";
    }

    if (!formData.maxPassengers || Number(formData.maxPassengers) <= 0) {
      nextErrors.maxPassengers = "Số hành khách tối đa phải lớn hơn 0";
    }

    // Chỉ validate duration khi KHÔNG chọn không giới hạn
    if (!formData.unlimitedDuration) {
      if (!formData.durationMinutes || Number(formData.durationMinutes) <= 0) {
        nextErrors.durationMinutes = "Thời lượng phải lớn hơn 0 phút";
      }
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

    const payload = {
      serviceId: formData.serviceId,
      maxPassengers: Number(formData.maxPassengers),

      // Không giới hạn => null
      durationMinutes: formData.unlimitedDuration
        ? null
        : Number(formData.durationMinutes),
    };

    await onSubmit?.(assignment.id, payload);
  };

  const isEditing = assignment.status === "NOT_STARTED";

  return (
    <div
      className="service-tour-config-overlay"
      onMouseDown={(event) => {
        if (event.target === event.currentTarget && !submitting) {
          onClose?.();
        }
      }}
    >
      <div className="service-tour-config-modal">
        {/* =====================================================
            HEADER
            ===================================================== */}

        <div className="service-tour-config-modal-header">
          <div className="service-tour-config-modal-header-content">
            <span className="service-tour-config-modal-eyebrow">
              {isEditing ? "Chỉnh sửa cấu hình" : "Cấu hình dịch vụ"}
            </span>

            <h2>{assignment.tourCode}</h2>

            <p>
              {assignment.tourName}
              {" · "}
              {assignment.cruiseAreaName}
            </p>
          </div>

          <button
            type="button"
            className="service-tour-config-modal-close"
            onClick={onClose}
            disabled={submitting}
            aria-label="Đóng"
          >
            <X size={20} />
          </button>
        </div>

        {/* =====================================================
            FORM
            ===================================================== */}

        <form
          className="service-tour-config-modal-form"
          onSubmit={handleSubmit}
        >
          {/* ===================================================
              SERVICE
              =================================================== */}

          <div className="service-tour-config-field">
            <label>
              <Wrench size={15} />
              <span>Dịch vụ</span>
              <b>*</b>
            </label>

            <ServiceSelect
              value={formData.serviceId}
              onChange={handleServiceChange}
              disabled={submitting}
              error={errors.serviceId}
            />
          </div>

          {/* ===================================================
              PASSENGERS + DURATION
              =================================================== */}

          <div className="service-tour-config-two-columns">
            {/* PASSENGERS */}

            <div className="service-tour-config-field">
              <label>
                <Users size={15} />
                <span>Số hành khách tối đa</span>
                <b>*</b>
              </label>

              <input
                type="number"
                name="maxPassengers"
                min="1"
                step="1"
                value={formData.maxPassengers}
                onChange={handleChange}
                disabled={submitting}
                placeholder="Ví dụ: 50"
              />

              {errors.maxPassengers && (
                <span className="service-tour-config-field-error">
                  {errors.maxPassengers}
                </span>
              )}
            </div>

            {/* DURATION */}

            <div className="service-tour-config-field">
              <label>
                <Clock3 size={15} />
                <span>Thời lượng</span>
              </label>

              {/* CHỌN CÓ / KHÔNG GIỚI HẠN */}

              <select
                className="service-tour-config-duration-select"
                value={formData.unlimitedDuration ? "UNLIMITED" : "LIMITED"}
                onChange={handleDurationModeChange}
                disabled={submitting}
              >
                <option value="LIMITED">Có giới hạn thời gian</option>

                <option value="UNLIMITED">Không giới hạn</option>
              </select>

              {/* INPUT THỜI GIAN */}

              {!formData.unlimitedDuration && (
                <div className="service-tour-config-duration">
                  <input
                    type="number"
                    name="durationMinutes"
                    min="1"
                    step="1"
                    value={formData.durationMinutes}
                    onChange={handleChange}
                    disabled={submitting}
                    placeholder="Ví dụ: 60"
                  />

                  <span>phút</span>
                </div>
              )}

              {/* UNLIMITED HINT */}

              {formData.unlimitedDuration && (
                <small className="service-tour-config-duration-hint">
                  Dịch vụ này không giới hạn thời gian sử dụng.
                </small>
              )}

              {errors.durationMinutes && (
                <span className="service-tour-config-field-error">
                  {errors.durationMinutes}
                </span>
              )}
            </div>
          </div>

          {/* =====================================================
              FOOTER
              ===================================================== */}

          <div className="service-tour-config-modal-footer">
            <button
              type="button"
              className="service-tour-config-cancel"
              onClick={onClose}
              disabled={submitting}
            >
              Hủy
            </button>

            <button
              type="submit"
              className="service-tour-config-submit"
              disabled={submitting}
            >
              <Save size={17} />

              <span>
                {submitting
                  ? "Đang lưu..."
                  : isEditing
                    ? "Lưu thay đổi"
                    : "Lưu cấu hình"}
              </span>
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default ServiceTourConfigModal;
