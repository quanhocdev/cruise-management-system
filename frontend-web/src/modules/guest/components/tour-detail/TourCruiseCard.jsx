import "./TourCruiseCard.css";

export default function TourCruiseCard({ cruise, maxPassengers }) {
  if (!cruise) return null;

  return (
    <section className="cruise-card">
      <div className="cruise-card__media">
        {cruise.imageUrl ? (
          <img src={cruise.imageUrl} alt={cruise.name} />
        ) : (
          <div className="cruise-card__placeholder">Chưa có ảnh du thuyền</div>
        )}
      </div>

      <div className="cruise-card__body">
        <h2 className="cruise-card__name">{cruise.name}</h2>
        <p className="cruise-card__code">Mã tàu {cruise.code}</p>
        <p className="cruise-card__desc">
          {cruise.description ||
            "Du thuyền tiêu chuẩn quốc tế với đầy đủ tiện ích giải trí sang trọng."}
        </p>
        {maxPassengers != null && (
          <p className="cruise-card__capacity">
            Sức chứa tối đa <strong>{maxPassengers} hành khách</strong>
          </p>
        )}
      </div>
    </section>
  );
}
