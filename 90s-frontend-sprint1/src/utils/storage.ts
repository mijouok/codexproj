const ACCESS = "90s_access_token";
const REFRESH = "90s_refresh_token";

function getCookie(name: string): string {
  const target = `${encodeURIComponent(name)}=`;
  const matched = document.cookie
    .split(";")
    .map((part) => part.trim())
    .find((part) => part.startsWith(target));

  if (!matched) return "";
  return decodeURIComponent(matched.slice(target.length));
}

function setCookie(name: string, value: string, maxAgeSeconds = 60 * 60 * 24 * 7) {
  const key = encodeURIComponent(name);
  const val = encodeURIComponent(value);
  document.cookie = `${key}=${val}; Path=/; Max-Age=${maxAgeSeconds}; SameSite=Lax`;
}

function clearCookie(name: string) {
  const key = encodeURIComponent(name);
  document.cookie = `${key}=; Path=/; Max-Age=0; SameSite=Lax`;
}

export const tokenStorage = {
  getAccessToken: () => getCookie(ACCESS),
  getRefreshToken: () => getCookie(REFRESH),
  setAccessToken: (t: string) => setCookie(ACCESS, t),
  setRefreshToken: (t: string) => setCookie(REFRESH, t),
  clear: () => {
    clearCookie(ACCESS);
    clearCookie(REFRESH);
  }
};
