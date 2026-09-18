// src/components/common/EmptyState.jsx
import React from "react";
import "./EmptyState.css";

const EmptyState = ({ message = "Không có dữ liệu hiển thị", icon = null }) => {
  return (
    <div className="empty-state">
      {icon && <div className="empty-state-icon">{icon}</div>}
      <p className="empty-state-message">{message}</p>
    </div>
  );
};

export default EmptyState;
