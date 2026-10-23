import {
  LayoutDashboard,
  Ship,
  CalendarDays,
  Ticket,
  Settings,
  Activity,
  ListChecks,
  History,
} from "lucide-react";

export const onboardConfig = {
  theme: "onboard",
  brand: { icon: Ship, title: "Cruise", subtitle: "Onboard" },
  header: { title: "Onboard Management", subtitle: "Quản lý trên tàu" },
  user: { name: "Onboard Admin", role: "Quản lý vận hành", initials: "OB" },
  footerLinks: [
    { label: "Trợ giúp", href: "#help" },
    { label: "Bảo mật", href: "#privacy" },
  ],
  status: { title: "Hệ thống hoạt động", subtitle: "Đã kết nối" },
  menu: [
    {
      title: "QUẢN LÝ TRÊN TÀU",
      items: [
        {
          label: "Dashboard",
          icon: LayoutDashboard,
          path: "/onboard",
          end: true,
        },
        {
          label: "Danh mục hoạt động",
          icon: ListChecks,
          path: "/onboard/activities-catalog",
        },
        {
          label: "Cấu hình hoạt động",
          icon: Activity,
          path: "/onboard/activity-cruise",
        },
        {
          label: "Lịch sử sử dụng",
          icon: History,
          path: "/onboard/activity-cruise-usages",
        },
        { label: "Lịch trình", icon: CalendarDays, path: "/onboard/schedules" },
        { label: "Du thuyền", icon: Ship, path: "/onboard/cruises" },
        { label: "Booking", icon: Ticket, path: "/onboard/bookings" },
        { label: "Cài đặt", icon: Settings, path: "/onboard/settings" },
      ],
    },
  ],
};
