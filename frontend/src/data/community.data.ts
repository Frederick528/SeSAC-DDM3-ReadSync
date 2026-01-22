export interface CommunityPost {
  id: number;
  title: string;
  author: string;
  content: string;
  likes: number;
  comments: number;
  createdAt: string;
}

export const communityPosts: CommunityPost[] = [
  {
    id: 1,
    title: "오늘 읽은 문장 공유합니다 📖",
    author: "leafReader",
    content: "문장은 사람의 생각을 확장시킨다.",
    likes: 12,
    comments: 3,
    createdAt: "2026-01-18",
  },
  {
    id: 2,
    title: "이 책 같이 읽으실 분?",
    author: "forest",
    content: "비문학 독서룸 열 예정입니다.",
    likes: 8,
    comments: 5,
    createdAt: "2026-01-17",
  },
  {
    id: 3,
    title: "AI 요약 기능 후기",
    author: "syncMaster",
    content: "생각보다 정확해서 놀랐어요.",
    likes: 15,
    comments: 6,
    createdAt: "2026-01-16",
  },
  {
    id: 4,
    title: "독서 습관 만들기 팁",
    author: "reader01",
    content: "매일 10분부터 시작하세요.",
    likes: 20,
    comments: 9,
    createdAt: "2026-01-15",
  },
  {
    id: 5,
    title: "추천 도서 리스트 공유",
    author: "bookTree",
    content: "자기계발서 위주입니다.",
    likes: 7,
    comments: 2,
    createdAt: "2026-01-14",
  },
];
