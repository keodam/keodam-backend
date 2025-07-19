package com.keodam.keodam_backend.app.user.coffeeChatProfile.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

@Getter
public class MentorRequestDto {

    private String major;
    private String company;
    private String jobDescription;
    private List<String> helpHashtags;
    private List<String> selfHashtags;
    private List<String> evaluatedHashtags;
    private int mentoringBeanAmount;

    @NotNull
    private String mentoringTopics;

    @NotNull
    private String selfIntroduction;
}
