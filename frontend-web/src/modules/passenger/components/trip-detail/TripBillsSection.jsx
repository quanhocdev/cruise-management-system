import React, { useMemo, useState } from "react";
import {
  Alert,
  Badge,
  Button,
  Card,
  Col,
  Collapse,
  Row,
  Spinner,
} from "react-bootstrap";
import {
  ReceiptText,
  RefreshCw,
  CalendarDays,
  ChevronDown,
  ChevronUp,
  Hash,
  CreditCard,
  Package,
} from "lucide-react";

import "./TripBillsSection.css";

function unwrapList(value) {
  if (Array.isArray(value)) return value;
  if (!value || typeof value !== "object") return [];

  if (Array.isArray(value.bills)) return value.bills;
  if (Array.isArray(value.items)) return value.items;
  if (Array.isArray(value.content)) return value.content;
  if (Array.isArray(value.data)) return value.data;

  // API có thể trả về một PassengerBillDTO đơn lẻ.
  if ("totalAmount" in value && "bookingId" in value) {
    return [value];
  }

  return [];
}

function formatCurrency(value) {
  if (value === null || value === undefined) {
    return "—";
  }

  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(Number(value));
}

function formatDate(value) {
  if (!value) return "—";

  const date = new Date(value);

  if (Number.isNaN(date.getTime())) return "—";

  return date.toLocaleString("vi-VN", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
}

function getStatusInfo(status) {
  const normalized = String(status || "").toUpperCase();

  const statusMap = {
    PAID: { label: "Đã thanh toán", variant: "success" },
    PENDING: { label: "Đang chờ", variant: "warning" },
    UNPAID: { label: "Chưa thanh toán", variant: "warning" },
    CANCELLED: { label: "Đã hủy", variant: "secondary" },
    REFUNDED: { label: "Đã hoàn tiền", variant: "info" },
  };

  return (
    statusMap[normalized] || {
      label: status || "Chưa cập nhật",
      variant: "secondary",
    }
  );
}

function getUsageTypeLabel(type) {
  const labels = {
    PRODUCT: "Sản phẩm",
    SERVICE: "Dịch vụ",
    ACTIVITY_CRUISE: "Hoạt động trên tàu",
    VISIT: "Tham quan",
  };

  return labels[String(type || "").toUpperCase()] || type || "Khác";
}

function BillItem({ item }) {
  return (
    <div className="trip-bill-item">
      <div className="trip-bill-item-main">
        <div className="trip-bill-item-icon">
          <Package size={17} />
        </div>

        <div className="trip-bill-item-info">
          <strong>{getUsageTypeLabel(item.usageType)}</strong>
          <span>Mã lượt sử dụng: {item.usageId ?? "—"}</span>
        </div>
      </div>

      <div className="trip-bill-item-pricing">
        <div>
          <span>Số lượng</span>
          <strong>{item.quantity ?? 0}</strong>
        </div>

        <div>
          <span>Đơn giá</span>
          <strong>{formatCurrency(item.unitPrice)}</strong>
        </div>

        <div>
          <span>Giảm giá</span>
          <strong>{formatCurrency(item.discountAmount)}</strong>
        </div>

        <div className="trip-bill-item-final">
          <span>Thành tiền</span>
          <strong>{formatCurrency(item.finalAmount)}</strong>
        </div>
      </div>
    </div>
  );
}

function BillCard({ bill, index }) {
  const [expanded, setExpanded] = useState(false);

  const statusInfo = getStatusInfo(bill.status);
  const items = Array.isArray(bill.items) ? bill.items : [];

  return (
    <Card className="trip-bill-card">
      <Card.Body>
        <div className="trip-bill-card-header">
          <div className="trip-bill-icon">
            <ReceiptText size={21} />
          </div>

          <div className="trip-bill-heading">
            <strong>Hóa đơn #{bill.id}</strong>
            <span>Hóa đơn {index + 1}</span>
          </div>

          <Badge bg={statusInfo.variant} className="trip-bill-status">
            {statusInfo.label}
          </Badge>
        </div>

        <Row className="trip-bill-summary g-3">
          <Col xs={12} sm={6}>
            <div className="trip-bill-summary-item">
              <span>Tổng tiền</span>
              <strong className="trip-bill-total">
                {formatCurrency(bill.totalAmount)}
              </strong>
            </div>
          </Col>

          <Col xs={12} sm={6}>
            <div className="trip-bill-summary-item">
              <span>Booking ID</span>
              <strong>
                <Hash size={14} />
                {bill.bookingId ?? "—"}
              </strong>
            </div>
          </Col>
        </Row>

        <div className="trip-bill-information">
          <div className="trip-bill-info-item">
            <CalendarDays size={16} />
            <div>
              <span>Ngày tạo</span>
              <strong>{formatDate(bill.createdAt)}</strong>
            </div>
          </div>

          <div className="trip-bill-info-item">
            <CreditCard size={16} />
            <div>
              <span>Cập nhật lần cuối</span>
              <strong>{formatDate(bill.updatedAt)}</strong>
            </div>
          </div>
        </div>

        <div className="trip-bill-details-toggle">
          <Button
            variant="link"
            onClick={() => setExpanded((current) => !current)}
          >
            {expanded ? "Ẩn chi tiết" : `Xem chi tiết (${items.length})`}
            {expanded ? <ChevronUp size={16} /> : <ChevronDown size={16} />}
          </Button>
        </div>

        <Collapse in={expanded}>
          <div className="trip-bill-items">
            {items.length > 0 ? (
              items.map((item, itemIndex) => (
                <BillItem
                  key={item.id ?? `${item.usageId}-${itemIndex}`}
                  item={item}
                />
              ))
            ) : (
              <div className="trip-bill-no-items">
                Hóa đơn chưa có chi tiết.
              </div>
            )}
          </div>
        </Collapse>
      </Card.Body>
    </Card>
  );
}

export default function TripBillsSection({
  bills,
  loading = false,
  error = "",
  onReload,
}) {
  const billList = useMemo(() => unwrapList(bills), [bills]);

  return (
    <section className="trip-bills-section">
      <div className="trip-bills-section-header">
        <div className="trip-bills-title-group">
          <div className="trip-bills-section-icon">
            <ReceiptText size={22} />
          </div>

          <div>
            <h2>Hóa đơn</h2>
            <p>Thông tin hóa đơn và các khoản sử dụng trong booking</p>
          </div>
        </div>

        {onReload && (
          <Button
            variant="outline-secondary"
            size="sm"
            onClick={onReload}
            disabled={loading}
            className="trip-bills-reload"
          >
            {loading ? (
              <Spinner animation="border" size="sm" />
            ) : (
              <RefreshCw size={15} />
            )}
            Làm mới
          </Button>
        )}
      </div>

      {loading && billList.length === 0 && (
        <div className="trip-bills-state">
          <Spinner animation="border" variant="primary" />
          <span>Đang tải hóa đơn...</span>
        </div>
      )}

      {error && (
        <Alert variant="danger" className="trip-bills-error">
          <div>{error}</div>
          {onReload && (
            <Button
              variant="outline-danger"
              size="sm"
              onClick={onReload}
              className="mt-2"
            >
              Thử lại
            </Button>
          )}
        </Alert>
      )}

      {!loading && !error && billList.length === 0 && (
        <div className="trip-bills-empty">
          <div className="trip-bills-empty-icon">
            <ReceiptText size={30} />
          </div>
          <h5>Chưa có hóa đơn</h5>
          <p>Hiện chưa có hóa đơn nào được ghi nhận cho booking này.</p>
        </div>
      )}

      {billList.length > 0 && (
        <Row className="g-3">
          {billList.map((bill, index) => (
            <Col xs={12} key={bill.id ?? index}>
              <BillCard bill={bill} index={index} />
            </Col>
          ))}
        </Row>
      )}
    </section>
  );
}
