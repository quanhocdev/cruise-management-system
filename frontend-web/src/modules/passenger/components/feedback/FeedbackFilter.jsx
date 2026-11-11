import React from "react";
import "./FeedbackFilter.css";

const FILTERS = [
  { value: "ALL", label: "Tất cả" },
  { value: "PENDING", label: "Chưa đánh giá" },
  { value: "COMPLETED", label: "Đã đánh giá" },
  { value: "SKIPPED", label: "Đã bỏ qua" },
];

export default function FeedbackFilter({ value, onChange, counts = {} }) {
  return (
    <div className="feedback-filter">
      {FILTERS.map((item) => (
        <button
          key={item.value}
          type="button"
          className={`feedback-filter__button ${
            value === item.value ? "active" : ""
          }`}
          onClick={() => onChange(item.value)}
        >
          <span>{item.label}</span>
          <span className="feedback-filter__count">
            {item.value === "ALL"
              ? (counts.all ?? 0)
              : (counts[item.value.toLowerCase()] ?? 0)}
          </span>
        </button>
      ))}
    </div>
  );
}
