package com.keodam.keodam_backend.app.user.service;

import com.keodam.keodam_backend.app.domain.RoleType;
import com.keodam.keodam_backend.app.domain.User;
import com.keodam.keodam_backend.app.user.repository.UserRepository;
import com.keodam.keodam_backend.app.user.dto.UserResponseDto;
import com.keodam.keodam_backend.exception.GeneralException;
import com.keodam.keodam_backend.global.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final BannedWordsService bannedWordsService;
    private final UserRepository userRepository;

    /**
     * 닉네임 저장 및 중복 체크
     */
    @Transactional
    public UserResponseDto updateNickname(User user, String nickname) {
        user.updateNickname(nickname);
        return createUserResponseDto(user);
    }

    /**
     * 역할 저장 (멘토 / 멘티)
     */
    @Transactional
    public UserResponseDto updateRole(User user, RoleType roleType) {
        if (roleType != RoleType.MENTOR && roleType != RoleType.MENTEE) {
            throw new GeneralException(ErrorStatus.INVALID_ROLE_TYPE);
        }
        user.updateRole(roleType);
        return createUserResponseDto(user);
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
                .hasRole(user.getRoleType() != null && user.getRoleType()!=RoleType.GUEST)
                .build();
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }


    private boolean containsSpecialChar(String nickname) {
        // 한글, 영어, 숫자만 허용. 그 외는 특수문자
        return !nickname.matches("^[가-힣a-zA-Z0-9]+$");
    }

    private boolean isDuplicateNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    private boolean containsBadWord(String nickname) {
        return bannedWordsService.containsBannedWord(nickname);
    }

    /**
     * 닉네임 검증
     */
    public void validateNickname(String nickname) {
        if (containsSpecialChar(nickname)) {
            throw new GeneralException(ErrorStatus.NICKNAME_SPECIAL_CHAR);
        }

        if (isDuplicateNickname(nickname)) {
            throw new GeneralException(ErrorStatus.NICKNAME_DUPLICATE);
        }

        if (containsBadWord(nickname)) {
            throw new GeneralException(ErrorStatus.NICKNAME_CONTAINS_BAD_WORD);
        }
    }
}
