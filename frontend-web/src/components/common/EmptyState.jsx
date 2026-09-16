// src/components/common/EmptyState.jsx
import React from "react";

const EmptyState = ({ message = "Không có dữ liệu hiển thị" }) => {
  return (
    <div style={{ textAlign: "center", padding: "20px", color: "#888" }}>
      <p>{message}</p>
    </div>
  );
};

export default EmptyState;
