import { useParams, useNavigate } from "react-router-dom";
import { noticePosts } from "../../data/notice.data";
import styles from "./Notice.module.css";

export default function NoticeDetail() {
  const { id } = useParams();
  const navigate = useNavigate();

  const post = noticePosts.find(
    (p) => p.id === Number(id)
  );

  if (!post) return <div>공지사항이 없습니다.</div>;

  return (
    <div className="page-wrapper">
      <div className={styles.detail}>
        <button onClick={() => navigate(-1)}>← 목록</button>
        <h2>{post.title}</h2>
        <p className={styles.meta}>
          {post.author} · {post.createdAt}
        </p>
        <p>{post.content}</p>
      </div>
    </div>
  );
}
