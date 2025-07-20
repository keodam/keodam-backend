package com.keodam.keodam_backend.global.code.status;

import com.keodam.keodam_backend.global.code.BaseErrorCode;
import com.keodam.keodam_backend.global.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER4001", "사용자가 없습니다."),
    EMAIL_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER4003", "이메일이 없습니다."),
    NICKNAME_NOT_EXIST(HttpStatus.BAD_REQUEST, "MEMBER4002", "닉네임은 필수 입니다."),
    EMAIL_FAILED(HttpStatus.BAD_REQUEST, "MEMBER4004", "이메일 전송에 실패하였습니다"),
    INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "MEMBER4005", "이메일 형식이 올바르지 않습니다."),

    PASSWORD_VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "PASSWORD4001", "비밀번호는 영어 대/소문자, 숫자 중 2종류 이상을 조합해야 합니다."),

    JWT_SIGNATURE_INVALID(HttpStatus.UNAUTHORIZED, "AUTH001", "JWT 서명이 올바르지 않습니다."),
    JWT_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH002", "JWT 토큰이 만료되었습니다."),
    JWT_MALFORMED(HttpStatus.UNAUTHORIZED, "AUTH003", "JWT 토큰이 올바르지 않은 형식입니다."),

    //UserVerifyCode
    INVALID_PHONE_FORMAT(HttpStatus.BAD_REQUEST, "PHONE4001", "휴대폰번호 형식을 확인해주세요. "),
    NAME_INVALID(HttpStatus.BAD_REQUEST, "PHONE4002", "이름을 확인해주세요."),
    BIRTH_INVALID(HttpStatus.BAD_REQUEST, "PHONE4003", "생년월일을 확인해주세요."),
    VERIFICATION_FAILED(HttpStatus.UNAUTHORIZED, "PHONE4004", "인증번호가 일치하지 않습니다."),
    ARCHIVE_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "PHONE4005", "이 전화번호로 생성된 인증 정보가 너무 많습니다."),
    TOO_MANY_REQUEST(HttpStatus.TOO_MANY_REQUESTS, "PHONE4291", "인증번호 요청은 2분마다 가능합니다."),

    //S3
    S3_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "S34001", "사진 업로드에 실패했습니다."),
    S3_FORMAT(HttpStatus.BAD_REQUEST, "S34002", "잘못된 형식의 파일입니다."),
    S3_EMPTY_FILE(HttpStatus.BAD_REQUEST, "S34003", "업로드할 파일이 없습니다."),

    //user
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER4001", "회원 정보를 찾을 수 없습니다."),
    ALREADY_REGISTER_REFERRAL(HttpStatus.BAD_REQUEST, "USER4003", "이미 추천인을 등록하셨습니다."),
    CANNOT_REFER_SELF(HttpStatus.BAD_REQUEST, "USER4004", "자기 자신은 추천인으로 등록할 수 없습니다."),

    // admin
    ALREADY_REGISTER_ADMIN(HttpStatus.BAD_REQUEST, "ADMIN4001", "이미 가입한 기록이 존재합니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "ADMIN4002", "비밀번호를 확인해주세요."),
    ADMIN_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "ADMIN4011", "관리자의 승인이 필요합니다."),

    // nickname
    NICKNAME_SPECIAL_CHAR(HttpStatus.BAD_REQUEST, "NICKNAME4001", "특수문자는 닉네임에 포함될 수 없어요."),
    NICKNAME_DUPLICATE(HttpStatus.BAD_REQUEST, "NICKNAME4002", "중복된 닉네임이에요."),
    NICKNAME_ALREADY_CHANGED(HttpStatus.BAD_REQUEST, "NICKNAME4003", "닉네임은 최초 설정 후 한 번만 변경할 수 있어요."),
    NICKNAME_CONTAINS_NAME(HttpStatus.BAD_REQUEST, "NICKNAME4004", "닉네임에 이름은 포함할 수 없어요."),
    NICKNAME_CONTAINS_BAD_WORD(HttpStatus.BAD_REQUEST, "NICKNAME4005", "닉네임에 비속어는 포함할 수 없어요."),

    //code
    INVALID_VERIFICATION_CODE(HttpStatus.NOT_FOUND, "CODE4001", "인증코드가 일치하지 않습니다."),
    EXPIRED_CODE(HttpStatus.GONE, "CODE4002", "인증코드가 만료되었습니다."),

    //document
    INVALID_DOCUMENT_TYPE(HttpStatus.BAD_REQUEST, "S34002", "유효하지 않은 문서 타입입니다."),

    // role type
    INVALID_ROLE_TYPE(HttpStatus.BAD_REQUEST, "ROLE4001", "잘못된 역할 선택입니다."),

    //api
    EMPTY_RESPONSE(HttpStatus.NO_CONTENT, "API4001", "API 응답이 비어있어요."),
    INVALID_JSON_RESPONSE(HttpStatus.INTERNAL_SERVER_ERROR, "API4002", "JSON 응답 형식 오류입니다."),

    //bootpay
    BOOTPAY_CONFIRM_FAILED(HttpStatus.BAD_REQUEST, "P1001", "Bootpay 승인 실패입니다."),
    BOOTPAY_CONFIRM_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "P1002", "Bootpay 승인 중 예외 발생했습니다."),
    BOOTPAY_TOKEN_FAILED(HttpStatus.BAD_REQUEST, "P1003", "Bootpay 토큰 발급 실패했습니다."),

    //payment
    ALREADY_PROCESSED_PAYMENT(HttpStatus.CONFLICT, "P1004", "이미 처리된 결제입니다."),

    //community
    COMMUNITY_NOT_FOUND(HttpStatus.BAD_REQUEST, "COMMUNITY01", "존재하지 않는 커뮤니티 게시물입니다."),
    UNAUTHORIZED_COMMUNITY_MODIFICATION(HttpStatus.FORBIDDEN, "COMMENT02", "해당 게시물을 수정하거나 삭제할 권한이 없습니다."),

    //comment
    COMMUNITY_COMMENT_NOT_FOUND(HttpStatus.BAD_REQUEST, "COMMENT01", "존재하지 않는 커뮤니티 댓글입니다."),
    UNAUTHORIZED_COMMENT_MODIFICATION(HttpStatus.FORBIDDEN, "COMMENT02", "해당 댓글을 수정하거나 삭제할 권한이 없습니다."),

    //mentee
    MENTEE_NOT_FOUND(HttpStatus.BAD_REQUEST, "MENTEE01", "존재하지 않는 멘티 프로필입니다."),

    //mentor
    MENTOR_NOT_FOUND(HttpStatus.BAD_REQUEST, "MENTOR01", "존재하지 않는 멘토 프로필입니다."),

    //oidc
    UNSUPPORTED_PROVIDER(HttpStatus.BAD_REQUEST, "LOGIN01", "지원하지 않는 제공 업체입니다.");
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build();
    }
}