// src/modules/operation/components/tour-configuration/ServiceConfigurationTable.jsx

import React from "react";
import { CheckCircle2, Wrench, XCircle } from "lucide-react";
import { isServiceConfigured } from "../../utils/tourConfigurationUtils";

import "../../styles/tour-configuration/ServiceConfigurationTable.css";

const ServiceConfigurationTable = ({ services = [] }) => {
  const safeServices = services || [];

  const getStatusLabel = (status) => {
    switch (status) {
      case "WAITING_CONFIG":
        return "Chờ cấu hình";
      case "NOT_STARTED":
        return "Đã cấu hình";
      case "IN_PROGRESS":
        return "Đang hoạt động";
      case "COMPLETED":
        return "Đã kết thúc";
      default:
        return status || "Không xác định";
    }
  };

  return (
    <section className="service-configuration-table-section">
      <div className="service-configuration-table-header">
        <div className="service-configuration-table-title">
          <div className="service-configuration-table-icon">
            <Wrench size={20} />
          </div>

          <div>
            <h2>Dịch vụ</h2>
            <p>Các dịch vụ được Operation phân công cho tour.</p>
          </div>
        </div>

        <span className="service-configuration-table-count">
          {safeServices.length} phân công
        </span>
      </div>

      {safeServices.length === 0 ? (
        <div className="service-configuration-table-empty">
          <XCircle size={24} />
          <span>Tour chưa được phân công dịch vụ.</span>
        </div>
      ) : (
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
              {safeServices.map((item, index) => {
                const configured = isServiceConfigured(item);

                return (
                  <tr key={item.id || `service-${index}`}>
                    <td>
                      <div className="service-configuration-name">
                        <strong>
                          {item.serviceName || "Chưa chọn dịch vụ"}
                        </strong>

                        {item.serviceId && <span>{item.serviceId}</span>}
                      </div>
                    </td>

                    <td className="service-configuration-description">
                      {item.description || "—"}
                    </td>

                    <td>
                      {item.maxPassengers != null ? item.maxPassengers : "—"}
                    </td>

                    <td>
                      <span
                        className={`service-configuration-status ${
                          configured ? "configured" : "waiting"
                        }`}
                      >
                        {configured ? (
                          <CheckCircle2 size={14} />
                        ) : (
                          <XCircle size={14} />
                        )}

                        {configured
                          ? "Đã cấu hình"
                          : getStatusLabel(item.status)}
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
