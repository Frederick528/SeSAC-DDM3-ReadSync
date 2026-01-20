import React, { useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import "./Community.css";

export default function CommunityDetail() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [comments, setComments] = useState([
    {
      id: 1,
      author: "책읽는마법사",
      text: "이 책 진짜 추천합니다!",
      mine: false,
    },
    {
      id: 2,
      author: "나",
      text: "웃기시네~ 어디서 약을 팔아!!!",
      mine: true,
    },
  ]);

  const [input, setInput] = useState("");

  const submitComment = () => {
    if (!input.trim()) return;

    setComments([
      ...comments,
      {
        id: Date.now(),
        author: "나",
        text: input,
        mine: true,
      },
    ]);
    setInput("");
  };

  return (
    <div className="community-page">
      {/* 게시글 카드 */}
      <div className="post-detail-card">
        <h2>게시글 제목 {id}</h2>
        <div className="post-detail-meta">작성자 · 2026-01-10</div>

        <p className="post-content">
          이곳은 게시글 상세 내용입니다.  
          독서 경험과 생각을 자유롭게 나눠보세요.
        </p>

        <button className="back-btn" onClick={() => navigate(-1)}>
          ← 목록으로
        </button>
      </div>

      {/* 댓글 영역 */}
      <div className="chat-section">
        <div className="chat-title">💬 댓글</div>

        <div className="chat-box">
          {comments.map((c) => (
            <div
              key={c.id}
              className={`chat-line ${c.mine ? "mine" : "other"}`}
            >
              {!c.mine && <div className="chat-avatar">🧙‍♂️</div>}

              <div className="chat-bubble">
                {!c.mine && (
                  <div className="chat-author">{c.author}</div>
                )}
                <div>{c.text}</div>
              </div>

              {c.mine && <div className="chat-avatar">🙂</div>}
            </div>
          ))}
        </div>

        {/* 입력창 */}
        <div className="chat-input">
          <input
            value={input}
            onChange={(e) => setInput(e.target.value)}
            placeholder="댓글을 입력하세요..."
          />
          <button onClick={submitComment}>전송</button>
        </div>
      </div>
    </div>
  );
}
