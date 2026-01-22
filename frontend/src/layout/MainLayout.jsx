import { Outlet, Link } from "react-router-dom";
import "./MainLayout.css";

export default function MainLayout() {
  return (
    <div className="layout-wrapper">
      <header className="header">
        <h1 className="logo">READSYNC</h1>
      </header>

      <main className="main-content">
        <Outlet />
      </main>

      <footer className="bottom-nav">
        <Link to="/community">💬 Community</Link>
        <Link to="/inquiry">❓ Inquiry</Link>
        <Link to="/notice">📢 Notice</Link>
      </footer>
    </div>
  );
}
