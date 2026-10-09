import { useCallback, useEffect, useMemo, useState } from "react";
import { activityVisitUsageService } from "../services/activityVisitUsageService";

const useActivityVisitUsage = () => {
  const [usages, setUsages] = useState([]);

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const [searchKeyword, setSearchKeyword] = useState("");
  const [statusFilter, setStatusFilter] = useState("ALL");

  // LOAD ALL USAGES
  const loadUsages = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);

      const usagesData = await activityVisitUsageService.getAll();

      setUsages(Array.isArray(usagesData) ? usagesData : []);
    } catch (err) {
      console.error("LOAD ACTIVITY VISIT USAGES ERROR:", err);

      setError(
        err.response?.data?.message ||
          "Không thể tải danh sách sử dụng hoạt động bờ biển",
      );
    } finally {
      setLoading(false);
    }
  }, []);

  // INITIAL LOAD
  useEffect(() => {
    loadUsages();
  }, [loadUsages]);

  // FILTER
  const filteredUsages = useMemo(() => {
    const keyword = searchKeyword.trim().toLowerCase();

    return usages.filter((usage) => {
      const matchesSearch =
        !keyword ||
        usage.passengerName?.toLowerCase().includes(keyword) ||
        usage.bookingCode?.toLowerCase().includes(keyword) ||
        usage.activityName?.toLowerCase().includes(keyword);

      const matchesStatus =
        statusFilter === "ALL" || usage.visitTourStatus === statusFilter;

      return matchesSearch && matchesStatus;
    });
  }, [usages, searchKeyword, statusFilter]);

  // RESET FILTER
  const resetFilters = useCallback(() => {
    setSearchKeyword("");
    setStatusFilter("ALL");
  }, []);

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

export default useActivityVisitUsage;
