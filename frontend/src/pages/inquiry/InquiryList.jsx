import { useNavigate } from "react-router-dom";
import styles from "./Inquiry.module.css";
import { inquiryPosts } from "../../data/inquiry.data";

export default function InquiryList() {
  const navigate = useNavigate();

  return (
    <div className="page-wrapper">
      <div className={styles.list}>
        {inquiryPosts.map((post) => (
          <div
            key={post.id}
            className={styles.card}
            onClick={() => navigate(`/inquiry/${post.id}`)}
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
