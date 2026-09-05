// src/modules/operation/services/operationTourService.js

import api from "../../../api/axios";

const OPERATION_TOUR_BASE_URL = "/operation/tours";

const operationTourService = {
  getPendingTours: async () => {
    const response = await api.get(`${OPERATION_TOUR_BASE_URL}/pending`);
    return response.data;
  },

  getApprovedTours: async () => {
    const response = await api.get(`${OPERATION_TOUR_BASE_URL}/approved`);
    return response.data;
  },

  getReadyTours: async () => {
    try {
      const response = await api.get(`${OPERATION_TOUR_BASE_URL}/ready`);
      return response.data;
    } catch (err) {
      console.warn("Backend chưa có endpoint /ready, trả về mảng rỗng.");
      return [];
    }
  },

  approveTour: async (tourId, payload = null) => {
    const response = await api.post(
      `${OPERATION_TOUR_BASE_URL}/${tourId}/approve`,
      payload,
    );
    return response.data;
  },
};

export default operationTourService;
