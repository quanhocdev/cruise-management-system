import { Button } from "react-bootstrap";
import "../styles/TourStatusFilter.css";

export default function TourStatusFilter({
  filterStatus,
  setFilterStatus,
  totalCount,
}) {
  return (
    <div className="d-flex align-items-center gap-2 mb-3 overflow-x-auto pb-2">
      <span className="text-muted small fw-semibold me-2 flex-shrink-0">
        Trạng thái chuyến đi:
      </span>
      <div className="d-flex gap-2 overflow-x-auto pb-1">
        <Button
          variant={filterStatus === "ALL" ? "dark" : "outline-secondary"}
          size="sm"
          className="rounded-pill px-3 flex-shrink-0"
          onClick={() => setFilterStatus("ALL")}
        >
          Tất cả chuyến ({totalCount})
        </Button>
        <Button
          variant={
            filterStatus === "UPCOMING" ? "primary" : "outline-secondary"
          }
          size="sm"
          className="rounded-pill px-3 flex-shrink-0"
          onClick={() => setFilterStatus("UPCOMING")}
        >
          Sắp diễn ra
        </Button>
        <Button
          variant={filterStatus === "ONGOING" ? "info" : "outline-secondary"}
          size="sm"
          className="rounded-pill px-3 text-dark flex-shrink-0"
          onClick={() => setFilterStatus("ONGOING")}
        >
          Đang diễn ra
        </Button>
        <Button
          variant={
            filterStatus === "COMPLETED" ? "secondary" : "outline-secondary"
          }
          size="sm"
          className="rounded-pill px-3 flex-shrink-0"
          onClick={() => setFilterStatus("COMPLETED")}
        >
          Đã kết thúc
        </Button>
      </div>
    </div>
  );
}
