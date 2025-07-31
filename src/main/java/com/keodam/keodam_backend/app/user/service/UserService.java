package com.keodam.keodam_backend.app.user.service;

import com.keodam.keodam_backend.app.user.coffeechatprofile.domain.Mentor;
import com.keodam.keodam_backend.app.user.coffeechatprofile.repository.MentorRepository;
import com.keodam.keodam_backend.app.user.domain.RoleType;
import com.keodam.keodam_backend.app.user.domain.StudentStatus;
import com.keodam.keodam_backend.app.user.domain.User;
import com.keodam.keodam_backend.app.user.dto.StudentStatusRequestDto;
import com.keodam.keodam_backend.app.user.repository.UserRepository;
import com.keodam.keodam_backend.app.user.dto.UserResponseDto;
import com.keodam.keodam_backend.exception.GeneralException;
import com.keodam.keodam_backend.global.aws.AwsS3Service;
import com.keodam.keodam_backend.global.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final BannedWordsService bannedWordsService;
    private final UserRepository userRepository;
    private final AwsS3Service awsS3Service;
    private final MentorRepository mentorRepository;

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
                .hasRole(user.getRoleType() != null && user.getRoleType() != RoleType.GUEST)
                .build();
    }

    public UserResponseDto uploadProfileImage(User user, MultipartFile file) {
        String imgUrl = awsS3Service.uploadFile(file);

        if (user.getProfileUrl() != null) {
            awsS3Service.deleteFile(user.getProfileUrl());
        }
        user.updateProfileImage(imgUrl);
        userRepository.save(user);

        return UserResponseDto.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .roleType(user.getRoleType())
                .profileUrl(user.getProfileUrl())
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

    @Transactional
    public void updateStudentStatus(User user, StudentStatusRequestDto studentStatusRequestDto) {
        StudentStatus status;
        try {
            status = StudentStatus.valueOf(studentStatusRequestDto.studentStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new GeneralException(ErrorStatus.INVALID_STUDENT_STATUS);
        }
        user.updateStudentStatus(status);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public boolean completeProfile(User user) {
        if (user.getRoleType() == RoleType.MENTEE) {
            return true;
        } else if (user.getRoleType() == RoleType.MENTOR) {
            return isMentorProfileComplete(user);
        }
        return false;
    }

    @Transactional
    public void createMentoringBean(User user, int mentoringBean) {
        if (user.getRoleType() != RoleType.MENTOR) {
            throw new GeneralException(ErrorStatus.MENTOR_ACCESS_ONLY);
        }

        Mentor mentor = Mentor.builder()
                .user(user)
                .mentoringBeanAmount(mentoringBean)
                .build();
        mentorRepository.save(mentor);
    }

    private boolean isMentorProfileComplete(User user) {
        return mentorRepository.findByUser(user)
                .map(mentor -> mentor.getMentoringBeanAmount() != null)
                .orElse(false);
    }
}
