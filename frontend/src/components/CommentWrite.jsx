import { useState } from "react";
import { createComment } from "../api/communityApi";


export default function CommentWrite({ postId }) {
const [content, setContent] = useState("");


const submit = () => {
createComment(postId, { content, userId: 1 }).then(() => setContent(""));
};


return (
<div className="mt-4 flex gap-2">
<input value={content} onChange={e => setContent(e.target.value)} className="border flex-1 p-2" />
<button onClick={submit} className="bg-emerald-500 text-white px-4">등록</button>
</div>
);
}