package com.keodam.keodam_backend.app.user.coffeeChatProfile.service;

import com.keodam.keodam_backend.app.domain.User;
import com.keodam.keodam_backend.app.user.coffeeChatProfile.domain.Mentor;
import com.keodam.keodam_backend.app.user.coffeeChatProfile.domain.MentorHashtag;
import com.keodam.keodam_backend.app.user.coffeeChatProfile.domain.MentorHashtagType;
import com.keodam.keodam_backend.app.user.coffeeChatProfile.dto.request.MentorRequestDto;
import com.keodam.keodam_backend.app.user.coffeeChatProfile.dto.response.MentorResponseDto;
import com.keodam.keodam_backend.app.user.coffeeChatProfile.repository.MentorHashtagRepository;
import com.keodam.keodam_backend.app.user.coffeeChatProfile.repository.MentorRepository;
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
public class MentorProfileService {

    private final MentorRepository mentorRepository;
    private final UserRepository userRepository;
    private final HashTagRepository hashTagRepository;
    private final MentorHashtagRepository mentorHashtagRepository;

    @Transactional
    public MentorResponseDto updateMentor(String email, MentorRequestDto mentorRequestDto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        Mentor mentor = Mentor.builder()
                .user(user)
                .major(mentorRequestDto.getMajor())
                .mentoringTopics(mentorRequestDto.getMentoringTopics())
                .company(mentorRequestDto.getCompany())
                .selfIntroduction(mentorRequestDto.getSelfIntroduction())
                .jobDescription(mentorRequestDto.getJobDescription())
                .build();

        mentorRepository.save(mentor);

        List<MentorHashtag> helpMentorHashtag = saveMentorHashtags(mentor, mentorRequestDto.getHelpHashtags(), MentorHashtagType.HELP);
        List<MentorHashtag> selfMentorHashtag = saveMentorHashtags(mentor, mentorRequestDto.getSelfHashtags(), MentorHashtagType.SELF);
        List<MentorHashtag> evaluetedMentorHashtag = saveMentorHashtags(mentor, mentorRequestDto.getEvaluatedHashtags(), MentorHashtagType.EVALUATED);

        return MentorResponseDto.from(mentor, helpMentorHashtag, selfMentorHashtag, evaluetedMentorHashtag);
    }

    private List<MentorHashtag> saveMentorHashtags(Mentor mentor, List<String> hashtags, MentorHashtagType mentorHashtagType) {
        List<MentorHashtag> mentorHashtags = new ArrayList<>();
        if (hashtags == null || hashtags.isEmpty()) return null;

        for (int i = 0; i < hashtags.size(); i++) {
            String hashtagName = hashtags.get(i);
            HashTag hashTag = hashTagRepository.findByName(hashtagName)
                    .orElseGet(() -> hashTagRepository.save(HashTag.builder().name(hashtagName).build()));

            MentorHashtag mentorHashtag = MentorHashtag.builder()
                    .mentor(mentor)
                    .hashtag(hashTag)
                    .hashtagType(mentorHashtagType)
                    .isMain(i == 0)
                    .build();

            mentorHashtagRepository.save(mentorHashtag);
            mentorHashtags.add(mentorHashtag);
        }
        return mentorHashtags;
    }
}
