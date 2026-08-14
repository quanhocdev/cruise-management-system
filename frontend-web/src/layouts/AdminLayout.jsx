import AdminSidebar from "../components/admin/AdminSidebar";
import AdminHeader from "../components/admin/AdminHeader";
import AdminFooter from "../components/admin/AdminFooter";
import "./AdminLayout.css";

function AdminLayout({ children }) {
  return (
    <div className="admin-layout">
      <AdminSidebar />

      <div className="admin-layout-main">
        <AdminHeader />

        <main className="admin-layout-content">{children}</main>

        <AdminFooter />
      </div>
    </div>
  );
}

export default AdminLayout;
