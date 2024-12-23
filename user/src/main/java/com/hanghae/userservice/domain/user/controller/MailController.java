package com.hanghae.userservice.domain.user.controller;

import com.hanghae.userservice.domain.user.dto.mail.MailAuthDto;
import com.hanghae.userservice.domain.user.dto.mail.MailRequestDto;
import com.hanghae.userservice.domain.user.service.MailService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user-service")
public class MailController {

  private final MailService mailService;

  /* Send Email: 인증번호 전송 버튼 click */
  @PostMapping("/email/send")
  public Map<String, String> mailSend(@RequestBody @Valid MailRequestDto mailRequestDto)
      throws MessagingException {
    String code = mailService.sendSimpleMessage(mailRequestDto.getEmail());
    // response를 JSON 문자열으로 반환
    Map<String, String> response = new HashMap<>();
    response.put("code", code);

    return response;
  }

  /* Email Auth: 인증번호 입력 후 인증 버튼 click */
  @PostMapping("/email/auth")
  public String authCheck(@RequestBody @Valid MailAuthDto mailAuthDto) {
    Boolean checked = mailService.checkAuthNum(mailAuthDto.getMail(), mailAuthDto.getAuthNum());
    if (checked) {
      return "이메일 인증 성공!";
    }
    else {
      throw new IllegalArgumentException("이메일 인증 실패!");
    }
  }
}
