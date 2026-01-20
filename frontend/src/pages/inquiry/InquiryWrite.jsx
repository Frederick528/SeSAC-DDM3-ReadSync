import { Link } from "react-router-dom";

export default function InquiryWrite() {
  return (
    <div style={{ padding: 24 }}>
      <h2>문의 작성</h2>

      <input placeholder="제목" /><br /><br />
      <textarea placeholder="문의 내용" rows={5} /><br /><br />

      <button>등록</button>

      <br />
      <Link to="/inquiry">취소</Link>
    </div>
  );
}
