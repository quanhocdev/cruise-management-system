// src/modules/guest/components/TourStatusFilter.jsx

import { Button } from "react-bootstrap";

export default function TourStatusFilter({
  filterStatus,
  setFilterStatus,
  totalCount,
}) {
  return (
    <div className="tour-filter-buttons d-flex gap-2 overflow-x-auto pb-1">
      <Button
        variant={filterStatus === "ALL" ? "primary" : "outline-secondary"}
        size="sm"
        className="rounded-pill px-3 fw-medium flex-shrink-0"
        onClick={() => setFilterStatus("ALL")}
      >
        Tất cả ({totalCount})
      </Button>
      <Button
        variant={filterStatus === "READY" ? "success" : "outline-secondary"}
        size="sm"
        className="rounded-pill px-3 fw-medium flex-shrink-0"
        onClick={() => setFilterStatus("READY")}
      >
        Sắp diễn ra
      </Button>
      <Button
        variant={
          filterStatus === "IN_PROGRESS" ? "warning" : "outline-secondary"
        }
        size="sm"
        className="rounded-pill px-3 fw-medium text-dark flex-shrink-0"
        onClick={() => setFilterStatus("IN_PROGRESS")}
      >
        Đang diễn ra
      </Button>
      <Button
        variant={
          filterStatus === "COMPLETED" ? "secondary" : "outline-secondary"
        }
        size="sm"
        className="rounded-pill px-3 fw-medium flex-shrink-0"
        onClick={() => setFilterStatus("COMPLETED")}
      >
        Đã hoàn thành
      </Button>
    </div>
  );
}
