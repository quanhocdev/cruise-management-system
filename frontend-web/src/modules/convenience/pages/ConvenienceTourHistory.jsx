// src/modules/convenience/pages/ConvenienceTourHistory.jsx

import React, { useState } from "react";
import { Package, Wrench } from "lucide-react";

import ConvenienceProductTourDetail from "../components/tour-config/ConvenienceProductTourDetail";
import ConvenienceServiceTourDetail from "../components/tour-config/ConvenienceServiceTourDetail";

import "../styles/tour-config/ConvenienceTourHistory.css";

const ConvenienceTourHistory = () => {
  const [activeTab, setActiveTab] = useState("product");

  return (
    <div className="convenience-tour-history">
      {/* HEADER */}
      <div className="convenience-tour-history-header">
        <div>
          <span className="convenience-tour-history-eyebrow">
            Convenience Management
          </span>

          <h1>Lịch sử cấu hình Tour</h1>

          <p>Xem lại các sản phẩm và dịch vụ đã được cấu hình cho từng Tour.</p>
        </div>
      </div>

      {/* TABS */}
      <div className="convenience-tour-history-tabs">
        <button
          type="button"
          className={
            activeTab === "product"
              ? "convenience-tour-history-tab active"
              : "convenience-tour-history-tab"
          }
          onClick={() => setActiveTab("product")}
        >
          <Package size={17} />
          <span>Sản phẩm</span>
        </button>

        <button
          type="button"
          className={
            activeTab === "service"
              ? "convenience-tour-history-tab active"
              : "convenience-tour-history-tab"
          }
          onClick={() => setActiveTab("service")}
        >
          <Wrench size={17} />
          <span>Dịch vụ</span>
        </button>
      </div>

      {/* CONTENT */}
      <div className="convenience-tour-history-content">
        {activeTab === "product" && <ConvenienceProductTourDetail />}

        {activeTab === "service" && <ConvenienceServiceTourDetail />}
      </div>
    </div>
  );
};

export default ConvenienceTourHistory;
