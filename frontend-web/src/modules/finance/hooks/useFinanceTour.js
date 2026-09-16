// src/modules/finance/hooks/useFinanceTour.js
import { useCallback, useMemo } from "react";
import financeService from "../services/financeService";
import useAsyncData from "./useAsyncData";
import { FINANCE_VISIBLE_TOUR_STATUSES } from "../constants/statuses";

export const useFinanceTours = (cruiseId) => {
  const fetcher = useCallback(
    () => financeService.getTours(cruiseId),
    [cruiseId],
  );
  const { data, loading, error, reload } = useAsyncData(fetcher);

  // Chốt chặn phía FE, phòng khi BE chưa lọc status
  const tours = useMemo(
    () =>
      data.filter(
        (t) =>
          !t.statusTrip || FINANCE_VISIBLE_TOUR_STATUSES.includes(t.statusTrip),
      ),
    [data],
  );

  return { tours, loading, error, reload };
};

export const useFinanceSchedules = (tourId) => {
  const fetcher = useCallback(
    () => financeService.getSchedulesByTour(tourId),
    [tourId],
  );
  const { data, ...rest } = useAsyncData(fetcher, { enabled: !!tourId });
  return { schedules: data, ...rest };
};

export const useScheduleStops = (scheduleId) => {
  const fetcher = useCallback(
    () => financeService.getScheduleStops(scheduleId),
    [scheduleId],
  );
  const { data, ...rest } = useAsyncData(fetcher, { enabled: !!scheduleId });
  return { stops: data, ...rest };
};

export const useTourCruise = (tourId) => {
  const fetcher = useCallback(
    () => financeService.getCruiseByTour(tourId),
    [tourId],
  );
  const { data, ...rest } = useAsyncData(fetcher, {
    enabled: !!tourId,
    initial: null,
  });
  return { cruise: data, ...rest };
};
