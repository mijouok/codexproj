import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../store/authStore";

function tlLabel(tl: 0 | 1 | 2 | 3) {
  return ["L0", "L1", "L2", "L3"][tl];
}

export default function MePage() {
  const nav = useNavigate();
  const { me, loadMe, logout } = useAuth();

  useEffect(() => {
    loadMe().catch(() => {});
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  if (!me) return <div style={{ padding: 24 }}>Loading...</div>;

  return (
    <div style={{ maxWidth: 720, margin: "40px auto", padding: 24, fontFamily: "system-ui" }}>
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
        <h2 style={{ margin: 0 }}>个人主页</h2>
        <button
          onClick={async () => {
            await logout();
            nav("/auth", { replace: true });
          }}
        >
          Logout
        </button>
      </div>

      <div style={{ marginTop: 14, lineHeight: 1.9 }}>
        <div><b>昵称：</b>{me.nickname}</div>
        <div><b>邮箱：</b>{me.email || "-"}</div>
        <div><b>手机号：</b>{me.phone || "-"}</div>
        <div><b>Trust Level：</b>{tlLabel(me.trust_level)}</div>
      </div>

      <h3 style={{ marginTop: 24 }}>加入的空间</h3>
      {(me.spaces?.length ?? 0) === 0 ? (
        <div style={{ opacity: 0.7 }}>你还没有加入任何空间。</div>
      ) : (
        <ul>
          {me.spaces.map((s) => (
            <li key={s.id}>
              {s.name} <span style={{ opacity: 0.7 }}>({s.membership_status})</span>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
