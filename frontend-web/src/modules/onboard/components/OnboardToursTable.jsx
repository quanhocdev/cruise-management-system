// src/modules/onboard/components/OnboardToursTable.jsx
import React from "react";

export const OnboardToursTable = ({ tours }) => {
  if (!tours || tours.length === 0) {
    return <p className="onboard-empty">Không có tour nào đã được duyệt.</p>;
  }

  return (
    <div className="onboard-table-container">
      <table className="onboard-table">
        <thead>
          <tr>
            <th>#</th>
            <th>Tên Tour</th>
            <th>Trạng Thái</th>
            <th>Thao Tác</th>
          </tr>
        </thead>
        <tbody>
          {tours.map((tour, index) => (
            <tr key={tour.id || index}>
              <td>{index + 1}</td>
              <td className="tour-name">{tour.name}</td>
              <td>
                <span className="badge badge-approved">Đã duyệt</span>
              </td>
              <td>
                <button className="btn-action">Xem chi tiết</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};
