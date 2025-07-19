package com.keodam.keodam_backend.term.domain;

import lombok.Getter;

@Getter
public class Term {

    private Long id;
    private TermType type;
    private Integer version;
    private String content;
}
