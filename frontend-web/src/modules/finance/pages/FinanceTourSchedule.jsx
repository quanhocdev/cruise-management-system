// src/modules/finance/pages/FinanceTourSchedule.jsx
import React, { useState } from "react";
import TourSelector from "../components/TourSelector";
import DataTable from "../components/DataTable";
import LoadingSpinner from "../../../components/common/LoadingSpinner";
import { useFinanceSchedules, useScheduleStops } from "../hooks/useFinanceTour";
import { formatDate, formatDateTime } from "../utils/format";
import { SCHEDULE_STATUS_OPTIONS, labelOf } from "../constants/statuses";
import "../styles/layout.css";

const FinanceTourSchedule = () => {
  const [selectedTourId, setSelectedTourId] = useState("");
  const [selectedScheduleId, setSelectedScheduleId] = useState("");

  const { schedules, loading } = useFinanceSchedules(selectedTourId);
  const { stops, loading: loadingStops } = useScheduleStops(selectedScheduleId);

  const handleSelectTour = (tourId) => {
    setSelectedTourId(tourId);
    setSelectedScheduleId("");
  };

  const scheduleColumns = [
    { header: "Ngày #", accessor: "dayNumber" },
    { header: "Tên lịch trình", accessor: "name" },
    {
      header: "Ngày thực tế",
      accessor: "realDay",
      render: formatDate,
    },
    {
      header: "Trạng thái",
      accessor: "status",
      render: (v) => labelOf(SCHEDULE_STATUS_OPTIONS, v),
    },
  ];

  const stopColumns = [
    { header: "Thứ tự", accessor: "stopOrder" },
    { header: "Cảng dừng", accessor: "portName" },
    {
      header: "Giờ đến",
      accessor: "arriveAt",
      render: formatDateTime,
    },
    {
      header: "Giờ rời",
      accessor: "leaveAt",
      render: formatDateTime,
    },
  ];

  return (
    <div className="finance-page">
      <div className="finance-page__header">
        <div>
          <h2 className="finance-page__title">Quản lý Lịch trình Tour</h2>
          <p className="finance-page__subtitle">
            Xem lịch trình và các điểm dừng của từng Tour
          </p>
        </div>
      </div>

      <div className="finance-card">
        <TourSelector
          selectedTourId={selectedTourId}
          onSelectTour={handleSelectTour}
        />
      </div>

      <div className="finance-card">
        {loading ? (
          <LoadingSpinner />
        ) : (
          <DataTable
            columns={scheduleColumns}
            data={schedules}
            selectedKey={selectedScheduleId}
            onRowClick={(row) => setSelectedScheduleId(row.id)}
            emptyText={
              selectedTourId
                ? "Tour này chưa có lịch trình"
                : "Chọn một Tour để xem lịch trình"
            }
          />
        )}
      </div>

      {selectedScheduleId && (
        <div className="finance-card">
          <h3 className="finance-page__title">Điểm dừng của lịch trình</h3>

          {loadingStops ? (
            <LoadingSpinner />
          ) : (
            <DataTable columns={stopColumns} data={stops} />
          )}
        </div>
      )}
    </div>
  );
};

export default FinanceTourSchedule;
