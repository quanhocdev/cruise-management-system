// src/modules/profile/services/profileService.js
// ⚠️ Đổi import dưới đây thành axios instance (đã gắn JWT) mà project bạn đang dùng.
import apiClient from "../../../services/apiClient";

const BASE = "/api/profile";

export const getMyProfile = async () => {
  const res = await apiClient.get(`${BASE}/me`);
  return res.data;
};

// PATCH: chỉ gửi các field muốn thay đổi
export const updateMyProfile = async (payload) => {
  const res = await apiClient.patch(`${BASE}/me`, payload);
  return res.data;
};

export const extractErrorMessage = (error, fallback) =>
  error?.response?.data?.message || error?.message || fallback;
