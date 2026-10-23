// src/ layouts/SchedulerLayout.jsx
import ManagementLayout from "../components/management/ManagementLayout";
import { schedulerConfig } from "../config/roles/scheduler.config";

// TODO: thay bằng useNotifications(schedulerNotificationService, ...) khi có socket
const notificationProps = {
  notifications: [],
  unreadCount: 0,
  title: "Thông báo Scheduler",
};

function SchedulerLayout({ children }) {
  return (
    <ManagementLayout
      config={schedulerConfig}
      notificationProps={notificationProps}
    >
      {children}
    </ManagementLayout>
  );
}

export default SchedulerLayout;