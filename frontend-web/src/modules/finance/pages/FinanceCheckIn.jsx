// src/modules/finance/pages/FinanceCheckIn.jsx
import React, { useState, useCallback } from "react";
import TourSelector from "../components/TourSelector";
import DataTable from "../components/DataTable";
import LoadingSpinner from "../../../components/common/LoadingSpinner";
import financeService from "../services/financeService";
import { useTourSocket } from "../hooks/useTourSocket";
import CheckInPassengerModal from "../components/CheckInPassengerModal";
import "../styles/layout.css";
import "../styles/financeCheckIn.css";

const FinanceCheckIn = () => {
  const [selectedTourId, setSelectedTourId] = useState("");
  const [currentBookingId, setCurrentBookingId] = useState(null);
  const [scannedBookingCode, setScannedBookingCode] = useState("");
  const [passengers, setPassengers] = useState([]);
  const [loadingPassengers, setLoadingPassengers] = useState(false);
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

  const handleManualSearch = async (bookingCode) => {
    if (!bookingCode.trim() || !selectedTourId) return;

    setLoadingPassengers(true);
    try {
      const bookings = await financeService.getBookingsByTour(selectedTourId);
      const found = bookings.find(
        (b) => b.bookingCode.toLowerCase() === bookingCode.trim().toLowerCase(),
      );

      if (found) {
        setCurrentBookingId(found.id);
        loadPassengers(found.id);
      } else {
        alert("Không tìm thấy mã booking này trong tour hiện tại!");
        setPassengers([]);
        setCurrentBookingId(null);
      }
    } catch (err) {
      console.error("Lỗi tìm kiếm booking:", err);
      alert("Lỗi khi tìm kiếm mã booking!");
    } finally {
      setLoadingPassengers(false);
    }
  };

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
        <div className="finance-card finance-checkin__container">
          <label style={{ fontSize: "14px", fontWeight: "bold" }}>
            Mã Booking (Quét QR hoặc Nhập tay):
          </label>
          <div className="finance-checkin__input-group">
            <input
              type="text"
              className="finance-checkin__input"
              value={scannedBookingCode}
              onChange={(e) => setScannedBookingCode(e.target.value)}
              onKeyDown={(e) => {
                if (e.key === "Enter") {
                  handleManualSearch(scannedBookingCode);
                }
              }}
              placeholder="Nhập mã booking hoặc chờ quét QR..."
            />
            <button
              type="button"
              className="finance-checkin__btn-search"
              onClick={() => handleManualSearch(scannedBookingCode)}
            >
              Tải danh sách
            </button>
          </div>
        </div>
      )}

      {selectedTourId && loadingPassengers ? (
        <LoadingSpinner />
      ) : (
        <DataTable columns={passengerColumns} data={passengers} />
      )}

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
