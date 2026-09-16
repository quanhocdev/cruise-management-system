// src/components/common/LoadingSpinner.jsx
import React from "react";
import "./LoadingSpinner.css";

const LoadingSpinner = ({ rows = 3 }) => {
  return (
    <div className="skeleton-container">
      {Array.from({ length: rows }).map((_, index) => (
        <div key={index} className="skeleton-row">
          <div className="skeleton-pulse skeleton-cell-short" />
          <div className="skeleton-pulse skeleton-cell-long" />
          <div className="skeleton-pulse skeleton-cell-medium" />
        </div>
      ))}
    </div>
  );
};

export default LoadingSpinner;
