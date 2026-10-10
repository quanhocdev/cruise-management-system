// src/modules/convenience/pages/ServiceUsageHistory.jsx
import { useMemo, useState } from "react";
import { RefreshCw, Wrench, AlertCircle } from "lucide-react";

import useServiceUsage from "../hooks/useServiceUsage";
import UsageHistoryFilter from "../components/usage-history/UsageHistoryFilter";
import UsageHistoryTable from "../components/usage-history/UsageHistoryTable";
import "../styles/ServiceUsageHistory.css";

const STATUS_OPTIONS = [
  { value: "ALL", label: "Tất cả trạng thái" },
  { value: "WAITING_CONFIG", label: "Chờ cấu hình" },
  { value: "CONFIGURED", label: "Đã cấu hình" },
  { value: "NOT_STARTED", label: "Chưa diễn ra" },
  { value: "IN_PROGRESS", label: "Đang diễn ra" },
  { value: "COMPLETED", label: "Đã kết thúc" },
  { value: "CANCELLED", label: "Đã hủy" },
];

const ServiceUsageHistory = () => {
  const { serviceUsages, loading, error, refetch } = useServiceUsage();

  const [searchKeyword, setSearchKeyword] = useState("");
  const [statusFilter, setStatusFilter] = useState("ALL");

  const filteredUsages = useMemo(() => {
    const keyword = searchKeyword.trim().toLowerCase();

    return serviceUsages.filter((usage) => {
      const matchesKeyword =
        !keyword ||
        [
          usage.passengerName,
          usage.bookingCode,
          usage.bookingId,
          usage.bookingPassengerId,
          usage.serviceName,
          usage.serviceId,
          usage.serviceTourId,
        ].some((value) =>
          String(value ?? "")
            .toLowerCase()
            .includes(keyword),
        );

      const matchesStatus =
        statusFilter === "ALL" || usage.serviceTourStatus === statusFilter;

      return matchesKeyword && matchesStatus;
    });
  }, [serviceUsages, searchKeyword, statusFilter]);

  const resetFilters = () => {
    setSearchKeyword("");
    setStatusFilter("ALL");
  };

  return (
    <div className="usage-history-page">
      <div className="usage-history-page__header">
        <div className="usage-history-page__heading">
          <div className="usage-history-page__icon">
            <Wrench size={22} />
          </div>

          <div>
            <h1 className="usage-history-page__title">
              Lịch sử sử dụng dịch vụ
            </h1>

            <p className="usage-history-page__subtitle">
              Theo dõi các lượt sử dụng dịch vụ của hành khách.
            </p>
          </div>
        </div>

        <button
          type="button"
          className="usage-history-page__refresh"
          onClick={refetch}
          disabled={loading}
        >
          <RefreshCw
            size={17}
            className={
              loading ? "usage-history-page__refresh-icon--spinning" : ""
            }
          />

          <span>Làm mới</span>
        </button>
      </div>

      {error && (
        <div className="usage-history-page__error">
          <AlertCircle size={18} />
          <span>{error}</span>
        </div>
      )}

      <section className="usage-history-page__section">
        <div className="usage-history-page__section-header">
          <div>
            <h2 className="usage-history-page__section-title">
              Danh sách sử dụng
            </h2>

            <p className="usage-history-page__section-description">
              Lịch sử các dịch vụ đã được ghi nhận sử dụng.
            </p>
          </div>

          <span className="usage-history-page__count">
            {filteredUsages.length}
          </span>
        </div>

        <UsageHistoryFilter
          searchKeyword={searchKeyword}
          setSearchKeyword={setSearchKeyword}
          statusFilter={statusFilter}
          setStatusFilter={setStatusFilter}
          statusOptions={STATUS_OPTIONS}
          onReset={resetFilters}
        />

        <UsageHistoryTable
          usages={filteredUsages}
          loading={loading}
          type="service"
        />
      </section>
    </div>
  );
};

export default ServiceUsageHistory;
