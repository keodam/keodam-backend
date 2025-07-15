package com.keodam.keodam_backend.app.user.coffeeChatProfile.service;

import com.keodam.keodam_backend.app.domain.User;
import com.keodam.keodam_backend.app.user.coffeeChatProfile.domain.Mentee;
import com.keodam.keodam_backend.app.user.coffeeChatProfile.domain.MenteeHashtag;
import com.keodam.keodam_backend.app.user.coffeeChatProfile.dto.MenteeRequestDto;
import com.keodam.keodam_backend.app.user.coffeeChatProfile.dto.MenteeResponseDto;
import com.keodam.keodam_backend.app.user.coffeeChatProfile.repository.MenteeHashtagRepository;
import com.keodam.keodam_backend.app.user.coffeeChatProfile.repository.MenteeRepository;
import com.keodam.keodam_backend.app.user.repository.UserRepository;
import com.keodam.keodam_backend.community.domain.HashTag;
import com.keodam.keodam_backend.community.repository.HashTagRepository;
import com.keodam.keodam_backend.exception.GeneralException;
import com.keodam.keodam_backend.global.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MenteeProfileService {

    private final MenteeRepository menteeRepository;
    private final UserRepository userRepository;
    private final HashTagRepository hashTagRepository;
    private final MenteeHashtagRepository menteeHashtagRepository;

    @Transactional
    public MenteeResponseDto updateMentee(String email, MenteeRequestDto menteeRequestDto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        Mentee menteeProfile = Mentee.builder()
                .user(user)
                .gradeMajor(menteeRequestDto.getGradeMajor())
                .desiredCareer(menteeRequestDto.getDesiredCareer())
                .desiredMentoring(menteeRequestDto.getDesiredMentoring())
                .build();

        menteeRepository.save(menteeProfile);

        List<MenteeHashtag> menteeHashtags = new ArrayList<>();
        if (menteeRequestDto.getHashtags() != null && !menteeRequestDto.getHashtags().isEmpty()) {
            menteeRequestDto.getHashtags().forEach(hashtagName -> {
                HashTag hashTag = hashTagRepository.findByName(hashtagName)
                        .orElseGet(() -> hashTagRepository.save(
                                HashTag.builder()
                                        .name(hashtagName)
                                        .build()));

                MenteeHashtag menteeHashtag = MenteeHashtag
                        .builder()
                        .hashtag(hashTag)
                        .mentee(menteeProfile)
                        .build();

                menteeHashtagRepository.save(menteeHashtag);
                menteeHashtags.add(menteeHashtag);
            });
        }
        return MenteeResponseDto.from(menteeProfile, menteeHashtags);
    }
}
