// src/modules/scheduler/components/vessel/DetailHero.jsx
import React from "react";
import { ArrowLeft, RefreshCw, Anchor, MapPin } from "lucide-react";
import { STATUS_LABEL } from "../../utils/vesselFormat";
import "../../styles/DetailHero.css";

export default function DetailHero({ tour, loading, onBack, onReload }) {
  const rawStatus = tour?.statusTrip || tour?.status;
  const status = (rawStatus || "").toLowerCase();

  return (
    <header className="vm-hero dh-hero">
      <div className="dh-bar">
        <button className="dh-back" onClick={onBack}>
          <ArrowLeft size={16} />
          <span>Quay lại danh sách</span>
        </button>
        <button className="dh-reload" onClick={onReload} disabled={loading}>
          <RefreshCw size={15} className={loading ? "vm-spin" : ""} />
          <span>Làm mới</span>
        </button>
      </div>

      <div className="dh-title">
        <div className="dh-tags">
          <span className="dh-code">{tour?.code || "..."}</span>
          {rawStatus && (
            <span className={`vm-badge ${status}`}>
              {STATUS_LABEL[rawStatus] || rawStatus}
            </span>
          )}
        </div>
        <h1>{tour?.name || "Chi tiết giám sát tour"}</h1>

        {tour && (
          <div className="dh-route">
            <span>
              <Anchor size={15} /> {tour.departurePort?.name || "—"}
            </span>
            <span className="dh-route-line" aria-hidden="true" />
            <span>
              <MapPin size={15} /> {tour.arrivalPort?.name || "—"}
            </span>
          </div>
        )}
      </div>
    </header>
  );
}
