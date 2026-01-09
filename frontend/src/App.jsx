import React, { useState, useEffect } from 'react';
import './styles/App.css';

function App() {
    // 1. 위치 및 드래그 상태
    const [position, setPosition] = useState({ x: 0, y: 0 });
    const [isDragging, setIsDragging] = useState(false);
    const [offset, setOffset] = useState({ x: 0, y: 0 });

    // 드래그 시작 (핸들 부분을 잡았을 때만 작동하도록 설정 가능)
    const handleMouseDown = (e) => {
        // 버튼이나 링크를 클릭했을 때는 드래그가 시작되지 않도록 방지
        if (e.target.closest('.nav-item')) return;

        setIsDragging(true);
        setOffset({
            x: e.clientX - position.x,
            y: e.clientY - position.y
        });
    };

    useEffect(() => {
        const handleMouseMove = (e) => {
            if (!isDragging) return;
            setPosition({
                x: e.clientX - offset.x,
                y: e.clientY - offset.y
            });
        };

        const handleMouseUp = () => setIsDragging(false);

        if (isDragging) {
            window.addEventListener('mousemove', handleMouseMove);
            window.addEventListener('mouseup', handleMouseUp);
        }
        return () => {
            window.removeEventListener('mousemove', handleMouseMove);
            window.removeEventListener('mouseup', handleMouseUp);
        };
    }, [isDragging, offset]);

    // 2. 이동 함수
    const navigateTo = (pageName) => {
        alert(`[시스템] ${pageName} 페이지로 이동합니다.`);
        // 실제 구현 시: window.location.href = "/last-read" 또는 useNavigate() 사용
    };

    return (
        <div className="container">
            <aside className="floating-sidebar">

                <button>📅</button><button>🔊</button><button>💬</button><button>👤</button><button>⚙️</button>

            </aside>
            {/* 상단 헤더 (생략) */}
            <header className="header">
                <div className="logo">READ<br/>SYNC</div>
                <div className="center-nav">
                    <nav className="nav-menu">
                        <a href="#home" className="active">Home</a>
                        <a href="#library">Library</a>
                        <a href="#community">Community</a>
                    </nav>
                </div>
            </header>

            <main className="hero-section">
                <div className="hero-character-container">
                    <span style={{fontSize: '120px'}}>🧙‍♂️</span>
                </div>

                {/* --- 드래그 가능한 RECENT BOOKS 카드 --- */}
                <section
                    className="recent-books-card draggable"
                    onMouseDown={handleMouseDown}
                    style={{
                        transform: `translate(${position.x}px, ${position.y}px)`,
                        cursor: isDragging ? 'grabbing' : 'grab'
                    }}
                >
                    {/* 드래그 핸들 영역 */}
                    <div className="drag-handle">⠿ MOVE PANEL</div>

                    <h3>RECENT BOOKS</h3>

                    <div className="book-thumbnails">
                        {/* 버튼 1: 마지막 읽은 페이지 */}
                        <button
                            className="nav-item book-btn"
                            onClick={() => navigateTo('마지막으로 읽은 페이지')}
                        >
                            <span className="book-icon">📖</span>
                            <span className="label">LAST READ</span>
                        </button>

                        {/* 버튼 2: 업적 페이지 */}
                        <button
                            className="nav-item book-btn"
                            onClick={() => navigateTo('업적 페이지')}
                        >
                            <span className="book-icon">🏆</span>
                            <span className="label">ACHIEVE</span>
                        </button>
                    </div>
                </section>
            </main>

            {/* 하단 메뉴 바 */}
            <footer className="bottom-menu-bar">
                <button className="menu-btn" onClick={() => navigateTo('전체 도서')}>🏛️<br/>전체 도서</button>
                <button className="menu-btn" onClick={() => navigateTo('내 서재')}>🎒<br/>내 서재</button>
                <button className="menu-btn" onClick={() => navigateTo('도서 추천')}>💡<br/>도서 추천</button>
                <button className="menu-btn" onClick={() => navigateTo('커뮤니티')}>📢<br/>커뮤니티</button>
            </footer>
        </div>
    );
}

export default App;