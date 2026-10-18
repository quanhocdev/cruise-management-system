import "./TourCardSkeleton.css";

export default function TourCardSkeleton() {
  return (
    <div className="tour-skeleton" aria-hidden="true">
      <div className="tour-skeleton__media tour-skeleton__shimmer" />

      <div className="tour-skeleton__body">
        <div className="tour-skeleton__row">
          <span className="tour-skeleton__line is-w30 tour-skeleton__shimmer" />
          <span className="tour-skeleton__line is-w20 tour-skeleton__shimmer" />
        </div>

        <span className="tour-skeleton__line is-title tour-skeleton__shimmer" />
        <span className="tour-skeleton__line is-title is-w60 tour-skeleton__shimmer" />

        <span className="tour-skeleton__line tour-skeleton__shimmer" />
        <span className="tour-skeleton__line is-w80 tour-skeleton__shimmer" />

        <div className="tour-skeleton__info">
          <span className="tour-skeleton__line tour-skeleton__shimmer" />
          <span className="tour-skeleton__line tour-skeleton__shimmer" />
          <span className="tour-skeleton__line is-w70 tour-skeleton__shimmer" />
        </div>

        <div className="tour-skeleton__row">
          <span className="tour-skeleton__line is-price tour-skeleton__shimmer" />
          <span className="tour-skeleton__btn tour-skeleton__shimmer" />
        </div>
      </div>
    </div>
  );
}
