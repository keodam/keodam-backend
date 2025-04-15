package com.keodam.keodam_backend.mypage.certification.dto.response;

import com.keodam.keodam_backend.mypage.certification.domain.UserVerification;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FileResponseDto {

    private Long id;
    private String userEmail;
    private String documentFilePath;
    private String documentType;
    private String creationDate;

    public static FileResponseDto from(UserVerification userVerification) {
        return FileResponseDto.builder()
                .id(userVerification.getId())
                .userEmail(userVerification.getUser().getEmail())
                .documentFilePath(userVerification.getDocumentFilePath())
                .documentType(userVerification.getDocumentType().toString())
                .creationDate(userVerification.getCreationDate().toString())
                .build();
    }
}
