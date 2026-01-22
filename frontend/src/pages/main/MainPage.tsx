import "./MainPage.css";

export default function MainPage() {
  const level = 23; // 🔥 여기 숫자만 바꾸면 됨

  const treeStage = Math.floor(level / 10); // 0~n

  return (
    <div className="main-wrapper">
      <h2 className="main-title">📖 ReadSync Main</h2>
      <p className="main-subtitle">
        오늘의 독서 기록과 활동을 한눈에 확인하세요
      </p>

      <div className="main-grid">
        {/* 🌳 성장의 나무 (가운데 강조) */}
        <section className="main-card large tree-center">
          <h3>🌳 성장의 나무</h3>
          <p className="card-desc">
            이번 달 독서 활동을 기반으로 성장 중입니다
          </p>

          <div className="tree-visual-wrapper">
            <svg
              className={`tree-svg tree-stage-${treeStage}`}
              viewBox="0 0 300 360"
              xmlns="http://www.w3.org/2000/svg"
            >
              {/* 줄기 */}
              <rect x="130" y="180" width="40" height="120" rx="10" />

              {/* 잎 */}
              <circle cx="150" cy="110" r="90" />
              <circle cx="90" cy="150" r="60" />
              <circle cx="210" cy="150" r="60" />
            </svg>

            <div className="tree-level-text">Lv. {level}</div>
            <span className="tree-text">
              다음 레벨까지 {30 - (level % 10)}권 남았어요
            </span>
          </div>
        </section>

        {/* 공지 */}
        <section className="main-card">
          <h3>📢 공지 요약</h3>
          <ul className="card-list">
            <li>신규 독서룸 기능 오픈</li>
            <li>AI 요약 정확도 개선</li>
            <li>1월 시스템 점검 안내</li>
          </ul>
        </section>

        {/* 추천 */}
        <section className="main-card">
          <h3>📚 추천 도서</h3>
          <p className="highlight">『아주 작은 습관의 힘』</p>
          <span className="card-desc">
            형윤님 독서 패턴 기반 추천
          </span>
        </section>

        {/* 문의 */}
        <section className="main-card">
          <h3>❓ 문의 현황</h3>
          <p className="highlight">진행중 1건</p>
          <span className="card-desc">
            최근 문의에 답변이 등록되었습니다
          </span>
        </section>
      </div>
    </div>
  );
}
