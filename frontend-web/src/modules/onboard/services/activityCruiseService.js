import api from "../../../api/axios";

// Đổi theo đúng URL backend: /api/onboard/activities-cruise
const API_URL = "/onboard/activities-cruise";

// Helper đóng gói FormData hỗ trợ Upload File
const buildFormData = (data) => {
  const formData = new FormData();
  Object.keys(data).forEach((key) => {
    const value = data[key];
    if (value !== undefined && value !== null) {
      if (value instanceof File) {
        formData.append(key, value);
      } else {
        formData.append(key, String(value));
      }
    }
  });
  return formData;
};

export const activityCruiseService = {
  getAll: async () => {
    const response = await api.get(API_URL);
    return response.data;
  },

  getActive: async () => {
    const response = await api.get(`${API_URL}/active`);
    return response.data;
  },

  getById: async (id) => {
    const response = await api.get(`${API_URL}/${id}`);
    return response.data;
  },

  create: async (data) => {
    // Nếu data truyền vào chứa file ảnh (image), đóng gói thành FormData
    const formData = data instanceof FormData ? data : buildFormData(data);
    const response = await api.post(API_URL, formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });
    return response.data;
  },

  update: async (id, data) => {
    // Chuyển sang PATCH theo đúng Controller ở Backend
    const formData = data instanceof FormData ? data : buildFormData(data);
    const response = await api.patch(`${API_URL}/${id}`, formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });
    return response.data;
  },

  delete: async (id) => {
    const response = await api.delete(`${API_URL}/${id}`);
    return response.data;
  },
};
