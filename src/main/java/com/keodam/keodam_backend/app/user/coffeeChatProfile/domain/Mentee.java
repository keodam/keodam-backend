package com.keodam.keodam_backend.app.user.coffeeChatProfile.domain;

import com.keodam.keodam_backend.app.domain.User;
import com.keodam.keodam_backend.app.user.coffeeChatProfile.dto.request.MenteeRequestDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Builder
    public Mentee(User user, String gradeMajor, String desiredCareer, String desiredMentoring, String selfIntroduction) {
        this.user = user;
        this.gradeMajor = gradeMajor;
        this.desiredCareer = desiredCareer;
        this.desiredMentoring = desiredMentoring;
        this.selfIntroduction = selfIntroduction;
    }

    public void updateFromDto(User user, MenteeRequestDto menteeRequestDto) {
        this.user = user;
        this.gradeMajor = menteeRequestDto.getGradeMajor();
        this.desiredCareer = menteeRequestDto.getDesiredCareer();
        this.desiredMentoring = menteeRequestDto.getDesiredMentoring();
        this.selfIntroduction = menteeRequestDto.getSelfIntroduction();
    }
}
