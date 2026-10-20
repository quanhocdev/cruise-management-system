// config/roles/shore.config.js
import { LayoutDashboard, ClipboardList, History, Ship } from "lucide-react";

export const shoreConfig = {
  theme: "shore",
  brand: { icon: Ship, title: "Cruise", subtitle: "Shore" },
  header: {
    title: "Shore Management",
    subtitle: "Quản lý tour bờ",
  },
  user: { name: "Shore Manager", role: "Quản lý tour bờ" },
  footerRight: "Shore Management",
  menu: [
    {
      title: null, // đặt tiêu đề section nếu cần
      items: [
        {
          label: "Dashboard",
          icon: LayoutDashboard,
          path: "/shore/dashboard",
          end: true,
        },
        {
          label: "Danh sách Tour",
          icon: ClipboardList,
          path: "/shore/tours",
          extraPaths: ["/shore/visit-tour-configuration"],
        },
        {
          label: "Lịch sử sử dụng",
          icon: History,
          path: "/shore/activity-visit-usages",
        },
      ],
    },
  ],
};
