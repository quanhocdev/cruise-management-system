// src/modules/scheduler/components/vessel/LivePositionPanel.jsx
import React from "react";
import { Navigation } from "lucide-react";
import { fmtCoord, minutesAgo } from "../../utils/vesselFormat";
import "../../styles/LivePositionPanel.css";

const STALE_AFTER_MIN = 10;

export default function LivePositionPanel({ position }) {
  if (!position) {
    return (
      <section className="lp-panel lp-empty">
        <div className="lp-radar off" aria-hidden="true">
          <Navigation size={20} />
        </div>
        <div>
          <h4>Vị trí GPS</h4>
          <p>Chưa nhận được tín hiệu từ tàu. Tour có thể chưa khởi hành.</p>
        </div>
      </section>
    );
  }

  const ago = minutesAgo(position.updatedAt);
  const stale = ago !== null && ago > STALE_AFTER_MIN;

  return (
    <section className={`lp-panel ${stale ? "stale" : "live"}`}>
      <div className={`lp-radar ${stale ? "off" : ""}`} aria-hidden="true">
        <Navigation size={20} />
      </div>

      <div className="lp-info">
        <h4>{stale ? "Mất tín hiệu gần đây" : "Vị trí tàu trực tiếp"}</h4>
        <div className="lp-coords">
          <div>
            <span>Vĩ độ</span>
            <strong>{fmtCoord(position.latitude, "N", "S")}</strong>
          </div>
          <div>
            <span>Kinh độ</span>
            <strong>{fmtCoord(position.longitude, "E", "W")}</strong>
          </div>
        </div>
        <p className="lp-time">
          {ago === null
            ? ""
            : ago < 1
              ? "Cập nhật vừa xong"
              : `Cập nhật ${ago} phút trước`}
        </p>
      </div>
    </section>
  );
}
