// frontend-web/src/layouts/FinanceLayout.jsx
import ManagementLayout from "../components/management/ManagementLayout";
import { financeConfig } from "../config/roles/finance.config";

// TODO: thay bằng useNotifications(financeNotificationService, ...) khi có socket
const notificationProps = {
  notifications: [],
  unreadCount: 0,
  title: "Thông báo Finance",
};

function FinanceLayout() {
  return (
    <ManagementLayout
      config={financeConfig}
      notificationProps={notificationProps}
    />
  );
}

export default FinanceLayout;
