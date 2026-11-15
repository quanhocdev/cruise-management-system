// src/modules/profile/services/profileService.js

const API_BASE_URL =
  (import.meta.env.VITE_API_BASE_URL || "http://localhost:8080").replace(
    /\/+$/,
    "",
  ) + "/api";

async function request(path, options = {}) {
  const isFormData = options.body instanceof FormData;

  const response = await fetch(`${API_BASE_URL}${path}`, {
    credentials: "include",
    ...options,
    headers: {
      ...(options.body && !isFormData
        ? { "Content-Type": "application/json" }
        : {}),
      ...options.headers,
    },
  });

  const responseText = await response.text();
  let data = null;

  if (responseText) {
    try {
      data = JSON.parse(responseText);
    } catch {
      data = responseText;
    }
  }

  if (!response.ok) {
    const message =
      data?.message ||
      data?.error ||
      (typeof data === "string" ? data : null) ||
      `Yêu cầu thất bại (${response.status})`;

    const error = new Error(message);
    error.status = response.status;
    throw error;
  }

  return data;
}

// GET /api/passenger/profiles/me
// Backend cần bổ sung endpoint GET này.
export async function getMyProfile() {
  return request("/profile/me");
}

// POST /api/passenger/profiles
export async function createMyProfile(requestData) {
  return request("/passenger/profiles", {
    method: "POST",
    body: JSON.stringify(requestData),
  });
}

// PATCH /api/passenger/profiles
export async function updateMyProfile(requestData) {
  return request("/passenger/profiles", {
    method: "PATCH",
    body: JSON.stringify(requestData),
  });
}

// POST /api/passenger/profiles/avatar
export async function uploadMyAvatar(file) {
  const formData = new FormData();
  formData.append("avatar", file);

  return request("/passenger/profiles/avatar", {
    method: "POST",
    body: formData,
  });
}

// DELETE /api/passenger/profiles/avatar
export async function deleteMyAvatar() {
  return request("/passenger/profiles/avatar", {
    method: "DELETE",
  });
}

export const extractErrorMessage = (error, fallback) =>
  error?.message || fallback;

const profileService = {
  getMyProfile,
  createMyProfile,
  updateMyProfile,
  uploadMyAvatar,
  deleteMyAvatar,
};

export default profileService;
