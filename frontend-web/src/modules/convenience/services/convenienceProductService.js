import api from "../../../api/axios";

const convenienceProductService = {
  getAllProducts: async (params) => {
    console.log("🟢 [SERVICE TEST] GỌI API PRODUCT");
    const response = await api.get("/convenience/products", { params });
    return response.data;
  },

  getProductById: async (id) => {
    const response = await api.get(`/convenience/products/${id}`);
    return response.data;
  },
};

export default convenienceProductService;
