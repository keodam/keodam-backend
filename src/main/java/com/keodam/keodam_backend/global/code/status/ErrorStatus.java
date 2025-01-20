package com.keodam.keodam_backend.global.code.status;

import com.keodam.keodam_backend.global.code.BaseErrorCode;
import com.keodam.keodam_backend.global.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER4001", "사용자가 없습니다."),
    EMAIL_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER4003", "이메일이 없습니다."),
    NICKNAME_NOT_EXIST(HttpStatus.BAD_REQUEST, "MEMBER4002", "닉네임은 필수 입니다."),
    EMAIL_FAILED(HttpStatus.BAD_REQUEST, "MEMBER4004","이메일 전송에 실패하였습니다"),

    PASSWORD_VALIDATION_FAILED(HttpStatus.BAD_REQUEST,"PASSWORD4001","비밀번호는 영어 대/소문자, 숫자 중 2종류 이상을 조합해야 합니다."),

    JWT_SIGNATURE_INVALID(HttpStatus.UNAUTHORIZED, "AUTH001", "JWT 서명이 올바르지 않습니다."),
    JWT_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH002", "JWT 토큰이 만료되었습니다."),
    JWT_MALFORMED(HttpStatus.UNAUTHORIZED, "AUTH003", "JWT 토큰이 올바르지 않은 형식입니다."),


    //S3
    S3_FAILED(HttpStatus.INTERNAL_SERVER_ERROR,"S34001","사진 업로드에 실패했습니다."),
    S3_FORMAT(HttpStatus.BAD_REQUEST, "S34002","잘못된 형식의 파일입니다."),

    //user
    USER_NOT_FOUND(HttpStatus.NOT_FOUND,"USER4001","회원 정보를 찾을 수 없습니다."),
    FAMILY_ALREADY(HttpStatus.BAD_REQUEST,"FAMILY4002", "이미 가입한 가족이 존재합니다."),

    //quiz
    QUIZ_NOT_FOUND(HttpStatus.NOT_FOUND,"QUIZ4001","퀴즈를 찾을 수 없습니다.");

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
