// src/modules/convenience/tour-config/ConvenienceTourConfig.jsx
import React, { useState } from "react";
import { Package, Wrench } from "lucide-react";

import ProductTourConfigTable from "./ProductTourConfigTable";
import ServiceTourConfigTable from "./ServiceTourConfigTable";
import "../../styles/tour-config/ConvenienceTourConfig.css";

const ConvenienceTourConfig = () => {
  const [activeTab, setActiveTab] = useState("product");

  return (
    <div className="convenience-tour-config">
      {/* =====================================================
          HEADER
          ===================================================== */}

      <div className="convenience-tour-config-header">
        <div>
          <span className="convenience-tour-config-eyebrow">
            Convenience Management
          </span>

          <h1>Cấu hình tiện ích Tour</h1>

          <p>Cấu hình sản phẩm và dịch vụ được phân công cho từng Tour.</p>
        </div>
      </div>

      {/* =====================================================
          TABS
          ===================================================== */}

      <div className="convenience-tour-config-tabs">
        <button
          type="button"
          className={
            activeTab === "product"
              ? "convenience-tour-config-tab active"
              : "convenience-tour-config-tab"
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
              ? "convenience-tour-config-tab active"
              : "convenience-tour-config-tab"
          }
          onClick={() => setActiveTab("service")}
        >
          <Wrench size={17} />
          <span>Dịch vụ</span>
        </button>
      </div>

      {/* =====================================================
          CONTENT
          ===================================================== */}

      <div className="convenience-tour-config-content">
        {activeTab === "product" && <ProductTourConfigTable />}

        {activeTab === "service" && <ServiceTourConfigTable />}
      </div>
    </div>
  );
};

export default ConvenienceTourConfig;
