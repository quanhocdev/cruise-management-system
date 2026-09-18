// src/modules/finance/hooks/useFinanceNfc.js
import { useCallback } from "react";
import financeService from "../services/financeService";
import useAsyncData from "./useAsyncData";

const useFinanceNfc = () => {
  const fetcher = useCallback(() => financeService.getNfcCards(), []);
  const { data, ...rest } = useAsyncData(fetcher);
  return { cards: data, ...rest };
};

export default useFinanceNfc;
