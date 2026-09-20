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

// Hook lấy danh sách vòng NFC khả dụng
export const useAvailableWristbands = () => {
  const fetcher = useCallback(
    () => financeService.getAvailableWristbands(),
    [],
  );
  const { data, ...rest } = useAsyncData(fetcher, {
    enabled: true,
    initial: [],
  });
  return { availableWristbands: data, ...rest };
};
