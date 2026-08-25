// src/modules/operation/components/tour-configuration/ServiceConfigurationTable.jsx

import React from "react";
import { CheckCircle2, Wrench, XCircle } from "lucide-react";

import "../../styles/tour-configuration/ServiceConfigurationTable.css";

const ServiceConfigurationTable = ({ services = [] }) => {
  const safeServices = Array.isArray(services) ? services : [];

  return (
    <section className="service-configuration-table-section">
      {/* =========================================================
          HEADER
          ========================================================= */}
      <div className="service-configuration-table-header">
        <div className="service-configuration-table-title">
          <div className="service-configuration-table-icon">
            <Wrench size={20} />
          </div>

          <div>
            <h2>Dịch vụ</h2>

            <p>Các dịch vụ đã được cấu hình cho Tour.</p>
          </div>
        </div>

        <span className="service-configuration-table-count">
          {safeServices.length} dịch vụ
        </span>
      </div>

      {/* =========================================================
          EMPTY
          ========================================================= */}
      {safeServices.length === 0 ? (
        <div className="service-configuration-table-empty">
          <XCircle size={24} />

          <span>Tour chưa có dịch vụ được cấu hình.</span>
        </div>
      ) : (
        /* =======================================================
           TABLE
           ======================================================= */
        <div className="service-configuration-table-wrapper">
          <table className="service-configuration-table">
            <thead>
              <tr>
                <th>Dịch vụ</th>
                <th>Mô tả</th>
                <th>Khách tối đa</th>
                <th>Trạng thái</th>
              </tr>
            </thead>

            <tbody>
              {safeServices.map((item, index) => (
                <tr key={item.id || `service-${index}`}>
                  {/* =================================================
                      SERVICE
                      ================================================= */}
                  <td>
                    <div className="service-configuration-name">
                      <strong>{item.serviceName || "Chưa xác định"}</strong>

                      {item.serviceId && <span>{item.serviceId}</span>}
                    </div>
                  </td>

                  {/* =================================================
                      DESCRIPTION
                      ================================================= */}
                  <td className="service-configuration-description">
                    {item.description || "—"}
                  </td>

                  {/* =================================================
                      MAX PASSENGERS
                      ================================================= */}
                  <td>
                    {item.maxPassengers != null ? item.maxPassengers : "—"}
                  </td>

                  {/* =================================================
                      STATUS
                      ================================================= */}
                  <td>
                    <span className="service-configuration-status configured">
                      <CheckCircle2 size={14} />
                      Đã cấu hình
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </section>
  );
};

export default ServiceConfigurationTable;
