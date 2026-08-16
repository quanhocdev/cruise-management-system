import React, { useEffect, useState } from "react";
import { Search, RefreshCw } from "lucide-react";
import useService from "../hooks/useService";
import ServiceTable from "../components/ServiceTable";
import "../styles/ConvenienceServices.css";

const ConvenienceServices = () => {
  const { services, loading, error, loadServices } = useService();
  const [searchTerm, setSearchTerm] = useState("");

  useEffect(() => {
    loadServices();
  }, [loadServices]);

  const filteredServices = services.filter(
    (item) =>
      item.name?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      item.description?.toLowerCase().includes(searchTerm.toLowerCase()),
  );

  return (
    <div className="convenience-page">
      {/* Header Trang */}
      <div className="page-header">
        <div>
          <h2 className="page-title">Danh sách Dịch vụ Tiện ích</h2>
          <p className="page-subtitle">
            Xem chi tiết thông tin và thời lượng dịch vụ tiện ích trên tàu
          </p>
        </div>
      </div>

      {/* Thanh công cụ Tìm kiếm */}
      <div className="page-toolbar">
        <div className="search-box">
          <Search size={18} className="search-icon" />
          <input
            type="text"
            placeholder="Tìm theo tên hoặc mô tả dịch vụ..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>
        <button
          type="button"
          className="btn btn-secondary"
          onClick={() => loadServices()}
          title="Làm mới"
        >
          <RefreshCw size={18} />
        </button>
      </div>

      {/* Báo lỗi nếu có */}
      {error && <div className="alert alert-danger">{error}</div>}

      {/* Bảng dữ liệu Read-only */}
      <div className="card">
        <ServiceTable services={filteredServices} loading={loading} />
      </div>
    </div>
  );
};

export default ConvenienceServices;
