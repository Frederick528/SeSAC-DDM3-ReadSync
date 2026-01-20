import { useNavigate } from "react-router-dom";

export default function InquiryList() {
  const navigate = useNavigate();

  return (
    <div className="page inquiry">
      <h2>❓ 문의 목록</h2>

      <div
        className="inquiry-item"
        onClick={() => navigate("/inquiry/1")}
      >
        결제가 안 됩니다
      </div>
    </div>
  );
}
