// src/modules/convenience/tour-config/ServiceTourConfigModal.jsx
import React, { useEffect, useState } from "react";
import { Clock3, Save, Users, Wrench, X } from "lucide-react";
import "../../styles/tour-config/ServiceTourConfigModal.css";

import ServiceSelect from "./ServiceSelect";

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
  });

  const [errors, setErrors] = useState({});

  // =====================================================
  // RESET
  // =====================================================

  useEffect(() => {
    if (!assignment) {
      return;
    }

    setFormData({
      serviceId: assignment.serviceId || "",

      maxPassengers:
        assignment.maxPassengers != null
          ? String(assignment.maxPassengers)
          : "",

      durationMinutes:
        assignment.durationMinutes != null
          ? String(assignment.durationMinutes)
          : "",
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

    if (!formData.durationMinutes || Number(formData.durationMinutes) <= 0) {
      nextErrors.durationMinutes = "Thời lượng phải lớn hơn 0 phút";
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
      durationMinutes: Number(formData.durationMinutes),
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
        {/* HEADER */}

        <div className="service-tour-config-modal-header">
          <div>
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

        {/* FORM */}

        <form
          className="service-tour-config-modal-form"
          onSubmit={handleSubmit}
        >
          {/* SERVICE */}

          <div className="service-tour-config-field">
            <label>
              <Wrench size={15} />
              Dịch vụ
              <span>*</span>
            </label>

            <ServiceSelect
              value={formData.serviceId}
              onChange={handleServiceChange}
              disabled={submitting}
            />

            {errors.serviceId && (
              <span className="service-tour-config-field-error">
                {errors.serviceId}
              </span>
            )}
          </div>

          {/* PASSENGERS + DURATION */}

          <div className="service-tour-config-two-columns">
            <div className="service-tour-config-field">
              <label>
                <Users size={15} />
                Số hành khách tối đa
                <span>*</span>
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

            <div className="service-tour-config-field">
              <label>
                <Clock3 size={15} />
                Thời lượng
                <span>*</span>
              </label>

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

              {errors.durationMinutes && (
                <span className="service-tour-config-field-error">
                  {errors.durationMinutes}
                </span>
              )}
            </div>
          </div>

          {/* FOOTER */}

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

              {submitting
                ? "Đang lưu..."
                : isEditing
                  ? "Lưu thay đổi"
                  : "Lưu cấu hình"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default ServiceTourConfigModal;
