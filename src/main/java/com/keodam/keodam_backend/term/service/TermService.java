package com.keodam.keodam_backend.term.service;

import com.keodam.keodam_backend.app.domain.User;
import com.keodam.keodam_backend.app.user.repository.UserRepository;
import com.keodam.keodam_backend.exception.GeneralException;
import com.keodam.keodam_backend.global.code.status.ErrorStatus;
import com.keodam.keodam_backend.term.TermsAgreementsRequest;
import com.keodam.keodam_backend.term.domain.Term;
import com.keodam.keodam_backend.term.domain.TermAgreement;
import com.keodam.keodam_backend.term.domain.TermType;
import com.keodam.keodam_backend.term.repository.TermAgreementRepository;
import com.keodam.keodam_backend.term.repository.TermRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class TermService {

    private final TermRepository termRepository;
    private final TermAgreementRepository termAgreementRepository;
    private final UserRepository userRepository;

    @Transactional
    public boolean agree(String email, TermsAgreementsRequest request){

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        Term term;

        if(request.privacyPolicy()){

            term = termRepository.findByTermTypeAndVersion(TermType.PRIVACY_POLICY, request.privacyVersion())
                    .orElseThrow(() -> new GeneralException(ErrorStatus.TERM_NOT_FOUND));

            if (termAgreementRepository.findByUserAndTerm(user, term).isPresent())
                throw new GeneralException(ErrorStatus.ALREADY_AGREED_TERM);

            termAgreementRepository.save(TermAgreement.builder()
                    .term(term)
                    .agreedAt(LocalDateTime.now())
                    .user(user)
                    .build());
        }

        if(request.termsOfService()){

            term = termRepository.findByTermTypeAndVersion(TermType.TERMS_OF_SERVICE, request.privacyVersion())
                    .orElseThrow(() -> new GeneralException(ErrorStatus.TERM_NOT_FOUND));

            if (termAgreementRepository.findByUserAndTerm(user, term).isPresent())
                throw new GeneralException(ErrorStatus.ALREADY_AGREED_TERM);

            termAgreementRepository.save(TermAgreement.builder()
                    .term(term)
                    .agreedAt(LocalDateTime.now())
                    .user(user)
                    .build());
        }

        if(request.marketingAgreement()){

            term = termRepository.findByTermTypeAndVersion(TermType.MARKETING_AGREEMENT, request.privacyVersion())
                    .orElseThrow(() -> new GeneralException(ErrorStatus.TERM_NOT_FOUND));

            if (termAgreementRepository.findByUserAndTerm(user, term).isPresent())
                throw new GeneralException(ErrorStatus.ALREADY_AGREED_TERM);

            termAgreementRepository.save(TermAgreement.builder()
                    .term(term)
                    .agreedAt(LocalDateTime.now())
                    .user(user)
                    .build());
        }

        return true;
    }

    public String getLatestTerm(String type){

        Term term = termRepository.findTopByTermTypeOrderByVersionDesc(TermType.from(type))
                .orElseThrow(() -> new GeneralException(ErrorStatus.TERM_NOT_FOUND));

        return term.getContent();
    }
}
