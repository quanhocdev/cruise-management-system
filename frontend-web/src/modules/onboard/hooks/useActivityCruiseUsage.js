// src/modules/onboard/hooks/useActivityCruiseUsage.js
import { useCallback, useEffect, useMemo, useState } from "react";
import { activityCruiseUsageService } from "../services/activityCruiseUsageService";

const useActivityCruiseUsage = () => {
  const [usages, setUsages] = useState([]);

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const [searchKeyword, setSearchKeyword] = useState("");
  const [statusFilter, setStatusFilter] = useState("ALL");

  // =====================================================
  // LOAD ALL USAGES
  // =====================================================

  const loadUsages = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);

      const usagesData = await activityCruiseUsageService.getAll();

      setUsages(usagesData || []);
    } catch (err) {
      console.error("LOAD ACTIVITY CRUISE USAGES ERROR:", err);

      setError(
        err.response?.data?.message ||
          "Không thể tải danh sách sử dụng hoạt động",
      );
    } finally {
      setLoading(false);
    }
  }, []);

  // =====================================================
  // INITIAL LOAD
  // =====================================================

  useEffect(() => {
    loadUsages();
  }, [loadUsages]);

  // =====================================================
  // FILTER
  // =====================================================

  const filteredUsages = useMemo(() => {
    const keyword = searchKeyword.trim().toLowerCase();

    return usages.filter((usage) => {
      // -----------------------------------------------
      // SEARCH
      // -----------------------------------------------

      const matchesSearch =
        !keyword ||
        usage.passengerName?.toLowerCase().includes(keyword) ||
        usage.bookingCode?.toLowerCase().includes(keyword) ||
        usage.activityName?.toLowerCase().includes(keyword);

      // -----------------------------------------------
      // STATUS
      // -----------------------------------------------

      const matchesStatus =
        statusFilter === "ALL" ||
        usage.activityCruiseTourStatus === statusFilter;

      return matchesSearch && matchesStatus;
    });
  }, [usages, searchKeyword, statusFilter]);

  // =====================================================
  // RESET FILTER
  // =====================================================

  const resetFilters = useCallback(() => {
    setSearchKeyword("");
    setStatusFilter("ALL");
  }, []);

  // =====================================================
  // RETURN
  // =====================================================

  return {
    usages,
    filteredUsages,

    searchKeyword,
    setSearchKeyword,

    statusFilter,
    setStatusFilter,

    loading,
    error,

    loadUsages,
    resetFilters,
  };
};

export default useActivityCruiseUsage;
