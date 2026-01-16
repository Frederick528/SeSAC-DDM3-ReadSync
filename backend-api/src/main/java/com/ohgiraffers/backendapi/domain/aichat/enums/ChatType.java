package com.ohgiraffers.backendapi.domain.aichat.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChatType {
    // 1. 단어/용어 정의 (Dictionary/Lexical)
    // 기술: 단순 LLM or 외부 사전 API (RAG 불필요/선택)
    // 예: "이 단어 뜻이 뭐야?", "이 문맥에서 '푸른'은 무슨 의미야?"
    DEFINITION("단어/용어 정의"),

    // 2. 책 내용 질문/심층 분석 (Content Q&A)
    // 기술: RAG (Vector DB 검색 + Context 주입)
    // 예: "주인공이 왜 화를 낸 거야?", "이 복선은 나중에 어떻게 회수돼?"
    CONTENT_QA("책 내용 질문/심층 분석"),

    // 3. 요약 요청 (Summary) -> [REQ-REA-006]
    // 기술: 현재 챕터 전체 텍스트 주입 + 요약 프롬프트
    // 예: "지금 읽은 챕터 3줄 요약해줘", "작가의 의도가 뭐야?"
    SUMMARY("요약 요청"),

    // 4. 퀴즈/질문 생성 (Quiz) -> [REQ-REA-005]
    // 기술: 생성형 프롬프트 (정답을 주는게 아니라 문제를 출제)
    // 예: "내가 잘 이해했는지 퀴즈 내줘"
    QUIZ("퀴즈/질문 생성"),

    // 5. 일상 대화/기타 (Chit-Chat)
    // 기술: 단순 LLM (History만 포함)
    // 예: "안녕", "너 똑똑하네", "고마워" (책과 무관한 대화 처리)
    CHIT_CHAT("일상 대화/기타 (Chit-Chat)");

    private final String description;
}
