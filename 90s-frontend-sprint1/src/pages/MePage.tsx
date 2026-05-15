import { useCallback, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../store/authStore";
import { homeApi } from "../api/home";
import type { HomeResponse } from "../api/types";

function tlLabel(tl: 0 | 1 | 2 | 3) {
  return ["L0", "L1", "L2", "L3"][tl];
}

function actionLabel(actionText: string) {
  if (actionText === "Reply") return "\u56de\u590d";
  if (actionText === "View") return "\u67e5\u770b";
  if (actionText === "Open") return "\u53bb\u770b\u770b";
  return actionText;
}

export default function MePage() {
  const nav = useNavigate();
  const { me, loadMe, logout } = useAuth();
  const [home, setHome] = useState<HomeResponse | null>(null);
  const [statusDraft, setStatusDraft] = useState("");
  const [messageDraft, setMessageDraft] = useState("");
  const [postingStatus, setPostingStatus] = useState(false);
  const [postingMessage, setPostingMessage] = useState(false);

  const refreshHome = useCallback(async () => {
    try {
      const data = await homeApi.getHome();
      setHome(data);
    } catch {
      setHome(null);
    }
  }, []);

  useEffect(() => {
    loadMe().catch(() => {});
  }, [loadMe]);

  useEffect(() => {
    refreshHome().catch(() => {});
  }, [refreshHome]);

  if (!me) return <div className="x-loading">Loading...</div>;

  const school = home?.school || "\u6c5f\u57ce\u5927\u5b66";
  const department = home?.department || "\u8ba1\u7b97\u673a\u4e0e\u4fe1\u606f\u5de5\u7a0b\u5b66\u9662";
  const statusText = home?.statusText || "\u6700\u8fd1\u5728\u5fd9\u671f\u672b\uff0c\u56fe\u4e66\u9986\u5e38\u9a7b\u4e2d\u3002";
  const albums = home?.albums || [];
  const messages = home?.messages || [];
  const activities = home?.activities || [];
  const visitors = home?.visitors || [];
  const widgets = home?.widgets || [];
  const spaces = home?.spaces || [];

  async function submitStatus() {
    if (!statusDraft.trim() || postingStatus) return;
    setPostingStatus(true);
    try {
      await homeApi.postStatus(statusDraft.trim());
      setStatusDraft("");
      await refreshHome();
    } finally {
      setPostingStatus(false);
    }
  }

  async function submitMessage() {
    if (!messageDraft.trim() || postingMessage) return;
    setPostingMessage(true);
    try {
      await homeApi.postMessage(messageDraft.trim());
      setMessageDraft("");
      await refreshHome();
    } finally {
      setPostingMessage(false);
    }
  }

  return (
    <div className="x-page">
      <header className="x-topbar x-home-topbar">
        <div className="x-topbar-inner x-home-topbar-inner">
          <div className="x-home-brand-wrap">
            <div className="x-brand x-home-brand">{"\u6821\u5185\u7f51"}</div>
            <div className="x-home-brand-sub">{"\u9752\u6625\u4e0d\u6563\u573a"}</div>
          </div>
          <nav className="x-home-nav">
            <a href="#">{"\u4e3b\u9875"}</a>
            <a href="#">{"\u597d\u53cb"}</a>
            <a href="#">{"\u76f8\u518c"}</a>
            <a href="#">{"\u7559\u8a00"}</a>
            <a href="#">{"\u5c0f\u7ec4"}</a>
          </nav>
          <div className="x-home-user-ops">
            <span className="x-home-welcome">{"\u6b22\u8fce\u4f60\uff0c"}{me.nickname}</span>
            <button
              className="x-btn x-btn-quiet x-home-logout"
              onClick={async () => {
                await logout();
                nav("/auth", { replace: true });
              }}
            >
              {"\u9000\u51fa"}
            </button>
          </div>
        </div>
      </header>

      <main className="x-main x-home-main">
        <div className="x-home-layout">
          <aside className="x-card x-home-card">
            <div className="x-home-section-title">{"\u4e2a\u4eba\u6863\u6848"}</div>
            <div className="x-card-body x-home-compact">
              <div className="x-home-profile-head">
                <div className="x-home-avatar" aria-hidden="true">
                  {me.nickname.slice(0, 1).toUpperCase()}
                </div>
                <div>
                  <div className="x-home-name">{me.nickname}</div>
                  <div className="x-home-meta">{school}</div>
                  <div className="x-home-meta">{department}</div>
                </div>
              </div>
              <table className="x-home-table" role="presentation">
                <tbody>
                  <tr>
                    <th>{"\u72b6\u6001"}</th>
                    <td>{statusText}</td>
                  </tr>
                  <tr>
                    <th>{"\u90ae\u7bb1"}</th>
                    <td>{me.email || "\u8fd8\u6ca1\u586b\u5199"}</td>
                  </tr>
                  <tr>
                    <th>{"\u624b\u673a\u53f7"}</th>
                    <td>{me.phone || "\u6682\u672a\u516c\u5f00"}</td>
                  </tr>
                  <tr>
                    <th>{"\u4fe1\u4efb\u7b49\u7ea7"}</th>
                    <td>{tlLabel(me.trust_level)}</td>
                  </tr>
                </tbody>
              </table>

              <div className="x-home-postbox">
                <label className="x-label" htmlFor="status-input">{"\u53d1\u65b0\u72b6\u6001"}</label>
                <input
                  id="status-input"
                  className="x-input"
                  placeholder={"\u4f8b\u5982\uff1a\u6700\u8fd1\u5728\u5fd9\u671f\u672b"}
                  value={statusDraft}
                  maxLength={200}
                  onChange={(e) => setStatusDraft(e.target.value)}
                />
                <button className="x-btn x-home-small-btn" onClick={submitStatus} disabled={postingStatus || !statusDraft.trim()}>
                  {postingStatus ? "..." : "\u53d1\u5e03"}
                </button>
              </div>

              <div className="x-home-links">
                <a href="#">{"\u5199\u65b0\u72b6\u6001"}</a>
                <a href="#">{"\u7f16\u8f91\u8d44\u6599"}</a>
                <a href="#">{"\u7ba1\u7406\u76f8\u518c"}</a>
              </div>
            </div>
          </aside>

          <section className="x-home-center">
            <article className="x-card x-home-card">
              <div className="x-home-section-title">{"\u7559\u8a00\u677f"}</div>
              <div className="x-card-body x-home-compact">
                <div className="x-home-postbox x-home-postbox-inline">
                  <input
                    className="x-input"
                    placeholder={"\u7559\u4e00\u53e5\uff1a\u6765\u8e29\u4e00\u811a~"}
                    value={messageDraft}
                    maxLength={300}
                    onChange={(e) => setMessageDraft(e.target.value)}
                  />
                  <button className="x-btn x-home-small-btn" onClick={submitMessage} disabled={postingMessage || !messageDraft.trim()}>
                    {postingMessage ? "..." : "\u7559\u8a00"}
                  </button>
                </div>

                <ul className="x-home-feed">
                  {messages.map((item) => (
                    <li key={`${item.fromNickname}-${item.timeText}-${item.content}`}>
                      <strong>{item.fromNickname}</strong>: {item.content}
                      <a href="#">{actionLabel(item.actionText)}</a>
                      <span>{item.timeText}</span>
                    </li>
                  ))}
                  {messages.length === 0 && <li className="x-muted">{"\u6682\u65f6\u8fd8\u6ca1\u6709\u65b0\u7559\u8a00\u3002"}</li>}
                </ul>
              </div>
            </article>

            <article className="x-card x-home-card">
              <div className="x-home-section-title">{"\u597d\u53cb\u52a8\u6001"}</div>
              <div className="x-card-body x-home-compact">
                <ul className="x-home-feed">
                  {activities.map((item) => (
                    <li key={`${item.actorNickname}-${item.timeText}-${item.content}`}>
                      <strong>{item.actorNickname}</strong> {item.content}
                      <a href="#">{actionLabel(item.actionText)}</a>
                      <span>{item.timeText}</span>
                    </li>
                  ))}
                  {activities.length === 0 && <li className="x-muted">{"\u4f60\u7684\u597d\u53cb\u6700\u8fd1\u6bd4\u8f83\u5b89\u9759\u3002"}</li>}
                </ul>
              </div>
            </article>

            <article className="x-card x-home-card">
              <div className="x-home-section-title">{"\u76f8\u518c\u7f29\u7565\u56fe"}</div>
              <div className="x-card-body x-home-compact">
                <div className="x-home-gallery">
                  {albums.map((item) => (
                    <a href="#" key={`${item.title}-${item.marker}`} className="x-home-thumb">
                      <div className="x-home-thumb-img">{item.marker}</div>
                      <div className="x-home-thumb-title">{item.title}</div>
                    </a>
                  ))}
                </div>
                {albums.length === 0 && <div className="x-muted">{"\u4f60\u8fd8\u6ca1\u6709\u4e0a\u4f20\u76f8\u518c\u3002"}</div>}
              </div>
            </article>
          </section>

          <aside className="x-home-right">
            <article className="x-card x-home-card">
              <div className="x-home-section-title">{"\u6765\u8bbf\u8bb0\u5f55"}</div>
              <div className="x-card-body x-home-compact">
                <ul className="x-home-visitor-list">
                  {visitors.map((item) => (
                    <li key={`${item.nickname}-${item.timeText}`}>
                      <div className="x-home-visitor-avatar">{item.nickname[0]}</div>
                      <div>
                        <div>
                          <strong>{item.nickname}</strong>
                          <span className="x-home-visitor-note">{item.note}</span>
                        </div>
                        <div className="x-muted">{item.timeText}</div>
                      </div>
                    </li>
                  ))}
                  {visitors.length === 0 && <li className="x-muted">{"\u8fd8\u6ca1\u6709\u6765\u8bbf\u8bb0\u5f55\u3002"}</li>}
                </ul>
              </div>
            </article>

            <article className="x-card x-home-card">
              <div className="x-home-section-title">{"\u6211\u7684\u7a7a\u95f4"}</div>
              <div className="x-card-body x-home-compact">
                {spaces.length === 0 ? (
                  <div className="x-muted">{"\u4f60\u8fd8\u6ca1\u6709\u52a0\u5165\u4efb\u4f55\u7a7a\u95f4\u3002"}</div>
                ) : (
                  <ul className="x-list x-home-list-tight">
                    {spaces.map((s) => (
                      <li key={`${s.name}-${s.membershipStatus}`}>
                        {s.name} <span className="x-muted">({s.membershipStatus})</span>
                      </li>
                    ))}
                  </ul>
                )}
              </div>
            </article>

            <article className="x-card x-home-card">
              <div className="x-home-section-title">{"\u4eca\u65e5\u5c0f\u7ec4\u4ef6"}</div>
              <div className="x-card-body x-home-compact x-home-widget">
                {widgets.map((item) => (
                  <p key={item.title}>
                    <strong>{item.title}:</strong> {item.content}
                  </p>
                ))}
                {widgets.length === 0 && <p className="x-muted">{"\u6682\u65e0\u5c0f\u7ec4\u4ef6\u5185\u5bb9\u3002"}</p>}
              </div>
            </article>
          </aside>
        </div>
      </main>
    </div>
  );
}


