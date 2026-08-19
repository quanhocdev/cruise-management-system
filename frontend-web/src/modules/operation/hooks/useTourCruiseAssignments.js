import { useCallback, useState } from "react";
import tourCruiseAssignmentService from "../services/tourCruiseAssignmentService";

export default function useTourCruiseAssignments() {
  const [availableCruises, setAvailableCruises] = useState([]);
  const [cruiseLayout, setCruiseLayout] = useState([]);

  const [cruiseLoading, setCruiseLoading] = useState(false);
  const [layoutLoading, setLayoutLoading] = useState(false);
  const [assigning, setAssigning] = useState(false);

  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  /**
   * Tải danh sách du thuyền khả dụng
   */
  const loadAvailableCruises = useCallback(async (tourId) => {
    setCruiseLoading(true);
    setError("");
    setAvailableCruises([]);

    try {
      const data =
        await tourCruiseAssignmentService.getAvailableCruises(tourId);
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

  /**
   * Tải sơ đồ tầng và khu vực du thuyền
   */
  const loadCruiseLayout = useCallback(async (tourId) => {
    setLayoutLoading(true);
    setError("");
    setCruiseLayout([]);

    try {
      const data = await tourCruiseAssignmentService.getCruiseLayout(tourId);
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

  /**
   * Gán du thuyền cho Tour
   */
  const assignCruise = useCallback(async (tourId, cruiseId) => {
    setAssigning(true);
    setError("");
    setSuccess("");

    try {
      const updated = await tourCruiseAssignmentService.assignCruise(
        tourId,
        cruiseId,
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

  const clearAvailableCruises = useCallback(() => setAvailableCruises([]), []);
  const clearCruiseLayout = useCallback(() => setCruiseLayout([]), []);
  const clearMessages = useCallback(() => {
    setError("");
    setSuccess("");
  }, []);

  return {
    availableCruises,
    cruiseLayout,
    cruiseLoading,
    layoutLoading,
    assigning,
    cruiseError: error,
    cruiseSuccess: success,
    loadAvailableCruises,
    loadCruiseLayout,
    assignCruise,
    clearAvailableCruises,
    clearCruiseLayout,
    clearMessages,
  };
}
