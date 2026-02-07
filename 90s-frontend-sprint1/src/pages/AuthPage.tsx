import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../store/authStore";

export default function AuthPage() {
  const nav = useNavigate();
  const { login, register, me, loadMe, isAuthed } = useAuth();

  const [mode, setMode] = useState<"login" | "register">("login");
  const [identifier, setIdentifier] = useState("");
  const [nickname, setNickname] = useState("");
  const [password, setPassword] = useState("");
  const [err, setErr] = useState("");
  const [busy, setBusy] = useState(false);

  useEffect(() => {
    (async () => {
      if (isAuthed && !me) {
        try { await loadMe(); } catch {}
      }
      if (isAuthed && me) {
        nav((me.spaces?.length ?? 0) > 0 ? "/me" : "/join", { replace: true });
      }
    })();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isAuthed, me]);

  async function onSubmit() {
    setErr("");
    setBusy(true);
    try {
      if (mode === "login") await login(identifier, password);
      else await register(identifier, nickname, password);

      // login/register 已 loadMe，这里用最新 me（若想更严谨可再 await loadMe()）
      const spaces = (me?.spaces?.length ?? 0);
      nav(spaces > 0 ? "/me" : "/join", { replace: true });
    } catch (e: any) {
      setErr(e?.response?.data?.message || e?.message || "Auth failed");
    } finally {
      setBusy(false);
    }
  }

  return (
    <div style={{ maxWidth: 420, margin: "60px auto", padding: 24, fontFamily: "system-ui" }}>
      <h2 style={{ marginBottom: 8 }}>90’s 校友网</h2>
      <div style={{ opacity: 0.7, marginBottom: 16 }}>Sprint 1 最小认证 Demo</div>

      <div style={{ display: "flex", gap: 8, margin: "12px 0" }}>
        <button onClick={() => setMode("login")} disabled={mode === "login"}>登录</button>
        <button onClick={() => setMode("register")} disabled={mode === "register"}>注册</button>
      </div>

      <div style={{ display: "flex", flexDirection: "column", gap: 10 }}>
        <input
          placeholder="邮箱或手机号"
          value={identifier}
          onChange={(e) => setIdentifier(e.target.value)}
        />

        {mode === "register" && (
          <input
            placeholder="昵称"
            value={nickname}
            onChange={(e) => setNickname(e.target.value)}
          />
        )}

        <input
          placeholder="密码"
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />

        {err && <div style={{ color: "crimson" }}>{err}</div>}

        <button onClick={onSubmit} disabled={busy}>
          {busy ? "处理中..." : (mode === "login" ? "登录" : "注册")}
        </button>
      </div>

      <div style={{ marginTop: 14, opacity: 0.7, fontSize: 12 }}>
        * Demo: refresh_token 存 localStorage，生产环境建议 httpOnly cookie
      </div>
    </div>
  );
}
