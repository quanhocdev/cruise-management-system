import { useCallback, useEffect, useRef, useState } from "react";
import {
  getActiveSchedulerPorts,
  previewSchedulerRoute,
  createTourWithRoute,
} from "../services/schedulerRouteService";

export default function useSchedulerRoute() {
  const [ports, setPorts] = useState([]);
  const [departurePortId, setDeparturePortId] = useState("");
  const [destinationPortId, setDestinationPortId] = useState("");

  const [routePreview, setRoutePreview] = useState(null);
  const [loadingPorts, setLoadingPorts] = useState(false);
  const [routeLoading, setRouteLoading] = useState(false);
  const [routeError, setRouteError] = useState("");

  // Vô hiệu hóa kết quả preview cũ khi người dùng đổi lựa chọn.
  const previewRequestRef = useRef(0);

  const loadPorts = useCallback(async () => {
    setLoadingPorts(true);

    try {
      const data = await getActiveSchedulerPorts();
      setPorts(data);
      return data;
    } catch (error) {
      setRouteError(error.message || "Không thể tải danh sách cảng.");
      throw error;
    } finally {
      setLoadingPorts(false);
    }
  }, []);

  useEffect(() => {
    loadPorts().catch(() => {});
  }, [loadPorts]);

  const handleSelectPort = useCallback(
    (port) => {
      if (!port?.id) return;

      previewRequestRef.current += 1;

      setRoutePreview(null);
      setRouteError("");
      setRouteLoading(false);

      const selectedId = String(port.id);

      if (String(departurePortId) === selectedId) {
        setDeparturePortId("");
        return;
      }

      if (String(destinationPortId) === selectedId) {
        setDestinationPortId("");
        return;
      }

      if (!departurePortId) {
        setDeparturePortId(selectedId);
        return;
      }

      if (!destinationPortId) {
        setDestinationPortId(selectedId);
        return;
      }

      // Đã chọn đủ hai cảng: thay cảng đến.
      setDestinationPortId(selectedId);
    },
    [departurePortId, destinationPortId],
  );

  const clearRouteSelection = useCallback(() => {
    previewRequestRef.current += 1;

    setDeparturePortId("");
    setDestinationPortId("");
    setRoutePreview(null);
    setRouteError("");
    setRouteLoading(false);
  }, []);

  const handlePreviewRoute = useCallback(async () => {
    if (!departurePortId || !destinationPortId) {
      setRouteError("Vui lòng chọn cảng khởi hành và cảng đến.");
      return null;
    }

    if (departurePortId === destinationPortId) {
      setRouteError("Cảng khởi hành và cảng đến phải khác nhau.");
      return null;
    }

    const departurePort = ports.find(
      (port) => String(port.id) === String(departurePortId),
    );

    const destinationPort = ports.find(
      (port) => String(port.id) === String(destinationPortId),
    );

    if (!departurePort || !destinationPort) {
      setRouteError("Không tìm thấy thông tin cảng đã chọn.");
      return null;
    }

    const requestId = ++previewRequestRef.current;

    setRouteLoading(true);
    setRouteError("");
    setRoutePreview(null);

    try {
      const result = await previewSchedulerRoute(
        departurePort,
        destinationPort,
      );

      if (requestId !== previewRequestRef.current) return null;

      setRoutePreview(result);
      return result;
    } catch (error) {
      if (requestId !== previewRequestRef.current) return null;

      setRouteError(error.message || "Không thể tính tuyến đường.");
      return null;
    } finally {
      if (requestId === previewRequestRef.current) {
        setRouteLoading(false);
      }
    }
  }, [departurePortId, destinationPortId, ports]);

  const handleCreateTourWithRoute = useCallback(
    async (tourData) => {
      if (!departurePortId || !destinationPortId || !routePreview) {
        throw new Error("Vui lòng chọn hai cảng và tính tuyến đường trước.");
      }

      const result = await createTourWithRoute(
        tourData,
        departurePortId,
        destinationPortId,
      );

      clearRouteSelection();

      return result;
    },
    [departurePortId, destinationPortId, routePreview, clearRouteSelection],
  );

  const departurePort =
    ports.find((port) => String(port.id) === String(departurePortId)) || null;

  const destinationPort =
    ports.find((port) => String(port.id) === String(destinationPortId)) || null;

  return {
    ports,
    departurePort,
    destinationPort,
    departurePortId,
    destinationPortId,
    routePreview,

    loadingPorts,
    routeLoading,
    routeError,

    loadPorts,
    handleSelectPort,
    clearRouteSelection,
    handlePreviewRoute,
    handleCreateTourWithRoute,
  };
}
