import api from "../../../api/axios";

const TOUR_BASE_URL = "/scheduler/tours";

const tourService = {
  /**
   * =====================================================
   * LẤY DANH SÁCH TOUR
   * =====================================================
   *
   * Có thể truyền:
   *
   * getAllTours()
   * -> GET /scheduler/tours
   *
   * getAllTours("DRAFT")
   * -> GET /scheduler/tours?statusTrip=DRAFT
   */
  getAllTours: async (statusTrip = null) => {
    const response = await api.get(TOUR_BASE_URL, {
      params: statusTrip ? { statusTrip } : {},
    });

    return response.data;
  },

  /**
   * =====================================================
   * LẤY TOUR THEO ID
   * =====================================================
   */

  getTourById: async (id) => {
    const response = await api.get(`${TOUR_BASE_URL}/${id}`);

    return response.data;
  },

  /**
   * =====================================================
   * LẤY TOUR THEO CODE
   * =====================================================
   */

  getTourByCode: async (code) => {
    const response = await api.get(
      `${TOUR_BASE_URL}/code/${encodeURIComponent(code)}`,
    );

    return response.data;
  },

  /**
   * =====================================================
   * TẠO TOUR
   * =====================================================
   *
   * Scheduler KHÔNG chọn Cruise.
   *
   * Request:
   * {
   *   code,
   *   name,
   *   description,
   *   startDate,
   *   endDate
   * }
   */

  createTour: async (data) => {
    const response = await api.post(TOUR_BASE_URL, data);

    return response.data;
  },

  /**
   * =====================================================
   * CẬP NHẬT TOUR
   * =====================================================
   */

  updateTour: async (id, data) => {
    const response = await api.patch(`${TOUR_BASE_URL}/${id}`, data);

    return response.data;
  },

  /**
   * =====================================================
   * XÓA TOUR
   * =====================================================
   */

  deleteTour: async (id) => {
    await api.delete(`${TOUR_BASE_URL}/${id}`);

    return true;
  },
};

export default tourService;
