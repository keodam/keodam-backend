package com.keodam.keodam_backend.app.user.coffeeChatProfile.domain;

import com.keodam.keodam_backend.app.domain.User;
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

    @Builder
    public Mentee(User user, String gradeMajor, String desiredCareer, String desiredMentoring) {
        this.user = user;
        this.gradeMajor = gradeMajor;
        this.desiredCareer = desiredCareer;
        this.desiredMentoring = desiredMentoring;
    }
}
