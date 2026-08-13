import AdminSidebar from "../components/AdminSidebar";
import AdminHeader from "../components/AdminHeader";
import AdminFooter from "../components/AdminFooter";
import "../styles/AdminLayout.css";

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
