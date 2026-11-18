// src/modules/passenger/hooks/trip-detail/usePassengerTripCruise.js
import { useCallback, useState } from "react";
import passengerTripService from "../../services/passengerTripService";
import usePassengerResource from "./usePassengerResource";

export default function usePassengerTripCruise(tourId) {
  const fetchCruise = useCallback(
    () => passengerTripService.getCruise(tourId),
    [tourId],
  );

  const fetchDecks = useCallback(
    () => passengerTripService.getCruiseDecks(tourId),
    [tourId],
  );

  const cruiseResource = usePassengerResource(fetchCruise, null);
  const decksResource = usePassengerResource(fetchDecks, []);

  const [deckAreas, setDeckAreas] = useState({});
  const [areasLoading, setAreasLoading] = useState(false);
  const [areasError, setAreasError] = useState("");

  const loadCruise = useCallback(async () => {
    if (!tourId) return;

    await Promise.allSettled([cruiseResource.load(), decksResource.load()]);
  }, [tourId, cruiseResource.load, decksResource.load]);

  const loadDeckAreas = useCallback(
    async (deckId) => {
      if (!tourId || !deckId) return;

      setAreasLoading(true);
      setAreasError("");

      try {
        const result = await passengerTripService.getCruiseDeckAreas(
          tourId,
          deckId,
        );

        setDeckAreas((previous) => ({
          ...previous,
          [deckId]: result,
        }));

        return result;
      } catch (err) {
        setAreasError(
          err.response?.data?.message || "Không thể tải khu vực của tầng tàu.",
        );
        throw err;
      } finally {
        setAreasLoading(false);
      }
    },
    [tourId],
  );

  return {
    cruise: cruiseResource.data,
    decks: decksResource.data,
    deckAreas,
    cruiseLoading: cruiseResource.loading,
    decksLoading: decksResource.loading,
    areasLoading,
    cruiseError: cruiseResource.error,
    decksError: decksResource.error,
    areasError,
    loadCruise,
    loadDeckAreas,
  };
}
