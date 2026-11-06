// src/modules/passenger/components/messages/ConversationList.jsx

import { useState } from "react";
import { Button, Form, Spinner } from "react-bootstrap";
import { MessageCircle, Plus, Search } from "lucide-react";
import "./ConversationList.css";

const DEPARTMENTS = [
  { value: "ONBOARD", label: "Nhân viên trên du thuyền" },
  { value: "CONVENIENCE", label: "Dịch vụ tiện ích" },
  { value: "FINANCE", label: "Tài chính" },
  { value: "SHORE", label: "Hoạt động bờ" },
  { value: "SCHEDULER", label: "Điều phối" },
];

const DEPARTMENT_LABELS = Object.fromEntries(
  DEPARTMENTS.map((item) => [item.value, item.label]),
);

const getBookingLabel = (booking) =>
  booking.bookingCode || booking.code || `Booking #${booking.id}`;

const formatTime = (value) => {
  if (!value) return "";

  return new Date(value).toLocaleString("vi-VN", {
    hour: "2-digit",
    minute: "2-digit",
    day: "2-digit",
    month: "2-digit",
  });
};

export default function ConversationList({
  bookings = [],
  conversations = [],
  selectedConversationId,
  bookingsLoading,
  loading,
  creating,
  onSelect,
  onCreate,
}) {
  const [keyword, setKeyword] = useState("");
  const [bookingId, setBookingId] = useState("");
  const [department, setDepartment] = useState("SHORE");
  const [showCreateForm, setShowCreateForm] = useState(false);

  const filteredConversations = conversations.filter((item) => {
    const searchText = [
      item.bookingCode,
      item.bookingId,
      DEPARTMENT_LABELS[item.department] || item.department,
    ]
      .join(" ")
      .toLowerCase();

    return searchText.includes(keyword.trim().toLowerCase());
  });

  const handleCreate = async (event) => {
    event.preventDefault();

    if (!bookingId || !department) return;

    await onCreate(bookingId, department);
    setShowCreateForm(false);
    setBookingId("");
  };

  return (
    <aside className="passenger-conversation-list">
      <div className="passenger-conversation-header">
        <div>
          <h5>Tin nhắn</h5>
          <p>Trao đổi với các phòng ban</p>
        </div>

        <Button
          variant="primary"
          size="sm"
          title="Tạo cuộc trò chuyện"
          onClick={() => setShowCreateForm((value) => !value)}
        >
          <Plus size={17} />
        </Button>
      </div>

      {showCreateForm && (
        <Form className="passenger-create-conversation" onSubmit={handleCreate}>
          <h6>Bắt đầu cuộc trò chuyện</h6>

          <Form.Group className="mb-3">
            <Form.Label>Chọn booking</Form.Label>

            <Form.Select
              value={bookingId}
              onChange={(event) => setBookingId(event.target.value)}
              disabled={bookingsLoading || creating}
              required
            >
              <option value="">
                {bookingsLoading
                  ? "Đang tải booking..."
                  : "Chọn booking của bạn"}
              </option>

              {bookings.map((booking) => (
                <option key={booking.id} value={booking.id}>
                  {getBookingLabel(booking)}
                </option>
              ))}
            </Form.Select>
          </Form.Group>

          <Form.Group className="mb-3">
            <Form.Label>Phòng ban cần liên hệ</Form.Label>

            <Form.Select
              value={department}
              onChange={(event) => setDepartment(event.target.value)}
              disabled={creating}
            >
              {DEPARTMENTS.map((item) => (
                <option key={item.value} value={item.value}>
                  {item.label}
                </option>
              ))}
            </Form.Select>
          </Form.Group>

          <div className="d-flex gap-2">
            <Button
              type="button"
              variant="light"
              className="flex-grow-1"
              onClick={() => setShowCreateForm(false)}
              disabled={creating}
            >
              Hủy
            </Button>

            <Button
              type="submit"
              className="flex-grow-1"
              disabled={!bookingId || creating}
            >
              {creating ? <Spinner size="sm" animation="border" /> : "Bắt đầu"}
            </Button>
          </div>
        </Form>
      )}

      <div className="passenger-conversation-search">
        <Search size={17} />

        <input
          value={keyword}
          onChange={(event) => setKeyword(event.target.value)}
          placeholder="Tìm cuộc trò chuyện..."
        />
      </div>

      <div className="passenger-conversation-items">
        {loading ? (
          <div className="passenger-conversation-state">
            <Spinner animation="border" size="sm" />
            <span>Đang tải cuộc trò chuyện...</span>
          </div>
        ) : filteredConversations.length === 0 ? (
          <div className="passenger-conversation-empty">
            <MessageCircle size={30} />
            <p>Chưa có cuộc trò chuyện nào</p>
            <small>Chọn booking và phòng ban để bắt đầu trao đổi.</small>
          </div>
        ) : (
          filteredConversations.map((conversation) => (
            <button
              type="button"
              key={conversation.id}
              className={`passenger-conversation-item ${
                selectedConversationId === conversation.id ? "active" : ""
              }`}
              onClick={() => onSelect(conversation.id)}
            >
              <div className="passenger-conversation-avatar">
                <MessageCircle size={20} />
              </div>

              <div className="passenger-conversation-info">
                <div className="passenger-conversation-top">
                  <strong>
                    {DEPARTMENT_LABELS[conversation.department] ||
                      conversation.department}
                  </strong>

                  <small>{formatTime(conversation.updatedAt)}</small>
                </div>

                <span>
                  {conversation.bookingCode ||
                    `Booking #${conversation.bookingId}`}
                </span>

                <small className="passenger-conversation-status">
                  {conversation.status === "CLOSED"
                    ? "Đã đóng"
                    : "Đang hoạt động"}
                </small>
              </div>
            </button>
          ))
        )}
      </div>
    </aside>
  );
}
