// src/modules/finance/components/CheckInPassengerModal.jsx
import React, { useState } from "react";
import {
  useAvailableRooms,
  useAvailableWristbands,
} from "../hooks/useFinanceCheckIn";
import financeService from "../services/financeService";

export default function CheckInPassengerModal({
  tourId,
  bookingId,
  passenger,
  onClose,
  onSuccess,
}) {
  const [selectedRoomId, setSelectedRoomId] = useState("");
  const [selectedNfcCode, setSelectedNfcCode] = useState("");
  const [submitting, setSubmitting] = useState(false);

  // 1. Lấy trực tiếp tourPackageId từ hành khách (đã được map từ booking sang)
  const tourPackageId = passenger.tourPackageId;

  // 2. Gọi hook lấy phòng trống bằng tourPackageId
  const { availableRooms, loading: loadingRooms } = useAvailableRooms(
    bookingId,
    tourPackageId,
  );

  // Lấy danh sách vòng NFC khả dụng
  const { availableWristbands, loading: loadingNfc } = useAvailableWristbands();

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!selectedRoomId || !selectedNfcCode) {
      alert("Vui lòng chọn đầy đủ phòng và mã vòng NFC!");
      return;
    }

    setSubmitting(true);
    try {
      await financeService.checkInPassenger(bookingId, {
        passengerId: passenger.passengerId || passenger.id,
        roomId: selectedRoomId,
        nfcCode: selectedNfcCode,
      });
      alert("Check-in thành công cho hành khách!");
      onSuccess?.();
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

        <div
          style={{
            marginBottom: "16px",
            padding: "10px",
            background: "#f8f9fa",
            borderRadius: "4px",
          }}
        >
          <p style={{ margin: "4px 0" }}>
            Hành khách: <b>{passenger.fullName}</b>
          </p>
        </div>

        <form
          onSubmit={handleSubmit}
          style={{ display: "flex", flexDirection: "column", gap: "14px" }}
        >
          {/* 1. Chọn số phòng trống */}
          <div>
            <label
              style={{
                display: "block",
                fontSize: "14px",
                fontWeight: "bold",
                marginBottom: "4px",
              }}
            >
              1. Chọn số phòng trống:
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
              disabled={loadingRooms}
            >
              <option value="">
                {loadingRooms ? "Đang tải phòng..." : "-- Chọn số phòng --"}
              </option>
              {availableRooms.map((room) => (
                <option key={room.id} value={room.id}>
                  Phòng {room.code}
                </option>
              ))}
            </select>
          </div>

          {/* 2. Chọn Vòng NFC */}
          <div>
            <label
              style={{
                display: "block",
                fontSize: "14px",
                fontWeight: "bold",
                marginBottom: "4px",
              }}
            >
              2. Chọn vòng NFC (AVAILABLE):
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
}
