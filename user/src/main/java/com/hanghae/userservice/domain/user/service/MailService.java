package com.hanghae.userservice.domain.user.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

  private final JavaMailSender javaMailSender;
  private final RedisTemplate<String, String> redisTemplate;

  @Value("${mail.username}")
  private String senderEmail;

  public String sendSimpleMessage(String sendEmail) throws MessagingException {
    String number = createNumber();

    MimeMessage message = createMail(sendEmail, number);
    try {
      javaMailSender.send(message);
    } catch (MailException e) {
      throw new IllegalArgumentException("메일 발송 중 오류가 발생했습니다.");
    }

    return number;
  }

  public Boolean checkAuthNum(String email, String authNum) {
    String code = redisTemplate.opsForValue().get(email);
    return Objects.equals(code, authNum);
  }

  public String createNumber() {
    Random random = new Random();
    StringBuilder key = new StringBuilder();

    for (int i = 0; i < 8; i++) {
      int index = random.nextInt(3);

      switch (index) {
        case 0 -> key.append((char) (random.nextInt(26) + 97));
        case 1 -> key.append((char) (random.nextInt(26) + 65));
        case 2 -> key.append(random.nextInt(10));
      }
    }

    return key.toString();
  }

  public MimeMessage createMail(String mail, String number) throws MessagingException {
    MimeMessage message = javaMailSender.createMimeMessage();

    message.setFrom(senderEmail);
    message.setRecipients(MimeMessage.RecipientType.TO, mail);
    message.setSubject("이메일 인증");
    String body = "";
    body += "<h3>요청하신 인증 번호입니다.</h3>";
    body += "<h1>" + number + "</h1>";
    body += "<h3>감사합니다.</h3>";
    message.setText(body, "UTF-8", "html");

    // redis에 3분 동안 이메일과 인증 코드 저장
    redisTemplate.opsForValue().set(mail, number, 180, TimeUnit.SECONDS);

    return message;
  }

}
