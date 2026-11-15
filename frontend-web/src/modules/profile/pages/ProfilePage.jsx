// src/modules/profile/pages/ProfilePage.jsx

import { Alert, Button, Spinner } from "react-bootstrap";
import { useAuth } from "../../../context/AuthContext";
import useProfile from "../hooks/useProfile";
import useProfileForm from "../hooks/useProfileForm";
import ProfileSummaryCard from "../components/ProfileSummaryCard";
import ProfileForm from "../components/ProfileForm";
import "../styles/ProfilePage.css";

export default function ProfilePage() {
  const { user } = useAuth();
  const isPassenger = user?.role === "PASSENGER";

  const {
    profile,
    setProfile,
    loading,
    error: loadError,
    reload,
  } = useProfile();

  const formState = useProfileForm(profile, setProfile);

  if (loading) {
    return (
      <div className="pf-state">
        <Spinner animation="border" size="sm" /> Đang tải thông tin tài khoản...
      </div>
    );
  }

  if (loadError) {
    return (
      <div className="pf-page">
        <Alert variant="danger">
          <div className="d-flex justify-content-between align-items-center">
            <span>{loadError}</span>
            <Button size="sm" variant="danger" onClick={reload}>
              Thử lại
            </Button>
          </div>
        </Alert>
      </div>
    );
  }

  return (
    <div className="pf-page">
      <h1 className="pf-title">Thông tin tài khoản</h1>

      {formState.error && (
        <Alert
          variant="danger"
          dismissible
          onClose={() => formState.setError("")}
        >
          {formState.error}
        </Alert>
      )}

      {formState.success && (
        <Alert
          variant="success"
          dismissible
          onClose={() => formState.setSuccess("")}
        >
          {formState.success}
        </Alert>
      )}

      {!profile && (
        <Alert variant="info">
          Bạn chưa tạo hồ sơ cá nhân. Vui lòng nhập thông tin bên dưới và nhấn
          Lưu hồ sơ.
        </Alert>
      )}

      <div className="pf-layout">
        <aside className="pf-card">
          <ProfileSummaryCard profile={profile} user={user} />
        </aside>

        <section className="pf-card">
          <ProfileForm formState={formState} readOnly={!isPassenger} />
        </section>
      </div>
    </div>
  );
}
