// src/modules/scheduler/services/vesselTrackingService.js
import api from "../../../api/axios";

const vesselTrackingService = {
  // 1. Lấy danh sách tour Scheduler (Endpoint #4)
  getActiveTours: async (statuses) => {
    const params = statuses ? { statuses: statuses.join(",") } : {};
    const response = await api.get("/scheduler/tours", { params });
    return response.data;
  },

  // 2. Lấy chi tiết tour kèm cảng, lịch trình, stops (Endpoint #5)
  getTourDetails: async (tourId) => {
    const response = await api.get(`/scheduler/tours/${tourId}`);
    return response.data;
  },

  // 3. Lấy riêng tọa độ GPS real-time từ Redis (Endpoint #2)
  getVesselLocation: async (tourId) => {
    const response = await api.get(`/vessel-tracking/tours/${tourId}/location`);
    return response.data;
  },

  // 4. (Dành cho Android app, hoặc web test) Đẩy tọa độ lên Redis (Endpoint #1)
  updateVesselLocation: async (tourId, latitude, longitude) => {
    const response = await api.post(
      `/vessel-tracking/tours/${tourId}/location`,
      null,
      {
        params: { latitude, longitude },
      },
    );
    return response.data;
  },

  // 5. Lấy thông tin thời tiết Open-Meteo (Endpoint #3)
  getWeatherForecast: async (latitude, longitude, startDate, endDate) => {
    const params = { latitude, longitude };
    if (startDate) params.startDate = startDate;
    if (endDate) params.endDate = endDate;

    const response = await api.get("/public/weather", { params });
    return response.data;
  },
};

export default vesselTrackingService;
