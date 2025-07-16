package com.keodam.keodam_backend.app.user.coffeeChatProfile.domain;

import com.keodam.keodam_backend.community.domain.HashTag;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "mentor_hashtag")
public class MentorHashtag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "mentor_id")
    private Mentor mentor;

    @ManyToOne
    @JoinColumn(name = "hashtag_id")
    private HashTag hashtag;

    @Enumerated(EnumType.STRING)
    private MentorHashtagType hashtagType;

    private boolean isMain;

    @Builder
    public MentorHashtag(Mentor mentor, HashTag hashtag, MentorHashtagType hashtagType, boolean isMain) {
        this.mentor = mentor;
        this.hashtag = hashtag;
        this.hashtagType = hashtagType;
        this.isMain = isMain;
    }
}
