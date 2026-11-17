// src/modules/passenger/services/passengerTrackingService.js
import api from "../../../api/axios";

const passengerTrackingService = {
  // 1. Lấy danh sách tour mở cho Passenger (Khớp với @RequestMapping("/api/passenger/tours"))
  getActiveTours: async (statuses) => {
    const params = statuses ? { statuses: statuses.join(",") } : {};
    const response = await api.get("/passenger/tours", { params });
    return response.data;
  },

  // 2. Lấy chi tiết tour (Khớp với @GetMapping("/{tourId}") - KHÔNG CÓ /details)
  getTourDetails: async (tourId) => {
    const response = await api.get(`/passenger/tours/${tourId}`);
    return response.data;
  },

  // --- CÁC ENDPOINT DÙNG CHUNG (HELPER) ---

  // 3. Lấy riêng tọa độ GPS real-time từ Redis
  getVesselLocation: async (tourId) => {
    const response = await api.get(`/vessel-tracking/tours/${tourId}/location`);
    return response.data;
  },

  // 4. Lấy tổng quan lộ trình của tour (bảng chính `tour_routes`) để vẽ Mapbox
  getTourRouteOverview: async (tourId) => {
    const response = await api.get(`/tours/routes/tours/${tourId}/overview`);
    return response.data;
  },

  // 5. Lấy danh sách chi tiết các chặng của tour (bảng phụ `tour_route_legs`)
  getTourRouteLegs: async (tourId) => {
    const response = await api.get(`/tours/routes/tours/${tourId}/legs`);
    return response.data;
  },

  // 6. Lấy thông tin thời tiết Open-Meteo
  getWeatherForecast: async (latitude, longitude, startDate, endDate) => {
    const params = { latitude, longitude };
    if (startDate) params.startDate = startDate;
    if (endDate) params.endDate = endDate;

    const response = await api.get("/public/weather", { params });
    return response.data;
  },
};

export default passengerTrackingService;
