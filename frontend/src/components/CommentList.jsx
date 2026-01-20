import { useEffect, useState } from "react";
import { getComments } from "../api/communityApi";


export default function CommentList({ postId }) {
const [comments, setComments] = useState([]);


useEffect(() => {
getComments(postId).then(res => setComments(res.data));
}, [postId]);


return (
<div className="mt-6">
<h3 className="font-semibold mb-2">댓글</h3>
{comments.map(c => (
<div key={c.commentId} className="border-b py-2">
{c.content}
</div>
))}
</div>
);
}