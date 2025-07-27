package com.keodam.keodam_backend.term.domain;

import java.io.Serializable;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TermAgreementId implements Serializable {

    private Long user;
    private Long term;
}