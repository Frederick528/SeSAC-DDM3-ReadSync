import { useParams, useNavigate } from "react-router-dom";
import { communityPosts } from "../../data/community.data";
import styles from "./Community.module.css";

export default function CommunityDetail() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const post = communityPosts.find(
    (p) => p.id === Number(id)
  );

  if (!post) return <div>게시글이 없습니다.</div>;

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
