/* 0. 기존 데이터 전체 삭제 및 ID 초기화 (연관된 모든 데이터 삭제) */
TRUNCATE TABLE
    "notices", "content_reports", "libraries", "bookmarks", "books",
    "likes", "credits", "inquiries", "friendships", "room_participants",
    "exp_logs", "reports", "levels", "carts", "book_ai_chats",
    "reviews", "comments", "reading_rooms", "order_items", "exp_rules",
    "users", "subscriptions", "user_informations", "orders", "inquiry_answers",
    "community_posts", "categories", "book_logs", "chapters", "blacklists",
    "payment_history", "credit_type", "community_comments", "chat_logs",
    "book_ai_chat_rooms", "room_invitations", "payment_methods", "refresh_tokens",
    "user_vectors", "book_vectors", "chapter_vectors"
    RESTART IDENTITY CASCADE;

-- [1] Levels (기초 레벨 정보)
INSERT INTO "levels" ("required_exp", "max_comment_limit", "can_upload_image") VALUES
                                                                                   (0, 30, FALSE),
                                                                                   (1000, 50, FALSE),
                                                                                   (5000, 100, TRUE);

-- [2] Categories (도서/활동 카테고리)
INSERT INTO "categories" ("category_name") VALUES
                                               ('소설'),
                                               ('IT/과학'),
                                               ('인문');

-- [3] Exp Rules (경험치 획득 규칙 - Category 참조)
INSERT INTO "exp_rules" ("activity_type", "exp", "category_id") VALUES
                                                                    ('DAILY_ATTENDANCE', 10, NULL),
                                                                    ('READ_BOOK', 50, 1),
                                                                    ('WRITE_REVIEW', 100, NULL);

-- [4] Users (사용자 기본 정보)
-- ★★★ 수정됨: status 컬럼 추가 ('ACTIVE'로 초기화) ★★★
INSERT INTO "users" ("role", "provider", "provider_id", "login_id", "password", "status") VALUES
                                                                                              ('ADMIN', 'LOCAL', 'admin_01', 'admin', 'pass_admin', 'ACTIVE'),
                                                                                              ('USER', 'GOOGLE', 'google_123', 'user_a', 'pass_user_a', 'ACTIVE'),
                                                                                              ('USER', 'KAKAO', 'kakao_456', 'user_b', 'pass_user_b', 'ACTIVE'),
                                                                                              ('USER', 'NAVER', 'naver_789', 'user_c', 'pass_user_c', 'ACTIVE');

-- [5] User Informations (사용자 상세 정보 - User, Level 참조)
INSERT INTO "user_informations" ("preferred_genre", "experience", "level_id", "user_id", "user_name", "tag") VALUES
                                                                                                                 ('SF/판타지', 150, 1, 1, '관리자', to_char(floor(random() * 10000), 'fm0000')),
                                                                                                                 ('기술 서적', 1200, 2, 2, '김철수', to_char(floor(random() * 10000), 'fm0000')),
                                                                                                                 ('고전 소설', 0, 1, 3, '이영희', to_char(floor(random() * 10000), 'fm0000')),
                                                                                                                 ('로맨스', 300, 1, 4, '박민수', to_char(floor(random() * 10000), 'fm0000'));

-- [6] User Vectors (사용자 벡터 - User 참조)
INSERT INTO "user_vectors" ("user_id", "vector", "created_at") VALUES
                                                     (1, array_fill(0, ARRAY[1024])::halfvec(1024), now()),
                                                     (2, array_fill(0, ARRAY[1024])::halfvec(1024), now()),
                                                     (3, array_fill(0, ARRAY[1024])::halfvec(1024), now()),
                                                     (4, array_fill(0, ARRAY[1024])::halfvec(1024), now());

-- [7] Refresh Tokens (인증 토큰 - User 참조)
INSERT INTO "refresh_tokens" ("user_id", "token") VALUES
                                                      (1, 'ref_token_admin'),
                                                      (2, 'ref_token_user_a'),
                                                      (3, 'ref_token_user_b'),
                                                      (4, 'ref_token_user_c');

-- [8] Blacklists (차단 내역 - User 참조)
INSERT INTO "blacklists" ("user_id", "type", "reason", "end_date", "is_active") VALUES
                                                                                    (3, 'SITE_BAN', '운영 정책 위반(욕설)', now() + interval '7 days', TRUE),
                                                                                    (4, 'SITE_BAN', '도배성 댓글', now() + interval '1 days', FALSE),
                                                                                    (2, 'SITE_BAN', '결제 오류 악용', now() + interval '3 days', TRUE);

-- [9] Books (도서 정보 - Category 참조)
INSERT INTO "books" ("category_id", "title", "author", "summary", "price", "language") VALUES
                                                                                           (1, '데이터베이스의 이해', '강작가', 'SQL과 DB 설계의 기초', 25000, 'KOREAN'),
                                                                                           (2, '자바 프로그래밍 마스터', '이코딩', '실전 예제로 배우는 Java', 32000, 'KOREAN'),
                                                                                           (1, 'AI와 미래 사회', '박지능', '인공지능이 바꿀 미래', 18000, 'KOREAN');

-- [10] Book Vectors (도서 벡터 - Book 참조)
INSERT INTO "book_vectors" ("book_id", "vector", "created_at") VALUES
                                                     (1, array_fill(0, ARRAY[1024])::halfvec(1024), now()),
                                                     (2, array_fill(0, ARRAY[1024])::halfvec(1024), now()),
                                                     (3, array_fill(0, ARRAY[1024])::halfvec(1024), now());

-- [11] Chapters (챕터 정보 - Book 참조)
INSERT INTO "chapters" ("book_id", "chapter_name", "sequence", "book_content_path", paragraphs) VALUES
                                                                                        (1, '1장: 관계형 모델', 1, 'https://drive.google.com/file/d/16LgsOHk6FwihTrLuLVH_5Cqx8Q_KyOU5/view?usp=drive_link', -1),
                                                                                        (1, '2장: 정규화', 2, '/path/db_ch2', -1),
                                                                                        (2, '1장: 자바 입문', 1, '/path/java_ch1', -1),
                                                                                        (2, '2장: 객체지향', 2, '/path/java_ch2', -1),
                                                                                        (3, '1장: AI의 역사', 1, '/path/ai_ch1', -1);

-- [12] Chapter Vectors (챕터 벡터 - Chapter 참조)
INSERT INTO "chapter_vectors" ("chapter_id", "vector", "created_at") VALUES
                                                           (1, array_fill(0, ARRAY[1024])::halfvec(1024), now()),
                                                           (2, array_fill(0, ARRAY[1024])::halfvec(1024), now()),
                                                           (3, array_fill(0, ARRAY[1024])::halfvec(1024), now()),
                                                           (4, array_fill(0, ARRAY[1024])::halfvec(1024), now()),
                                                           (5, array_fill(0, ARRAY[1024])::halfvec(1024), now());

-- [13] Libraries (서재/소장 - User, Book 참조)
INSERT INTO "libraries" ("user_id", "book_id", "type", "reading_status") VALUES
                                                                             (2, 1, 'OWNED', 'READING'),
                                                                             (2, 2, 'OWNED', 'COMPLETED'),
                                                                             (3, 1, 'OWNED', 'READING'),
                                                                             (4, 3, 'OWNED', 'READING');

-- [14] Bookmarks (북마크/진척도 - Chapter, Library 참조)
INSERT INTO "bookmarks" ("chapter_id", "last_read_pos", "progress", "library_id", "read_mask") VALUES
                                                                                                   (1, 6, 50.0, 1, '\x11111110000000'),
                                                                                                   (3, 0, 0.0, 2, '\x00000000000000'),
                                                                                                   (5, 0, 0.0, 4, '\x00000000000000');

-- [15] Book Logs (독서 로그 - Library 참조)
INSERT INTO "book_logs" ("read_paragraph", "read_time", "library_id") VALUES
                                                                          (5, 100, 1),
                                                                          (0, 0, 2),
                                                                          (0, 0, 4);

-- [16] Credit Type (재화 유형 - User 참조)
INSERT INTO "credit_type" ("base_expiry_days", "credit_name", "user_id") VALUES
                                                                             (30, '이벤트 포인트', 1),
                                                                             (365, '유료 크레딧', 1),
                                                                             (90, '보상 포인트', 1);

-- [17] Credits (재화 이력 - Credit Type 참조)
INSERT INTO "credits" ("credits", "created_at", "credit_type", "status") VALUES
                                                                             (1000, now(), 1, 'ACTIVE'),
                                                                             (5000, now(), 2, 'ACTIVE'),
                                                                             (500, now(), 1, 'ACTIVE');

-- [18] Payment Methods (결제 수단 - User 참조)
INSERT INTO "payment_methods" ("user_id", "billing_key", "pg_provider", "card_company", "card_last_4", "created_at") VALUES
                                                                                                                         (2, 'bill_key_user2', 'TOSS', 'SHINHAN', '1234', now()),
                                                                                                                         (3, 'bill_key_user3', 'KAKAO', 'KOOKMIN', '5678', now()),
                                                                                                                         (4, 'bill_key_user4', 'NAVER', 'HYUNDAI', '9012', now());

-- [19] Subscriptions (구독 정보 - User 참조)
INSERT INTO "subscriptions" ("plan_name", "price", "status", "next_billing_date", "user_id") VALUES
                                                                                                 ('프리미엄 독서', 9900, 'ACTIVE', now() + interval '1 month', 2),
                                                                                                 ('베이직 요금제', 5900, 'EXPIRED', now() - interval '1 day', 3),
                                                                                                 ('프리미엄 독서', 9900, 'ACTIVE', now() + interval '15 days', 4);

-- [20] Carts (장바구니 - User, Book 참조)
INSERT INTO "carts" ("user_id", "book_id", "quantity") VALUES
                                                           (2, 3, 1),
                                                           (3, 2, 1),
                                                           (4, 1, 1);

-- [21] Orders (주문 내역 - User, Payment Method, Subscription, Cart 참조)
INSERT INTO "orders" ("user_id", "method_id", "order_uid", "order_name", "total_amount", "status") VALUES
                                                                                                       (2, 1, 'ORD-001', '데이터베이스의 이해 외', 25000, 'COMPLETED'),
                                                                                                       (3, 2, 'ORD-002', '자바 프로그래밍 마스터', 32000, 'COMPLETED'),
                                                                                                       (4, 3, 'ORD-003', 'AI와 미래 사회', 18000, 'PENDING');

-- [22] Order Items (주문 상세 상품 - Book, Order 참조)
INSERT INTO "order_items" ("book_id", "order_id", "snapshot_price", "quantity", "status") VALUES
                                                                                              (1, 1, 25000, 1, 'ORDER_COMPLETED'),
                                                                                              (2, 2, 32000, 1, 'ORDER_COMPLETED'),
                                                                                              (3, 3, 18000, 1, 'PARTIAL_CANCELED');

-- [23] Payment History (결제 이력 - Order 참조)
INSERT INTO "payment_history" ("pg_payment_key", "amount", "status", "pg_provider", "created_at", "order_id") VALUES
                                                                                                                  ('pg_key_1', 25000, 'DONE', 'TOSS', now(), 1),
                                                                                                                  ('pg_key_2', 32000, 'DONE', 'KAKAO', now(), 2),
                                                                                                                  ('pg_key_3', 18000, 'WAITING', 'NAVER', now(), 3);

-- [24] Community Posts (게시글 - User 참조)
INSERT INTO "community_posts" ("title", "content", "user_id") VALUES
                                                                  ('오늘의 추천도서', 'DB 책 꼭 보세요.', 2),
                                                                  ('같이 읽을 분 구해요', '주말 오전반 모집합니다.', 3),
                                                                  ('AI 관련 질문있습니다', '미래 사회 책 읽어보신 분?', 4);

-- [25] Community Comments (게시글 댓글 - User, Post 참조)
INSERT INTO "community_comments" ("content", "user_id", "post_id") VALUES
                                                                       ('감사합니다!', 3, 1),
                                                                       ('저 참여할게요.', 2, 2),
                                                                       ('저도 궁금하네요.', 2, 3);

-- [26] Notices (공지사항 - User(Admin) 참조)
INSERT INTO "notices" ("title", "content", "user_id") VALUES
                                                          ('서버 점검 안내', '새벽 2시부터 4시까지 점검입니다.', 1),
                                                          ('신규 가입 이벤트', '웰컴 포인트를 드립니다.', 1),
                                                          ('약관 변경 안내', '개인정보 처리방침이 변경됩니다.', 1);

-- [27] Inquiries (1:1 문의 - User 참조)
INSERT INTO "inquiries" ("title", "user_id", "content") VALUES
                                                            ('결제 취소 문의', 2, '실수로 잘못 결제했어요.'),
                                                            ('로그인이 안돼요', 3, '비밀번호 찾기가 안됩니다.'),
                                                            ('책 내용 오류 신고', 4, '오타를 발견했습니다.');

-- [28] Inquiry Answers (문의 답변 - User(Admin), Inquiry 참조)
INSERT INTO "inquiry_answers" ("content", "user_id", "inquiry_id") VALUES
                                                                       ('취소 처리 해드렸습니다.', 1, 1),
                                                                       ('임시 비밀번호를 발송했습니다.', 1, 2),
                                                                       ('소중한 제보 감사합니다.', 1, 3);

-- [29] Friendships (친구 관계 - User 참조)
INSERT INTO "friendships" ("status", "requester_id", "addressee_id") VALUES
                                                                         ('ACCEPTED', 2, 3),
                                                                         ('PENDING', 1, 2),
                                                                         ('ACCEPTED', 3, 4);

-- [30] Reading Rooms (독서실 - Host(User), Library 참조)
INSERT INTO "reading_rooms" ("host_id", "library_id", "room_name", "current_chapter") VALUES
                                                                                          (2, 1, '데이터베이스 빡독방', 1),
                                                                                          (3, 2, '자바 스터디', 1),
                                                                                          (4, 3, 'AI 독서 모임', 1);

-- [31] Room Participants (독서실 참여자 - Room, User 참조)
INSERT INTO "room_participants" ("room_id", "user_id", "connection_status") VALUES
                                                                                (1, 2, 'ACTIVE'),
                                                                                (1, 3, 'ACTIVE'),
                                                                                (2, 4, 'ACTIVE');

-- [32] Chat Logs (독서실 채팅 - Room, User 참조)
INSERT INTO "chat_logs" ("room_id", "user_id", "content") VALUES
                                                              (1, 2, '안녕하세요! 1장부터 읽으시죠.'),
                                                              (1, 3, '네 반갑습니다.'),
                                                              (2, 4, '혼자 하려니 심심하네요.');

-- [33] Room Invitations (독서실 초대 - Room, User 참조)
INSERT INTO "room_invitations" ("room_id", "status", "receiver_id", "sender_id") VALUES
                                                                                     (1, 'ACCEPTED', 3, 2),
                                                                                     (2, 'PENDING', 2, 3),
                                                                                     (1, 'REJECTED', 4, 2);

-- [34] Reports (채팅 신고 - User, ChatLog 참조)
INSERT INTO "reports" ("reporter_id", "chat_id", "reason") VALUES
                                                               (3, 1, '부적절한 대화 내용'),
                                                               (2, 3, '광고성 메시지'),
                                                               (4, 2, '욕설 사용');

-- [35] Book AI Chat Rooms (AI 채팅방 - User, Chapter 참조)
INSERT INTO "book_ai_chat_rooms" ("user_id", "chapter_id", "title") VALUES
                                                                        (2, 1, 'DB 정규화 질문'),
                                                                        (3, 3, '자바 객체지향 개념'),
                                                                        (4, 5, 'AI 윤리 문제');

-- [36] Book AI Chats (AI 대화 - AI Room, User 참조)
INSERT INTO "book_ai_chats" ("ai_room_id", "user_id", "user_msg", "ai_msg") VALUES
                                                                                (1, 2, '제2정규형이 뭐야?', '부분 함수 종속을 제거한 것입니다.'),
                                                                                (2, 3, '다형성이란?', '하나의 인터페이스로 다양한 구현을 갖는 것입니다.'),
                                                                                (3, 4, 'AI가 일자리를 뺏을까?', '일부 대체되겠지만 새로운 직업도 생길 것입니다.');

-- [37] Reviews (도서 리뷰 - User, Book 참조)
INSERT INTO "reviews" ("user_id", "book_id", "rating", "review_content", "visibility_status") VALUES
                                                                                                  (2, 1, 5, '최고의 DB 입문서!', 'ACTIVE'),
                                                                                                  (3, 1, 4, '내용은 좋은데 조금 어렵네요.', 'ACTIVE'),
                                                                                                  (4, 2, 3, '예제가 좀 옛날 방식인 듯.', 'ACTIVE');

-- [38] Comments (챕터 코멘트 - User, Chapter 참조)
INSERT INTO "comments" ("user_id", "chapter_id", "comment_content", "visibility_status") VALUES
                                                                                             (2, 1, '이 부분 설명이 아주 좋습니다.', 'ACTIVE'),
                                                                                             (3, 2, '이해가 잘 안되네요.', 'ACTIVE'),
                                                                                             (4, 3, '코드 따라 치기 좋아요.', 'ACTIVE');

-- [39] Likes (좋아요 - Review, User 참조)
INSERT INTO "likes" ("review_id", "user_id", "reaction_type") VALUES
                                                                  (1, 3, 'LIKE'),
                                                                  (2, 2, 'LIKE'),
                                                                  (3, 4, 'DISLIKE');

-- [40] Content Reports (콘텐츠 신고 - Review, User 참조)
INSERT INTO "content_reports" ("review_id", "user_id", "target_type", "reason_type") VALUES
                                                                                         (2, 3, 'REVIEW', 'SPOILER'),
                                                                                         (1, 4, 'REVIEW', 'AD'),
                                                                                         (3, 2, 'REVIEW', 'ABUSE');

-- [41] Exp Logs (경험치 로그 - User, Exp Rule 참조)
INSERT INTO "exp_logs" ("user_id", "exp_rule_id", "earned_exp", "target_id", "reference_id") VALUES
                                                                                                 (2, 1, 10, 0, 0), -- 로그인
                                                                                                 (2, 3, 100, 1, 1), -- 리뷰 작성
                                                                                                 (3, 2, 50, 1, 1); -- 챕터 읽기