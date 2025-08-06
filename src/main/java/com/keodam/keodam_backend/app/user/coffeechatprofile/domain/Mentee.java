package com.keodam.keodam_backend.app.user.coffeechatprofile.domain;

import com.keodam.keodam_backend.app.user.domain.User;
import com.keodam.keodam_backend.app.user.coffeechatprofile.dto.request.MenteeRequestDto;
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
@Table(name = "mentee")
public class Mentee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "grade_major")
    private String gradeMajor;

    @Column(name = "desired_career")
    private String desiredCareer;

    @Column(name = "desired_mentoring")
    private String desiredMentoring;

    @Column(name = "self_introduction")
    private String selfIntroduction;

    @Column(name = "preferred_days", length = 255)
    private String preferredDays;

    @Column(name = "preferred_locations", length = 2048)
    private String preferredLocations;

    @Builder
    public Mentee(User user, String gradeMajor, String desiredCareer, String desiredMentoring, String selfIntroduction, List<String> preferredDays, List<String> preferredLocations) {
        this.user = user;
        this.gradeMajor = gradeMajor;
        this.desiredCareer = desiredCareer;
        this.desiredMentoring = desiredMentoring;
        this.selfIntroduction = selfIntroduction;
        updatePreferredDays(preferredDays);
        updatePreferredLocations(preferredLocations);
    }

    public void updateFromDto(User user, MenteeRequestDto menteeRequestDto) {
        this.user = user;
        this.gradeMajor = menteeRequestDto.getGradeMajor();
        this.desiredCareer = menteeRequestDto.getDesiredCareer();
        this.desiredMentoring = menteeRequestDto.getDesiredMentoring();
        this.selfIntroduction = menteeRequestDto.getSelfIntroduction();
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
