package com.ohgiraffers.backendapi.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Payment
    INVALID_PAYMENT_AMOUNT(HttpStatus.BAD_REQUEST, "PAY-003", "결제 금액이 일치하지 않습니다."),
    // 공통
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C001", "서버 내부 오류가 발생했습니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C002", "잘못된 입력값입니다."),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "C003", "잘못된 타입입니다."),

    // 인증/권환
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "A001", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "A002", "만료된 토큰입니다."),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "A003", "아이디 또는 비밀번호가 일치하지 않습니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "A004", "접근 권한이 없습니다."),

    // 유저
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "사용자를 찾을 수 없습니다."),
    DUPLICATE_LOGIN_ID(HttpStatus.CONFLICT, "U002", "이미 사용 중인 아이디입니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "U003", "이미 사용 중인 이메일입니다."),

    // 댓글
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "C001", "해당 댓글을 찾을 수 없습니다."),
    CANNOT_REPLY_TO_DELETED(HttpStatus.NOT_FOUND, "C002", "삭제된 댓글에는 대댓글을 달 수 없습니다."),
    CANNOT_REPLY_TO_SUSPENDED(HttpStatus.NOT_ACCEPTABLE, "C003", "신고로 비노출 된 댓글에는 대댓글을 달 수 없습니다."),
    COMMENT_SUSPENDED(HttpStatus.FORBIDDEN, "C004", "신고 누적으로 인해 제한된 댓글입니다."),

    // 책
    BOOK_NOT_FOUND(HttpStatus.NOT_FOUND, "B001", "해당 책을 찾을 수 없습니다."),

    // 챕터
    CHAPTER_NOT_FOUND(HttpStatus.NOT_FOUND, "CH001", "해당 챕터를 찾을 수 없습니다."),

    // 파일
    FILE_NOT_FOUND(HttpStatus.NO_CONTENT, "FI001", "해당 파일이 비어있습니다"),
    FILE_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "FI002", "파일 업로드 중 오류가 발생했습니다."),
    FILE_READ_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "FI003", "파일을 읽는 중 오류가 발생했습니다."),
    FILE_DELETE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "FI004", "파일 삭제 중 오류가 발생했습니다."),

    // 리뷰
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "R001", "해당 리뷰를 찾을 수 없습니다."),
    REVIEW_SUSPENDED(HttpStatus.FORBIDDEN, "R002", "신고 누적으로 인해 제한된 리뷰입니다."),
    REVIEW_NOT_OWNER(HttpStatus.FORBIDDEN, "R003", "리뷰 작성자가 아닙니다."),

    // 친구
    CANNOT_REQUEST_TO_SELF(HttpStatus.BAD_REQUEST, "F001", "자기 자신에게는 친구 요청을 보낼 수 없습니다."),
    ADDRESSEE_NOT_FOUND(HttpStatus.NOT_FOUND, "F002", "대상자를 찾을 수 없습니다."),
    ALREADY_FRIENDS(HttpStatus.BAD_REQUEST, "F003", "이미 친구 관계입니다."),
    FRIEND_REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "F004", "존재하지 않는 친구 요청입니다."),
    NO_AUTHORITY_TO_UPDATE(HttpStatus.FORBIDDEN, "F005", "해당 작업에 대한 권한이 없습니다."),
    INVALID_REQUEST_STATUS(HttpStatus.BAD_REQUEST, "F006", "유효하지 않은 요청 상태입니다."),

    // 서재
    LIBRARY_NOT_FOUND(HttpStatus.NOT_FOUND, "L001", "서재에 존재하지 않는 책입니다."),

    // 독서룸
    ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "R001", "존재하지 않는 독서룸입니다."),
    PARTICIPANT_NOT_FOUND(HttpStatus.NOT_FOUND, "R002", "해당 독서룸의 참여자가 아닙니다."),
    ROOM_ALREADY_EXISTS(HttpStatus.CONFLICT, "R003", "이미 운영 중인 독서룸이 있습니다."),
    ALREADY_INVITED(HttpStatus.CONFLICT, "R004", "이미 초대 요청을 보낸 사용자입니다."),
    NOT_HOST(HttpStatus.FORBIDDEN, "R005", "방장 권한이 필요합니다."),
    KICKED_USER(HttpStatus.FORBIDDEN, "R006", "강퇴당한 독서룸에는 재입장할 수 없습니다."),
    NOT_YOUR_INVITATION(HttpStatus.FORBIDDEN, "R007", "본인의 초대장이 아닙니다."),
    ROOM_IS_FULL(HttpStatus.BAD_REQUEST, "R008", "독서룸 정원이 초과되었습니다."),
    ROOM_IS_PLAYING(HttpStatus.BAD_REQUEST, "R009", "재생 중인 독서룸에는 입장할 수 없습니다."),
    INVITATION_NOT_ALLOWED_PLAYING(HttpStatus.BAD_REQUEST, "R010", "재생 중일 때는 초대장을 보낼 수 없습니다."),
    INVITATION_NOT_ALLOWED_FULL(HttpStatus.BAD_REQUEST, "R011", "정원이 초과되어 초대장을 보낼 수 없습니다."),
    INVALID_PLAY_SPEED(HttpStatus.BAD_REQUEST, "R012", "재생 속도는 0.5배에서 2.0배 사이여야 합니다."),
    INVITATION_NOT_FOUND(HttpStatus.NOT_FOUND, "R013", "존재하지 않거나 삭제된 초대장입니다."),
    INVITATION_EXPIRED(HttpStatus.BAD_REQUEST, "R014", "만료된 초대장입니다."),

    // 장바구니
    CART_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "CA001", "장바구니에서 해당 항목을 찾을 수 없습니다."),

    // 결제
    PAYMENT_METHOD_NOT_FOUND(HttpStatus.BAD_REQUEST, "PAY001", "등록된 기본 결제 수단이 없습니다. 카드를 먼저 등록해주세요."),
    ALREADY_SUBSCRIBED(HttpStatus.BAD_REQUEST, "SUB001", "이미 구독 중인 사용자입니다."),
    ROOM_FINISHED(HttpStatus.CONFLICT, "R015", "종료된 독서룸입니다."),

    // 신고
    REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "RPT001", "해당 신고를 찾을 수 없습니다."),

    // AI 채팅
    AI_CHAT_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "AI001", "해당 AI 채팅방을 찾을 수 없습니다."),
    AI_CHAT_NOT_FOUND(HttpStatus.NOT_FOUND, "AI002", "해당 AI 채팅 메시지를 찾을 수 없습니다."),
    AI_CHAT_NOT_OWNER(HttpStatus.FORBIDDEN, "AI003", "해당 채팅방의 소유자가 아닙니다."),
    AI_SERVER_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "AI004", "AI 서버와 통신 중 오류가 발생했습니다."),
    AI_RATING_INVALID(HttpStatus.BAD_REQUEST, "AI005", "평점은 1~5 사이의 값이어야 합니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}

/*
 * 결과: "해당 유저를 찾을 수 없습니다."
 * throw new CustomException(ErrorCode.USER_NOT_FOUND);
 * 
 * Long userId = 100L;
 * // 결과: "해당 유저를 찾을 수 없습니다. (ID: 100)"
 * throw new CustomException(ErrorCode.USER_NOT_FOUND, "ID: " + userId);
 * 
 */

