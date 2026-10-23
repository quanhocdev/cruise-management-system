import {
  LayoutDashboard,
  Users,
  Ship,
  Anchor,
  Package,
  ConciergeBell,
  FileText,
  MonitorSmartphone,
  CreditCard,
} from "lucide-react";

export const adminConfig = {
  theme: "admin",
  brand: { icon: Ship, title: "Cruise", subtitle: "Admin" },
  header: { title: "Cruise Admin", titleFromRoute: true },
  user: { name: "Admin", role: "Administrator" },
  footerRight: "Administration",
  menu: [
    {
      items: [
        { label: "Dashboard", icon: LayoutDashboard, path: "/admin/dashboard" },
        { label: "Quản lý tài khoản", icon: Users, path: "/admin/accounts" },
        { label: "Quản lý du thuyền", icon: Ship, path: "/admin/cruises" },
        { label: "Quản lý cảng", icon: Anchor, path: "/admin/ports" },
        {
          label: "Quản lý thẻ NFC",
          icon: CreditCard,
          path: "/admin/nfc-cards",
        },
        { label: "Quản lý sản phẩm", icon: Package, path: "/admin/products" },
        {
          label: "Quản lý dịch vụ",
          icon: ConciergeBell,
          path: "/admin/services",
        },
        {
          label: "Quản lý máy POS",
          icon: MonitorSmartphone,
          path: "/admin/pos-terminals",
        },
        {
          label: "Quản lý chính sách",
          icon: FileText,
          path: "/admin/policies",
        },
      ],
    },
  ],
};
