// src/modules/finance/pages/FinanceTourSchedule.jsx
import React, { useState } from "react";
import TourSelector from "../components/TourSelector";
import DataTable from "../components/DataTable";
import LoadingSpinner from "../../../components/common/LoadingSpinner";
import { useFinanceSchedule } from "../hooks/useFinanceTour";

const FinanceTourSchedule = () => {
  const [selectedTourId, setSelectedTourId] = useState("");
  const { schedules, loading } = useFinanceSchedule(selectedTourId);

  const columns = [
    { header: "ID Lịch trình", accessor: "id" },
    { header: "Tiêu đề / Tên", accessor: "title" },
    { header: "Ngày bắt đầu", accessor: "startDate" },
    { header: "Trạng thái", accessor: "status" },
  ];

  return (
    <div style={{ padding: "20px" }}>
      <h2>Quản lý Lịch trình Tour</h2>
      <TourSelector
        selectedTourId={selectedTourId}
        onSelectTour={setSelectedTourId}
      />

      {loading ? (
        <LoadingSpinner />
      ) : (
        <DataTable columns={columns} data={schedules} />
      )}
    </div>
  );
};

export default FinanceTourSchedule;
