package com.keodam.keodam_backend.app.user.coffeeChatProfile.service;

import com.keodam.keodam_backend.app.user.domain.User;
import com.keodam.keodam_backend.app.user.coffeeChatProfile.domain.Mentee;
import com.keodam.keodam_backend.app.user.coffeeChatProfile.domain.MenteeHashtag;
import com.keodam.keodam_backend.app.user.coffeeChatProfile.dto.request.MenteeRequestDto;
import com.keodam.keodam_backend.app.user.coffeeChatProfile.dto.response.MenteeResponseDto;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

        Optional<Mentee> existingMentor = menteeRepository.findByUser(user);

        Mentee mentee;

        if (existingMentor.isPresent()) {
            mentee = existingMentor.get();
            mentee.updateFromDto(user, menteeRequestDto);
        } else {
            mentee = Mentee.builder()
                    .user(user)
                    .gradeMajor(menteeRequestDto.getGradeMajor())
                    .desiredCareer(menteeRequestDto.getDesiredCareer())
                    .desiredMentoring(menteeRequestDto.getDesiredMentoring())
                    .selfIntroduction(menteeRequestDto.getSelfIntroduction())
                    .build();
        }

        menteeRepository.save(mentee);

        List<MenteeHashtag> menteeHashtags = updateMenteeHashtags(mentee, menteeRequestDto.getHashtags());

        return MenteeResponseDto.from(mentee, menteeHashtags);
    }

    @Transactional(readOnly = true)
    public MenteeResponseDto getMentee(Long menteeId) {
        Mentee mentee = menteeRepository.findById(menteeId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MENTEE_NOT_FOUND));

        List<MenteeHashtag> menteeHashtags = menteeHashtagRepository.findAllByMentee(mentee);

        return MenteeResponseDto.from(mentee, menteeHashtags);
    }

    private List<MenteeHashtag> updateMenteeHashtags(Mentee mentee, List<String> hashtags) {
        menteeHashtagRepository.deleteByMentee(mentee);

        if (hashtags == null || hashtags.isEmpty()) return Collections.emptyList();

        List<MenteeHashtag> menteeHashtags = new ArrayList<>();
        for (String hashtag : hashtags) {
            HashTag hashTag = hashTagRepository.findByName(hashtag)
                    .orElseGet(() -> hashTagRepository.save(HashTag.builder().name(hashtag).build()));

            MenteeHashtag menteeHashtag = MenteeHashtag.builder()
                    .mentee(mentee)
                    .hashtag(hashTag)
                    .build();

            menteeHashtagRepository.save(menteeHashtag);
            menteeHashtags.add(menteeHashtag);
        }
        return menteeHashtags;
    }
}
