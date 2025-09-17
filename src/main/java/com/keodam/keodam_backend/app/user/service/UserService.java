package com.keodam.keodam_backend.app.user.service;

import com.keodam.keodam_backend.app.phone.domain.UserIdentityInfo;
import com.keodam.keodam_backend.app.phone.repository.UserIdentityInfoRepository;
import com.keodam.keodam_backend.app.user.coffeechatprofile.domain.Mentee;
import com.keodam.keodam_backend.app.user.coffeechatprofile.domain.Mentor;
import com.keodam.keodam_backend.app.user.coffeechatprofile.repository.*;
import com.keodam.keodam_backend.app.user.domain.RoleType;
import com.keodam.keodam_backend.app.user.domain.StudentStatus;
import com.keodam.keodam_backend.app.user.domain.User;
import com.keodam.keodam_backend.app.user.dto.StudentStatusRequestDto;
import com.keodam.keodam_backend.app.user.dto.UserMeResponseDto;
import com.keodam.keodam_backend.app.user.repository.UserRepository;
import com.keodam.keodam_backend.app.user.dto.UserResponseDto;
import com.keodam.keodam_backend.community.domain.Community;
import com.keodam.keodam_backend.community.repository.CommunityCommentRepository;
import com.keodam.keodam_backend.community.repository.CommunityImageRepository;
import com.keodam.keodam_backend.community.repository.CommunityRepository;
import com.keodam.keodam_backend.exception.GeneralException;
import com.keodam.keodam_backend.global.aws.AwsS3Service;
import com.keodam.keodam_backend.global.code.status.ErrorStatus;
import com.keodam.keodam_backend.mypage.certification.repository.UserVerificationRepository;
import com.keodam.keodam_backend.mypage.payment.repository.BeanTransactionRepository;
import com.keodam.keodam_backend.mypage.payment.repository.BeanWalletRepository;
import com.keodam.keodam_backend.mypage.payment.repository.PaymentRepository;
import com.keodam.keodam_backend.mypage.roulette.repository.ExchangeRequestRepository;
import com.keodam.keodam_backend.mypage.roulette.repository.RouletteSpinLogRepository;
import com.keodam.keodam_backend.term.domain.TermType;
import com.keodam.keodam_backend.term.repository.TermAgreementRepository;
import com.keodam.keodam_backend.app.user.referral.repository.ReferralRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final BannedWordsService bannedWordsService;
    private final UserRepository userRepository;
    private final AwsS3Service awsS3Service;
    private final MentorRepository mentorRepository;
    private final MenteeRepository menteeRepository;
    private final UserIdentityInfoRepository userIdentityInfoRepository;
    private final TermAgreementRepository termAgreementRepository;
    private final ReferralRepository referralRepository;
    private final RouletteSpinLogRepository rouletteSpinLogRepository;
    private final PaymentRepository paymentRepository;
    private final MypageStatsRepository mypageStatsRepository;
    private final ExchangeRequestRepository exchangeRequestRepository;
    private final CommunityCommentRepository communityCommentRepository;
    private final CommunityRepository communityRepository;
    private final CommunityImageRepository communityImageRepository;
    private final BeanTransactionRepository beanTransactionRepository;
    private final BeanWalletRepository beanWalletRepository;
    private final MentorHashtagRepository mentorHashtagRepository;
    private final MenteeHashtagRepository menteeHashtagRepository;

    @Transactional(readOnly = true)
    public UserMeResponseDto getUserInfoAndSignupStep(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        boolean agreedTerms = termAgreementRepository.existsByUserAndTerm_Type(user, TermType.PRIVACY_POLICY) &&
                termAgreementRepository.existsByUserAndTerm_Type(user, TermType.TERMS_OF_SERVICE);

        Optional<UserIdentityInfo> identityOpt = userIdentityInfoRepository.findByUser(user);
        boolean isPhoneVerified = identityOpt.isPresent() &&
                identityOpt.get().getIsActive() &&
                identityOpt.get().getVerifiedAt() != null;

        boolean isProfileCompleted = user.getNickname() != null &&
                user.getStudentStatus() != null &&
                user.getRoleType() != null &&
                user.getRoleType() != RoleType.GUEST;

        boolean isMentor = user.getRoleType() == RoleType.MENTOR;
        boolean isBeanPreferenceSet = false;
        if (isMentor) {
            isBeanPreferenceSet = isMentorProfileComplete(user);
        }

        String referralStatus = getReferralRegister(user);

        String signupStep;
        if (!agreedTerms) {
            signupStep = "AGREEMENT";
        } else if (!isPhoneVerified) {
            signupStep = "PHONE_VERIFICATION";
        } else if (!isProfileCompleted) {
            signupStep = "PROFILE_SETUP";
        } else if (isMentor && !isBeanPreferenceSet) {
            signupStep = "BEAN_PREFERENCE_SETUP";
        } else {
            signupStep = "DONE";
        }

        return UserMeResponseDto.builder()
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileUrl())
                .agreedTerms(agreedTerms)
                .phoneVerified(isPhoneVerified)
                .profileCompleted(isProfileCompleted)
                .mentor(isMentor)
                .beanPreferenceSet(isBeanPreferenceSet)
                .signupStep(signupStep)
                .studentStatus(getStudentStatusString(user))
                .referralRegistered(referralStatus)
                .build();
    }

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

    @Transactional
    public void deleteUser(User user) {
        deleteMentorData(user);
        deleteMenteeData(user);
        deleteReferralData(user);
        deleteUserRelatedData(user);
        deleteCommunityData(user);
        deleteBeanData(user);
        userRepository.delete(user);
    }

    private void deleteMentorData(User user) {
        if (user.getRoleType() == RoleType.MENTOR) {
            mentorRepository.findByUser(user).ifPresent(mentor -> {
                mentorHashtagRepository.deleteByMentor(mentor);
                mentorRepository.delete(mentor);
            });
        }
    }

    private void deleteMenteeData(User user) {
        if (user.getRoleType() == RoleType.MENTEE) {
            menteeRepository.findByUser(user).ifPresent(mentee -> {
                menteeHashtagRepository.deleteByMentee(mentee);
                menteeRepository.delete(mentee);
            });
        }
    }

    private void deleteReferralData(User user) {
        referralRepository.findBySponsor(user).ifPresent(referralRepository::delete);
    }

    private void deleteUserRelatedData(User user) {
        userIdentityInfoRepository.deleteByUser(user);
        termAgreementRepository.deleteByUser(user);
        rouletteSpinLogRepository.deleteByUser(user);
        paymentRepository.deleteByUser(user);
        mypageStatsRepository.deleteByUser(user);
        exchangeRequestRepository.deleteByUser(user);
    }

    private void deleteCommunityData(User user) {
        List<Community> communities = communityRepository.findAllByUser(user);
        for (Community community : communities) {
            communityCommentRepository.deleteByCommunity(community);
            communityImageRepository.deleteByCommunityId(community.getId());
            communityRepository.delete(community);
        }
    }

    private void deleteBeanData(User user) {
        beanTransactionRepository.deleteByUser(user);
        beanWalletRepository.deleteByUser(user);
    }

    private boolean isMentorProfileComplete(User user) {
        return mentorRepository.findByUser(user)
                .map(mentor -> mentor.getMentoringBeanAmount() != null)
                .orElse(false);
    }

    private String getReferralRegister(User user) {
        if (referralRepository.findBySponsor(user).isPresent()) {
            return "REGISTERED";
        } else {
            return "NOT_REGISTERED";
        }
    }

    private String getStudentStatusString(User user) {
        if (user.getStudentStatus() != null) {
            return user.getStudentStatus().name();
        } else {
            return "NOT_STATUS";
        }
    }
}
