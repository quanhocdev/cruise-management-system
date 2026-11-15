import { useCallback } from "react";
import financeService from "../services/financeService";
import useAsyncData from "./useAsyncData";

export const useFinanceRefunds = (status = "PENDING") => {
  const fetcher = useCallback(
    () => financeService.getRefunds(status),
    [status],
  );

  const { data, ...rest } = useAsyncData(fetcher);

  return {
    refunds: data,
    ...rest,
  };
};

export const useFinanceRefundDetail = (refundId) => {
  const fetcher = useCallback(
    () => financeService.getRefundDetail(refundId),
    [refundId],
  );

  const { data, ...rest } = useAsyncData(fetcher, {
    enabled: !!refundId,
    initial: null,
  });

  return {
    refund: data,
    ...rest,
  };
};
