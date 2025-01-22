package com.keodam.keodam_backend.mypage.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class UserDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String documentfilePath;

    @Enumerated(EnumType.STRING)
    private DocumentType documentType;

    private LocalDateTime creationDate;





}
