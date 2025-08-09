package com.keodam.keodam_backend.app.user.coffeechatprofile.domain;

import com.keodam.keodam_backend.app.user.domain.User;
import com.keodam.keodam_backend.app.user.coffeechatprofile.dto.request.MentorRequestDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "mentor")
public class Mentor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String major;

    private String company;

    @Column(name = "job_description")
    private String jobDescription;

    @Column(name = "mentoring_topics")
    private String mentoringTopics;

    @Column(name = "self_introduction")
    private String selfIntroduction;

    @Column(name = "mentoring_bean_amount")
    private Integer mentoringBeanAmount;

    @Column(name = "preferred_days", length = 255)
    private String preferredDays;

    @Column(name = "preferred_locations", length = 2048)
    private String preferredLocations;

    @Builder
    public Mentor(User user, String major, String company, String jobDescription, String mentoringTopics, String selfIntroduction, Integer mentoringBeanAmount, List<String> preferredDays, List<String> preferredLocations) {
        this.user = user;
        this.major = major;
        this.company = company;
        this.jobDescription = jobDescription;
        this.mentoringTopics = mentoringTopics;
        this.selfIntroduction = selfIntroduction;
        this.mentoringBeanAmount = mentoringBeanAmount;
        updatePreferredDays(preferredDays);
        updatePreferredLocations(preferredLocations);
    }

    public void updateFromDto(User user, MentorRequestDto mentorRequestDto) {
        this.user = user;
        this.major = mentorRequestDto.getMajor();
        this.company = mentorRequestDto.getCompany();
        this.jobDescription = mentorRequestDto.getJobDescription();
        this.mentoringTopics = mentorRequestDto.getMentoringTopics();
        this.selfIntroduction = mentorRequestDto.getSelfIntroduction();
        this.mentoringBeanAmount = mentorRequestDto.getMentoringBeanAmount();
    }

    public void updatePreferredDays(List<String> preferredDays) {
        this.preferredDays = preferredDays != null ? String.join(",", preferredDays) : null;
    }

    public List<String> getPreferredDaysAsList() {
        if (this.preferredDays == null || this.preferredDays.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.stream(this.preferredDays.split(",")).collect(Collectors.toList());
    }

    public void updatePreferredLocations(List<String> preferredLocations) {
        this.preferredLocations = preferredLocations != null ? String.join(",", preferredLocations) : null;
    }

    public List<String> getPreferredLocationsAsList() {
        if (this.preferredLocations == null || this.preferredLocations.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.stream(this.preferredLocations.split(",")).collect(Collectors.toList());
    }
}
