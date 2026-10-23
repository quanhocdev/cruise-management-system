import ManagementLayout from "../components/management/ManagementLayout";
import { operationConfig } from "../config/roles/operation.config";

// TODO: thay bằng useNotifications(operationNotificationService, ...) khi có socket
const notificationProps = {
  notifications: [],
  unreadCount: 0,
  title: "Thông báo Operation",
};

function OperationLayout() {
  return (
    <ManagementLayout
      config={operationConfig}
      notificationProps={notificationProps}
    />
  );
}

export default OperationLayout;
