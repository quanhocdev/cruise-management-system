import { Button } from "react-bootstrap";

export default function BookingFilter({ filterStatus, setFilterStatus }) {
  return (
    <div className="d-flex align-items-center gap-2 mb-4 overflow-x-auto pb-2">
      <span className="text-muted small fw-semibold me-2 flex-shrink-0">
        Trạng thái đơn:
      </span>
      <div className="d-flex gap-2 overflow-x-auto pb-1">
        <Button
          variant={filterStatus === "ALL" ? "dark" : "outline-secondary"}
          size="sm"
          className="rounded-pill px-3 flex-shrink-0"
          onClick={() => setFilterStatus("ALL")}
        >
          Tất cả đơn
        </Button>
        <Button
          variant={
            filterStatus === "PENDING_PAYMENT" ? "warning" : "outline-secondary"
          }
          size="sm"
          className="rounded-pill px-3 text-dark flex-shrink-0"
          onClick={() => setFilterStatus("PENDING_PAYMENT")}
        >
          Chờ thanh toán
        </Button>
        <Button
          variant={
            filterStatus === "CONFIRMED" ? "success" : "outline-secondary"
          }
          size="sm"
          className="rounded-pill px-3 flex-shrink-0"
          onClick={() => setFilterStatus("CONFIRMED")}
        >
          Đã xác nhận
        </Button>
        <Button
          variant={
            filterStatus === "CANCELLED" ? "danger" : "outline-secondary"
          }
          size="sm"
          className="rounded-pill px-3 flex-shrink-0"
          onClick={() => setFilterStatus("CANCELLED")}
        >
          Đã hủy
        </Button>
      </div>
    </div>
  );
}
