package com.keodam.keodam_backend.app.user.service;


import com.keodam.keodam_backend.app.domain.RoleType;
import com.keodam.keodam_backend.app.user.domain.User;
import com.keodam.keodam_backend.app.user.dto.res.SignupStatusResponseDto;
import com.keodam.keodam_backend.app.user.repository.UserRepository;
import com.keodam.keodam_backend.app.user.dto.res.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
//    private final FileStorageService fileStorageService;

    /**
     * 닉네임 저장 및 중복 체크
     */
    @Transactional
    public UserResponseDto updateNickname(User user, String nickname) {
        if (userRepository.existsByNickname(nickname)) {
            throw new IllegalArgumentException("닉네임이 중복되었습니다.");
        }

        User updatedUser = user.update(nickname, null, null);
        userRepository.save(updatedUser);

        return createUserResponseDto(updatedUser);
    }

    /**
     * 프로필 사진 업로드 및 저장
     */
//    @Transactional
//    public UserResponseDto updateProfileImage(User user, MultipartFile file) {
//        String imageUrl = fileStorageService.uploadFile(file);
//        User updatedUser = user.update(null, imageUrl, null);
//        userRepository.save(updatedUser);
//
//        return createUserResponseDto(updatedUser);
//    }

    /**
     * 역할 저장 (멘토 / 멘티)
     */
    @Transactional
    public UserResponseDto updateRole(User user, RoleType roleType) {
        if (!roleType.equals("MENTOR") && !roleType.equals("MENTEE")) {
            throw new IllegalArgumentException("잘못된 역할 선택입니다.");
        }

        User updatedUser = user.update(null, null, roleType);
        userRepository.save(updatedUser);

        return createUserResponseDto(updatedUser);
    }

    /**
     * 회원가입 상태 확인
     */
    @Transactional(readOnly = true)
    public SignupStatusResponseDto checkSignupStatus(User user) {
        return new SignupStatusResponseDto(
                user.getNickname() != null,
                user.getProfileUrl() != null,
                user.getRoleType() != null && !user.getRoleType().equals("GUEST")
        );
    }

    /**
     * UserResponse 생성 메서드 (중복 로직 제거)
     */
    private UserResponseDto createUserResponseDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .profileUrl(user.getProfileUrl())
                .roleType(user.getRoleType())
                .hasProfileImage(user.getProfileUrl() != null)
                .hasRole(user.getRoleType() != null && !user.getRoleType().equals("GUEST"))
                .build();
    }

    public Optional<User> findByOAuthId(String oauthId) {
        return userRepository.findByOauthId(oauthId);
    }

    @Transactional
    public User createUser(String oauthId, String email) {
        User newUser = new User(oauthId, email);
        return userRepository.save(newUser);
    }

    public boolean isNicknameAvailable(String nickname) {
        return !userRepository.existsByNickname(nickname);
    }
}
