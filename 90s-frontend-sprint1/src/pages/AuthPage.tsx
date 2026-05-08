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
        try {
          await loadMe();
        } catch {}
      }
      if (isAuthed && me) {
        nav((me.spaces?.length ?? 0) > 0 ? "/me" : "/join", { replace: true });
      }
    })();
  }, [isAuthed, loadMe, me, nav]);

  async function onSubmit() {
    setErr("");
    setBusy(true);
    try {
      if (mode === "login") await login(identifier, password);
      else await register(identifier, nickname, password);

      const spaces = me?.spaces?.length ?? 0;
      nav(spaces > 0 ? "/me" : "/join", { replace: true });
    } catch (e: any) {
      setErr(e?.response?.data?.message || e?.message || "Auth failed");
    } finally {
      setBusy(false);
    }
  }

  return (
    <div className="x-page">
      <header className="x-topbar">
        <div className="x-topbar-inner">
          <div className="x-brand">校内网 90's Demo</div>
          <div className="x-topbar-note">欢迎回来，同学</div>
        </div>
      </header>

      <main className="x-main">
        <div className="x-layout-2col">
          <section className="x-card">
            <div className="x-card-header">账号登录</div>
            <div className="x-card-body">
              <div className="x-tabs">
                <button
                  className={`x-tab ${mode === "login" ? "x-tab-active" : ""}`}
                  onClick={() => setMode("login")}
                  disabled={mode === "login"}
                >
                  登录
                </button>
                <button
                  className={`x-tab ${mode === "register" ? "x-tab-active" : ""}`}
                  onClick={() => setMode("register")}
                  disabled={mode === "register"}
                >
                  注册
                </button>
              </div>

              <div className="x-form">
                <label>
                  <span className="x-label">邮箱或手机号</span>
                  <input
                    className="x-input"
                    placeholder="输入邮箱或手机号"
                    value={identifier}
                    onChange={(e) => setIdentifier(e.target.value)}
                  />
                </label>

                {mode === "register" && (
                  <label>
                    <span className="x-label">昵称</span>
                    <input
                      className="x-input"
                      placeholder="你的校园昵称"
                      value={nickname}
                      onChange={(e) => setNickname(e.target.value)}
                    />
                  </label>
                )}

                <label>
                  <span className="x-label">密码</span>
                  <input
                    className="x-input"
                    placeholder="输入密码"
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                  />
                </label>

                {err && <div className="x-error">{err}</div>}

                <button className="x-btn" onClick={onSubmit} disabled={busy}>
                  {busy ? "处理中..." : mode === "login" ? "立即登录" : "立即注册"}
                </button>
              </div>
            </div>
          </section>

          <aside className="x-card">
            <div className="x-card-header">新鲜事</div>
            <div className="x-card-body x-muted">
              <p style={{ marginTop: 0 }}>
                这是 Sprint 1 的最小认证演示，页面样式参考了早期校内网的蓝白校园风格。
              </p>
              <p>你可以先登录，再加入一个 Cohort Space。</p>
              <p style={{ marginBottom: 0, fontSize: 12 }}>
                Demo 提示：refresh token 仅保存在 localStorage。
              </p>
            </div>
          </aside>
        </div>
      </main>
    </div>
  );
}
