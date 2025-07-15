package com.keodam.keodam_backend.app.user.coffeeChatProfile.dto;

import lombok.Getter;
import java.util.List;

@Getter
public class MenteeRequestDto {
    private String gradeMajor;
    private String desiredCareer;
    private String desiredMentoring;
    private List<String> hashtags;
}
