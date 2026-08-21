// src/modules/operation/components/tour-configuration/TourConfigurationSummary.jsx

import React from "react";
import { Activity, CheckCircle2, Package, Wrench } from "lucide-react";
import {
  isActivityConfigured,
  isProductConfigured,
  isServiceConfigured,
} from "../../utils/tourConfigurationUtils";

import "../../styles/tour-configuration/TourConfigurationSummary.css";

const TourConfigurationSummary = ({
  activities = [],
  products = [],
  services = [],
  configurationComplete = false,
  activeFilter = "ALL", // Filter hiện tại ("ALL", "activity", "product", "service")
  onFilterChange, // Callback truyền filter được chọn ra ngoài
}) => {
  const safeActivities = activities || [];
  const safeProducts = products || [];
  const safeServices = services || [];

  const configuredActivities =
    safeActivities.filter(isActivityConfigured).length;
  const configuredProducts = safeProducts.filter(isProductConfigured).length;
  const configuredServices = safeServices.filter(isServiceConfigured).length;

  const cards = [
    {
      key: "activity",
      label: "Hoạt động",
      icon: Activity,
      configured: configuredActivities,
      total: safeActivities.length,
    },
    {
      key: "product",
      label: "Sản phẩm",
      icon: Package,
      configured: configuredProducts,
      total: safeProducts.length,
    },
    {
      key: "service",
      label: "Dịch vụ",
      icon: Wrench,
      configured: configuredServices,
      total: safeServices.length,
    },
  ];

  const handleCardClick = (key) => {
    if (!onFilterChange) return;
    // Bấm lại ô đang chọn thì reset về xem tất cả ("ALL"), ngược lại thì lọc theo key
    const nextFilter = activeFilter === key ? "ALL" : key;
    onFilterChange(nextFilter);
  };

  return (
    <section className="tour-configuration-summary">
      <div className="tour-configuration-summary-header">
        <div>
          <h2>Tổng quan cấu hình</h2>
          <p>Tình trạng cấu hình các thành phần được phân công cho tour.</p>
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
            >
              <div className="tour-configuration-summary-card-icon">
                <Icon size={22} />
              </div>

              <div className="tour-configuration-summary-card-content">
                <span>{card.label}</span>

                <strong>
                  {card.configured}
                  <small> / {card.total}</small>
                </strong>

                <p>
                  {card.total === 0
                    ? "Không có phân công"
                    : card.configured === card.total
                      ? "Đã cấu hình đầy đủ"
                      : `${card.total - card.configured} mục chưa cấu hình`}
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
