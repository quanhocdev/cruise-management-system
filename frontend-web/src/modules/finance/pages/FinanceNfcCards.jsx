// src/modules/finance/pages/FinanceNfcCards.jsx
import React, { useState } from "react";
import FilterToolbar from "../components/FilterToolbar";
import DataTable from "../components/DataTable";
import LoadingSpinner from "../../../components/common/LoadingSpinner";
import useFinanceNfc from "../hooks/useFinanceNfc";

const FinanceNfcCards = () => {
  const { cards, loading } = useFinanceNfc();
  const [filterStatus, setFilterStatus] = useState("ALL");
  const [searchTerm, setSearchTerm] = useState("");

  const filteredCards = cards.filter((card) => {
    const matchStatus = filterStatus === "ALL" || card.status === filterStatus;
    const matchSearch = card.cardUid
      ?.toLowerCase()
      .includes(searchTerm.toLowerCase());
    return matchStatus && matchSearch;
  });

  const columns = [
    { header: "ID Thẻ", accessor: "id" },
    { header: "Mã UID Thẻ", accessor: "cardUid" },
    { header: "Trạng thái", accessor: "status" },
  ];

  return (
    <div style={{ padding: "20px" }}>
      <h2>Quản lý Vòng NFC</h2>
      <FilterToolbar
        filterStatus={filterStatus}
        onStatusChange={setFilterStatus}
        searchTerm={searchTerm}
        onSearchChange={setSearchTerm}
      />

      {loading ? (
        <LoadingSpinner />
      ) : (
        <DataTable columns={columns} data={filteredCards} />
      )}
    </div>
  );
};

export default FinanceNfcCards;
