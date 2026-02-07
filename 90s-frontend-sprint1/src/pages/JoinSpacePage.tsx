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
    setErr(""); setMsg("");
    setBusy(true);
    try {
      await spacesApi.joinByCode(code.trim());
      await loadMe();
      setMsg("加入成功，正在跳转…");
      nav("/me", { replace: true });
    } catch (e: any) {
      setErr(e?.response?.data?.message || e?.message || "Join failed");
    } finally {
      setBusy(false);
    }
  }

  return (
    <div style={{ maxWidth: 520, margin: "60px auto", padding: 24, fontFamily: "system-ui" }}>
      <h2>加入 Cohort Space</h2>
      <p style={{ opacity: 0.75 }}>
        输入邀请码加入你们这一届的空间（例如：UCL · MSc CS · 2022F）。
      </p>

      <div style={{ display: "flex", gap: 10 }}>
        <input
          placeholder="邀请码"
          value={code}
          onChange={(e) => setCode(e.target.value)}
          style={{ flex: 1 }}
        />
        <button onClick={join} disabled={busy || !code.trim()}>
          {busy ? "加入中..." : "Join"}
        </button>
      </div>

      {msg && <div style={{ color: "green", marginTop: 10 }}>{msg}</div>}
      {err && <div style={{ color: "crimson", marginTop: 10 }}>{err}</div>}
    </div>
  );
}
