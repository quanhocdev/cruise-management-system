// src/modules/profile/hooks/useProfile.js

import { useCallback, useEffect, useState } from "react";
import { getMyProfile, extractErrorMessage } from "../services/profileService";

export default function useProfile() {
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const load = useCallback(async () => {
    setLoading(true);
    setError(null);

    try {
      const data = await getMyProfile();
      setProfile(data);
    } catch (err) {
      const isNotFound =
        Number(err.status) === 404 ||
        err.message?.includes("404 NOT_FOUND") ||
        err.message?.includes("Không tìm thấy hồ sơ cá nhân");

      if (isNotFound) {
        setProfile(null);
        setError(null);
      } else {
        setError(
          extractErrorMessage(err, "Không tải được thông tin tài khoản"),
        );
      }
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  return {
    profile,
    setProfile,
    hasProfile: Boolean(profile),
    loading,
    error,
    setError,
    reload: load,
  };
}
