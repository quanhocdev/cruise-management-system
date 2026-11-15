// src/modules/scheduler/components/vessel/RouteProgress.jsx
import React, { useMemo } from "react";
import { Anchor, MapPin, Ship } from "lucide-react";
import { fmtDate } from "../../utils/vesselFormat";
import "../../styles/RouteProgress.css";

function buildNodes(tour) {
  const stops = (tour.schedules ?? []).flatMap((s) => s.stops ?? []);
  return [
    {
      key: "dep",
      type: "departure",
      name: tour.departurePort?.name,
      sub: tour.departurePort?.city,
    },
    ...stops.map((s, i) => ({
      key: s.stopId || i,
      type: "stop",
      order: s.stopOrder || i + 1,
      name: s.port?.name,
      sub: fmtDate(s.arriveAt),
      at: s.arriveAt,
    })),
    {
      key: "arr",
      type: "arrival",
      name: tour.arrivalPort?.name,
      sub: tour.arrivalPort?.city,
    },
  ];
}

export default function RouteProgress({ tour }) {
  const { nodes, idx } = useMemo(() => {
    const nodes = buildNodes(tour);
    const status = tour.statusTrip || tour.status;
    const now = Date.now();
    let idx = 0; // vị trí tàu: 0 = cảng đi

    if (
      status === "COMPLETED" ||
      (tour.endDate && new Date(tour.endDate) <= now)
    ) {
      idx = nodes.length - 1;
    } else if (status !== "READY") {
      nodes.forEach((n, i) => {
        if (n.type === "stop" && n.at && new Date(n.at) <= now) idx = i;
      });
    }
    return { nodes, idx };
  }, [tour]);

  const n = nodes.length;
  const started = (tour.statusTrip || tour.status) !== "READY";

  return (
    <section className="rp-section">
      <div className="rp-head">
        <h3>Hành trình tuyến đường</h3>
        <span className="rp-hint">
          {started
            ? "Tàu đang ở " + (nodes[idx].name || "—")
            : "Tàu chưa rời cảng"}
        </span>
      </div>

      <div className="rp-scroll">
        <div className="rp-board" style={{ "--n": n, "--idx": idx }}>
          <div className="rp-track">
            <div className="rp-fill" />
          </div>

          <div className={`rp-ship ${started ? "moving" : ""}`}>
            <Ship size={22} />
          </div>

          {nodes.map((node, i) => (
            <div
              key={node.key}
              className={`rp-node ${node.type} ${
                i < idx ? "done" : i === idx ? "current" : "todo"
              }`}
            >
              <div className="rp-icon">
                {node.type === "departure" ? (
                  <Anchor size={16} />
                ) : node.type === "arrival" ? (
                  <MapPin size={16} />
                ) : (
                  node.order
                )}
              </div>
              <strong title={node.name}>{node.name || "—"}</strong>
              <span>{node.sub}</span>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}
