// src/components/ProtectedRoute.jsx
import { Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function ProtectedRoute({ children, allowedRoles }) {
  // 1. Lấy thêm loading từ AuthContext
  const { user, loading } = useAuth();

  // 2. Nếu đang trong quá trình gọi API /auth/me để kiểm tra Cookie -> Giữ nguyên trạng thái (chờ)
  if (loading) {
    return (
      <div style={{ padding: "20px", textAlign: "center" }}>
        Đang kiểm tra phiên đăng nhập...
      </div>
    );
  }

  // 3. Đã kiểm tra xong (loading = false) mà thực sự KHÔNG có user -> Mới văng về /login
  if (!user) {
    return <Navigate to="/login" replace />;
  }

  // 4. Nếu role không hợp lệ
  if (allowedRoles && !allowedRoles.includes(user.role)) {
    return <h2>403 - Bạn không có quyền truy cập trang này!</h2>;
  }

  return children;
}
