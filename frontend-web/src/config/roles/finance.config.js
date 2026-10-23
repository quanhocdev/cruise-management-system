import {
  LayoutDashboard,
  Ship,
  CalendarDays,
  DoorOpen,
  CreditCard,
  TicketCheck,
  QrCode,
  Banknote,
} from "lucide-react";

export const financeConfig = {
  theme: "finance",
  brand: { icon: Ship, title: "Cruise", subtitle: "Finance" },
  header: {
    title: "Finance Management",
    subtitle: "Quản lý lễ tân - tài chính",
  },
  user: {
    name: "Finance role",
    role: "Quản lý lễ tân - tài chính",
    initials: "FN",
  },
  footerLinks: [
    { label: "Trợ giúp", href: "#help" },
    { label: "Bảo mật", href: "#privacy" },
  ],
  status: { title: "Hệ thống hoạt động", subtitle: "Đã kết nối" },
  menu: [
    {
      title: "QUẢN LÝ LỄ TÂN",
      items: [
        {
          label: "Dashboard",
          icon: LayoutDashboard,
          path: "/finance",
          end: true,
        },
        { label: "Quầy Check-in POS", icon: QrCode, path: "/finance/check-in" },
        { label: "Quầy Thanh toán", icon: Banknote, path: "/finance/checkout" },
        {
          label: "Quản lý Đơn đặt Tour",
          icon: TicketCheck,
          path: "/finance/bookings",
        },
        {
          label: "Quản lý Lịch trình Tour",
          icon: CalendarDays,
          path: "/finance/tours-schedule",
        },
        { label: "Danh sách phòng", icon: DoorOpen, path: "/finance/rooms" },
        {
          label: "Danh sách vòng NFC",
          icon: CreditCard,
          path: "/finance/nfc-cards",
        },
      ],
    },
  ],
};
