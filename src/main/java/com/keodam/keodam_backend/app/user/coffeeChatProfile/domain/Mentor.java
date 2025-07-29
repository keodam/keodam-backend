package com.keodam.keodam_backend.app.user.coffeeChatProfile.domain;

import com.keodam.keodam_backend.app.user.domain.User;
import com.keodam.keodam_backend.app.user.coffeeChatProfile.dto.request.MentorRequestDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Column(name = "exp_point")
    private Integer expPoint;

    @Builder
    public Mentor(User user, String major, String company, String jobDescription, String mentoringTopics, String selfIntroduction, Integer mentoringBeanAmount) {
        this.user = user;
        this.major = major;
        this.company = company;
        this.jobDescription = jobDescription;
        this.mentoringTopics = mentoringTopics;
        this.selfIntroduction = selfIntroduction;
        this.mentoringBeanAmount = mentoringBeanAmount;
        this.expPoint = 0;
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

    public void addExpPoint(int amount) {
        if (this.expPoint == null) {
            this.expPoint = 0;
        }
        this.expPoint += amount;
    }
}
