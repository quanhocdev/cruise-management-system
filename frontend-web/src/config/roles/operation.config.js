import {
  LayoutDashboard,
  Ship,
  ClipboardCheck,
  CalendarDays,
  Ticket,
  Package,
  Settings,
  SlidersHorizontal,
} from "lucide-react";

export const operationConfig = {
  theme: "operation",
  brand: { icon: Ship, title: "Cruise", subtitle: "Operation" },
  header: { title: "Operation Management", subtitle: "Quản lý vận hành" },
  user: { name: "Operation", role: "Quản lý vận hành" },
  footerRight: "Operation Management",
  status: { title: "Hệ thống hoạt động", subtitle: "Đã kết nối" },
  menu: [
    {
      title: "VẬN HÀNH",
      items: [
        {
          label: "Dashboard",
          icon: LayoutDashboard,
          path: "/operation",
          end: true,
        },
        {
          label: "Quản lý tour",
          icon: ClipboardCheck,
          path: "/operation/tours",
        },
        {
          label: "Cấu hình Tour",
          icon: SlidersHorizontal,
          path: "/operation/tour-configuration",
        },
        {
          label: "Lịch trình",
          icon: CalendarDays,
          path: "/operation/schedules",
        },
        { label: "Du thuyền", icon: Ship, path: "/operation/cruises" },
        { label: "Booking", icon: Ticket, path: "/operation/bookings" },
        {
          label: "Sản phẩm / Dịch vụ",
          icon: Package,
          path: "/operation/products",
        },
      ],
    },
    {
      title: "HỆ THỐNG",
      items: [
        { label: "Cài đặt", icon: Settings, path: "/operation/settings" },
      ],
    },
  ],
};
