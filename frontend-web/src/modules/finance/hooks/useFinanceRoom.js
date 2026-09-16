// src/modules/finance/hooks/useFinanceRoom.js
import { useCallback } from "react";
import financeService from "../services/financeService";
import useAsyncData from "./useAsyncData";

const useFinanceRoom = (deckId) => {
  const fetcher = useCallback(
    () => financeService.getRoomsByDeck(deckId),
    [deckId],
  );
  const { data, ...rest } = useAsyncData(fetcher, { enabled: !!deckId });
  return { rooms: data, ...rest };
};

export const useRoomTypes = () => {
  const fetcher = useCallback(() => financeService.getRoomTypes(), []);
  const { data, ...rest } = useAsyncData(fetcher);
  return { roomTypes: data, ...rest };
};

export default useFinanceRoom;
