// src/modules/operation/components/tour-configuration/TourConfigurationSummary.jsx

import React from "react";
import { Activity, CheckCircle2, MapPin, Package, Wrench } from "lucide-react";

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
  const safeActivityCruises = Array.isArray(activityCruises)
    ? activityCruises
    : [];

  const safeActivityVisits = Array.isArray(activityVisits)
    ? activityVisits
    : [];

  const safeProducts = Array.isArray(products) ? products : [];

  const safeServices = Array.isArray(services) ? services : [];

  /*
   * =========================================================
   * CÁC API NÀY ĐÃ TRẢ VỀ DỮ LIỆU ĐÃ CẤU HÌNH
   * =========================================================
   *
   * Không cần:
   *
   * isActivityConfigured()
   * isProductConfigured()
   * isServiceConfigured()
   *
   * Số lượng phần tử chính là số lượng đã cấu hình.
   */

  const configuredActivityCruises = safeActivityCruises.length;
  const configuredActivityVisits = safeActivityVisits.length;
  const configuredProducts = safeProducts.length;
  const configuredServices = safeServices.length;

  const cards = [
    {
      key: "activityCruise",
      label: "Hoạt động trên tàu",
      icon: Activity,
      configured: configuredActivityCruises,
      total: configuredActivityCruises,
    },
    {
      key: "activityVisit",
      label: "Hoạt động trên bờ",
      icon: MapPin,
      configured: configuredActivityVisits,
      total: configuredActivityVisits,
    },
    {
      key: "product",
      label: "Sản phẩm",
      icon: Package,
      configured: configuredProducts,
      total: configuredProducts,
    },
    {
      key: "service",
      label: "Dịch vụ",
      icon: Wrench,
      configured: configuredServices,
      total: configuredServices,
    },
  ];

  const handleCardClick = (key) => {
    if (!onFilterChange) return;

    const nextFilter = activeFilter === key ? "ALL" : key;

    onFilterChange(nextFilter);
  };

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
              {/* ICON */}
              <div className="tour-configuration-summary-card-icon">
                <Icon size={22} />
              </div>

              {/* CONTENT */}
              <div className="tour-configuration-summary-card-content">
                <span>{card.label}</span>

                <strong>
                  {card.configured}
                  <small> / {card.total}</small>
                </strong>

                <p>{card.total === 0 ? "Chưa có cấu hình" : "Đã cấu hình"}</p>
              </div>
            </div>
          );
        })}
      </div>
    </section>
  );
};

export default TourConfigurationSummary;
