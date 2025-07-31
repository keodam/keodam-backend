package com.keodam.keodam_backend.app.user.coffeechatprofile.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

@Getter
public class MenteeRequestDto {
    private String gradeMajor;
    private String desiredCareer;
    private List<String> hashtags;

    @NotNull
    private String desiredMentoring;

    @NotNull
    private String selfIntroduction;
}
