import { useState } from "react";
import "./TourHero.css";

const DEPARTURE_OPTIONS = [
  { value: "", label: "Tất cả điểm đi" },
  { value: "HA_LONG", label: "Hạ Long" },
  { value: "LAN_HA", label: "Lan Hạ" },
  { value: "NHA_TRANG", label: "Nha Trang" },
  { value: "PHU_QUOC", label: "Phú Quốc" },
  { value: "SINGAPORE_MALAYSIA", label: "Singapore - Malaysia" },
];

const MONTH_OPTIONS = [
  { value: "", label: "Tất cả tháng" },
  { value: "2026-12", label: "Tháng 12/2026" },
  { value: "2027-01", label: "Tháng 01/2027" },
  { value: "2027-02", label: "Tháng 02/2027" },
  { value: "2027-03", label: "Tháng 03/2027" },
  { value: "2027-04", label: "Tháng 04/2027" },
];

export default function TourSearchBar({ onSearch }) {
  const [destination, setDestination] = useState("");
  const [departure, setDeparture] = useState("");
  const [month, setMonth] = useState("");

  const handleSubmit = (event) => {
    event.preventDefault();
    onSearch?.({
      destination: destination.trim(),
      departure,
      month,
    });
  };

  return (
    <form
      className="tour-search-box"
      onSubmit={handleSubmit}
      aria-label="Tìm kiếm hành trình du thuyền"
    >
      {/* Hàng trên: Thanh tìm kiếm dài bằng tổng 3 box bên dưới */}
      <div className="tour-search-box__top">
        <span className="tour-search-box__icon" aria-hidden="true">
          <svg
            viewBox="0 0 24 24"
            width="19"
            height="19"
            fill="none"
            stroke="currentColor"
            strokeWidth="2"
            strokeLinecap="round"
            strokeLinejoin="round"
          >
            <circle cx="11" cy="11" r="8" />
            <path d="m21 21-4.3-4.3" />
          </svg>
        </span>
        <input
          type="text"
          className="tour-search-box__input"
          placeholder="Bạn muốn đi đâu"
          value={destination}
          onChange={(e) => setDestination(e.target.value)}
        />
      </div>

      {/* Hàng dưới: 3 box theo tỷ lệ 4 : 4 : 2 */}
      <div className="tour-search-box__bottom">
        <div className="tour-search-field">
          <label htmlFor="tour-departure" className="tour-search-field__label">
            Điểm đi
          </label>
          <select
            id="tour-departure"
            className="tour-search-field__control"
            value={departure}
            onChange={(e) => setDeparture(e.target.value)}
          >
            {DEPARTURE_OPTIONS.map((opt) => (
              <option key={opt.value} value={opt.value}>
                {opt.label}
              </option>
            ))}
          </select>
        </div>

        <div className="tour-search-field">
          <label htmlFor="tour-month" className="tour-search-field__label">
            Tháng đi
          </label>
          <select
            id="tour-month"
            className="tour-search-field__control"
            value={month}
            onChange={(e) => setMonth(e.target.value)}
          >
            {MONTH_OPTIONS.map((opt) => (
              <option key={opt.value} value={opt.value}>
                {opt.label}
              </option>
            ))}
          </select>
        </div>

        <button type="submit" className="tour-search-box__submit">
          Tìm tour
        </button>
      </div>
    </form>
  );
}
