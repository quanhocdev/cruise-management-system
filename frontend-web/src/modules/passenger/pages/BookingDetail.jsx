import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import {
  Container,
  Row,
  Col,
  Card,
  Button,
  Badge,
  Spinner,
  Alert,
  Form,
  Modal,
} from "react-bootstrap";
import {
  ArrowLeft,
  CalendarDays,
  CreditCard,
  Users,
  Edit3,
  Ship,
} from "lucide-react";
import passengerBookingService from "../services/passengerBookingService";
import passengerService from "../services/passengerService";
import "../styles/BookingDetail.css";

const statusConfig = {
  PENDING_PAYMENT: { label: "Chờ thanh toán", bg: "warning", text: "dark" },
  CONFIRMED: { label: "Đã xác nhận", bg: "success", text: "white" },
  CANCELLED: { label: "Đã hủy", bg: "danger", text: "white" },
};

const formatMoney = (value) =>
  new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
    maximumFractionDigits: 0,
  }).format(value || 0);

const formatDateTime = (value) =>
  value
    ? new Intl.DateTimeFormat("vi-VN", {
        dateStyle: "medium",
        timeStyle: "short",
      }).format(new Date(value))
    : "—";

export default function BookingDetail() {
  const { bookingId } = useParams();
  const [booking, setBooking] = useState(null);
  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  // Modal chỉnh sửa hành khách
  const [showEditModal, setShowEditModal] = useState(false);
  const [editingPassenger, setEditingPassenger] = useState(null);

  const fetchBookingDetail = async () => {
    setLoading(true);
    setError("");
    try {
      const data = await passengerBookingService.getBooking(bookingId);
      setBooking(data);
    } catch (err) {
      setError(
        err.response?.data?.message || "Không thể tải chi tiết booking.",
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchBookingDetail();
  }, [bookingId]);

  const handleCancel = async () => {
    if (!window.confirm("Bạn có chắc chắn muốn hủy đơn hàng này không?"))
      return;
    setActionLoading(true);
    setError("");
    try {
      const updated = await passengerBookingService.cancelBooking(booking.id);
      setBooking(updated);
      setSuccess("Hủy đơn hàng thành công.");
    } catch (err) {
      setError(err.response?.data?.message || "Không thể hủy đơn hàng.");
    } finally {
      setActionLoading(false);
    }
  };

  const openEditPassengerModal = (passenger) => {
    setEditingPassenger({ ...passenger });
    setShowEditModal(true);
  };

  const handleSavePassenger = async (e) => {
    e.preventDefault();
    if (!editingPassenger) return;
    setActionLoading(true);
    setError("");
    setSuccess("");
    try {
      // Gọi service cập nhật hành khách (Patch)
      await passengerService.update(editingPassenger.id, editingPassenger);
      setSuccess("Cập nhật thông tin hành khách thành công.");
      setShowEditModal(false);
      fetchBookingDetail(); // Tải lại thông tin booking mới nhất
    } catch (err) {
      setError(err.response?.data?.message || "Không thể cập nhật hành khách.");
    } finally {
      setActionLoading(false);
    }
  };

  if (loading) {
    return (
      <Container className="py-5 text-center">
        <Spinner animation="border" variant="primary" />
        <p className="mt-2 text-muted">Đang tải chi tiết booking...</p>
      </Container>
    );
  }

  if (!booking) {
    return (
      <Container className="py-5 text-center">
        <Alert variant="danger">
          {error || "Không tìm thấy thông tin đơn hàng."}
        </Alert>
        <Button
          as={Link}
          to="/passenger/bookings"
          variant="outline-primary"
          size="sm"
        >
          Quay lại danh sách
        </Button>
      </Container>
    );
  }

  const currentStatus = statusConfig[booking.status] || {
    label: booking.status,
    bg: "secondary",
    text: "white",
  };

  return (
    <div className="tour-public-page bg-light min-vh-100 pb-5">
      <div className="tour-public-hero text-white py-4 mb-4 shadow-sm text-center">
        <Container>
          <div className="d-flex justify-content-start mb-2">
            <Link
              to="/passenger/bookings"
              className="text-white text-decoration-none small d-flex align-items-center gap-1"
            >
              <ArrowLeft size={16} /> Quay lại Booking của tôi
            </Link>
          </div>
          <h1 className="fw-bold fs-3 mb-1">
            🎫 Chi Tiết Đơn: {booking.bookingCode || `#${booking.id}`}
          </h1>
        </Container>
      </div>

      <Container>
        {error && <Alert variant="danger">{error}</Alert>}
        {success && <Alert variant="success">{success}</Alert>}

        <Row className="g-4">
          {/* Thông tin chính & Danh sách hành khách */}
          <Col lg={8}>
            <Card className="border-0 shadow-sm rounded-4 p-4 mb-4">
              <h5 className="fw-bold text-primary mb-3">
                📦 Tổng quan đơn hàng
              </h5>
              <Row className="small text-secondary g-3">
                <Col md={6}>
                  <div>
                    <strong>Trạng thái:</strong>{" "}
                    <Badge bg={currentStatus.bg} text={currentStatus.text}>
                      {currentStatus.label}
                    </Badge>
                  </div>
                  <div className="mt-2">
                    <strong>Ngày tạo:</strong>{" "}
                    {formatDateTime(booking.createdAt)}
                  </div>
                </Col>
                <Col md={6}>
                  <div>
                    <strong>Người liên hệ:</strong> {booking.primaryContactName}
                  </div>
                  <div className="mt-2">
                    <strong>Số điện thoại:</strong>{" "}
                    {booking.primaryContactPhone}
                  </div>
                </Col>
              </Row>
            </Card>

            <Card className="border-0 shadow-sm rounded-4 p-4">
              <div className="d-flex justify-content-between align-items-center mb-3">
                <h5 className="fw-bold text-primary m-0">
                  👥 Danh sách hành khách tham gia
                </h5>
              </div>

              {booking.passengers?.map((p, index) => (
                <div
                  key={p.id || index}
                  className="passenger-row p-3 mb-3 d-flex justify-content-between align-items-center"
                >
                  <div>
                    <div className="fw-bold text-dark">
                      {index + 1}. {p.fullName}{" "}
                      <span className="text-muted fw-normal">
                        ({p.gender}, {p.dateOfBirth})
                      </span>
                    </div>
                    <div className="small text-secondary mt-1">
                      {p.idCardType}: <strong>{p.identificationNumber}</strong>{" "}
                      | SĐT: {p.phoneNumber || "—"} | Email: {p.email || "—"}
                    </div>
                    {p.documentNote && (
                      <div className="small text-muted fst-italic mt-1">
                        Ghi chú: {p.documentNote}
                      </div>
                    )}
                  </div>
                  <div>
                    <Button
                      variant="outline-primary"
                      size="sm"
                      className="rounded-pill px-3 d-flex align-items-center gap-1"
                      onClick={() => openEditPassengerModal(p)}
                    >
                      <Edit3 size={14} /> Sửa
                    </Button>
                  </div>
                </div>
              ))}
            </Card>
          </Col>

          {/* Sidebar tóm tắt và hành động */}
          <Col lg={4}>
            <Card
              className="border-0 shadow-sm rounded-4 p-4 sticky-top"
              style={{ top: "20px" }}
            >
              <h5 className="fw-bold text-dark mb-3">Tóm tắt thanh toán</h5>
              <p className="text-muted small mb-2">
                Mã Tour / Chuyến:{" "}
                <strong>{booking.tourCode || booking.tourId || "N/A"}</strong>
              </p>
              <hr />
              <div className="d-flex justify-content-between mb-3">
                <span className="text-muted">Tổng tiền:</span>
                <span className="fw-bold text-primary fs-5">
                  {formatMoney(booking.totalAmount)}
                </span>
              </div>

              {booking.status === "PENDING_PAYMENT" && (
                <div className="d-grid gap-2">
                  <Button
                    variant="danger"
                    size="sm"
                    className="rounded-pill fw-bold"
                    onClick={handleCancel}
                    disabled={actionLoading}
                  >
                    {actionLoading ? <Spinner size="sm" /> : "Hủy đơn hàng"}
                  </Button>
                </div>
              )}

              {booking.status === "CONFIRMED" && (
                <Alert
                  variant="success"
                  className="small text-center py-2 mb-0"
                >
                  Đơn hàng đã được xác nhận thành công.
                </Alert>
              )}
            </Card>
          </Col>
        </Row>
      </Container>

      {/* Modal chỉnh sửa hành khách */}
      <Modal
        show={showEditModal}
        onHide={() => setShowEditModal(false)}
        centered
      >
        <Modal.Header closeButton>
          <Modal.Title className="fw-bold fs-5 text-primary">
            Chỉnh sửa thông tin hành khách
          </Modal.Title>
        </Modal.Header>
        <Form onSubmit={handleSavePassenger}>
          <Modal.Body>
            {editingPassenger && (
              <Row className="g-3">
                <Col md={12}>
                  <Form.Label className="small">Họ và tên</Form.Label>
                  <Form.Control
                    size="sm"
                    type="text"
                    required
                    value={editingPassenger.fullName || ""}
                    onChange={(e) =>
                      setEditingPassenger({
                        ...editingPassenger,
                        fullName: e.target.value,
                      })
                    }
                  />
                </Col>
                <Col md={6}>
                  <Form.Label className="small">Ngày sinh</Form.Label>
                  <Form.Control
                    size="sm"
                    type="date"
                    required
                    value={editingPassenger.dateOfBirth || ""}
                    onChange={(e) =>
                      setEditingPassenger({
                        ...editingPassenger,
                        dateOfBirth: e.target.value,
                      })
                    }
                  />
                </Col>
                <Col md={6}>
                  <Form.Label className="small">Giới tính</Form.Label>
                  <Form.Select
                    size="sm"
                    value={editingPassenger.gender || "Nam"}
                    onChange={(e) =>
                      setEditingPassenger({
                        ...editingPassenger,
                        gender: e.target.value,
                      })
                    }
                  >
                    <option value="Nam">Nam</option>
                    <option value="Nữ">Nữ</option>
                    <option value="Khác">Khác</option>
                  </Form.Select>
                </Col>
                <Col md={6}>
                  <Form.Label className="small">Số điện thoại</Form.Label>
                  <Form.Control
                    size="sm"
                    type="text"
                    value={editingPassenger.phoneNumber || ""}
                    onChange={(e) =>
                      setEditingPassenger({
                        ...editingPassenger,
                        phoneNumber: e.target.value,
                      })
                    }
                  />
                </Col>
                <Col md={6}>
                  <Form.Label className="small">Email</Form.Label>
                  <Form.Control
                    size="sm"
                    type="email"
                    value={editingPassenger.email || ""}
                    onChange={(e) =>
                      setEditingPassenger({
                        ...editingPassenger,
                        email: e.target.value,
                      })
                    }
                  />
                </Col>
                <Col md={6}>
                  <Form.Label className="small">Loại giấy tờ</Form.Label>
                  <Form.Select
                    size="sm"
                    value={editingPassenger.idCardType || "CCCD"}
                    onChange={(e) =>
                      setEditingPassenger({
                        ...editingPassenger,
                        idCardType: e.target.value,
                      })
                    }
                  >
                    <option value="CCCD">CCCD</option>
                    <option value="CMND">CMND</option>
                    <option value="Passport">Passport</option>
                  </Form.Select>
                </Col>
                <Col md={6}>
                  <Form.Label className="small">Số định danh / CCCD</Form.Label>
                  <Form.Control
                    size="sm"
                    type="text"
                    required
                    value={editingPassenger.identificationNumber || ""}
                    onChange={(e) =>
                      setEditingPassenger({
                        ...editingPassenger,
                        identificationNumber: e.target.value,
                      })
                    }
                  />
                </Col>
                <Col md={12}>
                  <Form.Label className="small">Ghi chú giấy tờ</Form.Label>
                  <Form.Control
                    size="sm"
                    type="text"
                    value={editingPassenger.documentNote || ""}
                    onChange={(e) =>
                      setEditingPassenger({
                        ...editingPassenger,
                        documentNote: e.target.value,
                      })
                    }
                  />
                </Col>
              </Row>
            )}
          </Modal.Body>
          <Modal.Footer>
            <Button
              variant="secondary"
              size="sm"
              onClick={() => setShowEditModal(false)}
            >
              Đóng
            </Button>
            <Button
              variant="primary"
              size="sm"
              type="submit"
              disabled={actionLoading}
            >
              {actionLoading ? <Spinner size="sm" /> : "Lưu thay đổi"}
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>
    </div>
  );
}
