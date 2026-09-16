import { useCallback } from "react";
import financeService from "../services/financeService";
import useAsyncData from "./useAsyncData";

export const useDecksByCruise = (cruiseId) => {
  const fetcher = useCallback(
    () => financeService.getDecksByCruise(cruiseId),
    [cruiseId],
  );
  const { data, ...rest } = useAsyncData(fetcher, { enabled: !!cruiseId });
  return { decks: data, ...rest };
};

export const useAreasByDeck = (deckId) => {
  const fetcher = useCallback(
    () => financeService.getAreasByDeck(deckId),
    [deckId],
  );
  const { data, ...rest } = useAsyncData(fetcher, { enabled: !!deckId });
  return { areas: data, ...rest };
};
