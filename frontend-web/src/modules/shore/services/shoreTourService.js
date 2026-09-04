// src/modules/shore/services/shoreTourService.js

import api from "../../../api/axios";

const BASE_URL = "/shore/tours";

const shoreTourService = {
  /**
   * GET /api/shore/tours
   *
   * Lấy danh sách Tour mà Shore được phép quản lý.
   *
   * Backend chỉ trả:
   * APPROVED
   * READY
   * IN_PROGRESS
   * COMPLETED
   */
  getAvailableTours: async () => {
    const response = await api.get(BASE_URL);

    return response.data;
  },

  /**
   * GET /api/shore/tours/{tourId}/configuration
   *
   * Lấy toàn bộ cấu hình Shore của một Tour.
   *
   * Có thể truyền status để lọc VisitTour:
   * NOT_STARTED
   * IN_PROGRESS
   * COMPLETED
   * DELAYED
   * CANCELLED
   */
  getConfiguration: async (tourId, status = null) => {
    const response = await api.get(`${BASE_URL}/${tourId}/configuration`, {
      params: status ? { status } : {},
    });

    return response.data;
  },
};

export default shoreTourService;
