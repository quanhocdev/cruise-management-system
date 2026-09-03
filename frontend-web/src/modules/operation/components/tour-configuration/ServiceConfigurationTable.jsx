// src/modules/operation/components/tour-configuration/ServiceConfigurationTable.jsx

import React from "react";
import { CheckCircle2, Wrench, XCircle } from "lucide-react";
import {
  formatVND,
  getTourStatusMeta,
  isTourItemConfigured,
} from "../../utils/tourConfigurationUtils";

import "../../styles/tour-configuration/ServiceConfigurationTable.css";

const ServiceConfigurationTable = ({ services = [] }) => {
  const safeServices = Array.isArray(services) ? services : [];

  // =========================================================
  // SỐ LƯỢNG THỰC SỰ ĐÃ CẤU HÌNH (status !== WAITING_CONFIG)
  // =========================================================

  const configuredCount = safeServices.filter((item) =>
    isTourItemConfigured(item.status),
  ).length;

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

        {/* ✅ hiển thị rõ số đã cấu hình / tổng số đã phân công */}
        <span className="service-configuration-table-count">
          {configuredCount}/{safeServices.length} đã cấu hình
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
                <th>Giá</th>
                <th>Trạng thái</th>
              </tr>
            </thead>

            <tbody>
              {safeServices.map((item, index) => {
                // 🔍 Log kiểm tra cấu trúc dữ liệu trả về thực tế từ Backend
                console.log("Service item data:", item);

                const statusMeta = getTourStatusMeta(item.status);
                const configured = isTourItemConfigured(item.status);

                // Fallback linh hoạt tên trường
                const displayName =
                  item.serviceName || item.name || "Chưa xác định";

                const displayDescription =
                  item.description || item.serviceDescription || "—";

                const displayPrice = item.price;

                return (
                  <tr key={item.id || item.serviceId || `service-${index}`}>
                    {/* =================================================
                        SERVICE
                        ================================================= */}
                    <td>
                      <div className="service-configuration-name">
                        <strong>{displayName}</strong>

                        {item.serviceId && <span>{item.serviceId}</span>}
                      </div>
                    </td>

                    {/* =================================================
                        DESCRIPTION
                        ================================================= */}
                    <td className="service-configuration-description">
                      {displayDescription}
                    </td>

                    {/* =================================================
                        MAX PASSENGERS
                        ================================================= */}
                    <td>
                      {item.maxPassengers != null ? item.maxPassengers : "—"}
                    </td>

                    {/* =================================================
                        PRICE (ĐÃ BỔ SUNG CỘT GIÁ)
                        ================================================= */}
                    <td>{formatVND(displayPrice)}</td>

                    {/* =================================================
                        STATUS
                        ================================================= */}
                    <td>
                      <span
                        className={`service-configuration-status ${statusMeta.className}`}
                      >
                        {configured ? (
                          <CheckCircle2 size={14} />
                        ) : (
                          <XCircle size={14} />
                        )}
                        {statusMeta.label}
                      </span>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      )}
    </section>
  );
};

export default ServiceConfigurationTable;
