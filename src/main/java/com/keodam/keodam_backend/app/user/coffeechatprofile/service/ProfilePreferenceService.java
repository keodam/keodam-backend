package com.keodam.keodam_backend.app.user.coffeechatprofile.service;

import com.keodam.keodam_backend.app.user.domain.User;
import com.keodam.keodam_backend.app.user.repository.UserRepository;
import com.keodam.keodam_backend.exception.GeneralException;
import com.keodam.keodam_backend.global.code.status.ErrorStatus;
import com.keodam.keodam_backend.app.user.coffeechatprofile.dto.request.PreferenceRequestDto;
import com.keodam.keodam_backend.app.user.coffeechatprofile.domain.Mentor;
import com.keodam.keodam_backend.app.user.coffeechatprofile.domain.Mentee;
import com.keodam.keodam_backend.app.user.coffeechatprofile.repository.MentorRepository;
import com.keodam.keodam_backend.app.user.coffeechatprofile.repository.MenteeRepository;
import com.keodam.keodam_backend.mypage.address.service.AddressService;
import java.util.HashSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProfilePreferenceService {

    private final UserRepository userRepository;
    private final MentorRepository mentorRepository;
    private final MenteeRepository menteeRepository;
    private final AddressService addressService;

    private static final Set<String> VALID_DAYS = new HashSet<>(Arrays.asList(
            "월요일", "화요일", "수요일", "목요일", "금요일", "토요일", "일요일"
    ));

    @Transactional
    public void updatePreferences(String email, PreferenceRequestDto request) {
        User user = findUserByEmail(email);

        validatePreferenceRequest(request);

        if ("MENTOR".equalsIgnoreCase(request.getUserType())) {
            updateMentorProfile(user, request);
        } else if ("MENTEE".equalsIgnoreCase(request.getUserType())) {
            updateMenteeProfile(user, request);
        } else {
            throw new GeneralException(ErrorStatus.INVALID_USER_TYPE);
        }
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
    }

    private void validatePreferenceRequest(PreferenceRequestDto request) {
        for (String day : request.getPreferredDays()) {
            if (!VALID_DAYS.contains(day)) {
                throw new GeneralException(ErrorStatus.INVALID_PREFERRED_DAY);
            }
        }

        for (String location : request.getPreferredLocations()) {
            String[] parts = location.split(" ");
            if (parts.length < 2 || parts.length > 4) {
                throw new GeneralException(ErrorStatus.INVALID_LOCATION_FORMAT);
            }

            String province = parts[0];
            String district = parts[1];
            String neighborhood = parts.length > 2 ? parts[2] : null;
            String ri = parts.length > 3 ? parts[3] : null;

            List<String> validDistricts = addressService.getDistrictsByProvince(province);
            if (!validDistricts.contains(district)) {
                throw new GeneralException(ErrorStatus.INVALID_LOCATION_DETAIL);
            }

            if (neighborhood != null) {
                List<String> validNeighborhoods = addressService.getNeighborhoodsByDistrict(province, district);
                String fullNeighborhood = neighborhood;
                if (ri != null) {
                    fullNeighborhood += " " + ri;
                }
                if (!validNeighborhoods.contains(fullNeighborhood)) {
                    throw new GeneralException(ErrorStatus.INVALID_LOCATION_DETAIL);
                }
            }
        }
    }

    private void updateMentorProfile(User user, PreferenceRequestDto request) {
        Mentor mentorProfile = mentorRepository.findByUser(user)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MENTOR_NOT_FOUND));

        mentorProfile.updatePreferredDays(request.getPreferredDays());
        mentorProfile.updatePreferredLocations(request.getPreferredLocations());
        mentorRepository.save(mentorProfile);
    }

    private void updateMenteeProfile(User user, PreferenceRequestDto request) {
        Mentee menteeProfile = menteeRepository.findByUser(user)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MENTEE_NOT_FOUND));

        menteeProfile.updatePreferredDays(request.getPreferredDays());
        menteeProfile.updatePreferredLocations(request.getPreferredLocations());
        menteeRepository.save(menteeProfile);
    }
}
