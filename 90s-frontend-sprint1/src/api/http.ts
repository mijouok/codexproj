import axios from "axios";
import { tokenStorage } from "../utils/storage";
import type { TokenPair } from "./types";

const BASE_URL = import.meta.env.VITE_API_BASE_URL || "";

export const http = axios.create({
  baseURL: BASE_URL,
  headers: { "Content-Type": "application/json" }
});

http.interceptors.request.use((config) => {
  const token = tokenStorage.getAccessToken();
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

let refreshing: Promise<string> | null = null;

async function refreshAccessToken(): Promise<string> {
  const refreshToken = tokenStorage.getRefreshToken();
  if (!refreshToken) throw new Error("No refresh token");

  const res = await axios.post(
    `${BASE_URL}/api/auth/refresh`,
    { refresh_token: refreshToken },
    { headers: { "Content-Type": "application/json" } }
  );

  const data = res.data as TokenPair;
  const access = data.access_token || data.accessToken || "";
  const refresh = data.refresh_token || data.refreshToken || "";
  if (!access) throw new Error("No access token in refresh response");
  tokenStorage.setAccessToken(access);
  tokenStorage.setRefreshToken(refresh);
  return access;
}

http.interceptors.response.use(
  (resp) => resp,
  async (error) => {
    const original = error.config;

    if (error.response?.status !== 401 || original?._retry) {
      return Promise.reject(error);
    }

    original._retry = true;

    try {
      if (!refreshing) refreshing = refreshAccessToken();
      const newAccess = await refreshing;
      refreshing = null;

      original.headers.Authorization = `Bearer ${newAccess}`;
      return http(original);
    } catch (e) {
      refreshing = null;
      tokenStorage.clear();
      return Promise.reject(e);
    }
  }
);
