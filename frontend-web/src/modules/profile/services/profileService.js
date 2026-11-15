// src/modules/profile/services/profileService.js

const API_BASE_URL = `${(
  import.meta.env.VITE_API_BASE_URL || "http://localhost:8080"
).replace(/\/$/, "")}/api`;

async function request(path, options = {}) {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    credentials: "include",
    ...options,
    headers: {
      ...(options.body ? { "Content-Type": "application/json" } : {}),
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

    throw new Error(message);
  }

  return data;
}

/**
 * Lấy hồ sơ của người đang đăng nhập.
 *
 * GET /api/profile/me
 */
export async function getMyProfile() {
  return request("/profile/me");
}

// request() đã ném Error(message) nên chỉ cần lấy message
export const extractErrorMessage = (error, fallback) =>
  error?.message || fallback;

const profileService = {
  getMyProfile,
  updateMyProfile,
};

export default profileService;
