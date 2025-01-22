package com.keodam.keodam_backend.mypage.service;

import com.keodam.keodam_backend.app.repository.UserRepository;
import com.keodam.keodam_backend.mypage.dto.response.EmailResponseDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class MailSendService {

    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final UserRepository userRepository;
    private int authNumber;

    @Value("${mail.username}")
    private String mailUsername;

    public void makeRandomNumber() {
        Random r = new Random();
        String randomNumber = "";
        for(int i = 0; i < 6; i++){
            randomNumber += Integer.toString(r.nextInt(10));
        }
        authNumber = Integer.parseInt(randomNumber);
    }

    public EmailResponseDto checkEmail(String email) {
        makeRandomNumber();
        String setFrom = mailUsername;
        String toMail = email;
        String title = "[keodam] 인증메일입니다." ;
        String content =
                "<br><br>" +
                "인증 번호는 " + authNumber + "입니다." +
                "<br>" +
                "인증번호를 정확히 입력해주세요"; //이메일 내용 삽입
        mailSend(setFrom, toMail, title, content);
        String code = Integer.toString(authNumber);

        return EmailResponseDto.builder()
                .code(code)
                .build();
    }

    //이메일 전송
    public void mailSend(String setFrom, String toMail, String title, String content) {
        MimeMessage message = mailSender.createMimeMessage();//JavaMailSender 객체를 사용하여 MimeMessage 객체를 생성
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message,true,"utf-8");
            helper.setFrom(setFrom);//이메일의 발신자 주소 설정
            helper.setTo(toMail);//이메일의 수신자 주소 설정
            helper.setSubject(title);//이메일의 제목을 설정
            helper.setText(content,true);//이메일의 내용 설정 두 번째 매개 변수에 true를 설정하여 html 설정으로한다.
            mailSender.send(message);
        } catch (MessagingException e) {//이메일 서버에 연결할 수 없거나, 잘못된 이메일 주소를 사용하거나, 인증 오류가 발생하는 등 오류
            e.printStackTrace();//e.printStackTrace()는 예외를 기본 오류 스트림에 출력하는 메서드
        }
    }
    
}
