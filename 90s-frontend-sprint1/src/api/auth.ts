import { http } from "./http";
import type { LoginReq, RegisterReq, MeResponse, TokenPair } from "./types";
import { tokenStorage } from "../utils/storage";

export const authApi = {
  login: async (req: LoginReq): Promise<TokenPair> => {
    const { data } = await http.post("/api/auth/login", req);
    return data;
  },
  register: async (req: RegisterReq): Promise<TokenPair> => {
    const { data } = await http.post("/api/auth/register", req);
    return data;
  },
  logout: async (): Promise<void> => {
    const refreshToken = tokenStorage.getRefreshToken();
    if (!refreshToken) return;
    await http.post("/api/auth/logout", { refreshToken });
  },
  me: async (): Promise<MeResponse> => {
    const { data } = await http.get("/api/auth/me");
    const normalizedTrustLevel = data.trust_level ?? data.trustLevel;
    return {
      ...data,
      trust_level: normalizedTrustLevel,
      roles: data.roles ?? []
    };
  }
};
