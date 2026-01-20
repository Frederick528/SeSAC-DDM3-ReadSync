import React from "react";
import { useNavigate } from "react-router-dom";
import "./Community.css";

export default function CommunityList() {
  const navigate = useNavigate();

  const posts = [
    {
      id: 1,
      title: "요즘 읽기 좋은 책 추천해요",
      author: "책읽는마법사",
      date: "2026-01-10",
    },
    {
      id: 2,
      title: "집중 안 될 때 독서 팁",
      author: "고독한전사",
      date: "2026-01-09",
    },
    {
      id: 3,
      title: "인생 책 한 권 추천",
      author: "현자",
      date: "2026-01-08",
    },
    {
      id: 4,
      title: "독서 습관 만드는 방법",
      author: "초보모험가",
      date: "2026-01-07",
    },
    {
      id: 5,
      title: "전자책 vs 종이책",
      author: "북덕후",
      date: "2026-01-06",
    },
  ];

  return (
    <div className="community-page">
      {/* 상단 타이틀 */}
      <div className="community-title">
        📢 커뮤니티
      </div>

      {/* 캐릭터 영역 */}
      <div className="community-character">
        <div className="character-icon">🧙‍♂️</div>
        <div className="character-text">
          독서에 대한 이야기를 자유롭게 나눠보세요.
        </div>
      </div>

      {/* 게시글 리스트 */}
      <div className="post-list">
        {posts.map((post) => (
          <div
            key={post.id}
            className="post-card"
            onClick={() => navigate(`/community/${post.id}`)}
          >
            <div className="post-title">{post.title}</div>
            <div className="post-meta">
              <span>{post.author}</span>
              <span>{post.date}</span>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
