// src/modules/finance/pages/FinanceCheckIn.jsx
import React, { useState, useCallback } from "react";
import TourSelector from "../components/TourSelector";
import DataTable from "../components/DataTable";
import LoadingSpinner from "../../../components/common/LoadingSpinner";
import financeService from "../services/financeService";
import { useTourSocket } from "../hooks/useTourSocket";
import CheckInPassengerModal from "../components/CheckInPassengerModal";
import "../styles/layout.css";

const FinanceCheckIn = () => {
  const [selectedTourId, setSelectedTourId] = useState("");
  const [currentBookingId, setCurrentBookingId] = useState(null);
  const [scannedBookingCode, setScannedBookingCode] = useState("");
  const [passengers, setPassengers] = useState([]);
  const [loadingPassengers, setLoadingPassengers] = useState(false);

  // State quản lý Modal check-in cho hành khách cụ thể
  const [activePassengerModal, setActivePassengerModal] = useState(null);

  const loadPassengers = async (bookingId) => {
    setLoadingPassengers(true);
    try {
      const data = await financeService.getPassengersByBooking(bookingId);
      setPassengers(data);
    } catch (error) {
      console.error("Không thể tải hành khách:", error);
      setPassengers([]);
    } finally {
      setLoadingPassengers(false);
    }
  };

  const handleBookingScanned = useCallback((notification) => {
    setScannedBookingCode(notification.bookingCode);
    setCurrentBookingId(notification.bookingId);
    loadPassengers(notification.bookingId);
  }, []);

  const { socketStatus } = useTourSocket(selectedTourId, handleBookingScanned);

  const passengerColumns = [
    { header: "Họ và tên", accessor: "fullName" },
    { header: "Giới tính", accessor: "gender" },
    { header: "Ngày sinh", accessor: "dateOfBirth" },
    { header: "Giấy tờ tùy thân", accessor: "identificationNumber" },
    { header: "Trạng thái", accessor: "status" },
    {
      header: "Thao tác",
      accessor: "actions",
      render: (_, row) =>
        row.status === "CHECKED_IN" ? (
          <span style={{ color: "green", fontWeight: "bold" }}>
            Đã Check-in
          </span>
        ) : (
          <button
            onClick={() => setActivePassengerModal(row)}
            style={{
              padding: "6px 12px",
              backgroundColor: "#28a745",
              color: "white",
              border: "none",
              borderRadius: "4px",
              cursor: "pointer",
            }}
          >
            Check-in ngay
          </button>
        ),
    },
  ];

  return (
    <div className="finance-page">
      <div className="finance-page__header">
        <h2 className="finance-page__title">Quầy Check-in Trực tiếp</h2>
      </div>

      <div className="finance-card">
        <TourSelector
          selectedTourId={selectedTourId}
          onSelectTour={(id) => {
            setSelectedTourId(id);
            setCurrentBookingId(null);
            setScannedBookingCode("");
            setPassengers([]);
          }}
        />
        <div style={{ marginTop: 12 }}>Trạng thái kênh: {socketStatus}</div>
      </div>

      {selectedTourId && (
        <div className="finance-card">
          <label>Mã Booking đang xử lý: </label>
          <input
            type="text"
            value={scannedBookingCode}
            onChange={(e) => setScannedBookingCode(e.target.value)}
            placeholder="Chờ quét mã QR / thẻ từ POS..."
            style={{ padding: "8px", width: "300px" }}
            readOnly
          />
        </div>
      )}

      {selectedTourId && loadingPassengers ? (
        <LoadingSpinner />
      ) : (
        <DataTable columns={passengerColumns} data={passengers} />
      )}

      {/* Gọi component Modal độc lập */}
      {activePassengerModal && currentBookingId && (
        <CheckInPassengerModal
          tourId={selectedTourId}
          bookingId={currentBookingId}
          passenger={activePassengerModal}
          onClose={() => setActivePassengerModal(null)}
          onSuccess={() => loadPassengers(currentBookingId)}
        />
      )}
    </div>
  );
};

export default FinanceCheckIn;
