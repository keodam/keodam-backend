package com.keodam.keodam_backend.mypage.controller;

import com.keodam.keodam_backend.global.ApiResponse;
import com.keodam.keodam_backend.mypage.dto.request.FileRequestDto;
import com.keodam.keodam_backend.mypage.dto.response.FileResponseDto;
import com.keodam.keodam_backend.mypage.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage/authenticate")
@Tag(name = "FILE Verify", description = "FILE Verify API")
public class FileController {

    private final FileService fileService;

    @PostMapping(value = "/file", consumes = "multipart/form-data")
    @Operation(summary = "파일로 인증 요청", description = "파일이미지 기반 인증요청 API", security = @SecurityRequirement(name = "Authorization"))
    public ApiResponse<FileResponseDto> createFile(Authentication authentication,
                                                   @RequestPart(name = "ImageFile", required = true) MultipartFile file,
                                                   @RequestPart(name = "fileRequestDto", required = true) FileRequestDto fileRequestDto) {
        String email = authentication.getName();
        return ApiResponse.onSuccess(fileService.createFile(email, file, fileRequestDto));
    }
}
