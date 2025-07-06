package com.keodam.keodam_backend.community.domain;

import com.keodam.keodam_backend.app.domain.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "community")
public class Community {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommunityHashTag> communityHashTags = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @CreatedDate
    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @Builder
    public Community(String content, User user) {
        this.content = content;
        this.creationDate = LocalDateTime.now();
        this.user = user;
    }

    public void addCommunityHashTag(CommunityHashTag communityHashTag) {
        this.communityHashTags.add(communityHashTag);
    }

    public void updateContent(String content) {
        this.content = content;
    }
}
