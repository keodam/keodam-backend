package com.keodam.keodam_backend.community.domain;

import com.keodam.keodam_backend.app.user.domain.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "Community_comment")
public class CommunityComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id")
    private Community community;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // 부모 댓글 (null이면 최상위 댓글)
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private CommunityComment parent;

    // 대댓글 목록
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<CommunityComment> children = new ArrayList<>();

    private LocalDateTime createdAt;

    @Builder
    public CommunityComment(String content, Community community, User user, CommunityComment parent) {
        this.content = content;
        this.community = community;
        this.parent = parent;
        this.user = user;
    }

    public void updateComment(String content) {
        this.content = content;
    }
}
