import { LayoutDashboard, Map, Ship } from "lucide-react";

export const schedulerConfig = {
  theme: "scheduler",
  brand: { icon: Ship, title: "Cruise", subtitle: "Scheduler" },
  header: { title: "Scheduler" },
  user: { name: "Scheduler", role: "Tour Scheduler" },
  footerRight: "Scheduler Management",
  status: {
    title: "Tour Management",
    subtitle: "Scheduler",
    hideDot: true,
  },
  menu: [
    {
      items: [
        {
          label: "Dashboard",
          icon: LayoutDashboard,
          path: "/scheduler/dashboard",
        },
        { label: "Quản lý Tour", icon: Map, path: "/scheduler/tours" },
      ],
    },
  ],
};
