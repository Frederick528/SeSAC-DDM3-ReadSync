import { useParams, useNavigate } from "react-router-dom";
import { inquiryPosts } from "../../data/inquiry.data";
import styles from "./Inquiry.module.css";

export default function InquiryDetail() {
  const { id } = useParams();
  const navigate = useNavigate();

  const post = inquiryPosts.find(
    (p) => p.id === Number(id)
  );

  if (!post) return <div>문의글이 없습니다.</div>;

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
