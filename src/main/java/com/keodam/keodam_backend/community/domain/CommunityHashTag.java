package com.keodam.keodam_backend.community.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "community_hashtag")
public class CommunityHashTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "community_id")
    private Community community;

    @ManyToOne
    @JoinColumn(name = "hashtag_id")
    private HashTag hashtag;

    private boolean isMain;

    @Builder
    public CommunityHashTag(Community community, HashTag hashtag, boolean isMain) {
        this.community = community;
        this.hashtag = hashtag;
        this.isMain = isMain;
    }
}

