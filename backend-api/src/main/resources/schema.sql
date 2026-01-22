-- pgvector 확장 설치 (필수)
CREATE EXTENSION IF NOT EXISTS vector;

-- 0. 기존 테이블 삭제 (초기화용)
DROP TABLE IF EXISTS "notices" CASCADE;
DROP TABLE IF EXISTS "content_reports" CASCADE;
DROP TABLE IF EXISTS "libraries" CASCADE;
DROP TABLE IF EXISTS "user_books" CASCADE;
DROP TABLE IF EXISTS "bookmarks" CASCADE;
DROP TABLE IF EXISTS "books" CASCADE;
DROP TABLE IF EXISTS "likes" CASCADE;
DROP TABLE IF EXISTS "credits" CASCADE;
DROP TABLE IF EXISTS "inquiries" CASCADE;
DROP TABLE IF EXISTS "friendships" CASCADE;
DROP TABLE IF EXISTS "room_participants" CASCADE;
DROP TABLE IF EXISTS "exp_logs" CASCADE;
DROP TABLE IF EXISTS "reports" CASCADE;
DROP TABLE IF EXISTS "levels" CASCADE;
DROP TABLE IF EXISTS "carts" CASCADE;
DROP TABLE IF EXISTS "book_ai_chats" CASCADE;
DROP TABLE IF EXISTS "reviews" CASCADE;
DROP TABLE IF EXISTS "comments" CASCADE;
DROP TABLE IF EXISTS "reading_rooms" CASCADE;
DROP TABLE IF EXISTS "order_items" CASCADE;
DROP TABLE IF EXISTS "exp_rules" CASCADE;
DROP TABLE IF EXISTS "get_exp_rules" CASCADE;
DROP TABLE IF EXISTS "users" CASCADE;
DROP TABLE IF EXISTS "subscriptions" CASCADE;
DROP TABLE IF EXISTS "user_informations" CASCADE;
DROP TABLE IF EXISTS "orders" CASCADE;
DROP TABLE IF EXISTS "inquiry_answers" CASCADE;
DROP TABLE IF EXISTS "community_posts" CASCADE;
DROP TABLE IF EXISTS "categories" CASCADE;
DROP TABLE IF EXISTS "book_logs" CASCADE;
DROP TABLE IF EXISTS "chapters" CASCADE;
DROP TABLE IF EXISTS "blacklists" CASCADE;
DROP TABLE IF EXISTS "payment_history" CASCADE;
DROP TABLE IF EXISTS "credit_type" CASCADE;
DROP TABLE IF EXISTS "community_comments" CASCADE;
DROP TABLE IF EXISTS "chat_logs" CASCADE;
DROP TABLE IF EXISTS "book_ai_chat_rooms" CASCADE;
DROP TABLE IF EXISTS "room_invitations" CASCADE;
DROP TABLE IF EXISTS "payment_methods" CASCADE;
DROP TABLE IF EXISTS "refresh_tokens" CASCADE;
DROP TABLE IF EXISTS "user_vectors" CASCADE;
DROP TABLE IF EXISTS "book_vectors" CASCADE;
DROP TABLE IF EXISTS "chapter_vectors" CASCADE;

-- 1. Notices
CREATE TABLE "notices" (
    "notice_id" BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    "title" VARCHAR(100) NOT NULL,
    "content" TEXT NOT NULL,
    "created_at" TIMESTAMP DEFAULT now() NOT NULL,
    "updated_at" TIMESTAMP DEFAULT now() NOT NULL,
    "views" INT DEFAULT 0 NOT NULL,
    "user_id" BIGINT NOT NULL,
    CONSTRAINT "PK_NOTICES" PRIMARY KEY ("notice_id")
);

-- 2. Content Reports
CREATE TABLE "content_reports" (
    "report_id" BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    "comment_id" BIGINT NULL,
    "review_id" BIGINT NULL,
    "user_id" BIGINT NOT NULL,
    "target_type" VARCHAR(20) NOT NULL,
    "reason_type" VARCHAR(20) NOT NULL,
    "reason_detail" TEXT NULL,
    "process_status" VARCHAR(20) DEFAULT 'PENDING' NOT NULL,
    "created_at" TIMESTAMP DEFAULT Now() NOT NULL,
    CONSTRAINT "PK_CONTENT_REPORTS" PRIMARY KEY ("report_id")
);

-- 3. Libraries
CREATE TABLE "libraries" (
    "library_id" BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    "user_id" BIGINT NOT NULL,
    "book_id" BIGINT NOT NULL,
    "type" VARCHAR(20) NOT NULL,
    "total_progress" DECIMAL(5,2) DEFAULT 0.00 NOT NULL,
    "created_at" TIMESTAMP DEFAULT now() NOT NULL,
    "expires_at" TIMESTAMP NULL,
    "reading_status" VARCHAR(20) NOT NULL,
    "updated_at" TIMESTAMP DEFAULT now() NOT NULL,
    CONSTRAINT "PK_LIBRARIES" PRIMARY KEY ("library_id")
);

-- 4. Bookmarks
CREATE TABLE "bookmarks" (
    "bookmark_id" BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    "chapter_id" BIGINT NOT NULL,
    "last_read_pos" INT NOT NULL,
    "progress" DECIMAL(5,2) DEFAULT 0.00 NOT NULL,
    "library_id" BIGINT NOT NULL,
    "read_mask" BYTEA NOT NULL,
    "created_at" TIMESTAMP DEFAULT now() NOT NULL,
    "updated_at" TIMESTAMP DEFAULT now() NOT NULL,
    CONSTRAINT "PK_BOOKMARKS" PRIMARY KEY ("bookmark_id")
);

-- 5. Books
CREATE TABLE "books" (
    "book_id" BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    "category_id" BIGINT NOT NULL,
    "title" VARCHAR(255) NOT NULL,
    "author" VARCHAR(100) NOT NULL,
    "is_adult_only" BOOLEAN DEFAULT FALSE NOT NULL,
    "summary" TEXT NULL,
    "publisher" VARCHAR(100) NULL,
    "published_date" TIMESTAMP NULL,
    "cover_url" TEXT NULL,
    "view_permission" VARCHAR(20) DEFAULT 'FREE' NOT NULL,
    "price" DECIMAL(10,0) DEFAULT 0 NOT NULL,
    "language" VARCHAR(100) NOT NULL,
    "created_at" TIMESTAMP DEFAULT now() NOT NULL,
    "updated_at" TIMESTAMP DEFAULT now() NOT NULL,
    CONSTRAINT "PK_BOOKS" PRIMARY KEY ("book_id")
);

-- 6. Likes
CREATE TABLE "likes" (
    "like_id" BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    "comment_id" BIGINT NULL,
    "review_id" BIGINT NULL,
    "user_id" BIGINT NOT NULL,
    "reaction_type" VARCHAR(10) NOT NULL,
    "created_at" TIMESTAMP DEFAULT Now() NOT NULL,
    CONSTRAINT "PK_LIKES" PRIMARY KEY ("like_id")
);

-- 7. Credits
CREATE TABLE "credits" (
    "credits_id" BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    "credits" INT NOT NULL,
    "created_at" TIMESTAMP NOT NULL,
    "deleted_at" TIMESTAMP NULL,
    "credit_type" BIGINT NOT NULL,
    "status" VARCHAR(20) NOT NULL,
    CONSTRAINT "PK_CREDITS" PRIMARY KEY ("credits_id")
);

-- 8. Inquiries
CREATE TABLE "inquiries" (
    "inquiry_id" BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    "title" VARCHAR(200) NULL,
    "status" VARCHAR(20) DEFAULT 'WAIT' NOT NULL,
    "user_id" BIGINT NOT NULL,
    "content" TEXT NULL,
    "created_at" TIMESTAMP DEFAULT now() NOT NULL,
    "updated_at" TIMESTAMP DEFAULT now() NOT NULL,
    CONSTRAINT "PK_INQUIRIES" PRIMARY KEY ("inquiry_id")
);

-- 9. Friendships
CREATE TABLE "friendships" (
    "friendships_id" BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    "status" varchar(20) NOT NULL,
    "created_at" TIMESTAMP DEFAULT now() NOT NULL,
    "updated_at" TIMESTAMP DEFAULT now() NOT NULL,
    "requester_id" BIGINT NOT NULL,
    "addressee_id" BIGINT NOT NULL,
    CONSTRAINT "PK_FRIENDSHIPS" PRIMARY KEY ("friendships_id")
);

-- 10. Room Participants
CREATE TABLE "room_participants" (
    "participants_id" BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    "room_id" BIGINT NOT NULL,
    "created_at" TIMESTAMP DEFAULT now() NOT NULL,
    "user_id" BIGINT NOT NULL,
    "is_kicked" BOOLEAN DEFAULT FALSE NOT NULL,
    "connection_status" VARCHAR(50) DEFAULT 'ACTIVE' NOT NULL,
    CONSTRAINT "PK_ROOM_PARTICIPANTS" PRIMARY KEY ("participants_id")
);

-- 11. Exp Logs
CREATE TABLE "exp_logs" (
    "exp_log_id" BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    "user_id" BIGINT NOT NULL,
    "exp_rule_id" BIGINT NOT NULL,
    "earned_exp" INT NOT NULL,
    "target_id" BIGINT NOT NULL,
    "reference_id" BIGINT NOT NULL,
    "created_at" TIMESTAMP DEFAULT now() NOT NULL,
    CONSTRAINT "PK_EXP_LOGS" PRIMARY KEY ("exp_log_id")
);

-- 12. Reports
CREATE TABLE "reports" (
    "report_id" BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    "reporter_id" BIGINT NOT NULL,
    "chat_id" BIGINT NOT NULL,
    "reason" TEXT NOT NULL,
    "status" VARCHAR(20) DEFAULT 'PENDING' NOT NULL,
    "created_at" TIMESTAMP DEFAULT now() NOT NULL,
    "updated_at" TIMESTAMP DEFAULT now() NOT NULL,
    CONSTRAINT "PK_REPORTS" PRIMARY KEY ("report_id")
);

-- 13. Levels
CREATE TABLE "levels" (
    "level_id" BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    "required_exp" INT NOT NULL,
    "max_comment_limit" INT NOT NULL,
    "can_upload_image" BOOLEAN DEFAULT FALSE NOT NULL,
    CONSTRAINT "PK_LEVELS" PRIMARY KEY ("level_id")
);

-- 14. Carts
CREATE TABLE "carts" (
    "cart_id" BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    "user_id" BIGINT NOT NULL,
    "book_id" BIGINT NOT NULL,
    "quantity" INT DEFAULT 1 NOT NULL,
    "created_at" TIMESTAMP DEFAULT Now() NOT NULL,
    CONSTRAINT "PK_CARTS" PRIMARY KEY ("cart_id")
);

-- 15. Book AI Chats
CREATE TABLE "book_ai_chats" (
    "chat_id" BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    "ai_room_id" BIGINT NOT NULL,
    "user_id" BIGINT NOT NULL,
    "chat_type" VARCHAR(20) DEFAULT 'CONTENT_QA' NOT NULL,
    "user_msg" TEXT NOT NULL,
    "ai_msg" TEXT NOT NULL,
    "rating" INT DEFAULT 0 NOT NULL,
    "created_at" TIMESTAMP DEFAULT now() NOT NULL,
    "token_count" INT NULL,
    "response_time_ms" INT NULL,
    CONSTRAINT "PK_BOOK_AI_CHATS" PRIMARY KEY ("chat_id")
);

-- 16. Reviews
CREATE TABLE "reviews" (
    "review_id" BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    "user_id" BIGINT NOT NULL,
    "book_id" BIGINT NOT NULL,
    "rating" INT NOT NULL,
    "review_content" TEXT NOT NULL,
    "created_at" TIMESTAMP DEFAULT Now() NOT NULL,
    "is_changed" BOOLEAN DEFAULT FALSE NOT NULL,
    "updated_at" TIMESTAMP DEFAULT Now() NOT NULL,
    "is_spoiler" BOOLEAN DEFAULT FALSE NOT NULL,
    "visibility_status" VARCHAR(20) NOT NULL,
    "spoiler_report_count" INT DEFAULT 0 NOT NULL,
    "violation_report_count" INT DEFAULT 0 NOT NULL,
    "like_count" INT DEFAULT 0 NOT NULL,
    "dislike_count" INT DEFAULT 0 NOT NULL,
    CONSTRAINT "PK_REVIEWS" PRIMARY KEY ("review_id")
);

-- 17. Comments
CREATE TABLE "comments" (
    "comment_id" BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    "user_id" BIGINT NOT NULL,
    "parent_id" BIGINT NULL,
    "chapter_id" BIGINT NOT NULL,
    "comment_content" TEXT NOT NULL,
    "created_at" TIMESTAMP DEFAULT Now() NOT NULL,
    "is_changed" BOOLEAN DEFAULT FALSE NOT NULL,
    "updated_at" TIMESTAMP DEFAULT Now() NOT NULL,
    "is_spoiler" BOOLEAN DEFAULT FALSE NOT NULL,
    "visibility_status" VARCHAR(20) NOT NULL,
    "spoiler_report_count" INT DEFAULT 0 NOT NULL,
    "violation_report_count" INT DEFAULT 0 NOT NULL,
    "like_count" INT DEFAULT 0 NOT NULL,
    "dislike_count" INT DEFAULT 0 NOT NULL,
    CONSTRAINT "PK_COMMENTS" PRIMARY KEY ("comment_id")
);

-- 18. Reading Rooms
CREATE TABLE "reading_rooms" (
    "room_id" BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    "host_id" BIGINT NOT NULL,
    "library_id" BIGINT NOT NULL,
    "room_name" VARCHAR(100) NOT NULL,
    "voice_type" VARCHAR(50) DEFAULT 'BASIC' NOT NULL,
    "play_speed" DECIMAL(3,1) DEFAULT 1.0 NOT NULL,
    "max_capacity" INT DEFAULT 8 NOT NULL,
    "status" VARCHAR(20) DEFAULT 'WAITING' NOT NULL,
    "current_chapter" INT NOT NULL,
    "last_read_pos" INT DEFAULT 0 NOT NULL,
    "created_at" TIMESTAMP DEFAULT now() NOT NULL,
    "updated_at" TIMESTAMP DEFAULT now() NOT NULL,
    CONSTRAINT "PK_READING_ROOMS" PRIMARY KEY ("room_id")
);

-- 19. Order Items
CREATE TABLE "order_items" (
    "item_id" BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    "book_id" BIGINT NOT NULL,
    "order_id" BIGINT NOT NULL,
    "snapshot_price" DECIMAL(10,0) NOT NULL,
    "quantity" INT DEFAULT 1 NOT NULL,
    "status" VARCHAR(20) NOT NULL,
    CONSTRAINT "PK_ORDER_ITEMS" PRIMARY KEY ("item_id")
);

-- 20. Exp Rules
CREATE TABLE "exp_rules" (
    "exp_rule_id" BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    "activity_type" VARCHAR(50) NOT NULL,
    "exp" INT NOT NULL,
    "category_id" BIGINT NULL,
    "created_at" TIMESTAMP DEFAULT now() NOT NULL,
    "updated_at" TIMESTAMP DEFAULT now() NOT NULL,
    CONSTRAINT "PK_EXP_RULES" PRIMARY KEY ("exp_rule_id")
);

-- 21. Users
CREATE TABLE "users" (
    "user_id" BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    "role" VARCHAR(20) DEFAULT 'USER' NOT NULL,
    "is_blacklisted" BOOLEAN DEFAULT FALSE NOT NULL,
    "is_adult" BOOLEAN DEFAULT FALSE NOT NULL,
    "provider" VARCHAR(20) NOT NULL,
    "provider_id" VARCHAR(255) NOT NULL,
    "created_at" TIMESTAMP DEFAULT now() NOT NULL,
    "last_login_at" TIMESTAMP DEFAULT now() NOT NULL,
    "updated_at" TIMESTAMP NULL,
    "deleted_at" TIMESTAMP NULL,
    "status" VARCHAR(20) NULL,
    "password" VARCHAR(255) NULL,
    "login_id" VARCHAR(255) NULL,
    CONSTRAINT "PK_USERS" PRIMARY KEY ("user_id")
);

-- 22. Subscriptions
CREATE TABLE "subscriptions" (
    "sub_id" BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    "plan_name" VARCHAR(50) NOT NULL,
    "price" DECIMAL NOT NULL,
    "status" VARCHAR(20) NOT NULL,
    "next_billing_date" TIMESTAMP NOT NULL,
    "started_at" TIMESTAMP NULL,
    "ended_at" TIMESTAMP NULL,
    "created_at" TIMESTAMP NOT NULL DEFAULT now(),
    "updated_at" TIMESTAMP NOT NULL DEFAULT now(),
    "deleted_at" TIMESTAMP NULL,
    "user_id" BIGINT NOT NULL,
    CONSTRAINT "PK_SUBSCRIPTIONS" PRIMARY KEY ("sub_id")
);

-- 23. User Informations (태그 컬럼 추가됨)
CREATE TABLE "user_informations" (
                                     "user_information_id" BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
                                     "preferred_genre" VARCHAR(255) NOT NULL,
                                     "profile_image" VARCHAR(255) NULL,
                                     "experience" INT DEFAULT 0 NOT NULL,
                                     "level_id" BIGINT NOT NULL,
                                     "user_id" BIGINT NOT NULL,
                                     "user_name" VARCHAR(30) NOT NULL,
                                     "tag" VARCHAR(4) NOT NULL,

    -- [추가됨] JPA BaseTimeEntity와 매핑될 컬럼들
                                     "created_at" TIMESTAMP DEFAULT now() NOT NULL,
                                     "updated_at" TIMESTAMP DEFAULT now() NOT NULL,
                                     "deleted_at" TIMESTAMP NULL,

                                     CONSTRAINT "PK_USER_INFORMATIONS" PRIMARY KEY ("user_information_id")
);

-- 24. Orders
CREATE TABLE "orders" (
    "order_id" BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    "user_id" BIGINT NOT NULL,
    "method_id" BIGINT NULL,
    "order_uid" VARCHAR(255) NOT NULL,
    "order_name" VARCHAR(255) NOT NULL,
    "total_amount" DECIMAL(10,0) NOT NULL,
    "status" VARCHAR(20) NOT NULL,
    "created_at" TIMESTAMP DEFAULT now() NOT NULL,
    "updated_at" TIMESTAMP DEFAULT now() NOT NULL,
    "deleted_at" TIMESTAMP NULL,
    "sub_id" BIGINT NULL,
    "cart_id" BIGINT NULL,
    CONSTRAINT "PK_ORDERS" PRIMARY KEY ("order_id")
);

-- 25. Inquiry Answers
CREATE TABLE "inquiry_answers" (
    "answer_id" BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    "content" TEXT NOT NULL,
    "user_id" BIGINT NOT NULL,
    "inquiry_id" BIGINT NOT NULL,
    "updated_at" TIMESTAMP DEFAULT now() NOT NULL,
    "created_at" TIMESTAMP DEFAULT now() NOT NULL,
    CONSTRAINT "PK_INQUIRY_ANSWERS" PRIMARY KEY ("answer_id")
);

-- 26. Community Posts
CREATE TABLE "community_posts" (
    "post_id" BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    "title" VARCHAR(200) NOT NULL,
    "content" TEXT NOT NULL,
    "views" INT DEFAULT 0 NOT NULL,
    "report" INT DEFAULT 0 NOT NULL,
    "user_id" BIGINT NOT NULL,
    "like_count" INT DEFAULT 0 NOT NULL,
    "created_at" TIMESTAMP DEFAULT now() NOT NULL,
    "updated_at" TIMESTAMP DEFAULT now() NOT NULL,
    CONSTRAINT "PK_COMMUNITY_POSTS" PRIMARY KEY ("post_id")
);

-- 27. Categories
CREATE TABLE "categories" (
    "category_id" BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    "category_name" VARCHAR(50) NOT NULL,
    "created_at" TIMESTAMP DEFAULT now() NOT NULL,
    "updated_at" TIMESTAMP DEFAULT now() NOT NULL,
    CONSTRAINT "PK_CATEGORIES" PRIMARY KEY ("category_id")
);

-- 28. Book Logs
CREATE TABLE "book_logs" (
    "book_log_id" BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    "read_date" DATE DEFAULT CURRENT_DATE NOT NULL,
    "read_paragraph" INT NOT NULL,
    "read_time" INT NOT NULL,
    "library_id" BIGINT NOT NULL,
    CONSTRAINT "PK_BOOK_LOGS" PRIMARY KEY ("book_log_id")
);

-- 29. Chapters
CREATE TABLE "chapters" (
    "chapter_id" BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    "book_id" BIGINT NOT NULL,
    "chapter_name" VARCHAR(255) NULL,
    "sequence" INT DEFAULT 1 NOT NULL,
    "book_content_path" TEXT NOT NULL,
    "created_at" TIMESTAMP DEFAULT Now() NOT NULL,
    "updated_at" TIMESTAMP DEFAULT Now() NOT NULL,
    "is_embedded" BOOLEAN DEFAULT FALSE NOT NULL,
    "paragraphs" INT DEFAULT -1 NOT NULL,
    CONSTRAINT "PK_CHAPTERS" PRIMARY KEY ("chapter_id")
);

-- 30. Blacklists
CREATE TABLE "blacklists" (
    "blacklist_id" BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    "user_id" BIGINT NOT NULL,
    "type" VARCHAR(20) NOT NULL,
    "reason" TEXT NULL,
    "start_date" TIMESTAMP DEFAULT now() NOT NULL,
    "end_date" TIMESTAMP NOT NULL,
    "is_active" BOOLEAN DEFAULT TRUE NOT NULL,
    "created_at" TIMESTAMP DEFAULT now() NOT NULL,
    "updated_at" TIMESTAMP DEFAULT now() NOT NULL,
    CONSTRAINT "PK_BLACKLISTS" PRIMARY KEY ("blacklist_id")
);

-- 31. Payment History
CREATE TABLE "payment_history" (
    "history_id" BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    "pg_payment_key" VARCHAR(255) NOT NULL,
    "amount" DECIMAL(10,0) NOT NULL,
    "status" VARCHAR(20) NOT NULL,
    "trans_type" VARCHAR(10) DEFAULT 'PAY' NOT NULL,
    "pg_provider" VARCHAR(20) NOT NULL,
    "cancel_reason" VARCHAR(255) NULL,
    "created_at" TIMESTAMP NOT NULL,
    "receipt_url" VARCHAR(500) NULL,
    "fail_reason" VARCHAR(255) NULL,
    "order_id" BIGINT NOT NULL,
    CONSTRAINT "PK_PAYMENT_HISTORY" PRIMARY KEY ("history_id")
);

-- 32. Credit Type
CREATE TABLE "credit_type" (
    "credit_type" BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    "base_expiry_days" INT NULL,
    "credit_name" VARCHAR(20) NOT NULL,
    "user_id" BIGINT NOT NULL,
    CONSTRAINT "PK_CREDIT_TYPE" PRIMARY KEY ("credit_type")
);

-- 33. Community Comments
CREATE TABLE "community_comments" (
    "comment_id" BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    "content" VARCHAR(100) NOT NULL,
    "created_at" TIMESTAMP DEFAULT now() NOT NULL,
    "user_id" BIGINT NOT NULL,
    "parent_id" BIGINT NULL,
    "post_id" BIGINT NOT NULL,
    "updated_at" TIMESTAMP DEFAULT now() NOT NULL,
    CONSTRAINT "PK_COMMUNITY_COMMENTS" PRIMARY KEY ("comment_id")
);

-- 34. Chat Logs
CREATE TABLE "chat_logs" (
    "chat_id" BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    "room_id" BIGINT NOT NULL,
    "user_id" BIGINT NOT NULL,
    "message_type" VARCHAR(20) DEFAULT 'TEXT' NOT NULL,
    "content" TEXT NULL,
    "image_url" VARCHAR(2083) NULL,
    "send_at" TIMESTAMP DEFAULT now() NOT NULL,
    CONSTRAINT "PK_CHAT_LOGS" PRIMARY KEY ("chat_id")
);

-- 35. Book AI Chat Rooms
CREATE TABLE "book_ai_chat_rooms" (
    "ai_room_id" BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    "user_id" BIGINT NOT NULL,
    "chapter_id" BIGINT NOT NULL,
    "title" VARCHAR(100) NULL,
    "created_at" TIMESTAMP DEFAULT now() NOT NULL,
    "updated_at" TIMESTAMP DEFAULT now() Not NULL,
    "deleted_at" TIMESTAMP NULL,
    CONSTRAINT "PK_BOOK_AI_CHAT_ROOMS" PRIMARY KEY ("ai_room_id")
);

-- 36. Room Invitations
CREATE TABLE "room_invitations" (
    "invitations_id" BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    "room_id" BIGINT NOT NULL,
    "status" VARCHAR(20) DEFAULT 'PENDING' NOT NULL,
    "sent_at" TIMESTAMP DEFAULT now() NOT NULL,
    "receiver_id" BIGINT NOT NULL,
    "sender_id" BIGINT NOT NULL,
    CONSTRAINT "PK_ROOM_INVITATIONS" PRIMARY KEY ("invitations_id")
);

-- 37. Payment Methods
CREATE TABLE "payment_methods" (
    "method_id" BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    "user_id" BIGINT NOT NULL,
    "customer_key" VARCHAR(255) NULL,
    "billing_key" VARCHAR(255) NOT NULL,
    "pg_provider" VARCHAR(20) NOT NULL,
    "card_company" VARCHAR(20) NOT NULL,
    "card_last_4" VARCHAR(20) NOT NULL,
    "is_default" BOOLEAN DEFAULT FALSE NOT NULL,
    "created_at" TIMESTAMP NOT NULL,
    "updated_at" TIMESTAMP NOT NULL DEFAULT now(),
    "deleted_at" TIMESTAMP NULL,
    CONSTRAINT "PK_PAYMENT_METHODS" PRIMARY KEY ("method_id")
);

-- 38. Refresh Tokens
CREATE TABLE "refresh_tokens" (
    "user_id" BIGINT NOT NULL,
    "token" VARCHAR(512) NOT NULL,
    CONSTRAINT "PK_REFRESH_TOKENS" PRIMARY KEY ("user_id")
);

-- 39. User Vectors
CREATE TABLE "user_vectors" (
    "user_id" BIGINT NOT NULL,
    "vector" HALFVEC(1024) NULL,
    "created_at" TIMESTAMP DEFAULT Now() NOT NULL,
    CONSTRAINT "PK_User_Vectors" PRIMARY KEY ("user_id")
);

-- 40. Book Vectors
CREATE TABLE "book_vectors" (
    "book_id" BIGINT NOT NULL,
    "vector" HALFVEC(1024) NULL,
    "created_at" TIMESTAMP DEFAULT Now() NOT NULL,
    CONSTRAINT "PK_Book_Vectors" PRIMARY KEY ("book_id")
);

-- 41. Chapter Vectors
CREATE TABLE "chapter_vectors" (
    "chapter_id" BIGINT NOT NULL,
    "vector" HALFVEC(1024) NULL,
    "created_at" TIMESTAMP DEFAULT Now() NOT NULL,
    CONSTRAINT "PK_chapter_Vectors" PRIMARY KEY ("chapter_id")
);

-- FK 설정 (기존과 동일)
ALTER TABLE "book_vectors" ADD CONSTRAINT "FK_books_TO_book_vectors_1" FOREIGN KEY ("book_id") REFERENCES "books" ("book_id");
ALTER TABLE "chapter_vectors" ADD CONSTRAINT "FK_chapters_TO_chapter_vectors_1" FOREIGN KEY ("chapter_id") REFERENCES "chapters" ("chapter_id");
ALTER TABLE "user_vectors" ADD CONSTRAINT "FK_users_TO_user_vectors_1" FOREIGN KEY ("user_id") REFERENCES "users" ("user_id");
ALTER TABLE "refresh_tokens" ADD CONSTRAINT "FK_users_TO_refresh_tokens_1" FOREIGN KEY ("user_id") REFERENCES "users" ("user_id");
ALTER TABLE "notices" ADD CONSTRAINT "FK_users_TO_notices_1" FOREIGN KEY ("user_id") REFERENCES "users" ("user_id");
ALTER TABLE "content_reports" ADD CONSTRAINT "FK_comments_TO_content_reports_1" FOREIGN KEY ("comment_id") REFERENCES "comments" ("comment_id");
ALTER TABLE "content_reports" ADD CONSTRAINT "FK_reviews_TO_content_reports_1" FOREIGN KEY ("review_id") REFERENCES "reviews" ("review_id");
ALTER TABLE "content_reports" ADD CONSTRAINT "FK_users_TO_content_reports_1" FOREIGN KEY ("user_id") REFERENCES "users" ("user_id");
ALTER TABLE "libraries" ADD CONSTRAINT "FK_users_TO_libraries_1" FOREIGN KEY ("user_id") REFERENCES "users" ("user_id");
ALTER TABLE "libraries" ADD CONSTRAINT "FK_books_TO_libraries_1" FOREIGN KEY ("book_id") REFERENCES "books" ("book_id");
ALTER TABLE "bookmarks" ADD CONSTRAINT "FK_chapters_TO_bookmarks_1" FOREIGN KEY ("chapter_id") REFERENCES "chapters" ("chapter_id");
ALTER TABLE "bookmarks" ADD CONSTRAINT "FK_libraries_TO_bookmarks_1" FOREIGN KEY ("library_id") REFERENCES "libraries" ("library_id");
ALTER TABLE "books" ADD CONSTRAINT "FK_categories_TO_books_1" FOREIGN KEY ("category_id") REFERENCES "categories" ("category_id");
ALTER TABLE "likes" ADD CONSTRAINT "FK_comments_TO_likes_1" FOREIGN KEY ("comment_id") REFERENCES "comments" ("comment_id");
ALTER TABLE "likes" ADD CONSTRAINT "FK_reviews_TO_likes_1" FOREIGN KEY ("review_id") REFERENCES "reviews" ("review_id");
ALTER TABLE "likes" ADD CONSTRAINT "FK_users_TO_likes_1" FOREIGN KEY ("user_id") REFERENCES "users" ("user_id");
ALTER TABLE "credits" ADD CONSTRAINT "FK_credit_Type_TO_credits_1" FOREIGN KEY ("credit_type") REFERENCES "credit_type" ("credit_type");
ALTER TABLE "inquiries" ADD CONSTRAINT "FK_users_TO_inquiries_1" FOREIGN KEY ("user_id") REFERENCES "users" ("user_id");
ALTER TABLE "friendships" ADD CONSTRAINT "FK_users_TO_friendships_1" FOREIGN KEY ("requester_id") REFERENCES "users" ("user_id");
ALTER TABLE "friendships" ADD CONSTRAINT "FK_users_TO_friendships_2" FOREIGN KEY ("addressee_id") REFERENCES "users" ("user_id");
ALTER TABLE "room_participants" ADD CONSTRAINT "FK_reading_rooms_TO_room_participants_1" FOREIGN KEY ("room_id") REFERENCES "reading_rooms" ("room_id");
ALTER TABLE "room_participants" ADD CONSTRAINT "FK_users_TO_room_participants_1" FOREIGN KEY ("user_id") REFERENCES "users" ("user_id");
ALTER TABLE "exp_logs" ADD CONSTRAINT "FK_users_TO_exp_logs_1" FOREIGN KEY ("user_id") REFERENCES "users" ("user_id");
ALTER TABLE "exp_logs" ADD CONSTRAINT "FK_exp_rules_TO_exp_logs_1" FOREIGN KEY ("exp_rule_id") REFERENCES "exp_rules" ("exp_rule_id");
ALTER TABLE "reports" ADD CONSTRAINT "FK_users_TO_reports_reporter" FOREIGN KEY ("reporter_id") REFERENCES "users" ("user_id");
ALTER TABLE "reports" ADD CONSTRAINT "FK_chat_logs_TO_reports_target" FOREIGN KEY ("chat_id") REFERENCES "chat_logs" ("chat_id");
ALTER TABLE "carts" ADD CONSTRAINT "FK_users_TO_carts_1" FOREIGN KEY ("user_id") REFERENCES "users" ("user_id");
ALTER TABLE "carts" ADD CONSTRAINT "FK_books_TO_carts_1" FOREIGN KEY ("book_id") REFERENCES "books" ("book_id");
ALTER TABLE "book_ai_chats" ADD CONSTRAINT "FK_book_ai_chat_rooms_TO_book_ai_chats_1" FOREIGN KEY ("ai_room_id") REFERENCES "book_ai_chat_rooms" ("ai_room_id");
ALTER TABLE "book_ai_chats" ADD CONSTRAINT "FK_users_TO_book_ai_chats_1" FOREIGN KEY ("user_id") REFERENCES "users" ("user_id");
ALTER TABLE "reviews" ADD CONSTRAINT "FK_users_TO_reviews_1" FOREIGN KEY ("user_id") REFERENCES "users" ("user_id");
ALTER TABLE "reviews" ADD CONSTRAINT "FK_books_TO_reviews_1" FOREIGN KEY ("book_id") REFERENCES "books" ("book_id");
ALTER TABLE "comments" ADD CONSTRAINT "FK_users_TO_comments_1" FOREIGN KEY ("user_id") REFERENCES "users" ("user_id");
ALTER TABLE "comments" ADD CONSTRAINT "FK_comments_TO_comments_1" FOREIGN KEY ("parent_id") REFERENCES "comments" ("comment_id");
ALTER TABLE "comments" ADD CONSTRAINT "FK_chapters_TO_comments_1" FOREIGN KEY ("chapter_id") REFERENCES "chapters" ("chapter_id");
ALTER TABLE "reading_rooms" ADD CONSTRAINT "FK_users_TO_reading_rooms_1" FOREIGN KEY ("host_id") REFERENCES "users" ("user_id");
ALTER TABLE "reading_rooms" ADD CONSTRAINT "FK_libraries_TO_reading_rooms_1" FOREIGN KEY ("library_id") REFERENCES "libraries" ("library_id");
ALTER TABLE "order_items" ADD CONSTRAINT "FK_books_TO_order_items_1" FOREIGN KEY ("book_id") REFERENCES "books" ("book_id");
ALTER TABLE "order_items" ADD CONSTRAINT "FK_orders_TO_order_items_1" FOREIGN KEY ("order_id") REFERENCES "orders" ("order_id");
ALTER TABLE "exp_rules" ADD CONSTRAINT "FK_categories_TO_exp_rules_1" FOREIGN KEY ("category_id") REFERENCES "categories" ("category_id");
ALTER TABLE "subscriptions" ADD CONSTRAINT "FK_users_TO_subscriptions_1" FOREIGN KEY ("user_id") REFERENCES "users" ("user_id");
ALTER TABLE "user_informations" ADD CONSTRAINT "FK_levels_TO_user_informations_1" FOREIGN KEY ("level_id") REFERENCES "levels" ("level_id");
ALTER TABLE "user_informations" ADD CONSTRAINT "FK_users_TO_user_informations_1" FOREIGN KEY ("user_id") REFERENCES "users" ("user_id");
ALTER TABLE "orders" ADD CONSTRAINT "FK_users_TO_orders_1" FOREIGN KEY ("user_id") REFERENCES "users" ("user_id");
ALTER TABLE "orders" ADD CONSTRAINT "FK_payment_methods_TO_orders_1" FOREIGN KEY ("method_id") REFERENCES "payment_methods" ("method_id");
ALTER TABLE "orders" ADD CONSTRAINT "FK_subscriptions_TO_orders_1" FOREIGN KEY ("sub_id") REFERENCES "subscriptions" ("sub_id");
ALTER TABLE "orders" ADD CONSTRAINT "FK_carts_TO_orders_1" FOREIGN KEY ("cart_id") REFERENCES "carts" ("cart_id");
ALTER TABLE "inquiry_answers" ADD CONSTRAINT "FK_users_TO_inquiry_answers_1" FOREIGN KEY ("user_id") REFERENCES "users" ("user_id");
ALTER TABLE "inquiry_answers" ADD CONSTRAINT "FK_inquiries_TO_inquiry_answers_1" FOREIGN KEY ("inquiry_id") REFERENCES "inquiries" ("inquiry_id");
ALTER TABLE "community_posts" ADD CONSTRAINT "FK_users_TO_community_posts_1" FOREIGN KEY ("user_id") REFERENCES "users" ("user_id");
ALTER TABLE "book_logs" ADD CONSTRAINT "FK_Libraries_TO_book_logs_1" FOREIGN KEY ("library_id") REFERENCES "libraries" ("library_id");
ALTER TABLE "chapters" ADD CONSTRAINT "FK_books_TO_chapters_1" FOREIGN KEY ("book_id") REFERENCES "books" ("book_id");
ALTER TABLE "blacklists" ADD CONSTRAINT "FK_users_TO_blacklists_1" FOREIGN KEY ("user_id") REFERENCES "users" ("user_id");
ALTER TABLE "payment_history" ADD CONSTRAINT "FK_orders_TO_payment_history_1" FOREIGN KEY ("order_id") REFERENCES "orders" ("order_id");
ALTER TABLE "credit_type" ADD CONSTRAINT "FK_users_TO_credit_Type_1" FOREIGN KEY ("user_id") REFERENCES "users" ("user_id");
ALTER TABLE "community_comments" ADD CONSTRAINT "FK_users_TO_community_comments_1" FOREIGN KEY ("user_id") REFERENCES "users" ("user_id");
ALTER TABLE "community_comments" ADD CONSTRAINT "FK_community_comments_TO_community_comments_1" FOREIGN KEY ("parent_id") REFERENCES "community_comments" ("comment_id");
ALTER TABLE "community_comments" ADD CONSTRAINT "FK_community_posts_TO_community_comments_1" FOREIGN KEY ("post_id") REFERENCES "community_posts" ("post_id");
ALTER TABLE "chat_logs" ADD CONSTRAINT "FK_reading_rooms_TO_chat_logs_1" FOREIGN KEY ("room_id") REFERENCES "reading_rooms" ("room_id");
ALTER TABLE "chat_logs" ADD CONSTRAINT "FK_users_TO_chat_logs_1" FOREIGN KEY ("user_id") REFERENCES "users" ("user_id");
ALTER TABLE "room_invitations" ADD CONSTRAINT "FK_reading_rooms_TO_room_invitations_1" FOREIGN KEY ("room_id") REFERENCES "reading_rooms" ("room_id");
ALTER TABLE "room_invitations" ADD CONSTRAINT "FK_users_TO_room_invitations_1" FOREIGN KEY ("receiver_id") REFERENCES "users" ("user_id");
ALTER TABLE "room_invitations" ADD CONSTRAINT "FK_users_TO_room_invitations_2" FOREIGN KEY ("sender_id") REFERENCES "users" ("user_id");
ALTER TABLE "payment_methods" ADD CONSTRAINT "FK_users_TO_payment_methods_1" FOREIGN KEY ("user_id") REFERENCES "users" ("user_id");
ALTER TABLE "book_ai_chat_rooms" ADD CONSTRAINT "FK_users_TO_book_ai_chat_rooms_1" FOREIGN KEY ("user_id") REFERENCES "users" ("user_id");
ALTER TABLE "book_ai_chat_rooms" ADD CONSTRAINT "FK_chapters_TO_book_ai_chat_rooms_1" FOREIGN KEY ("chapter_id") REFERENCES "chapters" ("chapter_id");

-- Unique Constraints
ALTER TABLE "users" ADD CONSTRAINT "UQ_USERS_LOGIN_ID" UNIQUE ("login_id");
ALTER TABLE "users" ADD CONSTRAINT "UQ_USERS_PROVIDER" UNIQUE ("provider", "provider_id");
ALTER TABLE "user_informations" ADD CONSTRAINT "UQ_USER_INFORMATIONS_USER_ID" UNIQUE ("user_id");
ALTER TABLE "friendships" ADD CONSTRAINT "UQ_FRIENDSHIP_PAIR" UNIQUE ("requester_id", "addressee_id");
ALTER TABLE "orders" ADD CONSTRAINT "UQ_ORDERS_UID" UNIQUE ("order_uid");
ALTER TABLE "categories" ADD CONSTRAINT "UQ_CATEGORIES_NAME" UNIQUE ("category_name");
ALTER TABLE "exp_rules" ADD CONSTRAINT "UQ_EXP_RULE_TYPE" UNIQUE NULLS NOT DISTINCT ("activity_type", "category_id");
ALTER TABLE "books" ADD CONSTRAINT "UQ_BOOKS_TITLE_AUTHOR" UNIQUE ("title", "author");
ALTER TABLE "libraries" ADD CONSTRAINT "UQ_MY_BOOK" UNIQUE ("user_id", "book_id");
ALTER TABLE "bookmarks" ADD CONSTRAINT "UQ_USER_BOOKMARK" UNIQUE ("library_id", "chapter_id");
ALTER TABLE "exp_logs" ADD CONSTRAINT "UQ_USER_EXP_REWARD" UNIQUE ("user_id", "exp_rule_id", "reference_id");
ALTER TABLE "book_logs" ADD CONSTRAINT "UQ_DAILY_READ" UNIQUE ("library_id", "read_date");