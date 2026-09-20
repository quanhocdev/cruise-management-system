// src/modules/finance/hooks/useFinanceCheckIn.js
import { useCallback, useState } from "react";
import financeService from "../services/financeService";
import useAsyncData from "./useAsyncData";

// Hook lấy danh sách phòng trống dựa theo tourPackageId
export const useAvailableRooms = (bookingId, tourPackageId) => {
  const fetcher = useCallback(
    () => financeService.getAvailableRooms(bookingId, tourPackageId),
    [bookingId, tourPackageId],
  );
  const { data, ...rest } = useAsyncData(fetcher, {
    enabled: !!bookingId && !!tourPackageId,
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
