import { useCallback, useState } from "react";
import operationTourService from "../services/operationTourService";

export default function useOperationTours() {
  // =====================================================
  // TOURS
  // =====================================================

  const [pendingTours, setPendingTours] = useState([]);
  const [approvedTours, setApprovedTours] = useState([]);

  // =====================================================
  // CRUISE
  // =====================================================

  const [availableCruises, setAvailableCruises] = useState([]);
  const [cruiseLayout, setCruiseLayout] = useState([]);

  // =====================================================
  // ASSIGNMENT
  // =====================================================

  const [assignments, setAssignments] = useState([]);

  // =====================================================
  // LOADING
  // =====================================================

  const [loading, setLoading] = useState(false);
  const [cruiseLoading, setCruiseLoading] = useState(false);
  const [layoutLoading, setLayoutLoading] = useState(false);
  const [assignmentLoading, setAssignmentLoading] = useState(false);
  const [approving, setApproving] = useState(false);

  // =====================================================
  // MESSAGE
  // =====================================================

  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  // =====================================================
  // LOAD PENDING TOURS
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

  // =====================================================
  // LOAD APPROVED TOURS
  // =====================================================

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
  // LOAD AVAILABLE CRUISES
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

  // =====================================================
  // LOAD CRUISE LAYOUT
  // =====================================================

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

  // =====================================================
  // LOAD ASSIGNMENTS
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

  // =====================================================
  // ASSIGN ACTIVITY CRUISE AREA
  // =====================================================

  const assignActivityCruiseArea = useCallback(async (tourId, cruiseAreaId) => {
    setAssignmentLoading(true);
    setError("");
    setSuccess("");

    try {
      const created = await operationTourService.assignActivityCruiseArea(
        tourId,
        cruiseAreaId,
      );

      setAssignments((prev) => [...prev, created]);

      setSuccess("Phân công khu vực cho Tour thành công.");

      return created;
    } catch (err) {
      console.error("ASSIGN ACTIVITY CRUISE AREA ERROR:", err);

      setError(err.response?.data?.message || "Không thể phân công khu vực.");

      throw err;
    } finally {
      setAssignmentLoading(false);
    }
  }, []);

  // =====================================================
  // DELETE ASSIGNMENT
  // =====================================================

  const deleteActivityCruiseAssignment = useCallback(async (assignmentId) => {
    setAssignmentLoading(true);
    setError("");
    setSuccess("");

    try {
      await operationTourService.deleteActivityCruiseAssignment(assignmentId);

      setAssignments((prev) =>
        prev.filter((assignment) => assignment.id !== assignmentId),
      );

      setSuccess("Đã xóa phân công khu vực.");
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

  const approveTour = useCallback(async (tourId, cruiseId) => {
    setApproving(true);
    setError("");
    setSuccess("");

    try {
      const updated = await operationTourService.approveTour(tourId, cruiseId);

      setPendingTours((prev) => prev.filter((tour) => tour.id !== tourId));

      setSuccess("Duyệt Tour và gán du thuyền thành công.");

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
  // CLEAR
  // =====================================================

  const clearAvailableCruises = useCallback(() => {
    setAvailableCruises([]);
  }, []);

  const clearCruiseLayout = useCallback(() => {
    setCruiseLayout([]);
  }, []);

  const clearAssignments = useCallback(() => {
    setAssignments([]);
  }, []);

  const clearMessages = useCallback(() => {
    setError("");
    setSuccess("");
  }, []);

  // =====================================================
  // RETURN
  // =====================================================

  return {
    // Tours
    pendingTours,
    approvedTours,

    // Cruise
    availableCruises,
    cruiseLayout,

    // Assignment
    assignments,

    // Loading
    loading,
    cruiseLoading,
    layoutLoading,
    assignmentLoading,
    approving,

    // Messages
    error,
    success,

    // Tour APIs
    loadPendingTours,
    loadApprovedTours,
    loadAvailableCruises,
    approveTour,

    // Cruise layout
    loadCruiseLayout,

    // Assignment
    loadAssignments,
    assignActivityCruiseArea,
    deleteActivityCruiseAssignment,

    // Clear
    clearAvailableCruises,
    clearCruiseLayout,
    clearAssignments,
    clearMessages,
  };
}
