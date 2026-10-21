// layouts/ShoreLayout.jsx
import ManagementLayout from "../components/management/ManagementLayout";
import { shoreConfig } from "../config/roles/shore.config";
import { useNotifications } from "../hooks/useNotifications";
import shoreNotificationService from "../modules/shore/services/shoreNotificationService";

function ShoreLayout() {
  const notificationProps = useNotifications(
    shoreNotificationService,
    "Thông báo Shore",
  );
  return (
    <ManagementLayout
      config={shoreConfig}
      notificationProps={notificationProps}
    />
  );
}

export default ShoreLayout;
