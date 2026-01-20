import { useParams, Link } from "react-router-dom";

export default function NoticeDetail() {
  const { id } = useParams();

  return (
    <div style={{ padding: 24 }}>
      <h2>공지 #{id}</h2>
      <p>서비스 점검 예정입니다.</p>

      <Link to="/notice">목록으로</Link>
    </div>
  );
}
