import { Outlet, Link } from "react-router-dom";
import "./MainLayout.css";

export default function MainLayout() {
  return (
    <div className="layout-wrapper">
      {/* ===== 상단 헤더 (로고만) ===== */}
      <header className="header">
        <h1 className="logo">READSYNC</h1>
      </header>

      {/* ===== 페이지 콘텐츠 ===== */}
      <main className="main-content">
        <Outlet />
      </main>

      {/* ===== 하단 네비 (유일한 네비) ===== */}
      <footer className="bottom-nav">
        <Link to="/community">💬 Community</Link>
        <Link to="/inquiry">❓ Inquiry</Link>
        <Link to="/notice">📢 Notice</Link>
      </footer>
    </div>
  );
}
