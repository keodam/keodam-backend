package com.keodam.keodam_backend.term.mapper;

import com.keodam.keodam_backend.term.domain.Term;
import com.keodam.keodam_backend.term.dto.TermCreateResponse;
import com.keodam.keodam_backend.term.dto.TermsAgreementsResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TermMapper {

    TermsAgreementsResponse toTermAgreementsResponse(Term term);

    TermCreateResponse toTermCreateResponse(Term term);
}
