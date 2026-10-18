import FilterChips from "./FilterChips";
import { TRIP_FILTERS, BOOKING_FILTERS } from "./constants";
import "./TourFilters.css";

const countBy = (tours, key, value) =>
  value === "ALL" ? tours.length : tours.filter((t) => t[key] === value).length;

export default function TourFilters({
  tours,
  loading,
  shownCount,
  filterStatus,
  filterBooking,
  onStatusChange,
  onBookingChange,
  onReset,
}) {
  const withCounts = (options, key) =>
    options.map((o) => ({
      ...o,
      count: loading ? null : countBy(tours, key, o.value),
    }));

  const hasFilter = filterStatus !== "ALL" || filterBooking !== "ALL";

  return (
    <section className="tour-filters" aria-label="Bộ lọc tour">
      <div className="tour-filters__row">
        <span className="tour-filters__label">Hành trình</span>
        <FilterChips
          label="Lọc theo trạng thái hành trình"
          options={withCounts(TRIP_FILTERS, "statusTrip")}
          value={filterStatus}
          onChange={onStatusChange}
        />
      </div>

      <div className="tour-filters__row">
        <span className="tour-filters__label">Trạng thái vé</span>
        <FilterChips
          label="Lọc theo trạng thái mở bán vé"
          options={withCounts(BOOKING_FILTERS, "statusBooking")}
          value={filterBooking}
          onChange={onBookingChange}
        />
      </div>

      <div className="tour-filters__foot">
        <span className="tour-filters__result">
          {loading
            ? "Đang tải tour..."
            : `Hiển thị ${shownCount} / ${tours.length} tour`}
        </span>
        {hasFilter && (
          <button
            type="button"
            className="tour-filters__reset"
            onClick={onReset}
          >
            Xóa bộ lọc
          </button>
        )}
      </div>
    </section>
  );
}
