package com.keodam.keodam_backend.mypage.certification.domain;

public enum EmailTitle {
    EMAIL_VERIFICATION("Keodam", "인증메일"),
    INQUIRY_RECEIVED("keodam", "문의접수");

    private final String prefix;
    private final String title;

    EmailTitle(String prefix, String title) {
        this.prefix = prefix;
        this.title = title;
    }

    public String getFullTitle() {
        return "[" + prefix + "] " + title;
    }
}
