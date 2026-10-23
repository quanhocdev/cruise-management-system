import { formatDateTime } from "./formatters";
import "./TourScheduleTab.css";

export default function TourScheduleTab({ schedules }) {
  if (!schedules?.length) {
    return <p className="tab-empty">Lịch trình đang được cập nhật.</p>;
  }

  return (
    <ol className="schedule">
      {schedules.map((schedule) => (
        <li key={schedule.id} className="schedule__day">
          <h3 className="schedule__title">
            Ngày {schedule.dayNumber}: {schedule.name}
          </h3>
          <p className="schedule__desc">
            {schedule.description}
            {schedule.realDay && (
              <span className="schedule__date"> ({schedule.realDay})</span>
            )}
          </p>

          {schedule.stops?.length > 0 && (
            <ul className="schedule__stops">
              {schedule.stops.map((stop) => (
                <li key={stop.id} className="schedule__stop">
                  <div className="schedule__port">
                    {stop.portName}
                    <span>
                      {" "}
                      – {stop.portCity}, {stop.portCountry}
                    </span>
                  </div>
                  <div className="schedule__time">
                    Đến {formatDateTime(stop.arriveAt)} · Rời{" "}
                    {formatDateTime(stop.leaveAt)}
                  </div>
                </li>
              ))}
            </ul>
          )}
        </li>
      ))}
    </ol>
  );
}
