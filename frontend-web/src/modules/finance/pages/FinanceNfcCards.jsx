// src/modules/finance/pages/FinanceNfcCards.jsx
import React, { useMemo, useState } from "react";
import FilterToolbar from "../components/FilterToolbar";
import DataTable from "../components/DataTable";
import LoadingSpinner from "../../../components/common/LoadingSpinner";
import useFinanceNfc from "../hooks/useFinanceNfc";
import { NFC_STATUS_OPTIONS, labelOf } from "../constants/statuses";
import { matchesSearch, formatDateTime } from "../utils/format";
import "../styles/layout.css";

const FinanceNfcCards = () => {
  const { cards, loading, error, reload } = useFinanceNfc();
  const [filterStatus, setFilterStatus] = useState("ALL");
  const [searchTerm, setSearchTerm] = useState("");

  const filteredCards = useMemo(
    () =>
      cards.filter(
        (card) =>
          (filterStatus === "ALL" || card.status === filterStatus) &&
          matchesSearch(searchTerm, card.cardUid),
      ),
    [cards, filterStatus, searchTerm],
  );

  const columns = [
    { header: "Mã UID Thẻ", accessor: "cardUid" },
    {
      header: "Trạng thái",
      accessor: "status",
      render: (v) => labelOf(NFC_STATUS_OPTIONS, v),
    },
    { header: "Ngày tạo", accessor: "createdAt", render: formatDateTime },
  ];

  return (
    <div className="finance-page">
      <div className="finance-page__header">
        <div>
          <h2 className="finance-page__title">Quản lý Vòng NFC</h2>
          <p className="finance-page__subtitle">
            Danh sách toàn bộ thẻ NFC trong hệ thống
          </p>
        </div>
      </div>

      <div className="finance-card">
        <FilterToolbar
          filterStatus={filterStatus}
          onStatusChange={setFilterStatus}
          searchTerm={searchTerm}
          onSearchChange={setSearchTerm}
          statusOptions={NFC_STATUS_OPTIONS}
          searchPlaceholder="Tìm theo mã UID..."
        />
      </div>

      {error && (
        <div className="finance-error-banner">
          <span>Không tải được dữ liệu.</span>
          <button onClick={reload}>Thử lại</button>
        </div>
      )}

      {loading ? (
        <LoadingSpinner />
      ) : (
        <DataTable columns={columns} data={filteredCards} />
      )}
    </div>
  );
};

export default FinanceNfcCards;
