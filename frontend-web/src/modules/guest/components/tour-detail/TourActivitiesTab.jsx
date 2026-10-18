import { formatCurrency } from "./formatters";
import "./TourActivitiesTab.css";

export default function TourActivitiesTab({ activities }) {
  if (!activities?.length) {
    return <p className="tab-empty">Chưa có hoạt động nào trên tàu.</p>;
  }

  return (
    <div className="activities">
      {activities.map((act) => (
        <article key={act.id} className="activity">
          <h3 className="activity__name">{act.activityName}</h3>
          <p className="activity__desc">{act.activityDescription}</p>
          <span className={`activity__price ${act.price ? "" : "is-free"}`}>
            {act.price ? formatCurrency(act.price) : "Miễn phí"}
          </span>
        </article>
      ))}
    </div>
  );
}
