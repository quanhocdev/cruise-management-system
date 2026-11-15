// src/modules/passenger/components/tracking/ItineraryDays.jsx
import { Anchor } from "lucide-react";
import "./ItineraryDays.css";

const time = (v) =>
  v
    ? new Date(v).toLocaleTimeString("vi-VN", {
        hour: "2-digit",
        minute: "2-digit",
      })
    : "";

const day = (v) =>
  v
    ? new Date(v).toLocaleDateString("vi-VN", {
        weekday: "short",
        day: "2-digit",
        month: "2-digit",
      })
    : "";

export default function ItineraryDays({ schedules = [] }) {
  const days = [...schedules].sort(
    (a, b) => (a.dayNumber ?? 0) - (b.dayNumber ?? 0),
  );
  const now = Date.now();

  if (days.length === 0) {
    return (
      <section className="it-section">
        <h3>Lịch trình theo ngày</h3>
        <p className="it-empty">Chưa có lịch trình chi tiết cho tour này.</p>
      </section>
    );
  }

  return (
    <section className="it-section">
      <h3>Lịch trình theo ngày</h3>

      <div className="it-days">
        {days.map((d) => {
          const stops = [...(d.stops ?? [])].sort(
            (a, b) => (a.stopOrder ?? 0) - (b.stopOrder ?? 0),
          );
          return (
            <article key={d.scheduleId} className="it-day">
              <header>
                <span className="it-day-no">Ngày {d.dayNumber}</span>
                <span className="it-day-date">{day(d.realDay)}</span>
                {d.name && <strong>{d.name}</strong>}
              </header>

              {stops.length === 0 ? (
                <p className="it-empty">
                  Ngày di chuyển trên biển, chưa có điểm dừng.
                </p>
              ) : (
                <ol>
                  {stops.map((s) => {
                    const passed =
                      s.arriveAt && new Date(s.arriveAt).getTime() <= now;
                    return (
                      <li key={s.stopId} className={passed ? "passed" : ""}>
                        <span className="it-pin">
                          <Anchor size={13} />
                        </span>
                        <div>
                          <strong>{s.port?.name}</strong>
                          <small>
                            {[s.port?.city, s.port?.country]
                              .filter(Boolean)
                              .join(", ")}
                          </small>
                        </div>
                        <span className="it-time">
                          {time(s.arriveAt)}
                          {s.leaveAt ? ` – ${time(s.leaveAt)}` : ""}
                        </span>
                      </li>
                    );
                  })}
                </ol>
              )}
            </article>
          );
        })}
      </div>
    </section>
  );
}
