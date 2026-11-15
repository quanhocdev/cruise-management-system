// src/modules/scheduler/components/vessel/TourCard.jsx
import React from "react";
import { Calendar, ArrowRight, Ship } from "lucide-react";
import { STATUS_LABEL } from "../../utils/vesselFormat";
import "../../styles/TourCard.css";

export default function TourCard({ tour, onOpen }) {
  const status = (tour.statusTrip || "").toLowerCase();
  const active = tour.statusTrip === "IN_PROGRESS";

  return (
    <article className={`tc-card ${status}`}>
      <div className="tc-top">
        <span className="tc-code">{tour.code}</span>
        <span className={`vm-badge ${status}`}>
          {STATUS_LABEL[tour.statusTrip] || tour.statusTrip}
        </span>
      </div>

      <h3 className="tc-name">{tour.name}</h3>

      {/* Đường hành trình thu nhỏ: tàu chạy khi tour đang diễn ra */}
      <div className="tc-mini-route" aria-hidden="true">
        <span className="tc-dot" />
        <span className="tc-line">
          <Ship size={14} className={active ? "tc-ship sailing" : "tc-ship"} />
        </span>
        <span className="tc-dot end" />
      </div>

      <div className="tc-dates">
        <Calendar size={14} />
        <span>
          {tour.startDate} ➔ {tour.endDate}
        </span>
      </div>

      <button className="tc-open" onClick={() => onOpen(tour.id)}>
        <span>Xem giám sát chi tiết</span>
        <ArrowRight size={16} />
      </button>
    </article>
  );
}
