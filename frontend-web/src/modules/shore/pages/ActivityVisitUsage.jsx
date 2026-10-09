// src/modules/shore/pages/ActivityVisitUsage.jsx

import { RefreshCw, Waves, AlertCircle } from "lucide-react";

import useActivityVisitUsage from "../hooks/useActivityVisitUsage";

import ActivityVisitUsageFilter from "../components/activity-visit-usage/ActivityVisitUsageFilter";
import ActivityVisitUsageTable from "../components/activity-visit-usage/ActivityVisitUsageTable";

import "../styles/ActivityVisitUsage.css";

const ActivityVisitUsage = () => {
  const {
    filteredUsages,
    searchKeyword,
    setSearchKeyword,
    statusFilter,
    setStatusFilter,
    loading,
    error,
    loadUsages,
    resetFilters,
  } = useActivityVisitUsage();

  return (
    <div className="activity-visit-usage-page">
      {/* HEADER */}
      <div className="activity-visit-usage-page__header">
        <div className="activity-visit-usage-page__heading">
          <div className="activity-visit-usage-page__icon">
            <Waves size={22} />
          </div>

          <div>
            <h1 className="activity-visit-usage-page__title">
              Lịch sử sử dụng hoạt động
            </h1>

            <p className="activity-visit-usage-page__subtitle">
              Theo dõi hành khách đã sử dụng các hoạt động bờ biển
            </p>
          </div>
        </div>

        <button
          type="button"
          className="activity-visit-usage-page__refresh"
          onClick={loadUsages}
          disabled={loading}
        >
          <RefreshCw
            size={17}
            className={
              loading ? "activity-visit-usage-page__refresh-icon--spinning" : ""
            }
          />

          <span>Làm mới</span>
        </button>
      </div>

      {/* ERROR */}
      {error && (
        <div className="activity-visit-usage-page__error">
          <AlertCircle size={18} />
          <span>{error}</span>
        </div>
      )}

      {/* MAIN SECTION */}
      <section className="activity-visit-usage-page__section">
        <div className="activity-visit-usage-page__section-header">
          <div>
            <h2 className="activity-visit-usage-page__section-title">
              Danh sách sử dụng
            </h2>

            <p className="activity-visit-usage-page__section-description">
              Danh sách các lượt sử dụng hoạt động bờ biển của hành khách.
            </p>
          </div>

          <span className="activity-visit-usage-page__count">
            {filteredUsages.length}
          </span>
        </div>

        {/* FILTER */}
        <ActivityVisitUsageFilter
          searchKeyword={searchKeyword}
          setSearchKeyword={setSearchKeyword}
          statusFilter={statusFilter}
          setStatusFilter={setStatusFilter}
          onReset={resetFilters}
        />

        {/* TABLE */}
        <ActivityVisitUsageTable usages={filteredUsages} loading={loading} />
      </section>
    </div>
  );
};

export default ActivityVisitUsage;
