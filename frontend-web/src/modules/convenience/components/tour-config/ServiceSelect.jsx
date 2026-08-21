// src/modules/convenience/tour-config/ServiceSelect.jsx
import React, { useEffect } from "react";
import { ChevronDown, Loader2 } from "lucide-react";

import useService from "../../hooks/useService";

import "../../styles/tour-config/ServiceSelect.css";

const ServiceSelect = ({
  value = "",
  onChange,
  disabled = false,
  error = "",
}) => {
  const { services, loading, error: loadError, loadServices } = useService();

  // =====================================================
  // LOAD SERVICES
  // =====================================================

  useEffect(() => {
    loadServices();
  }, [loadServices]);

  // =====================================================
  // CHANGE
  // =====================================================

  const handleChange = (event) => {
    const serviceId = event.target.value;

    onChange?.(serviceId);
  };

  return (
    <div className="convenience-service-select">
      <div
        className={`convenience-service-select-wrapper ${
          error ? "has-error" : ""
        }`}
      >
        <select
          value={value}
          onChange={handleChange}
          disabled={disabled || loading}
          className="convenience-service-select-input"
        >
          <option value="">
            {loading ? "Đang tải dịch vụ..." : "Chọn dịch vụ"}
          </option>

          {services.map((service) => (
            <option key={service.id} value={service.id}>
              {service.name}
            </option>
          ))}
        </select>

        {loading ? (
          <Loader2 size={17} className="convenience-service-select-loading" />
        ) : (
          <ChevronDown size={17} className="convenience-service-select-icon" />
        )}
      </div>

      {error && (
        <span className="convenience-service-select-error">{error}</span>
      )}

      {!error && loadError && (
        <span className="convenience-service-select-error">{loadError}</span>
      )}

      {!loading && !loadError && services.length === 0 && (
        <span className="convenience-service-select-hint">
          Chưa có dịch vụ khả dụng
        </span>
      )}
    </div>
  );
};

export default ServiceSelect;
