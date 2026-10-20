// layouts/ShoreLayout.jsx
import ManagementLayout from "../components/management/ManagementLayout";
import { shoreConfig } from "../config/roles/shore.config";
import { useShoreNotifications } from "../hooks/useShoreNotifications";

function ShoreLayout() {
  const notificationProps = useShoreNotifications();
  return (
    <ManagementLayout
      config={shoreConfig}
      notificationProps={notificationProps}
    />
  );
}

export default ShoreLayout;
