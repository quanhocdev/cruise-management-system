import React, { useState, useCallback } from "react";
import TourSelector from "../components/TourSelector";
import DataTable from "../components/DataTable";
import LoadingSpinner from "../../../components/common/LoadingSpinner";
import financeService from "../services/financeService";
import { useTourSocket } from "../hooks/useTourSocket";
import "../styles/layout.css";

const FinanceCheckIn = () => {
  const [selectedTourId, setSelectedTourId] = useState("");
  const [scannedBookingCode, setScannedBookingCode] = useState("");
  const [passengers, setPassengers] = useState([]);
  const [loadingPassengers, setLoadingPassengers] = useState(false);

  const loadPassengers = async (bookingId) => {
    setLoadingPassengers(true);
    try {
      const data = await financeService.getPassengersByBooking(bookingId);
      setPassengers(data);
    } catch (error) {
      console.error("Không thể tải hành khách:", error);
    } finally {
      setLoadingPassengers(false);
    }
  };

  // Sử dụng useCallback để giữ nguyên tham chiếu của hàm callback qua các lần render,
  // tránh làm hook useTourSocket bị re-trigger (mount/unmount) liên tục.
  const handleBookingScanned = useCallback((notification) => {
    setScannedBookingCode(notification.bookingCode);
    loadPassengers(notification.bookingId);
  }, []);

  // Gọi Custom Hook xử lý socket với callback đã được tối ưu
  const { socketStatus } = useTourSocket(selectedTourId, handleBookingScanned);

  const passengerColumns = [
    { header: "Họ và tên", accessor: "fullName" },
    { header: "Giới tính", accessor: "gender" },
    { header: "Ngày sinh", accessor: "dateOfBirth" },
    { header: "Giấy tờ tùy thân", accessor: "identificationNumber" },
    { header: "Trạng thái", accessor: "status" },
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
            style={{ padding: "8px", width: "300px" }}
          />
        </div>
      )}

      {selectedTourId && loadingPassengers ? (
        <LoadingSpinner />
      ) : (
        <DataTable columns={passengerColumns} data={passengers} />
      )}
    </div>
  );
};

export default FinanceCheckIn;
