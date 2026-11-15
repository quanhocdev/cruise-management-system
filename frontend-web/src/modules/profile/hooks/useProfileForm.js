// src/modules/profile/hooks/useProfileForm.js
import { useEffect, useState } from "react";

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

export default function useProfileForm(profile) {
  const [form, setForm] = useState(() => toForm(profile));

  useEffect(() => {
    setForm(toForm(profile));
  }, [profile]);

  return { form };
}
