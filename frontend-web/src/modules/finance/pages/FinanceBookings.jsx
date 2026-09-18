// src/modules/finance/pages/FinanceBookings.jsx
import React, { useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import TourSelector from "../components/TourSelector";
import FilterToolbar from "../components/FilterToolbar";
import DataTable from "../components/DataTable";
import LoadingSpinner from "../../../components/common/LoadingSpinner";
import { useFinanceBookings } from "../hooks/useFinanceBookings";
import { BOOKING_STATUS_OPTIONS, labelOf } from "../constants/statuses";
import { matchesSearch, formatDateTime } from "../utils/format";
import "../styles/layout.css";

const FinanceBookings = () => {
  const navigate = useNavigate();
  const [selectedTourId, setSelectedTourId] = useState("");
  const [filterStatus, setFilterStatus] = useState("ALL");
  const [searchTerm, setSearchTerm] = useState("");

  const { bookings, loading, error, reload } =
    useFinanceBookings(selectedTourId);

  const filteredBookings = useMemo(() => {
    if (!bookings) return [];
    return bookings.filter(
      (b) =>
        (filterStatus === "ALL" || b.status === filterStatus) &&
        matchesSearch(
          searchTerm,
          b.bookingCode,
          b.primaryContactName,
          b.primaryContactPhone,
        ),
    );
  }, [bookings, filterStatus, searchTerm]);

  const columns = [
    { header: "Mã đơn", accessor: "bookingCode" },
    { header: "Người liên hệ", accessor: "primaryContactName" },
    { header: "Số điện thoại", accessor: "primaryContactPhone" },
    { header: "Số khách", accessor: "numberPassengers" },
    { header: "Số phòng", accessor: "numberOfRooms" },
    {
      header: "Tổng tiền",
      accessor: "totalAmount",
      render: (v) => `${Number(v || 0).toLocaleString("vi-VN")} đ`,
    },
    {
      header: "Trạng thái",
      accessor: "status",
      render: (v) => labelOf(BOOKING_STATUS_OPTIONS, v), // <-- Dùng labelOf để dịch sang tiếng Việt
    },
    { header: "Ngày tạo", accessor: "createdAt", render: formatDateTime },
  ];

  return (
    <div className="finance-page">
      <div className="finance-page__header">
        <div>
          <h2 className="finance-page__title">Quản lý Đơn đặt Tour</h2>
          <p className="finance-page__subtitle">
            Danh sách đơn đặt vé và thông tin liên hệ theo tour
          </p>
        </div>
      </div>

      <div className="finance-card">
        <TourSelector
          selectedTourId={selectedTourId}
          onSelectTour={setSelectedTourId}
        />
        <div style={{ marginTop: 16 }}>
          <FilterToolbar
            filterStatus={filterStatus}
            onStatusChange={setFilterStatus}
            searchTerm={searchTerm}
            onSearchChange={setSearchTerm}
            statusOptions={BOOKING_STATUS_OPTIONS} // <-- Truyền thẳng vào bộ lọc
            searchPlaceholder="Tìm theo mã đơn, tên hoặc SĐT..."
          />
        </div>
      </div>

      {error && (
        <div className="finance-error-banner">
          <span>Không tải được danh sách đơn đặt tour.</span>
          <button onClick={reload}>Thử lại</button>
        </div>
      )}

      {!selectedTourId ? (
        <div
          className="finance-card"
          style={{ textAlign: "center", color: "var(--fin-gray-500)" }}
        >
          Vui lòng chọn một Tour ở phía trên để xem danh sách đơn hàng.
        </div>
      ) : loading ? (
        <LoadingSpinner />
      ) : (
        <DataTable
          columns={columns}
          data={filteredBookings}
          onRowClick={(row) =>
            navigate(`/finance/bookings/${row.id}/passengers`)
          }
          emptyText="Tour này chưa có đơn đặt chỗ nào"
        />
      )}
    </div>
  );
};

export default FinanceBookings;
