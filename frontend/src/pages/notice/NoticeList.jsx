import { useNavigate } from "react-router-dom";
import styles from "./Notice.module.css";
import { noticePosts } from "../../data/notice.data";

export default function NoticeList() {
  const navigate = useNavigate();

  return (
    <div className="page-wrapper">
      <div className={styles.list}>
        {noticePosts.map((post) => (
          <div
            key={post.id}
            className={styles.card}
            onClick={() => navigate(`/notice/${post.id}`)}
          >
            <h3>{post.title}</h3>
            <p className={styles.meta}>
              {post.author} · {post.createdAt}
            </p>
          </div>
        ))}
      </div>
    </div>
  );
}
