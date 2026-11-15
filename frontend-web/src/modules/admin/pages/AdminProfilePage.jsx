import { useCallback, useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { Alert, Container, Spinner } from "react-bootstrap";

import adminProfileService from "../services/adminProfileService";

import AdminProfileHeader from "../components/profile/AdminProfileHeader";
import AdminProfileAvatar from "../components/profile/AdminProfileAvatar";
import AdminProfileForm from "../components/profile/AdminProfileForm";

import "../styles/AdminProfilePage.css";

const EMPTY_FORM = {
  fullName: "",
  dateOfBirth: "",
  phone: "",
  gender: "",
  address: "",
  nationality: "",
  identityNumber: "",
};

function mapProfileToForm(profile) {
  return {
    fullName: profile?.fullName || "",
    dateOfBirth: profile?.dateOfBirth || "",
    phone: profile?.phone || "",
    gender: profile?.gender || "",
    address: profile?.address || "",
    nationality: profile?.nationality || "",
    identityNumber: profile?.identityNumber || "",
  };
}

export default function AdminProfilePage() {
  const { userId } = useParams();
  const navigate = useNavigate();

  const [profile, setProfile] = useState(null);
  const [hasProfile, setHasProfile] = useState(false);
  const [form, setForm] = useState(EMPTY_FORM);

  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [deleting, setDeleting] = useState(false);
  const [avatarLoading, setAvatarLoading] = useState(false);

  const [avatarFile, setAvatarFile] = useState(null);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const loadProfile = useCallback(async () => {
    try {
      setLoading(true);
      setError("");

      const response = await adminProfileService.getByUserId(userId);
      const data = response.data;

      setProfile(data);
      setHasProfile(true);
      setForm(mapProfileToForm(data));
    } catch (err) {
      if (err.response?.status === 404) {
        setProfile(null);
        setHasProfile(false);
        setForm(EMPTY_FORM);
      } else {
        setError(
          err.response?.data?.message || "Không thể tải thông tin hồ sơ.",
        );
      }
    } finally {
      setLoading(false);
    }
  }, [userId]);

  useEffect(() => {
    loadProfile();
  }, [loadProfile]);

  const handleChange = (event) => {
    const { name, value } = event.target;

    setForm((previous) => ({
      ...previous,
      [name]: value,
    }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    try {
      setSaving(true);
      setError("");
      setSuccess("");

      const requestData = {
        fullName: form.fullName.trim(),
        dateOfBirth: form.dateOfBirth || null,
        phone: form.phone.trim(),
        gender: form.gender || null,
        address: form.address.trim(),
        nationality: form.nationality.trim(),
        identityNumber: form.identityNumber.trim(),
      };

      const response = hasProfile
        ? await adminProfileService.update(userId, requestData)
        : await adminProfileService.create(userId, requestData);

      setProfile(response.data);
      setHasProfile(true);
      setForm(mapProfileToForm(response.data));

      setSuccess(
        hasProfile ? "Cập nhật hồ sơ thành công." : "Tạo hồ sơ thành công.",
      );
    } catch (err) {
      setError(err.response?.data?.message || "Không thể lưu thông tin hồ sơ.");
    } finally {
      setSaving(false);
    }
  };

  const handleDeleteProfile = async () => {
    const confirmed = window.confirm(
      "Bạn có chắc muốn xóa toàn bộ hồ sơ của tài khoản này không?",
    );

    if (!confirmed) return;

    try {
      setDeleting(true);
      setError("");
      setSuccess("");

      await adminProfileService.delete(userId);

      setProfile(null);
      setHasProfile(false);
      setForm(EMPTY_FORM);
      setAvatarFile(null);

      setSuccess("Đã xóa hồ sơ tài khoản.");
    } catch (err) {
      setError(err.response?.data?.message || "Không thể xóa hồ sơ tài khoản.");
    } finally {
      setDeleting(false);
    }
  };

  const handleUploadAvatar = async () => {
    if (!avatarFile) {
      setError("Vui lòng chọn ảnh đại diện.");
      return;
    }

    try {
      setAvatarLoading(true);
      setError("");
      setSuccess("");

      await adminProfileService.uploadAvatar(userId, avatarFile);

      setAvatarFile(null);
      await loadProfile();

      setSuccess("Tải ảnh đại diện thành công.");
    } catch (err) {
      setError(err.response?.data?.message || "Không thể tải ảnh đại diện.");
    } finally {
      setAvatarLoading(false);
    }
  };

  const handleDeleteAvatar = async () => {
    const confirmed = window.confirm(
      "Bạn có chắc muốn xóa ảnh đại diện này không?",
    );

    if (!confirmed) return;

    try {
      setAvatarLoading(true);
      setError("");
      setSuccess("");

      await adminProfileService.deleteAvatar(userId);
      await loadProfile();

      setSuccess("Đã xóa ảnh đại diện.");
    } catch (err) {
      setError(err.response?.data?.message || "Không thể xóa ảnh đại diện.");
    } finally {
      setAvatarLoading(false);
    }
  };

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
