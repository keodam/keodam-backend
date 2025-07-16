package com.keodam.keodam_backend.app.user.coffeeChatProfile.domain;

import com.keodam.keodam_backend.app.domain.User;
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

    @Builder
    public Mentor(User user, String major, String company, String jobDescription, String mentoringTopics, String selfIntroduction) {
        this.user = user;
        this.major = major;
        this.company = company;
        this.jobDescription = jobDescription;
        this.mentoringTopics = mentoringTopics;
        this.selfIntroduction = selfIntroduction;
    }
}
