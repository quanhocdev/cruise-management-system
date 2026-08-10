// src/routes/roleRoutes.js
import { UserRole } from "../constants/roles";

export const ROLE_HOME_PATH = {
  ADMIN: "/admin/dashboard",
  SCHEDULER: "/scheduler/dashboard",
  SHORE: "/shore/dashboard",
  ONBOARD: "/onboard/dashboard",
  CONVENIENCE: "/convenience/dashboard",
  FINANCE: "/finance/dashboard",
  OPERATION: "/operation/dashboard",
  PASSENGER: "/passenger/dashboard",
  GUEST: "/login",
};

// Hàm hỗ trợ điều hướng đúng trang Home theo Role sau khi Đăng nhập
export const getRedirectPathByRole = (role) => {
  return ROLE_HOME_PATH[role] || "/login";
};
