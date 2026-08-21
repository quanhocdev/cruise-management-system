import api from "../../../api/axios";

const BASE_URL = "/operation/tours";

const operationTourConfigurationService = {
  /**
   * GET /api/operation/tours/{tourId}/configuration
   */
  getConfiguration: async (tourId) => {
    const response = await api.get(`${BASE_URL}/${tourId}/configuration`);

    // Nếu backend bọc data trong ApiResponse (ví dụ: { data: { activities: [], ... } })
    // Cần lấy response.data.data. Nếu backend trả trực tiếp thì giữ response.data
    return response.data?.data ?? response.data;
  },
};

export default operationTourConfigurationService;
