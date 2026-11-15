import { useEffect, useRef, useState } from "react";
import { Button, Image, Spinner } from "react-bootstrap";
import "./ProfileAvatar.css";

const API_ORIGIN = new URL(
  import.meta.env.VITE_API_BASE_URL || "http://localhost:8080",
).origin;

function resolveAvatarUrl(profile) {
  if (!profile) return null;

  const value = profile.avatarPublicId;

  if (value?.startsWith("https://") || value?.startsWith("http://")) {
    return value;
  }

  return null;
}

export default function ProfileAvatar({
  profile,
  editable = false,
  avatarLoading = false,
  onUpload,
  onDelete,
}) {
  const fileInputRef = useRef(null);
  const [imageError, setImageError] = useState(false);

  const avatarUrl = resolveAvatarUrl(profile);

  useEffect(() => {
    setImageError(false);
  }, [avatarUrl]);

  const handleFileChange = async (event) => {
    const file = event.target.files?.[0];

    if (!file) return;

    if (!file.type.startsWith("image/")) {
      event.target.value = "";
      return;
    }

    await onUpload(file);
    event.target.value = "";
  };

  const showImage = avatarUrl && !imageError;

  return (
    <div className="profile-avatar">
      <div className="profile-avatar__image">
        {showImage ? (
          <Image
            src={avatarUrl}
            alt="Ảnh đại diện"
            className="profile-avatar__photo"
            onError={() => setImageError(true)}
          />
        ) : (
          <div className="profile-avatar__placeholder">
            <i className="bi bi-person-fill" />
          </div>
        )}
      </div>

      <div className="profile-avatar__content">
        <div className="profile-avatar__title">Ảnh đại diện</div>

        <div className="profile-avatar__description">
          {showImage
            ? "Ảnh đại diện hiện tại của bạn"
            : "Bạn chưa có ảnh đại diện"}
        </div>

        {editable && (
          <>
            <input
              ref={fileInputRef}
              type="file"
              accept="image/jpeg,image/png,image/webp"
              hidden
              onChange={handleFileChange}
              disabled={avatarLoading}
            />

            <div className="profile-avatar__actions">
              <Button
                variant="primary"
                size="sm"
                disabled={avatarLoading || !profile}
                onClick={() => fileInputRef.current?.click()}
              >
                {avatarLoading ? (
                  <>
                    <Spinner size="sm" className="me-2" />
                    Đang xử lý...
                  </>
                ) : (
                  <>
                    <i className="bi bi-camera me-2" />
                    {avatarUrl ? "Đổi ảnh" : "Tải ảnh lên"}
                  </>
                )}
              </Button>

              {profile?.avatarPublicId && (
                <Button
                  variant="outline-danger"
                  size="sm"
                  disabled={avatarLoading}
                  onClick={onDelete}
                >
                  <i className="bi bi-trash me-1" />
                  Xóa ảnh
                </Button>
              )}
            </div>
          </>
        )}
      </div>
    </div>
  );
}
