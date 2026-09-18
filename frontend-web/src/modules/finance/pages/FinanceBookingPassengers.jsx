// src/modules/finance/pages/FinanceBookingPassengers.jsx
import React from "react";
import { useParams, useNavigate } from "react-router-dom";
import DataTable from "../components/DataTable";
import LoadingSpinner from "../../../components/common/LoadingSpinner";
import { useBookingPassengers } from "../hooks/useFinanceBookings";
import { formatDateTime } from "../utils/format";
import "../styles/layout.css";

const FinanceBookingPassengers = () => {
  const { bookingId } = useParams();
  const navigate = useNavigate();

  const { passengers, loading, error, reload } =
    useBookingPassengers(bookingId);

  const columns = [
    { header: "Họ và tên", accessor: "fullName" },
    { header: "Giới tính", accessor: "gender" },
    { header: "Ngày sinh", accessor: "dateOfBirth" },
    { header: "Số điện thoại", accessor: "phoneNumber" },
    { header: "Giấy tờ tùy thân", accessor: "identificationNumber" },
    { header: "Mã NFC UID", accessor: "nfcCardUid", render: (v) => v || "—" },
    { header: "Trạng thái", accessor: "status" },
    {
      header: "Thời gian Check-in",
      accessor: "checkedInAt",
      render: formatDateTime,
    },
  ];

  return (
    <div className="finance-page">
      <div className="finance-page__header">
        <div>
          <button
            onClick={() => navigate(-1)}
            style={{
              background: "none",
              border: "none",
              color: "var(--fin-primary)",
              cursor: "pointer",
              padding: 0,
              marginBottom: 8,
              fontWeight: 600,
              display: "flex",
              alignItems: "center",
              gap: 4,
            }}
          >
            ← Quay lại danh sách đơn
          </button>
          <h2 className="finance-page__title">
            Danh sách Hành khách trong Đơn
          </h2>
          <p className="finance-page__subtitle">
            Chi tiết thành viên đi tour, phân bổ phòng và trạng thái check-in
          </p>
        </div>
      </div>

      {error && (
        <div className="finance-error-banner">
          <span>Không tải được danh sách hành khách.</span>
          <button onClick={reload}>Thử lại</button>
        </div>
      )}

      {loading ? (
        <LoadingSpinner />
      ) : (
        <DataTable
          columns={columns}
          data={passengers}
          emptyText="Đơn hàng này chưa có hành khách nào"
        />
      )}
    </div>
  );
};

export default FinanceBookingPassengers;
