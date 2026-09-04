// src/modules/operation/pages/OperationTourConfiguration.jsx

import React, { useCallback, useEffect, useState } from "react";
import { AlertCircle, ArrowLeft, RefreshCw } from "lucide-react";
import { useNavigate, useSearchParams } from "react-router-dom";

// =========================================================
// HOOKS
// =========================================================

import useActivityCruiseTourAssignments from "../hooks/useActivityCruiseTourAssignments";
import useActivityVisitTourAssignments from "../hooks/useActivityVisitTourAssignments";
import useProductTourAssignments from "../hooks/useProductTourAssignments";
import useServiceTourAssignments from "../hooks/useServiceTourAssignments";

// =========================================================
// COMPONENTS
// =========================================================

import TourConfigurationSummary from "../components/tour-configuration/TourConfigurationSummary";

import ActivityCruiseConfigurationTable from "../components/tour-configuration/ActivityCruiseConfigurationTable";

import ActivityVisitConfigurationTable from "../components/tour-configuration/ActivityVisitConfigurationTable";

import ProductConfigurationTable from "../components/tour-configuration/ProductConfigurationTable";

import ServiceConfigurationTable from "../components/tour-configuration/ServiceConfigurationTable";

// =========================================================
// UTILS
// =========================================================

import { isTourItemConfigured } from "../utils/tourConfigurationUtils";

import "../styles/OperationTourConfiguration.css";

const OperationTourConfiguration = () => {
  const [searchParams] = useSearchParams();

  const tourId = searchParams.get("tourId");

  const navigate = useNavigate();

  // =========================================================
  // FILTER
  // =========================================================

  /**
   * ALL
   * activityCruise
   * activityVisit
   * product
   * service
   */
  const [activeFilter, setActiveFilter] = useState("ALL");

  // =========================================================
  // 4 HOOKS
  // =========================================================

  const {
    configuredActivities,
    configuredLoading: activityCruiseLoading,
    activityError,
    loadConfiguredActivitiesByTour,
  } = useActivityCruiseTourAssignments();

  const {
    configuredActivityVisits,
    configuredActivityVisitLoading: activityVisitLoading,
    activityVisitError,
    loadConfiguredActivityVisitsByTour,
  } = useActivityVisitTourAssignments();

  const {
    configuredProducts,
    configuredLoading: productLoading,
    productError,
    loadConfiguredProductsByTour,
  } = useProductTourAssignments();

  const {
    configuredServices,
    configuredLoading: serviceLoading,
    serviceError,
    loadConfiguredServicesByTour,
  } = useServiceTourAssignments();

  // =========================================================
  // LOADING
  // =========================================================

  const loading =
    activityCruiseLoading ||
    activityVisitLoading ||
    productLoading ||
    serviceLoading;

  // =========================================================
  // ERROR
  // =========================================================

  const error =
    activityError || activityVisitError || productError || serviceError;

  // =========================================================
  // TOUR INFO
  // =========================================================

  const configurationTourId = tourId;

  // =========================================================
  // LOAD 4 CONFIGURATIONS
  // =========================================================

  const loadConfigurations = useCallback(async () => {
    if (!tourId) return;

    await Promise.all([
      loadConfiguredActivitiesByTour(tourId),
      loadConfiguredActivityVisitsByTour(tourId),
      loadConfiguredProductsByTour(tourId),
      loadConfiguredServicesByTour(tourId),
    ]);
  }, [
    tourId,
    loadConfiguredActivitiesByTour,
    loadConfiguredActivityVisitsByTour,
    loadConfiguredProductsByTour,
    loadConfiguredServicesByTour,
  ]);

  // =========================================================
  // INITIAL LOAD
  // =========================================================

  useEffect(() => {
    loadConfigurations();
  }, [loadConfigurations]);

  // =========================================================
  // BACK
  // =========================================================

  const handleBack = () => {
    navigate("/operation/tours");
  };

  // =========================================================
  // REFRESH
  // =========================================================

  const handleRefresh = () => {
    loadConfigurations();
  };

  // =========================================================
  // FILTER
  // =========================================================

  const handleFilterChange = (filterKey) => {
    const newFilter = activeFilter === filterKey ? "ALL" : filterKey;

    setActiveFilter(newFilter);

    if (newFilter !== "ALL") {
      setTimeout(() => {
        const targetElement = document.getElementById(`${newFilter}-section`);

        if (targetElement) {
          targetElement.scrollIntoView({
            behavior: "smooth",
            block: "start",
          });
        }
      }, 100);
    }
  };

  // =========================================================
  // NO TOUR ID
  // =========================================================

  if (!tourId) {
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
            <strong>Thiếu Tour ID</strong>

            <p>Không xác định được Tour cần xem cấu hình.</p>
          </div>
        </div>
      </div>
    );
  }

  // =========================================================
  // INITIAL LOADING
  // =========================================================
  //
  // hasAnyData chỉ dùng để biết API đã trả về dữ liệu hay chưa.
  //
  // KHÔNG dùng biến này để xác định "đã cấu hình".
  // =========================================================

  const hasAnyData =
    configuredActivities.length > 0 ||
    configuredActivityVisits.length > 0 ||
    configuredProducts.length > 0 ||
    configuredServices.length > 0;

  if (loading && !hasAnyData) {
    return (
      <div className="operation-tour-configuration-loading">
        <RefreshCw size={22} className="operation-tour-configuration-spin" />

        <span>Đang tải cấu hình Tour...</span>
      </div>
    );
  }

  // =========================================================
  // ERROR
  // =========================================================

  if (error && !hasAnyData) {
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

  // =========================================================
  // ALL ASSIGNMENTS
  // =========================================================
  //
  // 4 API trả về TOÀN BỘ assignment:
  //
  // - Activity Cruise
  // - Activity Visit
  // - Product
  // - Service
  //
  // Vì vậy length = tổng số được phân công,
  // không phải tổng số đã cấu hình.
  // =========================================================

  const allAssignments = [
    ...configuredActivities,
    ...configuredActivityVisits,
    ...configuredProducts,
    ...configuredServices,
  ];

  // =========================================================
  // TOTAL ASSIGNMENTS
  // =========================================================

  const totalAssignments = allAssignments.length;

  // =========================================================
  // CONFIGURED ASSIGNMENTS
  // =========================================================
  //
  // WAITING_CONFIG = chưa cấu hình
  //
  // CONFIGURED
  // NOT_STARTED
  // IN_PROGRESS
  // COMPLETED
  // OUT_OF_STOCK
  // DELAYED
  // CANCELLED
  //
  // => đều đã vượt qua bước cấu hình.
  // =========================================================

  const configuredAssignments = allAssignments.filter((item) =>
    isTourItemConfigured(item.status),
  );

  const configuredCount = configuredAssignments.length;

  // =========================================================
  // CONFIGURATION COMPLETE
  // =========================================================
  //
  // Chỉ hoàn tất khi:
  //
  // 1. Tour có ít nhất một assignment
  // 2. TẤT CẢ assignment đều đã cấu hình
  //
  // Ví dụ:
  //
  // 5 assignment
  // 3 CONFIGURED
  // 2 WAITING_CONFIG
  //
  // => false
  //
  // 5 assignment
  // 5 CONFIGURED
  //
  // => true
  // =========================================================

  const configurationComplete =
    totalAssignments > 0 && configuredCount === totalAssignments;

  // =========================================================
  // RENDER
  // =========================================================

  return (
    <div className="operation-tour-configuration-page">
      {/* =======================================================
          HEADER
          ======================================================= */}

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

            <h1>{configurationTourId || "—"}</h1>
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

      {/* =======================================================
          SUMMARY
          ======================================================= */}

      <TourConfigurationSummary
        activityCruises={configuredActivities}
        activityVisits={configuredActivityVisits}
        products={configuredProducts}
        services={configuredServices}
        configurationComplete={configurationComplete}
        activeFilter={activeFilter}
        onFilterChange={handleFilterChange}
      />

      {/* =======================================================
          TABLES
          ======================================================= */}

      <div className="operation-tour-configuration-tables">
        {/* =====================================================
            ACTIVITY CRUISE
            ===================================================== */}

        {(activeFilter === "ALL" || activeFilter === "activityCruise") && (
          <section
            id="activityCruise-section"
            className="operation-tour-configuration-section"
          >
            <ActivityCruiseConfigurationTable
              activities={configuredActivities}
            />
          </section>
        )}

        {/* =====================================================
            ACTIVITY VISIT
            ===================================================== */}

        {(activeFilter === "ALL" || activeFilter === "activityVisit") && (
          <section
            id="activityVisit-section"
            className="operation-tour-configuration-section"
          >
            <ActivityVisitConfigurationTable
              activities={configuredActivityVisits}
            />
          </section>
        )}

        {/* =====================================================
            PRODUCT
            ===================================================== */}

        {(activeFilter === "ALL" || activeFilter === "product") && (
          <section
            id="product-section"
            className="operation-tour-configuration-section"
          >
            <ProductConfigurationTable products={configuredProducts} />
          </section>
        )}

        {/* =====================================================
            SERVICE
            ===================================================== */}

        {(activeFilter === "ALL" || activeFilter === "service") && (
          <section
            id="service-section"
            className="operation-tour-configuration-section"
          >
            <ServiceConfigurationTable services={configuredServices} />
          </section>
        )}
      </div>

      {/* =======================================================
          CREATE PACKAGE
          ======================================================= */}

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
          onClick={() => navigate(`/operation/tour-packages?tourId=${tourId}`)}
        >
          Tạo gói Tour
        </button>
      </section>
    </div>
  );
};

export default OperationTourConfiguration;
