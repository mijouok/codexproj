import React, { createContext, useCallback, useContext, useEffect, useMemo, useState } from "react";
import { authApi } from "../api/auth";
import { tokenStorage } from "../utils/storage";
import type { MeResponse, TokenPair } from "../api/types";

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

function pickTokens(res: TokenPair) {
  const access = res.access_token || res.accessToken || "";
  const refresh = res.refresh_token || res.refreshToken || "";
  return { access, refresh };
}

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [me, setMe] = useState<MeResponse | null>(null);
  const [loading, setLoading] = useState<boolean>(true);

  const isAuthed = !!tokenStorage.getAccessToken();

  const loadMe = useCallback(async () => {
    const data = await authApi.me();
    setMe(data);
  }, []);

  const login = useCallback(async (identifier: string, password: string) => {
    const res = await authApi.login({ identifier, password });
    const { access, refresh } = pickTokens(res);
    if (!access) throw new Error("No access token in login response");
    tokenStorage.setAccessToken(access);
    tokenStorage.setRefreshToken(refresh);
    await loadMe();
  }, [loadMe]);

  const register = useCallback(async (identifier: string, nickname: string, password: string) => {
    const res = await authApi.register({ identifier, nickname, password });
    const { access, refresh } = pickTokens(res);
    if (!access) throw new Error("No access token in register response");
    tokenStorage.setAccessToken(access);
    tokenStorage.setRefreshToken(refresh);
    await loadMe();
  }, [loadMe]);

  const logout = useCallback(async () => {
    try {
      await authApi.logout();
    } finally {
      tokenStorage.clear();
      setMe(null);
    }
  }, []);

  useEffect(() => {
    let active = true;

    (async () => {
      try {
        if (tokenStorage.getAccessToken()) {
          await loadMe();
        }
      } catch {
        tokenStorage.clear();
        setMe(null);
      } finally {
        if (active) {
          setLoading(false);
        }
      }
    })();

    return () => {
      active = false;
    };
  }, [loadMe]);

  const value = useMemo(
    () => ({ me, isAuthed, loading, loadMe, login, register, logout }),
    [me, isAuthed, loading, loadMe, login, register, logout]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used within AuthProvider");
  return ctx;
}
