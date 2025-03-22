package com.keodam.keodam_backend.mypage.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class UserDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    private DocumentType documentType;

    private String documentfilePath;

    @Column(nullable = false)
    private LocalDateTime creationDate;
}
