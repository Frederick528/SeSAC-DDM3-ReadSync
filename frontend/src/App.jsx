import React, { useState, useEffect } from "react";
import { Routes, Route, useNavigate } from "react-router-dom";
import "./styles/App.css";

/* 페이지 */
import CommunityList from "./pages/community/CommunityList";
import CommunityDetail from "./pages/community/CommunityDetail";

function App() {
  const navigate = useNavigate();

  // ================== 드래그 상태 ==================
  const [position, setPosition] = useState({ x: 0, y: 0 });
  const [isDragging, setIsDragging] = useState(false);
  const [offset, setOffset] = useState({ x: 0, y: 0 });

  const handleMouseDown = (e) => {
    if (e.target.closest(".nav-item")) return;

    setIsDragging(true);
    setOffset({
      x: e.clientX - position.x,
      y: e.clientY - position.y,
    });
  };

  useEffect(() => {
    const handleMouseMove = (e) => {
      if (!isDragging) return;
      setPosition({
        x: e.clientX - offset.x,
        y: e.clientY - offset.y,
      });
    };

    const handleMouseUp = () => setIsDragging(false);

    if (isDragging) {
      window.addEventListener("mousemove", handleMouseMove);
      window.addEventListener("mouseup", handleMouseUp);
    }

    return () => {
      window.removeEventListener("mousemove", handleMouseMove);
      window.removeEventListener("mouseup", handleMouseUp);
    };
  }, [isDragging, offset]);

  // ================== 실제 페이지 이동 ==================
  const navigateTo = (path) => {
    navigate(path);
  };

  return (
    <div className="container">
      {/* ===== 좌측 플로팅 ===== */}
      <aside className="floating-sidebar">
        <button>📅</button>
        <button>🔊</button>
        <button>💬</button>
        <button>👤</button>
        <button>⚙️</button>
      </aside>

      {/* ===== 헤더 ===== */}
      <header className="header">
        <div className="logo" onClick={() => navigateTo("/")}>
          READ<br />SYNC
        </div>

        <div className="center-nav">
          <nav className="nav-menu">
            <button onClick={() => navigateTo("/")}>Home</button>
            <button onClick={() => navigateTo("/")}>Library</button>
            <button onClick={() => navigateTo("/community")}>
              Community
            </button>
          </nav>
        </div>
      </header>

      {/* ===== 메인 영역 ===== */}
      <main className="hero-section">
        <div className="hero-character-container">
          <span style={{ fontSize: "120px" }}>🧙‍♂️</span>
        </div>

        {/* 드래그 카드 */}
        <section
          className="recent-books-card draggable"
          onMouseDown={handleMouseDown}
          style={{
            transform: `translate(${position.x}px, ${position.y}px)`,
            cursor: isDragging ? "grabbing" : "grab",
          }}
        >
          <div className="drag-handle">⠿ MOVE PANEL</div>

          <h3>RECENT BOOKS</h3>

          <div className="book-thumbnails">
            <button
              className="nav-item book-btn"
              onClick={() => navigateTo("/")}
            >
              <span className="book-icon">📖</span>
              <span className="label">LAST READ</span>
            </button>

            <button
              className="nav-item book-btn"
              onClick={() => navigateTo("/")}
            >
              <span className="book-icon">🏆</span>
              <span className="label">ACHIEVE</span>
            </button>
          </div>
        </section>
      </main>

      {/* ===== 라우터 ===== */}
      <Routes>
        <Route path="/community" element={<CommunityList />} />
        <Route path="/community/:id" element={<CommunityDetail />} />
      </Routes>

      {/* ===== 하단 메뉴 ===== */}
      <footer className="bottom-menu-bar">
        <button className="menu-btn">🏛️<br />전체 도서</button>
        <button className="menu-btn">🎒<br />내 서재</button>
        <button className="menu-btn">💡<br />도서 추천</button>
        <button
          className="menu-btn"
          onClick={() => navigateTo("/community")}
        >
          📢<br />커뮤니티
        </button>
      </footer>
    </div>
  );
}

export default App;
