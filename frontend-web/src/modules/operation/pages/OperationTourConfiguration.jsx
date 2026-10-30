import React from "react";
import { AlertCircle, ArrowLeft, RefreshCw } from "lucide-react";
import { useNavigate } from "react-router-dom";

import useTourConfigurationPage from "../hooks/useTourConfigurationPage";

import TourConfigurationSummary from "../components/tour-configuration/TourConfigurationSummary";
import ActivityCruiseConfigurationTable from "../components/tour-configuration/ActivityCruiseConfigurationTable";
import ActivityVisitConfigurationTable from "../components/tour-configuration/ActivityVisitConfigurationTable";
import ProductConfigurationTable from "../components/tour-configuration/ProductConfigurationTable";
import ServiceConfigurationTable from "../components/tour-configuration/ServiceConfigurationTable";

import "../styles/OperationTourConfiguration.css";

function ErrorState({ title, message, onBack }) {
  return (
    <div className="operation-tour-configuration-page">
      <div className="operation-tour-configuration-header">
        <button
          type="button"
          className="operation-tour-configuration-back"
          onClick={onBack}
        >
          <ArrowLeft size={18} />
          Quay lại
        </button>
      </div>

      <div className="operation-tour-configuration-error">
        <AlertCircle size={22} />
        <div>
          <strong>{title}</strong>
          <p>{message}</p>
        </div>
      </div>
    </div>
  );
}

const OperationTourConfiguration = () => {
  const navigate = useNavigate();
  const m = useTourConfigurationPage();

  const handleBack = () => navigate("/operation/tours");

  if (!m.tourId) {
    return (
      <ErrorState
        title="Thiếu Tour ID"
        message="Không xác định được Tour cần xem cấu hình."
        onBack={handleBack}
      />
    );
  }

  if (m.loading && !m.hasAnyData) {
    return (
      <div className="operation-tour-configuration-loading">
        <RefreshCw size={22} className="operation-tour-configuration-spin" />
        <span>Đang tải cấu hình Tour...</span>
      </div>
    );
  }

  if (m.error && !m.hasAnyData) {
    return (
      <ErrorState
        title="Không thể tải cấu hình Tour"
        message={m.error}
        onBack={handleBack}
      />
    );
  }

  const sections = [
    {
      key: "activityCruise",
      content: (
        <ActivityCruiseConfigurationTable activities={m.configuredActivities} />
      ),
    },
    {
      key: "activityVisit",
      content: (
        <ActivityVisitConfigurationTable
          activities={m.configuredActivityVisits}
        />
      ),
    },
    {
      key: "product",
      content: <ProductConfigurationTable products={m.configuredProducts} />,
    },
    {
      key: "service",
      content: <ServiceConfigurationTable services={m.configuredServices} />,
    },
  ];

  return (
    <div className="operation-tour-configuration-page">
      <div className="operation-tour-configuration-header">
        <div className="operation-tour-configuration-header-left">
          <button
            type="button"
            className="operation-tour-configuration-back"
            onClick={handleBack}
          >
            <ArrowLeft size={18} />
            Quay lại
          </button>

          <div className="operation-tour-configuration-title">
            <span>Cấu hình Tour</span>
            <h1>{m.tourId}</h1>
          </div>
        </div>

        <button
          type="button"
          className="operation-tour-configuration-refresh"
          onClick={m.refresh}
          disabled={m.loading}
        >
          <RefreshCw
            size={17}
            className={m.loading ? "operation-tour-configuration-spin" : ""}
          />
          Làm mới
        </button>
      </div>

      <TourConfigurationSummary
        activityCruises={m.configuredActivities}
        activityVisits={m.configuredActivityVisits}
        products={m.configuredProducts}
        services={m.configuredServices}
        configurationComplete={m.configurationComplete}
        activeFilter={m.activeFilter}
        onFilterChange={m.handleFilterChange}
      />

      <div className="operation-tour-configuration-tables">
        {sections
          .filter(
            ({ key }) => m.activeFilter === "ALL" || m.activeFilter === key,
          )
          .map(({ key, content }) => (
            <section
              key={key}
              id={`${key}-section`}
              className="operation-tour-configuration-section"
            >
              {content}
            </section>
          ))}
      </div>

      <section className="operation-tour-configuration-package">
        <div>
          <strong>Sẵn sàng tạo gói Tour & Mở bán vé?</strong>

          <p>
            Khi toàn bộ Activity, Product và Service đã được cấu hình đầy đủ,
            Operation có thể tạo các gói Tour hoặc tiến hành cấu hình thời gian
            mở bán vé cho tour này.
          </p>
        </div>

        <div className="operation-tour-configuration-package-actions">
          <button
            type="button"
            disabled={!m.configurationComplete}
            className="operation-tour-configuration-create-package"
            onClick={() =>
              navigate(`/operation/tour-packages?tourId=${m.tourId}`)
            }
          >
            Tạo gói Tour
          </button>

          <button
            type="button"
            className={`operation-tour-configuration-open-booking ${
              m.hasBookingConfig
                ? "operation-tour-configuration-open-booking--has-config"
                : ""
            }`}
            onClick={() =>
              navigate(`/operation/tour-booking-open?tourId=${m.tourId}`)
            }
          >
            {m.hasBookingConfig ? "Xem / Sửa cấu hình mở bán" : "Mở bán vé"}
          </button>
        </div>
      </section>
    </div>
  );
};

export default OperationTourConfiguration;
