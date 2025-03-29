package com.keodam.keodam_backend.mypage.service;

import com.keodam.keodam_backend.app.domain.User;
import com.keodam.keodam_backend.app.repository.UserRepository;
import com.keodam.keodam_backend.exception.GeneralException;
import com.keodam.keodam_backend.global.aws.AwsS3Service;
import com.keodam.keodam_backend.global.code.status.ErrorStatus;
import com.keodam.keodam_backend.mypage.domain.DocumentType;
import com.keodam.keodam_backend.mypage.domain.UserVerification;
import com.keodam.keodam_backend.mypage.dto.request.FileRequestDto;
import com.keodam.keodam_backend.mypage.dto.response.FileResponseDto;
import com.keodam.keodam_backend.mypage.repository.UserVerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class FileService {

    private final AwsS3Service awsS3Service;
    private final UserRepository userRepository;
    private final UserVerificationRepository userVerificationRepository;

    public FileResponseDto createFile(String email, MultipartFile img, FileRequestDto fileRequestDto) {
        String imgUrl = awsS3Service.uploadFile(img);
        DocumentType documentType = getDocumentType(fileRequestDto.getDocumentType());

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        UserVerification userVerification = UserVerification.builder()
                .user(user)
                .documentType(documentType)
                .documentFilePath(imgUrl)
                .build();

        userVerificationRepository.save(userVerification);

        return FileResponseDto.from(userVerification);
    }

    private DocumentType getDocumentType(String documentTypeStr) {
        try {
            return DocumentType.valueOf(documentTypeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new GeneralException(ErrorStatus.INVALID_DOCUMENT_TYPE);
        }
    }
}
