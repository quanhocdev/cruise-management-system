// src/modules/finance/pages/FinanceRooms.jsx
import React, { useState } from "react";
import TourSelector from "../components/TourSelector";
import FilterToolbar from "../components/FilterToolbar";
import DataTable from "../components/DataTable";
import LoadingSpinner from "../../../components/common/LoadingSpinner";
import useFinanceRoom from "../hooks/useFinanceRoom";

const FinanceRooms = () => {
  const [selectedTourId, setSelectedTourId] = useState("");
  // Giả định bạn chọn deckId trực tiếp hoặc qua cấu trúc tour (ở đây demo truyền deckId tạm thời hoặc lấy từ tour)
  const [selectedDeckId, setSelectedDeckId] = useState("");
  const [filterStatus, setFilterStatus] = useState("ALL");
  const [searchTerm, setSearchTerm] = useState("");

  const { rooms, loading } = useFinanceRoom(selectedDeckId);

  // Lọc trạng thái và tìm kiếm ở FE
  const filteredRooms = rooms.filter((room) => {
    const matchStatus = filterStatus === "ALL" || room.status === filterStatus;
    const matchSearch = room.code
      ?.toLowerCase()
      .includes(searchTerm.toLowerCase());
    return matchStatus && matchSearch;
  });

  const columns = [
    { header: "Mã phòng", accessor: "code" },
    { header: "Loại phòng", accessor: "roomTypeName" },
    { header: "Trạng thái", accessor: "status" },
  ];

  return (
    <div style={{ padding: "20px" }}>
      <h2>Quản lý Phòng</h2>
      <TourSelector
        selectedTourId={selectedTourId}
        onSelectTour={setSelectedTourId}
      />

      {/* Nếu có chọn deckId hoặc chọn từ tour */}
      <div style={{ margin: "15px 0" }}>
        <input
          type="text"
          placeholder="Nhập Deck ID (UUID) để test phòng..."
          value={selectedDeckId}
          onChange={(e) => setSelectedDeckId(e.target.value)}
          style={{ width: "300px", padding: "6px" }}
        />
      </div>

      <FilterToolbar
        filterStatus={filterStatus}
        onStatusChange={setFilterStatus}
        searchTerm={searchTerm}
        onSearchChange={setSearchTerm}
      />

      {loading ? (
        <LoadingSpinner />
      ) : (
        <DataTable columns={columns} data={filteredRooms} />
      )}
    </div>
  );
};

export default FinanceRooms;
