import ManagementLayout from "../components/management/ManagementLayout";
import { adminConfig } from "../config/roles/admin.config";
import { useAdminNotifications } from "../hooks/useAdminNotifications";

function AdminLayout({ children }) {
  const notificationProps = useAdminNotifications();
  return (
    <ManagementLayout
      config={adminConfig}
      notificationProps={notificationProps}
    >
      {children}
    </ManagementLayout>
  );
}

export default AdminLayout;
