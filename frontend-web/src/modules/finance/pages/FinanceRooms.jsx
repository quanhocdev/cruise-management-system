// src/modules/finance/pages/FinanceRooms.jsx
import React, { useMemo, useState } from "react";
import TourSelector from "../components/TourSelector";
import DeckSelector from "../components/DeckSelector";
import FilterToolbar from "../components/FilterToolbar";
import DataTable from "../components/DataTable";
import LoadingSpinner from "../../../components/common/LoadingSpinner";
import useFinanceRoom from "../hooks/useFinanceRoom";
import { useTourCruise } from "../hooks/useFinanceTour";
import { ROOM_STATUS_OPTIONS, labelOf } from "../constants/statuses";
import { matchesSearch } from "../utils/format";
import "../styles/layout.css";

const FinanceRooms = () => {
  const [selectedTourId, setSelectedTourId] = useState("");
  const [selectedDeckId, setSelectedDeckId] = useState("");
  const [filterStatus, setFilterStatus] = useState("ALL");
  const [searchTerm, setSearchTerm] = useState("");

  const { cruise, loading: loadingCruise } = useTourCruise(selectedTourId);
  const { rooms, loading: loadingRooms } = useFinanceRoom(selectedDeckId);

  const handleSelectTour = (tourId) => {
    setSelectedTourId(tourId);
    setSelectedDeckId("");
  };

  const filteredRooms = useMemo(
    () =>
      rooms.filter(
        (room) =>
          (filterStatus === "ALL" || room.status === filterStatus) &&
          matchesSearch(searchTerm, room.code, room.roomTypeName),
      ),
    [rooms, filterStatus, searchTerm],
  );

  const columns = [
    { header: "Mã phòng", accessor: "code" },
    { header: "Loại phòng", accessor: "roomTypeName" },
    {
      header: "Trạng thái",
      accessor: "status",
      render: (v) => labelOf(ROOM_STATUS_OPTIONS, v),
    },
  ];

  return (
    <div className="finance-page">
      <div className="finance-page__header">
        <div>
          <h2 className="finance-page__title">Quản lý Phòng</h2>
          <p className="finance-page__subtitle">
            Xem sơ đồ phòng theo tour và tầng tàu
          </p>
        </div>
      </div>

      <div className="finance-card">
        <TourSelector
          selectedTourId={selectedTourId}
          onSelectTour={handleSelectTour}
        />
        {selectedTourId && (
          <p className="finance-hint-text">
            Tàu:{" "}
            <strong>
              {loadingCruise
                ? "đang tải..."
                : (cruise?.name ?? "không xác định")}
            </strong>
          </p>
        )}
        <DeckSelector
          cruiseId={cruise?.id}
          selectedDeckId={selectedDeckId}
          onSelectDeck={setSelectedDeckId}
        />
        <FilterToolbar
          filterStatus={filterStatus}
          onStatusChange={setFilterStatus}
          searchTerm={searchTerm}
          onSearchChange={setSearchTerm}
          statusOptions={ROOM_STATUS_OPTIONS}
          searchPlaceholder="Tìm theo mã phòng hoặc loại phòng..."
        />
      </div>

      {loadingRooms ? (
        <LoadingSpinner />
      ) : (
        <DataTable
          columns={columns}
          data={filteredRooms}
          emptyText={
            !selectedDeckId
              ? "Chọn Tour và tầng để xem danh sách phòng"
              : "Tầng này chưa có phòng nào"
          }
        />
      )}
    </div>
  );
};

export default FinanceRooms;
