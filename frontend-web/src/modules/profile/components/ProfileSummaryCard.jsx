// src/modules/profile/components/ProfileSummaryCard.jsx
import { Badge } from "react-bootstrap";

const initials = (name = "") =>
  name
    .trim()
    .split(/\s+/)
    .slice(-2)
    .map((w) => w[0]?.toUpperCase())
    .join("") || "?";

export default function ProfileSummaryCard({ profile, user }) {
  const displayName = profile?.fullName || user?.username;

  return (
    <div className="pf-summary">
      {profile?.avatarUrl ? (
        <img src={profile.avatarUrl} alt="" className="pf-avatar" />
      ) : (
        <div className="pf-avatar pf-avatar-fallback">
          {initials(displayName)}
        </div>
      )}
      <h2>{displayName}</h2>
      <p className="pf-username">@{user?.username}</p>
      <Badge bg="primary">{user?.role}</Badge>
      {profile?.updatedAt && (
        <p className="pf-updated">
          Cập nhật lần cuối:{" "}
          {new Date(profile.updatedAt).toLocaleString("vi-VN")}
        </p>
      )}
    </div>
  );
}
