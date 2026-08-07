// src/routes/roleRoutes.js
import { UserRole } from "../constants/roles";

export const ROLE_HOME_PATH = {
  ADMIN: "/admin/dashboard",
  SCHEDULER: "/scheduler/schedules",
  SHORE: "/shore/excursions",
  ONBOARD: "/onboard/cabins",
  CONVENIENCE: "/convenience/services",
  FINANCE: "/finance/reports",
  OPERATION: "/operation/status",
  PASSENGER: "/passenger/dashboard",
  GUEST: "/guest/explore",
};

// Hàm hỗ trợ điều hướng đúng trang Home theo Role sau khi Đăng nhập
export const getRedirectPathByRole = (role) => {
  return ROLE_HOME_PATH[role] || "/login";
};
