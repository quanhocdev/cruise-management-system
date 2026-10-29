const API_BASE_URL = `${(
  import.meta.env.VITE_API_BASE_URL || "http://localhost:8080"
).replace(/\/$/, "")}/api`;

async function request(path, options = {}) {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    credentials: "include",
    ...options,
    headers: {
      ...(options.body ? { "Content-Type": "application/json" } : {}),
      ...options.headers,
    },
  });

  const responseText = await response.text();

  let data = null;

  if (responseText) {
    try {
      data = JSON.parse(responseText);
    } catch {
      data = responseText;
    }
  }

  if (!response.ok) {
    const message =
      data?.message ||
      data?.error ||
      (typeof data === "string" ? data : null) ||
      `Yêu cầu thất bại (${response.status})`;

    throw new Error(message);
  }

  return data;
}

/**
 * Lấy danh sách cảng ACTIVE dành cho Scheduler.
 *
 * GET /api/scheduler/ports
 */
export async function getActiveSchedulerPorts() {
  const data = await request("/scheduler/ports");

  if (Array.isArray(data)) {
    return data;
  }

  if (Array.isArray(data?.content)) {
    return data.content;
  }

  return [];
}

/**
 * Xem trước tuyến đường giữa hai cảng.
 *
 * POST /api/tours/routes/preview
 *
 * @param {Object} departurePort
 * @param {Object} destinationPort
 */
export async function previewSchedulerRoute(departurePort, destinationPort) {
  if (!departurePort?.id || !destinationPort?.id) {
    throw new Error("Vui lòng chọn cảng đi và cảng đến.");
  }

  if (departurePort.id === destinationPort.id) {
    throw new Error("Cảng đi và cảng đến không được trùng nhau.");
  }

  const departureLongitude = Number(departurePort.longitude);
  const departureLatitude = Number(departurePort.latitude);

  const destinationLongitude = Number(destinationPort.longitude);
  const destinationLatitude = Number(destinationPort.latitude);

  const coordinates = [
    departureLongitude,
    departureLatitude,
    destinationLongitude,
    destinationLatitude,
  ];

  if (!coordinates.every(Number.isFinite)) {
    throw new Error("Tọa độ của cảng đi hoặc cảng đến không hợp lệ.");
  }

  return request("/tours/routes/preview", {
    method: "POST",
    body: JSON.stringify({
      departureLongitude,
      departureLatitude,
      destinationLongitude,
      destinationLatitude,
    }),
  });
}

/**
 * Tạo Tour kèm tuyến đường ban đầu.
 *
 * POST /api/scheduler/tours/with-route
 *
 * Backend tự tính lại tuyến đường bằng SeaRoute và lưu cùng Tour.
 *
 * @param {Object} tourData
 * @param {string} departurePortId
 * @param {string} destinationPortId
 */
export async function createTourWithRoute(
  tourData,
  departurePortId,
  destinationPortId,
) {
  if (!departurePortId || !destinationPortId) {
    throw new Error("Vui lòng chọn cảng đi và cảng đến.");
  }

  if (departurePortId === destinationPortId) {
    throw new Error("Cảng đi và cảng đến không được trùng nhau.");
  }

  return request("/scheduler/tours/with-route", {
    method: "POST",
    body: JSON.stringify({
      ...tourData,
      departurePortId,
      destinationPortId,
    }),
  });
}

const schedulerRouteService = {
  getActiveSchedulerPorts,
  previewSchedulerRoute,
  createTourWithRoute,
};

export default schedulerRouteService;
