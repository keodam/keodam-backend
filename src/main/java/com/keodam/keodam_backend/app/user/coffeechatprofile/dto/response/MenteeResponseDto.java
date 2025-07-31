package com.keodam.keodam_backend.app.user.coffeechatprofile.dto.response;

import com.keodam.keodam_backend.app.user.coffeechatprofile.domain.Mentee;
import com.keodam.keodam_backend.app.user.coffeechatprofile.domain.MenteeHashtag;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class MenteeResponseDto {
    private String gradeMajor;
    private String desiredCareer;
    private String desiredMentoring;
    private String selfIntroduction;
    private List<String> hashtags;

    public static MenteeResponseDto from(Mentee mentee, List<MenteeHashtag> menteeHashtag) {
        return MenteeResponseDto.builder()
                .gradeMajor(mentee.getGradeMajor())
                .desiredCareer(mentee.getDesiredCareer())
                .desiredMentoring(mentee.getDesiredMentoring())
                .selfIntroduction(mentee.getSelfIntroduction())
                .hashtags(menteeHashtag.stream()
                        .map(tag -> tag.getHashtag().getName())
                        .collect(Collectors.toList()))
                .build();
    }
}
