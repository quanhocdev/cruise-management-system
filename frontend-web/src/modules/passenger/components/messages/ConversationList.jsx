// src/modules/passenger/components/messages/ConversationList.jsx

import { useState } from "react";
import { Button, Spinner } from "react-bootstrap";
import { MessageCircle, Search } from "lucide-react";
import "./ConversationList.css";

const DEPARTMENTS = [
  { value: "ONBOARD", label: "Nhân viên trên du thuyền" },
  { value: "CONVENIENCE", label: "Dịch vụ tiện ích" },
  { value: "FINANCE", label: "Tài chính" },
  { value: "SHORE", label: "Hoạt động bờ" },
  { value: "SCHEDULER", label: "Điều phối" },
];

const getBookingId = (booking) => booking.bookingId;

const getBookingLabel = (booking) =>
  booking.bookingCode || `Booking #${getBookingId(booking)}`;

const formatDate = (value) => {
  if (!value) return "Chưa có thông tin";

  return new Date(value).toLocaleString("vi-VN", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
};

const formatAmount = (value) => {
  if (value == null) return "Chưa có thông tin";

  return Number(value).toLocaleString("vi-VN") + " VNĐ";
};

export default function ConversationList({
  bookings = [],
  conversations = [],
  selectedConversationId,
  bookingsLoading,
  bookingsError,
  loading,
  creating,
  onSelect,
  onCreate,
}) {
  const [keyword, setKeyword] = useState("");

  const filteredBookings = bookings.filter((booking) => {
    const searchText = [
      booking.bookingCode,
      booking.bookingId,
      booking.recipientName,
    ]
      .join(" ")
      .toLowerCase();

    return searchText.includes(keyword.trim().toLowerCase());
  });

  const getConversation = (bookingId, department) =>
    conversations.find(
      (item) =>
        String(item.bookingId) === String(bookingId) &&
        item.department === department,
    );

  const handleDepartmentClick = async (bookingId, department) => {
    if (creating) return;

    await onCreate(bookingId, department);
  };

  return (
    <aside className="passenger-conversation-list">
      <div className="passenger-conversation-header">
        <div>
          <h5>Tin nhắn</h5>
          <p>Chọn booking và phòng ban cần hỗ trợ</p>
        </div>
      </div>

      <div className="passenger-conversation-search">
        <Search size={17} />

        <input
          value={keyword}
          onChange={(event) => setKeyword(event.target.value)}
          placeholder="Tìm mã booking..."
        />
      </div>

      <div className="passenger-conversation-items">
        {bookingsLoading ? (
          <div className="passenger-conversation-state">
            <Spinner animation="border" size="sm" />
            <span>Đang tải booking...</span>
          </div>
        ) : bookingsError ? (
          <div className="passenger-conversation-empty">
            <p>Không thể tải danh sách booking</p>
            <small>{bookingsError}</small>
          </div>
        ) : filteredBookings.length === 0 ? (
          <div className="passenger-conversation-empty">
            <MessageCircle size={30} />
            <p>
              {bookings.length === 0
                ? "Bạn chưa có booking nào được xác nhận"
                : "Không tìm thấy booking"}
            </p>
            <small>Booking đã xác nhận thanh toán sẽ xuất hiện tại đây.</small>
          </div>
        ) : (
          filteredBookings.map((booking) => {
            const bookingId = getBookingId(booking);

            return (
              <div className="passenger-booking-card" key={bookingId}>
                <div className="passenger-booking-card-header">
                  <div className="passenger-booking-icon">
                    <MessageCircle size={20} />
                  </div>

                  <div className="passenger-booking-info">
                    <strong>{getBookingLabel(booking)}</strong>
                    <small>Xác nhận: {formatDate(booking.confirmedAt)}</small>
                  </div>
                </div>

                <div className="passenger-booking-details">
                  <div>
                    <span>Người đặt</span>
                    <strong>
                      {booking.recipientName || "Chưa có thông tin"}
                    </strong>
                  </div>

                  <div>
                    <span>Số hành khách</span>
                    <strong>{booking.numberPassengers ?? 0}</strong>
                  </div>

                  <div>
                    <span>Tổng tiền</span>
                    <strong>{formatAmount(booking.totalAmount)}</strong>
                  </div>
                </div>

                <div className="passenger-booking-departments">
                  <span className="passenger-booking-department-title">
                    Liên hệ phòng ban
                  </span>

                  <div className="passenger-booking-department-list">
                    {DEPARTMENTS.map((department) => {
                      const conversation = getConversation(
                        bookingId,
                        department.value,
                      );

                      const isSelected =
                        conversation &&
                        conversation.id === selectedConversationId;

                      return (
                        <Button
                          key={department.value}
                          size="sm"
                          variant={
                            isSelected
                              ? "primary"
                              : conversation
                                ? "outline-primary"
                                : "light"
                          }
                          className="passenger-booking-department-button"
                          disabled={creating}
                          onClick={() =>
                            handleDepartmentClick(bookingId, department.value)
                          }
                          title={department.label}
                        >
                          <span>{department.label}</span>

                          {conversation && (
                            <small>
                              {conversation.status === "CLOSED"
                                ? "Đã đóng"
                                : "Đã có hội thoại"}
                            </small>
                          )}
                        </Button>
                      );
                    })}
                  </div>
                </div>
              </div>
            );
          })
        )}
      </div>

      {loading && (
        <div className="passenger-conversation-footer">
          <Spinner animation="border" size="sm" />
          <span>Đang tải hội thoại...</span>
        </div>
      )}
    </aside>
  );
}
