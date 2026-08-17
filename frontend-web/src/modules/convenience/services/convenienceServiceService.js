import api from "../../../api/axios";

const CONVENIENCE_BASE_URL = "/convenience/services";

const convenienceService = {
  getAllServices: async (params) => {
    console.log("🟢 [SERVICE TEST] GỌI API SERVICE");
    const response = await api.get(CONVENIENCE_BASE_URL, { params });
    return response.data;
  },

  getServiceById: async (id) => {
    const response = await api.get(`${CONVENIENCE_BASE_URL}/${id}`);
    return response.data;
  },
};

export default convenienceService;
