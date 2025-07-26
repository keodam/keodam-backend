package com.keodam.keodam_backend.term.dto;

import com.keodam.keodam_backend.term.domain.TermType;

public record TermsAgreementsResponse(TermType type,
                                      Integer version,
                                      String content){ }
