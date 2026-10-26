import { useCallback, useEffect, useMemo, useState } from "react";
import api from "../../../api/axios";

import cruiseDeckService from "../../admin/services/cruiseDeckService";
import { activityCruiseTourService } from "../services/activityCruiseTourService";

export default function useOnboardTourConfig() {
  const [tours, setTours] = useState([]);
  const [selectedTourId, setSelectedTourId] = useState("");

  const [decks, setDecks] = useState([]);
  const [activities, setActivities] = useState([]);

  const [loadingTours, setLoadingTours] = useState(false);
  const [loadingTargets, setLoadingTargets] = useState(false);

  const [error, setError] = useState("");

  // =====================================================
  // LOAD APPROVED + READY TOURS
  // =====================================================

  const loadTours = useCallback(async () => {
    try {
      setLoadingTours(true);
      setError("");

      const [approvedResponse, readyResponse] = await Promise.all([
        api.get("/operation/tours/approved"),
        api.get("/operation/tours/ready"),
      ]);

      const approvedTours = Array.isArray(approvedResponse.data)
        ? approvedResponse.data
        : [];

      const readyTours = Array.isArray(readyResponse.data)
        ? readyResponse.data
        : [];

      const uniqueTours = new Map();

      [...approvedTours, ...readyTours].forEach((tour) => {
        if (tour?.id && tour?.cruiseId) {
          uniqueTours.set(tour.id, tour);
        }
      });

      const result = Array.from(uniqueTours.values()).sort((a, b) =>
        (a.name || "").localeCompare(b.name || "", "vi"),
      );

      setTours(result);

      setSelectedTourId((previous) => {
        if (previous && result.some((tour) => tour.id === previous)) {
          return previous;
        }

        return result[0]?.id || "";
      });
    } catch (err) {
      console.error("LOAD ONBOARD TOURS ERROR:", err);

      setError(err.response?.data?.message || "Không thể tải danh sách Tour.");
    } finally {
      setLoadingTours(false);
    }
  }, []);

  // =====================================================
  // SELECTED TOUR
  // =====================================================

  const selectedTour = useMemo(
    () => tours.find((tour) => tour.id === selectedTourId) || null,
    [tours, selectedTourId],
  );

  // =====================================================
  // LOAD DECKS + ACTIVITIES
  // =====================================================

  const loadTourTargets = useCallback(async () => {
    if (!selectedTour?.id || !selectedTour?.cruiseId) {
      setDecks([]);
      setActivities([]);
      return;
    }

    try {
      setLoadingTargets(true);
      setError("");

      const [deckResponse, activityResponse] = await Promise.all([
        cruiseDeckService.getDecks(selectedTour.cruiseId, true),
        activityCruiseTourService.getAll(),
      ]);

      setDecks(Array.isArray(deckResponse) ? deckResponse : []);

      const activityList = Array.isArray(activityResponse)
        ? activityResponse
        : [];

      setActivities(
        activityList.filter((activity) => activity.tourId === selectedTour.id),
      );
    } catch (err) {
      console.error("LOAD ONBOARD TOUR TARGETS ERROR:", err);

      setDecks([]);
      setActivities([]);

      setError(
        err.response?.data?.message ||
          "Không thể tải danh sách tầng boong hoặc hoạt động.",
      );
    } finally {
      setLoadingTargets(false);
    }
  }, [selectedTour]);

  useEffect(() => {
    loadTours();
  }, [loadTours]);

  useEffect(() => {
    loadTourTargets();
  }, [loadTourTargets]);

  return {
    tours,
    selectedTour,
    selectedTourId,
    setSelectedTourId,

    decks,
    activities,

    loadingTours,
    loadingTargets,
    error,

    loadTours,
    loadTourTargets,
  };
}
