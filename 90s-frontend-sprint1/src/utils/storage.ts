const ACCESS = "90s_access_token";
const REFRESH = "90s_refresh_token";

export const tokenStorage = {
  getAccessToken: () => localStorage.getItem(ACCESS) || "",
  getRefreshToken: () => localStorage.getItem(REFRESH) || "",
  setAccessToken: (t: string) => localStorage.setItem(ACCESS, t),
  setRefreshToken: (t: string) => localStorage.setItem(REFRESH, t),
  clear: () => {
    localStorage.removeItem(ACCESS);
    localStorage.removeItem(REFRESH);
  }
};
