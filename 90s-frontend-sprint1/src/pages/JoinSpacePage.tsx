import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { spacesApi } from "../api/spaces";
import { useAuth } from "../store/authStore";

export default function JoinSpacePage() {
  const nav = useNavigate();
  const { loadMe } = useAuth();

  const [code, setCode] = useState("");
  const [msg, setMsg] = useState("");
  const [err, setErr] = useState("");
  const [busy, setBusy] = useState(false);

  async function join() {
    setErr("");
    setMsg("");
    setBusy(true);
    try {
      await spacesApi.joinByCode(code.trim());
      await loadMe();
      setMsg("加入成功，正在跳转...");
      nav("/me", { replace: true });
    } catch (e: any) {
      setErr(e?.response?.data?.message || e?.message || "Join failed");
    } finally {
      setBusy(false);
    }
  }

  return (
    <div className="x-page">
      <header className="x-topbar">
        <div className="x-topbar-inner">
          <div className="x-brand">校内网 90's Demo</div>
          <div className="x-topbar-note">加入你的班级空间</div>
        </div>
      </header>

      <main className="x-main">
        <div className="x-layout-2col">
          <section className="x-card">
            <div className="x-card-header">加入 Cohort Space</div>
            <div className="x-card-body">
              <p className="x-muted" style={{ marginTop: 0 }}>
                输入邀请代码，加入你们这一届的专属空间。
              </p>

              <div className="x-form">
                <label>
                  <span className="x-label">邀请码</span>
                  <input
                    className="x-input"
                    placeholder="例如: UCL-MSC-CS-2022F"
                    value={code}
                    onChange={(e) => setCode(e.target.value)}
                  />
                </label>

                <button className="x-btn" onClick={join} disabled={busy || !code.trim()}>
                  {busy ? "加入中..." : "加入空间"}
                </button>
              </div>

              {msg && <div className="x-success" style={{ marginTop: 10 }}>{msg}</div>}
              {err && <div className="x-error" style={{ marginTop: 10 }}>{err}</div>}
            </div>
          </section>

          <aside className="x-card">
            <div className="x-card-header">使用提示</div>
            <div className="x-card-body x-muted">
              <ul className="x-list" style={{ marginTop: 0 }}>
                <li>邀请码一般由管理员统一发放。</li>
                <li>加入后会自动同步你的空间信息。</li>
                <li>如果加入失败，请检查邀请码是否有空格。</li>
              </ul>
            </div>
          </aside>
        </div>
      </main>
    </div>
  );
}
