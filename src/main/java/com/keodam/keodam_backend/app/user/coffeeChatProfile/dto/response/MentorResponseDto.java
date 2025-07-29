package com.keodam.keodam_backend.app.user.coffeeChatProfile.dto.response;

import com.keodam.keodam_backend.app.user.coffeeChatProfile.domain.Mentor;
import com.keodam.keodam_backend.app.user.coffeeChatProfile.domain.MentorHashtag;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class MentorResponseDto {
    private String major;
    private String company;
    private String jobDescription;
    private List<HashtagInfo> helpHashtags;
    private List<HashtagInfo> selfHashtags;
    private List<HashtagInfo> evaluatedHashtags;
    private String mentoringTopics;
    private String selfIntroduction;
    private Integer mentoringBeanAmount;
    private Integer expPoint;

    public static MentorResponseDto from(Mentor mentor,
                                         List<MentorHashtag> helpHashtag,
                                         List<MentorHashtag> selfHashtag,
                                         List<MentorHashtag> evaluatedHashtag) {
        return MentorResponseDto.builder()
                .major(mentor.getMajor())
                .company(mentor.getCompany())
                .jobDescription(mentor.getJobDescription())
                .mentoringTopics(mentor.getMentoringTopics())
                .selfIntroduction(mentor.getSelfIntroduction())
                .mentoringBeanAmount(mentor.getMentoringBeanAmount())
                .expPoint(mentor.getExpPoint())
                .helpHashtags(toHashtagInfoList(helpHashtag))
                .selfHashtags(toHashtagInfoList(selfHashtag))
                .evaluatedHashtags(toHashtagInfoList(evaluatedHashtag))
                .build();
    }

    private static List<HashtagInfo> toHashtagInfoList(List<MentorHashtag> mentorHashtags) {
        if (mentorHashtags == null) return List.of();
        return mentorHashtags.stream()
                .map(tag -> HashtagInfo.builder()
                        .name(tag.getHashtag().getName())
                        .isMain(tag.isMain())
                        .build())
                .collect(Collectors.toList());
    }
}
