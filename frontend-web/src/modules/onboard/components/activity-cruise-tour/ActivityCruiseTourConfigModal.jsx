// src/modules/onboard/components/activity-cruise-tour/ActivityCruiseTourConfigModal.jsx

import React, { useEffect, useState } from "react";
import { CalendarDays, Clock3, DollarSign, Save, Users, X } from "lucide-react";

import ActivityCruiseSelect from "./ActivityCruiseSelect";

import "../../styles/activity-cruise-tour/ActivityCruiseTourConfigModal.css";

const ActivityCruiseTourConfigModal = ({
  assignment,
  onClose,
  onSubmit,
  submitting = false,
}) => {
  const [formData, setFormData] = useState({
    activityCruiseId: "",
    startTime: "",
    endTime: "",
    maxPassengers: "",
    price: "",
  });

  const [errors, setErrors] = useState({});

  // =====================================================
  // TOUR DATE RANGE
  // =====================================================

  const tourStartDate = assignment?.tourStartDate || "";
  const tourEndDate = assignment?.tourEndDate || "";

  // datetime-local cần format:
  // YYYY-MM-DDTHH:mm
  //
  // Tour chỉ có ngày nên:
  // start = YYYY-MM-DDT00:00
  // end   = YYYY-MM-DDT23:59
  const tourStartDateTime = tourStartDate ? `${tourStartDate}T00:00` : "";

  const tourEndDateTime = tourEndDate ? `${tourEndDate}T23:59` : "";

  // =====================================================
  // FORMAT TOUR DATE
  // =====================================================

  const formatTourDate = (value) => {
    if (!value) {
      return "—";
    }

    const date = new Date(`${value}T00:00:00`);

    if (Number.isNaN(date.getTime())) {
      return value;
    }

    return date.toLocaleDateString("vi-VN", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
    });
  };

  // =====================================================
  // RESET FORM WHEN ASSIGNMENT CHANGES
  // =====================================================

  useEffect(() => {
    if (!assignment) {
      return;
    }

    setFormData({
      activityCruiseId: assignment.activityCruiseId || "",
      startTime: assignment.startTime
        ? String(assignment.startTime).slice(0, 16)
        : "",
      endTime: assignment.endTime
        ? String(assignment.endTime).slice(0, 16)
        : "",
      maxPassengers:
        assignment.maxPassengers != null
          ? String(assignment.maxPassengers)
          : "",
      price: assignment.price != null ? String(assignment.price) : "",
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

    // ===================================================
    // START TIME
    // ===================================================

    if (name === "startTime") {
      if (tourStartDateTime && value && value < tourStartDateTime) {
        setErrors((previous) => ({
          ...previous,
          startTime:
            `Thời gian bắt đầu không được trước ngày bắt đầu Tour ` +
            `(${formatTourDate(tourStartDate)})`,
        }));

        return;
      }

      if (tourEndDateTime && value && value > tourEndDateTime) {
        setErrors((previous) => ({
          ...previous,
          startTime:
            `Thời gian bắt đầu không được sau ngày kết thúc Tour ` +
            `(${formatTourDate(tourEndDate)})`,
        }));

        return;
      }

      if (formData.endTime && value && value >= formData.endTime) {
        setErrors((previous) => ({
          ...previous,
          endTime: "Thời gian kết thúc phải sau thời gian bắt đầu",
        }));
      }
    }

    // ===================================================
    // END TIME
    // ===================================================

    if (name === "endTime") {
      if (tourStartDateTime && value && value < tourStartDateTime) {
        setErrors((previous) => ({
          ...previous,
          endTime:
            `Thời gian kết thúc không được trước ngày bắt đầu Tour ` +
            `(${formatTourDate(tourStartDate)})`,
        }));

        return;
      }

      if (tourEndDateTime && value && value > tourEndDateTime) {
        setErrors((previous) => ({
          ...previous,
          endTime:
            `Thời gian kết thúc không được sau ngày kết thúc Tour ` +
            `(${formatTourDate(tourEndDate)})`,
        }));

        return;
      }

      if (formData.startTime && value && value <= formData.startTime) {
        setErrors((previous) => ({
          ...previous,
          endTime: "Thời gian kết thúc phải sau thời gian bắt đầu",
        }));
      }
    }
  };

  // =====================================================
  // ACTIVITY SELECT
  // =====================================================

  const handleActivityChange = (activityId) => {
    setFormData((previous) => ({
      ...previous,
      activityCruiseId: activityId,
    }));

    setErrors((previous) => ({
      ...previous,
      activityCruiseId: "",
    }));
  };

  // =====================================================
  // VALIDATE
  // =====================================================

  const validate = () => {
    const nextErrors = {};

    // ===================================================
    // ACTIVITY
    // ===================================================

    if (!formData.activityCruiseId) {
      nextErrors.activityCruiseId = "Vui lòng chọn hoạt động";
    }

    // ===================================================
    // START TIME
    // ===================================================

    if (!formData.startTime) {
      nextErrors.startTime = "Vui lòng chọn thời gian bắt đầu";
    }

    // Không được trước Tour
    if (
      formData.startTime &&
      tourStartDateTime &&
      formData.startTime < tourStartDateTime
    ) {
      nextErrors.startTime =
        `Thời gian bắt đầu không được trước ngày bắt đầu Tour ` +
        `(${formatTourDate(tourStartDate)})`;
    }

    // Không được sau Tour
    if (
      formData.startTime &&
      tourEndDateTime &&
      formData.startTime > tourEndDateTime
    ) {
      nextErrors.startTime =
        `Thời gian bắt đầu không được sau ngày kết thúc Tour ` +
        `(${formatTourDate(tourEndDate)})`;
    }

    // ===================================================
    // END TIME
    // ===================================================

    if (!formData.endTime) {
      nextErrors.endTime = "Vui lòng chọn thời gian kết thúc";
    }

    // Không được trước Tour
    if (
      formData.endTime &&
      tourStartDateTime &&
      formData.endTime < tourStartDateTime
    ) {
      nextErrors.endTime =
        `Thời gian kết thúc không được trước ngày bắt đầu Tour ` +
        `(${formatTourDate(tourStartDate)})`;
    }

    // Không được sau Tour
    if (
      formData.endTime &&
      tourEndDateTime &&
      formData.endTime > tourEndDateTime
    ) {
      nextErrors.endTime =
        `Thời gian kết thúc không được sau ngày kết thúc Tour ` +
        `(${formatTourDate(tourEndDate)})`;
    }

    // Kết thúc phải sau bắt đầu
    if (
      formData.startTime &&
      formData.endTime &&
      formData.startTime >= formData.endTime
    ) {
      nextErrors.endTime = "Thời gian kết thúc phải sau thời gian bắt đầu";
    }

    // ===================================================
    // PASSENGERS
    // ===================================================

    if (!formData.maxPassengers || Number(formData.maxPassengers) <= 0) {
      nextErrors.maxPassengers = "Số hành khách phải lớn hơn 0";
    }

    // ===================================================
    // PRICE
    // ===================================================

    if (formData.price === "" || Number(formData.price) < 0) {
      nextErrors.price = "Giá phải lớn hơn hoặc bằng 0";
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
      activityCruiseId: formData.activityCruiseId,

      startTime: formData.startTime,

      endTime: formData.endTime,

      maxPassengers: Number(formData.maxPassengers),

      price: Number(formData.price),
    };

    await onSubmit?.(assignment.id, payload);
  };

  // =====================================================
  // RENDER
  // =====================================================

  return (
    <div
      className="activity-cruise-tour-config-overlay"
      onMouseDown={(event) => {
        if (event.target === event.currentTarget && !submitting) {
          onClose?.();
        }
      }}
    >
      <div className="activity-cruise-tour-config-modal">
        {/* =================================================
            HEADER
            ================================================= */}

        <div className="activity-cruise-tour-config-header">
          <div>
            <span className="activity-cruise-tour-config-eyebrow">
              Cấu hình hoạt động
            </span>

            <h2>{assignment.tourCode || "Tour"}</h2>

            <p>
              {assignment.tourName || "—"}
              {" · "}
              {assignment.cruiseAreaName || "—"}
            </p>
          </div>

          <button
            type="button"
            className="activity-cruise-tour-config-close"
            onClick={onClose}
            disabled={submitting}
            aria-label="Đóng"
          >
            <X size={20} />
          </button>
        </div>

        {/* =================================================
            TOUR DATE RANGE
            ================================================= */}

        <div className="activity-cruise-tour-config-tour-range">
          <div className="activity-cruise-tour-config-tour-range-icon">
            <CalendarDays size={19} />
          </div>

          <div className="activity-cruise-tour-config-tour-range-content">
            <span className="activity-cruise-tour-config-tour-range-label">
              Thời gian Tour
            </span>

            <strong>
              {formatTourDate(tourStartDate)}
              {" → "}
              {formatTourDate(tourEndDate)}
            </strong>

            <small>Hoạt động phải nằm trong khoảng thời gian của Tour.</small>
          </div>
        </div>

        {/* =================================================
            FORM
            ================================================= */}

        <form
          className="activity-cruise-tour-config-form"
          onSubmit={handleSubmit}
        >
          {/* =================================================
              ACTIVITY
              ================================================= */}

          <div className="activity-cruise-tour-config-field">
            <label>
              Hoạt động
              <span>*</span>
            </label>

            <ActivityCruiseSelect
              value={formData.activityCruiseId}
              onChange={handleActivityChange}
              disabled={submitting}
            />

            {errors.activityCruiseId && (
              <span className="activity-cruise-tour-config-error">
                {errors.activityCruiseId}
              </span>
            )}
          </div>

          {/* =================================================
              TIME
              ================================================= */}

          <div className="activity-cruise-tour-config-two-columns">
            {/* START */}

            <div className="activity-cruise-tour-config-field">
              <label>
                <Clock3 size={15} />
                Thời gian bắt đầu
                <span>*</span>
              </label>

              <input
                type="datetime-local"
                name="startTime"
                value={formData.startTime}
                min={tourStartDateTime || undefined}
                max={formData.endTime || tourEndDateTime || undefined}
                onChange={handleChange}
                disabled={submitting}
              />

              <small className="activity-cruise-tour-config-field-hint">
                Cho phép từ {formatTourDate(tourStartDate)}
                {" đến "}
                {formatTourDate(tourEndDate)}
              </small>

              {errors.startTime && (
                <span className="activity-cruise-tour-config-error">
                  {errors.startTime}
                </span>
              )}
            </div>

            {/* END */}

            <div className="activity-cruise-tour-config-field">
              <label>
                <Clock3 size={15} />
                Thời gian kết thúc
                <span>*</span>
              </label>

              <input
                type="datetime-local"
                name="endTime"
                value={formData.endTime}
                min={formData.startTime || tourStartDateTime || undefined}
                max={tourEndDateTime || undefined}
                onChange={handleChange}
                disabled={submitting}
              />

              <small className="activity-cruise-tour-config-field-hint">
                Không được sau {formatTourDate(tourEndDate)}
              </small>

              {errors.endTime && (
                <span className="activity-cruise-tour-config-error">
                  {errors.endTime}
                </span>
              )}
            </div>
          </div>

          {/* =================================================
              PASSENGERS + PRICE
              ================================================= */}

          <div className="activity-cruise-tour-config-two-columns">
            {/* PASSENGERS */}

            <div className="activity-cruise-tour-config-field">
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
                <span className="activity-cruise-tour-config-error">
                  {errors.maxPassengers}
                </span>
              )}
            </div>

            {/* PRICE */}

            <div className="activity-cruise-tour-config-field">
              <label>
                <DollarSign size={15} />
                Giá
                <span>*</span>
              </label>

              <input
                type="number"
                name="price"
                min="0"
                step="1000"
                value={formData.price}
                onChange={handleChange}
                disabled={submitting}
                placeholder="Ví dụ: 500000"
              />

              {errors.price && (
                <span className="activity-cruise-tour-config-error">
                  {errors.price}
                </span>
              )}
            </div>
          </div>

          {/* =================================================
              FOOTER
              ================================================= */}

          <div className="activity-cruise-tour-config-footer">
            <button
              type="button"
              className="activity-cruise-tour-config-cancel"
              onClick={onClose}
              disabled={submitting}
            >
              Hủy
            </button>

            <button
              type="submit"
              className="activity-cruise-tour-config-submit"
              disabled={submitting}
            >
              <Save size={17} />

              {submitting ? "Đang lưu..." : "Lưu cấu hình"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default ActivityCruiseTourConfigModal;
