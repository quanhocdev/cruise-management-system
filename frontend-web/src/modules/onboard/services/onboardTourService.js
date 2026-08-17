// src/modules/onboard/services/onboardTourService.js
import api from "../../../api/axios";

const API_URL = "/onboard/tours";

export const fetchApprovedTours = async () => {
  // Đổi axios.get thành api.get
  const response = await api.get(`${API_URL}/approved`);
  return response.data;
};
