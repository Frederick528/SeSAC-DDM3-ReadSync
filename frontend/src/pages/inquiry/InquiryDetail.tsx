import { useParams } from "react-router-dom";
import { useState } from "react";

export default function InquiryDetail() {
  const { id } = useParams();
  const [answer, setAnswer] = useState("");
  const [submitted, setSubmitted] = useState(false);

  return (
    <div className="page inquiry-detail">
      <h2>❓ 문의 상세 #{id}</h2>

      <div className="question-box">
        <strong>문의 내용</strong>
        <p>이 책 결제가 안 됩니다. 해결 방법이 있을까요?</p>
      </div>

      <div className="admin-answer">
        <strong>관리자 답변</strong>

        {submitted ? (
          <div className="answer-view">{answer}</div>
        ) : (
          <>
            <textarea
              value={answer}
              onChange={(e) => setAnswer(e.target.value)}
              placeholder="답변을 입력하세요"
            />
            <button onClick={() => setSubmitted(true)}>
              답변 등록
            </button>
          </>
        )}
      </div>
    </div>
  );
}
