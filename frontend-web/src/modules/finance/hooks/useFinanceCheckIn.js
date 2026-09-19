// src/modules/finance/hooks/useFinanceCheckIn.js
import { useCallback, useState } from "react";
import financeService from "../services/financeService";
import useAsyncData from "./useAsyncData";

// Hook lấy danh sách phòng trống
export const useAvailableRooms = (bookingId, roomTypeId) => {
  const fetcher = useCallback(
    () => financeService.getAvailableRooms(bookingId, roomTypeId),
    [bookingId, roomTypeId],
  );
  const { data, ...rest } = useAsyncData(fetcher, {
    enabled: !!bookingId && !!roomTypeId,
    initial: [],
  });
  return { availableRooms: data, ...rest };
};

// Hook lấy danh sách vòng NFC khả dụng của Tour
export const useAvailableWristbands = (tourId) => {
  const fetcher = useCallback(
    () => financeService.getAvailableWristbands(tourId),
    [tourId],
  );
  const { data, ...rest } = useAsyncData(fetcher, {
    enabled: !!tourId,
    initial: [],
  });
  return { availableWristbands: data, ...rest };
};
