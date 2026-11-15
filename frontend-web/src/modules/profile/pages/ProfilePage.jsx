// src/modules/profile/pages/ProfilePage.jsx
// Trang dùng chung cho mọi role: đặt route "profile" bên trong layout của từng role.
import { Alert, Button, Spinner } from "react-bootstrap";
import { useAuth } from "../../../context/AuthContext";
import useProfile from "../hooks/useProfile";
import useProfileForm from "../hooks/useProfileForm";
import ProfileSummaryCard from "../components/ProfileSummaryCard";
import ProfileForm from "../components/ProfileForm";
import "../styles/ProfilePage.css";

export default function ProfilePage() {
  const { user } = useAuth();
  const { profile, setProfile, loading, error, reload } = useProfile();
  const formState = useProfileForm(profile, setProfile);

  if (loading) {
    return (
      <div className="pf-state">
        <Spinner animation="border" size="sm" /> Đang tải thông tin tài khoản...
      </div>
    );
  }

  if (error) {
    return (
      <div className="pf-page">
        <Alert
          variant="danger"
          className="d-flex justify-content-between align-items-center"
        >
          <span>{error}</span>
          <Button size="sm" variant="danger" onClick={reload}>
            Thử lại
          </Button>
        </Alert>
      </div>
    );
  }

  return (
    <div className="pf-page">
      <h1 className="pf-title">Thông tin tài khoản</h1>
      <div className="pf-layout">
        <aside className="pf-card">
          <ProfileSummaryCard profile={profile} user={user} />
        </aside>
        <section className="pf-card">
          <ProfileForm formState={formState} />
        </section>
      </div>
    </div>
  );
}
