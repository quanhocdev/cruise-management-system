import {
  LayoutDashboard,
  ConciergeBell,
  Package,
  ClipboardList,
  Sparkles,
  History,
  Wrench,
} from "lucide-react";

export const convenienceConfig = {
  theme: "convenience",
  brand: { icon: Sparkles, title: "Cruise", subtitle: "Convenience" },
  header: { title: "Tiện ích & Dịch vụ" },
  user: { name: "Convenience", role: "Service Manager" },
  footerRight: "Convenience Management",
  status: {
    title: "Convenience Portal",
    subtitle: "Service & Product",
    hideDot: true,
  },
  menu: [
    {
      items: [
        {
          label: "Dashboard",
          icon: LayoutDashboard,
          path: "/convenience/dashboard",
        },
        {
          label: "Quản lý Dịch vụ",
          icon: ConciergeBell,
          path: "/convenience/services",
        },
        {
          label: "Quản lý Sản phẩm",
          icon: Package,
          path: "/convenience/products",
        },
        {
          label: "Cấu hình Tour",
          icon: ClipboardList,
          path: "/convenience/tour-config",
        },
        {
          label: "Lịch sử sản phẩm",
          icon: History,
          path: "/convenience/product-usage-history",
        },
        {
          label: "Lịch sử dịch vụ",
          icon: Wrench,
          path: "/convenience/service-usage-history",
        },
      ],
    },
  ],
};
