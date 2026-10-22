import ManagementLayout from "../components/management/ManagementLayout";
import { convenienceConfig } from "../config/roles/convenience.config";
import { useNotifications } from "../hooks/useNotifications";
import convenienceNotificationService from "../modules/convenience/services/convenienceNotificationService";

function ConvenienceLayout() {
  const notificationProps = useNotifications(
    convenienceNotificationService,
    "Thông báo Convenience",
  );
  return (
    <ManagementLayout
      config={convenienceConfig}
      notificationProps={notificationProps}
    />
  );
}

export default ConvenienceLayout;
