import FilterChips from "../../../guest/components/tour-list/FilterChips";
import { BOOKING_FILTERS, TRIP_FILTERS } from "./constants";
import { getTripStatus } from "./helpers";
import "./BookingFilters.css";

const countBy = (bookings, getValue, value) =>
  value === "ALL"
    ? bookings.length
    : bookings.filter((b) => getValue(b) === value).length;

export default function BookingFilters({
  bookings,
  loading,
  shownCount,
  filterBooking,
  filterTrip,
  onBookingChange,
  onTripChange,
  onReset,
}) {
  const withCounts = (options, getValue) =>
    options.map((o) => ({
      ...o,
      count: loading ? null : countBy(bookings, getValue, o.value),
    }));

  const hasFilter = filterBooking !== "ALL" || filterTrip !== "ALL";

  return (
    <section className="my-filters" aria-label="Bộ lọc đơn vé">
      <div className="my-filters__row">
        <span className="my-filters__label">Trạng thái đơn</span>
        <FilterChips
          label="Lọc theo trạng thái đơn vé"
          options={withCounts(BOOKING_FILTERS, (b) => b.status)}
          value={filterBooking}
          onChange={onBookingChange}
        />
      </div>

      <div className="my-filters__row">
        <span className="my-filters__label">Chuyến đi</span>
        <FilterChips
          label="Lọc theo trạng thái chuyến đi"
          options={withCounts(TRIP_FILTERS, getTripStatus)}
          value={filterTrip}
          onChange={onTripChange}
        />
      </div>

      <div className="my-filters__foot">
        <span className="my-filters__result">
          {loading
            ? "Đang tải đơn vé..."
            : `Hiển thị ${shownCount} / ${bookings.length} đơn vé`}
        </span>
        {hasFilter && (
          <button type="button" className="my-filters__reset" onClick={onReset}>
            Xóa bộ lọc
          </button>
        )}
      </div>
    </section>
  );
}
