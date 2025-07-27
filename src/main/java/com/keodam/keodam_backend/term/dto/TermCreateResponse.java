package com.keodam.keodam_backend.term.dto;

import com.keodam.keodam_backend.term.domain.TermType;
import java.time.LocalDateTime;

public record TermCreateResponse(TermType type,
                                 Integer version,
                                 String content,
                                 LocalDateTime createdAt) { }
