// src/modules/operation/components/tour-configuration/TourConfigurationSummary.jsx

import React from "react";
import { Activity, CheckCircle2, MapPin, Package, Wrench } from "lucide-react";

import { isTourItemConfigured } from "../../utils/tourConfigurationUtils";

import "../../styles/tour-configuration/TourConfigurationSummary.css";

const TourConfigurationSummary = ({
  activityCruises = [],
  activityVisits = [],
  products = [],
  services = [],
  configurationComplete = false,
  activeFilter = "ALL",
  onFilterChange,
}) => {
  // =========================================================
  // SAFE DATA
  // =========================================================

  const safeActivityCruises = Array.isArray(activityCruises)
    ? activityCruises
    : [];

  const safeActivityVisits = Array.isArray(activityVisits)
    ? activityVisits
    : [];

  const safeProducts = Array.isArray(products) ? products : [];

  const safeServices = Array.isArray(services) ? services : [];

  // =========================================================
  // TOTAL ASSIGNMENTS
  // =========================================================
  //
  // Tổng số item được phân công cho Tour.
  //
  // Ví dụ:
  // 3 activity trên tàu
  // -> total = 3
  //
  // =========================================================

  const totalActivityCruises = safeActivityCruises.length;
  const totalActivityVisits = safeActivityVisits.length;
  const totalProducts = safeProducts.length;
  const totalServices = safeServices.length;

  // =========================================================
  // ACTUALLY CONFIGURED
  // =========================================================
  //
  // CHỈ status === "CONFIGURED" mới được tính là đã cấu hình.
  //
  // WAITING_CONFIG -> chưa cấu hình
  // CONFIGURED     -> đã cấu hình
  // NOT_STARTED    -> chưa tính là configured ở bước này
  // IN_PROGRESS    -> chưa tính là configured ở bước này
  // COMPLETED      -> không dùng để xác định trạng thái cấu hình
  // =========================================================

  const configuredActivityCruises = safeActivityCruises.filter((item) =>
    isTourItemConfigured(item.status),
  ).length;

  const configuredActivityVisits = safeActivityVisits.filter((item) =>
    isTourItemConfigured(item.status),
  ).length;

  const configuredProducts = safeProducts.filter((item) =>
    isTourItemConfigured(item.status),
  ).length;

  const configuredServices = safeServices.filter((item) =>
    isTourItemConfigured(item.status),
  ).length;

  // =========================================================
  // SUMMARY CARDS
  // =========================================================

  const cards = [
    {
      key: "activityCruise",
      label: "Hoạt động trên tàu",
      icon: Activity,
      configured: configuredActivityCruises,
      total: totalActivityCruises,
    },
    {
      key: "activityVisit",
      label: "Hoạt động trên bờ",
      icon: MapPin,
      configured: configuredActivityVisits,
      total: totalActivityVisits,
    },
    {
      key: "product",
      label: "Sản phẩm",
      icon: Package,
      configured: configuredProducts,
      total: totalProducts,
    },
    {
      key: "service",
      label: "Dịch vụ",
      icon: Wrench,
      configured: configuredServices,
      total: totalServices,
    },
  ];

  // =========================================================
  // FILTER
  // =========================================================

  const handleCardClick = (key) => {
    if (!onFilterChange) return;

    const nextFilter = activeFilter === key ? "ALL" : key;

    onFilterChange(nextFilter);
  };

  // =========================================================
  // RENDER
  // =========================================================

  return (
    <section className="tour-configuration-summary">
      {/* =========================================================
          HEADER
          ========================================================= */}

      <div className="tour-configuration-summary-header">
        <div>
          <h2>Tổng quan cấu hình</h2>

          <p>Tình trạng cấu hình các thành phần của Tour.</p>
        </div>

        <div
          className={`tour-configuration-summary-result ${
            configurationComplete ? "complete" : "incomplete"
          }`}
        >
          <CheckCircle2 size={18} />

          <span>
            {configurationComplete
              ? "Đã hoàn tất cấu hình"
              : "Chưa hoàn tất cấu hình"}
          </span>
        </div>
      </div>

      {/* =========================================================
          SUMMARY CARDS
          ========================================================= */}

      <div className="tour-configuration-summary-grid">
        {cards.map((card) => {
          const Icon = card.icon;

          const isActive = activeFilter === card.key;

          // Có item và tất cả item đều đã CONFIGURED
          const isComplete = card.total > 0 && card.configured === card.total;

          // Có ít nhất một item đã CONFIGURED
          const hasConfigured = card.configured > 0;

          return (
            <div
              key={card.key}
              className={`tour-configuration-summary-card clickable ${
                isActive ? "active" : ""
              }`}
              onClick={() => handleCardClick(card.key)}
              role="button"
              tabIndex={0}
              onKeyDown={(event) => {
                if (event.key === "Enter" || event.key === " ") {
                  event.preventDefault();
                  handleCardClick(card.key);
                }
              }}
            >
              {/* =================================================
                  ICON
                  ================================================= */}

              <div className="tour-configuration-summary-card-icon">
                <Icon size={22} />
              </div>

              {/* =================================================
                  CONTENT
                  ================================================= */}

              <div className="tour-configuration-summary-card-content">
                <span>{card.label}</span>

                <strong>
                  {card.configured}
                  <small> / {card.total}</small>
                </strong>

                <p>
                  {card.total === 0
                    ? "Chưa có phân công"
                    : isComplete
                      ? "Đã cấu hình đầy đủ"
                      : hasConfigured
                        ? "Đang cấu hình"
                        : "Chưa cấu hình"}
                </p>
              </div>
            </div>
          );
        })}
      </div>
    </section>
  );
};

export default TourConfigurationSummary;
