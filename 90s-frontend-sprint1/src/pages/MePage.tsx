import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../store/authStore";
import { homeApi } from "../api/home";
import type { HomeResponse } from "../api/types";

function tlLabel(tl: 0 | 1 | 2 | 3) {
  return ["L0", "L1", "L2", "L3"][tl];
}

export default function MePage() {
  const nav = useNavigate();
  const { me, loadMe, logout } = useAuth();
  const [home, setHome] = useState<HomeResponse | null>(null);

  useEffect(() => {
    loadMe().catch(() => {});
  }, [loadMe]);

  useEffect(() => {
    homeApi
      .getHome()
      .then(setHome)
      .catch(() => {
        setHome(null);
      });
  }, []);

  if (!me) return <div className="x-loading">Loading...</div>;

  const school = home?.school || "Jiangcheng University";
  const department = home?.department || "School of Computer and Information Engineering";
  const statusText = home?.statusText || "Busy with finals these days.";
  const albums = home?.albums || [];
  const messages = home?.messages || [];
  const activities = home?.activities || [];
  const visitors = home?.visitors || [];
  const widgets = home?.widgets || [];
  const spaces = home?.spaces || [];

  return (
    <div className="x-page">
      <header className="x-topbar x-home-topbar">
        <div className="x-topbar-inner x-home-topbar-inner">
          <div className="x-home-brand-wrap">
            <div className="x-brand x-home-brand">Xiaonei</div>
            <div className="x-home-brand-sub">Campus Memories</div>
          </div>
          <nav className="x-home-nav">
            <a href="#">Home</a>
            <a href="#">Friends</a>
            <a href="#">Albums</a>
            <a href="#">Wall</a>
            <a href="#">Groups</a>
          </nav>
          <div className="x-home-user-ops">
            <span className="x-home-welcome">Welcome, {me.nickname}</span>
            <button
              className="x-btn x-btn-quiet x-home-logout"
              onClick={async () => {
                await logout();
                nav("/auth", { replace: true });
              }}
            >
              Logout
            </button>
          </div>
        </div>
      </header>

      <main className="x-main x-home-main">
        <div className="x-home-layout">
          <aside className="x-card x-home-card">
            <div className="x-home-section-title">Profile</div>
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
                    <th>Status</th>
                    <td>{statusText}</td>
                  </tr>
                  <tr>
                    <th>Email</th>
                    <td>{me.email || "Not set"}</td>
                  </tr>
                  <tr>
                    <th>Phone</th>
                    <td>{me.phone || "Private"}</td>
                  </tr>
                  <tr>
                    <th>Trust</th>
                    <td>{tlLabel(me.trust_level)}</td>
                  </tr>
                </tbody>
              </table>
              <div className="x-home-links">
                <a href="#">Post status</a>
                <a href="#">Edit profile</a>
                <a href="#">Manage albums</a>
              </div>
            </div>
          </aside>

          <section className="x-home-center">
            <article className="x-card x-home-card">
              <div className="x-home-section-title">Wall</div>
              <div className="x-card-body x-home-compact">
                <ul className="x-home-feed">
                  {messages.map((item) => (
                    <li key={`${item.fromNickname}-${item.timeText}-${item.content}`}>
                      <strong>{item.fromNickname}</strong>: {item.content}
                      <a href="#">{item.actionText}</a>
                      <span>{item.timeText}</span>
                    </li>
                  ))}
                  {messages.length === 0 && <li className="x-muted">No new posts yet.</li>}
                </ul>
              </div>
            </article>

            <article className="x-card x-home-card">
              <div className="x-home-section-title">Friend Updates</div>
              <div className="x-card-body x-home-compact">
                <ul className="x-home-feed">
                  {activities.map((item) => (
                    <li key={`${item.actorNickname}-${item.timeText}-${item.content}`}>
                      <strong>{item.actorNickname}</strong> {item.content}
                      <a href="#">{item.actionText}</a>
                      <span>{item.timeText}</span>
                    </li>
                  ))}
                  {activities.length === 0 && <li className="x-muted">Friends are quiet for now.</li>}
                </ul>
              </div>
            </article>

            <article className="x-card x-home-card">
              <div className="x-home-section-title">Album Thumbnails</div>
              <div className="x-card-body x-home-compact">
                <div className="x-home-gallery">
                  {albums.map((item) => (
                    <a href="#" key={`${item.title}-${item.marker}`} className="x-home-thumb">
                      <div className="x-home-thumb-img">{item.marker}</div>
                      <div className="x-home-thumb-title">{item.title}</div>
                    </a>
                  ))}
                </div>
                {albums.length === 0 && <div className="x-muted">No albums uploaded yet.</div>}
              </div>
            </article>
          </section>

          <aside className="x-home-right">
            <article className="x-card x-home-card">
              <div className="x-home-section-title">Visitors</div>
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
                  {visitors.length === 0 && <li className="x-muted">No visitor records yet.</li>}
                </ul>
              </div>
            </article>

            <article className="x-card x-home-card">
              <div className="x-home-section-title">My Spaces</div>
              <div className="x-card-body x-home-compact">
                {spaces.length === 0 ? (
                  <div className="x-muted">You have not joined any space yet.</div>
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
              <div className="x-home-section-title">Today Widgets</div>
              <div className="x-card-body x-home-compact x-home-widget">
                {widgets.map((item) => (
                  <p key={item.title}>
                    <strong>{item.title}:</strong> {item.content}
                  </p>
                ))}
                {widgets.length === 0 && <p className="x-muted">No widget content available.</p>}
              </div>
            </article>
          </aside>
        </div>
      </main>
    </div>
  );
}
