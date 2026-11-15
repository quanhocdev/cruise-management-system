// src/modules/profile/hooks/useProfile.js
// Hook tải hồ sơ của người đang đăng nhập.
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
      setProfile(await getMyProfile());
    } catch (err) {
      setError(extractErrorMessage(err, "Không tải được thông tin tài khoản"));
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  return { profile, setProfile, loading, error, reload: load };
}
