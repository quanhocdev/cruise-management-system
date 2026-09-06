// src/modules/guest/components/BookingStatusFilter.jsx

import { Button } from "react-bootstrap";

export default function BookingStatusFilter({
  filterBooking,
  setFilterBooking,
}) {
  return (
    <div className="d-flex align-items-center gap-2 mb-4 overflow-x-auto pb-2 tour-booking-filter-wrapper">
      <span className="text-muted small fw-semibold me-2 flex-shrink-0">
        Trạng thái vé:
      </span>
      <div className="tour-booking-filter-buttons d-flex gap-2 overflow-x-auto pb-1">
        <Button
          variant={filterBooking === "ALL" ? "dark" : "outline-secondary"}
          size="sm"
          className="rounded-pill px-3 flex-shrink-0"
          onClick={() => setFilterBooking("ALL")}
        >
          Tất cả vé
        </Button>
        <Button
          variant={filterBooking === "OPEN" ? "success" : "outline-secondary"}
          size="sm"
          className="rounded-pill px-3 flex-shrink-0"
          onClick={() => setFilterBooking("OPEN")}
        >
          Đang mở bán (OPEN)
        </Button>
        <Button
          variant={filterBooking === "WAITING" ? "info" : "outline-secondary"}
          size="sm"
          className="rounded-pill px-3 text-dark flex-shrink-0"
          onClick={() => setFilterBooking("WAITING")}
        >
          Sắp mở (WAITING)
        </Button>
        <Button
          variant={
            filterBooking === "NOT_OPEN" ? "warning" : "outline-secondary"
          }
          size="sm"
          className="rounded-pill px-3 text-dark flex-shrink-0"
          onClick={() => setFilterBooking("NOT_OPEN")}
        >
          Chưa mở (NOT_OPEN)
        </Button>
        <Button
          variant={
            filterBooking === "CLOSED" ? "secondary" : "outline-secondary"
          }
          size="sm"
          className="rounded-pill px-3 flex-shrink-0"
          onClick={() => setFilterBooking("CLOSED")}
        >
          Đã đóng (CLOSED)
        </Button>
      </div>
    </div>
  );
}
