// src/modules/onboard/pages/ActivityCruiseUsage.jsx
import { RefreshCw, Activity, AlertCircle } from "lucide-react";

import useActivityCruiseUsage from "../hooks/useActivityCruiseUsage";

import ActivityCruiseUsageFilter from "../components/activity-cruise-usage/ActivityCruiseUsageFilter";
import ActivityCruiseUsageTable from "../components/activity-cruise-usage/ActivityCruiseUsageTable";

import "../styles/ActivityCruiseUsage.css";

const ActivityCruiseUsage = () => {
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
  } = useActivityCruiseUsage();

  return (
    <div className="activity-cruise-usage-page">
      {/* =====================================================
          HEADER
      ===================================================== */}
      <div className="activity-cruise-usage-page__header">
        <div className="activity-cruise-usage-page__heading">
          <div className="activity-cruise-usage-page__icon">
            <Activity size={22} />
          </div>

          <div>
            <h1 className="activity-cruise-usage-page__title">
              Lịch sử sử dụng hoạt động
            </h1>

            <p className="activity-cruise-usage-page__subtitle">
              Theo dõi hành khách đã sử dụng các hoạt động trên tàu
            </p>
          </div>
        </div>

        <button
          type="button"
          className="activity-cruise-usage-page__refresh"
          onClick={loadUsages}
          disabled={loading}
        >
          <RefreshCw
            size={17}
            className={
              loading
                ? "activity-cruise-usage-page__refresh-icon--spinning"
                : ""
            }
          />

          <span>Làm mới</span>
        </button>
      </div>

      {/* =====================================================
          ERROR
      ===================================================== */}
      {error && (
        <div className="activity-cruise-usage-page__error">
          <AlertCircle size={18} />
          <span>{error}</span>
        </div>
      )}

      {/* =====================================================
          MAIN SECTION
      ===================================================== */}
      <section className="activity-cruise-usage-page__section">
        <div className="activity-cruise-usage-page__section-header">
          <div>
            <h2 className="activity-cruise-usage-page__section-title">
              Danh sách sử dụng
            </h2>

            <p className="activity-cruise-usage-page__section-description">
              Danh sách các lượt sử dụng hoạt động của hành khách.
            </p>
          </div>

          <span className="activity-cruise-usage-page__count">
            {filteredUsages.length}
          </span>
        </div>

        {/* =================================================
            FILTER
        ================================================= */}
        <ActivityCruiseUsageFilter
          searchKeyword={searchKeyword}
          setSearchKeyword={setSearchKeyword}
          statusFilter={statusFilter}
          setStatusFilter={setStatusFilter}
          onReset={resetFilters}
        />

        {/* =================================================
            TABLE
        ================================================= */}
        <ActivityCruiseUsageTable usages={filteredUsages} loading={loading} />
      </section>
    </div>
  );
};

export default ActivityCruiseUsage;
