import api from "../../../api/axios";

const TOUR_BASE_URL = "/scheduler/tours";

const tourService = {
  /**
   * Lấy tất cả Tour
   */
  getAllTours: async () => {
    const response = await api.get(TOUR_BASE_URL);

    return response.data;
  },

  /**
   * Lấy Tour theo ID
   */
  getTourById: async (id) => {
    const response = await api.get(`${TOUR_BASE_URL}/${id}`);

    return response.data;
  },

  /**
   * Lấy Tour theo code
   */
  getTourByCode: async (code) => {
    const response = await api.get(
      `${TOUR_BASE_URL}/code/${encodeURIComponent(code)}`,
    );

    return response.data;
  },

  /**
   * Tạo Tour
   *
   * Scheduler KHÔNG chọn Cruise.
   *
   * Request:
   * {
   *   code,
   *   name,
   *   description,
   *   dayStart,
   *   dayEnd
   * }
   */
  createTour: async (data) => {
    const response = await api.post(TOUR_BASE_URL, data);

    return response.data;
  },

  /**
   * Cập nhật Tour
   */
  updateTour: async (id, data) => {
    const response = await api.patch(`${TOUR_BASE_URL}/${id}`, data);

    return response.data;
  },

  /**
   * Xóa Tour
   */
  deleteTour: async (id) => {
    await api.delete(`${TOUR_BASE_URL}/${id}`);

    return true;
  },
};

export default tourService;
