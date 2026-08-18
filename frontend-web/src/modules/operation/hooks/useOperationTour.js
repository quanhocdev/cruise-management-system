import { useCallback, useState } from "react";
import operationTourService from "../services/operationTourService";

export default function useOperationTours() {
  // =====================================================
  // TOURS & CRUISE STATES
  // =====================================================
  const [pendingTours, setPendingTours] = useState([]);
  const [approvedTours, setApprovedTours] = useState([]);
  const [availableCruises, setAvailableCruises] = useState([]);
  const [cruiseLayout, setCruiseLayout] = useState([]);

  // =====================================================
  // ASSIGNMENTS (Bảng ActivityCruiseTour)
  // =====================================================
  const [assignments, setAssignments] = useState([]);

  // =====================================================
  // LOADING STATES
  // =====================================================
  const [loading, setLoading] = useState(false);
  const [cruiseLoading, setCruiseLoading] = useState(false);
  const [layoutLoading, setLayoutLoading] = useState(false);
  const [assignmentLoading, setAssignmentLoading] = useState(false);
  const [assigning, setAssigning] = useState(false);
  const [approving, setApproving] = useState(false);

  // =====================================================
  // MESSAGES
  // =====================================================
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  // =====================================================
  // TOURS API
  // =====================================================
  const loadPendingTours = useCallback(async () => {
    setLoading(true);
    setError("");

    try {
      const data = await operationTourService.getPendingTours();
      const tourList = Array.isArray(data)
        ? data
        : data?.content || data?.data || [];
      setPendingTours(tourList);
    } catch (err) {
      console.error("LOAD OPERATION PENDING TOURS ERROR:", err);
      setError(
        err.response?.data?.message ||
          "Không thể tải danh sách Tour chờ duyệt.",
      );
    } finally {
      setLoading(false);
    }
  }, []);

  const loadApprovedTours = useCallback(async () => {
    setLoading(true);
    setError("");

    try {
      const data = await operationTourService.getApprovedTours();
      const tourList = Array.isArray(data)
        ? data
        : data?.content || data?.data || [];
      setApprovedTours(tourList);
    } catch (err) {
      console.error("LOAD OPERATION APPROVED TOURS ERROR:", err);
      setError(
        err.response?.data?.message ||
          "Không thể tải danh sách Tour đã được duyệt.",
      );
    } finally {
      setLoading(false);
    }
  }, []);

  // =====================================================
  // CRUISE & LAYOUT API
  // =====================================================
  const loadAvailableCruises = useCallback(async (tourId) => {
    setCruiseLoading(true);
    setError("");
    setAvailableCruises([]);

    try {
      const data = await operationTourService.getAvailableCruises(tourId);
      const cruiseList = Array.isArray(data)
        ? data
        : data?.content || data?.data || [];
      setAvailableCruises(cruiseList);
      return cruiseList;
    } catch (err) {
      console.error("LOAD AVAILABLE CRUISES ERROR:", err);
      setError(
        err.response?.data?.message ||
          "Không thể tải danh sách du thuyền khả dụng.",
      );
      return [];
    } finally {
      setCruiseLoading(false);
    }
  }, []);

  const loadCruiseLayout = useCallback(async (tourId) => {
    setLayoutLoading(true);
    setError("");
    setCruiseLayout([]);

    try {
      const data = await operationTourService.getCruiseLayout(tourId);
      const layoutList = Array.isArray(data)
        ? data
        : data?.content || data?.data || [];
      setCruiseLayout(layoutList);
      return layoutList;
    } catch (err) {
      console.error("LOAD CRUISE LAYOUT ERROR:", err);
      setError(
        err.response?.data?.message ||
          "Không thể tải sơ đồ tầng và khu vực của du thuyền.",
      );
      return [];
    } finally {
      setLayoutLoading(false);
    }
  }, []);

  const assignCruise = useCallback(async (tourId, cruiseId) => {
    setAssigning(true);
    setError("");
    setSuccess("");

    try {
      const updated = await operationTourService.assignCruise(tourId, cruiseId);
      setPendingTours((prev) =>
        prev.map((tour) => (tour.id === tourId ? updated : tour)),
      );
      setSuccess("Gán du thuyền cho Tour thành công.");
      return updated;
    } catch (err) {
      console.error("ASSIGN CRUISE ERROR:", err);
      setError(
        err.response?.data?.message || "Không thể gán du thuyền cho Tour.",
      );
      throw err;
    } finally {
      setAssigning(false);
    }
  }, []);

  // =====================================================
  // ASSIGNMENTS API (BẢNG ActivityCruiseTour)
  // =====================================================
  const loadAssignments = useCallback(async (tourId) => {
    setAssignmentLoading(true);
    setError("");

    try {
      const data =
        await operationTourService.getActivityCruiseAssignments(tourId);
      const assignmentList = Array.isArray(data)
        ? data
        : data?.content || data?.data || [];
      setAssignments(assignmentList);
      return assignmentList;
    } catch (err) {
      console.error("LOAD ACTIVITY CRUISE ASSIGNMENTS ERROR:", err);
      setError(
        err.response?.data?.message || "Không thể tải danh sách phân công.",
      );
      return [];
    } finally {
      setAssignmentLoading(false);
    }
  }, []);

  /**
   * Phân công 1 khu vực cho Hoạt động (Tạo bản ghi mới trong bảng ActivityCruiseTour)
   */
  /**
   * Phân công 1 khu vực cho Hoạt động (Tạo bản ghi mới trong bảng ActivityCruiseTour)
   */
  const assignActivityCruiseArea = useCallback(
    async (tourIdOrPayload, cruiseAreaId) => {
      setAssignmentLoading(true);
      setError("");
      setSuccess("");

      try {
        // Tự động chuẩn hóa dữ liệu thành Object JSON hợp lệ
        let payload = {};
        if (typeof tourIdOrPayload === "object" && tourIdOrPayload !== null) {
          payload = tourIdOrPayload;
        } else {
          payload = {
            tourId: Number(tourIdOrPayload),
            cruiseAreaId: Number(cruiseAreaId),
          };
        }

        console.log("👉 PAYLOAD GỬI LÊN SPRING BOOT:", payload);

        const result =
          await operationTourService.assignActivityCruiseArea(payload);

        // Cập nhật lại danh sách assignments local
        setAssignments((prev) => {
          const exists = prev.some((item) => item.id === result.id);
          return exists ? prev : [...prev, result];
        });

        setSuccess("Lưu phân công khu vực thành công.");
        return result;
      } catch (err) {
        console.error("ASSIGN ACTIVITY CRUISE AREA ERROR:", err);
        setError(err.response?.data?.message || "Không thể phân công khu vực.");
        throw err;
      } finally {
        setAssignmentLoading(false);
      }
    },
    [],
  );

  /**
   * Xóa phân công khu vực theo assignmentId
   */
  const deleteActivityCruiseAssignment = useCallback(async (assignmentId) => {
    setAssignmentLoading(true);
    setError("");
    setSuccess("");

    try {
      await operationTourService.deleteActivityCruiseAssignment(assignmentId);

      setAssignments((prev) =>
        prev.filter((assignment) => assignment.id !== assignmentId),
      );

      setSuccess("Đã hủy phân công khu vực.");
    } catch (err) {
      console.error("DELETE ACTIVITY CRUISE ASSIGNMENT ERROR:", err);
      setError(err.response?.data?.message || "Không thể xóa phân công.");
      throw err;
    } finally {
      setAssignmentLoading(false);
    }
  }, []);

  // =====================================================
  // APPROVE TOUR
  // =====================================================
  const approveTour = useCallback(async (tourId, payload = null) => {
    setApproving(true);
    setError("");
    setSuccess("");

    try {
      const updated = await operationTourService.approveTour(tourId, payload);

      setPendingTours((prev) => prev.filter((tour) => tour.id !== tourId));
      if (updated) {
        setApprovedTours((prev) => [...prev, updated]);
      }

      setSuccess("Duyệt Tour thành công.");
      return updated;
    } catch (err) {
      console.error("APPROVE TOUR ERROR:", err);
      setError(err.response?.data?.message || "Không thể duyệt Tour.");
      throw err;
    } finally {
      setApproving(false);
    }
  }, []);

  // =====================================================
  // CLEAR HELPERS
  // =====================================================
  const clearAvailableCruises = useCallback(() => setAvailableCruises([]), []);
  const clearCruiseLayout = useCallback(() => setCruiseLayout([]), []);
  const clearAssignments = useCallback(() => setAssignments([]), []);
  const clearMessages = useCallback(() => {
    setError("");
    setSuccess("");
  }, []);

  return {
    pendingTours,
    approvedTours,
    availableCruises,
    cruiseLayout,
    assignments,
    setAssignments,

    loading,
    cruiseLoading,
    layoutLoading,
    assignmentLoading,
    assigning,
    approving,

    error,
    success,

    loadPendingTours,
    loadApprovedTours,
    loadAvailableCruises,
    assignCruise,
    approveTour,

    loadCruiseLayout,

    loadAssignments,
    assignActivityCruiseArea,
    deleteActivityCruiseAssignment,

    clearAvailableCruises,
    clearCruiseLayout,
    clearAssignments,
    clearMessages,
  };
}
