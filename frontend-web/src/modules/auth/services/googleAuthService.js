import api from "../../../api/axios";

const googleAuthService = {
  async login(idToken) {
    const response = await api.post("/auth/google/login", {
      idToken,
    });

    return response.data;
  },

  async verifyOtp(userId, otp) {
    const response = await api.post("/auth/google/verify-otp", {
      userId,
      otp,
    });

    return response.data;
  },
};

export default googleAuthService;
