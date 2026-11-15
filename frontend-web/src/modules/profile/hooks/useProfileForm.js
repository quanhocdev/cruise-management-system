// src/modules/profile/hooks/useProfileForm.js

import { useEffect, useState } from "react";
import {
  createMyProfile,
  updateMyProfile,
  uploadMyAvatar,
  deleteMyAvatar,
  extractErrorMessage,
} from "../services/profileService";

export const PROFILE_FIELDS = [
  "fullName",
  "dateOfBirth",
  "phone",
  "address",
  "gender",
  "nationality",
  "identityNumber",
];

const toForm = (profile) =>
  PROFILE_FIELDS.reduce((acc, key) => {
    acc[key] = profile?.[key] ?? "";
    return acc;
  }, {});

export default function useProfileForm(profile, setProfile) {
  const [form, setForm] = useState(() => toForm(profile));
  const [saving, setSaving] = useState(false);
  const [avatarLoading, setAvatarLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  useEffect(() => {
    setForm(toForm(profile));
  }, [profile]);

  const handleChange = (event) => {
    const { name, value } = event.target;

    setForm((previous) => ({
      ...previous,
      [name]: value,
    }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    setSaving(true);
    setError("");
    setSuccess("");

    const requestData = {
      fullName: form.fullName.trim(),
      dateOfBirth: form.dateOfBirth || null,
      phone: form.phone.trim(),
      address: form.address.trim(),
      gender: form.gender || null,
      nationality: form.nationality.trim(),
      identityNumber: form.identityNumber.trim(),
    };

    try {
      const response = profile
        ? await updateMyProfile(requestData)
        : await createMyProfile(requestData);

      setProfile(response);
      setSuccess(
        profile
          ? "Cập nhật hồ sơ thành công."
          : "Tạo hồ sơ cá nhân thành công.",
      );

      return true;
    } catch (err) {
      setError(extractErrorMessage(err, "Không thể lưu hồ sơ cá nhân."));
      return false;
    } finally {
      setSaving(false);
    }
  };

  const handleUploadAvatar = async (file) => {
    if (!file) {
      setError("Vui lòng chọn ảnh đại diện.");
      return;
    }

    if (!profile) {
      setError("Vui lòng tạo hồ sơ trước khi tải ảnh đại diện.");
      return;
    }

    setAvatarLoading(true);
    setError("");
    setSuccess("");

    try {
      const response = await uploadMyAvatar(file);
      setProfile(response);
      setSuccess("Tải ảnh đại diện thành công.");
    } catch (err) {
      setError(extractErrorMessage(err, "Không thể tải ảnh đại diện."));
    } finally {
      setAvatarLoading(false);
    }
  };

  const handleDeleteAvatar = async () => {
    if (!profile?.avatarUrl) return;

    if (!window.confirm("Bạn có chắc muốn xóa ảnh đại diện?")) {
      return;
    }

    setAvatarLoading(true);
    setError("");
    setSuccess("");

    try {
      const response = await deleteMyAvatar();
      setProfile(response);
      setSuccess("Xóa ảnh đại diện thành công.");
    } catch (err) {
      setError(extractErrorMessage(err, "Không thể xóa ảnh đại diện."));
    } finally {
      setAvatarLoading(false);
    }
  };

  return {
    form,
    saving,
    avatarLoading,
    error,
    success,
    setError,
    setSuccess,
    handleChange,
    handleSubmit,
    handleUploadAvatar,
    handleDeleteAvatar,
  };
}
