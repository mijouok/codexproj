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

  const school = "江城大学";
  const department = "计算机与信息工程学院";
  const albumItems = ["操场日落", "图书馆角落", "宿舍夜聊", "校园小路", "社团海报", "期末周"];
  const visitors = [
    { name: "阿泽", note: "来踩一脚", time: "今天 11:42" },
    { name: "小鱼", note: "顺路看看你", time: "今天 10:18" },
    { name: "梨子", note: "刚路过主页", time: "昨天 23:07" },
    { name: "Momo", note: "给你留言啦", time: "昨天 20:31" }
  ];

  return (
    <div className="x-page">
      <header className="x-topbar x-home-topbar">
        <div className="x-topbar-inner x-home-topbar-inner">
          <div className="x-home-brand-wrap">
            <div className="x-brand x-home-brand">校内网</div>
            <div className="x-home-brand-sub">青春不散场</div>
          </div>
          <nav className="x-home-nav">
            <a href="#">主页</a>
            <a href="#">好友</a>
            <a href="#">相册</a>
            <a href="#">留言</a>
            <a href="#">小组</a>
          </nav>
          <div className="x-home-user-ops">
            <span className="x-home-welcome">欢迎你，{me.nickname}</span>
            <button
              className="x-btn x-btn-quiet x-home-logout"
              onClick={async () => {
                await logout();
                nav("/auth", { replace: true });
              }}
            >
              退出
            </button>
          </div>
        </div>
      </header>

      <main className="x-main x-home-main">
        <div className="x-home-layout">
          <aside className="x-card x-home-card">
            <div className="x-home-section-title">个人档案</div>
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
                    <th>状态</th>
                    <td>最近在忙期末，图书馆常驻中。</td>
                  </tr>
                  <tr>
                    <th>邮箱</th>
                    <td>{me.email || "还没填写"}</td>
                  </tr>
                  <tr>
                    <th>手机号</th>
                    <td>{me.phone || "暂未公开"}</td>
                  </tr>
                  <tr>
                    <th>信任等级</th>
                    <td>{tlLabel(me.trust_level)}</td>
                  </tr>
                </tbody>
              </table>
              <div className="x-home-links">
                <a href="#">写新状态</a>
                <a href="#">编辑资料</a>
                <a href="#">管理相册</a>
              </div>
            </div>
          </aside>

          <section className="x-home-center">
            <article className="x-card x-home-card">
              <div className="x-home-section-title">留言板</div>
              <div className="x-card-body x-home-compact">
                <ul className="x-home-feed">
                  <li>
                    <strong>小楠</strong>：明天早点去占座呀，复习资料我带过去。
                    <a href="#">回复</a>
                    <span>今天 12:02</span>
                  </li>
                  <li>
                    <strong>阿泽</strong>：你上次拍的操场晚霞太绝了，记得发原图！
                    <a href="#">回复</a>
                    <span>今天 09:47</span>
                  </li>
                  <li>
                    <strong>团团</strong>：来踩一脚，祝你这周小测稳稳过。
                    <a href="#">回复</a>
                    <span>昨天 22:16</span>
                  </li>
                </ul>
              </div>
            </article>

            <article className="x-card x-home-card">
              <div className="x-home-section-title">好友动态</div>
              <div className="x-card-body x-home-compact">
                <ul className="x-home-feed">
                  <li>
                    <strong>林同学</strong> 刚上传了新相册《春天的操场》
                    <a href="#">去看看</a>
                    <span>5 分钟前</span>
                  </li>
                  <li>
                    <strong>阿苗</strong> 更新状态：食堂二楼今天的糖醋排骨好评。
                    <a href="#">回复</a>
                    <span>38 分钟前</span>
                  </li>
                  <li>
                    <strong>小雨</strong> 在留言板给你留言：期末后一起拍毕业照！
                    <a href="#">查看</a>
                    <span>今天 08:21</span>
                  </li>
                </ul>
              </div>
            </article>

            <article className="x-card x-home-card">
              <div className="x-home-section-title">相册缩略图</div>
              <div className="x-card-body x-home-compact">
                <div className="x-home-gallery">
                  {albumItems.map((name, idx) => (
                    <a href="#" key={name} className="x-home-thumb">
                      <div className="x-home-thumb-img">{idx + 1}</div>
                      <div className="x-home-thumb-title">{name}</div>
                    </a>
                  ))}
                </div>
              </div>
            </article>
          </section>

          <aside className="x-home-right">
            <article className="x-card x-home-card">
              <div className="x-home-section-title">来访记录</div>
              <div className="x-card-body x-home-compact">
                <ul className="x-home-visitor-list">
                  {visitors.map((v) => (
                    <li key={`${v.name}-${v.time}`}>
                      <div className="x-home-visitor-avatar">{v.name[0]}</div>
                      <div>
                        <div>
                          <strong>{v.name}</strong>
                          <span className="x-home-visitor-note">{v.note}</span>
                        </div>
                        <div className="x-muted">{v.time}</div>
                      </div>
                    </li>
                  ))}
                </ul>
              </div>
            </article>

            <article className="x-card x-home-card">
              <div className="x-home-section-title">我的空间</div>
              <div className="x-card-body x-home-compact">
                {(me.spaces?.length ?? 0) === 0 ? (
                  <div className="x-muted">你还没有加入任何空间。</div>
                ) : (
                  <ul className="x-list x-home-list-tight">
                    {me.spaces.map((s) => (
                      <li key={s.id}>
                        {s.name} <span className="x-muted">({s.membership_status})</span>
                      </li>
                    ))}
                  </ul>
                )}
              </div>
            </article>

            <article className="x-card x-home-card">
              <div className="x-home-section-title">今日小组件</div>
              <div className="x-card-body x-home-compact x-home-widget">
                <p>
                  <strong>心情天气：</strong> 多云转晴，适合晚自习后散步。
                </p>
                <p>
                  <strong>最近在听：</strong> 《稻香》循环第 27 次。
                </p>
                <p>
                  <strong>宿舍公告：</strong> 周六晚上记得交水电费。
                </p>
              </div>
            </article>
          </aside>
        </div>
      </main>
    </div>
  );
}
