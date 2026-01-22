import { useNavigate } from "react-router-dom";
import styles from "./Community.module.css";
import { communityPosts } from "../../data/community.data";

export default function CommunityList() {
  const navigate = useNavigate();

  return (
    <div className="page-wrapper">
      <div className={styles.list}>
        {communityPosts.map((post) => (
          <div
            key={post.id}
            className={styles.card}
            onClick={() => navigate(`/community/${post.id}`)}
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
