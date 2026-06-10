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
        nav("/me", { replace: true });
      }
    })();
  }, [isAuthed, loadMe, me, nav]);

  async function onSubmit() {
    setErr("");
    setBusy(true);
    try {
      if (mode === "login") await login(identifier, password);
      else await register(identifier, nickname, password);

      nav("/me", { replace: true });
    } catch (e: any) {
      setErr(e?.response?.data?.message || e?.message || "Auth failed");
    } finally {
      setBusy(false);
    }
  }

  return (
    <div className="x-page x-poster-page x-auth-page">
      <header className="x-topbar">
        <div className="x-topbar-inner">
          <div className="x-brand">90s 校内网</div>
          <div className="x-topbar-note">致我们逝去的青春和最纯真的网络时代</div>
        </div>
      </header>

      <main className="x-main">
        <section className="x-poster-hero" aria-label="90s 校内网">
          <p className="x-poster-kicker">不是微信朋友圈，也不是陌陌探探</p>
          <h1>90s 校内网</h1>
          <div className="x-poster-brush">让你重新认识陌生人的地方</div>
        </section>

        <div className="x-layout-2col">
          <section className="x-card x-auth-card">
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

          <aside className="x-paper-note x-auth-note">
            <div className="x-paper-pin" aria-hidden="true" />
            <div className="x-paper-title">这里没有</div>
            <ul>
              <li>短视频刷屏</li>
              <li>算法推荐</li>
              <li>颜值速配</li>
              <li>流量至上</li>
            </ul>
            <div className="x-paper-title x-paper-title-blue">这里只有</div>
            <ul>
              <li>真实的人</li>
              <li>真诚的交流</li>
              <li>慢下来的社交</li>
              <li>属于我们的青春回忆</li>
            </ul>
            <div className="x-paper-foot">
              校内的我们，简单、真诚、无限可能
            </div>
          </aside>
        </div>
      </main>
    </div>
  );
}
