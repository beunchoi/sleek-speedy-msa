package com.hanghae.userservice.domain.user.controller;

import com.hanghae.userservice.domain.user.dto.mail.MailAuthDto;
import com.hanghae.userservice.domain.user.dto.mail.MailRequestDto;
import com.hanghae.userservice.domain.user.dto.mail.MailResponseDto;
import com.hanghae.userservice.domain.user.service.MailService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
  public ResponseEntity<MailResponseDto> mailSend(@RequestBody @Valid MailRequestDto mailRequestDto)
      throws MessagingException {
    MailResponseDto code = mailService.sendSimpleMessage(mailRequestDto.getEmail());

    return ResponseEntity.status(HttpStatus.OK).body(code);
  }

  /* Email Auth: 인증번호 입력 후 인증 버튼 click */
  @PostMapping("/email/auth")
  public ResponseEntity<?> authCheck(@RequestBody @Valid MailAuthDto mailAuthDto) {
    Boolean checked = mailService.checkAuthNum(mailAuthDto.getMail(), mailAuthDto.getAuthNum());
    if (checked) {
      return ResponseEntity.status(HttpStatus.OK).body("이메일 인증 성공!");
    }
    else {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이메일 인증 실패!");
    }
  }
}
