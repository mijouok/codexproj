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
  }, [loadMe]);

  if (!me) return <div className="x-loading">Loading...</div>;

  return (
    <div className="x-page">
      <header className="x-topbar">
        <div className="x-topbar-inner">
          <div className="x-brand">校内网 90's Demo</div>
          <button
            className="x-btn x-btn-quiet"
            onClick={async () => {
              await logout();
              nav("/auth", { replace: true });
            }}
          >
            退出
          </button>
        </div>
      </header>

      <main className="x-main">
        <div className="x-layout-2col">
          <section className="x-card">
            <div className="x-card-header">个人主页</div>
            <div className="x-card-body">
              <div className="x-profile-grid">
                <div className="x-profile-key">昵称</div>
                <div>{me.nickname}</div>
                <div className="x-profile-key">邮箱</div>
                <div>{me.email || "-"}</div>
                <div className="x-profile-key">手机号</div>
                <div>{me.phone || "-"}</div>
                <div className="x-profile-key">Trust Level</div>
                <div>{tlLabel(me.trust_level)}</div>
              </div>
            </div>
          </section>

          <aside className="x-card">
            <div className="x-card-header">我的空间</div>
            <div className="x-card-body">
              {(me.spaces?.length ?? 0) === 0 ? (
                <div className="x-muted">你还没有加入任何空间。</div>
              ) : (
                <ul className="x-list">
                  {me.spaces.map((s) => (
                    <li key={s.id}>
                      {s.name} <span className="x-muted">({s.membership_status})</span>
                    </li>
                  ))}
                </ul>
              )}
            </div>
          </aside>
        </div>
      </main>
    </div>
  );
}
