package com.keodam.keodam_backend.term.service;

import com.keodam.keodam_backend.app.user.domain.User;
import com.keodam.keodam_backend.app.user.repository.UserRepository;
import com.keodam.keodam_backend.exception.GeneralException;
import com.keodam.keodam_backend.global.code.status.ErrorStatus;
import com.keodam.keodam_backend.term.dto.TermCreateRequest;
import com.keodam.keodam_backend.term.dto.TermCreateResponse;
import com.keodam.keodam_backend.term.dto.TermsAgreementsRequest;
import com.keodam.keodam_backend.term.domain.Term;
import com.keodam.keodam_backend.term.domain.TermAgreement;
import com.keodam.keodam_backend.term.domain.TermType;
import com.keodam.keodam_backend.term.dto.TermsAgreementsResponse;
import com.keodam.keodam_backend.term.mapper.TermMapper;
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
    private final TermMapper termMapper;

    @Transactional
    public boolean agree(String email, TermsAgreementsRequest request){

        User user = userRepository.findByEmail(email).orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        Term term;

        if(request.privacyPolicy()){

            term = termRepository.findByTypeAndVersion(TermType.PRIVACY_POLICY, request.privacyVersion())
                    .orElseThrow(() -> new GeneralException(ErrorStatus.PRIVACY_TERM_NOT_FOUND));

            processAgreement(user, term);
        }


        if(request.termsOfService()){

            term = termRepository.findByTypeAndVersion(TermType.TERMS_OF_SERVICE, request.serviceVersion())
                    .orElseThrow(() -> new GeneralException(ErrorStatus.SERVICE_TERM_NOT_FOUND));

            processAgreement(user, term);
        }

        if(request.marketingAgreement()){

            term = termRepository.findByTypeAndVersion(TermType.MARKETING_AGREEMENT, request.marketingVersion())
                    .orElseThrow(() -> new GeneralException(ErrorStatus.MARKETING_TERM_NOT_FOUND));

            processAgreement(user, term);
        }

        return true;
    }

    private void processAgreement(User user, Term term){

        if (termAgreementRepository.findByUserAndTerm(user, term).isPresent())
            return;

        termAgreementRepository.save(
                TermAgreement.builder()
                        .term(term)
                        .agreedAt(LocalDateTime.now())
                        .user(user)
                        .build());
    }

    public TermsAgreementsResponse getLatestTerm(String type){

        Term term = termRepository.findTopByTypeOrderByVersionDesc(TermType.from(type))
                .orElseThrow(() -> new GeneralException(ErrorStatus.TERM_NOT_FOUND));

        return termMapper.toTermAgreementsResponse(term);
    }

    @Transactional
    public TermCreateResponse addTerm(TermCreateRequest request){

        TermType termType = TermType.from(request.termType());
        Integer nextVersion = termRepository.findMaxVersionByType(termType) + 1;

        Term term = Term.builder()
                .type(termType)
                .content(request.content())
                .version(nextVersion)
                .createdAt(LocalDateTime.now())
                .build();

        termRepository.save(term);

        return termMapper.toTermCreateResponse(term);
    }
}
