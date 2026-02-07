import React, { createContext, useContext, useEffect, useMemo, useState } from "react";
import { authApi } from "../api/auth";
import { tokenStorage } from "../utils/storage";
import type { MeResponse } from "../api/types";

type AuthContextType = {
  me: MeResponse | null;
  isAuthed: boolean;
  loading: boolean;
  loadMe: () => Promise<void>;
  login: (identifier: string, password: string) => Promise<void>;
  register: (identifier: string, nickname: string, password: string) => Promise<void>;
  logout: () => Promise<void>;
};

const AuthContext = createContext<AuthContextType | null>(null);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [me, setMe] = useState<MeResponse | null>(null);
  const [loading, setLoading] = useState<boolean>(true);

  const isAuthed = !!tokenStorage.getAccessToken();

  async function loadMe() {
    const data = await authApi.me();
    setMe(data);
  }

  async function login(identifier: string, password: string) {
    const res = await authApi.login({ identifier, password });
    tokenStorage.setAccessToken(res.access_token);
    tokenStorage.setRefreshToken(res.refresh_token);
    await loadMe();
  }

  async function register(identifier: string, nickname: string, password: string) {
    const res = await authApi.register({ identifier, nickname, password });
    tokenStorage.setAccessToken(res.access_token);
    tokenStorage.setRefreshToken(res.refresh_token);
    await loadMe();
  }

  async function logout() {
    try {
      await authApi.logout();
    } finally {
      tokenStorage.clear();
      setMe(null);
    }
  }

  useEffect(() => {
    (async () => {
      try {
        if (tokenStorage.getAccessToken()) {
          await loadMe();
        }
      } finally {
        setLoading(false);
      }
    })();
  }, []);

  const value = useMemo(
    () => ({ me, isAuthed, loading, loadMe, login, register, logout }),
    [me, isAuthed, loading]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used within AuthProvider");
  return ctx;
}
