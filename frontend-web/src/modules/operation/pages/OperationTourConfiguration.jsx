// src/modules/operation/pages/OperationTourConfiguration.jsx

import React, { useEffect, useState } from "react";
import { AlertCircle, ArrowLeft, RefreshCw } from "lucide-react";
import { useNavigate, useSearchParams } from "react-router-dom";
import useOperationTourConfiguration from "../hooks/useOperationTourConfiguration";

import TourConfigurationSummary from "../components/tour-configuration/TourConfigurationSummary";
import ActivityConfigurationTable from "../components/tour-configuration/ActivityConfigurationTable";
import ProductConfigurationTable from "../components/tour-configuration/ProductConfigurationTable";
import ServiceConfigurationTable from "../components/tour-configuration/ServiceConfigurationTable";

import "../styles/OperationTourConfiguration.css";

const OperationTourConfiguration = () => {
  const [searchParams] = useSearchParams();
  const tourId = searchParams.get("tourId");
  const navigate = useNavigate();

  // State quản lý bộ lọc: "ALL" | "activity" | "product" | "service"
  const [activeFilter, setActiveFilter] = useState("ALL");

  const { configuration, loading, error, loadConfiguration } =
    useOperationTourConfiguration();

  useEffect(() => {
    if (tourId) {
      loadConfiguration(tourId);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [tourId]);

  const handleBack = () => {
    navigate("/operation/tours");
  };

  const handleRefresh = () => {
    if (tourId) {
      loadConfiguration(tourId);
    }
  };

  // Hàm xử lý khi bấm vào các Card ở phần Summary
  const handleFilterChange = (filterKey) => {
    // Nếu bấm lại card đang chọn thì hiện tất cả, ngược lại thì lọc theo card đó
    const newFilter = activeFilter === filterKey ? "ALL" : filterKey;
    setActiveFilter(newFilter);

    // Tự động cuộn mượt xuống bảng tương ứng
    if (newFilter !== "ALL") {
      setTimeout(() => {
        const targetElement = document.getElementById(`${newFilter}-section`);
        if (targetElement) {
          targetElement.scrollIntoView({ behavior: "smooth", block: "start" });
        }
      }, 100);
    }
  };

  if (loading && !configuration) {
    return (
      <div className="operation-tour-configuration-loading">
        <RefreshCw size={22} className="operation-tour-configuration-spin" />
        <span>Đang tải cấu hình Tour...</span>
      </div>
    );
  }

  if (error) {
    return (
      <div className="operation-tour-configuration-page">
        <div className="operation-tour-configuration-header">
          <button
            type="button"
            className="operation-tour-configuration-back"
            onClick={handleBack}
          >
            <ArrowLeft size={18} />
            Quay lại
          </button>
        </div>

        <div className="operation-tour-configuration-error">
          <AlertCircle size={22} />
          <div>
            <strong>Không thể tải cấu hình Tour</strong>
            <p>{error}</p>
          </div>
        </div>
      </div>
    );
  }

  if (!configuration) {
    return (
      <div className="operation-tour-configuration-page">
        <div className="operation-tour-configuration-header">
          <button
            type="button"
            className="operation-tour-configuration-back"
            onClick={handleBack}
          >
            <ArrowLeft size={18} />
            Quay lại
          </button>
        </div>

        <div className="operation-tour-configuration-error">
          <AlertCircle size={22} />
          <div>
            <strong>Không có dữ liệu cấu hình</strong>
            <p>Không tìm thấy thông tin cấu hình của Tour này.</p>
          </div>
        </div>
      </div>
    );
  }

  const configData = configuration?.data || configuration;

  const {
    tourId: configurationTourId,
    tourCode,
    tourName,
    activities,
    products,
    services,
    configurationComplete = false,
  } = configData;

  const safeActivities = Array.isArray(activities) ? activities : [];
  const safeProducts = Array.isArray(products) ? products : [];
  const safeServices = Array.isArray(services) ? services : [];

  return (
    <div className="operation-tour-configuration-page">
      {/* HEADER */}
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
            <h1>
              {tourCode || "—"}
              {tourName ? ` - ${tourName}` : ""}
            </h1>
          </div>
        </div>

        <button
          type="button"
          className="operation-tour-configuration-refresh"
          onClick={handleRefresh}
          disabled={loading}
        >
          <RefreshCw
            size={17}
            className={loading ? "operation-tour-configuration-spin" : ""}
          />
          Làm mới
        </button>
      </div>

      {/* SUMMARY - Đã truyền activeFilter và callback onFilterChange */}
      <TourConfigurationSummary
        tourId={configurationTourId}
        tourCode={tourCode}
        tourName={tourName}
        activities={safeActivities}
        products={safeProducts}
        services={safeServices}
        configurationComplete={configurationComplete}
        activeFilter={activeFilter}
        onFilterChange={handleFilterChange}
      />

      {/* DANH SÁCH BẢNG CÓ GẮN ID & FILTER LOGIC */}
      <div className="operation-tour-configuration-tables">
        {(activeFilter === "ALL" || activeFilter === "activity") && (
          <section
            id="activity-section"
            className="operation-tour-configuration-section"
          >
            <ActivityConfigurationTable activities={safeActivities} />
          </section>
        )}

        {(activeFilter === "ALL" || activeFilter === "product") && (
          <section
            id="product-section"
            className="operation-tour-configuration-section"
          >
            <ProductConfigurationTable products={safeProducts} />
          </section>
        )}

        {(activeFilter === "ALL" || activeFilter === "service") && (
          <section
            id="service-section"
            className="operation-tour-configuration-section"
          >
            <ServiceConfigurationTable services={safeServices} />
          </section>
        )}
      </div>

      {/* CREATE PACKAGE ACTION */}
      <section className="operation-tour-configuration-package">
        <div>
          <strong>Sẵn sàng tạo gói Tour?</strong>
          <p>
            Khi toàn bộ Activity, Product và Service đã được cấu hình đầy đủ,
            Operation có thể tạo các gói Tour để chuẩn bị public Tour.
          </p>
        </div>

        <button
          type="button"
          disabled={!configurationComplete}
          className="operation-tour-configuration-create-package"
        >
          Tạo gói Tour
        </button>
      </section>
    </div>
  );
};

export default OperationTourConfiguration;
