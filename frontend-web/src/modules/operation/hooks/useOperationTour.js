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
  const [assigning, setAssigning] = useState(false);

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

      console.log(" [RAW BACKEND DATA] cruiseLayout:", data);

      const layoutList = Array.isArray(data)
        ? data
        : data?.content || data?.data || [];

      console.log(" [PROCESSED LAYOUT DATA]:", layoutList);
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
  // ASSIGN CRUISE
  // =====================================================

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
  // ASSIGN ACTIVITY CRUISE AREA (SINGLE & BATCH)
  // =====================================================

  const assignActivityCruiseArea = useCallback(async (payload) => {
    setAssignmentLoading(true);
    setError("");
    setSuccess("");

    try {
      const result =
        await operationTourService.assignActivityCruiseArea(payload);

      // Nếu gửi danh sách mảng nhiều khu vực
      if (Array.isArray(result)) {
        setAssignments((prev) => [...prev, ...result]);
      } else if (result) {
        setAssignments((prev) => [...prev, result]);
      }

      setSuccess("Phân công khu vực cho Tour thành công.");
      return result;
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

  const approveTour = useCallback(async (tourId, payload = null) => {
    setApproving(true);
    setError("");
    setSuccess("");

    try {
      const updated = await operationTourService.approveTour(tourId, payload);

      // Cập nhật danh sách local ở FE: Xóa tour khỏi pendingTours và thêm vào approvedTours
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
    setAssignments, // Bổ sung setter để cập nhật trực tiếp state assignment trên FE

    // Loading
    loading,
    cruiseLoading,
    layoutLoading,
    assignmentLoading,
    assigning,
    approving,

    // Messages
    error,
    success,

    // Tour APIs
    loadPendingTours,
    loadApprovedTours,
    loadAvailableCruises,
    assignCruise,
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
