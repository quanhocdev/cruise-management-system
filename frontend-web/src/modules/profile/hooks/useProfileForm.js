// src/modules/profile/hooks/useProfileForm.js
// Hook quản lý form chỉnh sửa: state, kiểm tra, chỉ gửi field đã đổi.
import { useEffect, useMemo, useState } from "react";
import {
  updateMyProfile,
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

const MAX = {
  fullName: 150,
  phone: 20,
  address: 500,
  gender: 30,
  nationality: 100,
  identityNumber: 50,
};

function validate(form) {
  const errors = {};
  Object.entries(MAX).forEach(([key, max]) => {
    if (form[key] && form[key].length > max) {
      errors[key] = `Tối đa ${max} ký tự`;
    }
  });
  if (form.dateOfBirth && new Date(form.dateOfBirth) >= new Date()) {
    errors.dateOfBirth = "Ngày sinh phải nằm trong quá khứ";
  }
  return errors;
}

export default function useProfileForm(profile, onSaved) {
  const [form, setForm] = useState(toForm(profile));
  const [errors, setErrors] = useState({});
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState(null); // { type: "success" | "danger", text }

  // Nạp lại form khi dữ liệu từ server về / được cập nhật
  useEffect(() => {
    setForm(toForm(profile));
    setErrors({});
  }, [profile]);

  const changedPayload = useMemo(() => {
    const original = toForm(profile);
    const payload = {};
    PROFILE_FIELDS.forEach((key) => {
      const value =
        typeof form[key] === "string" ? form[key].trim() : form[key];
      if (value !== original[key]) payload[key] = value;
    });
    return payload;
  }, [form, profile]);

  const dirty = Object.keys(changedPayload).length > 0;

  const setField = (key, value) => {
    setForm((prev) => ({ ...prev, [key]: value }));
    setMessage(null);
  };

  const reset = () => {
    setForm(toForm(profile));
    setErrors({});
    setMessage(null);
  };

  const submit = async (e) => {
    e?.preventDefault();
    const found = validate(form);
    setErrors(found);
    if (Object.keys(found).length > 0 || !dirty) return;

    setSaving(true);
    setMessage(null);
    try {
      const updated = await updateMyProfile(changedPayload);
      onSaved?.(updated);
      setMessage({ type: "success", text: "Đã lưu thông tin tài khoản" });
    } catch (err) {
      setMessage({
        type: "danger",
        text: extractErrorMessage(err, "Không thể lưu thông tin"),
      });
    } finally {
      setSaving(false);
    }
  };

  return { form, errors, saving, message, dirty, setField, reset, submit };
}
