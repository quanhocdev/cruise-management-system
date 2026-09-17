// src/modules/finance/hooks/useAsyncData.js
import { useCallback, useEffect, useRef, useState } from "react";

const EMPTY_LIST = [];

/**
 * @param fetcher hàm async, PHẢI được memo bằng useCallback ở nơi gọi
 * @param enabled false => không gọi API, trả về initial
 * @param initial giá trị mặc định (dùng hằng số, đừng truyền [] inline)
 */
export default function useAsyncData(
  fetcher,
  { enabled = true, initial = EMPTY_LIST } = {},
) {
  const [data, setData] = useState(initial);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const requestId = useRef(0);
  const initialRef = useRef(initial);

  const run = useCallback(async () => {
    requestId.current += 1;
    const id = requestId.current;

    if (!enabled) {
      setData(initialRef.current);
      setError(null);
      setLoading(false);
      return;
    }

    setLoading(true);
    setError(null);
    try {
      const result = await fetcher();
      if (id !== requestId.current) return; // response cũ, bỏ qua
      setData(result ?? initialRef.current);
    } catch (err) {
      if (id !== requestId.current) return;
      console.error("[finance] fetch error:", err);
      setError(err);
      setData(initialRef.current);
    } finally {
      if (id === requestId.current) setLoading(false);
    }
  }, [fetcher, enabled]);

  useEffect(() => {
    run();
  }, [run]);

  return { data, loading, error, reload: run };
}

export { EMPTY_LIST };
