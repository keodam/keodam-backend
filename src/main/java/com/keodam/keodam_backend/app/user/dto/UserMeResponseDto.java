package com.keodam.keodam_backend.app.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "내 정보 조회 응답 DTO")
public class UserMeResponseDto {

    @Schema(description = "사용자 이메일", example = "user@example.com")
    private String email;

    @Schema(description = "커뮤니티 프로필 닉네임", example = "커피마시는쿼카")
    private String nickname;

    @Schema(description = "프로필 이미지 URL", example = "https://your-s3-bucket/profiles/image.jpg")
    private String profileImageUrl;

    @Schema(description = "약관 동의 여부", example = "true")
    private boolean agreedTerms;

    @Schema(description = "멘토 여부", example = "true")
    private boolean mentor;

    @Schema(description = "휴대폰 본인인증 여부", example = "true")
    private boolean phoneVerified;

    @Schema(description = "필수 프로필(닉네임, 재학상태, 역할) 작성 완료 여부", example = "true")
    private boolean profileCompleted;

    @Schema(description = "(멘토일 경우) 원두 취향 설정 완료 여부", example = "true")
    private boolean beanPreferenceSet;

    @Schema(description = "회원가입 진행 단계", example = "DONE",
            allowableValues = {"AGREEMENT", "PHONE_VERIFICATION", "PROFILE_SETUP", "BEAN_PREFERENCE_SETUP", "DONE"})
    private String signupStep;
}
