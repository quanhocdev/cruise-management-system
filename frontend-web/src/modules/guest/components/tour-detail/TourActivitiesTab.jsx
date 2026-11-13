import { formatCurrency } from "./formatters";
import "./TourActivitiesTab.css";

function formatDateTime(value) {
  if (!value) return "Chưa xác định";

  const date = new Date(value);

  if (Number.isNaN(date.getTime())) {
    return "Chưa xác định";
  }

  return date.toLocaleString("vi-VN", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
}

function ActivityPrice({ price }) {
  return (
    <span
      className={`activity__price ${price == null || Number(price) === 0 ? "is-free" : ""}`}
    >
      {price == null || Number(price) === 0
        ? "Miễn phí"
        : formatCurrency(price)}
    </span>
  );
}

function OnboardActivityCard({ activity }) {
  return (
    <article className="activity">
      {activity.imageUrl && (
        <div className="activity__image">
          <img
            src={activity.imageUrl}
            alt={activity.activityName || "Hoạt động trên tàu"}
            loading="lazy"
          />
        </div>
      )}

      <div className="activity__body">
        <h4 className="activity__name">
          {activity.activityName || "Hoạt động chưa có tên"}
        </h4>

        <p className="activity__desc">
          {activity.activityDescription || "Chưa có mô tả."}
        </p>

        <div className="activity__metadata">
          <span>Bắt đầu: {formatDateTime(activity.startTime)}</span>

          <span>Kết thúc: {formatDateTime(activity.endTime)}</span>

          {activity.maxPassengers != null && (
            <span>Tối đa {activity.maxPassengers} khách</span>
          )}
        </div>

        <ActivityPrice price={activity.price} />
      </div>
    </article>
  );
}

function VisitActivityCard({ activity }) {
  return (
    <article className="activity">
      <div className="activity__body">
        <h4 className="activity__name">
          {activity.visitName || "Hoạt động tham quan"}
        </h4>

        <p className="activity__desc">
          {activity.visitDescription || "Chưa có mô tả."}
        </p>

        <div className="activity__metadata">
          <span>Bắt đầu: {formatDateTime(activity.startTime)}</span>

          <span>Kết thúc: {formatDateTime(activity.endTime)}</span>

          {activity.maxPassengers != null && (
            <span>Tối đa {activity.maxPassengers} khách</span>
          )}
        </div>

        <ActivityPrice price={activity.price} />
      </div>
    </article>
  );
}

export default function TourActivitiesTab({
  onboardActivities = [],
  schedules = [],
}) {
  const visitActivities = schedules.flatMap((schedule) =>
    (schedule.stops || [])
      .filter((stop) => stop.visitActivity)
      .map((stop) => ({
        ...stop.visitActivity,
        stopId: stop.id,
        stopOrder: stop.stopOrder,
        portName: stop.portName,
        portCity: stop.portCity,
        portCountry: stop.portCountry,
        scheduleName: schedule.name,
        dayNumber: schedule.dayNumber,
        realDay: schedule.realDay,
      })),
  );

  const hasOnboardActivities = onboardActivities.length > 0;
  const hasVisitActivities = visitActivities.length > 0;

  if (!hasOnboardActivities && !hasVisitActivities) {
    return (
      <p className="tab-empty">
        Tour hiện chưa có hoạt động trên tàu hoặc hoạt động tham quan.
      </p>
    );
  }

  return (
    <div className="activities">
      {/* Hoạt động trên tàu */}
      {hasOnboardActivities && (
        <section className="activities__section">
          <div className="activities__heading">
            <h3>Hoạt động trên du thuyền</h3>
            <span>{onboardActivities.length} hoạt động</span>
          </div>

          <div className="activities__grid">
            {onboardActivities.map((activity) => (
              <OnboardActivityCard key={activity.id} activity={activity} />
            ))}
          </div>
        </section>
      )}

      {/* Hoạt động tham quan tại cảng */}
      {hasVisitActivities && (
        <section className="activities__section">
          <div className="activities__heading">
            <h3>Hoạt động tham quan tại các điểm dừng</h3>
            <span>{visitActivities.length} hoạt động</span>
          </div>

          <div className="activities__visits">
            {visitActivities.map((activity) => (
              <article key={activity.id} className="visit-group">
                <div className="visit-group__header">
                  <div>
                    <span className="visit-group__day">
                      {activity.dayNumber != null
                        ? `Ngày ${activity.dayNumber}`
                        : "Lịch trình"}
                      {activity.stopOrder != null
                        ? ` · Điểm dừng ${activity.stopOrder}`
                        : ""}
                    </span>

                    <h4>{activity.portName || "Điểm tham quan"}</h4>

                    {(activity.portCity || activity.portCountry) && (
                      <p>
                        {[activity.portCity, activity.portCountry]
                          .filter(Boolean)
                          .join(", ")}
                      </p>
                    )}
                  </div>
                </div>

                <VisitActivityCard activity={activity} />
              </article>
            ))}
          </div>
        </section>
      )}
    </div>
  );
}
