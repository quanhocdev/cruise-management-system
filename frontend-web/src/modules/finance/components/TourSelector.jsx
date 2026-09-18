// src/modules/finance/components/TourSelector.jsx
import React from "react";
import { useFinanceTours } from "../hooks/useFinanceTour";
import "./TourSelector.css";

const TourSelector = ({ selectedTourId, onSelectTour }) => {
  const { tours, loading, error } = useFinanceTours();

  return (
    <div className="tour-selector-container">
      <label htmlFor="tour-select">
        <strong>Chọn Tour: </strong>
      </label>
      <select
        id="tour-select"
        value={selectedTourId || ""}
        onChange={(e) => onSelectTour(e.target.value)}
        disabled={loading}
      >
        <option value="">
          {loading ? "Đang tải..." : "-- Chọn một Tour để xem --"}
        </option>
        {tours.map((tour) => (
          <option key={tour.id} value={tour.id}>
            {tour.name} ({tour.code})
          </option>
        ))}
      </select>
      {error && (
        <span style={{ color: "#c0392b", marginLeft: 8 }}>
          Không tải được danh sách tour
        </span>
      )}
    </div>
  );
};

export default TourSelector;
