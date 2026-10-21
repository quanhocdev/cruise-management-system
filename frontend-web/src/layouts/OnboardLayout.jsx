// frontend-web/src/layouts/OnboardLayout.jsx
import ManagementLayout from "../components/management/ManagementLayout";
import { onboardConfig } from "../config/roles/onboard.config";
import { useNotifications } from "../hooks/useNotifications";
import onboardNotificationService from "../modules/onboard/services/onboardNotificationService";

function OnboardLayout() {
  const notificationProps = useNotifications(
    onboardNotificationService,
    "Thông báo Onboard",
  );
  return (
    <ManagementLayout
      config={onboardConfig}
      notificationProps={notificationProps}
    />
  );
}

export default OnboardLayout;
