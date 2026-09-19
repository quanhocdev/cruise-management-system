import React, { useState, useCallback } from "react";
import TourSelector from "../components/TourSelector";
import DataTable from "../components/DataTable";
import LoadingSpinner from "../../../components/common/LoadingSpinner";
import financeService from "../services/financeService";
import { useTourSocket } from "../hooks/useTourSocket";
import { useRoomTypes } from "../hooks/useFinanceRoom";
import {
  useAvailableRooms,
  useAvailableWristbands,
} from "../hooks/useFinanceCheckIn";
import "../styles/layout.css";

// Component Modal phục vụ check-in từng hành khách
const CheckInModal = ({ tourId, bookingId, passenger, onClose, onSuccess }) => {
  const [selectedRoomTypeId, setSelectedRoomTypeId] = useState("");
  const [selectedRoomId, setSelectedRoomId] = useState("");
  const [selectedNfcCode, setSelectedNfcCode] = useState("");
  const [submitting, setSubmitting] = useState(false);

  // Lấy danh mục hạng phòng
  const { roomTypes } = useRoomTypes();

  // Lấy danh sách phòng trống dựa theo booking và hạng phòng
  const { availableRooms, loading: loadingRooms } = useAvailableRooms(
    bookingId,
    selectedRoomTypeId,
  );

  // Lấy danh sách vòng NFC khả dụng (AVAILABLE)
  const { availableWristbands, loading: loadingNfc } =
    useAvailableWristbands(tourId);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!selectedRoomId || !selectedNfcCode) {
      alert("Vui lòng chọn đầy đủ phòng và mã vòng NFC!");
      return;
    }

    setSubmitting(true);
    try {
      await financeService.checkInPassenger(bookingId, {
        passengerId: passenger.id,
        roomId: selectedRoomId,
        nfcCode: selectedNfcCode,
      });
      alert("Check-in thành công cho hành khách!");
      onSuccess?.(); // Gọi callback để load lại danh sách hành khách
      onClose();
    } catch (err) {
      console.error("Lỗi khi check-in:", err);
      alert(err.response?.data?.message || "Check-in thất bại!");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div
      style={{
        position: "fixed",
        top: 0,
        left: 0,
        width: "100%",
        height: "100%",
        backgroundColor: "rgba(0,0,0,0.5)",
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        zIndex: 1000,
      }}
    >
      <div
        style={{
          backgroundColor: "white",
          padding: "24px",
          borderRadius: "8px",
          width: "450px",
          boxShadow: "0 4px 12px rgba(0,0,0,0.15)",
        }}
      >
        <h3 style={{ marginTop: 0, marginBottom: "16px" }}>
          Check-in Hành khách
        </h3>
        <p style={{ marginBottom: "12px", color: "#555" }}>
          Hành khách: <b>{passenger.fullName}</b> (ID: {passenger.id})
        </p>

        <form
          onSubmit={handleSubmit}
          style={{ display: "flex", flexDirection: "column", gap: "12px" }}
        >
          {/* 1. Chọn Hạng phòng */}
          <div>
            <label
              style={{
                display: "block",
                fontSize: "14px",
                fontWeight: "bold",
                marginBottom: "4px",
              }}
            >
              1. Chọn hạng phòng:
            </label>
            <select
              style={{
                width: "100%",
                padding: "8px",
                borderRadius: "4px",
                border: "1px solid #ccc",
              }}
              value={selectedRoomTypeId}
              onChange={(e) => {
                setSelectedRoomTypeId(e.target.value);
                setSelectedRoomId(""); // Reset phòng khi đổi hạng
              }}
            >
              <option value="">-- Chọn hạng phòng --</option>
              {roomTypes.map((rt) => (
                <option key={rt.id} value={rt.id}>
                  {rt.name}
                </option>
              ))}
            </select>
          </div>

          {/* 2. Chọn Số phòng trống */}
          <div>
            <label
              style={{
                display: "block",
                fontSize: "14px",
                fontWeight: "bold",
                marginBottom: "4px",
              }}
            >
              2. Chọn phòng:
            </label>
            <select
              style={{
                width: "100%",
                padding: "8px",
                borderRadius: "4px",
                border: "1px solid #ccc",
              }}
              value={selectedRoomId}
              onChange={(e) => setSelectedRoomId(e.target.value)}
              disabled={!selectedRoomTypeId || loadingRooms}
            >
              <option value="">
                {loadingRooms ? "Đang tải phòng..." : "-- Chọn phòng trống --"}
              </option>
              {availableRooms.map((room) => (
                <option key={room.id} value={room.id}>
                  Phòng {room.code}
                </option>
              ))}
            </select>
          </div>

          {/* 3. Chọn Vòng NFC */}
          <div>
            <label
              style={{
                display: "block",
                fontSize: "14px",
                fontWeight: "bold",
                marginBottom: "4px",
              }}
            >
              3. Chọn vòng NFC (AVAILABLE):
            </label>
            <select
              style={{
                width: "100%",
                padding: "8px",
                borderRadius: "4px",
                border: "1px solid #ccc",
              }}
              value={selectedNfcCode}
              onChange={(e) => setSelectedNfcCode(e.target.value)}
              disabled={loadingNfc}
            >
              <option value="">
                {loadingNfc ? "Đang tải thẻ NFC..." : "-- Chọn mã thẻ NFC --"}
              </option>
              {availableWristbands.map((card) => (
                <option key={card.id || card.cardUid} value={card.cardUid}>
                  Mã thẻ: {card.cardUid}
                </option>
              ))}
            </select>
          </div>

          <div
            style={{
              display: "flex",
              justifyContent: "flex-end",
              gap: "8px",
              marginTop: "16px",
            }}
          >
            <button
              type="button"
              onClick={onClose}
              style={{
                padding: "8px 16px",
                background: "#ccc",
                border: "none",
                borderRadius: "4px",
                cursor: "pointer",
              }}
            >
              Hủy
            </button>
            <button
              type="submit"
              disabled={submitting}
              style={{
                padding: "8px 16px",
                background: "#007bff",
                color: "white",
                border: "none",
                borderRadius: "4px",
                cursor: "pointer",
              }}
            >
              {submitting ? "Đang lưu..." : "Xác nhận Check-in"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

const FinanceCheckIn = () => {
  const [selectedTourId, setSelectedTourId] = useState("");
  const [currentBookingId, setCurrentBookingId] = useState(null); // Lưu bookingId hiện tại khi quét/chọn
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

  // Định nghĩa các cột hiển thị trên bảng hành khách kèm nút Thao tác Check-in
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

      {/* Hiển thị Modal Check-in khi nhân viên bấm nút */}
      {activePassengerModal && currentBookingId && (
        <CheckInModal
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
