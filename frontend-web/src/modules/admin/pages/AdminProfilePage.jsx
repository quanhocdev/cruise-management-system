import { useNavigate, useParams } from "react-router-dom";
import { Alert, Container, Spinner } from "react-bootstrap";

import useAdminProfile from "../hooks/useAdminProfile";

import AdminProfileHeader from "../components/profile/AdminProfileHeader";
import AdminProfileAvatar from "../components/profile/AdminProfileAvatar";
import AdminProfileForm from "../components/profile/AdminProfileForm";

import "../styles/AdminProfilePage.css";

export default function AdminProfilePage() {
  const { userId } = useParams();
  const navigate = useNavigate();

  const {
    profile,
    hasProfile,
    form,
    loading,
    saving,
    deleting,
    avatarLoading,
    avatarFile,
    setAvatarFile,
    error,
    success,
    setError,
    setSuccess,
    handleChange,
    handleSubmit,
    handleDeleteProfile,
    handleUploadAvatar,
    handleDeleteAvatar,
  } = useAdminProfile(userId);

  if (loading) {
    return (
      <Container className="admin-profile-loading">
        <Spinner animation="border" />
        <div className="mt-2">Đang tải hồ sơ...</div>
      </Container>
    );
  }

  return (
    <Container fluid className="admin-profile-page py-4">
      <AdminProfileHeader
        userId={userId}
        onBack={() => navigate("/admin/accounts")}
      />

      {error && (
        <Alert variant="danger" dismissible onClose={() => setError("")}>
          {error}
        </Alert>
      )}

      {success && (
        <Alert variant="success" dismissible onClose={() => setSuccess("")}>
          {success}
        </Alert>
      )}

      {!hasProfile && (
        <Alert variant="info">
          Tài khoản này chưa có hồ sơ. Bạn có thể nhập thông tin bên dưới để tạo
          hồ sơ.
        </Alert>
      )}

      <AdminProfileAvatar
        avatarUrl={profile?.avatarUrl}
        avatarFile={avatarFile}
        hasProfile={hasProfile}
        loading={avatarLoading}
        onFileChange={setAvatarFile}
        onUpload={handleUploadAvatar}
        onDelete={handleDeleteAvatar}
      />

      <AdminProfileForm
        form={form}
        hasProfile={hasProfile}
        saving={saving}
        deleting={deleting}
        onChange={handleChange}
        onSubmit={handleSubmit}
        onDelete={handleDeleteProfile}
      />
    </Container>
  );
}
