package com.keodam.keodam_backend.mypage.service;

import com.keodam.keodam_backend.mypage.dto.response.EmailResponseDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class MailSendService {

    private static final int AUTH_CODE_LENGTH = 6;
    private static final Random random = new Random();
    private final JavaMailSender mailSender;
    private int authNumber;

    @Value("${spring.mail.username}")
    private String mailUsername;

    public void generateAuthCode() {
        String randomNumber = "";
        for (int i = 0; i < AUTH_CODE_LENGTH; i++) {
            randomNumber += Integer.toString(random.nextInt(10));
        }
        authNumber = Integer.parseInt(randomNumber);
    }

    public EmailResponseDto checkEmail(String email) {
        generateAuthCode();
        String setFrom = mailUsername;
        String toMail = email;
        String title = "[keodam] 인증메일입니다.";
        String content =
                "<br><br>" +
                        "인증 번호는 " + authNumber + "입니다." +
                        "<br>" +
                        "인증번호를 정확히 입력해주세요";
        mailSend(setFrom, toMail, title, content);
        String code = Integer.toString(authNumber);

        return EmailResponseDto.builder()
                .code(code)
                .build();

    }

    public void mailSend(String setFrom, String toMail, String title, String content) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            helper.setFrom(setFrom);
            helper.setTo(toMail);
            helper.setSubject(title);
            helper.setText(content, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
