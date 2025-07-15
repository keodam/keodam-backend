package com.keodam.keodam_backend.app.user.coffeeChatProfile.domain;

import com.keodam.keodam_backend.community.domain.HashTag;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "mentee_hashtag")
public class MenteeHashtag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "mentee_id")
    private Mentee mentee;

    @ManyToOne
    @JoinColumn(name = "hashtag_id")
    private HashTag hashtag;

    @Builder
    public MenteeHashtag(Mentee mentee, HashTag hashtag) {
        this.mentee = mentee;
        this.hashtag = hashtag;
    }
}
