package com.keodam.keodam_backend.mypage.domain;

import com.keodam.keodam_backend.app.domain.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "User_verification")
@EntityListeners(AuditingEntityListener.class)
public class UserVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type")
    private DocumentType documentType;

    @Column(name = "document_file_path")
    private String documentFilePath;

    @CreatedDate
    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @Column(name = "verification_email")
    private String verificationEmail;

    @Column(name = "verification_code")
    private String verificationCode;

    @Enumerated(EnumType.STRING)
    private VerificationStatus status;

    @Builder
    public UserVerification(User user, DocumentType documentType,
                            String documentFilePath, String verificationEmail,
                            String verificationCode) {
        this.user = user;
        this.documentType = documentType;
        this.documentFilePath = documentFilePath;
        this.verificationEmail = verificationEmail;
        this.status = VerificationStatus.PENDING;
        this.verificationCode = verificationCode;
    }
}
